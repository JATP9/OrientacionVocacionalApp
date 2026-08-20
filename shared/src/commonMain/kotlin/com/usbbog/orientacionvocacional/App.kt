package com.usbbog.orientacionvocacional

import androidx.compose.runtime.Composable
import com.usbbog.orientacionvocacional.navigation.AppNavigation
import com.usbbog.orientacionvocacional.platform.ResetLinkCoordinator
import com.usbbog.orientacionvocacional.ui.theme.OrientacionVocacionalAppTheme

/** Punto de entrada visual compartido por Android y iOS. */
@Composable
fun App() {
    OrientacionVocacionalAppTheme {
        AppNavigation(
            initialResetToken = ResetLinkCoordinator.token,
            resetLinkVersion = ResetLinkCoordinator.version,
        )
    }
}
