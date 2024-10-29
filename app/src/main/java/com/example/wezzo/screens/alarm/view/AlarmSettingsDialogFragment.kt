package com.example.wezzo.screens.alarm.view

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.wezzo.R
import com.example.wezzo.viewModel.AlarmViewModel
import java.util.Calendar

class AlarmSettingsDialog(private val viewModel: AlarmViewModel) : DialogFragment() {

    private lateinit var timePicker: TimePicker
    private lateinit var alarmTypeSpinner: Spinner

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater

        val view = inflater.inflate(R.layout.fragment_alarm_settings_dialog, null)
        timePicker = view.findViewById(R.id.time_picker)
        alarmTypeSpinner = view.findViewById(R.id.alarm_type_spinner)

        // Set up alarm types
        val alarmTypes = arrayOf("Notification", "Default Alarm Sound")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, alarmTypes)
        alarmTypeSpinner.adapter = adapter

        builder.setView(view)
            .setTitle("Set Alarm Settings")
            .setPositiveButton("OK") { dialog, _ ->
                setAlarm()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

        return builder.create()
    }

    private fun setAlarm() {
        val hour = timePicker.hour
        val minute = timePicker.minute
        val isNotification = alarmTypeSpinner.selectedItem.toString() == "Notification"

        // Calculate the time in milliseconds from the current time
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        val alarmTimeMillis = calendar.timeInMillis

        // Add the alarm to the ViewModel
        viewModel.addAlarm(alarmTimeMillis, isNotification)

        // Schedule the alarm
        scheduleAlarm(alarmTimeMillis, isNotification)
    }

    private fun scheduleAlarm(timeInMillis: Long, isNotification: Boolean) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                setExactAlarm(alarmManager, timeInMillis, isNotification)
            } else {
                Log.e("AlarmSettingsDialog", "Cannot schedule exact alarms. Permission not granted.")
            }
        } else {
            setExactAlarm(alarmManager, timeInMillis, isNotification)
        }
    }

    private fun setExactAlarm(alarmManager: AlarmManager, timeInMillis: Long, isNotification: Boolean) {
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, timeInMillis)
            putExtra(AlarmReceiver.EXTRA_ALARM_TYPE, if (isNotification) "NOTIFICATION" else "ALARM")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        } catch (e: SecurityException) {
            Log.e("AlarmSettingsDialog", "Failed to schedule exact alarm: ${e.message}")
        }
    }
}
