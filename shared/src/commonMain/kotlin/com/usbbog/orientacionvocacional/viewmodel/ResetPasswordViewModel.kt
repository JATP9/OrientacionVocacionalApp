package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usbbog.orientacionvocacional.data.remote.ApiException
import com.usbbog.orientacionvocacional.data.remote.VocationalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ResetPasswordUiState(
    val token: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val statusMessage: String? = null,
    val isSuccess: Boolean = false,
    val isSubmitting: Boolean = false,
) {
    val hasValidToken: Boolean
        get() = token.isNotBlank()
}

class ResetPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun setToken(value: String?) {
        val normalized = value?.trim().orEmpty()
        if (_uiState.value.token == normalized) return
        _uiState.value = ResetPasswordUiState(token = normalized)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            passwordError = null,
            statusMessage = null,
            isSuccess = false,
        )
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = value,
            confirmPasswordError = null,
            statusMessage = null,
            isSuccess = false,
        )
    }

    fun resetPassword() {
        val state = _uiState.value
        if (state.isSubmitting || state.isSuccess) return

        val passwordError = passwordValidationError(state.password)
        val confirmationError = when {
            state.confirmPassword.isBlank() -> "Confirma tu nueva contraseña."
            state.password != state.confirmPassword -> "Las contraseñas no coinciden."
            else -> null
        }

        if (!state.hasValidToken || passwordError != null || confirmationError != null) {
            _uiState.value = state.copy(
                passwordError = passwordError,
                confirmPasswordError = confirmationError,
                statusMessage = if (!state.hasValidToken) {
                    "El enlace no es válido o llegó sin el token de recuperación."
                } else {
                    null
                },
                isSuccess = false,
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(
                isSubmitting = true,
                passwordError = null,
                confirmPasswordError = null,
                statusMessage = null,
                isSuccess = false,
            )
            try {
                val message = VocationalRepository.resetPassword(
                    token = state.token,
                    newPassword = state.password,
                )
                _uiState.value = _uiState.value.copy(
                    password = "",
                    confirmPassword = "",
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

    fun clear() {
        _uiState.value = ResetPasswordUiState()
    }
}
