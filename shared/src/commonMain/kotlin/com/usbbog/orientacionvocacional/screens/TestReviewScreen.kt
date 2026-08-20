package com.usbbog.orientacionvocacional.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.usbbog.orientacionvocacional.ui.components.UsbAppFooter
import com.usbbog.orientacionvocacional.ui.components.UsbHeaderLogo
import com.usbbog.orientacionvocacional.ui.mobile.QuestionUi
import com.usbbog.orientacionvocacional.platform.PlatformBackHandler

private val ReviewOrange = Color(0xFFEF7D00)
private val ReviewBlue = Color(0xFF181E7B)
private val ReviewBlack = Color(0xFF1D1D1B)
private val ReviewWhite = Color(0xFFFFFFFF)
private val ReviewSurface = Color(0xFFF3F3F5)
private val ReviewMuted = Color(0xFF65656A)
private val ReviewBorder = Color(0xFFDEDEE3)
private val ReviewSuccess = Color(0xFF147A50)
private val ReviewSuccessSoft = Color(0xFFE9F6F0)
private val ReviewDanger = Color(0xFF9F2D20)
private val ReviewDangerSoft = Color(0xFFFFEBE8)

/** Adaptacion movil de TestReviewPage. */
@Composable
fun TestReviewWebScreen(
    questions: List<QuestionUi>,
    answeredQuestionNumbers: Set<Int>,
    isSubmitting: Boolean,
    errorMessage: String?,
    onEditQuestion: (Int) -> Unit,
    onSubmitClick: (Int?) -> Unit,
    onBackToQuestionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var satisfactionDialogOpen by rememberSaveable { mutableStateOf(false) }
    var selectedSatisfaction by rememberSaveable { mutableStateOf<Int?>(null) }

    val answeredCount = questions.indices.count { index ->
        index + 1 in answeredQuestionNumbers
    }
    val unansweredCount = (questions.size - answeredCount).coerceAtLeast(0)
    val isComplete = questions.isNotEmpty() && unansweredCount == 0

    PlatformBackHandler(enabled = !isSubmitting && !satisfactionDialogOpen) {
        onBackToQuestionsClick()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ReviewSurface),
    ) {
        ReviewHeader()

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = 14.dp,
                top = 18.dp,
                end = 14.dp,
                bottom = 26.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Column {
                    Text(
                        text = "REVISIÓN FINAL",
                        color = ReviewOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                    )
                    Spacer(Modifier.height(7.dp))
                    Text(
                        text = "Verifica tus respuestas antes de enviar",
                        color = ReviewBlack,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 28.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
            }

            item {
                CompletionSummaryCard(
                    answeredCount = answeredCount,
                    totalQuestions = questions.size,
                    unansweredCount = unansweredCount,
                )
            }

            item {
                Text(
                    text = "Detalle de preguntas",
                    color = ReviewBlack,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            itemsIndexed(
                items = questions,
                key = { index, _ -> index },
            ) { index, question ->
                val answered = index + 1 in answeredQuestionNumbers
                ReviewQuestionItem(
                    questionNumber = index + 1,
                    statement = question.statement,
                    answered = answered,
                    enabled = !isSubmitting,
                    onEditClick = { onEditQuestion(index) },
                )
            }

            item {
                ConfirmationCard(
                    isComplete = isComplete,
                    unansweredCount = unansweredCount,
                    isSubmitting = isSubmitting,
                    errorMessage = errorMessage,
                    onSubmitClick = {
                        if (isComplete) {
                            satisfactionDialogOpen = true
                        } else {
                            onSubmitClick(null)
                        }
                    },
                    onBackToQuestionsClick = onBackToQuestionsClick,
                )
            }
        }

        UsbAppFooter()
    }

    if (satisfactionDialogOpen) {
        SatisfactionDialog(
            totalQuestions = questions.size,
            satisfaction = selectedSatisfaction,
            isSubmitting = isSubmitting,
            errorMessage = errorMessage,
            onSatisfactionChange = { selectedSatisfaction = it },
            onSubmitClick = {
                selectedSatisfaction?.let { satisfaction ->
                    onSubmitClick(satisfaction)
                }
            },
            onContinueReviewClick = {
                if (!isSubmitting) satisfactionDialogOpen = false
            },
        )
    }
}

@Composable
private fun SatisfactionDialog(
    totalQuestions: Int,
    satisfaction: Int?,
    isSubmitting: Boolean,
    errorMessage: String?,
    onSatisfactionChange: (Int) -> Unit,
    onSubmitClick: () -> Unit,
    onContinueReviewClick: () -> Unit,
) {
    Dialog(
        onDismissRequest = {
            if (!isSubmitting) onContinueReviewClick()
        },
        properties = DialogProperties(
            dismissOnBackPress = !isSubmitting,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 360.dp),
            shape = RoundedCornerShape(18.dp),
            color = ReviewWhite,
            shadowElevation = 18.dp,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .background(ReviewOrange),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(ReviewWhite),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "!",
                            color = ReviewOrange,
                            fontSize = 30.sp,
                            lineHeight = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 22.dp,
                            top = 20.dp,
                            end = 22.dp,
                            bottom = 20.dp,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "¿Enviar tu prueba vocacional?",
                        color = ReviewBlack,
                        fontSize = 20.sp,
                        lineHeight = 25.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(11.dp))

                    Text(
                        text = "Has respondido las $totalQuestions preguntas. Al confirmar, " +
                                "la prueba se enviará y ya no podrás modificar tus respuestas.",
                        color = ReviewMuted,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "¿Cómo fue tu experiencia con la prueba?",
                        color = ReviewBlack,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        (1..5).forEach { value ->
                            val selected = satisfaction != null && value <= satisfaction

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .clickable(
                                        enabled = !isSubmitting,
                                        onClick = { onSatisfactionChange(value) },
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "★",
                                    color = if (selected) ReviewOrange else Color(0xFF9A9A9E),
                                    fontSize = 32.sp,
                                    lineHeight = 34.sp,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                    Text(
                        text = satisfaction?.let(::satisfactionLabel)
                            ?: "Selecciona una calificación del 1 al 5.",
                        color = if (satisfaction == null) ReviewMuted else ReviewOrange,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp,
                        fontWeight = if (satisfaction == null) {
                            FontWeight.Normal
                        } else {
                            FontWeight.Bold
                        },
                        textAlign = TextAlign.Center,
                    )

                    if (!errorMessage.isNullOrBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = errorMessage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(13.dp))
                                .background(ReviewDangerSoft)
                                .border(
                                    1.dp,
                                    ReviewDanger.copy(alpha = 0.35f),
                                    RoundedCornerShape(13.dp),
                                )
                                .padding(11.dp),
                            color = ReviewDanger,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = onSubmitClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        enabled = satisfaction != null && !isSubmitting,
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ReviewOrange,
                            contentColor = ReviewWhite,
                            disabledContainerColor = Color(0xFFB6B6B9),
                            disabledContentColor = ReviewWhite,
                        ),
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = ReviewWhite,
                                strokeWidth = 2.dp,
                            )
                            Spacer(Modifier.width(9.dp))
                        }
                        Text(
                            text = if (isSubmitting) "Enviando..." else "Sí, enviar prueba",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(Modifier.height(9.dp))

                    OutlinedButton(
                        onClick = onContinueReviewClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(50.dp),
                        border = BorderStroke(1.dp, ReviewBlue),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = ReviewBlue,
                        ),
                    ) {
                        Text(
                            text = "Seguir revisando",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

private fun satisfactionLabel(value: Int): String = when (value) {
    1 -> "1 de 5 · Muy inconforme"
    2 -> "2 de 5 · Inconforme"
    3 -> "3 de 5 · Neutral"
    4 -> "4 de 5 · Conforme"
    5 -> "5 de 5 · Muy conforme"
    else -> "Selecciona una calificación del 1 al 5."
}

@Composable
private fun ReviewHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ReviewWhite)
            .statusBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(ReviewOrange, ReviewBlue),
                    ),
                ),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            UsbHeaderLogo(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
            )
        }
    }
}

