package com.usbbog.orientacionvocacional.screens

import android.app.DatePickerDialog
import android.content.Context
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalUriHandler
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
import com.usbbog.orientacionvocacional.viewmodel.GENDER_OTHER_MAX_LENGTH
import com.usbbog.orientacionvocacional.viewmodel.USERNAME_MAX_LENGTH
import com.usbbog.orientacionvocacional.viewmodel.birthDateValidationError
import com.usbbog.orientacionvocacional.viewmodel.isValidEmail
import com.usbbog.orientacionvocacional.viewmodel.isValidPhone
import com.usbbog.orientacionvocacional.viewmodel.passwordRequirementText
import com.usbbog.orientacionvocacional.viewmodel.passwordValidationError
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

private const val UNDER_AGE_MESSAGE = "Debes ser mayor de edad."

private val genderOptions = listOf(
    "Masculino",
    "Femenino",
    "Prefiero no decirlo",
    "Otro",
)

private val semesterOptions = (1..10).map(Int::toString)

/**
 * Registro móvil basado en la pantalla web.
 *
 * Los campos se mantienen en RegisterUiState, usan los catálogos del backend y
 * se validan antes de crear la cuenta.
 */
@Composable
fun RegisterWebScreenV2(
    state: RegisterUiState,
    onFieldChange: (RegisterField, String) -> Unit,
    onBirthDateChange: (Long) -> Unit,
    onInstitutionLinkedChoiceChange: (String) -> Unit,
    onInstitutionRelationshipChange: (String) -> Unit,
    onPersonalDataConsentChange: (Boolean) -> Unit,
    onPrivacyPolicyChange: (Boolean) -> Unit,
    onTermsChange: (Boolean) -> Unit,
    onAdultConfirmedChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var formErrors by remember { mutableStateOf(RegisterFormErrors()) }
    var pageError by rememberSaveable { mutableStateOf<String?>(null) }
    var adultDialogOpen by rememberSaveable { mutableStateOf(false) }
    var adultCheck by rememberSaveable { mutableStateOf(false) }

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
                            value = state.username,
                            onValueChange = {
                                onFieldChange(RegisterField.Username, it)
                                formErrors = formErrors.copy(username = null)
                                pageError = null
                            },
                            label = "Nombre de usuario",
                            placeholder = "Ingresa tu nombre de usuario",
                            error = formErrors.username,
                            enabled = !state.isLoading,
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
                                    it,
                                )
                                formErrors = formErrors.copy(
                                    document = null,
                                )
                                pageError = null
                            },
                            label = "Identificación",
                            placeholder = "xxxxxxxxxxx",
                            keyboardType = KeyboardType.Text,
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
                                        birthDateValidationError(selectedDate)

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
                                    it.filter { character ->
                                        character.isDigit() || character == '+' || character == ' '
                                    },
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

                        if (state.gender == "Otro") {
                            RegisterTextField(
                                value = state.genderOther,
                                onValueChange = {
                                    onFieldChange(
                                        RegisterField.GenderOther,
                                        it,
                                    )
                                    formErrors = formErrors.copy(genderOther = null)
                                    pageError = null
                                },
                                label = "En caso de otro ¿cuál?",
                                placeholder = "Indica tu identidad de género",
                                error = formErrors.genderOther,
                                enabled = !state.isLoading,
                                capitalization = KeyboardCapitalization.Sentences,
                            )
                        }

                        RegisterDropdownField(
                            selectedValue = state.department,
                            label = "Departamento",
                            placeholder = "-",
                            options = state.departmentOptions.map { it.label },
                            error = formErrors.department,
                            enabled = !state.isLoading && !state.isCatalogLoading,
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
                            options = state.cityOptions.map { it.label },
                            error = formErrors.city,
                            enabled = !state.isLoading &&
                                !state.isCatalogLoading &&
                                state.departmentId != null,
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

                        Text(
                            text = passwordRequirementText(),
                            color = RegisterMuted,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
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

                        RegisterDropdownField(
                            selectedValue = state.institutionLinkedChoice,
                            label = "¿Estás vinculado a la Universidad de San Buenaventura?",
                            placeholder = "Selecciona una opción",
                            options = listOf("Sí", "No"),
                            error = formErrors.institutionLinked,
                            enabled = !state.isLoading,
                            onOptionSelected = {
                                onInstitutionLinkedChoiceChange(it)
                                formErrors = formErrors.copy(
                                    institutionLinked = null,
                                    institutionRelationship = null,
                                    currentCareer = null,
                                    currentSemester = null,
                                )
                                pageError = null
                            },
                        )

                        if (state.isInstitutionLinked) {
                            RegisterDropdownField(
                                selectedValue = state.institutionRelationship,
                                label = "Tipo de vinculación",
                                placeholder = "Selecciona una opción",
                                options = listOf("Inscrito", "Estudiante"),
                                error = formErrors.institutionRelationship,
                                enabled = !state.isLoading,
                                onOptionSelected = {
                                    onInstitutionRelationshipChange(it)
                                    formErrors = formErrors.copy(
                                        institutionRelationship = null,
                                        currentCareer = null,
                                        currentSemester = null,
                                    )
                                    pageError = null
                                },
                            )
                        }

                        if (state.requiresAcademicData) {
                            RegisterDropdownField(
                                selectedValue = state.currentCareer,
                                label = "Programa actual",
                                placeholder = "Programa académico",
                                options = state.careerOptions.map { it.label },
                                error = formErrors.currentCareer,
                                enabled = !state.isLoading && !state.isCatalogLoading,
                                onOptionSelected = {
                                    onFieldChange(
                                        RegisterField.CurrentCareer,
                                        it,
                                    )
                                    formErrors = formErrors.copy(
                                        currentCareer = null,
                                    )
                                    pageError = null
                                },
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
                            checked = state.personalDataConsentAccepted,
                            text =
                                "Tus datos serán usados únicamente con fines de orientación " +
                                    "vocacional, seguimiento académico y mejora del servicio.",
                            onCheckedChange = {
                                onPersonalDataConsentChange(it)
                                formErrors = formErrors.copy(personalDataConsent = null)
                                pageError = null
                            },
                            error = formErrors.personalDataConsent,
                            enabled = !state.isLoading,
                        )

                        RegisterConsentBox(
                            checked = state.privacyPolicyAccepted,
                            text = "Acepto las Políticas de uso y privacidad.",
                            onCheckedChange = {
                                onPrivacyPolicyChange(it)
                                formErrors = formErrors.copy(privacyPolicy = null)
                                pageError = null
                            },
                            error = formErrors.privacyPolicy,
                            enabled = !state.isLoading,
                            linkLabel = "Consultar políticas",
                            onLinkClick = {
                                runCatching {
                                    uriHandler.openUri(
                                        "https://www.usbbog.edu.co/politicas-de-uso-y-privacidad/",
                                    )
                                }
                            },
                        )

                        RegisterConsentBox(
                            checked = state.termsAccepted,
                            text = "Acepto los Términos y Condiciones.",
                            onCheckedChange = {
                                onTermsChange(it)
                                formErrors = formErrors.copy(termsAccepted = null)
                                pageError = null
                            },
                            error = formErrors.termsAccepted,
                            enabled = !state.isLoading,
                            linkLabel = "Consultar términos",
                            onLinkClick = {
                                runCatching {
                                    uriHandler.openUri(
                                        "https://www.usbbog.edu.co/politicas-de-uso-y-privacidad/",
                                    )
                                }
                            },
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
                                        username =
                                            state.username,
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
                                        institutionLinkedChoice =
                                            state.institutionLinkedChoice,
                                        institutionRelationship =
                                            state.institutionRelationship,
                                        requiresAcademicData =
                                            state.requiresAcademicData,
                                        currentCareer =
                                            state.currentCareer,
                                        currentSemester =
                                            state.currentSemester,
                                        password =
                                            state.password,
                                        confirmPassword =
                                            state.confirmPassword,
                                        personalDataConsent =
                                            state.personalDataConsentAccepted,
                                        privacyPolicy =
                                            state.privacyPolicyAccepted,
                                        termsAccepted =
                                            state.termsAccepted,
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
                                    adultCheck = false
                                    onAdultConfirmedChange(false)
                                    adultDialogOpen = true
                                }
                            },
                        )
                    }
                }
            }
        }

        UsbAppFooter()
    }

    if (adultDialogOpen) {
        AdultConfirmationDialog(
            checked = adultCheck,
            isLoading = state.isLoading,
            onCheckedChange = { adultCheck = it },
            onDismiss = {
                adultDialogOpen = false
                adultCheck = false
                onAdultConfirmedChange(false)
            },
            onConfirm = {
                onAdultConfirmedChange(true)
                adultDialogOpen = false
                onRegisterClick()
            },
        )
    }
}

