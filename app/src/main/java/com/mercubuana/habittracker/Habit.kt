package com.mercubuana.habittracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var isCompleted: Boolean = false,

    // New properties
    var lastCompletedDate: String? = null, // Use ISO format (e.g., "2025-04-23")
    var streakCount: Int = 0
)
