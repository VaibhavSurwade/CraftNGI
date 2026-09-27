package com.example.craftadmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.craftadmin.data.model.ProductCreateRequest
import com.example.craftadmin.data.model.ProductItem
import com.example.craftadmin.data.remote.AdminNetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Idle : RegisterState()
    object Submitting : RegisterState()
    data class Success(val product: ProductItem) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class AdminViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<ProductItem>>(emptyList())
    val products: StateFlow<List<ProductItem>> = _products.asStateFlow()

    private val _isLoadingList = MutableStateFlow(false)
    val isLoadingList: StateFlow<Boolean> = _isLoadingList.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        _isLoadingList.value = true
        viewModelScope.launch {
            try {
                val response = AdminNetworkClient.apiService.getProducts()
                if (response.isSuccessful) {
                    _products.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingList.value = false
            }
        }
    }

    fun registerProduct(request: ProductCreateRequest) {
        _registerState.value = RegisterState.Submitting
        viewModelScope.launch {
            try {
                val response = AdminNetworkClient.apiService.registerProduct(request)
                if (response.isSuccessful) {
                    val created = response.body()
                    if (created != null) {
                        _registerState.value = RegisterState.Success(created)
                        fetchProducts() // refresh list
                    } else {
                        _registerState.value = RegisterState.Error("Empty response from server")
                    }
                } else if (response.code() == 400) {
                    _registerState.value = RegisterState.Error("A product with barcode '${request.barcode}' already exists in registry!")
                } else {
                    _registerState.value = RegisterState.Error("Server error (${response.code()}): ${response.message()}")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Network failure (${AdminNetworkClient.currentBaseUrl}): ${e.localizedMessage ?: "Connection refused"}")
            }
        }
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }
}
