package com.usbbog.orientacionvocacional.ui.mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.usbbog.orientacionvocacional.ui.theme.USBColors

/**
 * Las pantallas son "stateless": la lógica, validaciones, ViewModel y navegación
 * permanecen fuera y se conectan mediante parámetros y callbacks.
 */

@Composable
fun LandingMobileScreen(
    steps: List<LandingStep>,
    onStartClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            UsbBrandBar(
                actionLabel = "Ingresar",
                onHelpClick = onHelpClick,
                onUserClick = onLoginClick,
            )
        },
        containerColor = USBColors.White,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    USBColors.Orange,
                                    Color(0xFFBB661E),
                                    Color(0xFF714548),
                                    USBColors.Blue,
                                ),
                            ),
                        )
                        .padding(horizontal = 22.dp, vertical = 42.dp),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        Text(
                            text = "Descubre el camino profesional que conecta contigo",
                            style = MaterialTheme.typography.headlineLarge,
                            color = USBColors.White,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = "Realiza la prueba de orientación vocacional y conoce áreas y programas compatibles con tus intereses.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = USBColors.White.copy(alpha = 0.94f),
                            textAlign = TextAlign.Center,
                        )
                        UsbPrimaryButton(
                            text = "Comenzar prueba",
                            onClick = onStartClick,
                            modifier = Modifier.fillMaxWidth(0.88f),
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F1F2))
                        .padding(horizontal = 18.dp, vertical = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    Text(
                        text = "¿Cómo funciona?",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = USBColors.Black,
                        textAlign = TextAlign.Center,
                    )
                    steps.forEach { step ->
                        UsbCard {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .background(USBColors.Orange),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = step.number.toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = USBColors.White,
                                )
                            }
                            Spacer(Modifier.height(14.dp))
                            Text(
                                text = step.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = USBColors.Black,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = step.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = USBColors.TextMuted,
                            )
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 34.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Sobre esta herramienta",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "La prueba apoya tu proceso de decisión. Sus resultados son orientativos y pueden complementarse con acompañamiento profesional.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF223058),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
fun LoginMobileScreen(
    email: String,
    password: String,
    rememberMe: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    UsbGradientBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            UsbCard(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(USBColors.Orange, USBColors.Blue),
                            ),
                        )
                        .padding(horizontal = 20.dp, vertical = 28.dp),
                ) {
                    Column {
                        Text(
                            text = "Bienvenido",
                            style = MaterialTheme.typography.headlineLarge,
                            color = USBColors.White,
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Inicia sesión para continuar con tu proceso de orientación vocacional.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = USBColors.White.copy(alpha = 0.94f),
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                UsbTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Correo electrónico",
                    placeholder = "usuario@correo.com",
                    keyboardType = KeyboardType.Email,
                    enabled = !isLoading,
                )

                Spacer(Modifier.height(14.dp))

                UsbTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = "Contraseña",
                    placeholder = "Ingresa tu contraseña",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardType = KeyboardType.Password,
                    enabled = !isLoading,
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { onRememberChange(!rememberMe) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = onRememberChange,
                            colors = CheckboxDefaults.colors(checkedColor = USBColors.Orange),
                        )
                        Text("Recordarme", style = MaterialTheme.typography.bodyMedium)
                    }
                    TextButton(onClick = onForgotPasswordClick, enabled = !isLoading) {
                        Text("Olvidé mi contraseña", color = USBColors.Orange)
                    }
                }

                if (!errorMessage.isNullOrBlank()) {
                    Spacer(Modifier.height(10.dp))
                    UsbErrorBanner(message = errorMessage)
                }

                Spacer(Modifier.height(16.dp))

                UsbPrimaryButton(
                    text = "Iniciar sesión",
                    onClick = onLoginClick,
                    loading = isLoading,
                    enabled = email.isNotBlank() && password.isNotBlank(),
                )

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "¿No tienes una cuenta?",
                        color = USBColors.TextMuted,
                    )
                    TextButton(onClick = onRegisterClick, enabled = !isLoading) {
                        Text("Regístrate", color = USBColors.Orange)
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterMobileScreen(
    state: RegisterUiState,
    onFieldChange: (RegisterField, String) -> Unit,
    onAcceptTermsChange: (Boolean) -> Unit,
    onAuthorizeDataChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    UsbGradientBackground(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                UsbCard {
                    UsbSectionTitle(
                        eyebrow = "Crear cuenta",
                        title = "Registro",
                        description = "Completa tus datos para iniciar el proceso de orientación vocacional.",
                    )

                    Spacer(Modifier.height(20.dp))

                    UsbTextField(
                        value = state.fullName,
                        onValueChange = { onFieldChange(RegisterField.FullName, it) },
                        label = "Nombre completo",
                        enabled = !state.isLoading,
                    )
                    Spacer(Modifier.height(12.dp))
                    UsbTextField(
                        value = state.documentNumber,
                        onValueChange = { onFieldChange(RegisterField.DocumentNumber, it) },
                        label = "Número de documento",
                        keyboardType = KeyboardType.Number,
                        enabled = !state.isLoading,
                    )
                    Spacer(Modifier.height(12.dp))
                    UsbTextField(
                        value = state.email,
                        onValueChange = { onFieldChange(RegisterField.Email, it) },
                        label = "Correo electrónico",
                        keyboardType = KeyboardType.Email,
                        enabled = !state.isLoading,
                    )
                    Spacer(Modifier.height(12.dp))
                    UsbTextField(
                        value = state.phone,
                        onValueChange = { onFieldChange(RegisterField.Phone, it) },
                        label = "Teléfono",
                        keyboardType = KeyboardType.Phone,
                        enabled = !state.isLoading,
                    )
                    Spacer(Modifier.height(12.dp))
                    UsbTextField(
                        value = state.password,
                        onValueChange = { onFieldChange(RegisterField.Password, it) },
                        label = "Contraseña",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = !state.isLoading,
                    )
                    Spacer(Modifier.height(12.dp))
                    UsbTextField(
                        value = state.confirmPassword,
                        onValueChange = { onFieldChange(RegisterField.ConfirmPassword, it) },
                        label = "Confirmar contraseña",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = !state.isLoading,
                    )

                    Spacer(Modifier.height(16.dp))

                    UsbCheckboxLine(
                        checked = state.acceptTerms,
                        text = "Acepto los términos y condiciones de uso.",
                        onCheckedChange = onAcceptTermsChange,
                    )
                    Spacer(Modifier.height(10.dp))
                    UsbCheckboxLine(
                        checked = state.authorizeData,
                        text = "Autorizo el tratamiento de mis datos personales.",
                        onCheckedChange = onAuthorizeDataChange,
                    )

                    if (!state.errorMessage.isNullOrBlank()) {
                        Spacer(Modifier.height(14.dp))
                        UsbErrorBanner(message = state.errorMessage.orEmpty())
                    }

                    Spacer(Modifier.height(18.dp))

                    val formComplete = state.fullName.isNotBlank() &&
                        state.documentNumber.isNotBlank() &&
                        state.email.isNotBlank() &&
                        state.password.isNotBlank() &&
                        state.confirmPassword.isNotBlank() &&
                        state.acceptTerms &&
                        state.authorizeData

                    UsbPrimaryButton(
                        text = "Crear cuenta",
                        onClick = onRegisterClick,
                        enabled = formComplete,
                        loading = state.isLoading,
                    )
                    Spacer(Modifier.height(10.dp))
                    UsbSecondaryButton(
                        text = "Volver a iniciar sesión",
                        onClick = onBackToLoginClick,
                        enabled = !state.isLoading,
                    )
                }
            }
        }
    }
}

