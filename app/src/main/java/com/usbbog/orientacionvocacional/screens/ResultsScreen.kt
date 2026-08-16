package com.usbbog.orientacionvocacional.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usbbog.orientacionvocacional.R
import com.usbbog.orientacionvocacional.ui.components.UsbAppFooter
import com.usbbog.orientacionvocacional.ui.components.UsbAppTopBar
import com.usbbog.orientacionvocacional.ui.mobile.CareerResultUi
import com.usbbog.orientacionvocacional.ui.mobile.ResultScoreUi
import com.usbbog.orientacionvocacional.ui.theme.USBColors

private val ResultPanelBackground = Color(0xFFFFFDFB)
private val ResultSectionBorder = Color(0xFFDAD3C6)
private val ResultCardBackground = Color(0xFFFCFCFC)
private val ResultCardBorder = Color(0xFFECE6DB)
private val ResultText = Color(0xFF121212)
private val ResultMuted = Color(0xFF6A6A6A)
private val ResultHint = Color(0xFFA45C09)
private val ResultSelectedOrange = Color(0xFFB25D00)
private val ResultGrid = Color(0xFFE9E4DB)

@Composable
fun ResultsScreen(
    userName: String,
    mainAreaId: Long?,
    mainArea: String,
    summary: String,
    scores: List<ResultScoreUi>,
    careers: List<CareerResultUi>,
    generatedAt: String,
    isDownloading: Boolean,
    downloadStatus: String?,
    backLabel: String,
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    var selectedAreaId by rememberSaveable(mainAreaId) { mutableStateOf(mainAreaId) }
    val selectedScore = scores.firstOrNull { it.areaId == selectedAreaId }
        ?: scores.firstOrNull()
    val topCareerAffinity = careers.maxOfOrNull(CareerResultUi::score) ?: 0

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        0f to USBColors.Orange,
                        0.30f to USBColors.Orange,
                        1f to USBColors.Blue,
                    ),
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(14.dp))

                ResultsBackButton(
                    label = backLabel,
                    onClick = onBackClick,
                    modifier = Modifier.padding(horizontal = 10.dp),
                )

                Spacer(Modifier.height(12.dp))

                ResultsWebPanel(
                    userName = userName,
                    mainAreaId = mainAreaId,
                    mainArea = mainArea,
                    summary = summary,
                    scores = scores,
                    selectedScore = selectedScore,
                    careers = careers,
                    topCareerAffinity = topCareerAffinity,
                    generatedAt = generatedAt,
                    isDownloading = isDownloading,
                    downloadStatus = downloadStatus,
                    onAreaSelect = { selectedAreaId = it },
                    onLearnMore = { url -> runCatching { uriHandler.openUri(url) } },
                    onDownloadClick = onDownloadClick,
                    modifier = Modifier.padding(horizontal = 10.dp),
                )

                Spacer(Modifier.height(20.dp))

                FinancingAction(
                    onClick = {
                        runCatching {
                            uriHandler.openUri(
                                "https://www.usbbog.edu.co/admisiones/financiacion-y-pagos/",
                            )
                        }
                    },
                    modifier = Modifier.padding(horizontal = 10.dp),
                )

                Spacer(Modifier.height(20.dp))
                UsbAppFooter()
            }
        }
    }
}

@Composable
private fun ResultsBackButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp),
        shape = CircleShape,
        border = BorderStroke(2.dp, USBColors.White),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = USBColors.White,
            contentColor = USBColors.Orange,
        ),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 11.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.ArrowBack,
            contentDescription = null,
            modifier = Modifier.size(19.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ResultsWebPanel(
    userName: String,
    mainAreaId: Long?,
    mainArea: String,
    summary: String,
    scores: List<ResultScoreUi>,
    selectedScore: ResultScoreUi?,
    careers: List<CareerResultUi>,
    topCareerAffinity: Int,
    generatedAt: String,
    isDownloading: Boolean,
    downloadStatus: String?,
    onAreaSelect: (Long?) -> Unit,
    onLearnMore: (String) -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = ResultPanelBackground,
        shadowElevation = 12.dp,
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            ResultSection {
                Text(
                    text = "Resultados de tu prueba vocacional",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 18.dp),
                    color = USBColors.Orange,
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(16.dp))
            PrimaryProfileSection(userName, mainAreaId, mainArea, summary)
            Spacer(Modifier.height(16.dp))

            AffinitySection(
                scores = scores,
                selectedScore = selectedScore,
                onAreaSelect = onAreaSelect,
            )

            Spacer(Modifier.height(16.dp))
            RecommendedCareersSection(careers, onLearnMore)
            Spacer(Modifier.height(16.dp))

            ProfileSummarySection(
                mainArea = mainArea,
                topCareerAffinity = topCareerAffinity,
                generatedAt = generatedAt,
                isDownloading = isDownloading,
                downloadStatus = downloadStatus,
                onDownloadClick = onDownloadClick,
            )
        }
    }
}

