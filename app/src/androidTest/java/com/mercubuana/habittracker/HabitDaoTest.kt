package com.mercubuana.habittracker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HabitDaoTest {

    private lateinit var db: HabitDatabase
    private lateinit var dao: HabitDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, HabitDatabase::class.java)
            .allowMainThreadQueries() // For testing only!
            .build()
        dao = db.habitDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertHabit_shouldAppearInDatabase() = runBlocking {
        val habit = Habit(name = "Test Habit", isCompleted = false)
        dao.insertHabit(habit)

        val result = dao.getAllHabits()

        Assert.assertTrue(result.any { it.name == "Test Habit" })
    }
}
