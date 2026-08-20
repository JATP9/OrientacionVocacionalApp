package com.usbbog.orientacionvocacional.platform

import androidx.compose.runtime.Composable
import platform.Foundation.NSDate

internal actual fun currentEpochMillis(): Long =
    (NSDate().timeIntervalSince1970 * 1_000.0).toLong()

@Composable
internal actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
) {
    // iOS navigation uses the visible controls and the native swipe gesture.
}
