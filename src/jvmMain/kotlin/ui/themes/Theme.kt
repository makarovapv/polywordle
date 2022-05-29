package ui.themes

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

/* dark theme */
private val DarkColorScheme = darkColors(
    onSurface = Color.White,
    onBackground = Color.DarkGray,
    primary = Color(0xffdf78ef)
)

/* light theme */
private val LightColorScheme = lightColors(
    onSurface = Color.DarkGray,
    onBackground = Color.LightGray,
    primary = Color(171, 71, 188)
)

data class PolyWordleTheme(val darkTheme: Boolean) {
    val colors: Colors =
        if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        }
}