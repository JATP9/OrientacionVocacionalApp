package com.usbbog.orientacionvocacional.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usbbog.orientacionvocacional.R
import com.usbbog.orientacionvocacional.ui.components.UsbFooterLogo
import com.usbbog.orientacionvocacional.ui.components.UsbGuideImage
import com.usbbog.orientacionvocacional.ui.components.UsbHeaderLogo

private val IntroOrange = Color(0xFFEF7D00)
private val IntroBlue = Color(0xFF181E7B)
private val IntroBlack = Color(0xFF1D1D1B)
private val IntroGuide = Color(0xFF20201F)
private val IntroWhite = Color(0xFFFFFFFF)
private val IntroCreamTop = Color(0xFFFFF9F1)
private val IntroCreamBottom = Color(0xFFFFF6EC)
private val IntroMuted = Color(0xFF5B5B57)
private val IntroEyebrow = Color(0xFF4F5A72)

private data class TestGuideCard(
    val title: String,
    val description: String,
    val imageRes: Int,
)

/**
 * Pantalla previa al test vocacional.
 *
 * La sesión y el estado del test continúan siendo responsabilidad de
 * AppNavigation y TestViewModel.
 */
@Composable
fun TestIntroWebScreenV2(
    userName: String,
    errorMessage: String?,
    onStartClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var startRequested by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            startRequested = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IntroWhite),
    ) {
        TestIntroTopBar(
            userName = userName.ifBlank { "Usuario" },
            onProfileClick = onProfileClick,
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(IntroWhite),
            contentPadding = PaddingValues(top = 20.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    TestIntroShell(
                        isLoading = startRequested,
                        errorMessage = errorMessage,
                        onStartClick = {
                            if (!startRequested) {
                                startRequested = true
                                onStartClick()
                            }
                        },
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))
                TestIntroFooter()
            }
        }
    }
}

@Composable
private fun TestIntroShell(
    isLoading: Boolean,
    errorMessage: String?,
    onStartClick: () -> Unit,
) {
    val shellShape = RoundedCornerShape(32.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 1380.dp)
            .shadow(
                elevation = 12.dp,
                shape = shellShape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f),
            )
            .clip(shellShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        IntroCreamTop,
                        IntroCreamBottom,
                    ),
                ),
            )
            .drawBehind {
                val radius = 142.dp.toPx()

                drawCircle(
                    color = IntroOrange.copy(alpha = 0.10f),
                    radius = radius,
                    center = Offset(
                        x = size.width + radius * 0.35f,
                        y = size.height + radius * 0.28f,
                    ),
                )
            },
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
        ) introConstraints@ {
            val availableWidth =
                this@introConstraints.maxWidth

            val compactPadding =
                availableWidth < 600.dp

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = if (compactPadding) {
                            22.dp
                        } else {
                            38.dp
                        },
                        vertical = if (compactPadding) {
                            28.dp
                        } else {
                            38.dp
                        },
                    ),
            ) {
                if (availableWidth >= 840.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(28.dp),
                        verticalAlignment =
                            Alignment.CenterVertically,
                    ) {
                        TestIntroCopy(
                            isLoading = isLoading,
                            errorMessage = errorMessage,
                            onStartClick = onStartClick,
                            modifier = Modifier.weight(0.9f),
                        )

                        TestGuideCarousel(
                            modifier = Modifier
                                .weight(0.8f)
                                .widthIn(max = 440.dp),
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        TestIntroCopy(
                            isLoading = isLoading,
                            errorMessage = errorMessage,
                            onStartClick = onStartClick,
                        )

                        Spacer(
                            modifier = Modifier.height(30.dp),
                        )

                        TestGuideCarousel()
                    }
                }
            }
        }
    }
}

