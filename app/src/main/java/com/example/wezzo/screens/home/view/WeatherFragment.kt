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
import com.example.wezzo.databinding.FragmentWeatherBinding
import com.example.wezzo.model.POJOs.Current
import com.example.wezzo.model.Repository
import com.example.wezzo.model.remote.NetworkService
import com.example.wezzo.screens.home.viewModel.WeatherViewModel
import com.example.wezzo.screens.home.viewModel.ViewModelFactory
import com.example.wezzo.screens.home.viewModel.WeatherResponseStatus
import kotlinx.coroutines.launch

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    var responseBuf: Current? = null

    private val viewModel: WeatherViewModel by viewModels {
        ViewModelFactory(
            Repository(NetworkService.retrofitService)
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

        val latitude = arguments?.getDouble("latitude") ?: 0.0
        val longitude = arguments?.getDouble("longitude") ?: 0.0

        lifecycleScope.launch {
            viewModel.weatherResponseStatus.collect { responseStatus ->
                when (responseStatus) {
                    is WeatherResponseStatus.Loading -> {
                        binding.textviewWeather.text = "Loading..."
                    }
                    is WeatherResponseStatus.Success -> {
                        binding.textviewWeather.text = responseStatus.weatherResponse.toString()
                        Log.i("WeatherFragment", "Response: ${responseStatus.weatherResponse}")
                        responseBuf = responseStatus.weatherResponse
                        updateToolbarTitle()
                    }
                    is WeatherResponseStatus.Error -> {
                        binding.textviewWeather.text = responseStatus.message
                        Log.e("WeatherFragment", "Error: ${responseStatus.message}")
                    }
                }
            }
        }

        viewModel.getWeatherByLatAndLong(latitude, longitude, "58016d418401e5a0e8e9baef8d569514")
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