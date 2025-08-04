package com.example.mcpclient.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mcpclient.presentation.components.AnimatedSplashScreen

@Composable
fun SplashScreen(
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedSplashScreen(
        onAnimationComplete = onNavigateNext,
        modifier = modifier
    )
}
