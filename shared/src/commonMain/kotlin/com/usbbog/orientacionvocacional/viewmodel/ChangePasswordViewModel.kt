package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usbbog.orientacionvocacional.data.remote.ApiException
import com.usbbog.orientacionvocacional.data.remote.VocationalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val statusMessage: String? = null,
    val isSuccess: Boolean = false,
    val isSubmitting: Boolean = false,
)

class ChangePasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun onCurrentPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            currentPassword = value,
            currentPasswordError = null,
            statusMessage = null,
            isSuccess = false,
        )
    }

    fun onNewPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            newPassword = value,
            newPasswordError = null,
            confirmPasswordError = null,
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

    fun changePassword(onSessionExpired: () -> Unit = {}) {
        val state = _uiState.value
        if (state.isSubmitting) return

        val currentPasswordError = if (state.currentPassword.isBlank()) {
            "Ingresa tu contraseña actual."
        } else {
            null
        }
        val newPasswordError = passwordValidationError(state.newPassword)
            ?: if (state.newPassword == state.currentPassword) {
                "La nueva contraseña debe ser diferente a la actual."
            } else {
                null
            }
        val confirmPasswordError = when {
            state.confirmPassword.isBlank() -> "Confirma tu nueva contraseña."
            state.confirmPassword != state.newPassword -> "Las contraseñas no coinciden."
            else -> null
        }

        if (
            currentPasswordError != null ||
            newPasswordError != null ||
            confirmPasswordError != null
        ) {
            _uiState.value = state.copy(
                currentPasswordError = currentPasswordError,
                newPasswordError = newPasswordError,
                confirmPasswordError = confirmPasswordError,
                statusMessage = null,
                isSuccess = false,
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(
                isSubmitting = true,
                currentPasswordError = null,
                newPasswordError = null,
                confirmPasswordError = null,
                statusMessage = null,
                isSuccess = false,
            )
            try {
                VocationalRepository.changePassword(
                    currentPassword = state.currentPassword,
                    newPassword = state.newPassword,
                )
                _uiState.value = ChangePasswordUiState(
                    statusMessage = "Tu contraseña fue actualizada correctamente.",
                    isSuccess = true,
                )
            } catch (error: ApiException) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    statusMessage = error.message,
                    isSuccess = false,
                )
                if (error.statusCode == 401) onSessionExpired()
            }
        }
    }

    fun clear() {
        _uiState.value = ChangePasswordUiState()
    }
}
