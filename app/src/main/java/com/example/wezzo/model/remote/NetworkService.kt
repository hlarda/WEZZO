package com.example.wezzo.model.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory

object NetworkService {
    private const val BASE_URL = "https://api.openweathermap.org/"

    val retrofitService: NetworkInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NetworkInterface::class.java)
    }
}

