package com.usbbog.orientacionvocacional

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.usbbog.orientacionvocacional.platform.AppEnvironment
import com.usbbog.orientacionvocacional.platform.ResetLinkCoordinator
import com.usbbog.orientacionvocacional.util.AndroidResultsReportExporter

class MainActivity : ComponentActivity() {
    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        AppEnvironment.initialize(
            apiBaseUrl = BuildConfig.API_BASE_URL,
            sessionStorage = AndroidSessionStorage(applicationContext),
            reportExporter = AndroidResultsReportExporter(applicationContext),
        )
        ResetLinkCoordinator.handleUrl(intent?.dataString)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        ResetLinkCoordinator.handleUrl(intent.dataString)
    }
}
