package com.example.mcpclient.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mcpclient.presentation.components.AnimatedSplashScreen
import com.example.mcpclient.presentation.screens.*
import com.example.mcpclient.presentation.viewmodel.McpViewModel

@Composable
fun McpNavigation(
    viewModel: McpViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = McpDestinations.SPLASH_ROUTE,
        modifier = modifier
    ) {
        composable(McpDestinations.SPLASH_ROUTE) {
            AnimatedSplashScreen(
                onAnimationComplete = {
                    val destination = if (viewModel.isLoggedIn) {
                        McpDestinations.DASHBOARD_ROUTE
                    } else {
                        McpDestinations.AUTH_ROUTE
                    }
                    navController.navigate(destination) {
                        popUpTo(McpDestinations.SPLASH_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        
        composable(McpDestinations.AUTH_ROUTE) {
            EnhancedAuthScreen(
                viewModel = viewModel,
                onNavigateToMain = {
                    navController.navigate(McpDestinations.DASHBOARD_ROUTE) {
                        popUpTo(McpDestinations.AUTH_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        
        composable(McpDestinations.DASHBOARD_ROUTE) {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToAuth = {
                    navController.navigate(McpDestinations.AUTH_ROUTE) {
                        popUpTo(McpDestinations.DASHBOARD_ROUTE) { inclusive = true }
                    }
                },
                onNavigateToDetails = { section ->
                    when {
                        section == "transactions" -> navController.navigate(McpDestinations.TRANSACTIONS_ROUTE)
                        section == "investments" -> navController.navigate(McpDestinations.INVESTMENTS_ROUTE)
                        section == "networth" -> navController.navigate(McpDestinations.NETWORTH_ROUTE)
                        section == "analytics" -> navController.navigate(McpDestinations.ANALYTICS_ROUTE)
                        section.startsWith("chat/") -> {
                            val sessionId = section.removePrefix("chat/")
                            val userId = viewModel.getStoredPhoneNumber() ?: "1313131313"
                            navController.navigate("${McpDestinations.CHAT_DETAILS_ROUTE}/$sessionId/$userId")
                        }
                        else -> navController.navigate("${McpDestinations.DETAILS_ROUTE}/$section")
                    }
                }
            )
        }
        
        composable(McpDestinations.TRANSACTIONS_ROUTE) {
            TransactionsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(McpDestinations.INVESTMENTS_ROUTE) {
            InvestmentsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(McpDestinations.NETWORTH_ROUTE) {
            NetWorthScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = "${McpDestinations.CHAT_DETAILS_ROUTE}/{sessionId}/{userId}",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ChatDetailsScreen(
                viewModel = viewModel,
                sessionId = sessionId,
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
