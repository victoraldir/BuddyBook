package com.quartzodev.buddybook;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.firebase.auth.FirebaseAuth;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;
import static org.hamcrest.core.AllOf.*;

/**
 * Created by victoraldir on 07/08/2017.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends AbstractTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void shouldLoadUserProfileOnDrawer(){

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fAuth.signInWithEmailAndPassword(TestConstants.USERNAME,TestConstants.PASSWORD);

        onView(allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed())).perform(click());

        sleep(2000);

        onView(withId(R.id.nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.main_textview_username)).check(matches(isDisplayed()));

//        onView(allOf(withId(R.id.main_textview_username), isDisplayed(),
//                        childAtPosition(
//                                childAtPosition(
//                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
//                                        2),
//                                1),
//                        isDisplayed())).check(matches(withText(TestConstants.USERNAME)));

        //onView(withId(R.id.main_textview_username)).check(ViewAssertions.matches(withText(TestConstants.USERNAME)));


    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

}