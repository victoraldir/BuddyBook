package com.quartzodev.buddybook;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginWithEmailTest extends AbstractTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;

    private Context instrumentationCtx;

    // Registers any resource that needs to be synchronized with Espresso before the test is run.
    @Before
    public void registerIdlingResource() {

        instrumentationCtx = InstrumentationRegistry.getContext();

        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:
        Espresso.registerIdlingResources(mIdlingResource);
    }

    // Remember to unregister resources when not needed to avoid malfunction.
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }

    @BeforeClass
    public static void beforeClass(){
        //First we make to sign out
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signOut();
    }

    @Test
    public void loginWithEmailTest() {

        onView(withId(R.id.email_button)).perform(click());

        onView(allOf(withId(R.id.email), isDisplayed()))
                .perform(click())
                .perform(replaceText(TestConstants.USERNAME), closeSoftKeyboard());

        onView(allOf(withId(R.id.button_next), withText("Next"), isDisplayed())).perform(click());

        sleep(5000);

        onView(withId(R.id.password)).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.password))).perform(replaceText(TestConstants.PASSWORD), closeSoftKeyboard());

        onView(allOf(withId(R.id.button_done), withText("Sign in"))).perform(scrollTo(), click());

        sleep(5000);

        onView(allOf(withId(R.id.action_add_book), isDisplayed())).check(matches(isDisplayed()));

    }
}