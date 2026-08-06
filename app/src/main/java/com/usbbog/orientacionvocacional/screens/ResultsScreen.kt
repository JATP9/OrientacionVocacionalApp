package com.usbbog.orientacionvocacional.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.InsertChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usbbog.orientacionvocacional.R
import com.usbbog.orientacionvocacional.ui.components.UsbAppFooter
import com.usbbog.orientacionvocacional.ui.components.UsbAppTopBar
import com.usbbog.orientacionvocacional.ui.components.UsbGuideImage
import com.usbbog.orientacionvocacional.ui.mobile.CareerResultUi
import com.usbbog.orientacionvocacional.ui.mobile.ResultScoreUi
import com.usbbog.orientacionvocacional.ui.theme.USBColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ResultsScreen(
    userName: String,
    mainArea: String,
    summary: String,
    scores: List<ResultScoreUi>,
    careers: List<CareerResultUi>,
    generatedAt: String,
    isDownloading: Boolean,
    downloadStatus: String?,
    onDownloadClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            UsbAppTopBar(
                userLabel = userName,
                onUserClick = onProfileClick,
            )
        },
        containerColor = USBColors.Orange,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        0f to USBColors.Orange,
                        0.62f to USBColors.Orange,
                        1f to USBColors.Blue,
                    ),
                ),
            contentPadding = PaddingValues(top = 18.dp, bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "RESULTADO VOCACIONAL",
                        color = USBColors.White.copy(alpha = 0.86f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Resultados de tu Prueba Vocacional",
                        color = USBColors.White,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 30.sp,
                            lineHeight = 37.sp,
                            fontWeight = FontWeight.ExtraBold,
                        ),
                    )
                }
            }

            item {
                ResultHeroCard(
                    mainArea = mainArea,
                    summary = summary,
                    topAffinity = careers.firstOrNull()?.score ?: scores.firstOrNull()?.percentage ?: 0,
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
            }

            item {
                ResultCard(
                    title = "Afinidad por área",
                    subtitle = "Comparación de los puntajes obtenidos",
                    modifier = Modifier.padding(horizontal = 14.dp),
                ) {
                    scores.forEach { score ->
                        AffinityBar(score)
                        Spacer(Modifier.height(14.dp))
                    }
                }
            }

            item {
                ResultCard(
                    title = "Perfil de habilidades",
                    subtitle = "Vista radial de tus cinco áreas evaluadas",
                    modifier = Modifier.padding(horizontal = 14.dp),
                ) {
                    RadarProfileChart(
                        scores = scores,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        scores.forEachIndexed { index, score ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(9.dp)
                                        .background(radarColors[index % radarColors.size], CircleShape),
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "${score.label}: ${score.percentage}%",
                                    color = USBColors.TextMuted,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Programas recomendados",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = USBColors.White,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                )
            }

            items(careers, key = CareerResultUi::rank) { career ->
                CareerRecommendationCard(
                    career = career,
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
            }

            item {
                ResultCard(
                    title = "Resumen de tu perfil",
                    subtitle = "Informe generado el $generatedAt",
                    modifier = Modifier.padding(horizontal = 14.dp),
                ) {
                    Text(
                        text = "Tus resultados indican una inclinación hacia $mainArea. " +
                            "Úsalos como punto de partida para explorar programas, conversar con orientadores " +
                            "y tomar una decisión informada.",
                        color = USBColors.TextMuted,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                    )

                    if (!downloadStatus.isNullOrBlank()) {
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = downloadStatus,
                            color = USBColors.Success,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = onDownloadClick,
                        enabled = !isDownloading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = USBColors.Orange,
                            contentColor = USBColors.White,
                        ),
                    ) {
                        if (isDownloading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = USBColors.White,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Icon(Icons.Outlined.Download, contentDescription = null)
                        }
                        Spacer(Modifier.width(9.dp))
                        Text(
                            text = if (isDownloading) "Generando PDF..." else "Descargar PDF",
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Este resultado corresponde a un tamizaje de orientación vocacional y no " +
                        "reemplaza un proceso profesional de orientación académica o psicológica.",
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 4.dp),
                    color = USBColors.White.copy(alpha = 0.78f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                )
            }

            item { UsbAppFooter() }
        }
    }
}

@Composable
private fun ResultHeroCard(
    mainArea: String,
    summary: String,
    topAffinity: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = USBColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UsbGuideImage(
                    drawableRes = R.drawable.guide_results,
                    contentDescription = "Ilustración de resultados",
                    modifier = Modifier.size(104.dp),
                )
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ÁREA PRINCIPAL",
                        color = USBColors.Orange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = mainArea,
                        color = USBColors.Black,
                        fontSize = 24.sp,
                        lineHeight = 29.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "$topAffinity% de afinidad principal",
                        color = USBColors.Blue,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = summary,
                color = USBColors.TextMuted,
                fontSize = 14.sp,
                lineHeight = 21.sp,
            )
        }
    }
}

