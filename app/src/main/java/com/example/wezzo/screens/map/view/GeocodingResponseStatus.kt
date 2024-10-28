package com.example.wezzo.screens.map.view

import com.example.wezzo.model.POJOs.GeocodingResponse

sealed class GeocodingResponseStatus {
    object Loading : GeocodingResponseStatus()
    data class Success(val geocodingResponse: List<GeocodingResponse>) : GeocodingResponseStatus()
    data class Error(val message: String) : GeocodingResponseStatus()
}