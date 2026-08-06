package com.usbbog.orientacionvocacional.screens

import android.app.DatePickerDialog
import android.content.Context
import android.util.Patterns
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.usbbog.orientacionvocacional.ui.components.UsbAppFooter
import com.usbbog.orientacionvocacional.ui.components.UsbAppTopBar
import com.usbbog.orientacionvocacional.ui.mobile.RegisterField
import com.usbbog.orientacionvocacional.ui.mobile.RegisterUiState
import java.util.Calendar
import java.util.Locale

private val RegisterOrange = Color(0xFFEF7D00)
private val RegisterBlue = Color(0xFF181E7B)
private val RegisterBlack = Color(0xFF1D1D1B)
private val RegisterWhite = Color(0xFFFFFFFF)
private val RegisterMuted = Color(0xFF666661)
private val RegisterBorder = Color(0xFFD7D5D0)
private val RegisterSoftBackground = Color(0xFFFAF6F0)
private val RegisterError = Color(0xFF9F2D20)

private const val UNDER_AGE_MESSAGE =
    "Debes tener al menos 18 años para crear una cuenta."

private val genderOptions = listOf(
    "Masculino",
    "Femenino",
    "Prefiero no decirlo",
    "Otro",
)

private val departmentOptions = listOf(
    "Amazonas",
    "Antioquia",
    "Arauca",
    "Atlántico",
    "Bogotá D.C.",
    "Bolívar",
    "Boyacá",
    "Caldas",
    "Caquetá",
    "Casanare",
    "Cauca",
    "Cesar",
    "Chocó",
    "Córdoba",
    "Cundinamarca",
    "Guainía",
    "Guaviare",
    "Huila",
    "La Guajira",
    "Magdalena",
    "Meta",
    "Nariño",
    "Norte de Santander",
    "Putumayo",
    "Quindío",
    "Risaralda",
    "San Andrés y Providencia",
    "Santander",
    "Sucre",
    "Tolima",
    "Valle del Cauca",
    "Vaupés",
    "Vichada",
)

private val cityOptions = listOf(
    "Apartadó",
    "Arauca",
    "Armenia",
    "Barrancabermeja",
    "Barranquilla",
    "Bello",
    "Bogotá D.C.",
    "Bucaramanga",
    "Buenaventura",
    "Buga",
    "Cali",
    "Cartagena",
    "Cartago",
    "Chía",
    "Cúcuta",
    "Duitama",
    "Envigado",
    "Facatativá",
    "Florencia",
    "Floridablanca",
    "Fusagasugá",
    "Girardot",
    "Ibagué",
    "Ipiales",
    "Itagüí",
    "Jamundí",
    "Leticia",
    "Manizales",
    "Medellín",
    "Mitú",
    "Mocoa",
    "Montería",
    "Neiva",
    "Palmira",
    "Pasto",
    "Pereira",
    "Popayán",
    "Puerto Carreño",
    "Quibdó",
    "Riohacha",
    "Rionegro",
    "San Andrés",
    "Santa Marta",
    "Sincelejo",
    "Soacha",
    "Sogamoso",
    "Tunja",
    "Valledupar",
    "Villavicencio",
    "Yopal",
)

private val semesterOptions = (1..10).map(Int::toString)

/**
 * Registro móvil basado en la pantalla web.
 *
 * Todos los campos se mantienen en RegisterUiState y se validan antes de crear
 * la sesión local. La persistencia definitiva quedará a cargo del backend.
 */
