package com.example.wezzo.screens.home.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.wezzo.model.POJOs.Coord

class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val locations: List<Coord>) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = WeatherFragment()
        fragment.arguments = Bundle().apply {
            putDouble("latitude", locations[position].lat)
            putDouble("longitude", locations[position].lon)
        }
        return fragment
    }
}
