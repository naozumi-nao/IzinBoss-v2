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
class E2E2Login {
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
    fun testLoginValid_E2E2A() {
        onView(ViewMatchers.withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("test@gmail.com"))
        onView(ViewMatchers.withId(R.id.ed_login_password))
            .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.bottom_nav_home))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testLoginUnregistered_E2E2B() {
        onView(ViewMatchers.withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("anything@gmail.com"))
        onView(ViewMatchers.withId(R.id.ed_login_password))
            .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click())
        onView(ViewMatchers.withText("There is no user record corresponding to this identifier. The user may have been deleted."))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testLoginWrongPassword_E2E2C() {
        onView(ViewMatchers.withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("test@gmail.com"))
        onView(ViewMatchers.withId(R.id.ed_login_password))
            .perform(ViewActions.typeText("1010101010101"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click())
        onView(ViewMatchers.withText("The password is invalid or the user does not have a password."))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testLoginEmptyFields_E2E2D() {
        onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_login_email))
            .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Please input your email address")))
    }

    @Test
    fun testLoginInvalidEmail_E2E2E() {
        onView(ViewMatchers.withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("anything.com"))
        onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_login_email))
            .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Invalid Email")))
    }

    @Test
    fun testLoginEmptyPassword_E2E2F() {
        onView(ViewMatchers.withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("bimaadityokurniawan@gmail.com"))
        onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_login_password))
            .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Please input your password")))
    }

    @Test
    fun testLoginLessThan8Password_E2E2G() {
        onView(ViewMatchers.withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("bimaadityokurniawan@gmail.com"))
        onView(ViewMatchers.withId(R.id.ed_login_password))
            .perform(ViewActions.typeText("123"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.ed_login_password))
            .check(ViewAssertions.matches(ViewMatchers.hasErrorText("Password must not be less than 8 characters")))
    }
}