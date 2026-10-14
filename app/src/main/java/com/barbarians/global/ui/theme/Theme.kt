package com.barbarians.global.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BarbarianCrimson,
    onPrimary = TextPrimary,
    primaryContainer = BarbarianDeepRed,
    onPrimaryContainer = BarbarianGold,
    secondary = BarbarianGold,
    onSecondary = ArenaBlack,
    secondaryContainer = BarbarianAmber,
    onSecondaryContainer = ArenaBlack,
    tertiary = FuryOrange,
    background = ArenaBlack,
    onBackground = TextPrimary,
    surface = ArenaDarkGray,
    onSurface = TextPrimary,
    surfaceVariant = ArenaCardBg,
    onSurfaceVariant = TextSecondary,
    outline = ArenaBorder
)

@Composable
fun BarbarianArenaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
