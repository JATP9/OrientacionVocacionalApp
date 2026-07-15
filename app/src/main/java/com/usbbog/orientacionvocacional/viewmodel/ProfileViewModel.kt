package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import com.usbbog.orientacionvocacional.ui.mobile.ProfileUiState
import com.usbbog.orientacionvocacional.ui.mobile.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {

    private val emptyProfile = ProfileUiState(
        initials = "U",
        fullName = "Usuario",
        role = "Aspirante",
        email = "",
        documentNumber = "",
        phone = "",
        city = "Bogotá"
    )

    private val _uiState = MutableStateFlow(emptyProfile)

    val uiState: StateFlow<ProfileUiState> =
        _uiState.asStateFlow()

    fun loadFromRegistration(
        registerState: RegisterUiState
    ) {
        val cleanName = registerState.fullName.trim()

        _uiState.value = ProfileUiState(
            initials = getInitials(cleanName),
            fullName = cleanName.ifBlank { "Usuario" },
            role = "Aspirante",
            email = registerState.email.trim(),
            documentNumber = registerState.documentNumber.trim(),
            phone = registerState.phone.trim(),
            city = "Bogotá"
        )
    }

    fun loadFromLogin(email: String) {
        val cleanEmail = email.trim()
        val currentProfile = _uiState.value

        /*
         * Si el perfil proviene de un registro realizado
         * durante esta sesión, conserva sus datos.
         */
        if (
            currentProfile.email.equals(
                cleanEmail,
                ignoreCase = true
            ) &&
            currentProfile.fullName != "Usuario"
        ) {
            return
        }

        val nameFromEmail = cleanEmail
            .substringBefore("@")
            .replace(".", " ")
            .replace("_", " ")
            .trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.replaceFirstChar { character ->
                    character.uppercase()
                }
            }

        _uiState.value = emptyProfile.copy(
            initials = getInitials(nameFromEmail),
            fullName = nameFromEmail.ifBlank { "Usuario" },
            email = cleanEmail
        )
    }

    fun clearProfile() {
        _uiState.value = emptyProfile
    }

    private fun getInitials(name: String): String {
        val words = name
            .trim()
            .split(" ")
            .filter { it.isNotBlank() }

        return when {
            words.isEmpty() -> "U"

            words.size == 1 -> {
                words.first()
                    .take(2)
                    .uppercase()
            }

            else -> {
                "${words.first().first()}${words.last().first()}"
                    .uppercase()
            }
        }
    }
}