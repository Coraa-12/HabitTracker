package com.mercubuana.habittracker

import org.junit.Assert.*
import org.junit.Test

class HabitTest {

    @Test
    fun habitCompletion_shouldToggleCorrectly() {
        val habit = Habit("Test Habit", false)
        assertFalse(habit.isCompleted)

        habit.isCompleted = true
        assertTrue(habit.isCompleted)
    }
}
