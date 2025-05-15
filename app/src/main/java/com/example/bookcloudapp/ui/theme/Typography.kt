package com.example.bookcloudapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.bookcloudapp.R

val Literata = FontFamily(
    Font(R.font.literata_regular)
)

val ZorroTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Literata,
        fontSize = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Literata,
        fontSize = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Literata,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Literata,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Literata,
        fontSize = 12.sp
    )
)
