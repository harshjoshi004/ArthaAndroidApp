package com.example.mcpclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.mcpclient.presentation.McpApp
import com.example.mcpclient.ui.theme.McpClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            McpClientTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    McpApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
