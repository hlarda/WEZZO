package com.example.wezzo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.wezzo.databinding.ActivityMainBinding
import com.example.wezzo.model.remote.NetworkChangeReceiver
import com.example.wezzo.model.remote.NetworkUtils
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar

class MainContentActivity : AppCompatActivity(), NetworkChangeReceiver.NetworkChangeListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var menu: Menu? = null
    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private var snackbar: Snackbar? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var hasNavigatedToHome = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            menu?.findItem(R.id.action_location)?.isVisible = destination.id == R.id.FirstFragment
            menu?.findItem(R.id.action_add_city)?.isVisible = destination.id == R.id.LocationFragment || destination.id == R.id.FirstFragment
            updateFloatingVisibility(destination.id)
        }

        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_MainFragment_to_AlarmFragment)
        }

        networkChangeReceiver = NetworkChangeReceiver(this)
        checkNetworkAndDisplayMessages()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null && !hasNavigatedToHome) {
                        hasNavigatedToHome = true
                        navigateToMainFragment(location.latitude, location.longitude)
                    }
                }
            }
        }

        if (checkPermissions()) {
            startLocationUpdates()
        } else {
            requestPermissions()
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
//            interval = 1800000 // 30 minutes
//            fastestInterval = 900000 // 15 minutes
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            interval = 2000
            fastestInterval = 100
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        try {
            if (checkPermissions()) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            } else {
                requestPermissions()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun navigateToMainFragment(latitude: Double, longitude: Double) {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val bundle = Bundle().apply {
            putDouble("latitude", latitude)
            putDouble("longitude", longitude)
        }
        navController.navigate(R.id.FirstFragment, bundle)
    }

    private fun checkPermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            startLocationUpdates()
        } else {
            requestPermissions()
        }
    }

    override fun onResume() {
        super.onResume()
        networkChangeReceiver.register(this)
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        networkChangeReceiver.unregister(this)
        stopLocationUpdates()
    }

    private fun checkNetworkAndDisplayMessages() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Please provide network access", Toast.LENGTH_LONG).show()
            if (isSearchFragmentLoaded()) {
                showNetworkSettingsSnackbar()
            }
        }
    }

    private fun isSearchFragmentLoaded(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val currentDestination = navController.currentDestination
        return currentDestination?.id == R.id.addCityFragment
    }

    private fun showNetworkSettingsSnackbar() {
        snackbar = Snackbar.make(binding.root, "No network connection", Snackbar.LENGTH_INDEFINITE)
            .setAction("Settings") {
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(intent)
            }
        snackbar?.show()
    }

    override fun onNetworkChange(isConnected: Boolean) {
        if (isConnected) {
            snackbar?.dismiss()
        } else if (isSearchFragmentLoaded()) {
            showNetworkSettingsSnackbar()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_location -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.LocationFragment)
                true
            }
            R.id.action_settings -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.SettingsFragment)
                true
            }
            R.id.action_add_city -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.addCityFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun updateFloatingVisibility(destinationId: Int) {
        binding.fab.visibility = if ((destinationId == R.id.mapFragment) || (destinationId == R.id.SecondFragment)) View.GONE else View.VISIBLE
    }
}