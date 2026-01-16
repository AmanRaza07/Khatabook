package com.mruraza.khata.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Grey0 = Color(0xFFFFFFFF) // Pure white
val Grey2 = Color(0xFFF4F4F4)
val Grey4 = Color(0xFFE6E6E6)

val lightGreen = Color(0xFFF4FFF0)
val lightRed = Color(0XFFFFD6D7)

data class AppGray(
    val bg0: Color,   // screen bg
    val bg2: Color,   // card bg
    val bg4: Color    // subtle container / divider bg
)

val LightAppGray = AppGray(
    bg0 = Color(0xFFFFFFFF), // Grey0
    bg2 = Color(0xFFF4F4F4), // Grey2
    bg4 = Color(0xFFE6E6E6)  // Grey4
)

val DarkAppGray = AppGray(
    bg0 = Color(0xFF121212), // screen background
    bg2 = Color(0xFF1E1E1E), // card background
    bg4 = Color(0xFF2A2A2A)  // subtle surface
)
