package com.mercubuana.habittracker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE) // Addresses the warning in your logs
class HabitStreakLogicTest {

    private lateinit var db: HabitDatabase
    private lateinit var habitDao: HabitDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Get database reference
        db = HabitDatabase.getDatabase(context)
        habitDao = db.habitDao()

        // Clear tables using IO dispatcher to avoid main thread error
        runBlocking {
            withContext(Dispatchers.IO) {
                db.clearAllTables()
            }
        }
    }

    @After
    fun tearDown() {
        // Also use IO dispatcher here
        runBlocking {
            withContext(Dispatchers.IO) {
                db.clearAllTables()
            }
        }
    }

    @Test
    fun streak_increments_after_next_day_reset() = runBlocking {
        // Run all database operations on IO dispatcher
        withContext(Dispatchers.IO) {
            // Given a new habit
            val habit = Habit(name = "Drink Water")
            habitDao.insertHabit(habit)

            // Get the habit with its generated ID
            val habits = habitDao.getAllHabits()
            val insertedHabit = habits.first { it.name == "Drink Water" }

            // When the habit is completed
            insertedHabit.isCompleted = true
            habitDao.updateHabit(insertedHabit)

            // And a day reset happens
            habitDao.resetDay(insertedHabit.id)

            // Then the streak should be incremented
            val updatedHabit = habitDao.getHabit(insertedHabit.id)
            assertEquals(1, updatedHabit?.streakCount)
        }
    }
}