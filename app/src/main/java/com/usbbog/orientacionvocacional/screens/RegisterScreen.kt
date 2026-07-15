package com.usbbog.orientacionvocacional.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
fun RegisterScreen(
    onRegisterClick: () -> Unit = {},
    onBackToLoginClick: () -> Unit = {}
) {
    var nombreCompleto by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var documento by remember { mutableStateOf("") }
    var semestre by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    var mostrarContrasena by remember { mutableStateOf(false) }
    var mostrarConfirmarContrasena by remember { mutableStateOf(false) }

    var errorGeneral by remember { mutableStateOf<String?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    val semestres = listOf(
        "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "10"
    )

    val carreras = listOf(
        "Ingeniería de Sistemas",
        "Psicología",
        "Administración de Empresas",
        "Contaduría Pública",
        "Derecho",
        "Educación Infantil",
        "Licenciatura en Humanidades",
        "Otra"
    )

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
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 820.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                RegisterHeader()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    RegisterInfoCard()

                    Spacer(modifier = Modifier.height(20.dp))

                    FormLabel(text = "Nombre completo")

                    Spacer(modifier = Modifier.height(7.dp))

                    UsbTextField(
                        value = nombreCompleto,
                        onValueChange = {
                            nombreCompleto = it
                            errorGeneral = null
                            mensajeExito = null
                        },
                        placeholder = "Ingresa tu nombre completo",
                        keyboardType = KeyboardType.Text
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    FormLabel(text = "Correo institucional")

                    Spacer(modifier = Modifier.height(7.dp))

                    UsbTextField(
                        value = correo,
                        onValueChange = {
                            correo = it
                            errorGeneral = null
                            mensajeExito = null
                        },
                        placeholder = "usuario@usbbog.edu.co",
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            FormLabel(text = "Documento")

                            Spacer(modifier = Modifier.height(7.dp))

                            UsbTextField(
                                value = documento,
                                onValueChange = {
                                    documento = it
                                    errorGeneral = null
                                    mensajeExito = null
                                },
                                placeholder = "Número",
                                keyboardType = KeyboardType.Number
                            )
                        }

                        Column(
                            modifier = Modifier.width(125.dp)
                        ) {
                            FormLabel(text = "Semestre")

                            Spacer(modifier = Modifier.height(7.dp))

                            UsbDropdownField(
                                selectedValue = semestre,
                                placeholder = "Elegir",
                                options = semestres,
                                onOptionSelected = {
                                    semestre = it
                                    errorGeneral = null
                                    mensajeExito = null
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    FormLabel(text = "Carrera a la que pertenece")

                    Spacer(modifier = Modifier.height(7.dp))

                    UsbDropdownField(
                        selectedValue = carrera,
                        placeholder = "Seleccionar carrera",
                        options = carreras,
                        onOptionSelected = {
                            carrera = it
                            errorGeneral = null
                            mensajeExito = null
                        }
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    FormLabel(text = "Contraseña")

                    Spacer(modifier = Modifier.height(7.dp))

                    UsbPasswordField(
                        value = contrasena,
                        onValueChange = {
                            contrasena = it
                            errorGeneral = null
                            mensajeExito = null
                        },
                        placeholder = "Crea una contraseña",
                        visible = mostrarContrasena,
                        onVisibilityChange = {
                            mostrarContrasena = !mostrarContrasena
                        }
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    FormLabel(text = "Confirmar contraseña")

                    Spacer(modifier = Modifier.height(7.dp))

                    UsbPasswordField(
                        value = confirmarContrasena,
                        onValueChange = {
                            confirmarContrasena = it
                            errorGeneral = null
                            mensajeExito = null
                        },
                        placeholder = "Repite tu contraseña",
                        visible = mostrarConfirmarContrasena,
                        onVisibilityChange = {
                            mostrarConfirmarContrasena = !mostrarConfirmarContrasena
                        }
                    )

                    if (errorGeneral != null) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = errorGeneral ?: "",
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = Color(0xFFC2410C),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (mensajeExito != null) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = mensajeExito ?: "",
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = UsbOrange,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    GradientRegisterButton(
                        text = "Registrarme",
                        onClick = {
                            errorGeneral = null
                            mensajeExito = null

                            when {
                                nombreCompleto.isBlank() -> {
                                    errorGeneral = "Ingresa tu nombre completo."
                                }

                                correo.isBlank() -> {
                                    errorGeneral = "Ingresa tu correo institucional."
                                }

                                !correo.contains("@") -> {
                                    errorGeneral = "Ingresa un correo válido."
                                }

                                !correo.endsWith("@usbbog.edu.co") -> {
                                    errorGeneral = "Usa tu correo institucional de la USB Bogotá."
                                }

                                documento.isBlank() -> {
                                    errorGeneral = "Ingresa tu número de documento."
                                }

                                semestre.isBlank() -> {
                                    errorGeneral = "Selecciona tu semestre."
                                }

                                carrera.isBlank() -> {
                                    errorGeneral = "Selecciona la carrera a la que perteneces."
                                }

                                contrasena.isBlank() -> {
                                    errorGeneral = "Crea una contraseña."
                                }

                                contrasena.length < 6 -> {
                                    errorGeneral = "La contraseña debe tener al menos 6 caracteres."
                                }

                                confirmarContrasena.isBlank() -> {
                                    errorGeneral = "Confirma tu contraseña."
                                }

                                contrasena != confirmarContrasena -> {
                                    errorGeneral = "Las contraseñas no coinciden."
                                }

                                else -> {
                                    mensajeExito = "Registro en desarrollo. Datos válidos."
                                    onRegisterClick()
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿Ya tienes cuenta?",
                            fontSize = 13.sp,
                            color = UsbSecondaryText
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        TextButton(
                            onClick = onBackToLoginClick,
                            modifier = Modifier.height(24.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Iniciar sesión",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = UsbOrange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

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
private fun RegisterHeader() {
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
                        text = "Registro",
                        color = UsbWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Crear cuenta",
                color = UsbWhite,
                fontSize = 27.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 31.sp
            )

            Spacer(modifier = Modifier.height(9.dp))

            Text(
                text = "Regístrate para acceder a la plataforma institucional de orientación vocacional.",
                color = UsbWhite.copy(alpha = 0.86f),
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun RegisterInfoCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFFFF4DE))
            .border(
                BorderStroke(1.dp, Color(0xFFF2D29A)),
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 15.dp, vertical = 14.dp)
    ) {
        Text(
            text = "Crea tu perfil vocacional",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF7A4C10)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Completa tus datos académicos para iniciar tu proceso de orientación.",
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = Color(0xFF7C633A)
        )
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = UsbText
    )
}

@Composable
private fun UsbTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 13.sp,
                color = UsbPlaceholder
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(15.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = UsbGrayBackground,
            unfocusedContainerColor = UsbGrayBackground,
            focusedBorderColor = UsbOrange,
            unfocusedBorderColor = UsbGrayBorder,
            focusedTextColor = UsbText,
            unfocusedTextColor = UsbText,
            cursorColor = UsbOrange
        )
    )
}

@Composable
private fun UsbPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visible: Boolean,
    onVisibilityChange: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 13.sp,
                color = UsbPlaceholder
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(15.dp),
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(
                onClick = onVisibilityChange
            ) {
                Icon(
                    imageVector = if (visible) {
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
            focusedBorderColor = UsbOrange,
            unfocusedBorderColor = UsbGrayBorder,
            focusedTextColor = UsbText,
            unfocusedTextColor = UsbText,
            cursorColor = UsbOrange
        )
    )
}

@Composable
private fun UsbDropdownField(
    selectedValue: String,
    placeholder: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(UsbGrayBackground)
                .border(
                    BorderStroke(1.dp, UsbGrayBorder),
                    RoundedCornerShape(15.dp)
                )
                .clickable { expanded = true }
                .padding(horizontal = 15.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedValue.ifBlank { placeholder },
                    modifier = Modifier.weight(1f),
                    fontSize = 13.sp,
                    color = if (selectedValue.isBlank()) UsbPlaceholder else UsbText
                )

                Text(
                    text = "▾",
                    fontSize = 16.sp,
                    color = UsbSecondaryText
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.82f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontSize = 13.sp,
                            color = UsbText
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun GradientRegisterButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1F2F6B),
                        UsbOrange
                    )
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = UsbWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}