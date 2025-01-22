package com.naozumi.izinboss.uitest

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.naozumi.izinboss.R
import com.naozumi.izinboss.model.helper.EspressoIdlingResource
import com.naozumi.izinboss.view.entry.LoginActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class E2E15CheckLeaveRequestsList {
    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testCheckLeaveRequestListManagerEmpty_E2E15A() {
        TestAccount.loginManager3User()

        onView(ViewMatchers.withId(R.id.anim_empty_list))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testCheckLeaveRequestListEmployeeEmpty_E2E15B() {
        TestAccount.loginEmployee2User()

        onView(ViewMatchers.withId(R.id.anim_empty_list))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testCheckLeaveRequestListUnregistered_E2E15C() {
        TestAccount.loginEmployee3User()

        onView(ViewMatchers.withId(R.id.anim_empty_list))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testCheckLeaveRequestListManagerValid_E2E15D() {
        TestAccount.loginManagerUser()

        onView(ViewMatchers.withId(R.id.rv_leave_requests))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testCheckLeaveRequestListEmployeeValid_E2E15E() {
        TestAccount.loginEmployeeUser()

        onView(ViewMatchers.withId(R.id.rv_leave_requests))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}