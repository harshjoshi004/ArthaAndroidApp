package com.example.mcpclient.presentation.navigation

import androidx.navigation.NavHostController

class McpNavigationActions(private val navController: NavHostController) {
    
    fun navigateToAuth() {
        navController.navigate(McpDestinations.AUTH_ROUTE) {
            popUpTo(McpDestinations.MAIN_ROUTE) { inclusive = true }
        }
    }
    
    fun navigateToMain() {
        navController.navigate(McpDestinations.MAIN_ROUTE) {
            popUpTo(McpDestinations.AUTH_ROUTE) { inclusive = true }
        }
    }
    
    fun navigateBack() {
        navController.popBackStack()
    }
}
