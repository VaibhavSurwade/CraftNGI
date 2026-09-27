package com.example.craftscanner.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

    /**
     * Connection notes:
     * - Android Emulator hitting host machine: "http://192.168.1.8:8000/"
     * - Physical Android device over Wi-Fi: "http://<YOUR_COMPUTER_LOCAL_IP>:8000/" (e.g. "http://192.168.1.15:8000/")
     * - Production server: "https://your-production-domain.com/"
     */
    var currentBaseUrl: String = "http://192.168.1.8:8000/"
        private set

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private var retrofit: Retrofit = buildRetrofit(currentBaseUrl)

    var apiService: ProductApiService = retrofit.create(ProductApiService::class.java)
        private set

    private fun buildRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    fun updateBaseUrl(newBaseUrl: String) {
        val sanitizedUrl = if (newBaseUrl.endsWith("/")) newBaseUrl else "$newBaseUrl/"
        currentBaseUrl = sanitizedUrl
        retrofit = buildRetrofit(sanitizedUrl)
        apiService = retrofit.create(ProductApiService::class.java)
    }
}
