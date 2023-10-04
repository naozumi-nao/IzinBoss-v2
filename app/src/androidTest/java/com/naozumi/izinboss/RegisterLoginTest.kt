package com.naozumi.izinboss

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.naozumi.izinboss.model.helper.EspressoIdlingResource
import com.naozumi.izinboss.view.entry.LoginActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class RegisterLoginTest {
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
    fun bigTest() {
        testIfRegisterSuccess()
        testIfLoginSuccess()
        testIfCreateCompanySuccess()
        //testIfAddLeaveRequestSuccess()
        //testIfUserDeletionSuccess()
    }

    @Test
    fun testIfRegisterSuccess() {
        onView(withId(R.id.btn_create_account)).perform(ViewActions.click())
        onView(withId(R.id.ed_register_full_name))
            .perform(ViewActions.typeText("Bima Adityo Kurniawan"))
        onView(withId(R.id.ed_register_email))
            .perform(ViewActions.typeText("bimaadityokurniawan@gmail.com"))
        onView(withId(R.id.ed_register_password))
            .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.btn_register_user)).perform(ViewActions.click())
        onView(withText(R.string.continue_on)).perform(ViewActions.click())
    }

    @Test
    fun testIfLoginSuccess() {
        onView(withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("bimaadityokurniawan@gmail.com"))
        onView(withId(R.id.ed_login_password))
            .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(ViewActions.click())
    }

    //@Test
    fun testIfCreateCompanySuccess() {
        onView(withId(R.id.ed_register_company_name))
            .perform(ViewActions.typeText("Test Company"), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.btn_register_company)).perform(ViewActions.click())
        onView(withText(R.string.continue_on)).perform(ViewActions.click())
    }

    //@Test
    fun testIfAddLeaveRequestSuccess() {
        onView(withId(R.id.fab_add_leave))
            .perform(ViewActions.click())
        onView(withText("While using the app"))
            .perform(ViewActions.click())
        onView(withText("Vacation"))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click());
        //onView(withId(R.id.btn_pick_date))
            //.perform(ViewActions.click())
        onView(withText("Select date"))
            .perform(PickerActions.setDate(2023, 8, 17))
        onView(withText("Select date"))
            .perform(PickerActions.setDate(2023, 8, 20))
        onView(withId(R.id.ed_add_reason))
            .perform(ViewActions.typeText("Testing with Espresso"))
        onView(withId(R.id.btn_request_leave))
            .perform(ViewActions.click())
        onView(withText("Testing with Espresso"))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    //@Test
    fun testIfUserDeletionSuccess() {
        onView(withId(android.R.id.home)).perform(ViewActions.click())
        onView(withId(R.id.nav_profile)).perform(ViewActions.click())
        onView(withId(R.id.btn_delete_account)).perform(ViewActions.click())
        onView(withId(R.id.btn_login)).check(ViewAssertions.matches(isDisplayed()))
    }
}