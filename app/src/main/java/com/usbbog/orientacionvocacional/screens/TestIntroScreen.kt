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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val IntroOrange = Color(0xFFEF7D00)
private val IntroBlue = Color(0xFF181E7B)
private val IntroBlack = Color(0xFF1C1C1C)
private val IntroWhite = Color(0xFFFFFFFF)
private val IntroSurface = Color(0xFFF5F5F6)
private val IntroMuted = Color(0xFF66666B)
private val IntroBorder = Color(0xFFE0E0E3)

private data class TestGuideCard(
    val step: String,
    val title: String,
    val description: String,
    val detail: String,
)

/**
 * Adaptación móvil de TestIntroPage.
 *
 * Esta pantalla es stateless respecto a la sesión de la prueba. AppNavigation
 * conserva la responsabilidad de preparar el TestViewModel y navegar a TEST.
 */
@Composable
fun TestIntroWebScreenV2(
    userName: String,
    errorMessage: String?,
    onStartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onHelpClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var startRequested by rememberSaveable { mutableStateOf(false) }

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
            onHelpClick = onHelpClick,
            onProfileClick = onProfileClick,
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(IntroSurface),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 20.dp,
                end = 16.dp,
                bottom = 28.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                TestIntroCopyCard(
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

            item {
                TestGuideCarousel()
            }
        }

        TestIntroFooter()
    }
}

@Composable
private fun TestIntroCopyCard(
    isLoading: Boolean,
    errorMessage: String?,
    onStartClick: () -> Unit,
) {
    val shape = RoundedCornerShape(28.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 720.dp)
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f),
            ),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = IntroWhite),
        border = BorderStroke(1.dp, IntroBorder),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(IntroOrange, IntroBlue),
                    ),
                ),
        )

        Column(
            modifier = Modifier.padding(
                start = 22.dp,
                top = 22.dp,
                end = 22.dp,
                bottom = 24.dp,
            ),
        ) {
            Text(
                text = "ORIENTACIÓN VOCACIONAL DIGITAL",
                color = IntroOrange,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Prepárate antes de comenzar la prueba",
                color = IntroBlack,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 29.sp,
                    lineHeight = 37.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Esta vista reúne el contexto previo, la forma de responder y los pasos que verás antes, durante y después del test vocacional.",
                color = IntroMuted,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.5.sp,
                    lineHeight = 23.sp,
                ),
            )

            Spacer(Modifier.height(20.dp))

            IntroRecommendation(
                number = 1,
                text = "Busca un espacio tranquilo y responde pensando en tus gustos reales.",
            )
            IntroRecommendation(
                number = 2,
                text = "Las tarjetas se recorren dentro de esta misma página, como en la guía.",
            )
            IntroRecommendation(
                number = 3,
                text = "El tiempo estimado es de 35 minutos y podrás revisar el progreso en sesión.",
            )

            if (!errorMessage.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))
                IntroErrorAlert(message = errorMessage)
            }

            Spacer(Modifier.height(22.dp))

            IntroGradientButton(
                text = if (isLoading) "Preparando prueba..." else "Iniciar prueba",
                enabled = !isLoading,
                loading = isLoading,
                onClick = onStartClick,
            )
        }
    }
}

@Composable
private fun IntroRecommendation(
    number: Int,
    text: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(IntroOrange.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number.toString(),
                color = IntroOrange,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.width(11.dp))

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = IntroBlack,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.5.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

@Composable
private fun IntroErrorAlert(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFF1F0))
            .border(
                width = 1.dp,
                color = Color(0xFFE9A6A1),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(14.dp),
    ) {
        Text(
            text = "No fue posible iniciar la prueba.",
            color = Color(0xFF9E2D27),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = message,
            color = Color(0xFF7E3732),
            fontSize = 13.sp,
            lineHeight = 19.sp,
        )
    }
}

