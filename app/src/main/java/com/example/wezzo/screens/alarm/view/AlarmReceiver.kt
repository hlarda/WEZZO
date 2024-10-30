package com.example.wezzo.screens.alarm.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.wezzo.R
import com.example.wezzo.model.POJOs.Alarm
import com.example.wezzo.viewModel.AlarmViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1)
        val alarmType = intent.getStringExtra(EXTRA_ALARM_TYPE) ?: "ALARM"

        Log.d("AlarmReceiver", "Alarm triggered: ID $alarmId, Type $alarmType")

        // Start the AlarmService only if the alarm type is "ALARM"
        if (alarmType == "ALARM") {
            val serviceIntent = Intent(context, AlarmService::class.java)
            context.startForegroundService(serviceIntent)
        }

        showAlarmNotification(context, alarmId, alarmType)
        deleteAndCancelAlarm(context, alarmId)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showAlarmNotification(context: Context, alarmId: Long, alarmType: String) {
        val notificationManager = NotificationManagerCompat.from(context)

        // Create the notification channel if necessary
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Alarm Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for alarm notifications"
        }
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.bell)
            .setContentTitle("Alarm Triggered")
            .setContentText("Alarm ID: $alarmId, Type: $alarmType")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (alarmType == "NOTIFICATION") {
            // Use notification sound for "Notification" type
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        } else {
            // Use alarm sound for "Default Alarm Sound" type
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))

            // Add a "Dismiss" action specifically for alarm type
            val dismissIntent = Intent(context, DismissAlarmReceiver::class.java).apply {
                putExtra(EXTRA_ALARM_ID, alarmId)
            }
            val dismissPendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId.toInt(),
                dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            notificationBuilder.addAction(R.drawable.ic_launcher_foreground, "Dismiss", dismissPendingIntent)
        }

        // Show the notification with proper permissions
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(alarmId.toInt(), notificationBuilder.build())
        } else {
            ActivityCompat.requestPermissions(
                (context as Activity),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_POST_NOTIFICATIONS
            )
        }
    }

    companion object {
        const val CHANNEL_ID = "ALARM_CHANNEL"
        const val EXTRA_ALARM_ID = "EXTRA_ALARM_ID"
        const val EXTRA_ALARM_TYPE = "EXTRA_ALARM_TYPE"
        const val REQUEST_CODE_POST_NOTIFICATIONS = 1

        // Schedule an alarm with the specified type
        @SuppressLint("ScheduleExactAlarm")
        fun setAlarm(context: Context, alarm: Alarm) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra(EXTRA_ALARM_ID, alarm.id)
                putExtra(EXTRA_ALARM_TYPE, if (alarm.isNotification) "NOTIFICATION" else "ALARM")
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.time, pendingIntent)
            Log.d("AlarmReceiver", "Alarm set: ID ${alarm.id}, Time ${alarm.time}")
        }

        // Cancel an existing alarm
        fun cancelAlarm(context: Context, alarmId: Long) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            Log.d("AlarmReceiver", "Alarm canceled: ID $alarmId")
        }

        private fun deleteAndCancelAlarm(context: Context, alarmId: Long) {
            // Cancel the alarm
            cancelAlarm(context, alarmId)

            // Delete the alarm from the database
            val viewModel: AlarmViewModel = AlarmViewModel(context)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.deleteAlarm(alarmId)
            }
        }
    }
}