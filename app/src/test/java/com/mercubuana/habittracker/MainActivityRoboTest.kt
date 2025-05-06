package com.mercubuana.habittracker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainActivityRoboTest {
    private val ctx = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun clearDatabase() = runBlocking {
        HabitDatabase.getDatabase(ctx).clearAllTables()
        ctx.getSharedPreferences("HabitPrefs", Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @Test
    fun resetFlow_clearsCheckbox() {
        // 1) Launch
        val activity = Robolectric.buildActivity(MainActivity::class.java).setup().get()
        // 2) Add a habit manually via DAO (faster than dialog)
        runBlocking {
            HabitDatabase.getDatabase(ctx).habitDao()
                .insertHabit(Habit(name = "RoboHabit"))
        }
        // 3) Recreate to trigger reset logic
        val scenario = Robolectric.buildActivity(MainActivity::class.java)
            .create().resume().get()
        // 4) Find the only habit’s checkbox and assert it’s unchecked
        val checkbox = scenario.findViewById<android.widget.CheckBox>(R.id.habitCheckBox)
        assertFalse("Checkbox should be reset on new day", checkbox.isChecked)
    }
}
