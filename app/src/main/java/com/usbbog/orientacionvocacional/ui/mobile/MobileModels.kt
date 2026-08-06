package com.usbbog.orientacionvocacional.ui.mobile

import kotlin.math.roundToInt

enum class UserRole(val label: String) {
    Student("Estudiante"),
    Admin("Administrador"),
}

enum class VocationalArea(val label: String) {
    Engineering("Ingeniería y tecnología"),
    Health("Salud y bienestar"),
    Business("Negocios y gestión"),
    Social("Ciencias sociales"),
    Arts("Arte y comunicación"),
}

data class LandingStep(
    val number: Int,
    val title: String,
    val description: String,
)

data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    val documentNumber: String = "",
    val birthDateMillis: Long? = null,
    val age: Int? = null,
    val email: String = "",
    val phone: String = "",
    val gender: String = "",
    val genderOther: String = "",
    val department: String = "",
    val city: String = "",
    val belongsToUniversity: Boolean = false,
    val isActiveStudent: Boolean = false,
    val currentCareer: String = "",
    val currentSemester: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val acceptTerms: Boolean = false,
    val authorizeData: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

enum class RegisterField {
    FirstName,
    LastName,
    FullName,
    DocumentNumber,
    Email,
    Phone,
    Gender,
    GenderOther,
    Department,
    City,
    CurrentCareer,
    CurrentSemester,
    Password,
    ConfirmPassword,
}

data class QuestionOptionUi(
    val id: String,
    val title: String,
    val description: String = "",
)

data class QuestionUi(
    val id: String,
    val statement: String,
    val helperText: String = "Selecciona la opción que mejor te represente.",
    val dimension: String = "Intereses y preferencias",
    val area: VocationalArea = VocationalArea.Engineering,
    val options: List<QuestionOptionUi>,
)

data class ResultScoreUi(
    val label: String,
    val percentage: Int,
)

data class CareerResultUi(
    val rank: Int,
    val name: String,
    val area: String,
    val description: String,
    val score: Int,
)

data class ProfileUiState(
    val initials: String,
    val fullName: String,
    val role: String,
    val email: String,
    val documentNumber: String,
    val phone: String,
    val city: String,
    val department: String = "",
    val age: Int? = null,
    val gender: String = "",
    val belongsToUniversity: Boolean = false,
    val currentCareer: String = "",
    val currentSemester: String = "",
)

data class AdminMetricUi(
    val label: String,
    val value: String,
    val supportingText: String,
    val change: String = "",
)

data class AdminUserUi(
    val id: String,
    val fullName: String,
    val email: String,
    val role: String,
    val status: String,
    val documentNumber: String = "",
    val city: String = "",
)

data class AdminResultUi(
    val id: String,
    val studentName: String,
    val city: String,
    val primaryArea: String,
    val topCareer: String,
    val affinity: Int,
    val completedAt: String,
)

data class GeographicDistributionUi(
    val region: String,
    val users: Int,
    val completedTests: Int,
)

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val authenticatedRole: UserRole? = null,
)

data class TestUiState(
    val attemptId: String? = null,
    val startedAtMillis: Long? = null,
    val elapsedSeconds: Int = 0,
    val isRunning: Boolean = false,
    val versionLabel: String = "Versión v1.1",
    val attemptLabel: String = "Intento de demostración",
    val audienceLabel: String = "Usuario interno",
    val questions: List<QuestionUi> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val answers: Map<String, String> = emptyMap(),
    val errorMessage: String? = null,
) {
    val currentQuestion: QuestionUi?
        get() = questions.getOrNull(currentQuestionIndex)

    val selectedOptionId: String?
        get() = currentQuestion?.let { answers[it.id] }

    val answeredQuestionNumbers: Set<Int>
        get() = questions.mapIndexedNotNull { index, question ->
            if (answers.containsKey(question.id)) index + 1 else null
        }.toSet()

    val unansweredQuestionNumbers: List<Int>
        get() = questions.mapIndexedNotNull { index, question ->
            if (answers.containsKey(question.id)) null else index + 1
        }

    val isLastQuestion: Boolean
        get() = currentQuestionIndex == questions.lastIndex

    val progressPercent: Int
        get() = if (questions.isEmpty()) {
            0
        } else {
            ((answers.size.toDouble() / questions.size) * 100).roundToInt()
        }

    val remainingTime: String
        get() = formatTestDuration(elapsedSeconds)
}

data class ResultsUiState(
    val resultId: String = "",
    val mainArea: String = "",
    val summary: String = "",
    val scores: List<ResultScoreUi> = emptyList(),
    val careers: List<CareerResultUi> = emptyList(),
    val generatedAt: String = "",
    val isReady: Boolean = false,
)

data class AdminUiState(
    val metrics: List<AdminMetricUi> = emptyList(),
    val users: List<AdminUserUi> = emptyList(),
    val recentResults: List<AdminResultUi> = emptyList(),
    val geographicDistribution: List<GeographicDistributionUi> = emptyList(),
    val internalUsers: Int = 796,
    val externalUsers: Int = 488,
    val statusMessage: String? = null,
)

fun formatTestDuration(totalSeconds: Int): String {
    val safeSeconds = totalSeconds.coerceAtLeast(0)
    val hours = safeSeconds / 3600
    val minutes = (safeSeconds % 3600) / 60
    val seconds = safeSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
