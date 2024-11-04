package com.example.wezzo.screens.cities.add.view

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wezzo.R
import com.example.wezzo.databinding.FragmentAddCityBinding
import com.example.wezzo.model.Repository
import com.example.wezzo.model.local.dbCity
import com.example.wezzo.model.local.dbCityDatabase
import com.example.wezzo.model.remote.NetworkService
import com.example.wezzo.viewModel.CityViewModel
import com.example.wezzo.viewModel.ViewModelFactory
import com.example.wezzo.viewModel.WeatherViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddCityFragment : Fragment() {

    private var _binding: FragmentAddCityBinding? = null
    private val binding get() = _binding!!

    private lateinit var suggestionsAdapter: CitySuggestionsAdapter
    private val viewModel: CityViewModel by viewModels {
        ViewModelFactory(
            Repository(
                NetworkService.retrofitService,
                dbCityDatabase.getDatabase(requireContext()).cityDao(),
                requireContext()
            ),
            requireContext()
        )
    }
    private val cityList = listOf(
        dbCity("Cairo", 30.0444, 31.2357, "Egypt"),
        dbCity("Alexandria", 31.2156, 29.9553, "Egypt"),
        dbCity("Giza", 30.0131, 31.2089, "Egypt"),
        dbCity("Shubra El-Kheima", 30.1245, 31.2934, "Egypt"),
        dbCity("Port Said", 31.2653, 32.3019, "Egypt"),
        dbCity("Suez", 29.9668, 32.5498, "Egypt"),
        dbCity("Mansoura", 31.0364, 31.3807, "Egypt"),
        dbCity("El-Mahalla El-Kubra", 30.9706, 31.1669, "Egypt"),
        dbCity("Tanta", 30.7885, 31.0004, "Egypt"),
        dbCity("Asyut", 27.1783, 31.1859, "Egypt"),
        dbCity("Ismailia", 30.5965, 32.2715, "Egypt"),
        dbCity("Faiyum", 29.3102, 30.8418, "Egypt"),
        dbCity("Zagazig", 30.5877, 31.5020, "Egypt"),
        dbCity("Damietta", 31.4165, 31.8133, "Egypt"),
        dbCity("Luxor", 25.6872, 32.6396, "Egypt"),
        dbCity("Qena", 26.1551, 32.7160, "Egypt"),
        dbCity("Beni Suef", 29.0661, 31.0994, "Egypt"),
        dbCity("Sohag", 26.5591, 31.6954, "Egypt"),
        dbCity("Hurghada", 27.2579, 33.8116, "Egypt"),
        dbCity("Sharm El Sheikh", 27.9158, 34.3299, "Egypt"),
        dbCity("New York", 40.7128, -74.0060, "USA"),
        dbCity("Los Angeles", 34.0522, -118.2437, "USA"),
        dbCity("Chicago", 41.8781, -87.6298, "USA"),
        dbCity("Houston", 29.7604, -95.3698, "USA"),
        dbCity("Phoenix", 33.4484, -112.0740, "USA"),

        dbCity("Toronto", 43.6510, -79.3470, "Canada"),
        dbCity("Montreal", 45.5017, -73.5673, "Canada"),
        dbCity("Vancouver", 49.2827, -123.1207, "Canada"),
        dbCity("Calgary", 51.0447, -114.0719, "Canada"),
        dbCity("Edmonton", 53.5461, -113.4938, "Canada"),

        dbCity("London", 51.5074, -0.1278, "United Kingdom"),
        dbCity("Birmingham", 52.4862, -1.8904, "United Kingdom"),
        dbCity("Manchester", 53.4808, -2.2426, "United Kingdom"),
        dbCity("Glasgow", 55.8642, -4.2518, "United Kingdom"),
        dbCity("Liverpool", 53.4084, -2.9916, "United Kingdom"),

        dbCity("Sydney", -33.8688, 151.2093, "Australia"),
        dbCity("Melbourne", -37.8136, 144.9631, "Australia"),
        dbCity("Brisbane", -27.4698, 153.0251, "Australia"),
        dbCity("Perth", -31.9505, 115.8605, "Australia"),
        dbCity("Adelaide", -34.9285, 138.6007, "Australia"),

        dbCity("Mumbai", 19.0760, 72.8777, "India"),
        dbCity("Delhi", 28.6139, 77.2090, "India"),
        dbCity("Bengaluru", 12.9716, 77.5946, "India"),
        dbCity("Hyderabad", 17.3850, 78.4867, "India"),
        dbCity("Ahmedabad", 23.0225, 72.5714, "India"),

        dbCity("Tokyo", 35.6895, 139.6917, "Japan"),
        dbCity("Osaka", 34.6937, 135.5023, "Japan"),
        dbCity("Yokohama", 35.4437, 139.6380, "Japan"),
        dbCity("Nagoya", 35.1815, 136.9066, "Japan"),
        dbCity("Sapporo", 43.0618, 141.3545, "Japan"),

        dbCity("Paris", 48.8566, 2.3522, "France"),
        dbCity("Marseille", 43.2965, 5.3698, "France"),
        dbCity("Lyon", 45.7640, 4.8357, "France"),
        dbCity("Toulouse", 43.6047, 1.4442, "France"),
        dbCity("Nice", 43.7102, 7.2620, "France"),

        dbCity("Berlin", 52.5200, 13.4050, "Germany"),
        dbCity("Hamburg", 53.5511, 9.9937, "Germany"),
        dbCity("Munich", 48.1351, 11.5820, "Germany"),
        dbCity("Cologne", 50.9375, 6.9603, "Germany"),
        dbCity("Frankfurt", 50.1109, 8.6821, "Germany"),

        dbCity("São Paulo", -23.5505, -46.6333, "Brazil"),
        dbCity("Rio de Janeiro", -22.9068, -43.1729, "Brazil"),
        dbCity("Brasília", -15.8267, -47.9218, "Brazil"),
        dbCity("Salvador", -12.9777, -38.5016, "Brazil"),
        dbCity("Fortaleza", -3.7172, -38.5434, "Brazil"),

        dbCity("Beijing", 39.9042, 116.4074, "China"),
        dbCity("Shanghai", 31.2304, 121.4737, "China"),
        dbCity("Shenzhen", 22.5431, 114.0579, "China"),
        dbCity("Guangzhou", 23.1291, 113.2644, "China"),
        dbCity("Chengdu", 30.5728, 104.0668, "China"),

        dbCity("Seoul", 37.5665, 126.9780, "South Korea"),
        dbCity("Busan", 35.1796, 129.0756, "South Korea"),
        dbCity("Incheon", 37.4563, 126.7052, "South Korea"),
        dbCity("Daegu", 35.8714, 128.6014, "South Korea"),
        dbCity("Daejeon", 36.3504, 127.3845, "South Korea"),

        dbCity("Rome", 41.9028, 12.4964, "Italy"),
        dbCity("Milan", 45.4642, 9.1900, "Italy"),
        dbCity("Naples", 40.8518, 14.2681, "Italy"),
        dbCity("Turin", 45.0703, 7.6869, "Italy"),
        dbCity("Palermo", 38.1157, 13.3615, "Italy"),

        dbCity("Mexico City", 19.4326, -99.1332, "Mexico"),
        dbCity("Guadalajara", 20.6597, -103.3496, "Mexico"),
        dbCity("Monterrey", 25.6866, -100.3161, "Mexico"),
        dbCity("Puebla", 19.0414, -98.2063, "Mexico"),
        dbCity("Tijuana", 32.5149, -117.0382, "Mexico")
    )

    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCityBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.chooseFromMapButton.setOnClickListener {
            findNavController().navigate(R.id.action_addCityFragment_to_mapFragment)
        }

        binding.currentLocationButton.setOnClickListener {
            // Fetch the current location
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            } else {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    val bundle = Bundle()
                    bundle.putDouble("latitude", location.latitude)
                    bundle.putDouble("longitude", location.longitude)
                    findNavController().navigate(R.id.action_SearchFragment_to_FirstFragment, bundle)
                }
            }
        }
        setupSearchView()

        return view
    }

    private fun setupSearchView() {
        suggestionsAdapter = CitySuggestionsAdapter(emptyList()) { suggestion ->
            binding.searchView.setQuery(suggestion.name, true)
            binding.suggestionsRecyclerView.visibility = View.GONE
            addCityToDatabase(suggestion)
        }
        binding.suggestionsRecyclerView.adapter = suggestionsAdapter
        binding.suggestionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                newText?.let {
                    searchJob = lifecycleScope.launch {
                        delay(300)  // Debounce to limit the frequency of filtering
                        filterCitySuggestions(it)
                    }
                }
                return true
            }
        })
    }

    private fun filterCitySuggestions(query: String) {
        val filteredCities = cityList.filter { it.name.contains(query, ignoreCase = true) }

        if (filteredCities.isNotEmpty()) {
            binding.suggestionsRecyclerView.visibility = View.VISIBLE
            suggestionsAdapter.updateSuggestions(filteredCities)
        } else {
            binding.suggestionsRecyclerView.visibility = View.GONE
        }
    }

    private fun addCityToDatabase(suggestion: dbCity) {
        lifecycleScope.launch {
            viewModel.insertCity(suggestion)
            Toast.makeText(requireContext(), suggestion.name, Toast.LENGTH_SHORT).show()        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}