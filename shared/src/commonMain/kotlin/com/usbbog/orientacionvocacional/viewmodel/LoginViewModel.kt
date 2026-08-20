package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usbbog.orientacionvocacional.data.remote.ApiException
import com.usbbog.orientacionvocacional.data.remote.VocationalRepository
import com.usbbog.orientacionvocacional.data.session.SessionStore
import com.usbbog.orientacionvocacional.ui.mobile.LoginUiState
import com.usbbog.orientacionvocacional.ui.mobile.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val restoredSession = SessionStore.session

    private val _uiState = MutableStateFlow(
        LoginUiState(
            identifier = restoredSession?.username.orEmpty(),
            rememberMe = restoredSession != null,
            authenticatedRole = restoredSession?.role?.toUserRole(),
        ),
    )

    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onIdentifierChange(identifier: String) {
        _uiState.value = _uiState.value.copy(identifier = identifier, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun onRememberChange(remember: Boolean) {
        _uiState.value = _uiState.value.copy(rememberMe = remember)
    }

    fun prefillIdentifier(identifier: String) {
        _uiState.value = _uiState.value.copy(
            identifier = identifier,
            password = "",
            errorMessage = null,
            authenticatedRole = null,
        )
    }

    fun adoptAuthenticatedSession(identifier: String, role: UserRole) {
        _uiState.value = _uiState.value.copy(
            identifier = identifier.trim(),
            password = "",
            isLoading = false,
            errorMessage = null,
            authenticatedRole = role,
        )
    }

    fun login(onSuccess: (UserRole) -> Unit) {
        if (_uiState.value.isLoading) return

        val current = _uiState.value
        val identifier = current.identifier.trim()
        val password = current.password

        when {
            identifier.isBlank() -> showError("Ingresa tu correo o nombre de usuario.")
            password.isBlank() -> showError("Debes ingresar la contraseña.")
            else -> viewModelScope.launch {
                _uiState.value = current.copy(
                    identifier = identifier,
                    isLoading = true,
                    errorMessage = null,
                )

                try {
                    val response = VocationalRepository.login(
                        username = identifier,
                        password = password,
                        remember = current.rememberMe,
                    )
                    val role = response.role.toUserRole()
                    _uiState.value = _uiState.value.copy(
                        password = "",
                        isLoading = false,
                        authenticatedRole = role,
                    )
                    onSuccess(role)
                } catch (error: ApiException) {
                    showError(error.message)
                }
            }
        }
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

    fun clearSession() {
        val current = _uiState.value
        VocationalRepository.logout()
        _uiState.value = LoginUiState(
            identifier = if (current.rememberMe) current.identifier else "",
            rememberMe = current.rememberMe,
        )
    }
}

internal fun String.toUserRole(): UserRole = when (trim().uppercase()) {
    "ROOT", "ADMIN", "ADMINISTRADOR" -> UserRole.Admin
    else -> UserRole.Student
}
