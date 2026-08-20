package com.usbbog.orientacionvocacional.platform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.usbbog.orientacionvocacional.data.session.SessionStorage
import com.usbbog.orientacionvocacional.data.session.SessionStore
import com.usbbog.orientacionvocacional.ui.mobile.ResultsUiState

data class ReportExportResult(
    val fileName: String,
)

interface ResultsReportExporter {
    fun createAndShare(
        userName: String,
        result: ResultsUiState,
    ): ReportExportResult
}

object AppEnvironment {
    private const val DEFAULT_BASE_URL = "http://localhost:8088/"

    var apiBaseUrl: String = DEFAULT_BASE_URL
        private set

    var reportExporter: ResultsReportExporter = UnavailableResultsReportExporter
        private set

    fun initialize(
        apiBaseUrl: String,
        sessionStorage: SessionStorage,
        reportExporter: ResultsReportExporter,
    ) {
        this.apiBaseUrl = apiBaseUrl.trim()
            .ifBlank { DEFAULT_BASE_URL }
            .let { if (it.endsWith('/')) it else "$it/" }
        this.reportExporter = reportExporter
        SessionStore.initialize(sessionStorage)
    }
}

object ResetLinkCoordinator {
    var token by mutableStateOf<String?>(null)
        private set

    var version by mutableIntStateOf(0)
        private set

    fun handleUrl(url: String?) {
        val parsedToken = url
            ?.substringAfter('?', missingDelimiterValue = "")
            ?.substringBefore('#')
            ?.split('&')
            ?.firstNotNullOfOrNull { parameter ->
                val name = parameter.substringBefore('=', missingDelimiterValue = "")
                if (name == "token") {
                    parameter.substringAfter('=', missingDelimiterValue = "")
                        .trim()
                        .takeIf(String::isNotBlank)
                } else {
                    null
                }
            }

        if (parsedToken != null) {
            token = parsedToken
            version += 1
        }
    }
}

fun handleDeepLink(url: String) {
    ResetLinkCoordinator.handleUrl(url)
}

private object UnavailableResultsReportExporter : ResultsReportExporter {
    override fun createAndShare(
        userName: String,
        result: ResultsUiState,
    ): ReportExportResult = error("La exportación del informe no está configurada.")
}
