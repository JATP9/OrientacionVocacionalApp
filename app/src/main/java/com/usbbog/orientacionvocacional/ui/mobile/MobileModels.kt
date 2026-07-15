package com.usbbog.orientacionvocacional.ui.mobile

data class LandingStep(
    val number: Int,
    val title: String,
    val description: String,
)

data class RegisterUiState(
    val fullName: String = "",
    val documentNumber: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val acceptTerms: Boolean = false,
    val authorizeData: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

enum class RegisterField {
    FullName,
    DocumentNumber,
    Email,
    Phone,
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
)

data class AdminMetricUi(
    val label: String,
    val value: String,
    val supportingText: String,
)

data class AdminUserUi(
    val id: String,
    val fullName: String,
    val email: String,
    val role: String,
    val status: String,
)

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
data class TestUiState(
    val questions: List<QuestionUi> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val answers: Map<String, String> = emptyMap(),
    val remainingTime: String = "Sin límite",
    val errorMessage: String? = null
) {

    val currentQuestion: QuestionUi?
        get() = questions.getOrNull(currentQuestionIndex)

    val selectedOptionId: String?
        get() {
            val questionId = currentQuestion?.id ?: return null
            return answers[questionId]
        }

    val answeredQuestionNumbers: Set<Int>
        get() = questions.mapIndexedNotNull { index, question ->
            if (answers.containsKey(question.id)) {
                index + 1
            } else {
                null
            }
        }.toSet()

    val isLastQuestion: Boolean
        get() = currentQuestionIndex == questions.lastIndex
}

data class ResultsUiState(
    val mainArea: String = "",
    val summary: String = "",
    val scores: List<ResultScoreUi> = emptyList(),
    val careers: List<CareerResultUi> = emptyList(),
    val generatedAt: String = "",
    val isReady: Boolean = false
)