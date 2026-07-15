package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import com.usbbog.orientacionvocacional.ui.mobile.CareerResultUi
import com.usbbog.orientacionvocacional.ui.mobile.ResultScoreUi
import com.usbbog.orientacionvocacional.ui.mobile.ResultsUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ResultsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        ResultsUiState()
    )

    val uiState: StateFlow<ResultsUiState> =
        _uiState.asStateFlow()

    /**
     * Relación temporal entre cada pregunta y el área evaluada.
     */
    private val questionAreas = mapOf(
        "question_1" to "Ingeniería y tecnología",
        "question_2" to "Ciencias humanas",
        "question_3" to "Administración y liderazgo",
        "question_4" to "Ciencias exactas",
        "question_5" to "Arte y creatividad",
        "question_6" to "Educación",
        "question_7" to "Ciencia e investigación",
        "question_8" to "Administración y liderazgo"
    )

    private val areaDescriptions = mapOf(
        "Ingeniería y tecnología" to
                "Muestras afinidad por la resolución de problemas, la tecnología y la creación de soluciones.",

        "Ciencias humanas" to
                "Muestras interés por comprender a las personas, sus emociones y su comportamiento.",

        "Administración y liderazgo" to
                "Muestras facilidad para organizar, tomar decisiones, liderar y asumir responsabilidades.",

        "Ciencias exactas" to
                "Muestras afinidad por el análisis de datos, los números y el razonamiento lógico.",

        "Arte y creatividad" to
                "Muestras interés por la expresión de ideas, la creatividad y la comunicación.",

        "Educación" to
                "Muestras interés por enseñar, acompañar y contribuir al desarrollo de otras personas.",

        "Ciencia e investigación" to
                "Muestras curiosidad por investigar, analizar y comprender cómo funcionan las cosas."
    )

    private val careersByArea = mapOf(
        "Ingeniería y tecnología" to CareerTemplate(
            name = "Ingeniería de Sistemas",
            description =
                "Diseño y desarrollo de soluciones tecnológicas, software y sistemas de información."
        ),

        "Ciencias humanas" to CareerTemplate(
            name = "Psicología",
            description =
                "Comprensión del comportamiento humano, las emociones y los procesos sociales."
        ),

        "Administración y liderazgo" to CareerTemplate(
            name = "Administración de Empresas",
            description =
                "Gestión de organizaciones, liderazgo de equipos y toma de decisiones."
        ),

        "Ciencias exactas" to CareerTemplate(
            name = "Contaduría Pública",
            description =
                "Análisis financiero, control de información y gestión contable."
        ),

        "Arte y creatividad" to CareerTemplate(
            name = "Licenciatura en Humanidades",
            description =
                "Comunicación, pensamiento crítico, cultura y desarrollo de proyectos educativos."
        ),

        "Educación" to CareerTemplate(
            name = "Educación Infantil",
            description =
                "Acompañamiento pedagógico y desarrollo integral de niños y niñas."
        ),

        "Ciencia e investigación" to CareerTemplate(
            name = "Psicología",
            description =
                "Investigación de procesos humanos, sociales, cognitivos y conductuales."
        )
    )

    fun generateResults(
        answers: Map<String, String>
    ) {
        if (answers.isEmpty()) {
            _uiState.value = ResultsUiState(
                mainArea = "Sin resultado",
                summary =
                    "No se encontraron respuestas para calcular el resultado.",
                generatedAt = currentDate(),
                isReady = true
            )

            return
        }

        val valuesByArea =
            mutableMapOf<String, MutableList<Int>>()

        answers.forEach { (questionId, optionId) ->

            val area =
                questionAreas[questionId]
                    ?: return@forEach

            val answerValue =
                optionId.toIntOrNull()
                    ?.coerceIn(1, 5)
                    ?: return@forEach

            valuesByArea
                .getOrPut(area) {
                    mutableListOf()
                }
                .add(answerValue)
        }

        /*
         * Conversión provisional:
         *
         * Respuesta 1 = 0%
         * Respuesta 2 = 25%
         * Respuesta 3 = 50%
         * Respuesta 4 = 75%
         * Respuesta 5 = 100%
         */
        val scores = questionAreas
            .values
            .distinct()
            .map { area ->

                val values =
                    valuesByArea[area].orEmpty()

                val percentage =
                    if (values.isEmpty()) {
                        0
                    } else {
                        val average =
                            values.average()

                        (
                                (average - 1.0) /
                                        4.0 *
                                        100.0
                                ).roundToInt()
                    }

                ResultScoreUi(
                    label = area,
                    percentage = percentage
                )
            }
            .sortedByDescending {
                it.percentage
            }

        val mainResult =
            scores.firstOrNull()

        val mainArea =
            mainResult?.label
                ?: "Sin área principal"

        val recommendedCareers =
            scores
                .take(3)
                .mapIndexedNotNull {
                        index,
                        areaScore ->

                    val career =
                        careersByArea[
                            areaScore.label
                        ] ?: return@mapIndexedNotNull null

                    CareerResultUi(
                        rank = index + 1,
                        name = career.name,
                        area = areaScore.label,
                        description =
                            career.description,
                        score =
                            areaScore.percentage
                    )
                }

        _uiState.value = ResultsUiState(
            mainArea = mainArea,
            summary =
                areaDescriptions[mainArea]
                    ?: "Tu resultado refleja una combinación de diferentes intereses vocacionales.",
            scores = scores,
            careers = recommendedCareers,
            generatedAt = currentDate(),
            isReady = true
        )
    }

    fun clearResults() {
        _uiState.value =
            ResultsUiState()
    }

    private fun currentDate(): String {
        val formatter = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        )

        return formatter.format(
            Date()
        )
    }

    private data class CareerTemplate(
        val name: String,
        val description: String
    )
}