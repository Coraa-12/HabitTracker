package com.mercubuana.habittracker

import androidx.room.*

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

}
