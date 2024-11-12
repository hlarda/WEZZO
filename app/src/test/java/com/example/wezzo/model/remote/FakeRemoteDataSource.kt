package com.example.wezzo.model.remote

import com.example.wezzo.model.POJOs.*
import com.example.wezzo.model.remote.NetworkInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class FakeRemoteDataSource : NetworkInterface {
    var weatherResponse: Response<Current>? = null

    override suspend fun getWeather(location: String, appId: String): Response<Current> {
        val sampleResponse = Response.success(
            Current(
                coord = Coord(lon = -0.1257, lat = 51.5085),
                weather = listOf(Weather(id = 804, main = "Clouds", description = "overcast clouds", icon = "04n")),
                base = "stations",
                main = Main(temp = 288.11, feelsLike = 287.85, tempMin = 286.86, tempMax = 288.85, pressure = 1012, humidity = 84, seaLevel = 1012, grndLevel = 1008),
                visibility = 10000,
                wind = Wind(speed = 4.02, deg = 243, gust = 8.05),
                clouds = Clouds(all = 100),
                dt = 1729450200,
                sys = Sys(type = 2, id = 2075535, country = "GB", sunrise = 1729406002, sunset = 1729443406),
                timezone = 3600,
                id = 2643743,
                name = "London",
                cod = 200
            )
        )
        return sampleResponse
    }

    override suspend fun getWeatherByLatAndLong(lat: Double, long: Double, appId: String, units: String, lang: String): Response<Current> {
        val sampleResponse = Response.success(
            Current(
                coord = Coord(lon = -0.1257, lat = 51.5085),
                weather = listOf(Weather(id = 804, main = "Clouds", description = "overcast clouds", icon = "04n")),
                base = "stations",
                main = Main(temp = 288.11, feelsLike = 287.85, tempMin = 286.86, tempMax = 288.85, pressure = 1012, humidity = 84, seaLevel = 1012, grndLevel = 1008),
                visibility = 10000,
                wind = Wind(speed = 4.02, deg = 243, gust = 8.05),
                clouds = Clouds(all = 100),
                dt = 1729450200,
                sys = Sys(type = 2, id = 2075535, country = "GB", sunrise = 1729406002, sunset = 1729443406),
                timezone = 3600,
                id = 2643743,
                name = "London",
                cod = 200
            )
        )
        return sampleResponse
    }

    override suspend fun get5DaysForecast(
        lat: Double,
        long: Double,
        appId: String,
        units: String,
        lang: String
    ): Response<Forecast> {
        TODO("Not yet implemented")
    }

    override suspend fun getAirPollution(
        lat: Double,
        long: Double,
        appId: String
    ): Response<AirPollution> {
        TODO("Not yet implemented")
    }

    override suspend fun getCityAndCountry(lat: Double, lon: Double, appId: String): Response<List<GeocodingResponse>> {
        val sampleResponse = Response.success(
            listOf(
                GeocodingResponse(
                    name = "City of Westminster",
                    local_names = mapOf(
                        "be" to "Вэстмінстэр",
                        "ru" to "Вестминстер",
                        "fr" to "Cité de Westminster",
                        "cy" to "San Steffan",
                        "ko" to "시티오브웨스트민스터",
                        "he" to "וסטמינסטר",
                        "en" to "City of Westminster",
                        "mk" to "Град Вестминстер"
                    ),
                    lat = 51.4973206,
                    lon = -0.137149,
                    country = "GB",
                    state = "England"
                )
            )
        )
        return sampleResponse
    }
}