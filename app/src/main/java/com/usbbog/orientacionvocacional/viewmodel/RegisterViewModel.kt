package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import com.usbbog.orientacionvocacional.ui.mobile.RegisterField
import com.usbbog.orientacionvocacional.ui.mobile.RegisterUiState
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFieldChange(field: RegisterField, value: String) {
        val current = _uiState.value
        val updated = when (field) {
            RegisterField.FirstName -> current.copy(firstName = value)
            RegisterField.LastName -> current.copy(lastName = value)
            RegisterField.FullName -> current.copy(fullName = value)
            RegisterField.DocumentNumber -> current.copy(documentNumber = value)
            RegisterField.Email -> current.copy(email = value)
            RegisterField.Phone -> current.copy(phone = value)
            RegisterField.Gender -> current.copy(
                gender = value,
                genderOther = if (value == "Otro") current.genderOther else "",
            )
            RegisterField.GenderOther -> current.copy(genderOther = value)
            RegisterField.Department -> current.copy(department = value)
            RegisterField.City -> current.copy(city = value)
            RegisterField.CurrentCareer -> current.copy(currentCareer = value)
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
    }

    fun onBirthDateChange(value: Long) {
        _uiState.value = _uiState.value.copy(
            birthDateMillis = value,
            age = calculateAge(value),
            errorMessage = null,
        )
    }

    fun onBelongsToUniversityChange(value: Boolean) {
        val current = _uiState.value
        _uiState.value = current.copy(
            belongsToUniversity = value,
            currentCareer = if (!value && !current.isActiveStudent) "" else current.currentCareer,
            currentSemester = if (!value && !current.isActiveStudent) "" else current.currentSemester,
            errorMessage = null,
        )
    }

    fun onActiveStudentChange(value: Boolean) {
        val current = _uiState.value
        _uiState.value = current.copy(
            isActiveStudent = value,
            currentCareer = if (!value && !current.belongsToUniversity) "" else current.currentCareer,
            currentSemester = if (!value && !current.belongsToUniversity) "" else current.currentSemester,
            errorMessage = null,
        )
    }

    fun onAcceptTermsChange(accepted: Boolean) {
        _uiState.value = _uiState.value.copy(
            acceptTerms = accepted,
            errorMessage = null,
        )
    }

    fun onAuthorizeDataChange(authorized: Boolean) {
        _uiState.value = _uiState.value.copy(
            authorizeData = authorized,
            errorMessage = null,
        )
    }

    fun register(): Boolean {
        val state = _uiState.value
        val firstName = state.firstName.trim()
        val lastName = state.lastName.trim()
        val document = state.documentNumber.trim()
        val email = state.email.trim()
        val phone = state.phone.trim()
        val requiresAcademicData = state.belongsToUniversity || state.isActiveStudent

        return when {
            firstName.length < 2 -> fail("Debes ingresar un nombre válido.")
            lastName.length < 2 -> fail("Debes ingresar un apellido válido.")
            document.length < 6 || !document.all(Char::isDigit) ->
                fail("Debes ingresar un número de documento válido.")
            state.birthDateMillis == null -> fail("Debes seleccionar la fecha de nacimiento.")
            (state.age ?: 0) < 18 -> fail("Debes tener al menos 18 años para crear una cuenta.")
            !isValidEmail(email) -> fail("Debes ingresar un correo electrónico válido.")
            phone.length < 7 || !phone.all(Char::isDigit) ->
                fail("Debes ingresar un teléfono válido.")
            state.gender.isBlank() -> fail("Debes seleccionar un género.")
            state.gender == "Otro" && state.genderOther.isBlank() ->
                fail("Describe el género seleccionado.")
            state.department.isBlank() -> fail("Debes seleccionar un departamento.")
            state.city.isBlank() -> fail("Debes seleccionar una ciudad.")
            requiresAcademicData && state.currentCareer.isBlank() ->
                fail("Debes indicar la carrera actual.")
            requiresAcademicData && state.currentSemester.isBlank() ->
                fail("Debes seleccionar el semestre actual.")
            state.password.length < 8 ->
                fail("La contraseña debe tener mínimo 8 caracteres.")
            state.confirmPassword.isBlank() -> fail("Debes confirmar la contraseña.")
            state.password != state.confirmPassword -> fail("Las contraseñas no coinciden.")
            !state.authorizeData -> fail("Debes autorizar el tratamiento de datos personales.")
            !state.acceptTerms -> fail("Debes aceptar los términos y condiciones.")
            else -> {
                _uiState.value = state.copy(
                    firstName = firstName,
                    lastName = lastName,
                    fullName = buildFullName(firstName, lastName),
                    documentNumber = document,
                    email = email,
                    phone = phone,
                    genderOther = state.genderOther.trim(),
                    currentCareer = state.currentCareer.trim(),
                    errorMessage = null,
                )
                true
            }
        }
    }

    fun setLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = loading)
    }

    fun showError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = message,
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetForm() {
        _uiState.value = RegisterUiState()
    }

    private fun fail(message: String): Boolean {
        showError(message)
        return false
    }

    private fun isValidEmail(email: String): Boolean = Regex(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
    ).matches(email)

    private fun buildFullName(firstName: String, lastName: String): String =
        listOf(firstName.trim(), lastName.trim())
            .filter(String::isNotBlank)
            .joinToString(" ")

    private fun calculateAge(birthDateMillis: Long): Int {
        val birthDate = Calendar.getInstance().apply { timeInMillis = birthDateMillis }
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
        if (
            today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)
        ) {
            age--
        }
        return age
    }
}