@Composable
private fun ResultCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = USBColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                color = USBColors.Black,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            )
            Spacer(Modifier.height(4.dp))
            Text(text = subtitle, color = USBColors.TextMuted, fontSize = 13.sp)
            Spacer(Modifier.height(18.dp))
            content()
        }
    }
}

@Composable
private fun AffinityBar(score: ResultScoreUi) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = score.label,
                color = USBColors.Text,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${score.percentage}%",
                color = USBColors.Orange,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.height(7.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(USBColors.Sand, RoundedCornerShape(100.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(score.percentage.coerceIn(0, 100) / 100f)
                    .height(12.dp)
                    .background(
                        Brush.horizontalGradient(listOf(USBColors.Orange, USBColors.OrangeStrong)),
                        RoundedCornerShape(100.dp),
                    ),
            )
        }
    }
}

private val radarColors = listOf(
    USBColors.Orange,
    Color(0xFFDAA520),
    Color(0xFF2D8C73),
    USBColors.Blue,
    Color(0xFF9A4D8F),
)

@Composable
private fun RadarProfileChart(
    scores: List<ResultScoreUi>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        if (scores.size < 3) return@Canvas

        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = min(size.width, size.height) * 0.38f
        val count = scores.size

        fun point(index: Int, factor: Float): Offset {
            val angle = -PI / 2 + (2 * PI * index / count)
            return Offset(
                x = center.x + (cos(angle) * radius * factor).toFloat(),
                y = center.y + (sin(angle) * radius * factor).toFloat(),
            )
        }

        for (level in 1..4) {
            val factor = level / 4f
            val gridPath = Path()
            repeat(count) { index ->
                val p = point(index, factor)
                if (index == 0) gridPath.moveTo(p.x, p.y) else gridPath.lineTo(p.x, p.y)
            }
            gridPath.close()
            drawPath(gridPath, USBColors.GrayBorder, style = Stroke(width = 1.dp.toPx()))
        }

        repeat(count) { index ->
            drawLine(
                color = USBColors.GrayBorder,
                start = center,
                end = point(index, 1f),
                strokeWidth = 1.dp.toPx(),
            )
        }

        val valuePath = Path()
        scores.forEachIndexed { index, score ->
            val p = point(index, score.percentage.coerceIn(0, 100) / 100f)
            if (index == 0) valuePath.moveTo(p.x, p.y) else valuePath.lineTo(p.x, p.y)
        }
        valuePath.close()
        drawPath(valuePath, USBColors.Orange.copy(alpha = 0.22f))
        drawPath(valuePath, USBColors.Orange, style = Stroke(width = 3.dp.toPx()))

        scores.forEachIndexed { index, score ->
            drawCircle(
                color = radarColors[index % radarColors.size],
                radius = 5.dp.toPx(),
                center = point(index, score.percentage.coerceIn(0, 100) / 100f),
            )
        }
    }
}

@Composable
private fun CareerRecommendationCard(
    career: CareerResultUi,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = USBColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier.padding(17.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(USBColors.Orange, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = career.rank.toString(),
                    color = USBColors.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
            Spacer(Modifier.width(13.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = career.name,
                    color = USBColors.Black,
                    fontSize = 19.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = career.area,
                    color = USBColors.Orange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = career.description,
                    color = USBColors.TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Outlined.InsertChart,
                    contentDescription = null,
                    tint = USBColors.Blue,
                )
                Text(
                    text = "${career.score}%",
                    color = USBColors.Black,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}
