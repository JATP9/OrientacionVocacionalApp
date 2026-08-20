package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usbbog.orientacionvocacional.data.remote.ApiException
import com.usbbog.orientacionvocacional.data.remote.VocationalRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val email: String = "",
    val document: String = "",
    val isSubmitting: Boolean = false,
    val emailError: String? = null,
    val documentError: String? = null,
    val statusMessage: String? = null,
    val isSuccess: Boolean = false,
)

class ForgotPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()
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
            documentError = null,
            statusMessage = null,
            isSuccess = false,
        )
    }

    fun prefillEmail(value: String) {
        if (_uiState.value.email.isBlank() && value.isNotBlank()) {
            _uiState.value = _uiState.value.copy(email = value.trim(), emailError = null)
        }
    }

    fun recoverPassword() {
        if (_uiState.value.isSubmitting) return

        val email = _uiState.value.email.trim()
        val document = _uiState.value.document.trim()
        when {
            email.isBlank() -> showEmailError("Debes ingresar el correo electrónico.")
            !isValidEmail(email) -> showEmailError("Ingresa un correo válido.")
            document.length > 30 -> {
                _uiState.value = _uiState.value.copy(
                    documentError = "El documento no puede superar 30 caracteres.",
                    statusMessage = null,
                    isSuccess = false,
                )
            }
            else -> {
                recoverJob?.cancel()
                recoverJob = viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(
                        email = email,
                        isSubmitting = true,
                        emailError = null,
                        statusMessage = null,
                        isSuccess = false,
                    )

                    try {
                        val message = VocationalRepository.forgotPassword(email)
                        _uiState.value = _uiState.value.copy(
                            isSubmitting = false,
                            statusMessage = message,
                            isSuccess = true,
                        )
                    } catch (error: ApiException) {
                        _uiState.value = _uiState.value.copy(
                            isSubmitting = false,
                            statusMessage = error.message,
                            isSuccess = false,
                        )
                    }
                }
            }
        }
    }

    fun reset() {
        recoverJob?.cancel()
        recoverJob = null
        _uiState.value = ForgotPasswordUiState()
    }

    private fun showEmailError(message: String) {
        _uiState.value = _uiState.value.copy(
            emailError = message,
            statusMessage = null,
            isSuccess = false,
        )
    }

}
