package com.mercubuana.habittracker

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Switch
import android.widget.TimePicker
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
        val timePicker =
            v.findViewById<TimePicker>(R.id.timePicker) // Changed from findViewById to v.findViewById
        timePicker.setIs24HourView(true)

        // ---- If editing, pre-fill
        existing?.let {
            nameET.setText(it.name)
            switchRem.isChecked = it.reminderEnabled
            timePicker.isEnabled = it.reminderEnabled
            it.reminderTimeMillis?.let { ms ->
                val cal = Calendar.getInstance().apply { timeInMillis = ms }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    timePicker.hour = cal.get(Calendar.HOUR_OF_DAY)
                    timePicker.minute = cal.get(Calendar.MINUTE)
                } else {
                    @Suppress("DEPRECATION")
                    timePicker.currentHour = cal.get(Calendar.HOUR_OF_DAY)
                    @Suppress("DEPRECATION")
                    timePicker.currentMinute = cal.get(Calendar.MINUTE)
                }
            }
        }

        switchRem.setOnCheckedChangeListener { _, checked ->
            timePicker.isEnabled = checked
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(if (existing == null) "New Habit" else "Edit Habit")
            .setView(v)
            .setPositiveButton("Save") { _, _ ->
                val cal = Calendar.getInstance().apply {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        set(Calendar.HOUR_OF_DAY, timePicker.hour)
                        set(Calendar.MINUTE, timePicker.minute)
                    } else {
                        @Suppress("DEPRECATION")
                        set(Calendar.HOUR_OF_DAY, timePicker.currentHour)
                        @Suppress("DEPRECATION")
                        set(Calendar.MINUTE, timePicker.currentMinute)
                    }
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val habit = existing ?: Habit(name = "")
                habit.name = nameET.text.toString().trim()
                habit.reminderEnabled = switchRem.isChecked
                habit.reminderTimeMillis =
                    if (switchRem.isChecked) cal.timeInMillis else null
                onSave(habit)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}