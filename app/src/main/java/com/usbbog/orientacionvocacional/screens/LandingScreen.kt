package com.usbbog.orientacionvocacional.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LandingOrange = Color(0xFFEF7D00)
private val LandingBlue = Color(0xFF181E7B)
private val LandingBlack = Color(0xFF1C1C1C)
private val LandingWhite = Color(0xFFFFFFFF)
private val LandingSurface = Color(0xFFF1F1F2)
private val LandingMuted = Color(0xFF65656A)

private data class LandingStepItem(
    val number: Int,
    val title: String,
    val description: String,
)

/**
 * Adaptación móvil de LandingPage.
 *
 * Esta pantalla no decide las rutas. AppNavigation conecta tanto
 * "Iniciar prueba" como "Ingresar" con Routes.LOGIN.
 */
@Composable
fun LandingWebScreenV2(
    onStartClick: () -> Unit,
    onLoginClick: () -> Unit,
    onHelpClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val steps = listOf(
        LandingStepItem(
            number = 1,
            title = "Registro",
            description =
                "Crea tu cuenta proporcionando información básica para personalizar tu experiencia",
        ),
        LandingStepItem(
            number = 2,
            title = "Prueba Vocacional",
            description =
                "Responde 180 preguntas diseñadas para evaluar tus intereses, habilidades y " +
                        "preferencias (estimado de duración 40 minutos).",
        ),
        LandingStepItem(
            number = 3,
            title = "Resultados",
            description =
                "Obtén un análisis y opciones de carreras recomendadas de acuerdo con las " +
                        "respuestas brindadas.",
        ),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LandingWhite),
    ) {
        LandingTopBar(
            onHelpClick = onHelpClick,
            onLoginClick = onLoginClick,
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            item {
                LandingHero(onStartClick = onStartClick)
            }

            item {
                LandingStepsSection(steps = steps)
            }

            item {
                LandingAboutSection()
            }

            item {
                LandingFooter()
            }
        }
    }
}

@Composable
private fun LandingTopBar(
    onHelpClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .heightIn(min = 68.dp)
            .shadow(elevation = 4.dp)
            .background(LandingWhite)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(LandingOrange),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "USB",
                    color = LandingWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }

            Spacer(Modifier.width(8.dp))

            Column {
                Text(
                    text = "Universidad de San Buenaventura",
                    color = LandingBlack,
                    fontSize = 10.5.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
                Text(
                    text = "Bogotá",
                    color = LandingMuted,
                    fontSize = 9.5.sp,
                    lineHeight = 11.sp,
                )
            }
        }

        Spacer(Modifier.width(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .height(42.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFBEBEBE),
                        shape = RoundedCornerShape(100.dp),
                    )
                    .clickable(onClick = onHelpClick)
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(21.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = LandingMuted,
                            radius = size.minDimension / 2f - 0.75.dp.toPx(),
                            style = Stroke(width = 1.5.dp.toPx()),
                        )
                    }
                    Text(
                        text = "?",
                        color = LandingMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.width(5.dp))

                Text(
                    text = "Ayuda",
                    color = LandingMuted,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Box(
                modifier = Modifier
                    .height(42.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(LandingOrange)
                    .clickable(onClick = onLoginClick)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Ingresar",
                    color = LandingWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun LandingHero(
    onStartClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 500.dp)
            .background(
                Brush.verticalGradient(
                    0.10f to LandingOrange,
                    0.43f to Color(0xFFCB6A17),
                    0.70f to Color(0xFF714548),
                    1.00f to LandingBlue,
                ),
            )
            .padding(horizontal = 22.dp, vertical = 42.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 720.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Descubre tu camino académico y profesional con mayor claridad",
                color = LandingWhite,
                fontSize = 32.sp,
                lineHeight = 42.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text =
                    "La plataforma de orientación vocacional de la Universidad de San " +
                            "Buenaventura sede de Bogotá te ayuda a identificar intereses, habilidades " +
                            "y afinidades profesionales mediante pruebas, resultados guiados y " +
                            "recomendaciones académicas personalizadas.",
                color = LandingWhite,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(32.dp))

            OutlinedButton(
                onClick = onStartClick,
                modifier = Modifier
                    .width(250.dp)
                    .heightIn(min = 64.dp),
                shape = RoundedCornerShape(40.dp),
                border = BorderStroke(4.dp, LandingWhite),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = LandingWhite,
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    text = "Iniciar prueba",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun LandingStepsSection(
    steps: List<LandingStepItem>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LandingSurface)
            .padding(horizontal = 18.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "¿Cómo funciona la prueba vocacional?",
            modifier = Modifier.fillMaxWidth(),
            color = LandingBlack,
            fontSize = 28.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(30.dp))

        steps.forEachIndexed { index, step ->
            LandingStepCard(step = step)
            if (index < steps.lastIndex) {
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun LandingStepCard(
    step: LandingStepItem,
) {
    val shape = RoundedCornerShape(22.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 520.dp)
            .heightIn(min = 250.dp)
            .shadow(
                elevation = 5.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.06f),
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = LandingWhite),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(LandingOrange),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = step.number.toString(),
                    color = LandingWhite,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = step.title,
                color = LandingBlack,
                fontSize = 27.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = highlightedDescription(step.description),
                color = LandingBlack,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            )
        }
    }
}

private fun highlightedDescription(description: String) = buildAnnotatedString {
    if (!description.contains("180")) {
        append(description)
        return@buildAnnotatedString
    }

    val firstHighlight = "180"
    val secondHighlight = "(estimado de duración 40 minutos)."
    val firstStart = description.indexOf(firstHighlight)
    val secondStart = description.indexOf(secondHighlight)

    append(description.substring(0, firstStart))
    withStyle(SpanStyle(color = LandingOrange, fontWeight = FontWeight.Bold)) {
        append(firstHighlight)
    }

    if (secondStart >= 0) {
        append(description.substring(firstStart + firstHighlight.length, secondStart))
        withStyle(SpanStyle(color = LandingOrange, fontWeight = FontWeight.Bold)) {
            append(secondHighlight)
        }
        append(description.substring(secondStart + secondHighlight.length))
    } else {
        append(description.substring(firstStart + firstHighlight.length))
    }
}

@Composable
private fun LandingAboutSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LandingWhite)
            .padding(horizontal = 22.dp, vertical = 58.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Universidad de San Buenaventura sede de Bogotá",
            color = LandingBlack,
            fontSize = 28.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(26.dp))

        Text(
            text =
                "Institución comprometida con la formación integral de profesionales competentes, " +
                        "éticos y con sentido social. Nuestra prueba vocacional está respaldada por " +
                        "metodologías científicas validadas que te ayudarán a tener una primera guía " +
                        "para tu decisión de vida académica.",
            modifier = Modifier.widthIn(max = 720.dp),
            color = Color(0xFF223058),
            fontSize = 16.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LandingFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(LandingBlue)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text =
                "Copyright © 2026 Universidad de San Buenaventura, Sede Bogotá\n" +
                        "Políticas de uso y privacidad | Términos y Condiciones\n" +
                        "Institución de educación superior sujeta a la inspección y vigilancia del " +
                        "Ministerio de Educación Nacional",
            color = LandingWhite,
            fontSize = 10.5.sp,
            lineHeight = 15.sp,
            textAlign = TextAlign.Center,
        )
    }
}