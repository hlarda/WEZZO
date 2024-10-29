package com.example.wezzo.screens.alarm.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log

class DismissAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(AlarmReceiver.EXTRA_ALARM_ID, -1)
        Log.d("DismissAlarmReceiver", "Alarm dismissed: ID $alarmId")

        AlarmReceiver.cancelAlarm(context, alarmId)

        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
        if (ringtone.isPlaying) {
            ringtone.stop()
        }
    }
}
