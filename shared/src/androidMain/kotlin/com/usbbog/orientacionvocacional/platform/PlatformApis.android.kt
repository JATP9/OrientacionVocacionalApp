package com.usbbog.orientacionvocacional.platform

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

internal actual fun currentEpochMillis(): Long = System.currentTimeMillis()

@Composable
internal actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    BackHandler(enabled = enabled, onBack = onBack)
}
