package com.mercubuana.habittracker

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HabitDaoTest {

    private lateinit var database: HabitDatabase
    private lateinit var dao: HabitDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, HabitDatabase::class.java)
            .allowMainThreadQueries() // okay for testing
            .build()
        dao = database.habitDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertHabit_and_getAll_shouldReturnInsertedHabit() = runBlocking {
        val habit = Habit(name = "Read Book", isCompleted = false)
        dao.insertHabit(habit)

        val allHabits = dao.getAllHabits()
        assertEquals(1, allHabits.size)
        assertEquals("Read Book", allHabits[0].name)
        assertFalse(allHabits[0].isCompleted)
    }

    @Test
    fun deleteHabit_shouldRemoveIt() = runBlocking {
        val habit = Habit(name = "Meditate", isCompleted = false)
        dao.insertHabit(habit)
        dao.deleteHabit(habit)

        val allHabits = dao.getAllHabits()
        assertTrue(allHabits.isEmpty())
    }
}
