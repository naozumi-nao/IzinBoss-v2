package com.naozumi.izinboss.uitest

import androidx.test.espresso.Espresso
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
class E2E1Register {
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
    fun testRegisterValid_E2E1A() {
        onView(ViewMatchers.withId(R.id.btn_create_account)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_register_full_name))
            .perform(ViewActions.typeText("Bima Adityo Kurniawan"))
        onView(ViewMatchers.withId(R.id.ed_register_email))
            .perform(ViewActions.typeText("test@gmail.com"))
        onView(ViewMatchers.withId(R.id.ed_register_password))
            .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.btn_register_user)).perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.continue_on)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_login))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testRegisterEmpty_E2E1B() {
        onView(ViewMatchers.withId(R.id.btn_create_account)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_register_user)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_register_full_name))
            .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Please input your name")))
    }

    @Test
    fun testRegisterEmptyEmail_E2E1C() {
        onView(ViewMatchers.withId(R.id.btn_create_account)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_register_full_name))
            .perform(ViewActions.typeText("Bima Adityo Kurniawan"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.btn_register_user)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_register_email))
            .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Please input your email address")))
    }

    @Test
    fun testRegisterInvalidEmail_E2E1D() {
        onView(ViewMatchers.withId(R.id.btn_create_account)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_register_full_name))
            .perform(ViewActions.typeText("Bima Adityo Kurniawan"))
        onView(ViewMatchers.withId(R.id.ed_register_email))
            .perform(ViewActions.typeText("abcdefg"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.btn_register_user)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_register_email))
            .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Invalid Email")))
    }

    @Test
    fun testRegisterEmptyPassword_E2E1E() {
        onView(ViewMatchers.withId(R.id.btn_create_account)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_register_full_name))
            .perform(ViewActions.typeText("Bima Adityo Kurniawan"))
        onView(ViewMatchers.withId(R.id.ed_register_email))
            .perform(ViewActions.typeText("bimaadityokurniawan@gmail.com"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.btn_register_user)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_register_password))
            .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Please input your password")))
    }

    @Test
    fun testRegisterLessThan8Password_E2E1F() {
        onView(ViewMatchers.withId(R.id.btn_create_account)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_register_full_name))
            .perform(ViewActions.typeText("Bima Adityo Kurniawan"))
        onView(ViewMatchers.withId(R.id.ed_register_email))
            .perform(ViewActions.typeText("bimaadityokurniawan@gmail.com"))
        onView(ViewMatchers.withId(R.id.ed_register_password))
            .perform(ViewActions.typeText("123"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.btn_register_user)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_register_password))
            .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Password must not be less than 8 characters")))
    }

    @Test
    fun testRegisterEmailAlreadyUsed_E2E1G() {
        onView(ViewMatchers.withId(R.id.btn_create_account)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_register_full_name))
            .perform(ViewActions.typeText("Bima Adityo Kurniawan"))
        onView(ViewMatchers.withId(R.id.ed_register_email))
            .perform(ViewActions.typeText("bimaadityokurniawan@gmail.com"))
        onView(ViewMatchers.withId(R.id.ed_register_password))
            .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.btn_register_user)).perform(ViewActions.click())
        onView(ViewMatchers.withText("The email address is already in use by another account."))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}