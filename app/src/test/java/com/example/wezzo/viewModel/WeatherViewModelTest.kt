package com.example.wezzo.viewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.wezzo.model.POJOs.Clouds
import com.example.wezzo.model.POJOs.Coord
import com.example.wezzo.model.POJOs.Current
import com.example.wezzo.model.POJOs.Main
import com.example.wezzo.model.POJOs.Sys
import com.example.wezzo.model.POJOs.Weather
import com.example.wezzo.model.POJOs.Wind
import com.example.wezzo.model.Repository
import com.example.wezzo.screens.home.view.AirPollutionResponseStatus
import com.example.wezzo.screens.home.view.ForecastResponseStatus
import com.example.wezzo.screens.home.view.WeatherResponseStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.junit.MockitoJUnitRunner
import io.mockk.every
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.delay
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class WeatherViewModelTest {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var repository: Repository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        sharedPreferences = mockk()
        sharedPreferencesEditor = mockk()
        every { sharedPreferences.edit() } returns sharedPreferencesEditor

        // Mocking Repository
        repository = mockk()

        // Mocking static methods for logging
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        // Initializing the ViewModel with mocked repository
        weatherViewModel = WeatherViewModel(repository)
    }

    @After
    fun tearDown() {
        // Cleanup if necessary
    }

    @Test
    fun getWeatherByCity_weatherDataIsNotNull() = runTest {
        // Arrange
        val city = "London"
        val appId = "58016d418401e5a0e8e9baef8d569514"
        val mockCurrent = Current(
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
        val response = retrofit2.Response.success(mockCurrent)

        coEvery {
            repository.getWeatherByCity(city, appId)
        } returns flow {
            emit(response)
        }

        // Act
        weatherViewModel.getWeatherByCity(city, appId)

        // Assert
        assertThat(weatherViewModel.weatherResponseStatus.first(), instanceOf(WeatherResponseStatus.Loading::class.java))
        val result = weatherViewModel.weatherResponseStatus.first { it is WeatherResponseStatus.Success }

        // Now, access the correct property
        assertThat(result, instanceOf(WeatherResponseStatus.Success::class.java))
        val successResult = result as WeatherResponseStatus.Success // Cast to Success
        assertThat(successResult.weatherResponse, `is`(mockCurrent)) // Access weatherResponse correctly
    }

    @Test
    fun getWeatherByCity_failure_updatesWeatherResponseStatus() = runTest {
        // Arrange
        val city = "London"
        val appId = "mock_app_id"
        val errorBody = ResponseBody.create(
            "application/json".toMediaTypeOrNull(),
            """{"cod": 401, "message": "Invalid API key. Please see https://openweathermap.org/faq#error401 for more info."}"""
        )
        val errorResponse = Response.error<Current>(401, errorBody)

        coEvery { repository.getWeatherByCity(city, appId) } returns flow {
            emit(errorResponse)
        }

        // Act
        weatherViewModel.getWeatherByCity(city, appId)

        // Assert
        assertThat(weatherViewModel.weatherResponseStatus.first(), instanceOf(WeatherResponseStatus.Loading::class.java))
        val result = weatherViewModel.weatherResponseStatus.first { it is WeatherResponseStatus.Error }
        assertThat(result, instanceOf(WeatherResponseStatus.Error::class.java))
        val errorResult = result as WeatherResponseStatus.Error
        assertThat(errorResult.message, `is`("Error: 401"))
    }
}