@Composable
fun RegisterWebScreenV2(
    state: RegisterUiState,
    onFieldChange: (RegisterField, String) -> Unit,
    onBirthDateChange: (Long) -> Unit,
    onBelongsToUniversityChange: (Boolean) -> Unit,
    onActiveStudentChange: (Boolean) -> Unit,
    onAcceptTermsChange: (Boolean) -> Unit,
    onAuthorizeDataChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var formErrors by remember { mutableStateOf(RegisterFormErrors()) }
    var pageError by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RegisterWhite),
    ) {
        UsbAppTopBar(userLabel = "Usuario")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    Brush.verticalGradient(
                        0.00f to RegisterOrange,
                        0.26f to Color(0xFFE9851E),
                        0.63f to Color(0xFF9F6842),
                        1.00f to RegisterBlue,
                    ),
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(
                        start = 16.dp,
                        top = 24.dp,
                        end = 16.dp,
                        bottom = 40.dp,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 760.dp)
                        .shadow(
                            elevation = 18.dp,
                            shape = RoundedCornerShape(30.dp),
                            ambientColor = Color.Black.copy(alpha = 0.12f),
                            spotColor = Color.Black.copy(alpha = 0.12f),
                        ),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = RegisterWhite.copy(alpha = 0.98f),
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = RegisterWhite.copy(alpha = 0.55f),
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 22.dp,
                                vertical = 24.dp,
                            ),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Registro de Usuario",
                                color = RegisterBlack,
                                style =
                                    MaterialTheme.typography.headlineLarge.copy(
                                        fontSize = 32.sp,
                                        lineHeight = 36.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                    ),
                                textAlign = TextAlign.Center,
                            )

                            Spacer(
                                modifier = Modifier.height(10.dp),
                            )

                            Text(
                                text =
                                    "Completa el formulario para acceder " +
                                            "a la prueba vocacional",
                                color = RegisterMuted,
                                fontSize = 15.sp,
                                lineHeight = 21.sp,
                                textAlign = TextAlign.Center,
                            )
                        }

                        RegisterTextField(
                            value = state.firstName,
                            onValueChange = {
                                onFieldChange(RegisterField.FirstName, it)
                                formErrors = formErrors.copy(
                                    firstName = null,
                                )
                                pageError = null
                            },
                            label = "Nombre",
                            placeholder = "Nombre",
                            error = formErrors.firstName,
                            enabled = !state.isLoading,
                            capitalization = KeyboardCapitalization.Words,
                        )

                        RegisterTextField(
                            value = state.lastName,
                            onValueChange = {
                                onFieldChange(RegisterField.LastName, it)
                                formErrors = formErrors.copy(
                                    lastName = null,
                                )
                                pageError = null
                            },
                            label = "Apellido",
                            placeholder = "Apellido",
                            error = formErrors.lastName,
                            enabled = !state.isLoading,
                            capitalization = KeyboardCapitalization.Words,
                        )

                        RegisterTextField(
                            value = state.email,
                            onValueChange = {
                                onFieldChange(RegisterField.Email, it)
                                formErrors = formErrors.copy(
                                    email = null,
                                )
                                pageError = null
                            },
                            label = "Correo",
                            placeholder = "usuario@example.com",
                            keyboardType = KeyboardType.Email,
                            error = formErrors.email,
                            enabled = !state.isLoading,
                        )

                        RegisterTextField(
                            value = state.documentNumber,
                            onValueChange = {
                                onFieldChange(
                                    RegisterField.DocumentNumber,
                                    it.filter(Char::isDigit).take(15),
                                )
                                formErrors = formErrors.copy(
                                    document = null,
                                )
                                pageError = null
                            },
                            label = "Identificación",
                            placeholder = "xxxxxxxxxxx",
                            keyboardType = KeyboardType.Number,
                            error = formErrors.document,
                            enabled = !state.isLoading,
                        )

                        RegisterDateField(
                            value = state.birthDateMillis
                                ?.let(::formatDate)
                                .orEmpty(),
                            error = formErrors.birthDate,
                            enabled = !state.isLoading,
                            onClick = {
                                showBirthDatePicker(
                                    context = context,
                                    selectedDateMillis =
                                        state.birthDateMillis,
                                ) { selectedDate ->
                                    onBirthDateChange(selectedDate)

                                    val dateError =
                                        validateBirthDate(selectedDate)

                                    formErrors = formErrors.copy(
                                        birthDate = dateError,
                                    )

                                    pageError =
                                        if (
                                            dateError ==
                                            UNDER_AGE_MESSAGE
                                        ) {
                                            UNDER_AGE_MESSAGE
                                        } else {
                                            null
                                        }
                                }
                            },
                        )

                        RegisterTextField(
                            value = state.phone,
                            onValueChange = {
                                onFieldChange(
                                    RegisterField.Phone,
                                    it.filter(Char::isDigit).take(15),
                                )
                                formErrors = formErrors.copy(
                                    phone = null,
                                )
                                pageError = null
                            },
                            label = "Teléfono",
                            placeholder = "xxxxxxxxxx",
                            keyboardType = KeyboardType.Phone,
                            error = formErrors.phone,
                            enabled = !state.isLoading,
                        )

                        RegisterDropdownField(
                            selectedValue = state.gender,
                            label = "Género",
                            placeholder = "-",
                            options = genderOptions,
                            error = formErrors.gender,
                            enabled = !state.isLoading,
                            onOptionSelected = {
                                onFieldChange(RegisterField.Gender, it)
                                formErrors = formErrors.copy(
                                    gender = null,
                                    genderOther = null,
                                )
                                pageError = null
                            },
                        )

                        RegisterTextField(
                            value = state.genderOther,
                            onValueChange = {
                                onFieldChange(
                                    RegisterField.GenderOther,
                                    it,
                                )
                                formErrors = formErrors.copy(
                                    genderOther = null,
                                )
                                pageError = null
                            },
                            label = "En caso de otro ¿cuál?",
                            placeholder = "-",
                            error = formErrors.genderOther,
                            enabled =
                                state.gender == "Otro" &&
                                        !state.isLoading,
                            capitalization =
                                KeyboardCapitalization.Sentences,
                        )

                        RegisterDropdownField(
                            selectedValue = state.department,
                            label = "Departamento",
                            placeholder = "-",
                            options = departmentOptions,
                            error = formErrors.department,
                            enabled = !state.isLoading,
                            onOptionSelected = {
                                onFieldChange(
                                    RegisterField.Department,
                                    it,
                                )
                                formErrors = formErrors.copy(
                                    department = null,
                                )
                                pageError = null
                            },
                        )

                        RegisterDropdownField(
                            selectedValue = state.city,
                            label = "Ciudad",
                            placeholder = "-",
                            options = cityOptions,
                            error = formErrors.city,
                            enabled = !state.isLoading,
                            onOptionSelected = {
                                onFieldChange(RegisterField.City, it)
                                formErrors = formErrors.copy(
                                    city = null,
                                )
                                pageError = null
                            },
                        )

                        RegisterTextField(
                            value = state.password,
                            onValueChange = {
                                onFieldChange(
                                    RegisterField.Password,
                                    it,
                                )
                                formErrors = formErrors.copy(
                                    password = null,
                                )
                                pageError = null
                            },
                            label = "Contraseña",
                            placeholder = "Ingresa tu contraseña",
                            keyboardType = KeyboardType.Password,
                            visualTransformation =
                                if (passwordVisible) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },
                            error = formErrors.password,
                            enabled = !state.isLoading,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        passwordVisible =
                                            !passwordVisible
                                    },
                                    enabled = !state.isLoading,
                                ) {
                                    Icon(
                                        imageVector =
                                            if (passwordVisible) {
                                                Icons.Default.VisibilityOff
                                            } else {
                                                Icons.Default.Visibility
                                            },
                                        contentDescription =
                                            if (passwordVisible) {
                                                "Ocultar contraseña"
                                            } else {
                                                "Mostrar contraseña"
                                            },
                                        tint = RegisterMuted,
                                    )
                                }
                            },
                        )

                        RegisterTextField(
                            value = state.confirmPassword,
                            onValueChange = {
                                onFieldChange(
                                    RegisterField.ConfirmPassword,
                                    it,
                                )
                                formErrors = formErrors.copy(
                                    confirmPassword = null,
                                )
                                pageError = null
                            },
                            label = "Confirmar contraseña",
                            placeholder = "Repite tu contraseña",
                            keyboardType = KeyboardType.Password,
                            visualTransformation =
                                if (confirmPasswordVisible) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                },
                            error = formErrors.confirmPassword,
                            enabled = !state.isLoading,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        confirmPasswordVisible =
                                            !confirmPasswordVisible
                                    },
                                    enabled = !state.isLoading,
                                ) {
                                    Icon(
                                        imageVector =
                                            if (confirmPasswordVisible) {
                                                Icons.Default.VisibilityOff
                                            } else {
                                                Icons.Default.Visibility
                                            },
                                        contentDescription =
                                            if (confirmPasswordVisible) {
                                                "Ocultar confirmación " +
                                                        "de contraseña"
                                            } else {
                                                "Mostrar confirmación " +
                                                        "de contraseña"
                                            },
                                        tint = RegisterMuted,
                                    )
                                }
                            },
                        )

                        RegisterCheckLine(
                            checked = state.belongsToUniversity,
                            text =
                                "¿Se encuentra usted actualmente inscrito " +
                                        "en la Universidad de San Buenaventura?",
                            onCheckedChange = {
                                onBelongsToUniversityChange(it)

                                if (!it && !state.isActiveStudent) {
                                    formErrors = formErrors.copy(
                                        currentCareer = null,
                                        currentSemester = null,
                                    )
                                }

                                pageError = null
                            },
                            enabled = !state.isLoading,
                        )

                        RegisterCheckLine(
                            checked = state.isActiveStudent,
                            text =
                                "¿Es usted estudiante activo de algún " +
                                        "programa de la Universidad de " +
                                        "San Buenaventura?",
                            onCheckedChange = {
                                onActiveStudentChange(it)

                                if (!it && !state.belongsToUniversity) {
                                    formErrors = formErrors.copy(
                                        currentCareer = null,
                                        currentSemester = null,
                                    )
                                }

                                pageError = null
                            },
                            enabled = !state.isLoading,
                        )

                        if (
                            state.belongsToUniversity ||
                            state.isActiveStudent
                        ) {
                            RegisterTextField(
                                value = state.currentCareer,
                                onValueChange = {
                                    onFieldChange(
                                        RegisterField.CurrentCareer,
                                        it,
                                    )
                                    formErrors = formErrors.copy(
                                        currentCareer = null,
                                    )
                                    pageError = null
                                },
                                label = "Carrera actual",
                                placeholder = "Programa académico",
                                error = formErrors.currentCareer,
                                enabled = !state.isLoading,
                                capitalization =
                                    KeyboardCapitalization.Words,
                            )

                            RegisterDropdownField(
                                selectedValue = state.currentSemester,
                                label = "Semestre actual",
                                placeholder = "-",
                                options = semesterOptions,
                                error = formErrors.currentSemester,
                                enabled = !state.isLoading,
                                onOptionSelected = {
                                    onFieldChange(
                                        RegisterField.CurrentSemester,
                                        it,
                                    )
                                    formErrors = formErrors.copy(
                                        currentSemester = null,
                                    )
                                    pageError = null
                                },
                            )
                        }

                        RegisterConsentBox(
                            checked = state.authorizeData,
                            text =
                                "Tus datos serán usados únicamente con " +
                                        "fines de orientación vocacional, " +
                                        "seguimiento académico y mejora " +
                                        "del servicio.",
                            onCheckedChange = {
                                onAuthorizeDataChange(it)
                                formErrors = formErrors.copy(
                                    dataConsent = null,
                                )
                                pageError = null
                            },
                            error = formErrors.dataConsent,
                            enabled = !state.isLoading,
                        )

                        RegisterConsentBox(
                            checked = state.acceptTerms,
                            text =
                                "Declaro haber leído y aceptado las " +
                                        "Políticas de Tratamiento de Datos " +
                                        "Personales, así como los Términos " +
                                        "y Condiciones de la institución.",
                            onCheckedChange = {
                                onAcceptTermsChange(it)
                                formErrors = formErrors.copy(
                                    termsAccepted = null,
                                )
                                pageError = null
                            },
                            error = formErrors.termsAccepted,
                            enabled = !state.isLoading,
                        )

                        val visibleError =
                            pageError
                                ?: state.errorMessage
                                    ?.takeIf(String::isNotBlank)

                        if (visibleError != null) {
                            RegisterErrorBanner(
                                message = visibleError,
                            )
                        }

                        RegisterActions(
                            isLoading = state.isLoading,
                            onCancelClick = onBackToLoginClick,
                            onRegisterClick = {
                                val newErrors =
                                    validateRegisterForm(
                                        firstName =
                                            state.firstName,
                                        lastName =
                                            state.lastName,
                                        email =
                                            state.email,
                                        document =
                                            state.documentNumber,
                                        birthDateMillis =
                                            state.birthDateMillis,
                                        phone =
                                            state.phone,
                                        gender =
                                            state.gender,
                                        genderOther =
                                            state.genderOther,
                                        department =
                                            state.department,
                                        city =
                                            state.city,
                                        requiresAcademicData =
                                            state.belongsToUniversity ||
                                                    state.isActiveStudent,
                                        currentCareer =
                                            state.currentCareer,
                                        currentSemester =
                                            state.currentSemester,
                                        password =
                                            state.password,
                                        confirmPassword =
                                            state.confirmPassword,
                                        dataConsent =
                                            state.authorizeData,
                                        termsAccepted =
                                            state.acceptTerms,
                                    )

                                formErrors = newErrors

                                if (newErrors.hasErrors()) {
                                    pageError =
                                        if (
                                            newErrors.birthDate ==
                                            UNDER_AGE_MESSAGE
                                        ) {
                                            UNDER_AGE_MESSAGE
                                        } else {
                                            "Revisa los campos marcados " +
                                                    "antes de continuar."
                                        }
                                } else {
                                    pageError = null
                                    onRegisterClick()
                                }
                            },
                        )
                    }
                }
            }
        }

        UsbAppFooter()
    }
}

