package com.example.wezzo.screens.alarm.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.wezzo.viewModel.AlarmViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DismissAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(AlarmReceiver.EXTRA_ALARM_ID, -1)
        Log.d("DismissAlarmReceiver", "Alarm dismissed: ID $alarmId")

        // Stop the AlarmService
        val serviceIntent = Intent(context, AlarmService::class.java)
        context.stopService(serviceIntent)

        // Cancel the alarm
        AlarmReceiver.cancelAlarm(context, alarmId)

        // Delete the alarm from the database
        val viewModel: AlarmViewModel = AlarmViewModel(context)
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.deleteAlarm(alarmId)
        }
    }
}