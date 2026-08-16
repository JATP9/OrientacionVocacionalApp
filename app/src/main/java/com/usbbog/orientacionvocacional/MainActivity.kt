package com.usbbog.orientacionvocacional

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.usbbog.orientacionvocacional.data.session.SessionStore
import com.usbbog.orientacionvocacional.navigation.AppNavigation
import com.usbbog.orientacionvocacional.ui.theme.OrientacionVocacionalAppTheme

class MainActivity : ComponentActivity() {

    private var resetPasswordToken by mutableStateOf<String?>(null)
    private var resetLinkVersion by mutableStateOf(0)

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        SessionStore.initialize(applicationContext)
        resetPasswordToken = intent.resetToken()

        setContent {
            OrientacionVocacionalAppTheme {
                AppNavigation(
                    initialResetToken = resetPasswordToken,
                    resetLinkVersion = resetLinkVersion,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        resetPasswordToken = intent.resetToken()
        resetLinkVersion += 1
    }

    private fun Intent?.resetToken(): String? =
        this?.data?.getQueryParameter("token")?.trim()?.takeIf(String::isNotBlank)
}