@Composable
private fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization =
        KeyboardCapitalization.None,
    visualTransformation: VisualTransformation =
        VisualTransformation.None,
    error: String? = null,
    enabled: Boolean = true,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        RegisterFieldLabel(text = label)

        Spacer(
            modifier = Modifier.height(8.dp),
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = enabled,
            singleLine = true,
            isError = error != null,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF8B8A84),
                    fontSize = 15.sp,
                )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = capitalization,
                keyboardType = keyboardType,
            ),
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(16.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp,
                color = RegisterBlack,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RegisterOrange,
                unfocusedBorderColor = RegisterBorder,
                errorBorderColor = RegisterError,
                focusedContainerColor = RegisterWhite,
                unfocusedContainerColor = RegisterWhite,
                disabledContainerColor = Color(0xFFF2F0EC),
                disabledBorderColor = RegisterBorder,
                disabledTextColor = RegisterMuted,
                cursorColor = RegisterOrange,
                errorCursorColor = RegisterError,
            ),
        )

        RegisterFieldError(error)
    }
}

@Composable
private fun RegisterDateField(
    value: String,
    error: String?,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        RegisterFieldLabel(
            text = "Fecha de nacimiento",
        )

        Spacer(
            modifier = Modifier.height(8.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (enabled) {
                        RegisterWhite
                    } else {
                        Color(0xFFF2F0EC)
                    },
                )
                .border(
                    width = 1.dp,
                    color =
                        if (error != null) {
                            RegisterError
                        } else {
                            RegisterBorder
                        },
                    shape = RoundedCornerShape(16.dp),
                )
                .clickable(
                    enabled = enabled,
                    onClick = onClick,
                )
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value.ifBlank { "día/mes/año" },
                modifier = Modifier.weight(1f),
                color =
                    if (value.isBlank()) {
                        Color(0xFF8B8A84)
                    } else {
                        RegisterBlack
                    },
                fontSize = 15.sp,
            )

            Text(
                text = "▾",
                color = RegisterMuted,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        RegisterFieldError(error)
    }
}

