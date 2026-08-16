package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usbbog.orientacionvocacional.data.remote.ApiException
import com.usbbog.orientacionvocacional.data.remote.VocationalRepository
import com.usbbog.orientacionvocacional.ui.mobile.QuestionOptionUi
import com.usbbog.orientacionvocacional.ui.mobile.QuestionUi
import com.usbbog.orientacionvocacional.ui.mobile.TestUiState
import com.usbbog.orientacionvocacional.ui.mobile.VocationalArea
import java.text.Normalizer
import java.util.UUID
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TestViewModel : ViewModel() {

    private var timerJob: Job? = null

    private val _uiState = MutableStateFlow(TestUiState())
    val uiState: StateFlow<TestUiState> = _uiState.asStateFlow()

    fun startAttempt(
        audienceLabel: String = "Usuario interno",
        onReady: () -> Unit,
        onSessionExpired: () -> Unit = {},
    ) {
        if (_uiState.value.isLoadingQuestions) return

        timerJob?.cancel()
        _uiState.value = TestUiState(
            isLoadingQuestions = true,
            audienceLabel = audienceLabel,
        )

        viewModelScope.launch {
            try {
                val questions = VocationalRepository.questions().map { question ->
                    QuestionUi(
                        id = question.id.toString(),
                        backendId = question.id,
                        code = question.code,
                        statement = question.statement,
                        dimension = question.programName,
                        area = question.areaName.toVocationalArea(),
                        areaName = question.areaName,
                        programName = question.programName,
                        options = likertOptions(),
                    )
                }

                if (questions.size != EXPECTED_QUESTION_COUNT) {
                    throw ApiException(
                        statusCode = null,
                        message = "El backend devolvió ${questions.size} preguntas; " +
                            "la prueba configurada requiere $EXPECTED_QUESTION_COUNT.",
                    )
                }
                if (questions.map(QuestionUi::id).distinct().size != questions.size) {
                    throw ApiException(
                        statusCode = null,
                        message = "El backend devolvió preguntas duplicadas.",
                    )
                }

                _uiState.value = TestUiState(
                    attemptId = "pending-${UUID.randomUUID()}",
                    startedAtMillis = System.currentTimeMillis(),
                    elapsedSeconds = 0,
                    isRunning = true,
                    isLoadingQuestions = false,
                    versionLabel = "Versión $TEST_VERSION",
                    attemptLabel = "Nuevo intento",
                    audienceLabel = audienceLabel,
                    questions = questions,
                )
                startChronometer()
                onReady()
            } catch (error: ApiException) {
                _uiState.value = TestUiState(
                    isLoadingQuestions = false,
                    audienceLabel = audienceLabel,
                    errorMessage = error.message,
                )
                if (error.statusCode == 401) onSessionExpired()
            }
        }
    }

    fun selectOption(optionId: String) {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        if (!state.isRunning) return

        _uiState.value = state.copy(
            answers = state.answers + (question.id to optionId),
            errorMessage = null,
        )
    }

    /** Devuelve true cuando la prueba puede pasar a la revisión final. */
    fun nextQuestion(): Boolean {
        val state = _uiState.value
        val question = state.currentQuestion ?: return false

        if (!state.answers.containsKey(question.id)) {
            _uiState.value = state.copy(
                errorMessage = "Selecciona una respuesta antes de continuar.",
            )
            return false
        }

        if (state.isLastQuestion) {
            val firstUnanswered = state.questions.indexOfFirst { !state.answers.containsKey(it.id) }
            if (firstUnanswered >= 0) {
                _uiState.value = state.copy(
                    currentQuestionIndex = firstUnanswered,
                    errorMessage = "Aún faltan respuestas. Te llevamos a la primera pregunta pendiente.",
                )
                return false
            }
            return true
        }

        _uiState.value = state.copy(
            currentQuestionIndex = state.currentQuestionIndex + 1,
            errorMessage = null,
        )
        return false
    }

    fun previousQuestion() {
        val state = _uiState.value
        if (state.currentQuestionIndex <= 0) return
        _uiState.value = state.copy(
            currentQuestionIndex = state.currentQuestionIndex - 1,
            errorMessage = null,
        )
    }

    fun jumpToQuestion(index: Int) {
        val state = _uiState.value
        if (index !in state.questions.indices) return
        _uiState.value = state.copy(currentQuestionIndex = index, errorMessage = null)
    }

    fun focusFirstUnanswered(): Boolean {
        val state = _uiState.value
        val firstUnanswered = state.questions.indexOfFirst { !state.answers.containsKey(it.id) }
        if (firstUnanswered < 0) return false

        _uiState.value = state.copy(
            currentQuestionIndex = firstUnanswered,
            errorMessage = "Completa esta pregunta antes de enviar la prueba.",
        )
        return true
    }

    fun showError(message: String) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }

    fun stopAttempt() {
        timerJob?.cancel()
        timerJob = null
        _uiState.value = _uiState.value.copy(isRunning = false)
    }

    fun resetTest() {
        timerJob?.cancel()
        timerJob = null
        _uiState.value = TestUiState()
    }

    private fun startChronometer() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1_000)
                val state = _uiState.value
                if (!state.isRunning) break
                _uiState.value = state.copy(elapsedSeconds = state.elapsedSeconds + 1)
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

    private fun likertOptions(): List<QuestionOptionUi> = listOf(
        QuestionOptionUi("1", "Rara vez"),
        QuestionOptionUi("2", "A veces"),
        QuestionOptionUi("3", "A menudo"),
        QuestionOptionUi("4", "Siempre"),
    )

    private fun String.toVocationalArea(): VocationalArea {
        val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
            .lowercase()

        return when {
            "salud" in normalized -> VocationalArea.Health
            "negocio" in normalized || "administr" in normalized -> VocationalArea.Business
            "social" in normalized || "human" in normalized || "educ" in normalized ->
                VocationalArea.Social
            "arte" in normalized || "diseno" in normalized || "comunic" in normalized ->
                VocationalArea.Arts
            else -> VocationalArea.Engineering
        }
    }

    companion object {
        const val EXPECTED_QUESTION_COUNT = 180
        const val TEST_VERSION = "v1.1"
    }
}
