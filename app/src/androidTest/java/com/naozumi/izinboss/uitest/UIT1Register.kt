package com.naozumi.izinboss.uitest

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
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
class UIT1Register {
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
    fun testIfRegisterSuccess() {
        onView(withId(R.id.btn_create_account)).perform(ViewActions.click())
        onView(withId(R.id.ed_register_full_name))
            .perform(ViewActions.typeText("Bima Adityo Kurniawan"))
        onView(withId(R.id.ed_register_email))
            .perform(ViewActions.typeText("test@gmail.com"))
        onView(withId(R.id.ed_register_password))
            .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.btn_register_user)).perform(ViewActions.click())
        onView(withText(R.string.continue_on)).perform(ViewActions.click())
    }
}