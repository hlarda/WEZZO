import com.example.wezzo.model.*
import com.example.wezzo.model.POJOs.Clouds
import com.example.wezzo.model.POJOs.Coord
import com.example.wezzo.model.POJOs.Main
import com.example.wezzo.model.POJOs.Current
import com.example.wezzo.model.POJOs.Sys
import com.example.wezzo.model.POJOs.Weather
import com.example.wezzo.model.POJOs.Wind
import com.example.wezzo.model.remote.NetworkInterface
import com.example.wezzo.model.remote.NetworkService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Response
import kotlinx.coroutines.flow.flowOf

class NetworkServiceTest {

    private lateinit var networkService: NetworkService
    private lateinit var networkInterface: NetworkInterface

    @Before
    fun setUp() {
        networkInterface = Mockito.mock(NetworkInterface::class.java)
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
            .thenReturn(flowOf(sampleResponse))

        val response = Repository(networkInterface).fetchWeather("London,uk", "58016d418401e5a0e8e9baef8d569514").first()
        assertEquals(200, response.code())
        assertEquals("London", response.body()?.name)
        assertEquals(288.11, response.body()?.main?.temp)
    }
}
