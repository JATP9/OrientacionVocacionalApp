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

data class CatalogOptionUi(
    val id: String,
    val label: String,
)

data class ProgramOptionUi(
    val id: Long,
    val label: String,
)

data class LandingStep(
    val number: Int,
    val title: String,
    val description: String,
)

data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    val username: String = "",
    val documentNumber: String = "",
    val birthDateMillis: Long? = null,
    val age: Int? = null,
    val email: String = "",
    val phone: String = "",
    val gender: String = "",
    val genderOther: String = "",
    val department: String = "",
    val departmentId: String? = null,
    val city: String = "",
    val cityId: String? = null,
    val institutionLinkedChoice: String = "No",
    val institutionRelationship: String = "",
    val currentCareer: String = "",
    val currentCareerId: Long? = null,
    val currentSemester: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val personalDataConsentAccepted: Boolean = false,
    val privacyPolicyAccepted: Boolean = false,
    val termsAccepted: Boolean = false,
    val adultConfirmed: Boolean = false,
    val departmentOptions: List<CatalogOptionUi> = emptyList(),
    val cityOptions: List<CatalogOptionUi> = emptyList(),
    val careerOptions: List<ProgramOptionUi> = emptyList(),
    val isCatalogLoading: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val isInstitutionLinked: Boolean
        get() = institutionLinkedChoice == "Sí"

    val requiresAcademicData: Boolean
        get() = isInstitutionLinked && institutionRelationship == "Estudiante"

    // Alias de solo lectura para las pantallas heredadas que aún viven en el proyecto.
    val belongsToUniversity: Boolean
        get() = isInstitutionLinked

    val isActiveStudent: Boolean
        get() = institutionRelationship == "Estudiante"

    val authorizeData: Boolean
        get() = personalDataConsentAccepted

    val acceptTerms: Boolean
        get() = termsAccepted
}

enum class RegisterField {
    FirstName,
    LastName,
    FullName,
    Username,
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
    val backendId: Long? = null,
    val code: String = id,
    val statement: String,
    val helperText: String = "Selecciona la opción que mejor te represente.",
    val dimension: String = "Intereses y preferencias",
    val area: VocationalArea = VocationalArea.Engineering,
    val areaName: String = area.label,
    val programName: String = "",
    val options: List<QuestionOptionUi>,
)

data class ResultScoreUi(
    val areaId: Long? = null,
    val label: String,
    val percentage: Int,
    val profile: String = "",
    val description: String = "",
    val logoPath: String? = null,
    val pachoPath: String? = null,
)

data class CareerResultUi(
    val rank: Int,
    val name: String,
    val area: String,
    val description: String,
    val score: Int,
    val url: String? = null,
    val logoPath: String? = null,
)

data class ProfileUiState(
    val initials: String,
    val fullName: String,
    val role: String,
    val email: String,
    val username: String = "",
    val documentNumber: String,
    val phone: String,
    val city: String,
    val department: String = "",
    val age: Int? = null,
    val gender: String = "",
    val belongsToUniversity: Boolean = false,
    val currentCareer: String = "",
    val currentSemester: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: String? = null,
    val genderOther: String = "",
    val departmentId: String? = null,
    val cityId: String? = null,
    val programId: Long? = null,
    val departmentOptions: List<CatalogOptionUi> = emptyList(),
    val cityOptions: List<CatalogOptionUi> = emptyList(),
    val programOptions: List<ProgramOptionUi> = emptyList(),
    val canDeleteAccount: Boolean = true,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val errorMessage: String? = null,
    val statusMessage: String? = null,
    val deleteAccountError: String? = null,
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
    val identifier: String = "",
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
    val isLoadingQuestions: Boolean = false,
    val versionLabel: String = "Versión v1.1",
    val attemptLabel: String = "Nuevo intento",
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
    val mainAreaId: Long? = null,
    val mainArea: String = "",
    val summary: String = "",
    val pdfSummary: String = "",
    val scores: List<ResultScoreUi> = emptyList(),
    val careers: List<CareerResultUi> = emptyList(),
    val generatedAt: String = "",
    val generatedAtRaw: String = "",
    val reportName: String = "",
    val reportUrl: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isReady: Boolean = false,
) {
    /** Versión textual estable para los exportadores nativos y accesibilidad. */
    val shareReportText: String
        get() = buildString {
            appendLine("ORIENTACIÓN VOCACIONAL USB")
            appendLine("Resultado de tu prueba vocacional")
            appendLine()
            appendLine("Fecha de generación: ${generatedAt.ifBlank { "No disponible" }}")
            appendLine("Nombre del informe: ${reportName.ifBlank { "Sin identificar" }}")
            appendLine()
            appendLine("ÁREA DE MAYOR AFINIDAD")
            appendLine(mainArea.ifBlank { "No disponible" })
            appendLine(pdfSummary.ifBlank { summary })
            appendLine()
            appendLine("AFINIDAD POR ÁREA")
            scores.forEach { score ->
                appendLine("• ${score.label}: ${score.percentage}%")
            }
            appendLine()
            appendLine("PROGRAMAS RECOMENDADOS")
            careers.forEach { career ->
                appendLine("${career.rank}. ${career.name} — ${career.area} (${career.score}%)")
            }
            appendLine()
            append("Documento de orientación, no vinculante.")
        }
}

data class ResultHistoryItemUi(
    val id: Long,
    val attemptNumber: Int,
    val completedAt: String,
    val duration: String,
    val version: String,
)

data class ResultsHistoryUiState(
    val items: List<ResultHistoryItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val openingResultId: Long? = null,
    val errorMessage: String? = null,
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
    return "${hours.twoDigits()}:${minutes.twoDigits()}:${seconds.twoDigits()}"
}

private fun Int.twoDigits(): String = toString().padStart(2, '0')
