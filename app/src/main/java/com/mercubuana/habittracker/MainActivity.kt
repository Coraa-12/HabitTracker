package com.mercubuana.habittracker

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.TextView

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
            val habitToDelete = habitList[position]
            AlertDialog.Builder(this)
                .setTitle("Delete Habit")
                .setMessage("Are you sure you want to delete \"${habitToDelete.name}\"?")
                .setPositiveButton("Delete") { _, _ ->
                    lifecycleScope.launch {
                        habitDao.deleteHabit(habitToDelete)
                        habitList.removeAt(position)
                        habitAdapter.notifyItemRemoved(position)
                        updateEmptyMessage()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        recyclerView.adapter = habitAdapter

        // Load existing habits from DB
        lifecycleScope.launch {
            val habitsFromDb = withContext(Dispatchers.IO) {
                habitDao.getAllHabits()
            }
            habitList.addAll(habitsFromDb)
            habitAdapter.notifyDataSetChanged()
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
                            val id = withContext(Dispatchers.IO) {
                                habitDao.insertHabit(newHabit)
                                habitDao.getAllHabits()
                            }
                            habitList.clear()
                            habitList.addAll(habitDao.getAllHabits())
                            habitAdapter.notifyDataSetChanged()
                            updateEmptyMessage()
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