@Composable
private fun ResultSection(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = USBColors.White,
        border = BorderStroke(1.dp, ResultSectionBorder),
        shadowElevation = 4.dp,
        content = content,
    )
}

@Composable
private fun PrimaryProfileSection(
    userName: String,
    mainAreaId: Long?,
    mainArea: String,
    summary: String,
) {
    ResultSection {
        Column(
            modifier = Modifier.padding(
                start = 14.dp,
                top = 28.dp,
                end = 14.dp,
                bottom = 18.dp,
            ),
        ) {
            Image(
                painter = painterResource(areaImageResource(mainAreaId)),
                contentDescription = "Ilustración del área $mainArea",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(206.dp)
                    .shadow(10.dp, RoundedCornerShape(32.dp))
                    .clip(RoundedCornerShape(32.dp))
                    .background(USBColors.Orange),
                contentScale = ContentScale.Crop,
            )

            Spacer(Modifier.height(18.dp))
            Text(
                text = mainArea,
                color = ResultText,
                fontSize = 30.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(14.dp))
            Text(
                text = summary.ifBlank {
                    "Tu perfil vocacional fue calculado con base en tus respuestas de la prueba."
                },
                color = ResultMuted,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            )

            Spacer(Modifier.height(18.dp))
            Text(
                text = userName.ifBlank { "Nombre Usuario" },
                color = ResultText,
                fontSize = 30.sp,
                lineHeight = 35.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Tu perfil presenta mayor afinidad con:",
                color = ResultMuted,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            )
        }
    }
}