@Composable
private fun RegisterDropdownField(
    selectedValue: String,
    label: String,
    placeholder: String,
    options: List<String>,
    error: String?,
    enabled: Boolean,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        RegisterFieldLabel(text = label)

        Spacer(
            modifier = Modifier.height(8.dp),
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (enabled) {
                            RegisterWhite
                        } else {
                            Color(0xFFF2F0EC)
                        },
                    )
                    .border(
                        width = 1.dp,
                        color =
                            if (error != null) {
                                RegisterError
                            } else {
                                RegisterBorder
                            },
                        shape = RoundedCornerShape(16.dp),
                    )
                    .clickable(
                        enabled = enabled,
                        onClick = {
                            expanded = true
                        },
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedValue.ifBlank {
                        placeholder
                    },
                    modifier = Modifier.weight(1f),
                    color =
                        if (selectedValue.isBlank()) {
                            Color(0xFF8B8A84)
                        } else {
                            RegisterBlack
                        },
                    fontSize = 15.sp,
                )

                Text(
                    text = "▾",
                    color = RegisterMuted,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .background(RegisterWhite),
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = RegisterBlack,
                                fontSize = 14.sp,
                            )
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                    )
                }
            }
        }

        RegisterFieldError(error)
    }
}

@Composable
private fun RegisterFieldLabel(
    text: String,
) {
    Text(
        text = text,
        color = Color(0xFF2A2A27),
        fontSize = 14.5.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun RegisterFieldError(
    error: String?,
) {
    if (error != null) {
        Spacer(
            modifier = Modifier.height(5.dp),
        )

        Text(
            text = error,
            color = RegisterError,
            fontSize = 12.sp,
            lineHeight = 17.sp,
        )
    }
}

@Composable
private fun RegisterCheckLine(
    checked: Boolean,
    text: String,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = {
                    onCheckedChange(!checked)
                },
            ),
        verticalAlignment = Alignment.Top,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier.size(24.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = RegisterOrange,
                uncheckedColor = Color(0xFF858581),
                checkmarkColor = RegisterWhite,
            ),
        )

        Spacer(
            modifier = Modifier.width(10.dp),
        )

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = Color(0xFF393935),
            fontSize = 13.5.sp,
            lineHeight = 19.sp,
        )
    }
}

