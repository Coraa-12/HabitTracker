package com.mercubuana.habittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HabitAdapter(
    private val habits: List<Habit>,
    private val onLongClick: (Int) -> Unit,
    private val onCheckChanged: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    private val dateFormatter = DateTimeFormatter.ISO_DATE

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.habitName)
        val streakTextView: TextView = itemView.findViewById(R.id.streakCount) // New
        val habitCheckBox: CheckBox = itemView.findViewById(R.id.habitCheckBox)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.habit_item, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        // Bind habit name
        holder.nameTextView.text = habit.name

        // Bind streak count
        holder.streakTextView.text = "Streak: ${habit.streakCount} days"  // Bind the streak data

        // Bind completion checkbox state
        holder.habitCheckBox.isChecked = habit.isCompleted

        // Remove any existing listener before setting state to avoid unwanted callbacks
        holder.habitCheckBox.setOnCheckedChangeListener(null)
        holder.habitCheckBox.isChecked = habit.isCompleted

        holder.habitCheckBox.setOnCheckedChangeListener { _, isChecked ->
            habit.isCompleted = isChecked
            if (isChecked) {
                // Record today's date when marked completed
                habit.lastCompletedDate = LocalDate.now().format(dateFormatter)
            }
            onCheckChanged(habit)
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(position)
            true
        }
    }

    override fun getItemCount(): Int = habits.size
}
