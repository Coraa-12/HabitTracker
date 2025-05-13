package com.mercubuana.habittracker

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ui/main/MainViewModel.kt
class MainViewModel(
    private val repo: HabitRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

    val habits: MutableStateFlow<List<Habit>> = MutableStateFlow(emptyList())

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (prefs.getString("last_opened", null) != today) {
            habits.value = repo.resetAllForNewDay()
            prefs.edit().putString("last_opened", today).apply()
        } else {
            habits.value = repo.allHabits()
        }
    }

    fun addHabit(name: String) = viewModelScope.launch {
        val h = Habit(name = name)
        repo.insert(h)
        habits.value = repo.allHabits()
    }

    fun updateHabit(h: Habit) = viewModelScope.launch {
        repo.update(h)
        habits.value = repo.allHabits()
    }

    fun deleteHabit(h: Habit) = viewModelScope.launch {
        repo.delete(h)
        habits.value = repo.allHabits()
    }
}
