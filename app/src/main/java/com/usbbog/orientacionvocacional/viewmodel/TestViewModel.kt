package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usbbog.orientacionvocacional.ui.mobile.QuestionOptionUi
import com.usbbog.orientacionvocacional.ui.mobile.QuestionUi
import com.usbbog.orientacionvocacional.ui.mobile.TestUiState
import com.usbbog.orientacionvocacional.ui.mobile.VocationalArea
import java.util.UUID
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TestViewModel : ViewModel() {

    private val questions = buildQuestions()
    private var timerJob: Job? = null

    private val _uiState = MutableStateFlow(TestUiState(questions = questions))
    val uiState: StateFlow<TestUiState> = _uiState.asStateFlow()

    fun startAttempt(audienceLabel: String = "Usuario interno") {
        timerJob?.cancel()
        _uiState.value = TestUiState(
            attemptId = "attempt-${UUID.randomUUID()}",
            startedAtMillis = System.currentTimeMillis(),
            elapsedSeconds = 0,
            isRunning = true,
            versionLabel = "Versión v1.1",
            attemptLabel = "Intento #00011",
            audienceLabel = audienceLabel,
            questions = questions,
        )
        startChronometer()
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
        _uiState.value = state.copy(
            currentQuestionIndex = index,
            errorMessage = null,
        )
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

    fun stopAttempt() {
        timerJob?.cancel()
        timerJob = null
        _uiState.value = _uiState.value.copy(isRunning = false)
    }

    fun resetTest() {
        timerJob?.cancel()
        timerJob = null
        _uiState.value = TestUiState(questions = questions)
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

    private fun buildQuestions(): List<QuestionUi> {
        val statements = listOf(
            Triple("Disfruto resolver problemas utilizando tecnología.", "Razonamiento tecnológico", VocationalArea.Engineering),
            Triple("Me interesa comprender cómo funcionan los sistemas y dispositivos.", "Pensamiento sistémico", VocationalArea.Engineering),
            Triple("Me gusta analizar datos, números y patrones para encontrar soluciones.", "Análisis cuantitativo", VocationalArea.Engineering),
            Triple("Me motiva crear herramientas digitales que faciliten tareas cotidianas.", "Creación tecnológica", VocationalArea.Engineering),
            Triple("Disfruto acompañar a otras personas cuando necesitan apoyo.", "Servicio y cuidado", VocationalArea.Health),
            Triple("Me interesa aprender sobre el cuerpo, la salud y el bienestar.", "Interés científico en salud", VocationalArea.Health),
            Triple("Puedo mantener la calma y actuar con empatía ante situaciones difíciles.", "Empatía y autocontrol", VocationalArea.Health),
            Triple("Me siento cómodo organizando actividades y coordinando equipos.", "Liderazgo", VocationalArea.Business),
            Triple("Me interesa planear proyectos y tomar decisiones con información.", "Gestión", VocationalArea.Business),
            Triple("Disfruto proponer mejoras para que una organización funcione mejor.", "Innovación organizacional", VocationalArea.Business),
            Triple("Me interesa comprender el comportamiento y las emociones de las personas.", "Comprensión humana", VocationalArea.Social),
            Triple("Me gusta escuchar diferentes puntos de vista y facilitar acuerdos.", "Comunicación social", VocationalArea.Social),
            Triple("Disfruto enseñar y ayudar a otras personas a desarrollar sus habilidades.", "Acompañamiento educativo", VocationalArea.Social),
            Triple("Me interesa crear, diseñar o expresar ideas de manera visual.", "Expresión creativa", VocationalArea.Arts),
            Triple("Disfruto comunicar historias e ideas mediante distintos medios.", "Comunicación creativa", VocationalArea.Arts),
        )

        return statements.mapIndexed { index, (statement, dimension, area) ->
            QuestionUi(
                id = "question_${index + 1}",
                statement = statement,
                dimension = dimension,
                area = area,
                options = likertOptions(),
            )
        }
    }

    private fun likertOptions(): List<QuestionOptionUi> = listOf(
        QuestionOptionUi("1", "Rara vez"),
        QuestionOptionUi("2", "A veces"),
        QuestionOptionUi("3", "A menudo"),
        QuestionOptionUi("4", "Siempre"),
    )
}
