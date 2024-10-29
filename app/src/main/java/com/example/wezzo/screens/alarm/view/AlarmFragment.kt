package com.example.wezzo.screens.alarm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wezzo.R
import com.example.wezzo.databinding.FragmentAlarmBinding
import com.example.wezzo.viewModel.AlarmViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AlarmFragment : Fragment() {

    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AlarmViewModel
    private lateinit var alarmAdapter: AlarmAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        viewModel = AlarmViewModel(requireContext()) // ViewModel injection or creation


        alarmAdapter = AlarmAdapter { alarmId ->
            viewModel.deleteAlarm(alarmId) // Implement delete logic in ViewModel
        }
        binding.alarmRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.alarmRecyclerView.adapter = alarmAdapter

        // Add ItemTouchHelper to handle swipe actions
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val alarm = alarmAdapter.currentList[position]
                showDeleteConfirmationDialog(alarm.id)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.alarmRecyclerView)


        // Collect alarms from Flow
        lifecycleScope.launch {
            viewModel.getAlarms().collect { alarms ->
                alarmAdapter.submitList(alarms) // Update the adapter with the new list of alarms
            }
        }

        binding.addAlarm.setOnClickListener {
            showAlarmSettingsDialog()
        }



        return binding.root
    }

    private fun showAlarmSettingsDialog() {
        val dialog = AlarmSettingsDialog(viewModel)
        dialog.show(parentFragmentManager, "AlarmSettingsDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDeleteConfirmationDialog(alarmId: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Alarm")
            .setMessage("Are you sure you want to delete this alarm?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteAlarm(alarmId)
                AlarmReceiver.cancelAlarm(requireContext(), alarmId)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                alarmAdapter.notifyDataSetChanged()
            }
            .show()
    }
}
