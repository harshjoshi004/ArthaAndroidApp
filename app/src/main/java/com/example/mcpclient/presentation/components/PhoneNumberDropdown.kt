package com.example.mcpclient.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberDropdown(
    selectedPhoneNumber: String,
    onPhoneNumberSelected: (String) -> Unit,
    phoneNumbers: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedPhoneNumber,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Phone Number") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                phoneNumbers.forEach { phoneNumber ->
                    DropdownMenuItem(
                        text = { Text(phoneNumber) },
                        onClick = {
                            onPhoneNumberSelected(phoneNumber)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
