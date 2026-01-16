package com.mruraza.khata.presentation.UI

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.mruraza.khata.NavigationDestination
import com.mruraza.khata.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController
) {
    // Logo fade-in animation
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(targetValue = if (startAnimation) 1f else 0f)

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000) // wait 2 seconds
        navController.navigate(NavigationDestination.CUSTOMER) {
            popUpTo(NavigationDestination.SPLASH_SCREEN) { inclusive = true }
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        // Dynamic gradient using Box size
        val gradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFFBFE7F2),
                Color(0xFFCFEBDD),
                Color(0xFF8BCF9A)
            ),
            start = Offset(0f, 0f),
            end = with(density) { Offset(maxWidth.toPx(), maxHeight.toPx()) }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f))
                    .alpha(alphaAnim) // fade in
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_white_bg),
                    contentDescription = "Logo"
                )
            }
        }
    }
}
