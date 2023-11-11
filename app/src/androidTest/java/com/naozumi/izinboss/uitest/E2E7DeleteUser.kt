package com.naozumi.izinboss.uitest

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

class E2E7DeleteUser {
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
    fun testDeleteUser_E2E7() {
        TestAccount.loginGenericUser()

        onView(ViewMatchers.withId(R.id.bottom_nav_profile))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_delete_account))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("Yes")).perform(ViewActions.click())
        onView(ViewMatchers.withText(R.string.continue_on)).perform(ViewActions.click())
        TestAccount.loginGenericUser()
        onView(ViewMatchers.withText("There is no user record corresponding to this identifier. The user may have been deleted."))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}