package com.mercubuana.habittracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
// Removed: import android.widget.EditText // No longer needed here
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog // Still need for delete confirmation
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

    private val vm: MainViewModel by viewModels {
        MainViewModelFactory(
            HabitRepository(HabitDatabase.getDatabase(this).habitDao()),
            getSharedPreferences("HabitPrefs", MODE_PRIVATE)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestNotifPermissionIfNeeded()

        // RecyclerView setup - Updated onCheckChanged lambda
        adapter = HabitAdapter(
            onLongClick = { habit -> showItemOptions(habit) },
            onCheckChanged = { habit, isChecked -> vm.updateHabitCompletion(habit, isChecked) }
        )
        findViewById<RecyclerView>(R.id.habitRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        emptyMessage = findViewById(R.id.emptyMessage)

        lifecycleScope.launchWhenStarted {
            vm.habits.collectLatest { list ->
                adapter.submitList(list)
                emptyMessage.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        // FAB → add-dialog (Updated to call new function)
        findViewById<FloatingActionButton>(R.id.addHabitButton)
            .setOnClickListener { showAddEditDialog(null) } // Pass null for new habit

        createNotificationChannel()
    }

    /* ---------- NEW UNIFIED DIALOG FUNCTION ---------- */

    private fun showAddEditDialog(habitToEdit: Habit?) {
        val dialog = AddEditHabitDialogFragment(
            onSave = { habit ->
                vm.saveHabit(habit) // Use the new unified save function
            },
            existing = habitToEdit // Pass the habit if editing, or null if adding
        )
        dialog.show(supportFragmentManager, "AddEditHabitDialog")
    }

    /* ---------- DIALOGS (Updated/Removed) ---------- */

    // REMOVED: showAddDialog() - Replaced by showAddEditDialog(null)
    // REMOVED: showEditDialog(habit: Habit) - Replaced by showAddEditDialog(habit)

    private fun showItemOptions(habit: Habit) {
        val opts = arrayOf("Edit", "Delete")
        AlertDialog.Builder(this)
            .setTitle("Choose Action")
            .setItems(opts) { _, which ->
                when (which) {
                    0 -> showAddEditDialog(habit) // Call the new dialog for editing
                    1 -> confirmDelete(habit)
                }
            }.show()
    }

    // confirmDelete remains mostly the same
    private fun confirmDelete(habit: Habit) {
        AlertDialog.Builder(this)
            .setTitle("Delete Habit")
            .setMessage("Delete \"${habit.name}\"?")
            .setPositiveButton("Delete") { _, _ -> vm.deleteHabit(habit) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /* ---------- helpers (Unchanged) ---------- */

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