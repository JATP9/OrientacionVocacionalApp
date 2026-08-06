package com.usbbog.orientacionvocacional.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usbbog.orientacionvocacional.ui.components.UsbFooterLogo
import com.usbbog.orientacionvocacional.ui.components.UsbHeaderLogo

private val RecoverOrange = Color(0xFFEF7D00)
private val RecoverBlue = Color(0xFF181E7B)
private val RecoverBlack = Color(0xFF1C1C1C)
private val RecoverWhite = Color(0xFFFFFFFF)
private val RecoverGray = Color(0xFF858581)
private val RecoverBorder = Color(0xFFD8D6D1)

/**
 * Adaptación móvil de ForgotPasswordPage.
 *
 * La pantalla es stateless: las validaciones y el envío se administran desde
 * ForgotPasswordViewModel y AppNavigation.
 */
@Composable
fun ForgotPasswordScreen(
    email: String,
    document: String,
    isSubmitting: Boolean,
    emailError: String?,
    statusMessage: String?,
    isSuccess: Boolean,
    onEmailChange: (String) -> Unit,
    onDocumentChange: (String) -> Unit,
    onRecoverClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RecoverWhite),
    ) {
        RecoverTopBar()

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    Brush.verticalGradient(
                        0.00f to RecoverOrange,
                        0.34f to Color(0xFFD56E12),
                        0.67f to Color(0xFF8B5140),
                        1.00f to Color(0xFF4B3158),
                    ),
                ),
        ) {
            val viewportHeight = maxHeight

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .heightIn(min = viewportHeight)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                RecoverCard(
                    email = email,
                    document = document,
                    isSubmitting = isSubmitting,
                    emailError = emailError,
                    statusMessage = statusMessage,
                    isSuccess = isSuccess,
                    onEmailChange = onEmailChange,
                    onDocumentChange = onDocumentChange,
                    onRecoverClick = onRecoverClick,
                    onBackToLoginClick = onBackToLoginClick,
                )
            }
        }

        RecoverFooter()
    }
}

@Composable
private fun RecoverTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .heightIn(min = 68.dp)
            .shadow(4.dp)
            .background(RecoverWhite)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RecoverInstitutionMark(
            darkText = true,
            modifier = Modifier.weight(1f),
        )

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .height(42.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(RecoverOrange)
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Usuario",
                color = RecoverWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun RecoverCard(
    email: String,
    document: String,
    isSubmitting: Boolean,
    emailError: String?,
    statusMessage: String?,
    isSuccess: Boolean,
    onEmailChange: (String) -> Unit,
    onDocumentChange: (String) -> Unit,
    onRecoverClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
) {
    val cardShape = RoundedCornerShape(30.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 550.dp)
            .shadow(
                elevation = 16.dp,
                shape = cardShape,
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.12f),
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = RecoverWhite),
        border = BorderStroke(1.dp, RecoverWhite.copy(alpha = 0.55f)),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            0.00f to RecoverOrange,
                            0.45f to RecoverOrange,
                            0.75f to Color(0xFF92553C),
                            1.00f to RecoverBlue,
                        ),
                    )
                    .padding(
                        start = 24.dp,
                        top = 22.dp,
                        end = 24.dp,
                        bottom = 20.dp,
                    ),
            ) {
                Text(
                    text = "Recuperar acceso",
                    color = RecoverWhite,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 34.sp,
                        lineHeight = 39.sp,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )

                Spacer(Modifier.height(9.dp))

                Text(
                    text = "Simula el envío del enlace para el backend real",
                    color = RecoverWhite,
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        top = 20.dp,
                        end = 16.dp,
                        bottom = 18.dp,
                    ),
            ) {
                RecoverField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Correo",
                    placeholder = "usuario@example.com",
                    keyboardType = KeyboardType.Email,
                    enabled = !isSubmitting,
                )

                if (!emailError.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = emailError,
                        color = Color(0xFF9D2424),
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp,
                    )
                }

                Spacer(Modifier.height(16.dp))

                RecoverField(
                    value = document,
                    onValueChange = onDocumentChange,
                    label = "Documento opcional",
                    placeholder = "Número de documento",
                    keyboardType = KeyboardType.Number,
                    enabled = !isSubmitting,
                )

                if (!statusMessage.isNullOrBlank()) {
                    Spacer(Modifier.height(14.dp))
                    RecoverStatusMessage(
                        message = statusMessage,
                        isSuccess = isSuccess,
                    )
                }

                Spacer(Modifier.height(18.dp))

                RecoverButton(
                    text = if (isSubmitting) {
                        "Enviando..."
                    } else {
                        "Recuperar contraseña"
                    },
                    enabled = !isSubmitting,
                    onClick = onRecoverClick,
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Volver al login",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable(
                            enabled = !isSubmitting,
                            onClick = onBackToLoginClick,
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = RecoverOrange,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun RecoverField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType,
    enabled: Boolean,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = RecoverBlack,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(7.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    color = RecoverGray,
                    fontSize = 15.sp,
                )
            },
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(16.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp,
                color = RecoverBlack,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RecoverOrange,
                unfocusedBorderColor = RecoverBorder,
                disabledBorderColor = RecoverBorder,
                focusedContainerColor = RecoverWhite,
                unfocusedContainerColor = RecoverWhite,
                disabledContainerColor = RecoverWhite,
                cursorColor = RecoverOrange,
            ),
        )
    }
}

@Composable
private fun RecoverStatusMessage(
    message: String,
    isSuccess: Boolean,
) {
    val backgroundColor = if (isSuccess) {
        Color(0xFFEAF7EE)
    } else {
        Color(0xFFFFEEEE)
    }
    val borderColor = if (isSuccess) {
        Color(0xFF8FC79D)
    } else {
        Color(0xFFE4A6A6)
    }
    val textColor = if (isSuccess) {
        Color(0xFF246B36)
    } else {
        Color(0xFF9D2424)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 11.dp),
    ) {
        Text(
            text = message,
            color = textColor,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun RecoverButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(100.dp)

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp),
        enabled = enabled,
        shape = shape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = RecoverWhite,
            disabledContentColor = RecoverWhite,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (enabled) 1f else 0.68f)
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        0.00f to RecoverOrange,
                        0.42f to RecoverOrange,
                        0.72f to Color(0xFF92553C),
                        1.00f to RecoverBlue,
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                color = RecoverWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun RecoverFooter() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RecoverBlue)
            .navigationBarsPadding()
            .heightIn(min = 74.dp)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RecoverInstitutionMark(
            darkText = false,
            modifier = Modifier.width(118.dp),
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = "Universidad de San Buenaventura, Sede Bogotá\n" +
                    "Políticas de uso y privacidad | Términos y Condiciones\n" +
                    "Institución sujeta a inspección y vigilancia del Ministerio de Educación Nacional",
            modifier = Modifier.weight(1f),
            color = RecoverWhite,
            fontSize = 6.2.sp,
            lineHeight = 8.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun RecoverInstitutionMark(
    darkText: Boolean,
    modifier: Modifier = Modifier,
) {
    if (darkText) {
        UsbHeaderLogo(modifier = modifier.height(44.dp))
    } else {
        UsbFooterLogo(modifier = modifier.height(38.dp))
    }
}
