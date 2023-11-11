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

class E2E13AddUserToCompany {
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
    fun testAddUserToCompanyValid_E2E13A() {
        TestAccount.loginManagerUser()

        onView(ViewMatchers.withId(R.id.bottom_nav_company)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_company_members))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_user_id_input))
            .perform(ViewActions.typeText("OKoO81TVjRZfm2l6PZaqBff1weE3"),
                ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.actv_choose_user_role))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("EMPLOYEE"))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_add_user))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("Employee 2"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    @Test
    fun testAddUserToCompanyInvalidUser_E2E13B() {
        TestAccount.loginManagerUser()

        onView(ViewMatchers.withId(R.id.bottom_nav_company)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_company_members))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_user_id_input))
            .perform(ViewActions.typeText("1010101010101010"),
                ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.actv_choose_user_role))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("EMPLOYEE"))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_add_user))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("1010101010101010"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testAddUserToCompanyEmpty_E2E13C() {
        TestAccount.loginManagerUser()

        onView(ViewMatchers.withId(R.id.bottom_nav_company)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_company_members))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_add_user))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
    }

    @Test
    fun testAddUserToCompanyUnauthorized_E2E13D() {
        TestAccount.loginEmployeeUser()

        onView(ViewMatchers.withId(R.id.bottom_nav_company)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_company_members))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_company_members))
            .check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
    }
}