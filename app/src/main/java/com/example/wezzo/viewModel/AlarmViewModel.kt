package com.example.wezzo.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wezzo.model.POJOs.Alarm
import com.example.wezzo.model.local.AlarmDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AlarmViewModel(context: Context) : ViewModel() {
    private val alarmDao = AlarmDatabase.getInstance(context).alarmDao()

    fun addAlarm(alarmTime: Long, isNotification: Boolean) {
        viewModelScope.launch {
            alarmDao.insertAlarm(Alarm(time = alarmTime, isNotification = isNotification))
        }
    }

    fun getAlarms(): Flow<List<Alarm>> {
        return alarmDao.getAllAlarms()
    }

    fun deleteAlarm(alarmId: Long) {
        viewModelScope.launch {
            alarmDao.deleteAlarm(alarmId)
        }
    }
}