@Composable
private fun RegisterConsentBox(
    checked: Boolean,
    text: String,
    onCheckedChange: (Boolean) -> Unit,
    error: String?,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(RegisterSoftBackground)
                .clickable(
                    enabled = enabled,
                    onClick = {
                        onCheckedChange(!checked)
                    },
                )
                .padding(
                    horizontal = 14.dp,
                    vertical = 14.dp,
                ),
            verticalAlignment = Alignment.Top,
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                modifier = Modifier.size(24.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = RegisterOrange,
                    uncheckedColor = Color(0xFF858581),
                    checkmarkColor = RegisterWhite,
                ),
            )

            Spacer(
                modifier = Modifier.width(10.dp),
            )

            Text(
                text = text,
                modifier = Modifier.weight(1f),
                color = Color(0xFF393935),
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
        }

        RegisterFieldError(error)
    }
}

@Composable
private fun RegisterActions(
    isLoading: Boolean,
    onCancelClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (maxWidth >= 320.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(14.dp),
            ) {
                RegisterSecondaryButton(
                    text = "Cancelar",
                    onClick = onCancelClick,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f),
                )

                RegisterPrimaryButton(
                    text =
                        if (isLoading) {
                            "Registrando..."
                        } else {
                            "Registrarse"
                        },
                    onClick = onRegisterClick,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp),
            ) {
                RegisterPrimaryButton(
                    text =
                        if (isLoading) {
                            "Registrando..."
                        } else {
                            "Registrarse"
                        },
                    onClick = onRegisterClick,
                    enabled = !isLoading,
                )

                RegisterSecondaryButton(
                    text = "Cancelar",
                    onClick = onCancelClick,
                    enabled = !isLoading,
                )
            }
        }
    }
}

