package com.usbbog.orientacionvocacional.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
//import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val WebOrange = Color(0xFFEF7D00)
private val WebBlue = Color(0xFF181E7B)
private val WebBlack = Color(0xFF1C1C1C)
private val WebWhite = Color(0xFFFFFFFF)
private val WebGray = Color(0xFF858581)
private val WebBorder = Color(0xFFD8D6D1)

/**
 * Login móvil basado en la versión web.
 *
 * La pantalla conserva el estado y las validaciones del LoginViewModel que
 * llegan desde AppNavigation. Solo mantiene estado visual local para mostrar
 * u ocultar la contraseña.
 */
@Composable
fun LoginScreen(
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
    onHelpClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WebWhite),
    ) {
        WebLoginTopBar(onHelpClick = onHelpClick)

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    Brush.verticalGradient(
                        0.00f to WebOrange,
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
                WebLoginCard(
                    email = email,
                    password = password,
                    rememberMe = rememberMe,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange,
                    onRememberChange = onRememberChange,
                    onLoginClick = onLoginClick,
                    onRegisterClick = onRegisterClick,
                    onForgotPasswordClick = onForgotPasswordClick,
                )
            }
        }

        WebLoginFooter()
    }
}

@Composable
private fun WebLoginTopBar(
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .heightIn(min = 68.dp)
            .shadow(elevation = 4.dp)
            .background(WebWhite)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WebInstitutionMark(
            darkText = true,
            modifier = Modifier.weight(1f),
        )

        Spacer(Modifier.width(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .width(142.dp)
                    .height(42.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFBEBEBE),
                        shape = RoundedCornerShape(100.dp),
                    )
                    .clickable(onClick = onHelpClick),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(22.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color(0xFF858581),
                            radius = size.minDimension / 2f - 0.75.dp.toPx(),
                            style = Stroke(width = 1.5.dp.toPx()),
                        )
                    }
                    Text(
                        text = "?",
                        color = Color(0xFF777773),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.width(5.dp))

                Text(
                    text = "¿Cómo responder?",
                    color = Color(0xFF777773),
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }

            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(42.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(WebOrange),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Usuario",
                    color = WebWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun WebLoginCard(
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
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val cardShape = RoundedCornerShape(30.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 550.dp)
            .shadow(
                elevation = 16.dp,
                shape = cardShape,
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.12f),
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = WebWhite),
        border = BorderStroke(1.dp, WebWhite.copy(alpha = 0.55f)),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            0.00f to WebOrange,
                            0.42f to WebOrange,
                            0.70f to Color(0xFF92553C),
                            1.00f to WebBlue,
                        ),
                    )
                    .padding(
                        start = 26.dp,
                        top = 22.dp,
                        end = 26.dp,
                        bottom = 20.dp,
                    ),
            ) {
                Text(
                    text = "Iniciar sesión",
                    color = WebWhite,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 40.sp,
                        lineHeight = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "Accede a la plataforma institucional de orientación vocacional",
                    color = WebWhite,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.5.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WebWhite)
                    .padding(
                        start = 14.dp,
                        top = 18.dp,
                        end = 14.dp,
                        bottom = 14.dp,
                    ),
            ) {
                WebLoginField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Correo",
                    placeholder = "usuario@example.com",
                    keyboardType = KeyboardType.Email,
                    enabled = !isLoading,
                )

                Spacer(Modifier.height(16.dp))

                WebLoginField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = "Contraseña",
                    placeholder = "Ingresa tu contraseña",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    enabled = !isLoading,
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            enabled = !isLoading,
                            modifier = Modifier.size(40.dp),
                        ) {
                            PasswordVisibilityGlyph(visible = passwordVisible)
                        }
                    },
                )

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.clickable(
                            enabled = !isLoading,
                            onClick = { onRememberChange(!rememberMe) },
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(
                                    color = if (rememberMe) WebOrange else WebWhite,
                                    shape = RoundedCornerShape(1.dp),
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (rememberMe) WebOrange else Color(0xFF858581),
                                    shape = RoundedCornerShape(1.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (rememberMe) {
                                Text(
                                    text = "✓",
                                    color = WebWhite,
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text = "Recordarme",
                            color = WebBlack,
                            fontSize = 14.sp,
                        )
                    }

                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        modifier = Modifier
                            .clickable(
                                enabled = !isLoading,
                                onClick = onForgotPasswordClick,
                            )
                            .padding(vertical = 7.dp),
                        color = WebOrange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                    )
                }

                if (!errorMessage.isNullOrBlank()) {
                    Spacer(Modifier.height(10.dp))
                    WebLoginError(message = errorMessage)
                }

                Spacer(Modifier.height(16.dp))

                WebGradientLoginButton(
                    text = if (isLoading) "Validando..." else "Ingresar",
                    onClick = onLoginClick,
                    loading = isLoading,
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFDFDDD8),
                    )
                    Text(
                        text = "acceso seguro",
                        modifier = Modifier.padding(horizontal = 10.dp),
                        color = WebGray,
                        fontSize = 14.sp,
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFDFDDD8),
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "¿No tienes cuenta?",
                        color = WebBlack,
                        fontSize = 14.5.sp,
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Regístrate",
                        modifier = Modifier
                            .clickable(enabled = !isLoading, onClick = onRegisterClick)
                            .padding(vertical = 5.dp),
                        color = WebOrange,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Spacer(Modifier.height(9.dp))

                Text(
                    text = "UNIVERSIDAD DE SAN BUENAVENTURA VIGILADA MINIEDUCACIÓN",
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFC5C2BB),
                    fontSize = 8.sp,
                    lineHeight = 11.sp,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun WebLoginField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = WebBlack,
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
                    color = WebGray,
                    fontSize = 15.sp,
                )
            },
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(16.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp,
                color = WebBlack,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = WebOrange,
                unfocusedBorderColor = WebBorder,
                disabledBorderColor = WebBorder,
                focusedContainerColor = WebWhite,
                unfocusedContainerColor = WebWhite,
                disabledContainerColor = WebWhite,
                cursorColor = WebOrange,
            ),
        )
    }
}

