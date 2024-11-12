package com.example.wezzo.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager

class NetworkChangeReceiver(private val listener: NetworkChangeListener) : BroadcastReceiver() {

    interface NetworkChangeListener {
        fun onNetworkChange(isConnected: Boolean)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val isConnected = NetworkUtils.isNetworkAvailable(context)
        listener.onNetworkChange(isConnected)
    }

    fun register(context: Context) {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(this, filter)
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(this)
    }
}