package com.example.wezzo

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.wezzo.databinding.ActivityMainBinding
import com.example.wezzo.model.remote.NetworkChangeReceiver
import com.example.wezzo.model.remote.NetworkUtils
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), NetworkChangeReceiver.NetworkChangeListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var menu: Menu? = null
    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private var snackbar: Snackbar? = null

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
        }

        navController.addOnDestinationChangedListener() { _, destination, _ ->
            menu?.findItem(R.id.action_add_city)?.isVisible = destination.id == R.id.LocationFragment || destination.id == R.id.FirstFragment
        }

        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_MainFragment_to_AlarmFragment)
        }

        networkChangeReceiver = NetworkChangeReceiver(this)
        checkNetworkAndDisplayMessages()
    }

    override fun onResume() {
        super.onResume()
        networkChangeReceiver.register(this)
    }

    override fun onPause() {
        super.onPause()
        networkChangeReceiver.unregister(this)
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
        return currentDestination?.id == R.id.SearchFragment
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
                navController.navigate(R.id.SearchFragment)
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
}