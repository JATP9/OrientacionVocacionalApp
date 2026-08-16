package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usbbog.orientacionvocacional.data.remote.ApiException
import com.usbbog.orientacionvocacional.data.remote.RegistrationPayload
import com.usbbog.orientacionvocacional.data.remote.VocationalRepository
import com.usbbog.orientacionvocacional.ui.mobile.CatalogOptionUi
import com.usbbog.orientacionvocacional.ui.mobile.ProgramOptionUi
import com.usbbog.orientacionvocacional.ui.mobile.RegisterField
import com.usbbog.orientacionvocacional.ui.mobile.RegisterUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun loadCatalogs() {
        val current = _uiState.value
        if (current.isCatalogLoading || current.departmentOptions.isNotEmpty()) return

        viewModelScope.launch {
            _uiState.value = current.copy(isCatalogLoading = true, errorMessage = null)
            try {
                val (departments, programs) = coroutineScope {
                    val departmentsRequest = async { VocationalRepository.departments() }
                    val programsRequest = async { VocationalRepository.programs() }
                    departmentsRequest.await() to programsRequest.await()
                }

                _uiState.value = _uiState.value.copy(
                    departmentOptions = departments.map { CatalogOptionUi(it.id, it.name) },
                    careerOptions = programs.map { ProgramOptionUi(it.id, it.name) },
                    isCatalogLoading = false,
                )
            } catch (error: ApiException) {
                _uiState.value = _uiState.value.copy(
                    isCatalogLoading = false,
                    errorMessage = error.message,
                )
            }
        }
    }

    fun onFieldChange(field: RegisterField, value: String) {
        val current = _uiState.value
        val updated = when (field) {
            RegisterField.FirstName -> current.copy(firstName = value)
            RegisterField.LastName -> current.copy(lastName = value)
            RegisterField.FullName -> current.copy(fullName = value)
            RegisterField.Username -> current.copy(username = value)
            RegisterField.DocumentNumber -> current.copy(documentNumber = value)
            RegisterField.Email -> current.copy(email = value)
            RegisterField.Phone -> current.copy(phone = value)
            RegisterField.Gender -> current.copy(
                gender = value,
                genderOther = if (value == "Otro") current.genderOther else "",
            )
            RegisterField.GenderOther -> current.copy(genderOther = value)
            RegisterField.Department -> current.copy(
                department = value,
                departmentId = current.departmentOptions
                    .firstOrNull { it.label == value }
                    ?.id,
                city = "",
                cityId = null,
                cityOptions = emptyList(),
            )
            RegisterField.City -> current.copy(
                city = value,
                cityId = current.cityOptions.firstOrNull { it.label == value }?.id,
            )
            RegisterField.CurrentCareer -> current.copy(
                currentCareer = value,
                currentCareerId = current.careerOptions.firstOrNull { it.label == value }?.id,
            )
            RegisterField.CurrentSemester -> current.copy(currentSemester = value)
            RegisterField.Password -> current.copy(password = value)
            RegisterField.ConfirmPassword -> current.copy(confirmPassword = value)
        }

        _uiState.value = updated.copy(
            fullName = when (field) {
                RegisterField.FirstName -> buildFullName(value, updated.lastName)
                RegisterField.LastName -> buildFullName(updated.firstName, value)
                else -> updated.fullName
            },
            errorMessage = null,
        )

        if (field == RegisterField.Department) {
            _uiState.value.departmentId?.let(::loadMunicipalities)
        }
    }

    fun onBirthDateChange(value: Long) {
        _uiState.value = _uiState.value.copy(
            birthDateMillis = value,
            age = calculateAge(value),
            errorMessage = null,
        )
    }

    fun onInstitutionLinkedChoiceChange(value: String) {
        val current = _uiState.value
        _uiState.value = current.copy(
            institutionLinkedChoice = value,
            institutionRelationship = if (value == "Sí") current.institutionRelationship else "",
            currentCareer = if (value == "Sí") current.currentCareer else "",
            currentCareerId = if (value == "Sí") current.currentCareerId else null,
            currentSemester = if (value == "Sí") current.currentSemester else "",
            errorMessage = null,
        )
    }

    fun onInstitutionRelationshipChange(value: String) {
        val current = _uiState.value
        val requiresAcademicData = value == "Estudiante"
        _uiState.value = current.copy(
            institutionRelationship = value,
            currentCareer = if (requiresAcademicData) current.currentCareer else "",
            currentCareerId = if (requiresAcademicData) current.currentCareerId else null,
            currentSemester = if (requiresAcademicData) current.currentSemester else "",
            errorMessage = null,
        )
    }

    fun onPersonalDataConsentChange(accepted: Boolean) {
        _uiState.value = _uiState.value.copy(
            personalDataConsentAccepted = accepted,
            errorMessage = null,
        )
    }

    fun onPrivacyPolicyChange(accepted: Boolean) {
        _uiState.value = _uiState.value.copy(
            privacyPolicyAccepted = accepted,
            errorMessage = null,
        )
    }

    fun onTermsChange(accepted: Boolean) {
        _uiState.value = _uiState.value.copy(termsAccepted = accepted, errorMessage = null)
    }

    fun onAdultConfirmedChange(confirmed: Boolean) {
        _uiState.value = _uiState.value.copy(adultConfirmed = confirmed, errorMessage = null)
    }

    fun register(
        onRegistered: (identifier: String, message: String) -> Unit,
    ) {
        if (_uiState.value.isLoading) return
        val state = _uiState.value
        if (!validate(state)) return

        val requiresAcademicData = state.requiresAcademicData
        val payload = RegistrationPayload(
            programId = if (requiresAcademicData) state.currentCareerId else null,
            firstName = state.firstName.trim(),
            lastName = state.lastName.trim(),
            document = state.documentNumber.trim(),
            email = normalizeEmail(state.email),
            username = state.username.trim(),
            phone = state.phone.trim().takeIf(String::isNotBlank),
            birthDate = state.birthDateMillis?.let(::formatBirthDate),
            gender = state.gender.takeIf(String::isNotBlank),
            genderOther = state.genderOther.trim()
                .takeIf { state.gender == "Otro" && it.isNotBlank() },
            departmentId = state.departmentId,
            municipalityId = state.cityId,
            semester = if (requiresAcademicData) state.currentSemester.toIntOrNull() else null,
            password = state.password,
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                VocationalRepository.register(payload)
                _uiState.value = _uiState.value.copy(
                    firstName = payload.firstName,
                    lastName = payload.lastName,
                    fullName = buildFullName(payload.firstName, payload.lastName),
                    documentNumber = payload.document,
                    email = payload.email,
                    phone = payload.phone.orEmpty(),
                    isLoading = false,
                )
                onRegistered(
                    payload.username,
                    "Tu cuenta fue creada correctamente. Inicia sesión para continuar.",
                )
            } catch (error: ApiException) {
                showError(error.message)
            }
        }
    }

    fun resetForm() {
        _uiState.value = RegisterUiState()
    }

    private fun loadMunicipalities(departmentId: String) {
        viewModelScope.launch {
            try {
                val municipalities = VocationalRepository.municipalities(departmentId)
                if (_uiState.value.departmentId != departmentId) return@launch
                _uiState.value = _uiState.value.copy(
                    cityOptions = municipalities.map { CatalogOptionUi(it.id, it.name) },
                )
            } catch (error: ApiException) {
                if (_uiState.value.departmentId != departmentId) return@launch
                _uiState.value = _uiState.value.copy(errorMessage = error.message)
            }
        }
    }

    private fun validate(state: RegisterUiState): Boolean {
        val firstName = state.firstName.trim()
        val lastName = state.lastName.trim()
        val username = state.username.trim()
        val document = state.documentNumber.trim()
        val email = state.email.trim()
        val phone = state.phone.trim()
        val requiresAcademicData = state.requiresAcademicData
        val semester = state.currentSemester.toIntOrNull()

        return when {
            firstName.length < 2 -> fail("Debes ingresar un nombre válido.")
            lastName.length < 2 -> fail("Debes ingresar un apellido válido.")
            username.isBlank() -> fail("Ingresa el nombre de usuario.")
            username.length > USERNAME_MAX_LENGTH ->
                fail("El nombre de usuario no puede superar $USERNAME_MAX_LENGTH caracteres.")
            document.length !in 6..30 -> fail("Debes ingresar un documento válido.")
            birthDateValidationError(state.birthDateMillis) != null ->
                fail(birthDateValidationError(state.birthDateMillis).orEmpty())
            !isValidEmail(email) -> fail("Debes ingresar un correo electrónico válido.")
            !isValidPhone(phone) ->
                fail("Debes ingresar un teléfono válido.")
            state.gender.isBlank() -> fail("Debes seleccionar un género.")
            state.gender == "Otro" && state.genderOther.isBlank() ->
                fail("Indica otra identidad de género.")
            state.genderOther.length > GENDER_OTHER_MAX_LENGTH ->
                fail("El detalle de género no puede superar $GENDER_OTHER_MAX_LENGTH caracteres.")
            state.departmentId == null -> fail("Debes seleccionar un departamento válido.")
            state.cityId == null -> fail("Debes seleccionar una ciudad válida.")
            state.isInstitutionLinked && state.institutionRelationship.isBlank() ->
                fail("Selecciona el tipo de vinculación.")
            requiresAcademicData && state.currentCareerId == null ->
                fail("Debes seleccionar el programa actual.")
            requiresAcademicData && (semester == null || semester !in 1..10) ->
                fail("Debes seleccionar el semestre actual.")
            passwordValidationError(state.password) != null ->
                fail(passwordValidationError(state.password).orEmpty())
            state.confirmPassword.isBlank() -> fail("Debes confirmar la contraseña.")
            state.password != state.confirmPassword ->
                fail("La confirmación debe coincidir exactamente con la contraseña.")
            !state.personalDataConsentAccepted ->
                fail("Debes autorizar el tratamiento de datos personales.")
            !state.privacyPolicyAccepted -> fail("Debes aceptar las políticas de uso y privacidad.")
            !state.termsAccepted -> fail("Debes aceptar los términos y condiciones.")
            !state.adultConfirmed -> fail("Debes confirmar que eres mayor de 18 años.")
            else -> true
        }
    }

    private fun showError(message: String) {
        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = message)
    }

    private fun fail(message: String): Boolean {
        showError(message)
        return false
    }

    private fun buildFullName(firstName: String, lastName: String): String =
        listOf(firstName.trim(), lastName.trim())
            .filter(String::isNotBlank)
            .joinToString(" ")

    private fun formatBirthDate(birthDateMillis: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Date(birthDateMillis))
}
