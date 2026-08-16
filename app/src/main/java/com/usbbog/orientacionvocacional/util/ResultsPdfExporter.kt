package com.usbbog.orientacionvocacional.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.usbbog.orientacionvocacional.ui.mobile.ResultsUiState
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Replica en Android el informe creado por `generateResultPdf` en Web.
 *
 * Las medidas usan puntos sobre una página A4, igual que jsPDF, y conservan
 * los mismos textos, colores, márgenes, tamaños, tablas y pie de página.
 */
object ResultsPdfExporter {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 48f
    private const val HEADER_HEIGHT = 96f
    private const val TABLE_TOP_MARGIN = 40f
    private const val TABLE_BOTTOM = PAGE_HEIGHT - 40f
    private const val CELL_PADDING = 6f

    private val BrandColor = Color.rgb(239, 125, 0)
    private val DarkColor = Color.rgb(50, 50, 50)
    private val GrayColor = Color.rgb(109, 109, 109)
    private val FooterLineColor = Color.rgb(233, 228, 219)
    private val TableLineColor = Color.rgb(200, 200, 200)

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
        val file = File(reportsDirectory, reportFileName(result.reportName))

        val measurementDocument = PdfDocument()
        val totalPages = ReportRenderer(
            document = measurementDocument,
            totalPages = null,
            userName = userName,
            result = result,
        ).render()
        measurementDocument.close()

        val document = PdfDocument()
        ReportRenderer(
            document = document,
            totalPages = totalPages,
            userName = userName,
            result = result,
        ).render()

