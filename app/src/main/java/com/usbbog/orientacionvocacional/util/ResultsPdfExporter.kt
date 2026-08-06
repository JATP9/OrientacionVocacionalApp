package com.usbbog.orientacionvocacional.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.usbbog.orientacionvocacional.ui.mobile.ResultsUiState
import java.io.File
import java.io.FileOutputStream

object ResultsPdfExporter {

    fun createAndShare(
        context: Context,
        userName: String,
        result: ResultsUiState,
    ): File {
        val file = createPdf(context, userName, result)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(shareIntent, "Compartir informe vocacional")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
        return file
    }

    private fun createPdf(
        context: Context,
        userName: String,
        result: ResultsUiState,
    ): File {
        val reportsDirectory = File(context.cacheDir, "reports").apply { mkdirs() }
        val file = File(reportsDirectory, "usb-vocacional-resultado.pdf")
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 48f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        var pageNumber = 0
        var page = document.startPage(
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, ++pageNumber).create(),
        )
        var canvas = page.canvas
        var y = 62f

        fun finishPage() {
            document.finishPage(page)
        }

        fun newPage() {
            finishPage()
            page = document.startPage(
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, ++pageNumber).create(),
            )
            canvas = page.canvas
            y = 62f
        }

        fun ensureSpace(height: Float) {
            if (y + height > pageHeight - margin) newPage()
        }

        fun line(
            text: String,
            size: Float = 12f,
            color: Int = Color.rgb(45, 45, 48),
            bold: Boolean = false,
            gapAfter: Float = 8f,
        ) {
            ensureSpace(size + gapAfter + 8f)
            paint.textSize = size
            paint.color = color
            paint.typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            canvas.drawText(text, margin, y, paint)
            y += size + gapAfter
        }

        fun wrapped(
            text: String,
            size: Float = 12f,
            color: Int = Color.rgb(70, 70, 74),
            bold: Boolean = false,
        ) {
            paint.textSize = size
            paint.color = color
            paint.typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            val maxWidth = pageWidth - margin * 2
            val words = text.split(" ")
            var currentLine = ""
            words.forEach { word ->
                val candidate = if (currentLine.isBlank()) word else "$currentLine $word"
                if (paint.measureText(candidate) > maxWidth && currentLine.isNotBlank()) {
                    line(currentLine, size, color, bold, 6f)
                    currentLine = word
                } else {
                    currentLine = candidate
                }
            }
            if (currentLine.isNotBlank()) line(currentLine, size, color, bold, 10f)
        }

        val orange = Color.rgb(239, 125, 0)
        val blue = Color.rgb(24, 30, 123)

        line("UNIVERSIDAD DE SAN BUENAVENTURA", 12f, blue, true, 12f)
        line("Informe de orientación vocacional", 25f, orange, true, 18f)
        wrapped("Usuario: ${userName.ifBlank { "Usuario" }}", 13f, Color.DKGRAY, true)
        line("Generado: ${result.generatedAt}", 11f, Color.GRAY, false, 24f)

        line("Área principal", 12f, blue, true, 6f)
        wrapped(result.mainArea.ifBlank { "Sin resultado" }, 22f, orange, true)
        wrapped(result.summary, 13f)
        y += 8f

        line("Afinidad por área", 17f, blue, true, 12f)
        result.scores.forEach { score ->
            line("${score.label}: ${score.percentage}%", 12f, Color.DKGRAY, true, 7f)
        }
        y += 10f

        line("Programas recomendados", 17f, blue, true, 12f)
        result.careers.forEach { career ->
            ensureSpace(88f)
            line("${career.rank}. ${career.name} · ${career.score}%", 14f, orange, true, 5f)
            line(career.area, 11f, blue, true, 5f)
            wrapped(career.description, 11f)
            y += 5f
        }

        ensureSpace(70f)
        y += 12f
        wrapped(
            "Este informe ofrece recomendaciones iniciales de orientación. " +
                "La decisión académica debe complementarse con información sobre los programas, " +
                "acompañamiento profesional y tus circunstancias personales.",
            10f,
            Color.GRAY,
        )

        finishPage()
        FileOutputStream(file).use { output -> document.writeTo(output) }
        document.close()
        return file
    }
}
