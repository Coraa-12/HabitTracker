package com.mercubuana.habittracker

// data/HabitRepository.kt
class HabitRepository(private val dao: HabitDao) {

    suspend fun allHabits(): List<Habit> = dao.getAllHabits()
    suspend fun insert(h: Habit) = dao.insertHabit(h)
    suspend fun update(h: Habit) = dao.updateHabit(h)
    suspend fun delete(h: Habit) = dao.deleteHabit(h)

    suspend fun resetAllForNewDay(): List<Habit> {
        val list = dao.getAllHabits()
        val reset = ResetUtils.resetHabitsForNewDay(list)
        reset.forEach { dao.updateHabit(it) }
        return reset
    }
}
