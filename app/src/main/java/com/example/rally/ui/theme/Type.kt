package com.example.rally.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.rally.R


private val EczarFontFamily = FontFamily(
    Font(R.font.eczar_regular),
    Font(R.font.eczar_semibold, FontWeight.SemiBold)
)
private val RobotoCondensed = FontFamily(
    Font(R.font.robotocondensed_regular),
    Font(R.font.robotocondensed_light, FontWeight.Light),
    Font(R.font.robotocondensed_bold, FontWeight.Bold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    defaultFontFamily = RobotoCondensed,
    h1 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 96.sp,
    ),
    h2 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 60.sp,
        letterSpacing = 1.5.sp
    ),
    h3 = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 44.sp,
        fontFamily = EczarFontFamily
    ),
    h4 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp
    ),
    h5 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        letterSpacing = 0.2.em
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = 1.sp
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        letterSpacing = 1.sp
    ),
    subtitle2 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.em
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        letterSpacing = 0.1.em,
        fontFamily = EczarFontFamily
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.em
    ),
    button = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.em
    ),
    caption = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    overline = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp
    )
)