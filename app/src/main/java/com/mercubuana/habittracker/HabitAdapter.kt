package com.mercubuana.habittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat // Import
import java.util.Calendar // Import
import java.util.Locale // Import

class HabitAdapter(
    private val onLongClick: (Habit) -> Unit,
    private val onCheckChanged: (Habit, Boolean) -> Unit // Changed to pass boolean
) : ListAdapter<Habit, HabitAdapter.ViewHolder>(HabitDiffCallback()) {

    // Add SimpleDateFormat for time formatting
    private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.habitName)
        val completedCheckBox: CheckBox = view.findViewById(R.id.habitCheckBox)
        val streakTextView: TextView = view.findViewById(R.id.streakCount) // Add streak
        val reminderInfoTextView: TextView = view.findViewById(R.id.reminderInfo) // Add reminder info
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.habit_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val habit = getItem(position)
        holder.nameTextView.text = habit.name
        holder.streakTextView.text = "Streak: ${habit.streakCount} days" // Set streak text

        // --- Handle CheckBox carefully to avoid triggering listener on bind ---
        holder.completedCheckBox.setOnCheckedChangeListener(null) // Remove listener first
        holder.completedCheckBox.isChecked = habit.isCompleted
        holder.completedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            // Now call the lambda with the habit and the new state
            onCheckChanged(habit, isChecked)
        }
        // --- End CheckBox handling ---

        // Set reminder info
        if (habit.reminderEnabled && habit.reminderTimeMillis != null) {
            val calendar = Calendar.getInstance().apply { timeInMillis = habit.reminderTimeMillis!! }
            holder.reminderInfoTextView.text = "Reminder at ${timeFormatter.format(calendar.time)}"
            holder.reminderInfoTextView.visibility = View.VISIBLE
        } else {
            holder.reminderInfoTextView.visibility = View.GONE
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(habit)
            true
        }
    }

    class HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem == newItem
        }
    }
}