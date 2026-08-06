package com.usbbog.orientacionvocacional.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val UsbLightColorScheme = lightColorScheme(
    primary = USBColors.Orange,
    onPrimary = USBColors.White,
    primaryContainer = USBColors.OrangeSoft,
    onPrimaryContainer = USBColors.Black,
    secondary = USBColors.Blue,
    onSecondary = USBColors.White,
    secondaryContainer = USBColors.Sand,
    onSecondaryContainer = USBColors.Black,
    background = USBColors.GrayBackground,
    onBackground = USBColors.Text,
    surface = USBColors.Surface,
    onSurface = USBColors.Text,
    surfaceVariant = USBColors.SurfaceSoft,
    onSurfaceVariant = USBColors.TextSecondary,
    outline = USBColors.GrayBorder,
    error = USBColors.Danger,
    onError = USBColors.White,
)

/**
 * La identidad visual institucional siempre usa una paleta clara y estable.
 * Se desactiva el color dinámico de Android para evitar que el dispositivo
 * reemplace el naranja y el azul oficiales de la Universidad.
 */
@Composable
fun OrientacionVocacionalAppTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = UsbLightColorScheme,
        typography = Typography,
        content = content,
    )
}