@Composable
fun BeforeTestMobileScreen(
    title: String,
    description: String,
    instructions: List<String>,
    warning: String,
    onStartClick: () -> Unit,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            UsbBrandBar(
                userName = "Mi perfil",
                onUserClick = onProfileClick
            )
        },

        containerColor = USBColors.Cream,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                UsbCard(containerColor = Color(0xFFFFF9F1)) {
                    UsbSectionTitle(
                        eyebrow = "Antes de comenzar",
                        title = title,
                        description = description,
                    )
                    Spacer(Modifier.height(18.dp))

                    instructions.forEachIndexed { index, instruction ->
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(USBColors.Orange),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    color = USBColors.White,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = instruction,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge,
                                color = USBColors.TextMuted,
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    UsbErrorBanner(message = warning)
                    Spacer(Modifier.height(18.dp))
                    UsbPrimaryButton(text = "Iniciar prueba", onClick = onStartClick)
                    Spacer(Modifier.height(10.dp))
                    UsbSecondaryButton(text = "Volver", onClick = onBackClick)
                }
            }

            item {
                UsbCard(containerColor = USBColors.Black) {
                    Text(
                        text = "Guía rápida",
                        style = MaterialTheme.typography.titleLarge,
                        color = USBColors.White,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Lee cada afirmación con calma, responde con sinceridad y evita elegir lo que otras personas esperan de ti.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = USBColors.White.copy(alpha = 0.86f),
                    )
                }
            }
        }
    }
}

