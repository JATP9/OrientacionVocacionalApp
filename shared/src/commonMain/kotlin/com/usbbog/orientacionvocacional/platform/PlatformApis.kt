package com.usbbog.orientacionvocacional.platform

import androidx.compose.runtime.Composable

internal expect fun currentEpochMillis(): Long

@Composable
internal expect fun PlatformBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit,
)
