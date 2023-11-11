package com.naozumi.izinboss.uitest

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.naozumi.izinboss.R

object TestAccount {
    fun loginGenericUser() {
        Espresso.onView(ViewMatchers.withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("test@gmail.com"))
        Espresso.onView(ViewMatchers.withId(R.id.ed_login_password))
            .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click())
    }

    fun loginEmployeeUser() {
        Espresso.onView(ViewMatchers.withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("employee@gmail.com"))
        Espresso.onView(ViewMatchers.withId(R.id.ed_login_password))
            .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click())
    }

    fun loginEmployee2User() {
        Espresso.onView(ViewMatchers.withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("employee2@gmail.com"))
        Espresso.onView(ViewMatchers.withId(R.id.ed_login_password))
            .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click())
    }

    fun loginEmployee3User() {
        Espresso.onView(ViewMatchers.withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("employee3@gmail.com"))
        Espresso.onView(ViewMatchers.withId(R.id.ed_login_password))
            .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click())
    }

    fun loginManagerUser() {
        Espresso.onView(ViewMatchers.withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("manager@gmail.com"))
        Espresso.onView(ViewMatchers.withId(R.id.ed_login_password))
            .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click())
    }

    fun loginManager2User() {
        Espresso.onView(ViewMatchers.withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("manager2@gmail.com"))
        Espresso.onView(ViewMatchers.withId(R.id.ed_login_password))
            .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click())
    }

    fun loginManager3User() {
        Espresso.onView(ViewMatchers.withId(R.id.ed_login_email))
            .perform(ViewActions.typeText("manager3@gmail.com"))
        Espresso.onView(ViewMatchers.withId(R.id.ed_login_password))
            .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click())
    }
}