@Composable
private fun TestIntroCopy(
    isLoading: Boolean,
    errorMessage: String?,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = "ORIENTACIÓN VOCACIONAL DIGITAL",
            color = IntroEyebrow,
            fontSize = 12.5.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.35.sp,
        )

        Spacer(
            modifier = Modifier.height(16.dp),
        )

        Text(
            text = "Prepárate antes de comenzar la prueba",
            color = IntroBlack,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 31.sp,
                lineHeight = 38.sp,
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(
            modifier = Modifier.height(14.dp),
        )

        Text(
            text = "Esta vista reúne el contexto previo, " +
                    "la forma de responder y los pasos que verás " +
                    "antes, durante y después del test vocacional.",
            color = IntroMuted,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.5.sp,
                lineHeight = 24.sp,
            ),
        )

        Spacer(
            modifier = Modifier.height(20.dp),
        )

        IntroRecommendation(
            text = "Busca un espacio tranquilo y responde " +
                    "pensando en tus gustos reales.",
        )

        IntroRecommendation(
            text = "Las tarjetas se recorren dentro de esta " +
                    "misma página, como en la guía.",
        )

        IntroRecommendation(
            text = "El tiempo estimado es de 35 minutos y " +
                    "podrás revisar el progreso en sesión.",
        )

        if (!errorMessage.isNullOrBlank()) {
            Spacer(
                modifier = Modifier.height(16.dp),
            )

            IntroErrorAlert(
                message = errorMessage,
            )
        }

        Spacer(
            modifier = Modifier.height(22.dp),
        )

        IntroStartButton(
            text = if (isLoading) {
                "Preparando prueba..."
            } else {
                "Iniciar prueba"
            },
            enabled = !isLoading,
            loading = isLoading,
            onClick = onStartClick,
        )
    }
}

@Composable
private fun IntroRecommendation(
    text: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = "•",
            modifier = Modifier.width(20.dp),
            color = IntroMuted,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
        )

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = IntroMuted,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.5.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Normal,
            ),
        )
    }
}

@Composable
private fun IntroErrorAlert(
    message: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x149F2D20))
            .padding(
                horizontal = 18.dp,
                vertical = 16.dp,
            ),
    ) {
        Text(
            text = "No fue posible iniciar la prueba.",
            color = Color(0xFF9F2D20),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(
            modifier = Modifier.height(6.dp),
        )

        Text(
            text = message,
            color = Color(0xFF7E3732),
            fontSize = 13.sp,
            lineHeight = 19.sp,
        )
    }
}

@Composable
private fun IntroStartButton(
    text: String,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .widthIn(max = 250.dp)
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor =
                    IntroOrange.copy(alpha = 0.22f),
                spotColor =
                    IntroOrange.copy(alpha = 0.22f),
            )
            .clip(CircleShape)
            .background(
                if (enabled) {
                    IntroOrange
                } else {
                    IntroOrange.copy(alpha = 0.58f)
                },
            )
            .clickable(
                enabled = enabled,
                onClick = onClick,
            )
            .padding(
                horizontal = 22.dp,
                vertical = 15.dp,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(19.dp),
                    color = IntroWhite,
                    strokeWidth = 2.dp,
                )

                Spacer(
                    modifier = Modifier.width(10.dp),
                )
            }

            Text(
                text = text,
                color = IntroWhite,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun TestGuideCarousel(
    modifier: Modifier = Modifier,
) {
    val cards = listOf(
        TestGuideCard(
            title = "Registro",
            description = "Crea tu cuenta proporcionando " +
                    "información básica para personalizar tu experiencia.",
            imageRes = R.drawable.guide_registration,
        ),
        TestGuideCard(
            title = "Prueba vocacional",
            description = "Responderás preguntas diseñadas para " +
                    "evaluar intereses, habilidades y preferencias.",
            imageRes = R.drawable.guide_test,
        ),
        TestGuideCard(
            title = "Recomendaciones",
            description = "Contesta con sinceridad, sin estímulos " +
                    "externos y con la calma suficiente para decidir bien.",
            imageRes = R.drawable.guide_recommendations,
        ),
        TestGuideCard(
            title = "Resultados",
            description = "Obtén un análisis inicial y programas " +
                    "sugeridos de acuerdo con tus respuestas.",
            imageRes = R.drawable.guide_results,
        ),
    )

    var selectedIndex by rememberSaveable {
        mutableStateOf(0)
    }

    val selectedCard = cards[selectedIndex]

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(IntroGuide)
            .padding(22.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(IntroWhite),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (selectedIndex + 1).toString(),
                    color = IntroOrange,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(
                modifier = Modifier.width(16.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "GUÍA PASO A PASO",
                    color = IntroWhite.copy(alpha = 0.70f),
                    fontSize = 12.5.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 1.sp,
                )

                Spacer(
                    modifier = Modifier.height(4.dp),
                )

                Text(
                    text = selectedCard.title,
                    color = IntroWhite,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 27.sp,
                        lineHeight = 33.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(252.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            IntroWhite.copy(alpha = 0.07f),
                            IntroWhite.copy(alpha = 0.02f),
                        ),
                    ),
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            UsbGuideImage(
                drawableRes = selectedCard.imageRes,
                contentDescription = selectedCard.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp),
        )

        Text(
            text = selectedCard.description,
            color = IntroWhite.copy(alpha = 0.88f),
            fontSize = 16.sp,
            lineHeight = 25.sp,
        )

        Spacer(
            modifier = Modifier.height(22.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically,
        ) {
            CarouselArrow(
                label = "‹",
                onClick = {
                    selectedIndex =
                        (selectedIndex - 1 + cards.size) %
                                cards.size
                },
            )

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp),
                verticalAlignment =
                    Alignment.CenterVertically,
            ) {
                cards.indices.forEach { index ->
                    val selected =
                        index == selectedIndex

                    Box(
                        modifier = Modifier
                            .width(
                                if (selected) {
                                    26.dp
                                } else {
                                    10.dp
                                },
                            )
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) {
                                    IntroOrange
                                } else {
                                    IntroWhite.copy(
                                        alpha = 0.30f,
                                    )
                                },
                            )
                            .clickable {
                                selectedIndex = index
                            },
                    )
                }
            }

            CarouselArrow(
                label = "›",
                onClick = {
                    selectedIndex =
                        (selectedIndex + 1) % cards.size
                },
            )
        }
    }
}

