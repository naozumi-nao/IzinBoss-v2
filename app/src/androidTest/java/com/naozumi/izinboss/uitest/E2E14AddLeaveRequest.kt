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

class E2E14AddLeaveRequest {
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
    fun testAddLeaveRequestValid_E2E14A() {
        TestAccount.loginEmployeeUser()
        onView(ViewMatchers.withId(R.id.fab_add_leave_request))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.actv_add_type))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("Vacation"))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.ed_start_date_input))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_start_date_input))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(com.google.android.material.R.id.mtrl_picker_header_toggle))
            .perform(ViewActions.click())
        onView(ViewMatchers.withHint("Start date"))
            .perform(
                ViewActions.replaceText("12/12/23"),
                ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withHint("End date"))
            .perform(
                ViewActions.replaceText("12/17/23"),
                ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(com.google.android.material.R.id.confirm_button))
            .perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.ed_add_reason))
            .perform(ViewActions.typeText("Testing with Espresso"))
        onView(ViewMatchers.withId(R.id.btn_request_leave))
            .perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.rv_leave_requests))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("VACATION"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("12-12-2023"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("17-12-2023"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("Testing with Espresso"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testAddLeaveRequestMoreThan100CharacterReason_E2E14B() {
        TestAccount.loginEmployeeUser()
        onView(ViewMatchers.withId(R.id.fab_add_leave_request))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_add_reason)) // 102 characters
            .perform(ViewActions.typeText("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has always been"))
        onView(ViewMatchers.withId(R.id.ed_add_reason)) // 100 characters displayed
            .check(ViewAssertions.matches(ViewMatchers.withText("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has always be")))
    }

    @Test
    fun testAddLeaveRequestEmptyFields_E2E14C() {
        TestAccount.loginEmployeeUser()
        onView(ViewMatchers.withId(R.id.fab_add_leave_request))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_request_leave))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
    }

    @Test
    fun testAddLeaveRequestEmptyDateAndReason_E2E14D() {
        TestAccount.loginEmployeeUser()
        onView(ViewMatchers.withId(R.id.fab_add_leave_request))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.actv_add_type))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("Vacation"))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_request_leave))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
    }

    @Test
    fun testAddLeaveRequestEmptyReason_E2E14E() {
        TestAccount.loginEmployeeUser()
        onView(ViewMatchers.withId(R.id.fab_add_leave_request))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.actv_add_type))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("Vacation"))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.ed_start_date_input))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_start_date_input))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(com.google.android.material.R.id.mtrl_picker_header_toggle))
            .perform(ViewActions.click())
        onView(ViewMatchers.withHint("Start date"))
            .perform(
                ViewActions.replaceText("12/12/23"),
                ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withHint("End date"))
            .perform(
                ViewActions.replaceText("12/17/23"),
                ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(com.google.android.material.R.id.confirm_button))
            .perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.btn_request_leave))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
    }
}