@Composable
private fun PasswordVisibilityGlyph(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(22.dp)) {
        val stroke = 1.dp.toPx()
        val color = Color(0xFF8A8A86)

        drawCircle(
            color = color,
            radius = size.minDimension / 2f - stroke / 2f,
            style = Stroke(width = stroke),
        )
        drawCircle(
            color = color,
            radius = size.minDimension * 0.22f,
            style = Stroke(width = stroke),
        )
        if (visible) {
            drawCircle(
                color = WebBlue,
                radius = size.minDimension * 0.09f,
            )
        }
    }
}

@Composable
private fun WebGradientLoginButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
) {
    val buttonShape = RoundedCornerShape(100.dp)

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        enabled = !loading,
        shape = buttonShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = WebWhite,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = WebWhite,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(buttonShape)
                .background(
                    Brush.verticalGradient(
                        0.00f to WebOrange,
                        0.42f to WebOrange,
                        0.72f to Color(0xFF92553C),
                        1.00f to WebBlue,
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                color = WebWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun WebLoginError(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFFFEEEE),
                shape = RoundedCornerShape(12.dp),
            )
            .border(
                width = 1.dp,
                color = Color(0xFFE4A6A6),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = message,
            color = Color(0xFF9D2424),
            fontSize = 13.sp,
            lineHeight = 18.sp,
        )
    }
}

@Composable
private fun WebLoginFooter(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(WebBlue)
            .navigationBarsPadding()
            .heightIn(min = 74.dp)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WebInstitutionMark(
            darkText = false,
            showSecondaryMark = true,
            modifier = Modifier.width(118.dp),
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = "Somos una institución educativa de la Comunidad Franciscana Provincia de la Santa Fe " +
                    "de educación superior\n" +
                    "“con personería jurídica reconocida por el Ministerio de Educación en Resolución 1326 " +
                    "del 25 de marzo de 1975”\n" +
                    "Copyright © 2026 Universidad de San Buenaventura, Sede Bogotá | Políticas de uso y " +
                    "privacidad | Términos y Condiciones\n" +
                    "Institución de educación superior sujeta a la inspección y vigilancia del Ministerio " +
                    "de Educación Nacional",
            modifier = Modifier.weight(1f),
            color = WebWhite,
            fontSize = 5.8.sp,
            lineHeight = 7.2.sp,
            letterSpacing = 0.05.sp,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * Marca tipográfica temporal construida en Compose. Puede sustituirse por los
 * logos oficiales cuando los archivos se agreguen a res/drawable.
 */
@Composable
private fun WebInstitutionMark(
    darkText: Boolean,
    modifier: Modifier = Modifier,
    showSecondaryMark: Boolean = false,
) {
    val badgeWidth = if (showSecondaryMark) 25.dp else 30.dp
    val badgeHeight = if (showSecondaryMark) 34.dp else 40.dp
    val itemSpacing = if (showSecondaryMark) 4.dp else 5.dp

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(badgeWidth)
                .height(badgeHeight)
                .clip(
                    RoundedCornerShape(
                        topStart = 9.dp,
                        topEnd = 9.dp,
                        bottomStart = 6.dp,
                        bottomEnd = 6.dp,
                    ),
                )
                .background(WebOrange),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "USB",
                color = WebWhite,
                fontSize = 6.5.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.width(itemSpacing))

        Text(
            text = "UNIVERSIDAD DE\nSAN BUENAVENTURA",
            color = if (darkText) WebBlack else WebWhite,
            fontSize = if (darkText) 7.5.sp else 5.8.sp,
            lineHeight = if (darkText) 9.sp else 6.8.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
        )

        if (showSecondaryMark) {
            Spacer(Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(28.dp)
                    .background(WebWhite.copy(alpha = 0.85f)),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "USB",
                color = WebWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}