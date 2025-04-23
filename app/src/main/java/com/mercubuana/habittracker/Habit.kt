package com.mercubuana.habittracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var isCompleted: Boolean
)