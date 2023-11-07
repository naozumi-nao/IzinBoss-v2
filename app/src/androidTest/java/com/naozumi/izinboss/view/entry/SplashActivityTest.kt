package com.naozumi.izinboss.view.entry


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.naozumi.izinboss.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.`is`
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SplashActivityTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(SplashActivity::class.java)

    @Test
    fun splashActivityTest() {
        val materialButton = onView(
            allOf(
                withId(R.id.btn_skip), withText("Skip This >"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val customEditText = onView(
            allOf(
                withId(R.id.ed_login_email),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.til_email),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        customEditText.perform(replaceText("employee@gmail.com"), closeSoftKeyboard())

        val customEditText2 = onView(
            allOf(
                withId(R.id.ed_login_password),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.til_password),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        customEditText2.perform(replaceText("12345678"), closeSoftKeyboard())

        val materialButton2 = onView(
            allOf(
                withId(R.id.btn_login), withText("Login"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    8
                ),
                isDisplayed()
            )
        )
        materialButton2.perform(click())

        val floatingActionButton = onView(
            allOf(
                withId(R.id.fab_add_leave_request), withContentDescription("Add New Story"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_main_content_container),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        val materialAutoCompleteTextView = onView(
            allOf(
                withId(R.id.ed_start_date_input),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.til_start_date),
                        0
                    ),
                    0
                )
            )
        )
        materialAutoCompleteTextView.perform(scrollTo(), click())

        val materialTextView = onData(anything())
            .inAdapterView(
                allOf(
                    withId(com.google.android.material.R.id.month_grid),
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        1
                    )
                )
            )
            .atPosition(5)
        materialTextView.perform(click())

        val materialTextView2 = onData(anything())
            .inAdapterView(
                allOf(
                    withId(com.google.android.material.R.id.month_grid),
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        1
                    )
                )
            )
            .atPosition(14)
        materialTextView2.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
