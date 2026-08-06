package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import com.usbbog.orientacionvocacional.ui.mobile.CareerResultUi
import com.usbbog.orientacionvocacional.ui.mobile.QuestionUi
import com.usbbog.orientacionvocacional.ui.mobile.ResultScoreUi
import com.usbbog.orientacionvocacional.ui.mobile.ResultsUiState
import com.usbbog.orientacionvocacional.ui.mobile.VocationalArea
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ResultsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    private val areaDescriptions = mapOf(
        VocationalArea.Engineering.label to
            "Presentas afinidad por el razonamiento lógico, la tecnología y la creación de soluciones.",
        VocationalArea.Health.label to
            "Presentas interés por el cuidado, el bienestar y el acompañamiento de otras personas.",
        VocationalArea.Business.label to
            "Presentas fortalezas para organizar, liderar, planear y tomar decisiones.",
        VocationalArea.Social.label to
            "Presentas interés por comprender a las personas, comunicarte y aportar a su desarrollo.",
        VocationalArea.Arts.label to
            "Presentas afinidad por la creatividad, el diseño y la comunicación de ideas.",
    )

    private val careersByArea = mapOf(
        VocationalArea.Engineering.label to CareerTemplate(
            name = "Ingeniería de Sistemas",
            description = "Diseño y desarrollo de software, soluciones tecnológicas y sistemas de información.",
        ),
        VocationalArea.Health.label to CareerTemplate(
            name = "Psicología",
            description = "Comprensión del comportamiento humano y acompañamiento de procesos de bienestar.",
        ),
        VocationalArea.Business.label to CareerTemplate(
            name = "Administración de Empresas",
            description = "Gestión de organizaciones, liderazgo de equipos y toma de decisiones.",
        ),
        VocationalArea.Social.label to CareerTemplate(
            name = "Licenciatura en Humanidades",
            description = "Educación, comunicación, pensamiento crítico y desarrollo social.",
        ),
        VocationalArea.Arts.label to CareerTemplate(
            name = "Diseño Gráfico",
            description = "Comunicación visual, creatividad y desarrollo de propuestas gráficas.",
        ),
    )

    fun generateResults(
        answers: Map<String, String>,
        questions: List<QuestionUi> = emptyList(),
    ) {
        if (answers.isEmpty()) {
            _uiState.value = ResultsUiState(
                resultId = "result-${UUID.randomUUID()}",
                mainArea = "Sin resultado",
                summary = "No se encontraron respuestas para calcular el resultado.",
                generatedAt = currentDate(),
                isReady = true,
            )
            return
        }

        val areasByQuestion = if (questions.isNotEmpty()) {
            questions.associate { it.id to it.area.label }
        } else {
            fallbackQuestionAreas()
        }
        val valuesByArea = mutableMapOf<String, MutableList<Int>>()

        answers.forEach { (questionId, optionId) ->
            val area = areasByQuestion[questionId] ?: return@forEach
            val answerValue = optionId.toIntOrNull()?.coerceIn(1, 4) ?: return@forEach
            valuesByArea.getOrPut(area) { mutableListOf() }.add(answerValue)
        }

        val scores = VocationalArea.entries.map { area ->
            val values = valuesByArea[area.label].orEmpty()
            val percentage = if (values.isEmpty()) {
                0
            } else {
                (((values.average() - 1.0) / 3.0) * 100.0).roundToInt()
            }
            ResultScoreUi(area.label, percentage)
        }.sortedByDescending { it.percentage }

        val mainArea = scores.firstOrNull()?.label ?: "Sin área principal"
        val careers = scores.take(3).mapIndexedNotNull { index, score ->
            val career = careersByArea[score.label] ?: return@mapIndexedNotNull null
            CareerResultUi(
                rank = index + 1,
                name = career.name,
                area = score.label,
                description = career.description,
                score = score.percentage,
            )
        }

        _uiState.value = ResultsUiState(
            resultId = "result-${UUID.randomUUID()}",
            mainArea = mainArea,
            summary = areaDescriptions[mainArea]
                ?: "Tu resultado refleja una combinación de diferentes intereses vocacionales.",
            scores = scores,
            careers = careers,
            generatedAt = currentDate(),
            isReady = true,
        )
    }

    fun clearResults() {
        _uiState.value = ResultsUiState()
    }

    private fun fallbackQuestionAreas(): Map<String, String> = mapOf(
        "question_1" to VocationalArea.Engineering.label,
        "question_2" to VocationalArea.Engineering.label,
        "question_3" to VocationalArea.Engineering.label,
        "question_4" to VocationalArea.Engineering.label,
        "question_5" to VocationalArea.Health.label,
        "question_6" to VocationalArea.Health.label,
        "question_7" to VocationalArea.Health.label,
        "question_8" to VocationalArea.Business.label,
        "question_9" to VocationalArea.Business.label,
        "question_10" to VocationalArea.Business.label,
        "question_11" to VocationalArea.Social.label,
        "question_12" to VocationalArea.Social.label,
        "question_13" to VocationalArea.Social.label,
        "question_14" to VocationalArea.Arts.label,
        "question_15" to VocationalArea.Arts.label,
    )

    private fun currentDate(): String = SimpleDateFormat(
        "dd/MM/yyyy HH:mm",
        Locale("es", "CO"),
    ).format(Date())

    private data class CareerTemplate(
        val name: String,
        val description: String,
    )
}
