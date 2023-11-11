package com.naozumi.izinboss.uitest

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
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
class E2E8CreateCompany {
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
    fun testCreateCompanyValid_E2E8A() {
        TestAccount.loginManager2User()

        onView(ViewMatchers.withId(R.id.bottom_nav_company))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_register_company_name))
            .perform(ViewActions.typeText("Test Company"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.actv_select_industry_sector))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("Manufacturing"))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_register_company))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("Company Created!"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText(R.string.continue_on)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.bottom_nav_company))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("Test Company"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.bottom_nav_profile))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("Manager"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withText("Test Company"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testCreateCompanyEmptyFields_E2E8B() {
        TestAccount.loginManager3User()

        onView(ViewMatchers.withId(R.id.bottom_nav_company))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_register_company))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
    }

    @Test
    fun testCreateCompanyEmptySector_E2E8C() {
        TestAccount.loginManager3User()

        onView(ViewMatchers.withId(R.id.bottom_nav_company))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.ed_register_company_name))
            .perform(ViewActions.typeText("Test Company"), ViewActions.closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.btn_register_company))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
    }

    @Test
    fun testCreateCompanyEmptyName_E2E8D() {
        TestAccount.loginManager3User()

        onView(ViewMatchers.withId(R.id.bottom_nav_company))
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.actv_select_industry_sector))
            .perform(ViewActions.click())
        onView(ViewMatchers.withText("Manufacturing"))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.btn_register_company))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
    }
}