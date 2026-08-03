package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val email: String = "",
    val document: String = "",
    val isSubmitting: Boolean = false,
    val emailError: String? = null,
    val statusMessage: String? = null,
    val isSuccess: Boolean = false,
)

class ForgotPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())

    val uiState: StateFlow<ForgotPasswordUiState> =
        _uiState.asStateFlow()

    private var recoverJob: Job? = null

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            emailError = null,
            statusMessage = null,
            isSuccess = false,
        )
    }

    fun onDocumentChange(value: String) {
        _uiState.value = _uiState.value.copy(
            document = value,
            statusMessage = null,
            isSuccess = false,
        )
    }

    fun prefillEmail(value: String) {
        if (_uiState.value.email.isBlank() && value.isNotBlank()) {
            _uiState.value = _uiState.value.copy(
                email = value.trim(),
                emailError = null,
            )
        }
    }

    /**
     * Valida el formulario y simula el envío mientras se conecta el backend.
     * Sustituye el bloque con delay por la llamada al repositorio de
     * autenticación cuando recoverPassword esté disponible en Android.
     */
    fun recoverPassword() {
        if (_uiState.value.isSubmitting) return

        val email = _uiState.value.email.trim()
        val document = _uiState.value.document.trim()

        when {
            email.isBlank() -> {
                _uiState.value = _uiState.value.copy(
                    emailError = "Debes ingresar el correo electrónico.",
                    statusMessage = null,
                    isSuccess = false,
                )
            }

            !isValidEmail(email) -> {
                _uiState.value = _uiState.value.copy(
                    emailError = "Ingresa un correo válido.",
                    statusMessage = null,
                    isSuccess = false,
                )
            }

            else -> {
                recoverJob?.cancel()
                recoverJob = viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(
                        email = email,
                        document = document,
                        isSubmitting = true,
                        emailError = null,
                        statusMessage = null,
                        isSuccess = false,
                    )

                    delay(800)

                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        statusMessage =
                            "Si los datos coinciden con una cuenta registrada, " +
                                    "recibirás un enlace para recuperar tu contraseña.",
                        isSuccess = true,
                    )
                }
            }
        }
    }

    fun reset() {
        recoverJob?.cancel()
        recoverJob = null
        _uiState.value = ForgotPasswordUiState()
    }

    private fun isValidEmail(email: String): Boolean {
        return Regex(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        ).matches(email)
    }
}