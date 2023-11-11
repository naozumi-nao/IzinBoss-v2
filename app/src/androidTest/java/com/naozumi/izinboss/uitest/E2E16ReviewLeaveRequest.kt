package com.naozumi.izinboss.uitest

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.naozumi.izinboss.R
import com.naozumi.izinboss.model.helper.EspressoIdlingResource
import com.naozumi.izinboss.view.entry.LoginActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class E2E16ReviewLeaveRequest {
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
    fun testReviewLeaveRequestApprove_E2E16A() {
        TestAccount.loginManagerUser()

        onView(ViewMatchers.withText("Employee 1"))
            .perform(ViewActions.click())

        onView(ViewMatchers.withText("Employee 1"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("VACATION"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("12-12-2023"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("17-12-2023"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("Testing with Espresso"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        onView(ViewMatchers.withId(R.id.btn_approve))
            .perform(ViewActions.click())

        onView(ViewMatchers.withText("Approved"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("Approved by: "))
            .perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.tv_reviewed_by))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("Manager 1"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.tv_reviewed_on))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testReviewLeaveRequestReject_E2E16B() {
        TestAccount.loginManagerUser()

        onView(ViewMatchers.withText("Employee 2"))
            .perform(ViewActions.click())

        onView(ViewMatchers.withText("Employee 2"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("VACATION"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("12-12-2023"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("17-12-2023"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("Testing with Espresso"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        onView(ViewMatchers.withId(R.id.btn_reject))
            .perform(ViewActions.click())

        onView(ViewMatchers.withText("Rejected"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("Rejected by: "))
            .perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.tv_reviewed_by))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("Manager 1"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.tv_reviewed_on))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}