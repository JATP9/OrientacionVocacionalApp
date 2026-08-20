package com.usbbog.orientacionvocacional

import androidx.compose.ui.window.ComposeUIViewController
import com.usbbog.orientacionvocacional.platform.AppEnvironment
import com.usbbog.orientacionvocacional.platform.IosSessionStorage
import com.usbbog.orientacionvocacional.platform.ResultsReportExporter
import platform.UIKit.UIViewController

/** Construye la pantalla raíz que Swift integra dentro de la aplicación iOS. */
fun MainViewController(
    apiBaseUrl: String,
    reportExporter: ResultsReportExporter,
): UIViewController {
    AppEnvironment.initialize(
        apiBaseUrl = apiBaseUrl,
        sessionStorage = IosSessionStorage(),
        reportExporter = reportExporter,
    )

    return ComposeUIViewController {
        App()
    }
}
