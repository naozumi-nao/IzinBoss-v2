package com.naozumi.izinboss.uitest

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.RootMatchers
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
class FullUITest {
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
    fun testRegister_UIT1() {
        onView(withId(R.id.btn_create_account)).perform(ViewActions.click())
        onView(withId(R.id.ed_register_full_name))
            .perform(ViewActions.typeText("Bima Adityo Kurniawan"))
        onView(withId(R.id.ed_register_email))
            .perform(ViewActions.typeText("test@gmail.com"))
        onView(withId(R.id.ed_register_password))
            .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.btn_register_user)).perform(ViewActions.click())
        onView(withText(R.string.continue_on)).perform(ViewActions.click())
        onView(withId(R.id.btn_login))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun testLogin_UIT2() {
        onView(withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("test@gmail.com"))
        onView(withId(R.id.ed_login_password))
            .perform(ViewActions.typeText("12345678"),
                ViewActions.closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(ViewActions.click())
        onView(withId(R.id.bottom_nav_home))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun testGoogleSSO_UIT3() {
        onView(withId(R.id.btn_google_single_sign_on)).perform(ViewActions.click())
        onView(withId(R.id.bottom_nav_home))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun testProfileShowsData_UIT4() {
        TestAccount.loginGenericUser()

        onView(withId(R.id.bottom_nav_profile))
            .perform(ViewActions.click())
        onView(withId(R.id.tv_full_name_input))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.tv_user_id_input))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.tv_email_input))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun testLogout_UIT5() {
        TestAccount.loginGenericUser()

        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open());
        onView(withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isOpen()));
        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_logout));
        onView(withId(R.id.btn_login))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun testChangeName_UIT6() {
        TestAccount.loginGenericUser()

        onView(withId(R.id.bottom_nav_profile))
            .perform(ViewActions.click())
        onView(withId(R.id.btn_profile_info))
            .perform(ViewActions.click())
        onView(withId(R.id.ed_new_name_input))
            .perform(ViewActions.typeText("Bima A.K."))
        onView(withId(R.id.btn_confirm_new_name))
            .perform(ViewActions.click())
        onView(withId(R.id.tv_full_name_input))
            .check(ViewAssertions.matches(withText("Bima A.K.")))
    }

    @Test
    fun testDeleteUser_UIT7() {
        TestAccount.loginGenericUser()

        onView(withId(R.id.bottom_nav_profile))
            .perform(ViewActions.click())
        onView(withId(R.id.btn_delete_account))
            .perform(ViewActions.click())
        onView(withText("Yes")).perform(ViewActions.click())
        onView(withText(R.string.continue_on)).perform(ViewActions.click())
        TestAccount.loginGenericUser()
        onView(withText("There is no user record corresponding to this identifier. The user may have been deleted."))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun testCreateCompany_UIT8() {
        TestAccount.loginManagerUser()

        onView(withId(R.id.bottom_nav_company))
            .perform(ViewActions.click())
        onView(withId(R.id.ed_register_company_name))
            .perform(ViewActions.typeText("Test Company"),
                ViewActions.closeSoftKeyboard())
        onView(withId(R.id.actv_select_industry_sector))
            .perform(ViewActions.click())
        onView(withText("Manufacturing"))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())
        onView(withId(R.id.btn_register_company))
            .perform(ViewActions.click())
        onView(withText("Company Created!"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText(R.string.continue_on)).perform(ViewActions.click())
        onView(withId(R.id.bottom_nav_company))
            .perform(ViewActions.click())
        onView(withText("Test Company"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.bottom_nav_profile))
            .perform(ViewActions.click())
        onView(withText("Manager"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("Test Company"))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun testJoinCompany_UIT9() {
        TestAccount.loginEmployeeUser()

        onView(withId(R.id.bottom_nav_company)).perform(ViewActions.click())
        onView(withId(R.id.btn_already_have_company)).perform(ViewActions.click())
        onView(withId(R.id.ed_company_id_input))
            .perform(ViewActions.typeText("gC5QrLVBrWlNftWaU7jF"),
                ViewActions.closeSoftKeyboard())
        onView(withId(R.id.btn_join_company)).perform(ViewActions.click())
        onView(withText("gC5QrLVBrWlNftWaU7jF"))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun testLeaveCompany_UIT10() {
        TestAccount.loginEmployeeUser()

        onView(withId(R.id.bottom_nav_profile)).perform(ViewActions.click())
        onView(withId(R.id.btn_leave_current_company)).perform(ViewActions.click())
        onView(withText("Yes")).perform(ViewActions.click())
        onView(withText("Success"))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun testCheckCompanyInfoAndMembers_UIT11() {
        TestAccount.loginManagerUser()

        onView(withId(R.id.bottom_nav_company)).perform(ViewActions.click())
        onView(withText("Test Company"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("Wdjw1PQnxOI6nuuS6TkM"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("Manufacturing"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.rv_members_list))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun testRecruitCompanyMember_UIT12() {
        TestAccount.loginManagerUser()

        onView(withId(R.id.bottom_nav_company)).perform(ViewActions.click())
        onView(withId(R.id.btn_company_members))
            .perform(ViewActions.click())
        onView(withId(R.id.ed_user_id_input))
            .perform(ViewActions.typeText("y6Hws6Fw01UU5parInIiSF76lSg1"),
                ViewActions.closeSoftKeyboard())
        onView(withId(R.id.actv_choose_user_role))
            .perform(ViewActions.click())
        onView(withText("EMPLOYEE"))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())
        onView(withId(R.id.btn_add_user))
            .perform(ViewActions.click())
        onView(withText("Employee 1"))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun testAddLeaveRequest_UIT13() {
        TestAccount.loginEmployeeUser()
        onView(withId(R.id.fab_add_leave_request))
            .perform(ViewActions.click())
        onView(withId(R.id.actv_add_type))
            .perform(ViewActions.click())
        onView(withText("Vacation"))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click());
        onView(withId(R.id.ed_start_date_input))
            .perform(ViewActions.click())
        onView(withId(R.id.ed_start_date_input))
            .perform(ViewActions.click())

        onView(withId(com.google.android.material.R.id.mtrl_picker_header_toggle))
            .perform(ViewActions.click())

        onView(withHint("Start date"))
            .perform(ViewActions.replaceText("12/12/23"),
                ViewActions.closeSoftKeyboard())

        onView(withHint("End date"))
            .perform(ViewActions.replaceText("12/17/23"),
                ViewActions.closeSoftKeyboard())

        onView(withId(com.google.android.material.R.id.confirm_button))
            .perform(ViewActions.click())

        onView(withId(R.id.ed_add_reason))
            .perform(ViewActions.typeText("Testing with Espresso"))
        onView(withId(R.id.btn_request_leave))
            .perform(ViewActions.click())

        onView(withId(R.id.rv_leave_requests))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("VACATION"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("12-12-2023"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("17-12-2023"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("Testing with Espresso"))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun testApproveLeaveRequest_UIT14() {
        TestAccount.loginManagerUser()
        onView(withText("Employee 1"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("VACATION"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("12-12-2023"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("17-12-2023"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("Testing with Espresso"))
            .check(ViewAssertions.matches(isDisplayed()))

        onView(withText("Awaiting Approval"))
            .perform(ViewActions.click())

        onView(withText("Employee 1"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("VACATION"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("12-12-2023"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("17-12-2023"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("Testing with Espresso"))
            .check(ViewAssertions.matches(isDisplayed()))

        onView(withId(R.id.btn_approve))
            .perform(ViewActions.click())

        onView(withText("Approved"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("Approved by: "))
            .perform(ViewActions.click())

        onView(withId(R.id.tv_reviewed_by))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("Manager 1"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.tv_reviewed_on))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun testRejectLeaveRequest_UIT15() {
        TestAccount.loginManagerUser()
        onView(withText("Employee 1"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("VACATION"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("12-12-2023"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("17-12-2023"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("Testing with Espresso"))
            .check(ViewAssertions.matches(isDisplayed()))

        onView(withText("Awaiting Approval"))
            .perform(ViewActions.click())

        onView(withText("Employee 1"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("VACATION"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("12-12-2023"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("17-12-2023"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("Testing with Espresso"))
            .check(ViewAssertions.matches(isDisplayed()))

        onView(withId(R.id.btn_reject))
            .perform(ViewActions.click())

        onView(withText("Rejected"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("Rejected by: "))
            .perform(ViewActions.click())

        onView(withId(R.id.tv_reviewed_by))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withText("Manager 1"))
            .check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.tv_reviewed_on))
            .check(ViewAssertions.matches(isDisplayed()))
    }
}