        FileOutputStream(file).use { output -> document.writeTo(output) }
        document.close()
        return file
    }

    private fun reportFileName(reportName: String): String {
        val baseName = reportName
            .ifBlank { "resultado-vocacional" }
            .filter { character ->
                character in 'a'..'z' ||
                    character in 'A'..'Z' ||
                    character in '0'..'9' ||
                    character == '_' ||
                    character == '-'
            }
            .ifBlank { "resultado-vocacional" }
        return "$baseName.pdf"
    }

    private class ReportRenderer(
        private val document: PdfDocument,
        private val totalPages: Int?,
        userName: String,
        private val result: ResultsUiState,
    ) {
        private val displayName = userName.ifBlank { "Estudiante USB" }
        private val regularTypeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        private val boldTypeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG)
        private var page: PdfDocument.Page? = null
        private lateinit var canvas: Canvas
        private var pageNumber = 0

        fun render(): Int {
            startPage()
            drawHeader()

            var cursorY = 128f
            drawText(
                text = "Información general",
                x = MARGIN,
                y = cursorY,
                size = 12f,
                color = DarkColor,
                bold = true,
            )

            cursorY += 18f
            drawText(
                text = "Estudiante: $displayName",
                x = MARGIN,
                y = cursorY,
                size = 10f,
                color = GrayColor,
            )

            cursorY += 15f
            drawText(
                text = "Fecha de generación: ${formatWebDate(result)}",
                x = MARGIN,
                y = cursorY,
                size = 10f,
                color = GrayColor,
            )

            cursorY += 15f
            drawText(
                text = "Nombre del informe: ${result.reportName.ifBlank { "Sin identificar" }}",
                x = MARGIN,
                y = cursorY,
                size = 10f,
                color = GrayColor,
            )

            cursorY += 32f
            drawText(
                text = "Área de mayor afinidad",
                x = MARGIN,
                y = cursorY,
                size = 12f,
                color = DarkColor,
                bold = true,
            )

            cursorY += 20f
            drawText(
                text = result.mainArea,
                x = MARGIN,
                y = cursorY,
                size = 13f,
                color = BrandColor,
                bold = true,
            )

            cursorY += 18f
            val profileLines = wrapText(
                text = result.pdfSummary.ifBlank {
                    "Tu perfil vocacional fue calculado con base en tus respuestas de la prueba."
                },
                maxWidth = PAGE_WIDTH - MARGIN * 2,
                size = 10f,
            )
            drawLines(
                lines = profileLines,
                x = MARGIN,
                firstBaseline = cursorY,
                lineHeight = 13f,
                size = 10f,
                color = GrayColor,
            )
            cursorY += profileLines.size * 13f + 22f

            drawText(
                text = "Afinidad por área",
                x = MARGIN,
                y = cursorY,
                size = 12f,
                color = DarkColor,
                bold = true,
            )

            cursorY = drawTable(
                startY = cursorY + 8f,
                headers = listOf("Área", "Afinidad"),
                rows = result.scores.map { score ->
                    listOf(score.label, "${score.percentage}%")
                },
                headerColor = BrandColor,
                fixedWidths = mapOf(1 to 80f),
                rightAlignedColumns = setOf(1),
            )

            cursorY += 28f
            drawText(
                text = "Programas recomendados",
                x = MARGIN,
                y = cursorY,
                size = 12f,
                color = DarkColor,
                bold = true,
            )

            cursorY = drawTable(
                startY = cursorY + 8f,
                headers = listOf("Programa", "Área", "Compatibilidad"),
                rows = result.careers.map { career ->
                    listOf(career.name, career.area, "${career.score}%")
                },
                headerColor = DarkColor,
                fixedWidths = mapOf(2 to 90f),
                rightAlignedColumns = setOf(2),
            )

            cursorY += 24f
            val notice = "Este informe se genera automáticamente a partir de tus respuestas y " +
                "busca orientarte en la exploración de tu vocación. No sustituye la asesoría " +
                "profesional."
            val noticeLines = wrapText(
                text = notice,
                maxWidth = PAGE_WIDTH - MARGIN * 2,
                size = 10f,
                bold = true,
            )
            if (cursorY + noticeLines.size * 13f < PAGE_HEIGHT - 56f) {
                drawLines(
                    lines = noticeLines,
                    x = MARGIN,
                    firstBaseline = cursorY,
                    lineHeight = 13f,
                    size = 10f,
                    color = GrayColor,
                    bold = true,
                )
            }

            finishCurrentPage()
            return pageNumber
        }

        private fun startPage() {
            pageNumber += 1
            page = document.startPage(
                PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create(),
            )
            canvas = requireNotNull(page).canvas
        }

        private fun startTablePage() {
            finishCurrentPage()
            startPage()
        }

        private fun finishCurrentPage() {
            val activePage = page ?: return
            drawFooter(
                currentPage = pageNumber,
                pageCount = totalPages ?: pageNumber,
            )
            document.finishPage(activePage)
            page = null
        }

        private fun drawHeader() {
            paint.style = Paint.Style.FILL
            paint.color = BrandColor
            canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), HEADER_HEIGHT, paint)

            drawText(
                text = "Orientación Vocacional USB",
                x = MARGIN,
                y = 44f,
                size = 18f,
                color = Color.WHITE,
                bold = true,
            )
            drawText(
                text = "Resultado de tu prueba vocacional",
                x = MARGIN,
                y = 66f,
                size = 11f,
                color = Color.WHITE,
            )
            drawText(
                text = "usbbog.edu.co",
                x = PAGE_WIDTH - MARGIN,
                y = 66f,
                size = 9f,
                color = Color.WHITE,
                alignRight = true,
            )
        }

        private fun drawFooter(
            currentPage: Int,
            pageCount: Int,
        ) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 0.2f
            paint.color = FooterLineColor
            canvas.drawLine(
                MARGIN,
                PAGE_HEIGHT - 40f,
                PAGE_WIDTH - MARGIN,
                PAGE_HEIGHT - 40f,
                paint,
            )
            paint.style = Paint.Style.FILL

            drawText(
                text = "Orientación Vocacional USB · Documento de orientación, no vinculante.",
                x = MARGIN,
                y = PAGE_HEIGHT - 22f,
                size = 9f,
                color = GrayColor,
            )
            drawText(
                text = "Página $currentPage de $pageCount",
                x = PAGE_WIDTH - MARGIN,
                y = PAGE_HEIGHT - 22f,
                size = 9f,
                color = GrayColor,
                alignRight = true,
            )
        }

        private fun drawTable(
            startY: Float,
            headers: List<String>,
            rows: List<List<String>>,
            headerColor: Int,
            fixedWidths: Map<Int, Float>,
            rightAlignedColumns: Set<Int>,
        ): Float {
            val widths = calculateColumnWidths(headers, rows, fixedWidths)
            val headerHeight = rowHeight(
                cells = headers,
                widths = widths,
                size = 10f,
                bold = true,
            )
            var tableY = startY

            fun drawHeaderRow() {
                drawTableRow(
                    y = tableY,
                    cells = headers,
                    widths = widths,
                    height = headerHeight,
                    backgroundColor = headerColor,
                    textColor = Color.WHITE,
                    size = 10f,
                    bold = true,
                    rightAlignedColumns = rightAlignedColumns,
                )
                tableY += headerHeight
            }

            val firstBodyHeight = rows.firstOrNull()?.let { row ->
                rowHeight(row, widths, size = 9f, bold = false)
            } ?: 0f
            if (tableY + headerHeight + firstBodyHeight > TABLE_BOTTOM) {
                startTablePage()
                tableY = TABLE_TOP_MARGIN
            }
            drawHeaderRow()

            rows.forEach { row ->
                val bodyHeight = rowHeight(
                    cells = row,
                    widths = widths,
                    size = 9f,
                    bold = false,
                )
                if (tableY + bodyHeight > TABLE_BOTTOM) {
                    startTablePage()
                    tableY = TABLE_TOP_MARGIN
                    drawHeaderRow()
                }
                drawTableRow(
                    y = tableY,
                    cells = row,
                    widths = widths,
                    height = bodyHeight,
                    backgroundColor = Color.WHITE,
                    textColor = DarkColor,
                    size = 9f,
                    bold = false,
                    rightAlignedColumns = rightAlignedColumns,
                )
                tableY += bodyHeight
            }

            return tableY
        }

        private fun calculateColumnWidths(
            headers: List<String>,
            rows: List<List<String>>,
            fixedWidths: Map<Int, Float>,
        ): List<Float> {
            val totalWidth = PAGE_WIDTH - MARGIN * 2
            val fixedTotal = fixedWidths.values.sum()
            val flexibleIndices = headers.indices.filterNot(fixedWidths::containsKey)
            val naturalWidths = flexibleIndices.associateWith { index ->
                val headerWidth = textWidth(headers[index], size = 10f, bold = true)
                val bodyWidth = rows.maxOfOrNull { row ->
                    textWidth(row.getOrElse(index) { "" }, size = 9f, bold = false)
                } ?: 0f
                maxOf(headerWidth, bodyWidth) + CELL_PADDING * 2
            }
            val naturalTotal = naturalWidths.values.sum().takeIf { it > 0f } ?: 1f
            val flexibleWidth = (totalWidth - fixedTotal).coerceAtLeast(0f)

            return headers.indices.map { index ->
                fixedWidths[index]
                    ?: flexibleWidth * (naturalWidths.getValue(index) / naturalTotal)
            }
        }

        private fun rowHeight(
            cells: List<String>,
            widths: List<Float>,
            size: Float,
            bold: Boolean,
        ): Float {
            val lineHeight = size * 1.15f
            val maximumLines = cells.indices.maxOfOrNull { index ->
                wrapText(
                    text = cells[index],
                    maxWidth = widths[index] - CELL_PADDING * 2,
                    size = size,
                    bold = bold,
                ).size
            } ?: 1
            return maximumLines * lineHeight + CELL_PADDING * 2
        }

        private fun drawTableRow(
            y: Float,
            cells: List<String>,
            widths: List<Float>,
            height: Float,
            backgroundColor: Int,
            textColor: Int,
            size: Float,
            bold: Boolean,
            rightAlignedColumns: Set<Int>,
        ) {
            var x = MARGIN
            val lineHeight = size * 1.15f

            cells.indices.forEach { index ->
                val width = widths[index]
                val bounds = RectF(x, y, x + width, y + height)

                paint.style = Paint.Style.FILL
                paint.color = backgroundColor
                canvas.drawRect(bounds, paint)

                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 0.1f
                paint.color = TableLineColor
                canvas.drawRect(bounds, paint)
                paint.style = Paint.Style.FILL

                val lines = wrapText(
                    text = cells[index],
                    maxWidth = width - CELL_PADDING * 2,
                    size = size,
                    bold = bold,
                )
                lines.forEachIndexed { lineIndex, line ->
                    drawText(
                        text = line,
                        x = if (index in rightAlignedColumns) {
                            x + width - CELL_PADDING
                        } else {
                            x + CELL_PADDING
                        },
                        y = y + CELL_PADDING + size + lineIndex * lineHeight,
                        size = size,
                        color = textColor,
                        bold = bold,
                        alignRight = index in rightAlignedColumns,
                    )
                }

                x += width
            }
        }

        private fun drawLines(
            lines: List<String>,
            x: Float,
            firstBaseline: Float,
            lineHeight: Float,
            size: Float,
            color: Int,
            bold: Boolean = false,
        ) {
            lines.forEachIndexed { index, line ->
                drawText(
                    text = line,
                    x = x,
                    y = firstBaseline + index * lineHeight,
                    size = size,
                    color = color,
                    bold = bold,
                )
            }
        }

        private fun wrapText(
            text: String,
            maxWidth: Float,
            size: Float,
            bold: Boolean = false,
        ): List<String> {
            configureTextPaint(size, Color.BLACK, bold, alignRight = false)
            val paragraphs = text.split('\n')
            return paragraphs.flatMap { paragraph ->
                val words = paragraph.trim().split(Regex("\\s+")).filter(String::isNotBlank)
                if (words.isEmpty()) {
                    listOf("")
                } else {
                    buildList {
                        var currentLine = ""
                        words.forEach { word ->
                            val candidate = if (currentLine.isBlank()) word else "$currentLine $word"
                            if (paint.measureText(candidate) <= maxWidth || currentLine.isBlank()) {
                                currentLine = candidate
                            } else {
                                add(currentLine)
                                currentLine = word
                            }
                        }
                        if (currentLine.isNotBlank()) add(currentLine)
                    }
                }
            }
        }

        private fun textWidth(
            text: String,
            size: Float,
            bold: Boolean,
        ): Float {
            configureTextPaint(size, Color.BLACK, bold, alignRight = false)
            return paint.measureText(text)
        }

        private fun drawText(
            text: String,
            x: Float,
            y: Float,
            size: Float,
            color: Int,
            bold: Boolean = false,
            alignRight: Boolean = false,
        ) {
            configureTextPaint(size, color, bold, alignRight)
            canvas.drawText(text, x, y, paint)
        }

        private fun configureTextPaint(
            size: Float,
            color: Int,
            bold: Boolean,
            alignRight: Boolean,
        ) {
            paint.style = Paint.Style.FILL
            paint.textSize = size
            paint.color = color
            paint.typeface = if (bold) boldTypeface else regularTypeface
            paint.textAlign = if (alignRight) Paint.Align.RIGHT else Paint.Align.LEFT
        }

        private fun formatWebDate(result: ResultsUiState): String {
            val rawDate = result.generatedAtRaw.takeIf(String::isNotBlank)
                ?: return result.generatedAt
            val parsed = runCatching {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                    isLenient = true
                }.parse(rawDate.take(19))
            }.getOrNull() ?: return result.generatedAt.ifBlank { rawDate }

            return SimpleDateFormat(
                "d MMM yyyy, h:mm a",
                Locale("es", "CO"),
            ).format(parsed)
        }
    }
}
