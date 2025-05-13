package com.mercubuana.habittracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity() {

    private lateinit var emptyMessage: TextView
    private lateinit var adapter: HabitAdapter

    // ViewModel with factory injection
    private val vm: MainViewModel by viewModels {
        MainViewModelFactory(
            HabitRepository(HabitDatabase.getDatabase(this).habitDao()),
            getSharedPreferences("HabitPrefs", MODE_PRIVATE)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ask POST_NOTIFICATIONS on Android 13+
        requestNotifPermissionIfNeeded()

        // RecyclerView setup
        adapter = HabitAdapter(
            onLongClick = { habit -> showItemOptions(habit) },
            onCheckChanged = { vm.updateHabit(it) }
        )
        findViewById<RecyclerView>(R.id.habitRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        emptyMessage = findViewById(R.id.emptyMessage)

        // observe state
        lifecycleScope.launchWhenStarted {
            vm.habits.collectLatest { list ->
                adapter.submitList(list)
                emptyMessage.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        // FAB → add-dialog
        findViewById<FloatingActionButton>(R.id.addHabitButton)
            .setOnClickListener { showAddDialog() }

        // once-only notification channel
        createNotificationChannel()
    }

    /* ---------- dialogs ---------- */

    private fun showAddDialog() {
        val input = EditText(this).apply { hint = "Enter habit name" }
        AlertDialog.Builder(this)
            .setTitle("New Habit")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotBlank()) vm.addHabit(name)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showItemOptions(habit: Habit) {
        val opts = arrayOf("Edit", "Delete")
        AlertDialog.Builder(this)
            .setTitle("Choose Action")
            .setItems(opts) { _, which ->
                when (which) {
                    0 -> showEditDialog(habit)
                    1 -> confirmDelete(habit)
                }
            }.show()
    }

    private fun showEditDialog(habit: Habit) {
        val input = EditText(this).apply { setText(habit.name) }
        AlertDialog.Builder(this)
            .setTitle("Edit Habit")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotBlank()) {
                    habit.name = newName
                    vm.updateHabit(habit)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete(habit: Habit) {
        AlertDialog.Builder(this)
            .setTitle("Delete Habit")
            .setMessage("Delete \"${habit.name}\"?")
            .setPositiveButton("Delete") { _, _ -> vm.deleteHabit(habit) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /* ---------- helpers ---------- */

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = android.app.NotificationChannel(
                "habit_channel",
                "Habit Reminders",
                android.app.NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(android.app.NotificationManager::class.java)
                .createNotificationChannel(ch)
        }
    }

    private fun requestNotifPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }
}
