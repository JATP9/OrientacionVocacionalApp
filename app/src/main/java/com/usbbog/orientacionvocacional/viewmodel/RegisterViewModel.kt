package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import com.usbbog.orientacionvocacional.ui.mobile.RegisterField
import com.usbbog.orientacionvocacional.ui.mobile.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())

    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFieldChange(field: RegisterField, value: String) {
        val currentState = _uiState.value

        _uiState.value = when (field) {
            RegisterField.FullName -> currentState.copy(
                fullName = value,
                errorMessage = null,
            )

            RegisterField.DocumentNumber -> currentState.copy(
                documentNumber = value,
                errorMessage = null,
            )

            RegisterField.Email -> currentState.copy(
                email = value,
                errorMessage = null,
            )

            RegisterField.Phone -> currentState.copy(
                phone = value,
                errorMessage = null,
            )

            RegisterField.Password -> currentState.copy(
                password = value,
                errorMessage = null,
            )

            RegisterField.ConfirmPassword -> currentState.copy(
                confirmPassword = value,
                errorMessage = null,
            )
        }
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

    /**
     * Valida los campos que actualmente forman parte de RegisterUiState.
     *
     * RegisterScreen valida antes los campos adicionales del formulario web,
     * incluida la fecha de nacimiento y la mayoría de edad.
     */
    fun register(): Boolean {
        val currentState = _uiState.value
        val fullName = currentState.fullName.trim()
        val documentNumber = currentState.documentNumber.trim()
        val email = currentState.email.trim()
        val phone = currentState.phone.trim()

        return when {
            fullName.isBlank() -> fail("Debes ingresar el nombre completo.")

            fullName.length < 3 -> fail(
                "El nombre debe tener al menos 3 caracteres.",
            )

            documentNumber.isBlank() -> fail(
                "Debes ingresar el número de documento.",
            )

            !documentNumber.all(Char::isDigit) -> fail(
                "El número de documento solo debe contener números.",
            )

            documentNumber.length < 6 -> fail(
                "Debes ingresar un número de documento válido.",
            )

            email.isBlank() -> fail(
                "Debes ingresar el correo electrónico.",
            )

            !isValidEmail(email) -> fail(
                "Debes ingresar un correo electrónico válido.",
            )

            phone.isBlank() -> fail(
                "Debes ingresar el número de teléfono.",
            )

            !phone.all(Char::isDigit) -> fail(
                "El teléfono solo debe contener números.",
            )

            phone.length < 7 -> fail(
                "Debes ingresar un teléfono válido.",
            )

            currentState.password.isBlank() -> fail(
                "Debes crear una contraseña.",
            )

            currentState.password.length < 8 -> fail(
                "La contraseña debe tener mínimo 8 caracteres.",
            )

            currentState.confirmPassword.isBlank() -> fail(
                "Debes confirmar la contraseña.",
            )

            currentState.password != currentState.confirmPassword -> fail(
                "Las contraseñas no coinciden.",
            )

            !currentState.acceptTerms -> fail(
                "Debes aceptar los términos y condiciones.",
            )

            !currentState.authorizeData -> fail(
                "Debes autorizar el tratamiento de datos personales.",
            )

            else -> {
                _uiState.value = currentState.copy(
                    fullName = fullName,
                    documentNumber = documentNumber,
                    email = email,
                    phone = phone,
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

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        )
        return emailRegex.matches(email)
    }
}