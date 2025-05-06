package com.mercubuana.habittracker

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class DailyResetTest {

    @Test
    fun reset_shouldMarkAllHabitsAsNotCompleted_whenNewDay() {
        // Simulate habits completed yesterday
        val yesterday = LocalDate.now().minusDays(1).toString()

        val habit1 = Habit(id = 1, name = "Meditate", isCompleted = true, lastCompletedDate = yesterday, streakCount = 1)
        val habit2 = Habit(id = 2, name = "Drink Water", isCompleted = true, lastCompletedDate = yesterday, streakCount = 5)

        val habits = listOf(habit1, habit2)

        // Simulate daily reset logic
        val today = LocalDate.now().toString()
        val resetHabits = habits.map {
            if (it.lastCompletedDate != today) {
                it.isCompleted = false
            }
            it
        }

        // All habits should be marked as not completed for today
        assertFalse(resetHabits[0].isCompleted)
        assertFalse(resetHabits[1].isCompleted)
    }
}
