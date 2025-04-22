package com.mercubuana.habittracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var habitAdapter: HabitAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sample data
        val habitList = mutableListOf(
            Habit("Drink Water", false),
            Habit("Read 10 pages", false),
            Habit("Exercise", false)
        )

        recyclerView = findViewById(R.id.habitRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        habitAdapter = HabitAdapter(habitList)
        recyclerView.adapter = habitAdapter
    }
}
