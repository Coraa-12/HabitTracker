package com.mercubuana.habittracker

import org.junit.Assert.*
import org.junit.Test

class HabitTest {

    @Test
    fun habitCompletion_shouldToggleCorrectly() {
        val habit = Habit(name = "Test Habit", isCompleted = false)
        assertFalse(habit.isCompleted)

        habit.isCompleted = true
        assertTrue(habit.isCompleted)
    }

    @Test
    fun habitEdit_shouldUpdateNameCorrectly() {
        val habit = Habit(name = "Old Name", isCompleted = false)

        habit.name = "New Name"

        assertEquals("New Name", habit.name)
    }

    @Test
    fun habitToggle_shouldReflectLatestState() {
        val habit = Habit(name = "Meditate", isCompleted = false)

        habit.isCompleted = true
        habit.isCompleted = false

        assertFalse(habit.isCompleted)
    }
}
