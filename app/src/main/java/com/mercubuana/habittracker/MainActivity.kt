package com.mercubuana.habittracker

import android.content.Context
import android.os.Bundle
import android.view.View
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var habitDao: HabitDao
    private lateinit var emptyMessage: TextView
    private lateinit var addButton: FloatingActionButton
    private val habitList = mutableListOf<Habit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find views
        emptyMessage = findViewById(R.id.emptyMessage)
        recyclerView = findViewById(R.id.habitRecyclerView)
        addButton = findViewById(R.id.addHabitButton)

        // Initialize DAO
        val db = HabitDatabase.getDatabase(this)
        habitDao = db.habitDao()

        // Setup RecyclerView & Adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        habitAdapter = HabitAdapter(
            habits = habitList,
            onLongClick = { position ->
                showItemOptions(position)
            },
            onCheckChanged = { habit ->
                // Persist completion and date
                lifecycleScope.launch(Dispatchers.IO) {
                    habitDao.updateHabit(habit)
                }
            }
        )
        recyclerView.adapter = habitAdapter

        // Load, reset, persist, and display
        lifecycleScope.launch {
            // 1) Load from DB
            val fromDb = withContext(Dispatchers.IO) {
                habitDao.getAllHabits()
            }
            // 2) Reset for new day and compute streaks
            val resetList = ResetUtils.resetHabitsForNewDay(fromDb)
            // 3) Persist any updates (isCompleted, streakCount)
            withContext(Dispatchers.IO) {
                resetList.forEach { habitDao.updateHabit(it) }
            }
            // 4) Update last opened date
            val prefs = getSharedPreferences("HabitPrefs", Context.MODE_PRIVATE)
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Date())
            prefs.edit().putString("last_opened", todayStr).apply()
            // 5) Display
            habitList.clear()
            habitList.addAll(resetList)
            habitAdapter.notifyDataSetChanged()
            updateEmptyMessage()
        }

        // Add new habit
        addButton.setOnClickListener {
            val input = EditText(this)
            input.hint = "Enter habit name"

            AlertDialog.Builder(this)
                .setTitle("New Habit")
                .setView(input)
                .setPositiveButton("Add") { _, _ ->
                    val name = input.text.toString().trim()
                    if (name.isNotBlank()) {
                        val newHabit = Habit(name = name)
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

    private fun showItemOptions(position: Int) {
        val habit = habitList[position]
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

    private fun showEditDialog(position: Int) {
        val habit = habitList[position]
        val input = EditText(this)
        input.setText(habit.name)

        AlertDialog.Builder(this)
            .setTitle("Edit Habit")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val updated = input.text.toString().trim()
                if (updated.isNotBlank()) {
                    habit.name = updated
                    lifecycleScope.launch(Dispatchers.IO) {
                        habitDao.updateHabit(habit)
                    }
                    habitAdapter.notifyItemChanged(position)
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
                lifecycleScope.launch(Dispatchers.IO) {
                    habitDao.deleteHabit(habit)
                }
                habitList.removeAt(position)
                habitAdapter.notifyItemRemoved(position)
                updateEmptyMessage()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateEmptyMessage() {
        emptyMessage.visibility = if (habitList.isEmpty()) View.VISIBLE else View.GONE
    }
}
