package com.example.wezzo.screens.home.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wezzo.databinding.CardHourlyForecastBinding
import com.example.wezzo.model.POJOs.ForecastList

class ForecastAdapter(private val forecastList: List<ForecastList>) :
    RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    inner class ForecastViewHolder(private val binding: CardHourlyForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(forecastItem: ForecastList) {
            binding.apply {
                textviewTemp.text = "${forecastItem.main.temp}°C"
                textviewDescription.text = forecastItem.weather[0].description
                textviewDate.text = forecastItem.dtTxt
                Log.i(TAG, "forecastItem.dtTxt: "+ forecastItem.dtTxt)
                Glide.with(imageViewIcon.context)
                    .load("https://openweathermap.org/img/wn/${forecastItem.weather[0].icon}@2x.png")
                    .into(imageViewIcon)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding = CardHourlyForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(forecastList[position])
    }

    override fun getItemCount(): Int = forecastList.size
}