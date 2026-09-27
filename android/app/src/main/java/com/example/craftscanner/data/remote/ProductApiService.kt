package com.example.craftscanner.data.remote

import com.example.craftscanner.data.model.ProductDetail
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApiService {
    @GET("api/products/{barcode}")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): Response<ProductDetail>

    @GET("api/products")
    suspend fun getAllProducts(): Response<List<ProductDetail>>
}