@Composable
private fun CarouselArrow(
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                IntroWhite.copy(alpha = 0.08f),
            )
            .border(
                width = 1.dp,
                color = IntroWhite.copy(alpha = 0.25f),
                shape = CircleShape,
            )
            .clickable(
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = IntroWhite,
            fontSize = 31.sp,
            lineHeight = 31.sp,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Composable
private fun TestIntroTopBar(
    userName: String,
    onProfileClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .heightIn(min = 68.dp)
            .shadow(4.dp)
            .background(IntroWhite)
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp,
            ),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            UsbHeaderLogo(
                modifier = Modifier
                    .height(44.dp),
            )
        }

        Spacer(
            modifier = Modifier.width(8.dp),
        )

        Box(
            modifier = Modifier
                .height(42.dp)
                .widthIn(
                    min = 82.dp,
                    max = 118.dp,
                )
                .clip(CircleShape)
                .background(IntroOrange)
                .clickable(
                    onClick = onProfileClick,
                )
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = userName
                    .substringBefore(" ")
                    .ifBlank { "Usuario" },
                color = IntroWhite,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun TestIntroFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(IntroBlue)
            .navigationBarsPadding()
            .padding(
                horizontal = 18.dp,
                vertical = 14.dp,
            ),
        horizontalAlignment =
            Alignment.CenterHorizontally,
    ) {
        UsbFooterLogo(
            modifier = Modifier
                .width(230.dp)
                .height(46.dp),
        )

        Spacer(
            modifier = Modifier.height(7.dp),
        )

        Text(
            text = "Somos una institución educativa de la " +
                    "Comunidad Franciscana Provincia de la Santa Fe " +
                    "de educación superior con personería jurídica " +
                    "reconocida por el Ministerio de Educación en " +
                    "Resolución 1326 del 25 de marzo de 1975.",
            color = IntroWhite.copy(alpha = 0.88f),
            fontSize = 8.5.sp,
            lineHeight = 12.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier.height(3.dp),
        )

        Text(
            text = "Copyright © 2026 Universidad de San " +
                    "Buenaventura, Sede Bogotá | Políticas de uso " +
                    "y privacidad | Términos y Condiciones",
            color = IntroWhite.copy(alpha = 0.78f),
            fontSize = 8.5.sp,
            lineHeight = 12.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier.height(2.dp),
        )

        Text(
            text = "Institución de educación superior sujeta " +
                    "a la inspección y vigilancia del Ministerio " +
                    "de Educación Nacional.",
            color = IntroWhite.copy(alpha = 0.78f),
            fontSize = 8.5.sp,
            lineHeight = 12.sp,
            textAlign = TextAlign.Center,
        )
    }
}