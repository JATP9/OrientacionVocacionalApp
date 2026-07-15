package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import com.usbbog.orientacionvocacional.ui.mobile.QuestionOptionUi
import com.usbbog.orientacionvocacional.ui.mobile.QuestionUi
import com.usbbog.orientacionvocacional.ui.mobile.TestUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TestViewModel : ViewModel() {

    private val questions = listOf(
        QuestionUi(
            id = "question_1",
            statement = "Disfruto resolver problemas utilizando tecnología.",
            options = likertOptions()
        ),
        QuestionUi(
            id = "question_2",
            statement = "Me interesa comprender el comportamiento y las emociones de las personas.",
            options = likertOptions()
        ),
        QuestionUi(
            id = "question_3",
            statement = "Me siento cómodo organizando actividades y liderando grupos.",
            options = likertOptions()
        ),
        QuestionUi(
            id = "question_4",
            statement = "Disfruto analizar datos, números y patrones.",
            options = likertOptions()
        ),
        QuestionUi(
            id = "question_5",
            statement = "Me interesa crear, diseñar o expresar ideas de manera visual.",
            options = likertOptions()
        ),
        QuestionUi(
            id = "question_6",
            statement = "Me gusta ayudar a otras personas a aprender y desarrollar sus habilidades.",
            options = likertOptions()
        ),
        QuestionUi(
            id = "question_7",
            statement = "Me interesa investigar cómo funcionan las cosas.",
            options = likertOptions()
        ),
        QuestionUi(
            id = "question_8",
            statement = "Prefiero actividades en las que pueda tomar decisiones y asumir responsabilidades.",
            options = likertOptions()
        )
    )

    private val _uiState = MutableStateFlow(
        TestUiState(
            questions = questions
        )
    )

    val uiState: StateFlow<TestUiState> =
        _uiState.asStateFlow()

    fun selectOption(optionId: String) {
        val currentState = _uiState.value
        val question = currentState.currentQuestion ?: return

        _uiState.value = currentState.copy(
            answers = currentState.answers + (
                    question.id to optionId
                    ),
            errorMessage = null
        )
    }

    /**
     * Devuelve true cuando se respondió la última pregunta.
     */
    fun nextQuestion(): Boolean {
        val currentState =
            _uiState.value

        val currentQuestion =
            currentState.currentQuestion
                ?: return false

        val selectedOption =
            currentState.answers[
                currentQuestion.id
            ]

        if (selectedOption == null) {

            _uiState.value =
                currentState.copy(
                    errorMessage =
                        "Selecciona una respuesta antes de continuar."
                )

            return false
        }

        if (currentState.isLastQuestion) {

            val firstUnansweredIndex =
                currentState.questions
                    .indexOfFirst { question ->

                        !currentState.answers
                            .containsKey(question.id)
                    }

            if (firstUnansweredIndex != -1) {

                _uiState.value =
                    currentState.copy(
                        currentQuestionIndex =
                            firstUnansweredIndex,
                        errorMessage =
                            "Debes responder todas las preguntas antes de finalizar."
                    )

                return false
            }

            return true
        }

        _uiState.value =
            currentState.copy(
                currentQuestionIndex =
                    currentState.currentQuestionIndex + 1,
                errorMessage = null
            )

        return false
    }

    fun previousQuestion() {
        val currentState = _uiState.value

        if (currentState.currentQuestionIndex <= 0) {
            return
        }

        _uiState.value = currentState.copy(
            currentQuestionIndex =
                currentState.currentQuestionIndex - 1,
            errorMessage = null
        )
    }

    fun jumpToQuestion(index: Int) {
        val currentState = _uiState.value

        if (index !in currentState.questions.indices) {
            return
        }

        _uiState.value = currentState.copy(
            currentQuestionIndex = index,
            errorMessage = null
        )
    }

    fun resetTest() {
        _uiState.value = TestUiState(
            questions = questions
        )
    }

    private fun likertOptions(): List<QuestionOptionUi> {
        return listOf(
            QuestionOptionUi(
                id = "1",
                title = "Totalmente en desacuerdo"
            ),
            QuestionOptionUi(
                id = "2",
                title = "En desacuerdo"
            ),
            QuestionOptionUi(
                id = "3",
                title = "Ni de acuerdo ni en desacuerdo"
            ),
            QuestionOptionUi(
                id = "4",
                title = "De acuerdo"
            ),
            QuestionOptionUi(
                id = "5",
                title = "Totalmente de acuerdo"
            )
        )
    }
}