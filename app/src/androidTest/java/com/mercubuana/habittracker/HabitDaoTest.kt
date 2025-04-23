package com.mercubuana.habittracker

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HabitDaoTest {

    private lateinit var db: HabitDatabase
    private lateinit var habitDao: HabitDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, HabitDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        habitDao = db.habitDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun deleteHabit_shouldRemoveIt() = runBlocking {
        val habit = Habit(name = "Test Habit", isCompleted = false)
        habitDao.insertHabit(habit)

        val inserted = habitDao.getAllHabits().first { it.name == "Test Habit" }

        habitDao.deleteHabit(inserted)
        val allHabits = habitDao.getAllHabits()
        Assert.assertTrue(allHabits.none { it.id == inserted.id })
    }
}
