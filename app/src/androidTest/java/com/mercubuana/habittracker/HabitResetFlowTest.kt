package com.mercubuana.habittracker

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class HabitResetFlowTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun clearState() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext

        // Old (only resets flags):
        // runBlocking { HabitDatabase.getDatabase(ctx).habitDao().resetAllHabits() }

        // **New**: completely drop all rows before each run
        runBlocking {
            HabitDatabase.getDatabase(ctx).clearAllTables()
        }

        // Clear prefs as before
        ctx.getSharedPreferences("HabitPrefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }


    @Test
    fun addCheck_thenNextDay_resetsCheckbox() {
        // 1) Add a habit via FAB + dialog
        onView(withId(R.id.addHabitButton)).perform(click())
        onView(withHint("Enter habit name"))
            .perform(typeText("EspressoHabit"), closeSoftKeyboard())
        onView(withText("Add")).perform(click())

        // 2) Verify it shows and check it
        onView(withText("EspressoHabit")).check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.habitCheckBox),
                hasSibling(withText(containsString("spressoHabit")))
            )
        )
            .perform(click())
            .check(matches(isChecked()))

        // 3) Simulate “next day”
        val yesterday = LocalDate.now().minusDays(1)
            .format(DateTimeFormatter.ISO_DATE)
        val prefs = InstrumentationRegistry
            .getInstrumentation().targetContext
            .getSharedPreferences("HabitPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("last_opened", yesterday).apply()

        // 4) Relaunch activity to trigger reset logic
        activityRule.scenario.recreate()

        // 5) Verify it’s now unchecked
        onView(
            allOf(
                withId(R.id.habitCheckBox),
                hasSibling(withText(containsString("spressoHabit")))
            )
        )
            .check(matches(isNotChecked()))
    }
}
