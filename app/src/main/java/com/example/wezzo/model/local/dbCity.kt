package com.example.wezzo.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class dbCity(
    @PrimaryKey val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String)
