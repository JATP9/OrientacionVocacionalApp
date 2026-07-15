package com.usbbog.orientacionvocacional.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usbbog.orientacionvocacional.ui.theme.*

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var recordar by remember { mutableStateOf(false) }
    var mostrarContrasena by remember { mutableStateOf(false) }

    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorContrasena by remember { mutableStateOf<String?>(null) }
    var mensajeGeneral by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        UsbBlack,
                        UsbSoftBlack,
                        UsbBlack
                    )
                )
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(34.dp)),
            color = UsbWhite,
            shadowElevation = 16.dp,
            shape = RoundedCornerShape(34.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                LoginHeader()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    InfoCard()

                    Spacer(modifier = Modifier.height(22.dp))

                    Text(
                        text = "Correo institucional",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = UsbText
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = {
                            correo = it
                            errorCorreo = null
                            mensajeGeneral = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "usuario@usbbog.edu.co",
                                fontSize = 13.sp,
                                color = UsbPlaceholder
                            )
                        },
                        singleLine = true,
                        isError = errorCorreo != null,
                        shape = RoundedCornerShape(15.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = UsbGrayBackground,
                            unfocusedContainerColor = UsbGrayBackground,
                            errorContainerColor = UsbGrayBackground,
                            focusedBorderColor = UsbOrange,
                            unfocusedBorderColor = UsbGrayBorder,
                            errorBorderColor = Color(0xFFC2410C),
                            focusedTextColor = UsbText,
                            unfocusedTextColor = UsbText,
                            errorTextColor = UsbText,
                            cursorColor = UsbOrange
                        )
                    )

                    if (errorCorreo != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorCorreo ?: "",
                            fontSize = 11.sp,
                            color = Color(0xFFC2410C)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Contraseña",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = UsbText
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = {
                            contrasena = it
                            errorContrasena = null
                            mensajeGeneral = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Ingresa tu contraseña",
                                fontSize = 13.sp,
                                color = UsbPlaceholder
                            )
                        },
                        singleLine = true,
                        isError = errorContrasena != null,
                        shape = RoundedCornerShape(15.dp),
                        visualTransformation = if (mostrarContrasena) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { mostrarContrasena = !mostrarContrasena }
                            ) {
                                Icon(
                                    imageVector = if (mostrarContrasena) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = "Mostrar contraseña",
                                    tint = UsbSecondaryText
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = UsbGrayBackground,
                            unfocusedContainerColor = UsbGrayBackground,
                            errorContainerColor = UsbGrayBackground,
                            focusedBorderColor = UsbOrange,
                            unfocusedBorderColor = UsbGrayBorder,
                            errorBorderColor = Color(0xFFC2410C),
                            focusedTextColor = UsbText,
                            unfocusedTextColor = UsbText,
                            errorTextColor = UsbText,
                            cursorColor = UsbOrange
                        )
                    )

                    if (errorContrasena != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorContrasena ?: "",
                            fontSize = 11.sp,
                            color = Color(0xFFC2410C)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = recordar,
                                onCheckedChange = { recordar = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = UsbOrange,
                                    uncheckedColor = UsbGrayBorder,
                                    checkmarkColor = UsbWhite
                                )
                            )

                            Text(
                                text = "Recordarme",
                                fontSize = 12.sp,
                                color = UsbSecondaryText
                            )
                        }

                        TextButton(
                            onClick = onForgotPasswordClick
                        ) {
                            Text(
                                text = "¿Olvidaste tu clave?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = UsbOrange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            errorCorreo = null
                            errorContrasena = null
                            mensajeGeneral = null

                            when {
                                correo.isBlank() -> {
                                    errorCorreo = "Ingresa tu correo institucional"
                                }

                                !correo.contains("@") -> {
                                    errorCorreo = "Ingresa un correo válido"
                                }

                                !correo.endsWith("@usbbog.edu.co") -> {
                                    errorCorreo = "Usa tu correo institucional de la USB Bogotá"
                                }

                                contrasena.isBlank() -> {
                                    errorContrasena = "Ingresa tu contraseña"
                                }

                                contrasena.length < 6 -> {
                                    errorContrasena = "La contraseña debe tener al menos 6 caracteres"
                                }

                                else -> {
                                    mensajeGeneral = "Inicio de sesión en desarrollo"
                                    onLoginClick()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(17.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = UsbOrange,
                            contentColor = UsbWhite
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp
                        )
                    ) {
                        Text(
                            text = "Ingresar",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    if (mensajeGeneral != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = mensajeGeneral ?: "",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = UsbOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = UsbGrayBorder
                        )

                        Text(
                            text = "acceso seguro",
                            modifier = Modifier.padding(horizontal = 10.dp),
                            fontSize = 11.sp,
                            color = UsbSecondaryText,
                            fontWeight = FontWeight.Medium
                        )

                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = UsbGrayBorder
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿Aún no tienes cuenta?",
                            fontSize = 13.sp,
                            color = UsbSecondaryText
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        TextButton(
                            onClick = onRegisterClick,
                            modifier = Modifier.height(24.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Crear cuenta",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = UsbOrange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    HorizontalDivider(color = UsbGrayBorder)

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Universidad de San Buenaventura Bogotá\nVIGILADA MINEDUCACIÓN",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        lineHeight = 15.sp,
                        color = UsbSecondaryText
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        UsbBlack,
                        UsbSoftBlack,
                        UsbLightOrange
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 22.dp)
    ) {
        Box(
            modifier = Modifier
                .size(170.dp)
                .align(Alignment.TopEnd)
                .background(
                    color = UsbOrange.copy(alpha = 0.18f),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(110.dp)
                .align(Alignment.BottomStart)
                .background(
                    color = UsbWhite.copy(alpha = 0.08f),
                    shape = CircleShape
                )
        )

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(UsbWhite.copy(alpha = 0.12f))
                        .border(
                            BorderStroke(1.dp, UsbWhite.copy(alpha = 0.22f)),
                            RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "USB",
                        color = UsbWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(UsbWhite.copy(alpha = 0.13f))
                        .border(
                            BorderStroke(1.dp, UsbWhite.copy(alpha = 0.18f)),
                            RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 13.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Orientación Vocacional",
                        color = UsbWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Iniciar sesión",
                color = UsbWhite,
                fontSize = 27.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 31.sp
            )

            Spacer(modifier = Modifier.height(9.dp))

            Text(
                text = "Accede a la plataforma institucional de orientación vocacional.",
                color = UsbWhite.copy(alpha = 0.86f),
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun InfoCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(UsbOrangeBackground)
            .border(
                BorderStroke(1.dp, Color(0xFFF2D29A)),
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 15.dp, vertical = 14.dp)
    ) {
        Text(
            text = "Bienvenido a tu ruta vocacional",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF7A4C10)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Ingresa para consultar tus pruebas, resultados y recomendaciones académicas.",
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = Color(0xFF7C633A)
        )
    }
}