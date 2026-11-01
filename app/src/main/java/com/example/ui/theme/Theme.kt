package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StatMateColorScheme = lightColorScheme(
  primary = Navy800,
  onPrimary = Color.White,
  primaryContainer = Navy100,
  onPrimaryContainer = Navy900,
  secondary = VioletPrimary,
  onSecondary = Color.White,
  secondaryContainer = VioletLight,
  onSecondaryContainer = VioletDark,
  tertiary = CyanAccent,
  onTertiary = Color.White,
  tertiaryContainer = CyanLight,
  onTertiaryContainer = Color(0xFF075985),
  background = SurfaceCanvas,
  onBackground = TextPrimary,
  surface = SurfaceCard,
  onSurface = TextPrimary,
  surfaceVariant = Navy50,
  onSurfaceVariant = TextSecondary,
  outline = BorderMedium,
  outlineVariant = BorderSubtle,
  error = RoseError,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = StatMateColorScheme,
    typography = Typography,
    content = content
  )
}
