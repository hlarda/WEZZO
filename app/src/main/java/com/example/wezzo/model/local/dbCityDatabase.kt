package com.example.wezzo.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [dbCity::class], version = 1)
abstract class dbCityDatabase : RoomDatabase() {
    abstract fun cityDao(): dbCityDao

    companion object {
        @Volatile
        private var INSTANCE: dbCityDatabase? = null

        fun getDatabase(context: Context): dbCityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    dbCityDatabase::class.java,
                    "city_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}