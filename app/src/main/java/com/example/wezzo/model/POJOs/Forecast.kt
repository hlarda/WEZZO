package com.example.wezzo.model.POJOs

data class Forecast(
    val cod: String,
    val message: Long,
    val cnt: Long,
    val list: List<ForecastList>,
    val city: City,
)
