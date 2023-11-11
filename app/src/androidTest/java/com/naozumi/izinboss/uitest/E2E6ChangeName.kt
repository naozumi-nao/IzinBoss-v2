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
class E2E6ChangeName {
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
    fun testChangeNameValid_E2E6A() {
        TestAccount.loginGenericUser()

        onView(ViewMatchers.withId(R.id.bottom_nav_profile))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_profile_info))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_new_name_input))
            .perform(ViewActions.typeText("Bima A.K."))
        onView(ViewMatchers.withId(R.id.btn_confirm_new_name))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.tv_full_name_input))
            .check(ViewAssertions.matches(ViewMatchers.withText("Bima A.K.")))
    }

    @Test
    fun testChangeNameEmpty_E2E6B() {
        TestAccount.loginGenericUser()

        onView(ViewMatchers.withId(R.id.bottom_nav_profile))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_profile_info))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_new_name_input))
            .perform(ViewActions.typeText(""))
        onView(ViewMatchers.withId(R.id.btn_confirm_new_name))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
    }
}