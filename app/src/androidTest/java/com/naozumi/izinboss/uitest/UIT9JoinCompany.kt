package com.naozumi.izinboss.uitest

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.naozumi.izinboss.R
import com.naozumi.izinboss.model.helper.EspressoIdlingResource
import com.naozumi.izinboss.view.entry.LoginActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class UIT9JoinCompany {
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
    fun testJoinCompany() {
        TestAccount.loginEmployeeUser()

        onView(ViewMatchers.withId(R.id.bottom_nav_company)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_already_have_company)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_company_id_input))
            .perform(ViewActions.typeText("gC5QrLVBrWlNftWaU7jF"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.btn_join_company)).perform(ViewActions.click())
        onView(ViewMatchers.withText("gC5QrLVBrWlNftWaU7jF"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}