package com.example.craftadmin.data.remote

import com.example.craftadmin.data.model.ProductCreateRequest
import com.example.craftadmin.data.model.ProductItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AdminApiService {
    @GET("api/products")
    suspend fun getProducts(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100
    ): Response<List<ProductItem>>

    @POST("api/products")
    suspend fun registerProduct(
        @Body request: ProductCreateRequest
    ): Response<ProductItem>
}
