package com.quartzodev.buddybook;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by victoraldir on 07/08/2017.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends AbstractTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void shouldLoadUserProfileOnDrawer() {

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signInWithEmailAndPassword(TestConstants.USERNAME, TestConstants.PASSWORD);

        onView(allOf(withContentDescription("Open navigation drawer"),
                withParent(withId(R.id.toolbar)),
                isDisplayed())).perform(click());

        sleep(2000);

        onView(withId(R.id.nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.main_textview_username)).check(matches(isDisplayed()));

    }

}