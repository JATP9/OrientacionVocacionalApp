package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import com.usbbog.orientacionvocacional.ui.mobile.RegisterField
import com.usbbog.orientacionvocacional.ui.mobile.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(RegisterUiState())

    val uiState: StateFlow<RegisterUiState> =
        _uiState.asStateFlow()

    fun onFieldChange(
        field: RegisterField,
        value: String
    ) {
        val currentState = _uiState.value

        _uiState.value = when (field) {

            RegisterField.FullName -> {
                currentState.copy(
                    fullName = value,
                    errorMessage = null
                )
            }

            RegisterField.DocumentNumber -> {
                currentState.copy(
                    documentNumber = value,
                    errorMessage = null
                )
            }

            RegisterField.Email -> {
                currentState.copy(
                    email = value,
                    errorMessage = null
                )
            }

            RegisterField.Phone -> {
                currentState.copy(
                    phone = value,
                    errorMessage = null
                )
            }

            RegisterField.Password -> {
                currentState.copy(
                    password = value,
                    errorMessage = null
                )
            }

            RegisterField.ConfirmPassword -> {
                currentState.copy(
                    confirmPassword = value,
                    errorMessage = null
                )
            }
        }
    }

    fun onAcceptTermsChange(accepted: Boolean) {
        _uiState.value = _uiState.value.copy(
            acceptTerms = accepted,
            errorMessage = null
        )
    }

    fun onAuthorizeDataChange(authorized: Boolean) {
        _uiState.value = _uiState.value.copy(
            authorizeData = authorized,
            errorMessage = null
        )
    }

    /**
     * Por ahora valida la información del formulario.
     *
     * Cuando se conecte el backend, después de estas
     * validaciones se llamará al repositorio.
     */
    fun register(): Boolean {

        val currentState = _uiState.value

        val fullName =
            currentState.fullName.trim()

        val documentNumber =
            currentState.documentNumber.trim()

        val email =
            currentState.email.trim()

        val phone =
            currentState.phone.trim()

        return when {

            fullName.isBlank() -> {
                showError(
                    "Debes ingresar el nombre completo."
                )
                false
            }

            fullName.length < 3 -> {
                showError(
                    "El nombre debe tener al menos 3 caracteres."
                )
                false
            }

            documentNumber.isBlank() -> {
                showError(
                    "Debes ingresar el número de documento."
                )
                false
            }

            !documentNumber.all { it.isDigit() } -> {
                showError(
                    "El número de documento solo debe contener números."
                )
                false
            }

            email.isBlank() -> {
                showError(
                    "Debes ingresar el correo electrónico."
                )
                false
            }

            !isValidEmail(email) -> {
                showError(
                    "Debes ingresar un correo electrónico válido."
                )
                false
            }

            phone.isBlank() -> {
                showError(
                    "Debes ingresar el número de teléfono."
                )
                false
            }

            !phone.all { it.isDigit() } -> {
                showError(
                    "El teléfono solo debe contener números."
                )
                false
            }

            phone.length < 7 -> {
                showError(
                    "Debes ingresar un teléfono válido."
                )
                false
            }

            currentState.password.isBlank() -> {
                showError(
                    "Debes crear una contraseña."
                )
                false
            }

            currentState.password.length < 6 -> {
                showError(
                    "La contraseña debe tener mínimo 6 caracteres."
                )
                false
            }

            currentState.confirmPassword.isBlank() -> {
                showError(
                    "Debes confirmar la contraseña."
                )
                false
            }

            currentState.password !=
                    currentState.confirmPassword -> {

                showError(
                    "Las contraseñas no coinciden."
                )
                false
            }

            !currentState.acceptTerms -> {
                showError(
                    "Debes aceptar los términos y condiciones."
                )
                false
            }

            !currentState.authorizeData -> {
                showError(
                    "Debes autorizar el tratamiento de datos personales."
                )
                false
            }

            else -> {
                _uiState.value = currentState.copy(
                    fullName = fullName,
                    documentNumber = documentNumber,
                    email = email,
                    phone = phone,
                    errorMessage = null
                )

                true
            }
        }
    }

    fun setLoading(loading: Boolean) {
        _uiState.value = _uiState.value.copy(
            isLoading = loading
        )
    }

    fun showError(message: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = message
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }

    fun resetForm() {
        _uiState.value = RegisterUiState()
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex =
            Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

        return emailRegex.matches(email)
    }
}