@Composable
private fun RegisterPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(100.dp),
        contentPadding = PaddingValues(
            horizontal = 24.dp,
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = RegisterOrange,
            contentColor = RegisterWhite,
            disabledContainerColor =
                RegisterOrange.copy(alpha = 0.55f),
            disabledContentColor =
                RegisterWhite.copy(alpha = 0.85f),
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
        ),
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun RegisterSecondaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(100.dp),
        border = BorderStroke(
            width = 1.dp,
            color = RegisterBorder,
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = RegisterWhite,
            contentColor = RegisterOrange,
            disabledContentColor =
                RegisterOrange.copy(alpha = 0.45f),
        ),
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun RegisterErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = RegisterError.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp),
            )
            .border(
                width = 1.dp,
                color = RegisterError.copy(alpha = 0.28f),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(
                horizontal = 14.dp,
                vertical = 12.dp,
            ),
    ) {
        Text(
            text = message,
            color = RegisterError,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

private data class RegisterFormErrors(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val document: String? = null,
    val birthDate: String? = null,
    val phone: String? = null,
    val gender: String? = null,
    val genderOther: String? = null,
    val department: String? = null,
    val city: String? = null,
    val currentCareer: String? = null,
    val currentSemester: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null,
    val dataConsent: String? = null,
    val termsAccepted: String? = null,
) {
    fun hasErrors(): Boolean =
        listOf(
            firstName,
            lastName,
            email,
            document,
            birthDate,
            phone,
            gender,
            genderOther,
            department,
            city,
            currentCareer,
            currentSemester,
            password,
            confirmPassword,
            dataConsent,
            termsAccepted,
        ).any { it != null }
}

private fun validateRegisterForm(
    firstName: String,
    lastName: String,
    email: String,
    document: String,
    birthDateMillis: Long?,
    phone: String,
    gender: String,
    genderOther: String,
    department: String,
    city: String,
    requiresAcademicData: Boolean,
    currentCareer: String,
    currentSemester: String,
    password: String,
    confirmPassword: String,
    dataConsent: Boolean,
    termsAccepted: Boolean,
): RegisterFormErrors =
    RegisterFormErrors(
        firstName =
            if (firstName.trim().length < 2) {
                "Ingresa el nombre."
            } else {
                null
            },
        lastName =
            if (lastName.trim().length < 2) {
                "Ingresa los apellidos."
            } else {
                null
            },
        email =
            if (
                !Patterns.EMAIL_ADDRESS
                    .matcher(email.trim())
                    .matches()
            ) {
                "Ingresa un correo válido."
            } else {
                null
            },
        document =
            if (document.trim().length < 6) {
                "Ingresa un documento válido."
            } else {
                null
            },
        birthDate = validateBirthDate(birthDateMillis),
        phone =
            if (phone.trim().length < 7) {
                "Ingresa un teléfono válido."
            } else {
                null
            },
        gender =
            if (gender.isBlank()) {
                "Selecciona un género."
            } else {
                null
            },
        genderOther =
            if (
                gender == "Otro" &&
                genderOther.isBlank()
            ) {
                "Describe el género seleccionado."
            } else {
                null
            },
        department =
            if (department.isBlank()) {
                "Selecciona un departamento."
            } else {
                null
            },
        city =
            if (city.isBlank()) {
                "Selecciona una ciudad."
            } else {
                null
            },
        currentCareer =
            if (
                requiresAcademicData &&
                currentCareer.isBlank()
            ) {
                "Indica la carrera actual."
            } else {
                null
            },
        currentSemester =
            if (
                requiresAcademicData &&
                currentSemester.isBlank()
            ) {
                "Selecciona el semestre actual."
            } else {
                null
            },
        password =
            when {
                password.isBlank() ->
                    "Ingresa una contraseña."

                password.length < 8 ->
                    "La contraseña debe tener mínimo 8 caracteres."

                else -> null
            },
        confirmPassword =
            when {
                confirmPassword.isBlank() ->
                    "Confirma tu contraseña."

                password != confirmPassword ->
                    "Las contraseñas no coinciden."

                else -> null
            },
        dataConsent =
            if (!dataConsent) {
                "Debes confirmar que leíste el aviso " +
                        "sobre el uso de tus datos."
            } else {
                null
            },
        termsAccepted =
            if (!termsAccepted) {
                "Debes aceptar políticas y términos."
            } else {
                null
            },
    )

private fun validateBirthDate(
    dateMillis: Long?,
): String? {
    if (dateMillis == null) {
        return "Selecciona tu fecha de nacimiento."
    }

    val birthDate = normalizedCalendar(dateMillis)
    val today = normalizedCalendar(
        System.currentTimeMillis(),
    )

    if (birthDate.after(today)) {
        return "La fecha de nacimiento no puede ser futura."
    }

    val oldestAllowed =
        (today.clone() as Calendar).apply {
            add(Calendar.YEAR, -120)
        }

    if (birthDate.before(oldestAllowed)) {
        return "Ingresa una fecha de nacimiento válida."
    }

    val adultLimit =
        (today.clone() as Calendar).apply {
            add(Calendar.YEAR, -18)
        }

    return if (birthDate.after(adultLimit)) {
        UNDER_AGE_MESSAGE
    } else {
        null
    }
}

private fun showBirthDatePicker(
    context: Context,
    selectedDateMillis: Long?,
    onDateSelected: (Long) -> Unit,
) {
    val initialDate =
        selectedDateMillis
            ?.let(::normalizedCalendar)
            ?: normalizedCalendar(
                System.currentTimeMillis(),
            ).apply {
                add(Calendar.YEAR, -18)
            }

    val dialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate =
                Calendar.getInstance().apply {
                    clear()
                    set(
                        year,
                        month,
                        dayOfMonth,
                        0,
                        0,
                        0,
                    )
                }

            onDateSelected(
                selectedDate.timeInMillis,
            )
        },
        initialDate.get(Calendar.YEAR),
        initialDate.get(Calendar.MONTH),
        initialDate.get(Calendar.DAY_OF_MONTH),
    )

    val today = normalizedCalendar(
        System.currentTimeMillis(),
    )

    val oldestAllowed =
        (today.clone() as Calendar).apply {
            add(Calendar.YEAR, -120)
        }

    dialog.datePicker.maxDate = today.timeInMillis
    dialog.datePicker.minDate = oldestAllowed.timeInMillis
    dialog.show()
}

private fun normalizedCalendar(
    timeInMillis: Long,
): Calendar =
    Calendar.getInstance().apply {
        this.timeInMillis = timeInMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

private fun formatDate(
    timeInMillis: Long,
): String {
    val date = normalizedCalendar(timeInMillis)

    return String.format(
        Locale.getDefault(),
        "%02d/%02d/%04d",
        date.get(Calendar.DAY_OF_MONTH),
        date.get(Calendar.MONTH) + 1,
        date.get(Calendar.YEAR),
    )
}