package com.mercubuana.habittracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface HabitDao {

    /* basic CRUD + helpers */

    @Query("SELECT * FROM habit ORDER BY id DESC")
    suspend fun getAllHabits(): List<Habit>

    @Insert
    suspend fun insertHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Query("UPDATE habit SET isCompleted = 0")
    suspend fun resetAllHabits()

    @Query("SELECT * FROM habit WHERE id = :id")
    suspend fun getHabit(id: Int): Habit?

    /* streak helper (unchanged) */
    @Query("UPDATE habit SET streakCount = streakCount + 1 WHERE id = :id")
    suspend fun resetDay(id: Int)

    /* NEW — set or clear a reminder */
    @Query(
        """
        UPDATE habit 
        SET reminderTimeMillis = :time,
            reminderEnabled    = :enabled
        WHERE id = :id
        """
    )
    suspend fun setReminder(id: Int, time: Long?, enabled: Boolean)
}
