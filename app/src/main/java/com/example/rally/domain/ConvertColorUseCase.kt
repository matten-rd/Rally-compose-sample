package com.example.rally.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import javax.inject.Inject

class ColorToHexUseCase @Inject constructor() {
    operator fun invoke(color: Color): String {
        return java.lang.String.format("%06X", color.toArgb() and 0xFFFFFF)
    }
}

class HexToColorUseCase @Inject constructor() {
    operator fun invoke(hexString: String): Color {
        return Color(android.graphics.Color.parseColor("#$hexString"))
    }
}