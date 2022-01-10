package com.example.rally.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

private val DarkColorPalette = darkColors(
    primary = Green500,
    primaryVariant = DarkGreen700,
    onPrimary = Color.White,
    surface = DarkBlue900,
    onSurface = Color.White,
    background = DarkBlue900,
    onBackground = Color.White,
    error = Red600,
    onError = Color.White,
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Blue100

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun RallyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Composable
fun RallyDialogThemeOverlay(content: @Composable () -> Unit) {
    val dialogColors = darkColors(
        primary = Color.White,
        surface = Color.White.copy(alpha = 0.12f).compositeOver(Color.Black),
        onSurface = Color.White,
        secondary = Green500,
        error = Red600
    )

    // Copy the current [Typography] and replace some text styles for this theme.
    val currentTypography = MaterialTheme.typography
    val dialogTypography = currentTypography.copy(
        body2 = currentTypography.body1.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            letterSpacing = 1.sp
        ),
        button = currentTypography.button.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.2.em
        )
    )
    MaterialTheme(colors = dialogColors, typography = dialogTypography, content = content)
}