@Composable
private fun AdultConfirmationDialog(
    checked: Boolean,
    isLoading: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Confirmación de mayoría de edad",
                color = RegisterBlack,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Para completar el registro debes confirmar que eres mayor de 18 años.",
                    color = RegisterMuted,
                    lineHeight = 20.sp,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isLoading) { onCheckedChange(!checked) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        enabled = !isLoading,
                        colors = CheckboxDefaults.colors(checkedColor = RegisterOrange),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Confirmo que soy mayor de 18 años.",
                        color = RegisterBlack,
                        fontSize = 14.sp,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = checked && !isLoading,
            ) {
                Text("Confirmar y registrarme", color = RegisterOrange)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancelar", color = RegisterMuted)
            }
        },
        containerColor = RegisterWhite,
    )
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
    linkLabel: String? = null,
    onLinkClick: (() -> Unit)? = null,
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

        if (linkLabel != null && onLinkClick != null) {
            TextButton(
                onClick = onLinkClick,
                enabled = enabled,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
            ) {
                Text(
                    text = linkLabel,
                    color = RegisterOrange,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
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
    val username: String? = null,
    val email: String? = null,
    val document: String? = null,
    val birthDate: String? = null,
    val phone: String? = null,
    val gender: String? = null,
    val genderOther: String? = null,
    val department: String? = null,
    val city: String? = null,
    val institutionLinked: String? = null,
    val institutionRelationship: String? = null,
    val currentCareer: String? = null,
    val currentSemester: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null,
    val personalDataConsent: String? = null,
    val privacyPolicy: String? = null,
    val termsAccepted: String? = null,
) {
    fun hasErrors(): Boolean =
        listOf(
            firstName,
            lastName,
            username,
            email,
            document,
            birthDate,
            phone,
            gender,
            genderOther,
            department,
            city,
            institutionLinked,
            institutionRelationship,
            currentCareer,
            currentSemester,
            password,
            confirmPassword,
            personalDataConsent,
            privacyPolicy,
            termsAccepted,
        ).any { it != null }
}

private fun validateRegisterForm(
    firstName: String,
    lastName: String,
    username: String,
    email: String,
    document: String,
    birthDateMillis: Long?,
    phone: String,
    gender: String,
    genderOther: String,
    department: String,
    city: String,
    institutionLinkedChoice: String,
    institutionRelationship: String,
    requiresAcademicData: Boolean,
    currentCareer: String,
    currentSemester: String,
    password: String,
    confirmPassword: String,
    personalDataConsent: Boolean,
    privacyPolicy: Boolean,
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
        username = when {
            username.trim().isBlank() -> "Ingresa el nombre de usuario."
            username.trim().length > USERNAME_MAX_LENGTH ->
                "El nombre de usuario no puede superar $USERNAME_MAX_LENGTH caracteres."
            else -> null
        },
        email =
            if (!isValidEmail(email)) {
                "Ingresa un correo válido."
            } else {
                null
            },
        document =
            if (document.trim().length !in 6..30) {
                "Ingresa un documento válido."
            } else {
                null
            },
        birthDate = birthDateValidationError(birthDateMillis),
        phone =
            if (!isValidPhone(phone)) {
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
        genderOther = when {
            gender == "Otro" && genderOther.isBlank() ->
                "Indica otra identidad de género."
            genderOther.length > GENDER_OTHER_MAX_LENGTH ->
                "El detalle de género no puede superar $GENDER_OTHER_MAX_LENGTH caracteres."
            else -> null
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
        institutionLinked =
            if (institutionLinkedChoice !in listOf("Sí", "No")) {
                "Selecciona si estás vinculado a la universidad."
            } else {
                null
            },
        institutionRelationship =
            if (institutionLinkedChoice == "Sí" && institutionRelationship.isBlank()) {
                "Selecciona el tipo de vinculación."
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
        password = passwordValidationError(password),
        confirmPassword =
            when {
                confirmPassword.isBlank() ->
                    "Confirma tu contraseña."

                password != confirmPassword ->
                    "Las contraseñas no coinciden."

                else -> null
            },
        personalDataConsent =
            if (!personalDataConsent) {
                "Debes autorizar el tratamiento de datos personales."
            } else {
                null
            },
        privacyPolicy =
            if (!privacyPolicy) {
                "Debes aceptar las políticas de uso y privacidad."
            } else {
                null
            },
        termsAccepted =
            if (!termsAccepted) {
                "Debes aceptar los términos y condiciones."
            } else {
                null
            },
    )

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

    val adultLimit =
        (today.clone() as Calendar).apply {
            add(Calendar.YEAR, -18)
        }

    dialog.datePicker.maxDate = adultLimit.timeInMillis
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
