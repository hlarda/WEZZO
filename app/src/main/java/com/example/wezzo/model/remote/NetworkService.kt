package com.example.wezzo.model.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object NetworkService {
    private const val BASE_URL = "https://api.openweathermap.org/"

    private val okHttpClient= OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofitService: NetworkInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NetworkInterface::class.java)
    }
}

