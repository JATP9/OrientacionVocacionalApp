package com.usbbog.orientacionvocacional.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usbbog.orientacionvocacional.ui.mobile.QuestionUi

private val SessionOrange = Color(0xFFEF7D00)
private val SessionBlue = Color(0xFF181E7B)
private val SessionBlack = Color(0xFF1D1D1B)
private val SessionWhite = Color(0xFFFFFFFF)
private val SessionSurface = Color(0xFFF3F3F5)
private val SessionMuted = Color(0xFF65656A)
private val SessionBorder = Color(0xFFDEDEE3)
private val SessionOrangeSoft = Color(0xFFFFF0DF)

/**
 * Adaptacion movil de TestQuestionPage.
 *
 * La pantalla no modifica el ViewModel directamente. Recibe el estado actual y
 * comunica cada accion mediante callbacks para conservar una unica fuente de verdad.
 */
@Composable
fun TestQuestionWebScreen(
    question: QuestionUi,
    questionIndex: Int,
    totalQuestions: Int,
    selectedOptionId: String?,
    remainingTime: String,
    answeredQuestionNumbers: Set<Int>,
    errorMessage: String?,
    onSelectOption: (String) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onReviewClick: () -> Unit,
    onQuestionJump: (Int) -> Unit,
    onExitTest: () -> Unit,
    audienceLabel: String = "Usuario interno",
    versionLabel: String = "Versión v1.1",
    attemptLabel: String = "Intento actual",
    modifier: Modifier = Modifier,
) {
    var introOpen by rememberSaveable { mutableStateOf(true) }
    var finishOpen by rememberSaveable { mutableStateOf(false) }
    var exitOpen by rememberSaveable { mutableStateOf(false) }

    val safeTotal = totalQuestions.coerceAtLeast(1)
    val progress = ((questionIndex + 1f) / safeTotal).coerceIn(0f, 1f)
    val progressPercent = (progress * 100).toInt()
    val isLastQuestion = questionIndex == totalQuestions - 1

    BackHandler {
        exitOpen = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SessionSurface),
    ) {
        SessionHeader(
            audienceLabel = audienceLabel,
            versionLabel = versionLabel,
            attemptLabel = attemptLabel,
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = 14.dp,
                top = 16.dp,
                end = 14.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                SessionStatusCard(remainingTime = remainingTime)
            }

            item {
                SessionProgress(progress = progress)
            }

            item {
                QuestionCard(
                    question = question,
                    questionIndex = questionIndex,
                    totalQuestions = totalQuestions,
                    progressPercent = progressPercent,
                    selectedOptionId = selectedOptionId,
                    errorMessage = errorMessage,
                    onSelectOption = onSelectOption,
                    onPreviousClick = onPreviousClick,
                    onNextClick = {
                        if (isLastQuestion) {
                            finishOpen = true
                        } else {
                            onNextClick()
                        }
                    },
                    isLastQuestion = isLastQuestion,
                )
            }

            item {
                QuestionPagination(
                    totalQuestions = totalQuestions,
                    currentQuestionIndex = questionIndex,
                    answeredQuestionNumbers = answeredQuestionNumbers,
                    onQuestionJump = onQuestionJump,
                    onReviewClick = onReviewClick,
                )
            }
        }

        SessionFooter()
    }

    if (introOpen) {
        AlertDialog(
            onDismissRequest = { introOpen = false },
            title = {
                Text(
                    text = "Antes de iniciar la prueba",
                    color = SessionBlack,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "Lee con atención cada pregunta y responde con sinceridad. No hay respuestas correctas o incorrectas: el objetivo es identificar tus afinidades de manera clara.",
                    color = SessionMuted,
                    lineHeight = 21.sp,
                )
            },
            confirmButton = {
                Button(
                    onClick = { introOpen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SessionOrange),
                ) {
                    Text("Comenzar")
                }
            },
        )
    }

    if (finishOpen) {
        AlertDialog(
            onDismissRequest = { finishOpen = false },
            title = {
                Text(
                    text = "Revisa tus respuestas",
                    color = SessionBlack,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "Antes de calcular los resultados podrás comprobar cuáles preguntas están respondidas y editar cualquier respuesta.",
                    color = SessionMuted,
                    lineHeight = 21.sp,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        finishOpen = false
                        onReviewClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SessionBlue),
                ) {
                    Text("Revisar respuestas")
                }
            },
            dismissButton = {
                TextButton(onClick = { finishOpen = false }) {
                    Text("Seguir respondiendo", color = SessionBlue)
                }
            },
        )
    }

    if (exitOpen) {
        AlertDialog(
            onDismissRequest = { exitOpen = false },
            title = {
                Text(
                    text = "¿Salir de la prueba?",
                    color = SessionBlack,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "Si sales, deberás iniciar nuevamente y se perderán las respuestas de esta sesión.",
                    color = SessionMuted,
                    lineHeight = 21.sp,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        exitOpen = false
                        onExitTest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9F2D20)),
                ) {
                    Text("Salir y reiniciar")
                }
            },
            dismissButton = {
                TextButton(onClick = { exitOpen = false }) {
                    Text("Continuar prueba", color = SessionBlue)
                }
            },
        )
    }
}

