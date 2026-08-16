package com.usbbog.orientacionvocacional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usbbog.orientacionvocacional.data.remote.ApiException
import com.usbbog.orientacionvocacional.data.remote.TestAnswerDto
import com.usbbog.orientacionvocacional.data.remote.TestResultResponseDto
import com.usbbog.orientacionvocacional.data.remote.VocationalRepository
import com.usbbog.orientacionvocacional.ui.mobile.CareerResultUi
import com.usbbog.orientacionvocacional.ui.mobile.QuestionUi
import com.usbbog.orientacionvocacional.ui.mobile.ResultHistoryItemUi
import com.usbbog.orientacionvocacional.ui.mobile.ResultScoreUi
import com.usbbog.orientacionvocacional.ui.mobile.ResultsHistoryUiState
import com.usbbog.orientacionvocacional.ui.mobile.ResultsUiState
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    private val _historyState = MutableStateFlow(ResultsHistoryUiState())
    val historyState: StateFlow<ResultsHistoryUiState> = _historyState.asStateFlow()

    fun submitAttempt(
        answers: Map<String, String>,
        questions: List<QuestionUi>,
        elapsedSeconds: Int,
        onSuccess: () -> Unit,
        onSessionExpired: () -> Unit = {},
    ) {
        if (_uiState.value.isLoading) return

        val requestAnswers = questions.mapNotNull { question ->
            val backendId = question.backendId ?: return@mapNotNull null
            val value = answers[question.id]?.toIntOrNull() ?: return@mapNotNull null
            if (value !in 1..4) return@mapNotNull null
            TestAnswerDto(
                questionId = backendId,
                questionCode = question.code,
                value = value,
            )
        }

        if (requestAnswers.size != questions.size || questions.isEmpty()) {
            _uiState.value = ResultsUiState(
                errorMessage = "No fue posible preparar todas las respuestas para el envío.",
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = ResultsUiState(isLoading = true)
            try {
                val response = VocationalRepository.submitTest(
                    elapsedSeconds = elapsedSeconds,
                    version = TestViewModel.TEST_VERSION,
                    answers = requestAnswers,
                )
                _uiState.value = response.toUiState()
                onSuccess()
            } catch (error: ApiException) {
                _uiState.value = ResultsUiState(errorMessage = error.message)
                if (error.statusCode == 401) onSessionExpired()
            }
        }
    }

    fun clearResults() {
        _uiState.value = ResultsUiState()
        _historyState.value = ResultsHistoryUiState()
    }

    fun loadHistory(
        onSessionExpired: () -> Unit = {},
    ) {
        if (_historyState.value.isLoading) return

        viewModelScope.launch {
            _historyState.value = _historyState.value.copy(
                isLoading = true,
                errorMessage = null,
            )

            try {
                val tests = VocationalRepository.myTests()
                    .sortedByDescending { it.date.orEmpty() }

                _historyState.value = ResultsHistoryUiState(
                    items = tests.mapIndexed { index, test ->
                        ResultHistoryItemUi(
                            id = test.id,
                            attemptNumber = tests.size - index,
                            completedAt = formatServerDate(test.date),
                            duration = formatDuration(test.elapsedSeconds),
                            version = test.version
                                ?.removePrefix("Versión ")
                                ?.takeIf(String::isNotBlank)
                                ?: "Versión no disponible",
                        )
                    },
                )
            } catch (error: ApiException) {
                if (error.statusCode == 404) {
                    _historyState.value = ResultsHistoryUiState()
                } else {
                    _historyState.value = ResultsHistoryUiState(
                        errorMessage = error.message,
                    )
                    if (error.statusCode == 401) onSessionExpired()
                }
            }
        }
    }

    fun openResult(
        testId: Long,
        onSuccess: () -> Unit,
        onSessionExpired: () -> Unit = {},
    ) {
        if (_historyState.value.openingResultId != null) return

        viewModelScope.launch {
            _historyState.value = _historyState.value.copy(
                openingResultId = testId,
                errorMessage = null,
            )

            try {
                _uiState.value = VocationalRepository.testResult(testId).toUiState()
                _historyState.value = _historyState.value.copy(openingResultId = null)
                onSuccess()
            } catch (error: ApiException) {
                _historyState.value = _historyState.value.copy(
                    openingResultId = null,
                    errorMessage = error.message,
                )
                if (error.statusCode == 401) onSessionExpired()
            }
        }
    }

    private fun TestResultResponseDto.toUiState(): ResultsUiState = ResultsUiState(
        resultId = testId.toString(),
        mainAreaId = predominantAreaId,
        mainArea = predominantAreaName,
        summary = profile
            ?: areaDescription
            ?: "El resultado refleja tus principales intereses vocacionales.",
        pdfSummary = profile
            ?: "Tu perfil vocacional fue calculado con base en tus respuestas de la prueba.",
        scores = areaAffinities.map { affinity ->
            ResultScoreUi(
                areaId = affinity.areaId,
                label = affinity.areaName,
                percentage = affinity.affinity.coerceIn(0, 100),
                profile = affinity.profile.orEmpty(),
                description = affinity.description.orEmpty(),
                logoPath = affinity.logoPath,
                pachoPath = affinity.pachoPath,
            )
        },
        careers = recommendedPrograms.mapIndexed { index, program ->
            CareerResultUi(
                rank = index + 1,
                name = program.programName,
                area = program.areaName ?: predominantAreaName,
                description = program.description
                    ?: "Consulta la información institucional de este programa.",
                score = program.affinity.coerceIn(0, 100),
                url = program.url,
                logoPath = program.logoPath,
            )
        },
        generatedAt = formatServerDate(date),
        generatedAtRaw = date.orEmpty(),
        reportName = reportName,
        reportUrl = reportUrl,
        isReady = true,
    )

    private fun formatServerDate(value: String?): String {
        if (value.isNullOrBlank()) return "Fecha no disponible"

        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
            isLenient = true
        }
        val parsed = runCatching { parser.parse(value.take(19)) }.getOrNull()
            ?: return value.replace('T', ' ').take(16)

        return DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM,
            DateFormat.SHORT,
            Locale("es", "CO"),
        ).format(parsed)
    }

    private fun formatDuration(value: Int?): String {
        val totalSeconds = value?.coerceAtLeast(0) ?: return "Duración no disponible"
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return buildList {
            if (hours > 0) add("${hours} h")
            if (minutes > 0 || hours > 0) add("${minutes} min")
            if (hours == 0) add("${seconds} s")
        }.joinToString(" ")
    }
}
