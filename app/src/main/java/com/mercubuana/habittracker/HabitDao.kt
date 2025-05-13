package com.mercubuana.habittracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface HabitDao {

    @Query("SELECT * FROM Habit")
    suspend fun getAllHabits(): List<Habit>

    @Insert
    suspend fun insertHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Query("UPDATE Habit SET isCompleted = 0")
    suspend fun resetAllHabits()

    @Query("SELECT * FROM habit WHERE id = :id")
    suspend fun getHabit(id: Int): Habit?

    // Update streakCount (increment the streak)
    @Query("UPDATE habit SET streakCount = streakCount + 1 WHERE id = :id")
    suspend fun resetDay(id: Int)
}
