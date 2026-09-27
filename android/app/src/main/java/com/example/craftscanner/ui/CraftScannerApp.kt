package com.example.craftscanner.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.craftscanner.ui.screens.ProductDetailScreen
import com.example.craftscanner.ui.screens.ScannerScreen
import com.example.craftscanner.ui.theme.CraftScannerTheme
import com.example.craftscanner.ui.theme.TerracottaPrimary
import com.example.craftscanner.viewmodel.ScanUiState
import com.example.craftscanner.viewmodel.ScannerViewModel

@Composable
fun CraftScannerApp(
    viewModel: ScannerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Intercept Android System Navigation Back button to return to scanner when viewing product or error
    BackHandler(enabled = uiState !is ScanUiState.Idle) {
        viewModel.resetToScanner()
    }

    CraftScannerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (val state = uiState) {
                is ScanUiState.Idle -> {
                    ScannerScreen(
                        onBarcodeDetected = { barcode ->
                            viewModel.fetchProductByBarcode(barcode)
                        }
                    )
                }
                is ScanUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ScannerScreen(
                            onBarcodeDetected = { /* no-op during load */ }
                        )

                        // Loading overlay
                        Surface(
                            color = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = TerracottaPrimary,
                                    strokeWidth = 4.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Retrieving Craft Details...",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Barcode: ${state.barcode}",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
                is ScanUiState.Success -> {
                    ProductDetailScreen(
                        product = state.product,
                        onBackToScanner = {
                            viewModel.resetToScanner()
                        }
                    )
                }
                is ScanUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ScannerScreen(
                            onBarcodeDetected = { barcode ->
                                viewModel.fetchProductByBarcode(barcode)
                            }
                        )

                        AlertDialog(
                            onDismissRequest = { viewModel.resetToScanner() },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            title = {
                                Text(text = "Lookup Failed", fontWeight = FontWeight.Bold)
                            },
                            text = {
                                Text(text = state.message)
                            },
                            confirmButton = {
                                Button(
                                    onClick = { viewModel.fetchProductByBarcode(state.barcode) },
                                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary)
                                ) {
                                    Text("Retry")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { viewModel.resetToScanner() }) {
                                    Text("Scan Again")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
