package com.example.wezzo.screens.alarm.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wezzo.R
import com.example.wezzo.model.POJOs.Alarm

class AlarmAdapter(
    private val onDeleteAlarm: (Long) -> Unit // Callback to handle deleting alarms
) : ListAdapter<Alarm, AlarmAdapter.AlarmViewHolder>(AlarmDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = getItem(position)
        holder.bind(alarm)
    }

    inner class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val alarmTimeTextView: TextView = itemView.findViewById(R.id.alarm_time)
        private val alarmType: TextView = itemView.findViewById(R.id.alarm_type)

        fun bind(alarm: Alarm) {
            // Format the alarm time as needed
            alarmTimeTextView.text = formatAlarmTime(alarm.time)
            alarmType.text = alarm.isNotification.toString()
        }

        private fun formatAlarmTime(time: Long): String {
            // Format the time (assuming it's in milliseconds)
            val dateFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
            return dateFormat.format(time)
        }
    }

    class AlarmDiffCallback : DiffUtil.ItemCallback<Alarm>() {
        override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem == newItem
        }
    }
}
