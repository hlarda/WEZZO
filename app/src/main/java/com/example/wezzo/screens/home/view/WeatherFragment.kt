package com.example.wezzo.screens.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wezzo.databinding.FragmentWeatherBinding
import com.example.wezzo.model.POJOs.Current
import com.example.wezzo.model.POJOs.ForecastList
import com.example.wezzo.model.Repository
import com.example.wezzo.model.local.dbCityDatabase
import com.example.wezzo.model.remote.NetworkService
import com.example.wezzo.viewModel.WeatherViewModel
import com.example.wezzo.viewModel.ViewModelFactory
import kotlinx.coroutines.launch

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    var responseBuf: Current? = null

    private val viewModel: WeatherViewModel by viewModels {
        ViewModelFactory(
            Repository(
                NetworkService.retrofitService,
                dbCityDatabase.getDatabase(requireContext()).cityDao(),
                requireContext()
            ),
            requireContext()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val unitPreference = sharedPreferences.getString("units", "metric")

        val latitude = arguments?.getDouble("latitude") ?: 0.0
        val longitude = arguments?.getDouble("longitude") ?: 0.0

        lifecycleScope.launch {
            viewModel.weatherResponseStatus.collect { responseStatus ->
                when (responseStatus) {
                    is WeatherResponseStatus.Loading -> {
                        binding.apply {
                            listOf(textviewNow, textviewCurrentTemp, minTemp, maxTemp, textView8, textView11, unit, textviewFeelslike2,
                                textviewDesc, textviewFeelslike, weatherIcon
                            ).forEach { it.visibility = View.GONE }
                        }
                    }
                    is WeatherResponseStatus.Success -> {
                        binding.apply {
                            textviewCurrentTemp.text = "${responseStatus.weatherResponse.main.temp}"
                            maxTemp.text = responseStatus.weatherResponse.main.tempMax.toString()
                            minTemp.text = responseStatus.weatherResponse.main.tempMin.toString()
                            textviewDesc.text = responseStatus.weatherResponse.weather[0].description
                            textviewFeelslike.text = "${responseStatus.weatherResponse.main.feelsLike}°F"
                            Glide.with(requireContext()).load("https://openweathermap.org/img/wn/${responseStatus.weatherResponse.weather[0].icon}@2x.png").into(weatherIcon)
                            unit.text = if (unitPreference == "metric") "°C" else "°F"


                            textViewCloudDescription.text = responseStatus.weatherResponse.clouds.all.toString()
                            textViewVisibilityDescription.text = responseStatus.weatherResponse.visibility.toString()
                            textViewWindDescription.text = responseStatus.weatherResponse.wind.speed.toString()
                            textViewHumidityDescription.text = responseStatus.weatherResponse.main.humidity.toString()
                            textViewPressureDescription.text = responseStatus.weatherResponse.main.pressure.toString()
                            textViewSeaLevelDescription.text = responseStatus.weatherResponse.main.seaLevel.toString()
                        }
                        binding.apply {
                            listOf(textviewNow, textviewCurrentTemp, minTemp, maxTemp, textView8, textView11, unit, textviewFeelslike2,
                                textviewDesc, textviewFeelslike, weatherIcon
                            ).forEach { it.visibility = View.VISIBLE }
                        }
                        Log.i("WeatherFragment", "Response: ${responseStatus.weatherResponse}")
                        responseBuf = responseStatus.weatherResponse
                        updateToolbarTitle()
                    }
                    is WeatherResponseStatus.Error -> {
                        Log.e("WeatherFragment", "Error: ${responseStatus.message}")
                    }
                }
            }
        }
        viewModel.getWeatherByLatAndLong(latitude, longitude, "58016d418401e5a0e8e9baef8d569514")

        lifecycleScope.launch {
            viewModel.forecastResponseStatus.collect { responseStatus ->
                when (responseStatus) {
                    is ForecastResponseStatus.Loading -> {
                        // Handle loading state if needed
                    }
                    is ForecastResponseStatus.Success -> {
                        setupRecyclerView(responseStatus.forecastResponse.list)
                        Log.i("WeatherFragment", "Response: ${responseStatus.forecastResponse}")
                    }
                    is ForecastResponseStatus.Error -> {
                        Log.e("WeatherFragment", "Error: ${responseStatus.message}")
                    }
                }
            }
        }
        viewModel.get5DaysForecast(latitude, longitude, "58016d418401e5a0e8e9baef8d569514")
    }

    private fun setupRecyclerView(forecastList: List<ForecastList>) {
        val adapter = ForecastAdapter(forecastList)
        binding.recyclerViewForecast.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewForecast.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        updateToolbarTitle()
    }

    private fun updateToolbarTitle() {
        (activity as? AppCompatActivity)?.supportActionBar?.title = responseBuf?.name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getCityName(): String? {
        return responseBuf?.name
    }
}