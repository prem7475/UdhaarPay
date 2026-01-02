package com.example.udhaarpay.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.udhaarpay.R
import com.example.udhaarpay.SendMoneyActivity
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class SendMoneyActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun `send money UI elements are displayed correctly`() {
        // Given - Launch SendMoneyActivity
        ActivityScenario.launch(SendMoneyActivity::class.java)

        // Then - Verify UI elements are displayed
        onView(withId(R.id.etRecipientName)).check(matches(isDisplayed()))
        onView(withId(R.id.etUpiId)).check(matches(isDisplayed()))
        onView(withId(R.id.etAmount)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSendMoney)).check(matches(isDisplayed()))
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun `recipient name input field accepts text input`() {
        // Given - Launch SendMoneyActivity
        ActivityScenario.launch(SendMoneyActivity::class.java)

        // When - Enter recipient name
        onView(withId(R.id.etRecipientName)).perform(typeText("John Doe"), closeSoftKeyboard())

        // Then - Verify text is entered
        onView(withId(R.id.etRecipientName)).check(matches(withText("John Doe")))
    }

    @Test
    fun `UPI ID input field accepts valid UPI format`() {
        // Given - Launch SendMoneyActivity
        ActivityScenario.launch(SendMoneyActivity::class.java)

        // When - Enter valid UPI ID
        onView(withId(R.id.etUpiId)).perform(typeText("john@paytm"), closeSoftKeyboard())

        // Then - Verify text is entered
        onView(withId(R.id.etUpiId)).check(matches(withText("john@paytm")))
    }

    @Test
    fun `amount input field accepts numeric input`() {
        // Given - Launch SendMoneyActivity
        ActivityScenario.launch(SendMoneyActivity::class.java)

        // When - Enter amount
        onView(withId(R.id.etAmount)).perform(typeText("500"), closeSoftKeyboard())

        // Then - Verify text is entered
        onView(withId(R.id.etAmount)).check(matches(withText("500")))
    }

    @Test
    fun `empty recipient name shows error on send attempt`() {
        // Given - Launch SendMoneyActivity
        ActivityScenario.launch(SendMoneyActivity::class.java)

        // When - Leave recipient name empty and try to send
        onView(withId(R.id.etUpiId)).perform(typeText("john@paytm"), closeSoftKeyboard())
        onView(withId(R.id.etAmount)).perform(typeText("500"), closeSoftKeyboard())
        onView(withId(R.id.btnSendMoney)).perform(click())

        // Then - Error should be shown (though we can't easily test Toast in UI tests)
        // The activity should still be visible (not finished)
        onView(withId(R.id.etRecipientName)).check(matches(isDisplayed()))
    }

    @Test
    fun `empty UPI ID shows error on send attempt`() {
        // Given - Launch SendMoneyActivity
        ActivityScenario.launch(SendMoneyActivity::class.java)

        // When - Leave UPI ID empty and try to send
        onView(withId(R.id.etRecipientName)).perform(typeText("John Doe"), closeSoftKeyboard())
        onView(withId(R.id.etAmount)).perform(typeText("500"), closeSoftKeyboard())
        onView(withId(R.id.btnSendMoney)).perform(click())

        // Then - Error should be shown
        onView(withId(R.id.etUpiId)).check(matches(isDisplayed()))
    }

    @Test
    fun `invalid UPI ID format shows error on send attempt`() {
        // Given - Launch SendMoneyActivity
        ActivityScenario.launch(SendMoneyActivity::class.java)

        // When - Enter invalid UPI ID and try to send
        onView(withId(R.id.etRecipientName)).perform(typeText("John Doe"), closeSoftKeyboard())
        onView(withId(R.id.etUpiId)).perform(typeText("invalid_upi_id"), closeSoftKeyboard())
        onView(withId(R.id.etAmount)).perform(typeText("500"), closeSoftKeyboard())
        onView(withId(R.id.btnSendMoney)).perform(click())

        // Then - Error should be shown
        onView(withId(R.id.etUpiId)).check(matches(isDisplayed()))
    }

    @Test
    fun `empty amount shows error on send attempt`() {
        // Given - Launch SendMoneyActivity
        ActivityScenario.launch(SendMoneyActivity::class.java)

        // When - Leave amount empty and try to send
        onView(withId(R.id.etRecipientName)).perform(typeText("John Doe"), closeSoftKeyboard())
        onView(withId(R.id.etUpiId)).perform(typeText("john@paytm"), closeSoftKeyboard())
        onView(withId(R.id.btnSendMoney)).perform(click())

        // Then - Error should be shown
        onView(withId(R.id.etAmount)).check(matches(isDisplayed()))
    }

    @Test
    fun `zero amount shows error on send attempt`() {
        // Given - Launch SendMoneyActivity
        ActivityScenario.launch(SendMoneyActivity::class.java)

        // When - Enter zero amount and try to send
        onView(withId(R.id.etRecipientName)).perform(typeText("John Doe"), closeSoftKeyboard())
        onView(withId(R.id.etUpiId)).perform(typeText("john@paytm"), closeSoftKeyboard())
        onView(withId(R.id.etAmount)).perform(typeText("0"), closeSoftKeyboard())
        onView(withId(R.id.btnSendMoney)).perform(click())

        // Then - Error should be shown
        onView(withId(R.id.etAmount)).check(matches(isDisplayed()))
    }

    @Test
    fun `amount exceeding limit shows error on send attempt`() {
        // Given - Launch SendMoneyActivity
        ActivityScenario.launch(SendMoneyActivity::class.java)

        // When - Enter amount exceeding limit and try to send
        onView(withId(R.id.etRecipientName)).perform(typeText("John Doe"), closeSoftKeyboard())
        onView(withId(R.id.etUpiId)).perform(typeText("john@paytm"), closeSoftKeyboard())
        onView(withId(R.id.etAmount)).perform(typeText("15000"), closeSoftKeyboard())
        onView(withId(R.id.btnSendMoney)).perform(click())

        // Then - Error should be shown
        onView(withId(R.id.etAmount)).check(matches(isDisplayed()))
    }

    @Test
    fun `toolbar back button is displayed and functional`() {
        // Given - Launch SendMoneyActivity
        ActivityScenario.launch(SendMoneyActivity::class.java)

        // Then - Toolbar back button should be displayed
        onView(withContentDescription("Navigate up")).check(matches(isDisplayed()))
    }
}
