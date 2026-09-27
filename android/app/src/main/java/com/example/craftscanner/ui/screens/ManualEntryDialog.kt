package com.example.craftscanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.craftscanner.ui.theme.TerracottaPrimary

@Composable
fun ManualEntryDialog(
    onDismiss: () -> Unit,
    onSubmitBarcode: (String) -> Unit
) {
    var barcodeInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = null,
                tint = TerracottaPrimary
            )
        },
        title = {
            Text(
                text = "Manual Barcode Lookup",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Enter the barcode number printed on the craft tag to look up verified specifications:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = barcodeInput,
                    onValueChange = {
                        barcodeInput = it
                        isError = false
                    },
                    label = { Text("Barcode Number") },
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text("Please enter a valid barcode number.")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmed = barcodeInput.trim()
                    if (trimmed.isNotBlank()) {
                        onSubmitBarcode(trimmed)
                    } else {
                        isError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
            ) {
                Text("Search")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
