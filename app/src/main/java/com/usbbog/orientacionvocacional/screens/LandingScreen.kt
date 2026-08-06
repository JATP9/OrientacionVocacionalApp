package com.usbbog.orientacionvocacional.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usbbog.orientacionvocacional.ui.components.UsbFooterLogo
import com.usbbog.orientacionvocacional.ui.components.UsbHeaderLogo

private val LandingOrange = Color(0xFFEF7D00)
private val LandingBlue = Color(0xFF181E7B)
private val LandingBlack = Color(0xFF1C1C1C)
private val LandingWhite = Color(0xFFFFFFFF)
private val LandingSurface = Color(0xFFF1F1F2)

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
    modifier: Modifier = Modifier,
) {
    val steps = listOf(
        LandingStepItem(
            number = 1,
            title = "Registro",
            description =
                "Crea tu cuenta proporcionando información básica " +
                        "para personalizar tu experiencia",
        ),
        LandingStepItem(
            number = 2,
            title = "Prueba Vocacional",
            description =
                "Responde 180 preguntas diseñadas para evaluar tus " +
                        "intereses, habilidades y preferencias " +
                        "(estimado de duración 40 minutos).",
        ),
        LandingStepItem(
            number = 3,
            title = "Resultados",
            description =
                "Obtén un análisis y opciones de carreras recomendadas " +
                        "de acuerdo con las respuestas brindadas.",
        ),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LandingWhite),
    ) {
        LandingTopBar(
            onLoginClick = onLoginClick,
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            item {
                LandingHero(
                    onStartClick = onStartClick,
                )
            }

            item {
                LandingStepsSection(
                    steps = steps,
                )
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
    onLoginClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .heightIn(min = 68.dp)
            .shadow(elevation = 4.dp)
            .background(LandingWhite)
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            UsbHeaderLogo(
                modifier = Modifier
                    .width(143.dp)
                    .height(44.dp),
            )
        }

        Spacer(
            modifier = Modifier.width(6.dp),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .height(42.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(LandingOrange)
                    .clickable(
                        onClick = onLoginClick,
                    )
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
            .padding(
                horizontal = 22.dp,
                vertical = 42.dp,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 720.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text =
                    "Descubre tu camino académico y profesional " +
                            "con mayor claridad",
                color = LandingWhite,
                fontSize = 32.sp,
                lineHeight = 42.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(
                modifier = Modifier.height(24.dp),
            )

            Text(
                text =
                    "La plataforma de orientación vocacional de la " +
                            "Universidad de San Buenaventura sede de Bogotá " +
                            "te ayuda a identificar intereses, habilidades " +
                            "y afinidades profesionales mediante pruebas, " +
                            "resultados guiados y recomendaciones académicas " +
                            "personalizadas.",
                color = LandingWhite,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(
                modifier = Modifier.height(32.dp),
            )

            OutlinedButton(
                onClick = onStartClick,
                modifier = Modifier
                    .width(250.dp)
                    .heightIn(min = 64.dp),
                shape = RoundedCornerShape(40.dp),
                border = BorderStroke(
                    width = 4.dp,
                    color = LandingWhite,
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = LandingWhite,
                ),
                contentPadding = PaddingValues(
                    horizontal = 20.dp,
                    vertical = 10.dp,
                ),
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
            .padding(
                horizontal = 18.dp,
                vertical = 40.dp,
            ),
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

        Spacer(
            modifier = Modifier.height(30.dp),
        )

        steps.forEachIndexed { index, step ->
            LandingStepCard(
                step = step,
            )

            if (index < steps.lastIndex) {
                Spacer(
                    modifier = Modifier.height(24.dp),
                )
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
        colors = CardDefaults.cardColors(
            containerColor = LandingWhite,
        ),
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

            Spacer(
                modifier = Modifier.height(18.dp),
            )

            Text(
                text = step.title,
                color = LandingBlack,
                fontSize = 27.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(
                modifier = Modifier.height(12.dp),
            )

            Text(
                text = highlightedDescription(
                    description = step.description,
                ),
                color = LandingBlack,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            )
        }
    }
}

private fun highlightedDescription(
    description: String,
) = buildAnnotatedString {
    val highlightedParts = listOf(
        "180",
        "(estimado de duración 40 minutos).",
    )

    var currentIndex = 0

    highlightedParts.forEach { highlightedPart ->
        val startIndex = description.indexOf(
            string = highlightedPart,
            startIndex = currentIndex,
        )

        if (startIndex >= 0) {
            append(
                description.substring(
                    startIndex = currentIndex,
                    endIndex = startIndex,
                ),
            )

            withStyle(
                style = SpanStyle(
                    color = LandingOrange,
                    fontWeight = FontWeight.Bold,
                ),
            ) {
                append(highlightedPart)
            }

            currentIndex =
                startIndex + highlightedPart.length
        }
    }

    if (currentIndex < description.length) {
        append(
            description.substring(currentIndex),
        )
    }
}

@Composable
private fun LandingAboutSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LandingWhite)
            .padding(
                horizontal = 22.dp,
                vertical = 58.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text =
                "Universidad de San Buenaventura " +
                        "sede de Bogotá",
            color = LandingBlack,
            fontSize = 28.sp,
            lineHeight = 38.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier.height(26.dp),
        )

        Text(
            text =
                "Institución comprometida con la formación integral " +
                        "de profesionales competentes, éticos y con sentido " +
                        "social. Nuestra prueba vocacional está respaldada " +
                        "por metodologías científicas validadas que te " +
                        "ayudarán a tener una primera guía para tu decisión " +
                        "de vida académica.",
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LandingBlue)
            .navigationBarsPadding()
            .padding(
                horizontal = 20.dp,
                vertical = 18.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UsbFooterLogo(
            modifier = Modifier
                .width(230.dp)
                .height(46.dp),
        )

        Spacer(
            modifier = Modifier.height(6.dp),
        )

        Text(
            text =
                "Somos una institución educativa de la Comunidad " +
                        "Franciscana Provincia de la Santa Fe de educación " +
                        "superior con personería jurídica reconocida por el " +
                        "Ministerio de Educación en Resolución 1326 del 25 " +
                        "de marzo de 1975.\n" +
                        "Copyright © 2026 Universidad de San Buenaventura, " +
                        "Sede Bogotá | Políticas de uso y privacidad | " +
                        "Términos y Condiciones\n" +
                        "Institución de educación superior sujeta a la " +
                        "inspección y vigilancia del Ministerio de " +
                        "Educación Nacional",
            color = LandingWhite,
            fontSize = 10.5.sp,
            lineHeight = 15.sp,
            textAlign = TextAlign.Center,
        )
    }
}