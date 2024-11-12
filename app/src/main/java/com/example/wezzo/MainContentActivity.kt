package com.example.wezzo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.example.wezzo.databinding.ActivityMainBinding
import com.example.wezzo.network.NetworkChangeReceiver
import com.example.wezzo.network.NetworkUtils
import com.google.android.material.snackbar.Snackbar

class MainContentActivity : AppCompatActivity(), NetworkChangeReceiver.NetworkChangeListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private var snackbar: Snackbar? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var isDataReloaded = false
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
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
            showNetworkSettingsSnackbar()
        }
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
            if (!isDataReloaded && counter < 0){
                isDataReloaded = true
            }
        } else {
            showNetworkSettingsSnackbar()
        }
    }
}