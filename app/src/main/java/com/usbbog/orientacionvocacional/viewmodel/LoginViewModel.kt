package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import com.usbbog.orientacionvocacional.ui.mobile.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> =
        _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = null
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = null
        )
    }

    fun onRememberChange(remember: Boolean) {
        _uiState.value = _uiState.value.copy(
            rememberMe = remember
        )
    }

    fun prefillEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            password = "",
            errorMessage = null
        )
    }

    /**
     * Por ahora solo valida los campos.
     *
     * Cuando exista un backend, aquí se realizará la llamada
     * al repositorio para iniciar sesión.
     */
    fun login(): Boolean {

        val currentState = _uiState.value

        val email = currentState.email.trim()
        val password = currentState.password

        return when {

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

            password.isBlank() -> {
                showError(
                    "Debes ingresar la contraseña."
                )
                false
            }

            password.length < 6 -> {
                showError(
                    "La contraseña debe tener mínimo 6 caracteres."
                )
                false
            }

            else -> {
                _uiState.value = currentState.copy(
                    email = email,
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

    private fun isValidEmail(email: String): Boolean {
        val emailRegex =
            Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

        return emailRegex.matches(email)
    }

    fun clearSession() {
        val currentState = _uiState.value

        _uiState.value = LoginUiState(
            email = if (currentState.rememberMe) {
                currentState.email
            } else {
                ""
            },
            password = "",
            rememberMe = currentState.rememberMe,
            isLoading = false,
            errorMessage = null
        )
    }
}

