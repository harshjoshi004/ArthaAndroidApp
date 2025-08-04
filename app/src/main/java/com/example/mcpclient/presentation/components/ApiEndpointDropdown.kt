package com.example.mcpclient.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiEndpointDropdown(
    selectedEndpoint: String,
    onEndpointSelected: (String) -> Unit,
    endpoints: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedEndpoint,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select API Endpoint") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                endpoints.forEach { endpoint ->
                    DropdownMenuItem(
                        text = { Text(endpoint) },
                        onClick = {
                            onEndpointSelected(endpoint)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