@Composable
private fun CompletionSummaryCard(
    answeredCount: Int,
    totalQuestions: Int,
    unansweredCount: Int,
) {
    val complete = unansweredCount == 0 && totalQuestions > 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ReviewWhite),
        border = BorderStroke(1.dp, ReviewBorder),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "CHECKLIST",
                color = ReviewOrange,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Resumen de completitud",
                color = ReviewBlack,
                fontSize = 21.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SummaryMetric(
                    value = answeredCount.toString(),
                    label = "Respondidas",
                    color = ReviewSuccess,
                    modifier = Modifier.weight(1f),
                )
                SummaryMetric(
                    value = unansweredCount.toString(),
                    label = "Pendientes",
                    color = if (complete) ReviewSuccess else ReviewDanger,
                    modifier = Modifier.weight(1f),
                )
                SummaryMetric(
                    value = totalQuestions.toString(),
                    label = "Total",
                    color = ReviewBlue,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(15.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(if (complete) ReviewSuccessSoft else ReviewDangerSoft)
                    .border(
                        width = 1.dp,
                        color = if (complete) ReviewSuccess.copy(alpha = 0.35f) else ReviewDanger.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(15.dp),
                    )
                    .padding(14.dp),
            ) {
                Text(
                    text = "$answeredCount respuestas registradas",
                    color = if (complete) ReviewSuccess else ReviewDanger,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (complete) {
                        "Todo listo para finalizar."
                    } else {
                        "Aún faltan $unansweredCount respuestas."
                    },
                    color = ReviewBlack,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

@Composable
private fun SummaryMetric(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            color = color,
            fontSize = 21.sp,
            lineHeight = 25.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            color = ReviewMuted,
            fontSize = 9.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ReviewQuestionItem(
    questionNumber: Int,
    statement: String,
    answered: Boolean,
    enabled: Boolean,
    onEditClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = ReviewWhite),
        border = BorderStroke(1.dp, ReviewBorder),
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(if (answered) ReviewSuccessSoft else ReviewDangerSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = questionNumber.toString(),
                        color = if (answered) ReviewSuccess else ReviewDanger,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.size(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pregunta $questionNumber",
                        color = ReviewBlack,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = statement,
                        color = ReviewMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusPill(answered = answered)

                OutlinedButton(
                    onClick = onEditClick,
                    enabled = enabled,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ReviewBlue),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ReviewBlue),
                    contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = "Editar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPill(answered: Boolean) {
    Text(
        text = if (answered) "Respondida" else "Pendiente",
        modifier = Modifier
            .clip(CircleShape)
            .background(if (answered) ReviewSuccessSoft else ReviewDangerSoft)
            .padding(horizontal = 11.dp, vertical = 7.dp),
        color = if (answered) ReviewSuccess else ReviewDanger,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun ConfirmationCard(
    isComplete: Boolean,
    unansweredCount: Int,
    isSubmitting: Boolean,
    errorMessage: String?,
    onSubmitClick: () -> Unit,
    onBackToQuestionsClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ReviewBlack),
    ) {
        Column(modifier = Modifier.padding(19.dp)) {
            Text(
                text = "ENVIO",
                color = ReviewOrange,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Confirmación de envío",
                color = ReviewWhite,
                fontSize = 21.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(14.dp))

            ReviewBullet("Tus respuestas se usarán para calcular el perfil vocacional.")
            ReviewBullet("La siguiente pantalla mostrará áreas y carreras sugeridas.")
            ReviewBullet("Puedes volver y ajustar cualquier respuesta antes de enviar.")

            if (!isComplete) {
                Spacer(Modifier.height(13.dp))
                Text(
                    text = "Completa las $unansweredCount preguntas pendientes antes de enviar. " +
                            "Puedes ir directamente a la primera.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ReviewDanger.copy(alpha = 0.22f))
                        .border(
                            1.dp,
                            ReviewDanger.copy(alpha = 0.65f),
                            RoundedCornerShape(14.dp),
                        )
                        .padding(13.dp),
                    color = Color(0xFFFFD6D1),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(Modifier.height(13.dp))
                Text(
                    text = errorMessage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ReviewDanger.copy(alpha = 0.22f))
                        .padding(13.dp),
                    color = Color(0xFFFFD6D1),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onSubmitClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ReviewOrange,
                    disabledContainerColor = Color(0xFF5A5A5D),
                    disabledContentColor = ReviewWhite.copy(alpha = 0.55f),
                ),
                contentPadding = PaddingValues(vertical = 14.dp),
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = ReviewWhite,
                        strokeWidth = 2.dp,
                    )
                    Spacer(Modifier.size(9.dp))
                }
                Text(
                    text = when {
                        isSubmitting -> "Enviando..."
                        isComplete -> "Finalizar prueba"
                        else -> "Ir a la primera pendiente"
                    },
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onBackToQuestionsClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, ReviewWhite.copy(alpha = 0.55f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ReviewWhite),
                contentPadding = PaddingValues(vertical = 13.dp),
            ) {
                Text("Volver a preguntas", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ReviewBullet(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(7.dp)
                .clip(CircleShape)
                .background(ReviewOrange),
        )
        Spacer(Modifier.size(10.dp))
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = ReviewWhite.copy(alpha = 0.84f),
            fontSize = 13.sp,
            lineHeight = 19.sp,
        )
    }
}