@Composable
fun TestSessionMobileScreen(
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
    onQuestionJump: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress = if (totalQuestions <= 0) 0f else (questionIndex + 1f) / totalQuestions

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8F4EE),
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = 18.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    UsbStatusPill(
                        text = "Pregunta ${questionIndex + 1} de $totalQuestions",
                        modifier = Modifier.weight(1f),
                    )
                    UsbStatusPill(
                        text = remainingTime,
                        modifier = Modifier.weight(1f),
                        danger = false,
                    )
                }
            }

            item {
                UsbCard {
                    UsbProgress(
                        progress = progress,
                        label = "Progreso",
                        trailingText = "${(progress * 100).toInt()}%",
                    )

                    Spacer(Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(USBColors.SurfaceSoft)
                            .padding(20.dp),
                    ) {
                        Column {
                            Text(
                                text = question.statement,
                                style = MaterialTheme.typography.headlineMedium,
                                color = USBColors.Black,
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = question.helperText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = USBColors.TextMuted,
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    question.options.forEach { option ->
                        UsbAnswerOption(
                            option = option,
                            selected = selectedOptionId == option.id,
                            onClick = { onSelectOption(option.id) },
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    if (!errorMessage.isNullOrBlank()) {
                        UsbErrorBanner(
                            message = errorMessage
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        UsbSecondaryButton(
                            text = "Anterior",
                            onClick = onPreviousClick,
                            modifier = Modifier.weight(1f),
                            enabled = questionIndex > 0,
                        )
                        UsbPrimaryButton(
                            text = if (questionIndex == totalQuestions - 1) "Finalizar" else "Siguiente",
                            onClick = onNextClick,
                            modifier = Modifier.weight(1f),
                            enabled = true,
                        )
                    }
                }
            }

            item {
                UsbCard {
                    Text(
                        text = "Navegación de preguntas",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.height(14.dp))
                    QuestionNumberGrid(
                        totalQuestions = totalQuestions,
                        currentQuestion = questionIndex,
                        answeredQuestionNumbers = answeredQuestionNumbers,
                        onQuestionJump = onQuestionJump,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionNumberGrid(
    totalQuestions: Int,
    currentQuestion: Int,
    answeredQuestionNumbers: Set<Int>,
    onQuestionJump: (Int) -> Unit,
) {
    val rows = (0 until totalQuestions).chunked(5)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                rowItems.forEach { index ->
                    val isCurrent = index == currentQuestion
                    val isAnswered = (index + 1) in answeredQuestionNumbers
                    val background = when {
                        isCurrent -> USBColors.Black
                        isAnswered -> USBColors.OrangeSoft
                        else -> USBColors.White
                    }
                    val foreground = when {
                        isCurrent -> USBColors.White
                        isAnswered -> USBColors.Orange
                        else -> USBColors.Black
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(background)
                            .clickable { onQuestionJump(index) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            color = foreground,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
                repeat(5 - rowItems.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ResultsMobileScreen(
    userName: String,
    mainArea: String,
    summary: String,
    scores: List<ResultScoreUi>,
    careers: List<CareerResultUi>,
    generatedAt: String,
    onDownloadClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            UsbBrandBar(
                userName = userName,
                onUserClick = onProfileClick,
            )
        },
        containerColor = USBColors.Orange,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Text(
                    text = "Tus resultados",
                    style = MaterialTheme.typography.headlineLarge,
                    color = USBColors.White,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Este perfil resume las áreas y programas con mayor afinidad.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = USBColors.White.copy(alpha = 0.9f),
                )
            }

            item {
                UsbCard {
                    UsbStatusPill(text = "Área principal")
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = mainArea,
                        style = MaterialTheme.typography.headlineLarge,
                        color = USBColors.Black,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodyLarge,
                        color = USBColors.TextMuted,
                    )
                }
            }

            item {
                UsbCard {
                    Text(
                        text = "Afinidad por áreas",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(Modifier.height(16.dp))
                    scores.forEach { score ->
                        ResultScoreBar(score = score)
                        Spacer(Modifier.height(14.dp))
                    }
                }
            }

            item {
                Text(
                    text = "Programas recomendados",
                    style = MaterialTheme.typography.headlineMedium,
                    color = USBColors.White,
                )
            }

            items(careers, key = { it.rank }) { career ->
                UsbCard {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(USBColors.Orange),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = career.rank.toString(),
                                color = USBColors.White,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = career.name,
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = career.area,
                                color = USBColors.Orange,
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = career.description,
                                color = USBColors.TextMuted,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${career.score}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = USBColors.Black,
                        )
                    }
                }
            }

            item {
                UsbCard {
                    Text(
                        text = "Resumen del reporte",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Generado el $generatedAt. Los resultados son orientativos y no sustituyen un proceso de acompañamiento profesional.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = USBColors.TextMuted,
                    )
                    Spacer(Modifier.height(16.dp))
                    UsbPrimaryButton(
                        text = "Descargar reporte",
                        onClick = onDownloadClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultScoreBar(score: ResultScoreUi) {
    UsbProgress(
        progress = score.percentage.coerceIn(0, 100) / 100f,
        label = score.label,
        trailingText = "${score.percentage}%",
    )
}

@Composable
fun ProfileMobileScreen(
    state: ProfileUiState,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { UsbBrandBar(userName = state.fullName) },
        containerColor = USBColors.Cream,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                UsbCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(USBColors.Orange, USBColors.Blue),
                                    ),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = state.initials,
                                style = MaterialTheme.typography.headlineMedium,
                                color = USBColors.White,
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.role.uppercase(),
                                color = USBColors.Orange,
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Text(
                                text = state.fullName,
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                text = state.email,
                                color = USBColors.TextMuted,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }

            item {
                UsbCard {
                    ProfileDataRow("Documento", state.documentNumber)
                    UsbDivider(Modifier.padding(vertical = 12.dp))
                    ProfileDataRow("Teléfono", state.phone)
                    UsbDivider(Modifier.padding(vertical = 12.dp))
                    ProfileDataRow("Ciudad", state.city)
                }
            }

            item {
                UsbPrimaryButton(text = "Editar perfil", onClick = onEditClick)
                Spacer(Modifier.height(10.dp))
                UsbSecondaryButton(text = "Cerrar sesión", onClick = onLogoutClick)
                Spacer(Modifier.height(10.dp))
                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Volver", color = USBColors.Orange)
                }
            }
        }
    }
}

@Composable
private fun ProfileDataRow(label: String, value: String) {
    Column {
        Text(
            text = label.uppercase(),
            color = USBColors.TextMuted,
            style = MaterialTheme.typography.labelLarge,
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = value,
            color = USBColors.Black,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun AdminDashboardMobileScreen(
    administratorName: String,
    metrics: List<AdminMetricUi>,
    users: List<AdminUserUi>,
    onUserClick: (String) -> Unit,
    onGenerateReportClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableStateOf("Resumen") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            UsbBrandBar(
                userName = administratorName,
                onUserClick = onLogoutClick,
            )
        },
        containerColor = Color(0xFFF4ECDE),
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                UsbCard(
                    containerColor = USBColors.Orange,
                ) {
                    Text(
                        text = "Panel administrativo",
                        style = MaterialTheme.typography.headlineMedium,
                        color = USBColors.White,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Consulta usuarios, resultados y reportes desde una interfaz adaptada a móvil.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = USBColors.White.copy(alpha = 0.88f),
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Resumen", "Usuarios").forEach { tab ->
                            val selected = selectedTab == tab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (selected) USBColors.White
                                        else USBColors.White.copy(alpha = 0.16f),
                                    )
                                    .clickable { selectedTab = tab }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = tab,
                                    color = if (selected) USBColors.Orange else USBColors.White,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }
                }
            }

            if (selectedTab == "Resumen") {
                items(metrics) { metric ->
                    UsbCard {
                        Text(
                            text = metric.label.uppercase(),
                            color = USBColors.Orange,
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = metric.value,
                            style = MaterialTheme.typography.headlineLarge,
                            color = USBColors.Black,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = metric.supportingText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = USBColors.TextMuted,
                        )
                    }
                }

                item {
                    UsbPrimaryButton(
                        text = "Generar reporte",
                        onClick = onGenerateReportClick,
                    )
                }
            } else {
                items(users, key = { it.id }) { user ->
                    UsbCard(
                        modifier = Modifier.clickable { onUserClick(user.id) },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user.fullName,
                                    style = MaterialTheme.typography.titleLarge,
                                )
                                Spacer(Modifier.height(5.dp))
                                Text(
                                    text = user.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = USBColors.TextMuted,
                                )
                            }
                            UsbStatusPill(
                                text = user.status,
                                danger = user.status.equals("Inactivo", ignoreCase = true),
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Rol: ${user.role}",
                            style = MaterialTheme.typography.labelLarge,
                            color = USBColors.Orange,
                        )
                    }
                }
            }
        }
    }
}
