package com.example.mcpclient.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.mcpclient.data.network.NetworkModule
import com.example.mcpclient.presentation.navigation.McpNavigation
import com.example.mcpclient.presentation.viewmodel.McpViewModel

@Composable
fun McpApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val repository = remember { NetworkModule.provideRepository(context) }
    val viewModel: McpViewModel = viewModel { McpViewModel(repository) }
    val navController = rememberNavController()
    
    McpNavigation(
        viewModel = viewModel,
        navController = navController,
        modifier = modifier
    )
}
