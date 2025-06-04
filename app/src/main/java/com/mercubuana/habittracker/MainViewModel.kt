package com.mercubuana.habittracker

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(
    private val repo: HabitRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

    val habits: MutableStateFlow<List<Habit>> = MutableStateFlow(emptyList())
    private val todayString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    init {
        checkAndRefresh() // Renamed for clarity
    }

    private fun checkAndRefresh() = viewModelScope.launch {
        if (prefs.getString("last_opened", null) != todayString) {
            habits.value = repo.resetAllForNewDay()
            prefs.edit().putString("last_opened", todayString).apply()
        } else {
            habits.value = repo.allHabits()
        }
    }

    // New unified save function
    fun saveHabit(habit: Habit) = viewModelScope.launch {
        if (habit.id == 0) { // If id is 0, it's a new habit
            repo.insert(habit)
        } else { // Otherwise, it's an update
            repo.update(habit)
        }
        habits.value = repo.allHabits() // Refresh list
        // TODO: Schedule/Cancel reminder using WorkManager here!
    }

    // Updated updateHabit for checkbox changes
    fun updateHabitCompletion(habit: Habit, isCompleted: Boolean) = viewModelScope.launch {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val updatedHabit = habit.copy(
            isCompleted = isCompleted,
            // Update lastCompletedDate only when checking it ON
            lastCompletedDate = if (isCompleted) today else habit.lastCompletedDate
        )
        repo.update(updatedHabit)
        habits.value = repo.allHabits() // Refresh list
    }

    fun deleteHabit(h: Habit) = viewModelScope.launch {
        repo.delete(h)
        habits.value = repo.allHabits() // Refresh list
        // TODO: Cancel reminder using WorkManager here!
    }

    // Remove the old addHabit(name: String)
    // fun addHabit(name: String) ...
}