package com.mercubuana.habittracker

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HabitDaoTest {

    private lateinit var db: HabitDatabase
    private lateinit var dao: HabitDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HabitDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = db.habitDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndRetrieveHabit() = runBlocking {
        val habit = Habit(name = "Test Habit", isCompleted = false)
        dao.insertHabit(habit)

        val result = dao.getAllHabits()
        assertEquals(1, result.size)
        assertEquals("Test Habit", result[0].name)
    }

    @Test
    fun updateHabitName() = runBlocking {
        val habit = Habit(name = "Old Name", isCompleted = false)
        dao.insertHabit(habit)

        val inserted = dao.getAllHabits().first()
        inserted.name = "Updated Name"
        dao.updateHabit(inserted)

        val result = dao.getAllHabits()
        assertEquals("Updated Name", result[0].name)
    }

    @Test
    fun deleteHabit() = runBlocking {
        val habit = Habit(name = "To Delete", isCompleted = false)
        dao.insertHabit(habit)

        val inserted = dao.getAllHabits().first()
        dao.deleteHabit(inserted)

        val result = dao.getAllHabits()
        assertTrue(result.isEmpty())
    }
}
