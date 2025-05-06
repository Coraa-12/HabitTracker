package com.mercubuana.habittracker

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object ResetUtils {
    private val formatter = DateTimeFormatter.ISO_DATE
    private val today: LocalDate = LocalDate.now()

    fun resetHabitsForNewDay(habits: List<Habit>): List<Habit> {
        return habits.map { habit ->
            val lastDate = habit.lastCompletedDate?.let { LocalDate.parse(it, formatter) }

            // 1) Compute streak
            if (lastDate != null) {
                val daysBetween = ChronoUnit.DAYS.between(lastDate, today)
                habit.streakCount = when {
                    daysBetween == 1L && habit.isCompleted -> habit.streakCount + 1
                    daysBetween != 0L -> 0
                    else -> habit.streakCount
                }
            }

            // 2) Clear completion for new day
            if (lastDate == null || lastDate != today) {
                habit.isCompleted = false
            }

            habit
        }
    }
}

