package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usbbog.orientacionvocacional.data.remote.ApiException
import com.usbbog.orientacionvocacional.data.remote.DepartmentDto
import com.usbbog.orientacionvocacional.data.remote.MunicipalityDto
import com.usbbog.orientacionvocacional.data.remote.ProfileUpdatePayload
import com.usbbog.orientacionvocacional.data.remote.ProgramDto
import com.usbbog.orientacionvocacional.data.remote.UserResponseDto
import com.usbbog.orientacionvocacional.data.remote.VocationalRepository
import com.usbbog.orientacionvocacional.ui.mobile.CatalogOptionUi
import com.usbbog.orientacionvocacional.ui.mobile.ProfileUiState
import com.usbbog.orientacionvocacional.ui.mobile.ProgramOptionUi
import com.usbbog.orientacionvocacional.ui.mobile.RegisterUiState
import com.usbbog.orientacionvocacional.ui.mobile.UserRole
import com.usbbog.orientacionvocacional.platform.CivilDate
import com.usbbog.orientacionvocacional.platform.ageAt
import com.usbbog.orientacionvocacional.platform.todayUtc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val emptyProfile = ProfileUiState(
        initials = "U",
        fullName = "Usuario",
        role = "Aspirante",
        email = "",
        documentNumber = "",
        phone = "",
        city = "",
        department = "",
    )

    private val _uiState = MutableStateFlow(emptyProfile)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var currentRole = UserRole.Student
    private var departments = emptyList<DepartmentDto>()
    private var municipalities = emptyList<MunicipalityDto>()
    private var programs = emptyList<ProgramDto>()
    private var requestedDepartmentId: String? = null

    fun loadFromRegistration(registerState: RegisterUiState) {
        currentRole = UserRole.Student
        val cleanName = registerState.fullName.trim()
        _uiState.value = ProfileUiState(
            initials = getInitials(cleanName),
            fullName = cleanName.ifBlank { "Usuario" },
            role = UserRole.Student.label,
            email = registerState.email.trim(),
            username = registerState.username.trim(),
            documentNumber = registerState.documentNumber.trim(),
            phone = registerState.phone.trim(),
            city = registerState.city,
            department = registerState.department,
            age = registerState.age,
            gender = registerState.genderOther.takeIf { registerState.gender == "Otro" }
                ?: registerState.gender,
            belongsToUniversity = registerState.isInstitutionLinked,
            currentCareer = registerState.currentCareer,
            currentSemester = registerState.currentSemester,
            firstName = registerState.firstName.trim(),
            lastName = registerState.lastName.trim(),
            genderOther = registerState.genderOther.trim(),
            departmentId = registerState.departmentId,
            cityId = registerState.cityId,
            programId = registerState.currentCareerId,
            departmentOptions = registerState.departmentOptions,
            cityOptions = registerState.cityOptions,
            programOptions = registerState.careerOptions,
        )
    }

    fun loadFromLogin(
        identifier: String,
        role: UserRole = UserRole.Student,
        onSessionExpired: () -> Unit = {},
    ) {
        currentRole = role
        val cleanIdentifier = identifier.trim()
        _uiState.value = emptyProfile.copy(
            email = cleanIdentifier.takeIf(::isValidEmail).orEmpty(),
            username = cleanIdentifier.takeUnless(::isValidEmail).orEmpty(),
            role = role.label,
            isLoading = true,
        )
        loadProfile(onSessionExpired)
    }

    fun loadProfile(onSessionExpired: () -> Unit = {}) {
        if (_uiState.value.isLoading && _uiState.value.fullName != "Usuario") return
        requestedDepartmentId = null

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val user = VocationalRepository.profile()
                departments = try {
                    VocationalRepository.departments()
                } catch (_: ApiException) {
                    emptyList()
                }
                programs = try {
                    VocationalRepository.programs()
                } catch (_: ApiException) {
                    emptyList()
                }
                val departmentId = user.departmentId
                municipalities = if (departmentId.isNullOrBlank()) {
                    emptyList()
                } else {
                    try {
                        VocationalRepository.municipalities(departmentId)
                    } catch (_: ApiException) {
                        emptyList()
                    }
                }
                _uiState.value = user.toUiState(currentRole)
            } catch (error: ApiException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message,
                )
                if (error.statusCode == 401) onSessionExpired()
            }
        }
    }

    fun updateProfile(
        firstName: String,
        lastName: String,
        phone: String,
        city: String,
        department: String,
        currentCareer: String,
        currentSemester: String,
    ): Boolean {
        val previous = _uiState.value
        if (previous.isSaving) return false

        val cleanFirstName = firstName.trim()
        val cleanLastName = lastName.trim()
        val fullName = listOf(cleanFirstName, cleanLastName).joinToString(" ")
        val cleanPhone = phone.trim()
        if (
            cleanFirstName.length < 2 ||
            cleanLastName.length < 2 ||
            !isValidPhone(cleanPhone)
        ) {
            return false
        }

        val departmentId = resolveDepartmentId(department.trim(), previous) ?: return false
        val cityId = resolveMunicipalityId(city.trim(), previous, departmentId) ?: return false
        val cleanCareer = currentCareer.trim()
        val programId = if (previous.belongsToUniversity) {
            resolveProgramId(cleanCareer, previous) ?: return false
        } else {
            previous.programId
        }
        val semester = if (previous.belongsToUniversity) {
            currentSemester.toIntOrNull()?.takeIf { it in 1..10 } ?: return false
        } else {
            previous.currentSemester.toIntOrNull()
        }

        val payload = ProfileUpdatePayload(
            programId = programId,
            firstName = cleanFirstName,
            lastName = cleanLastName,
            phone = cleanPhone,
            gender = if (previous.genderOther.isNotBlank()) {
                "Otro"
            } else {
                previous.gender.takeIf(String::isNotBlank)
            },
            genderOther = previous.genderOther.takeIf(String::isNotBlank),
            departmentId = departmentId,
            municipalityId = cityId,
            semester = semester,
        )

        _uiState.value = previous.copy(
            initials = getInitials(fullName),
            fullName = fullName,
            phone = cleanPhone,
            city = city.trim(),
            department = department.trim(),
            firstName = payload.firstName,
            lastName = payload.lastName,
            departmentId = departmentId,
            cityId = cityId,
            belongsToUniversity = programId != null,
            currentCareer = if (programId != null) cleanCareer else "",
            currentSemester = semester?.toString().orEmpty(),
            programId = programId,
            isSaving = true,
            errorMessage = null,
            statusMessage = null,
        )

        viewModelScope.launch {
            try {
                val updated = VocationalRepository.updateProfile(payload)
                _uiState.value = updated.toUiState(currentRole).copy(
                    statusMessage = "Perfil actualizado correctamente.",
                )
            } catch (error: ApiException) {
                _uiState.value = previous.copy(
                    errorMessage = error.message,
                    statusMessage = null,
                )
            }
        }
        return true
    }

    fun onDepartmentSelectionChanged(department: String) {
        val departmentId = departments
            .firstOrNull { it.name.equals(department.trim(), ignoreCase = true) }
            ?.id
            ?: return
        requestedDepartmentId = departmentId

        viewModelScope.launch {
            try {
                val loaded = VocationalRepository.municipalities(departmentId)
                if (requestedDepartmentId != departmentId) return@launch
                municipalities = loaded
                _uiState.value = _uiState.value.copy(
                    cityOptions = loaded.map { CatalogOptionUi(it.id, it.name) },
                    errorMessage = null,
                )
            } catch (error: ApiException) {
                if (requestedDepartmentId != departmentId) return@launch
                _uiState.value = _uiState.value.copy(errorMessage = error.message)
            }
        }
    }

    fun deleteAccount(
        onDeleted: () -> Unit,
        onSessionExpired: () -> Unit = {},
    ) {
        val current = _uiState.value
        if (current.isDeletingAccount) return

        if (!current.canDeleteAccount) {
            _uiState.value = current.copy(
                deleteAccountError = "La cuenta ROOT no puede eliminar su propio perfil.",
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(
                isDeletingAccount = true,
                deleteAccountError = null,
            )
            try {
                VocationalRepository.deleteAccount()
                _uiState.value = _uiState.value.copy(isDeletingAccount = false)
                onDeleted()
            } catch (error: ApiException) {
                _uiState.value = _uiState.value.copy(
                    isDeletingAccount = false,
                    deleteAccountError = error.message,
                )
                if (error.statusCode == 401) onSessionExpired()
            }
        }
    }

    fun clearDeleteAccountError() {
        _uiState.value = _uiState.value.copy(deleteAccountError = null)
    }

    fun clearProfile() {
        currentRole = UserRole.Student
        departments = emptyList()
        municipalities = emptyList()
        programs = emptyList()
        requestedDepartmentId = null
        _uiState.value = emptyProfile
    }

    private fun UserResponseDto.toUiState(role: UserRole): ProfileUiState {
        val name = listOf(firstName, lastName).filter(String::isNotBlank).joinToString(" ")
        val departmentName = departments.firstOrNull { it.id == departmentId }?.name
            ?: departmentId.orEmpty()
        val cityName = municipalities.firstOrNull { it.id == municipalityId }?.name
            ?: municipalityId.orEmpty()
        val programName = programs.firstOrNull { it.id == programId }?.name
            ?: programId?.let { "Programa $it" }.orEmpty()

        return ProfileUiState(
            initials = getInitials(name),
            fullName = name.ifBlank { "Usuario" },
            role = role.label,
            email = email,
            username = username.orEmpty().ifBlank { _uiState.value.username },
            documentNumber = document,
            phone = phone.orEmpty(),
            city = cityName,
            department = departmentName,
            age = birthDate?.let(::calculateAge),
            gender = if (gender == "Otro") genderOther.orEmpty() else gender.orEmpty(),
            belongsToUniversity = programId != null,
            currentCareer = programName,
            currentSemester = semester?.toString().orEmpty(),
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            genderOther = genderOther.orEmpty(),
            departmentId = departmentId,
            cityId = municipalityId,
            programId = programId,
            departmentOptions = departments.map { CatalogOptionUi(it.id, it.name) },
            cityOptions = municipalities.map { CatalogOptionUi(it.id, it.name) },
            programOptions = programs
                .filter { !it.url.isNullOrBlank() }
                .map { ProgramOptionUi(it.id, it.name) },
            canDeleteAccount = roleId != 1L,
        )
    }

    private fun resolveDepartmentId(value: String, previous: ProfileUiState): String? = when {
        value.isBlank() -> null
        value.equals(previous.department, ignoreCase = true) -> previous.departmentId
        else -> departments.firstOrNull { it.name.equals(value, ignoreCase = true) }?.id
    }

    private fun resolveMunicipalityId(
        value: String,
        previous: ProfileUiState,
        departmentId: String,
    ): String? = when {
        value.isBlank() -> null
        value.equals(previous.city, ignoreCase = true) && departmentId == previous.departmentId ->
            previous.cityId
        else -> municipalities.firstOrNull {
            it.departmentId == departmentId && it.name.equals(value, ignoreCase = true)
        }?.id
    }

    private fun resolveProgramId(value: String, previous: ProfileUiState): Long? = when {
        value.isBlank() -> null
        value.equals(previous.currentCareer, ignoreCase = true) -> previous.programId
        else -> programs.firstOrNull {
            !it.url.isNullOrBlank() && it.name.equals(value, ignoreCase = true)
        }?.id
    }

    private fun calculateAge(value: String): Int? {
        val birthDate = CivilDate.parse(value) ?: return null
        return ageAt(birthDate, todayUtc())
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
