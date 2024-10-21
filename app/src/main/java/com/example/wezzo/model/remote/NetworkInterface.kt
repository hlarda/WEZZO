package com.example.wezzo.model.remote

import com.example.wezzo.model.Root
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkInterface {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") location: String,
        @Query("appid") appId: String
    ): Response<Root>
}
