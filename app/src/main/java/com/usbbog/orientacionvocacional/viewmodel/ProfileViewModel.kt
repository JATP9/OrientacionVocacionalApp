package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import com.usbbog.orientacionvocacional.ui.mobile.ProfileUiState
import com.usbbog.orientacionvocacional.ui.mobile.RegisterUiState
import com.usbbog.orientacionvocacional.ui.mobile.UserRole
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
        city = "Bogotá D.C.",
        department = "Bogotá D.C.",
    )

    private val _uiState = MutableStateFlow(emptyProfile)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadFromRegistration(registerState: RegisterUiState) {
        val cleanName = registerState.fullName.trim()
        _uiState.value = ProfileUiState(
            initials = getInitials(cleanName),
            fullName = cleanName.ifBlank { "Usuario" },
            role = UserRole.Student.label,
            email = registerState.email.trim(),
            documentNumber = registerState.documentNumber.trim(),
            phone = registerState.phone.trim(),
            city = registerState.city,
            department = registerState.department,
            age = registerState.age,
            gender = registerState.genderOther.takeIf {
                registerState.gender == "Otro"
            } ?: registerState.gender,
            belongsToUniversity = registerState.belongsToUniversity || registerState.isActiveStudent,
            currentCareer = registerState.currentCareer,
            currentSemester = registerState.currentSemester,
        )
    }

    fun loadFromLogin(
        email: String,
        role: UserRole = UserRole.Student,
    ) {
        val cleanEmail = email.trim()
        val current = _uiState.value

        if (
            current.email.equals(cleanEmail, ignoreCase = true) &&
            current.fullName != "Usuario"
        ) {
            _uiState.value = current.copy(role = role.label)
            return
        }

        val inferredName = if (role == UserRole.Admin) {
            "Coordinación USB"
        } else {
            cleanEmail
                .substringBefore("@")
                .replace(".", " ")
                .replace("_", " ")
                .trim()
                .split(" ")
                .filter(String::isNotBlank)
                .joinToString(" ") { word ->
                    word.replaceFirstChar { it.uppercase() }
                }
        }

        _uiState.value = emptyProfile.copy(
            initials = getInitials(inferredName),
            fullName = inferredName.ifBlank { "Usuario" },
            role = role.label,
            email = cleanEmail,
        )
    }

    fun updateProfile(
        fullName: String,
        phone: String,
        city: String,
        department: String,
        currentCareer: String,
        currentSemester: String,
    ): Boolean {
        val cleanName = fullName.trim()
        val cleanPhone = phone.trim()

        if (cleanName.length < 3 || (cleanPhone.isNotBlank() && !cleanPhone.all(Char::isDigit))) {
            return false
        }

        _uiState.value = _uiState.value.copy(
            initials = getInitials(cleanName),
            fullName = cleanName,
            phone = cleanPhone,
            city = city.trim(),
            department = department.trim(),
            currentCareer = currentCareer.trim(),
            currentSemester = currentSemester.trim(),
        )
        return true
    }

    fun clearProfile() {
        _uiState.value = emptyProfile
    }

    private fun getInitials(name: String): String {
        val words = name.trim().split(" ").filter(String::isNotBlank)
        return when {
            words.isEmpty() -> "U"
            words.size == 1 -> words.first().take(2).uppercase()
            else -> "${words.first().first()}${words.last().first()}".uppercase()
        }
    }
}
