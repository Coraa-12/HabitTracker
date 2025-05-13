package com.mercubuana.habittracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class HabitAdapter(
    private val onLongClick: (Habit) -> Unit,
    private val onCheckChanged: (Habit) -> Unit
) : ListAdapter<Habit, HabitAdapter.ViewHolder>(HabitDiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.habitName)
        val completedCheckBox: CheckBox = view.findViewById(R.id.habitCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.habit_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val habit = getItem(position)
        holder.nameTextView.text = habit.name
        holder.completedCheckBox.isChecked = habit.isCompleted

        holder.itemView.setOnLongClickListener {
            onLongClick(habit)
            true
        }

        holder.completedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val updatedHabit = habit.copy(isCompleted = isChecked)
            onCheckChanged(updatedHabit)
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