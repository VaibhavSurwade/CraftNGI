package com.example.craftscanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.craftscanner.data.model.ProductDetail
import com.example.craftscanner.data.remote.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ScanUiState {
    object Idle : ScanUiState()
    data class Loading(val barcode: String) : ScanUiState()
    data class Success(val product: ProductDetail) : ScanUiState()
    data class Error(val barcode: String, val message: String) : ScanUiState()
}

class ScannerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun fetchProductByBarcode(barcode: String) {
        val cleanBarcode = barcode.trim()
        if (cleanBarcode.isBlank()) return

        _uiState.value = ScanUiState.Loading(cleanBarcode)

        viewModelScope.launch {
            try {
                val response = NetworkClient.apiService.getProductByBarcode(cleanBarcode)
                if (response.isSuccessful) {
                    val product = response.body()
                    if (product != null) {
                        _uiState.value = ScanUiState.Success(product)
                    } else {
                        _uiState.value = ScanUiState.Error(
                            cleanBarcode,
                            "Empty response received from server."
                        )
                    }
                } else if (response.code() == 404) {
                    _uiState.value = ScanUiState.Error(
                        cleanBarcode,
                        "Barcode '$cleanBarcode' was not found in the verified GI craft database."
                    )
                } else {
                    _uiState.value = ScanUiState.Error(
                        cleanBarcode,
                        "Server error (${response.code()}): ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error(
                    cleanBarcode,
                    "Failed to connect to backend (${NetworkClient.currentBaseUrl}). Please verify network connection and server status. Details: ${e.localizedMessage ?: "Unknown network error"}"
                )
            }
        }
    }

    fun resetToScanner() {
        _uiState.value = ScanUiState.Idle
    }
}
