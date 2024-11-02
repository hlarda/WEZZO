package com.example.wezzo.screens.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.wezzo.databinding.FragmentMainBinding
import com.example.wezzo.model.POJOs.Coord
import com.example.wezzo.model.Repository
import com.example.wezzo.model.local.dbCityDatabase
import com.example.wezzo.model.remote.NetworkService
import com.example.wezzo.viewModel.WeatherViewModel
import com.example.wezzo.viewModel.ViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentLocation = Coord(lat = arguments?.getDouble("latitude") ?: 51.51, lon = arguments?.getDouble("longitude") ?: -0.13)

        lifecycleScope.launch {
            viewModel.getSavedLocations().collect { savedLocations ->
                val locations = mutableListOf(currentLocation)
                locations.addAll(savedLocations.map { dbCity -> Coord(lat = dbCity.latitude, lon = dbCity.longitude) })
                _binding?.let { setupViewPager(locations) }

                val locationIndex = arguments?.getInt("location_index") ?: 0
                _binding?.viewPager?.currentItem = locationIndex + 1
            }
        }

        // Call the forecast and log the output
        viewModel.get5DaysForecast(currentLocation.lat, currentLocation.lon, "58016d418401e5a0e8e9baef8d569514")

        lifecycleScope.launch {
            viewModel.forecastResponseStatus.collect { responseStatus ->
                when (responseStatus) {
                    is ForecastResponseStatus.Loading -> {
                        // Handle loading state if needed
                    }
                    is ForecastResponseStatus.Success -> {
                        Log.i("MainFragment", "Forecast Response: ${responseStatus.forecastResponse}")
                    }
                    is ForecastResponseStatus.Error -> {
                        Log.e("MainFragment", "Error: ${responseStatus.message}")
                    }
                }
            }
        }
    }

    private fun setupViewPager(locations: List<Coord>) {
        val adapter = ViewPagerAdapter(requireActivity(), locations)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            // Optionally set tab text or icon here
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateTitle(position)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        updateTitle(binding.viewPager.currentItem)
    }

    private fun updateTitle(position: Int) {
        val fragment = childFragmentManager.findFragmentByTag("f$position") as? WeatherFragment
        fragment?.let {
            (activity as? AppCompatActivity)?.supportActionBar?.title = it.getCityName()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}