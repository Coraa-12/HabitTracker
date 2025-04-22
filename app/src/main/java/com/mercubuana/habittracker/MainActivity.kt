package com.mercubuana.habittracker

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var habitList: MutableList<Habit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sample data
        habitList = mutableListOf(
            Habit("Drink Water", false),
            Habit("Read 10 pages", false),
            Habit("Exercise", false)
        )

        // Initialize adapter with long-click support
        habitAdapter = HabitAdapter(habitList) { position ->
            // Show confirmation dialog before deleting
            AlertDialog.Builder(this)
                .setTitle("Delete Habit")
                .setMessage("Are you sure you want to delete \"${habitList[position].name}\"?")
                .setPositiveButton("Delete") { _, _ ->
                    habitList.removeAt(position)
                    habitAdapter.notifyItemRemoved(position)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Set up RecyclerView
        recyclerView = findViewById(R.id.habitRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = habitAdapter

        // Set up FAB to add new habit
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
                        habitList.add(Habit(habitName, false))
                        habitAdapter.notifyItemInserted(habitList.size - 1)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