@Composable
private fun IntroGradientButton(
    text: String,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (enabled) {
                    Brush.horizontalGradient(listOf(IntroOrange, IntroBlue))
                } else {
                    Brush.horizontalGradient(
                        listOf(
                            IntroOrange.copy(alpha = 0.58f),
                            IntroBlue.copy(alpha = 0.58f),
                        ),
                    )
                },
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
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
                Spacer(Modifier.width(10.dp))
            }

            Text(
                text = text,
                color = IntroWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun TestGuideCarousel() {
    val cards = listOf(
        TestGuideCard(
            step = "ANTES",
            title = "Prepara tu espacio",
            description = "Reserva cerca de 35 minutos y evita interrupciones mientras realizas la prueba.",
            detail = "No necesitas estudiar: piensa en lo que realmente disfrutas, te interesa y prefieres hacer.",
        ),
        TestGuideCard(
            step = "DURANTE",
            title = "Responde con sinceridad",
            description = "Lee cada enunciado con calma y elige la alternativa que mejor te represente.",
            detail = "La sesión mostrará tu avance y te permitirá identificar las preguntas que ya respondiste.",
        ),
        TestGuideCard(
            step = "DESPUÉS",
            title = "Revisa tus resultados",
            description = "Al finalizar verás las áreas de mayor afinidad y las carreras relacionadas.",
            detail = "El resultado es una guía para apoyar tu decisión académica, no una elección obligatoria.",
        ),
    )

    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val selectedCard = cards[selectedIndex]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(IntroBlue)
            .padding(20.dp),
    ) {
        Text(
            text = "GUÍA DE LA PRUEBA",
            color = IntroOrange,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Antes, durante y después",
            color = IntroWhite,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 23.sp,
                lineHeight = 29.sp,
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = IntroWhite),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(IntroOrange.copy(alpha = 0.12f))
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                    ) {
                        Text(
                            text = selectedCard.step,
                            color = IntroOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.7.sp,
                        )
                    }

                    Text(
                        text = "${selectedIndex + 1} / ${cards.size}",
                        color = IntroMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    text = selectedCard.title,
                    color = IntroBlack,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = selectedCard.description,
                    color = IntroMuted,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                )

                Spacer(Modifier.height(14.dp))

                HorizontalDivider(color = IntroBorder)

                Spacer(Modifier.height(14.dp))

                Text(
                    text = selectedCard.detail,
                    color = IntroBlack,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CarouselArrow(
                label = "‹",
                enabled = selectedIndex > 0,
                onClick = { selectedIndex -= 1 },
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                cards.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == selectedIndex) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == selectedIndex) IntroOrange
                                else IntroWhite.copy(alpha = 0.38f),
                            )
                            .clickable { selectedIndex = index },
                    )
                }
            }

            CarouselArrow(
                label = "›",
                enabled = selectedIndex < cards.lastIndex,
                onClick = { selectedIndex += 1 },
            )
        }
    }
}

@Composable
private fun CarouselArrow(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
                if (enabled) IntroWhite
                else IntroWhite.copy(alpha = 0.18f),
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (enabled) IntroBlue else IntroWhite.copy(alpha = 0.44f),
            fontSize = 31.sp,
            lineHeight = 31.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun TestIntroTopBar(
    userName: String,
    onHelpClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .heightIn(min = 68.dp)
            .shadow(4.dp)
            .background(IntroWhite)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IntroInstitutionMark(
            darkText = true,
            modifier = Modifier.weight(1f),
        )

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .border(1.dp, Color(0xFFBEBEBE), CircleShape)
                .clickable(onClick = onHelpClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "?",
                color = IntroMuted,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.width(7.dp))

        Box(
            modifier = Modifier
                .height(42.dp)
                .widthIn(min = 82.dp, max = 118.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(IntroOrange)
                .clickable(onClick = onProfileClick)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = userName.substringBefore(" ").ifBlank { "Usuario" },
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IntroInstitutionMark(darkText = false)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Copyright © 2026 Universidad de San Buenaventura, Sede Bogotá | Políticas de uso y tratamiento de datos",
            color = IntroWhite.copy(alpha = 0.78f),
            fontSize = 9.sp,
            lineHeight = 13.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun IntroInstitutionMark(
    darkText: Boolean,
    modifier: Modifier = Modifier,
) {
    val textColor = if (darkText) IntroBlue else IntroWhite

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(34.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = if (darkText) IntroOrange else IntroWhite,
                    radius = size.minDimension / 2f - 1.dp.toPx(),
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
            Text(
                text = "USB",
                color = if (darkText) IntroOrange else IntroWhite,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.width(8.dp))

        Column {
            Text(
                text = "UNIVERSIDAD DE",
                color = textColor,
                fontSize = 7.5.sp,
                lineHeight = 9.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.4.sp,
            )
            Text(
                text = "SAN BUENAVENTURA",
                color = textColor,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.25.sp,
            )
            Text(
                text = "BOGOTÁ",
                color = textColor.copy(alpha = 0.82f),
                fontSize = 7.sp,
                lineHeight = 9.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}