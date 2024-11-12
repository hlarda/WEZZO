package com.example.wezzo.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.wezzo.model.FakeLocalDataSource
import com.example.wezzo.model.FakeRepository
import com.example.wezzo.model.POJOs.*
import com.example.wezzo.model.local.dbCity
import com.example.wezzo.model.remote.FakeRemoteDataSource
import com.example.wezzo.screens.home.view.AirPollutionResponseStatus
import com.example.wezzo.screens.home.view.ForecastResponseStatus
import com.example.wezzo.screens.home.view.WeatherResponseStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import kotlin.time.Duration.Companion.seconds

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var fakeRepository: FakeRepository
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var fakeLocalDataSource: FakeLocalDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        fakeRemoteDataSource = FakeRemoteDataSource()
        fakeLocalDataSource = FakeLocalDataSource()
        fakeRepository = FakeRepository(fakeRemoteDataSource, fakeLocalDataSource)
        weatherViewModel = WeatherViewModel(fakeRepository)
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

        fakeRemoteDataSource.weatherResponse = response

        // Act
        weatherViewModel.getWeatherByCity(city, appId)

        // Assert
        assertThat(weatherViewModel.weatherResponseStatus.first(), instanceOf(WeatherResponseStatus.Loading::class.java))
        val result = weatherViewModel.weatherResponseStatus.first { it is WeatherResponseStatus.Success }

        assertThat(result, instanceOf(WeatherResponseStatus.Success::class.java))
        val successResult = result as WeatherResponseStatus.Success
        assertThat(successResult.weatherResponse, `is`(mockCurrent))
    }

    @Test
    fun getSavedLocations_returnsSavedLocations() = runTest {
        // Arrange
        val savedLocations = listOf(
            dbCity(name = "London", country = "GB", latitude = 51.5085, longitude = -0.1257),
            dbCity(name = "New York", country = "US", latitude = 40.7128, longitude = -74.0060)
        )
        fakeLocalDataSource.cities = savedLocations.toMutableList()

        // Act
        val result = weatherViewModel.getSavedLocations().first()

        // Assert
        assertThat(result, `is`(savedLocations))
    }
}