@Composable
private fun SessionHeader(
    audienceLabel: String,
    versionLabel: String,
    attemptLabel: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SessionWhite)
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SessionPill(
                text = "Tamizaje vocacional",
                modifier = Modifier.weight(1f),
            )
            SessionPill(
                text = audienceLabel,
                soft = true,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SessionPill(
                text = versionLabel,
                outlined = true,
                modifier = Modifier.weight(1f),
            )
            SessionPill(
                text = attemptLabel,
                outlined = true,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SessionPill(
    text: String,
    modifier: Modifier = Modifier,
    soft: Boolean = false,
    outlined: Boolean = false,
) {
    val background = when {
        soft -> SessionOrangeSoft
        outlined -> SessionWhite
        else -> SessionBlue
    }
    val foreground = when {
        soft -> SessionOrange
        outlined -> SessionBlue
        else -> SessionWhite
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(background)
            .then(
                if (outlined) {
                    Modifier.border(1.dp, SessionBorder, RoundedCornerShape(50.dp))
                } else {
                    Modifier
                },
            )
            .padding(horizontal = 11.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = foreground,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SessionStatusCard(remainingTime: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SessionWhite),
        border = BorderStroke(1.dp, SessionBorder),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column {
                Text(
                    text = "Tiempo restante",
                    color = SessionMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = remainingTime.ifBlank { "--:--" },
                    color = SessionBlue,
                    fontSize = 26.sp,
                    lineHeight = 31.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SessionBorder),
            )

            Column {
                Text(
                    text = "Continuidad",
                    color = SessionMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Si sales, deberás iniciar nuevamente",
                    color = SessionBlack,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun SessionProgress(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(9.dp)
            .clip(CircleShape)
            .background(Color(0xFFE1E1E5)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(9.dp)
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        listOf(SessionOrange, Color(0xFFF5A243), SessionBlue),
                    ),
                ),
        )
    }
}

@Composable
private fun QuestionCard(
    question: QuestionUi,
    questionIndex: Int,
    totalQuestions: Int,
    progressPercent: Int,
    selectedOptionId: String?,
    errorMessage: String?,
    onSelectOption: (String) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    isLastQuestion: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = SessionWhite),
        border = BorderStroke(1.dp, SessionBorder),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Pregunta ${questionIndex + 1} de $totalQuestions | $progressPercent%",
                color = SessionOrange,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(13.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SessionBorder),
            )

            Spacer(Modifier.height(18.dp))

            Text(
                text = question.statement,
                color = SessionBlack,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 23.sp,
                    lineHeight = 31.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )

            if (question.helperText.isNotBlank()) {
                Spacer(Modifier.height(9.dp))
                Text(
                    text = question.helperText,
                    color = SessionMuted,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            }

            Spacer(Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                question.options.forEach { option ->
                    val selected = selectedOptionId == option.id

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectOption(option.id) },
                        shape = RoundedCornerShape(17.dp),
                        color = if (selected) SessionOrangeSoft else SessionWhite,
                        border = BorderStroke(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) SessionOrange else SessionBorder,
                        ),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (selected) SessionOrange else SessionWhite)
                                    .border(
                                        width = 2.dp,
                                        color = if (selected) SessionOrange else Color(0xFFB8B8BE),
                                        shape = RoundedCornerShape(6.dp),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (selected) {
                                    Text(
                                        text = "✓",
                                        color = SessionWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.title,
                                    color = SessionBlack,
                                    fontSize = 15.sp,
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                if (option.description.isNotBlank()) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = option.description,
                                        color = SessionMuted,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = errorMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFEBE8))
                        .border(1.dp, Color(0xFFE4A39B), RoundedCornerShape(14.dp))
                        .padding(13.dp),
                    color = Color(0xFF922C23),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = onPreviousClick,
                    modifier = Modifier.weight(1f),
                    enabled = questionIndex > 0,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SessionBlue),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SessionBlue),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 13.dp),
                ) {
                    Text("Anterior", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNextClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SessionBlue),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 13.dp),
                ) {
                    Text(
                        text = if (isLastQuestion) "Finalizar" else "Siguiente",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionPagination(
    totalQuestions: Int,
    currentQuestionIndex: Int,
    answeredQuestionNumbers: Set<Int>,
    onQuestionJump: (Int) -> Unit,
    onReviewClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SessionWhite),
        border = BorderStroke(1.dp, SessionBorder),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Navegación de preguntas",
                color = SessionBlack,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 2.dp),
            ) {
                items(
                    count = totalQuestions,
                    key = { index -> index },
                ) { index ->
                    val isCurrent = index == currentQuestionIndex
                    val isAnswered = index + 1 in answeredQuestionNumbers
                    val background = when {
                        isCurrent -> SessionBlue
                        isAnswered -> SessionOrangeSoft
                        else -> SessionWhite
                    }
                    val foreground = when {
                        isCurrent -> SessionWhite
                        isAnswered -> SessionOrange
                        else -> SessionBlack
                    }

                    Box(
                        modifier = Modifier
                            .size(43.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(background)
                            .border(
                                width = 1.dp,
                                color = if (isCurrent) SessionBlue else SessionBorder,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .clickable { onQuestionJump(index) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            color = foreground,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                item(key = "review") {
                    Box(
                        modifier = Modifier
                            .size(43.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SessionBlack)
                            .clickable(onClick = onReviewClick),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "✓",
                            color = SessionWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Toca el último botón para revisar todas tus respuestas.",
                color = SessionMuted,
                fontSize = 11.sp,
                lineHeight = 16.sp,
            )
        }
    }
}

@Composable
private fun SessionFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SessionBlue)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "UNIVERSIDAD DE SAN BUENAVENTURA - BOGOTÁ",
            color = SessionWhite,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center,
        )
    }
}