package com.mercubuana.habittracker

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build // Import Build
import android.os.Bundle
import android.widget.EditText
import android.widget.Switch
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class AddEditHabitDialogFragment(
    private val onSave: (Habit) -> Unit,
    private val existing: Habit? = null
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = requireActivity().layoutInflater
            .inflate(R.layout.dialog_add_edit_habit, null)

        val nameET = v.findViewById<EditText>(R.id.etHabitName)
        val switchRem = v.findViewById<Switch>(R.id.switchEnableReminder)
        val timePicker = v.findViewById<TimePicker>(R.id.timePicker)
        timePicker.setIs24HourView(true)

        existing?.let {
            nameET.setText(it.name)
            switchRem.isChecked = it.reminderEnabled
            timePicker.isEnabled = it.reminderEnabled
            it.reminderTimeMillis?.let { ms ->
                val cal = Calendar.getInstance().apply { timeInMillis = ms }
                // --- FIX: Use Build check ---
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.hour = cal.get(Calendar.HOUR_OF_DAY)
                    timePicker.minute = cal.get(Calendar.MINUTE)
                } else {
                    @Suppress("DEPRECATION")
                    timePicker.currentHour = cal.get(Calendar.HOUR_OF_DAY)
                    @Suppress("DEPRECATION")
                    timePicker.currentMinute = cal.get(Calendar.MINUTE)
                }
                // --- End FIX ---
            }
        } ?: run {
            // Default for new habits (e.g., set picker to 8:00 AM)
            // --- FIX: Use Build check ---
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.hour = 8
                timePicker.minute = 0
            } else {
                @Suppress("DEPRECATION")
                timePicker.currentHour = 8
                @Suppress("DEPRECATION")
                timePicker.currentMinute = 0
            }
            // --- End FIX ---
        }

        switchRem.setOnCheckedChangeListener { _, checked ->
            timePicker.isEnabled = checked
        }
        timePicker.isEnabled = switchRem.isChecked

        return AlertDialog.Builder(requireContext())
            .setTitle(if (existing == null) "New Habit" else "Edit Habit")
            .setView(v)
            .setPositiveButton("Save") { _, _ ->
                val habitName = nameET.text.toString().trim()

                if (habitName.isBlank()) {
                    Toast.makeText(requireContext(), "Habit name cannot be empty!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val cal = Calendar.getInstance().apply {
                    // --- FIX: Use Build check ---
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        set(Calendar.HOUR_OF_DAY, timePicker.hour)
                        set(Calendar.MINUTE, timePicker.minute)
                    } else {
                        @Suppress("DEPRECATION")
                        set(Calendar.HOUR_OF_DAY, timePicker.currentHour)
                        @Suppress("DEPRECATION")
                        set(Calendar.MINUTE, timePicker.currentMinute)
                    }
                    // --- End FIX ---
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val habitToSave = existing?.copy(
                    name = habitName,
                    reminderEnabled = switchRem.isChecked,
                    reminderTimeMillis = if (switchRem.isChecked) cal.timeInMillis else null
                ) ?: Habit(
                    name = habitName,
                    reminderEnabled = switchRem.isChecked,
                    reminderTimeMillis = if (switchRem.isChecked) cal.timeInMillis else null
                )

                onSave(habitToSave)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}