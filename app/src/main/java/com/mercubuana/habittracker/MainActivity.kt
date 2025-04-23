package com.mercubuana.habittracker

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var habitDao: HabitDao
    private lateinit var emptyMessage: TextView
    private val habitList = mutableListOf<Habit>()

    private fun updateEmptyMessage() {
        emptyMessage.visibility = if (habitList.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emptyMessage = findViewById(R.id.emptyMessage)

        // Initialize DAO from Room
        val db = HabitDatabase.getDatabase(this)
        habitDao = db.habitDao()

        // Set up RecyclerView
        recyclerView = findViewById(R.id.habitRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        habitAdapter = HabitAdapter(habitList) { position ->
            habitList[position]
            val options = arrayOf("Edit", "Delete")
            AlertDialog.Builder(this)
                .setTitle("Choose Action")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> showEditDialog(position)
                        1 -> confirmDeleteHabit(position)
                    }
                }
                .show()
        }
        recyclerView.adapter = habitAdapter

        // Load existing habits from DB
        lifecycleScope.launch {
            val habitsFromDb = withContext(Dispatchers.IO) {
                habitDao.getAllHabits()
            }
            habitList.addAll(habitsFromDb)
            habitAdapter.notifyItemRangeInserted(0, habitList.size)
            updateEmptyMessage()
        }

        // Add Habit Button
        val addButton = findViewById<FloatingActionButton>(R.id.addHabitButton)
        addButton.setOnClickListener {
            val input = EditText(this)
            input.hint = "Enter habit name"

            AlertDialog.Builder(this)
                .setTitle("New Habit")
                .setView(input)
                .setPositiveButton("Add") { _, _ ->
                    val habitName = input.text.toString()
                    if (habitName.isNotBlank()) {
                        val newHabit = Habit(name = habitName, isCompleted = false)
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                habitDao.insertHabit(newHabit)
                            }
                            habitList.add(newHabit)
                            habitAdapter.notifyItemInserted(habitList.lastIndex)
                            updateEmptyMessage()
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun showEditDialog(position: Int) {
        val habit = habitList[position]
        val input = EditText(this)
        input.setText(habit.name)

        AlertDialog.Builder(this)
            .setTitle("Edit Habit")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val updatedName = input.text.toString()
                if (updatedName.isNotBlank()) {
                    habit.name = updatedName
                    lifecycleScope.launch {
                        habitDao.updateHabit(habit)
                        habitAdapter.notifyItemChanged(position)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDeleteHabit(position: Int) {
        val habit = habitList[position]
        AlertDialog.Builder(this)
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete \"${habit.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    habitDao.deleteHabit(habit)
                    habitList.removeAt(position)
                    habitAdapter.notifyItemRemoved(position)
                    updateEmptyMessage()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