@Composable
private fun AffinitySection(
    scores: List<ResultScoreUi>,
    selectedScore: ResultScoreUi?,
    onAreaSelect: (Long?) -> Unit,
) {
    ResultSection {
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 12.dp,
            ),
        ) {
            Text(
                text = "Afinidad por Área",
                modifier = Modifier.fillMaxWidth(),
                color = ResultText,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(10.dp))
            VerticalAffinityChart(scores, selectedScore?.areaId, onAreaSelect)
            Spacer(Modifier.height(10.dp))

            Text(
                text = "Haz clic en una barra para ver el perfil del área.",
                modifier = Modifier.fillMaxWidth(),
                color = ResultHint,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(22.dp))
            Text(
                text = "Tu perfil vocacional",
                modifier = Modifier.fillMaxWidth(),
                color = ResultText,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(16.dp))
            if (selectedScore != null) {
                SelectedAreaProfile(selectedScore)
            } else {
                Text(
                    text = "Selecciona un área para ver su perfil.",
                    modifier = Modifier.fillMaxWidth(),
                    color = ResultHint,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun VerticalAffinityChart(
    scores: List<ResultScoreUi>,
    selectedAreaId: Long?,
    onAreaSelect: (Long?) -> Unit,
) {
    if (scores.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "No hay datos de afinidad disponibles.",
                color = ResultMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
        }
        return
    }

    val highestScore = scores.maxOf(ResultScoreUi::percentage).coerceAtLeast(1)
    val chartMaximum = ((highestScore + 3) / 4) * 4
    val tickValues = (4 downTo 0).map { step -> chartMaximum * step / 4 }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(238.dp),
        ) {
            Column(
                modifier = Modifier
                    .width(34.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                tickValues.forEach { tick ->
                    Text(
                        text = tick.toString(),
                        color = Color(0xFF8C8C8C),
                        fontSize = 11.sp,
                    )
                }
            }

            Spacer(Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val dash = PathEffect.dashPathEffect(floatArrayOf(7f, 7f))
                    repeat(5) { index ->
                        val y = size.height * index / 4f
                        drawLine(
                            color = ResultGrid,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = dash,
                        )
                    }
                    repeat(scores.size + 1) { index ->
                        val x = size.width * index / scores.size
                        drawLine(
                            color = ResultGrid,
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = dash,
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    scores.forEach { score ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { onAreaSelect(score.areaId) },
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.72f)
                                    .fillMaxHeight(
                                        score.percentage
                                            .coerceIn(0, chartMaximum)
                                            .toFloat() / chartMaximum,
                                    )
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 8.dp,
                                            topEnd = 8.dp,
                                        ),
                                    )
                                    .background(
                                        if (score.areaId == selectedAreaId) {
                                            ResultSelectedOrange
                                        } else {
                                            USBColors.Orange
                                        },
                                    ),
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Área",
            modifier = Modifier.fillMaxWidth(),
            color = ResultMuted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SelectedAreaProfile(score: ResultScoreUi) {
    val traits = score.description
        .removeSuffix(".")
        .split(',')
        .map { it.trim() }
        .filter(String::isNotBlank)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 8.dp),
    ) {
        if (maxWidth < 520.dp) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SelectedAreaImage(
                    score = score,
                    modifier = Modifier.size(160.dp),
                )

                Spacer(Modifier.height(14.dp))

                SelectedAreaDetails(
                    areaLabel = score.label,
                    traits = traits,
                    centerTitle = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SelectedAreaImage(
                    score = score,
                    modifier = Modifier.size(180.dp),
                )

                Spacer(Modifier.width(18.dp))

                SelectedAreaDetails(
                    areaLabel = score.label,
                    traits = traits,
                    centerTitle = false,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun SelectedAreaImage(
    score: ResultScoreUi,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(pachoImageResource(score.areaId)),
        contentDescription = "Perfil del área ${score.label}",
        modifier = modifier.clip(RoundedCornerShape(24.dp)),
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun SelectedAreaDetails(
    areaLabel: String,
    traits: List<String>,
    centerTitle: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = areaLabel,
            modifier = Modifier.fillMaxWidth(),
            color = USBColors.Orange,
            fontSize = 19.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = if (centerTitle) TextAlign.Center else TextAlign.Start,
        )

        traits.forEach { trait ->
            Spacer(Modifier.height(7.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "•",
                    color = USBColors.Orange,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = trait,
                    modifier = Modifier.weight(1f),
                    color = ResultText,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                )
            }
        }
    }
}

@Composable
private fun RecommendedCareersSection(
    careers: List<CareerResultUi>,
    onLearnMore: (String) -> Unit,
) {
    ResultSection {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 18.dp)) {
            Text(
                text = "Carreras recomendadas",
                color = USBColors.Orange,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(16.dp))
            if (careers.isEmpty()) {
                Text(
                    text = "No hay carreras recomendadas disponibles.",
                    color = ResultMuted,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                )
            } else {
                careers.forEachIndexed { index, career ->
                    if (index > 0) Spacer(Modifier.height(16.dp))
                    CareerRecommendationCard(
                        career = career,
                        onLearnMore = career.url
                            ?.takeIf(String::isNotBlank)
                            ?.let { url -> { onLearnMore(url) } },
                    )
                }
            }
        }
    }
}

@Composable
private fun CareerRecommendationCard(
    career: CareerResultUi,
    onLearnMore: (() -> Unit)?,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = ResultCardBackground,
        border = BorderStroke(1.dp, ResultCardBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(USBColors.Orange),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp, top = 18.dp, end = 18.dp, bottom = 18.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(65.dp)
                        .background(USBColors.Orange, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = career.rank.toString(),
                        color = USBColors.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.height(14.dp))
                Text(
                    text = career.name,
                    color = ResultText,
                    fontSize = 30.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(Modifier.height(6.dp))
                Text(
                    text = "${career.score}% de compatibilidad",
                    color = USBColors.Orange,
                    fontSize = 20.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(Modifier.height(18.dp))
                Text(
                    text = career.description,
                    color = ResultMuted,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                )

                if (onLearnMore != null) {
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = onLearnMore,
                        modifier = Modifier.heightIn(min = 54.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = USBColors.Orange,
                            contentColor = USBColors.White,
                        ),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                    ) {
                        Text(
                            text = "Conocer más",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileSummarySection(
    mainArea: String,
    topCareerAffinity: Int,
    generatedAt: String,
    isDownloading: Boolean,
    downloadStatus: String?,
    onDownloadClick: () -> Unit,
) {
    ResultSection {
        Column(
            modifier = Modifier.padding(
                start = 14.dp,
                top = 20.dp,
                end = 14.dp,
                bottom = 26.dp,
            ),
        ) {
            Text(
                text = "Resumen de tu perfil",
                color = USBColors.Orange,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(16.dp))
            Text(
                text = buildAnnotatedString {
                    append("Según tus respuestas, tu mayor afinidad es con el área de ")
                    withStyle(
                        SpanStyle(
                            color = USBColors.Orange,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    ) {
                        append(mainArea)
                    }
                    append(", con un resultado de ")
                    withStyle(
                        SpanStyle(
                            color = USBColors.Orange,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    ) {
                        append("$topCareerAffinity%")
                    }
                    append(
                        ". Esto indica que podrías sentirte cómodo/a explorando carreras " +
                            "relacionadas, ya que se alinean con tus intereses, habilidades y " +
                            "preferencias.",
                    )
                },
                color = ResultMuted,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            )

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Recuerda que este resultado es una orientación inicial: úsalo como " +
                    "punto de partida para conocer programas, investigar sus campos de acción y " +
                    "descubrir cuáles se ajustan mejor a tu proyecto de vida.",
                color = ResultMuted,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = "Informe generado el $generatedAt con recomendaciones iniciales.",
                color = ResultMuted,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            )

            if (!downloadStatus.isNullOrBlank()) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = downloadStatus,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(USBColors.OrangeSoft, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    color = ResultHint,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            }

            Spacer(Modifier.height(22.dp))
            OutlinedButton(
                onClick = onDownloadClick,
                enabled = !isDownloading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(4.dp, USBColors.Orange),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = USBColors.White,
                    contentColor = USBColors.Orange,
                    disabledContainerColor = USBColors.White,
                    disabledContentColor = USBColors.Orange.copy(alpha = 0.55f),
                ),
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = USBColors.Orange,
                        strokeWidth = 2.dp,
                    )
                    Spacer(Modifier.width(10.dp))
                }
                Text(
                    text = if (isDownloading) "Generando PDF..." else "Descargar PDF",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (!isDownloading) {
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = "↓",
                        fontSize = 24.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun FinancingAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = USBColors.Orange,
        contentColor = USBColors.White,
        shadowElevation = 10.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 7.dp, end = 14.dp, bottom = 7.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .shadow(6.dp, CircleShape)
                    .background(
                        Brush.linearGradient(listOf(USBColors.Blue, USBColors.Orange)),
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "USB",
                    color = USBColors.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.width(10.dp))
            Text(
                text = "Financiamiento",
                color = USBColors.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun areaImageResource(areaId: Long?): Int = when (areaId?.toInt()) {
    2 -> R.drawable.area_ciencias_salud
    3 -> R.drawable.area_ingenieria
    4 -> R.drawable.area_ciencias_economicas_administrativas
    5 -> R.drawable.area_ciencias_sociales_humanas
    6 -> R.drawable.area_artes
    7 -> R.drawable.area_ciencias_exactas
    8 -> R.drawable.area_educacion
    9 -> R.drawable.area_ciencias_agrarias
    else -> R.drawable.guide_results
}

private fun pachoImageResource(areaId: Long?): Int = when (areaId?.toInt()) {
    2 -> R.drawable.pacho_ciencias_salud
    3 -> R.drawable.pacho_ingenieria
    4 -> R.drawable.pacho_ciencias_economicas_administrativas
    5 -> R.drawable.pacho_ciencias_sociales_humanas
    6 -> R.drawable.pacho_artes
    7 -> R.drawable.pacho_ciencias_exactas
    8 -> R.drawable.pacho_educacion
    9 -> R.drawable.pacho_ciencias_agrarias
    else -> areaImageResource(areaId)
}
