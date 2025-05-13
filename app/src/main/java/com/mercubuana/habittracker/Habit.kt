package com.mercubuana.habittracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var isCompleted: Boolean = false,
    var lastCompletedDate: String? = null,
    var streakCount: Int = 0,

    /* NEW ↓ */
    var reminderTimeMillis: Long? = null,      // e.g. 08:30 → calendar.timeInMillis
    var reminderEnabled: Boolean = false
)
