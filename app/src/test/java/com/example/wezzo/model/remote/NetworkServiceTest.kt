package com.example.wezzo.model.remote

import com.example.wezzo.model.*
import com.example.wezzo.model.POJOs.Clouds
import com.example.wezzo.model.POJOs.Coord
import com.example.wezzo.model.POJOs.Main
import com.example.wezzo.model.POJOs.Current
import com.example.wezzo.model.POJOs.Sys
import com.example.wezzo.model.POJOs.Weather
import com.example.wezzo.model.POJOs.Wind
import com.example.wezzo.model.local.dbCityDao
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Response
import android.content.Context
import android.content.SharedPreferences
import com.example.wezzo.model.POJOs.City
import com.example.wezzo.model.POJOs.GeocodingResponse

class NetworkServiceTest {

    private lateinit var networkService: NetworkService
    private lateinit var cityDao: dbCityDao
    private lateinit var networkInterface: NetworkInterface
    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setUp() {
        networkInterface = Mockito.mock(NetworkInterface::class.java)
        cityDao = Mockito.mock(dbCityDao::class.java)
        context = Mockito.mock(Context::class.java)
        sharedPreferences = Mockito.mock(SharedPreferences::class.java)
        editor = Mockito.mock(SharedPreferences.Editor::class.java)

        Mockito.`when`(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt())).thenReturn(sharedPreferences)
        Mockito.`when`(sharedPreferences.edit()).thenReturn(editor)
        Mockito.`when`(editor.putString(Mockito.anyString(), Mockito.anyString())).thenReturn(editor)
        Mockito.`when`(editor.apply()).then { }

        networkService = NetworkService
    }

    @Test
    fun `fetchWeather should return weather data`() = runTest {
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

        Mockito.`when`(networkInterface.getWeather("London,uk", "58016d418401e5a0e8e9baef8d569514"))
            .thenReturn(sampleResponse)

        val response = Repository(networkInterface, cityDao, context).getWeatherByCity("London,uk", "58016d418401e5a0e8e9baef8d569514").first()
        assertEquals(200, response.code())
        assertEquals("London", response.body()?.name)
        assertEquals(288.11, response.body()?.main?.temp)
    }

    @Test
    fun `getCityAndCountry should return forcast`() = runTest {
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

        Mockito.`when`(networkInterface.getCityAndCountry(51.509, -0.1180, "58016d418401e5a0e8e9baef8d569514"))
            .thenReturn(sampleResponse)

        val response = Repository(networkInterface, cityDao, context).getCityAndCountry(51.509, -0.1180, "58016d418401e5a0e8e9baef8d569514").first()
        assertEquals(200, response.code())
        assertEquals("City of Westminster", response.body()?.get(0)?.name)
    }
}