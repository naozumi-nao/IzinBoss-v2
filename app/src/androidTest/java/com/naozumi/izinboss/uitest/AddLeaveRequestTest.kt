package com.naozumi.izinboss.uitest

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.naozumi.izinboss.R
import com.naozumi.izinboss.model.helper.EspressoIdlingResource
import com.naozumi.izinboss.view.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddLeaveRequestTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testIfAddLeaveRequestSuccess() {
        onView(ViewMatchers.withId(R.id.fab_add_leave))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("While using the app"))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("Vacation"))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click());
        //onView(withId(R.id.btn_pick_date))
        //.perform(ViewActions.click())
        onView(ViewMatchers.withText("Select date"))
            .perform(PickerActions.setDate(2023, 8, 17))
        onView(ViewMatchers.withText("Select date"))
            .perform(PickerActions.setDate(2023, 8, 20))
        onView(ViewMatchers.withId(R.id.ed_add_reason))
            .perform(ViewActions.typeText("Testing with Espresso"))
        onView(ViewMatchers.withId(R.id.btn_request_leave))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("Testing with Espresso"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}
