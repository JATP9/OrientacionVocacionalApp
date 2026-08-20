package com.usbbog.orientacionvocacional.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usbbog.orientacionvocacional.ui.components.UsbAppFooter
import com.usbbog.orientacionvocacional.ui.components.UsbAppTopBar
import com.usbbog.orientacionvocacional.ui.theme.USBColors
import com.usbbog.orientacionvocacional.viewmodel.ResetPasswordUiState
import com.usbbog.orientacionvocacional.viewmodel.passwordRequirementText

@Composable
fun ResetPasswordScreen(
    state: ResetPasswordUiState,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onResetClick: () -> Unit,
    onRequestAgainClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(USBColors.White),
    ) {
        UsbAppTopBar(userLabel = "Usuario")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    Brush.verticalGradient(
                        listOf(USBColors.Orange, Color(0xFF98543C), USBColors.Blue),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 560.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = USBColors.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text(
                            text = if (state.hasValidToken) {
                                "Restablecer contraseña"
                            } else {
                                "Enlace no válido"
                            },
                            color = USBColors.Black,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                            ),
                        )

                        Text(
                            text = if (state.hasValidToken) {
                                "Elige una nueva contraseña para tu cuenta."
                            } else {
                                "El enlace llegó sin el token de recuperación. Solicita uno nuevo."
                            },
                            color = USBColors.TextMuted,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )

                        if (state.hasValidToken) {
                            ResetPasswordField(
                                value = state.password,
                                onValueChange = onPasswordChange,
                                label = "Nueva contraseña",
                                error = state.passwordError,
                                enabled = !state.isSubmitting && !state.isSuccess,
                            )

                            Text(
                                text = passwordRequirementText(),
                                color = USBColors.TextMuted,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp,
                            )

                            ResetPasswordField(
                                value = state.confirmPassword,
                                onValueChange = onConfirmPasswordChange,
                                label = "Confirmar contraseña",
                                error = state.confirmPasswordError,
                                enabled = !state.isSubmitting && !state.isSuccess,
                            )
                        }

                        state.statusMessage?.takeIf(String::isNotBlank)?.let { message ->
                            Text(
                                text = message,
                                color = if (state.isSuccess) USBColors.Success else USBColors.Danger,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }

                        if (state.hasValidToken && !state.isSuccess) {
                            Button(
                                onClick = onResetClick,
                                enabled = !state.isSubmitting,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = USBColors.Orange,
                                    contentColor = USBColors.White,
                                ),
                                contentPadding = PaddingValues(horizontal = 20.dp),
                            ) {
                                if (state.isSubmitting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = USBColors.White,
                                        strokeWidth = 2.dp,
                                    )
                                    Spacer(Modifier.size(8.dp))
                                }
                                Text(
                                    text = if (state.isSubmitting) {
                                        "Restableciendo..."
                                    } else {
                                        "Restablecer contraseña"
                                    },
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        TextButton(
                            onClick = if (state.hasValidToken || state.isSuccess) {
                                onBackToLoginClick
                            } else {
                                onRequestAgainClick
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = if (state.hasValidToken || state.isSuccess) {
                                    "Volver a iniciar sesión"
                                } else {
                                    "Solicitar nuevamente"
                                },
                                color = USBColors.Orange,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }

        UsbAppFooter()
    }
}

@Composable
private fun ResetPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    enabled: Boolean,
) {
    var visible by rememberSaveable { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (visible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { visible = !visible }, enabled = enabled) {
                    Icon(
                        imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                    )
                }
            },
            isError = error != null,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = USBColors.Orange,
                cursorColor = USBColors.Orange,
            ),
        )

        if (!error.isNullOrBlank()) {
            Text(
                text = error,
                color = USBColors.Danger,
                fontSize = 12.sp,
                lineHeight = 17.sp,
            )
        }
    }
}
