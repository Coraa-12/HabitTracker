package com.mercubuana.habittracker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ResetUtilsTest {
    private val fmt = DateTimeFormatter.ISO_DATE

    @Test
    fun `reset on new day clears completion and resets streak`() {
        val yesterday = LocalDate.now().minusDays(1).format(fmt)
        val h = Habit(
            id = 1,
            name = "X",
            isCompleted = true,
            lastCompletedDate = yesterday,
            streakCount = 3
        )
        val out = ResetUtils.resetHabitsForNewDay(listOf(h)).first()
        assertFalse("should clear isCompleted", out.isCompleted)
        assertEquals("since last was yesterday and was completed", 4, out.streakCount)
    }

    @Test
    fun `skip same-day does not reset streak`() {
        val today = LocalDate.now().format(fmt)
        val h = Habit(
            id = 1,
            name = "X",
            isCompleted = true,
            lastCompletedDate = today,
            streakCount = 2
        )
        val out = ResetUtils.resetHabitsForNewDay(listOf(h)).first()
        assertTrue("keep completion for same day", out.isCompleted)
        assertEquals("streak unchanged", 2, out.streakCount)
    }

    @Test
    fun `long gap resets streak`() {
        val older = LocalDate.now().minusDays(5).format(fmt)
        val h = Habit(
            id = 1,
            name = "X",
            isCompleted = true,
            lastCompletedDate = older,
            streakCount = 7
        )
        val out = ResetUtils.resetHabitsForNewDay(listOf(h)).first()
        assertFalse("isCompleted cleared", out.isCompleted)
        assertEquals("gap >1 day resets streak", 0, out.streakCount)
    }
}
