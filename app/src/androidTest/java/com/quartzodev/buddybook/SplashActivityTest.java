//package com.quartzodev.buddybook;
//
//
//import android.support.test.espresso.ViewInteraction;
//import android.support.test.rule.ActivityTestRule;
//import android.support.test.runner.AndroidJUnit4;
//import android.test.suitebuilder.annotation.LargeTest;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//
//import org.hamcrest.Description;
//import org.hamcrest.Matcher;
//import org.hamcrest.TypeSafeMatcher;
//import org.hamcrest.core.IsInstanceOf;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static android.support.test.espresso.Espresso.onView;
//import static android.support.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.action.ViewActions.scrollTo;
//import static android.support.test.espresso.assertion.ViewAssertions.matches;
//import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
//import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.matcher.ViewMatchers.withParent;
//import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.Matchers.allOf;
//
//@LargeTest
//@RunWith(AndroidJUnit4.class)
//public class SplashActivityTest {
//
//    @Rule
//    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);
//
//    @Test
//    public void splashActivityTest() {
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(3598075);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction appCompatButton = onView(
//                allOf(withId(R.id.google_button), withText("Sign in with Google"),
//                        withParent(allOf(withId(R.id.btn_holder),
//                                withParent(withId(R.id.container))))));
//        appCompatButton.perform(scrollTo(), click());
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(3231274);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction appCompatButton2 = onView(
//                allOf(withId(R.id.google_button), withText("Sign in with Google"),
//                        withParent(allOf(withId(R.id.btn_holder),
//                                withParent(withId(R.id.container))))));
//        appCompatButton2.perform(scrollTo(), click());
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(3599184);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction appCompatImageButton = onView(
//                allOf(withContentDescription("Open navigation drawer"),
//                        withParent(withId(R.id.toolbar)),
//                        isDisplayed()));
//        appCompatImageButton.perform(click());
//
//        ViewInteraction appCompatImageButton2 = onView(
//                allOf(withContentDescription("Open navigation drawer"),
//                        withParent(withId(R.id.toolbar)),
//                        isDisplayed()));
//        appCompatImageButton2.perform(click());
//
//        ViewInteraction appCompatImageButton3 = onView(
//                allOf(withContentDescription("Open navigation drawer"),
//                        withParent(withId(R.id.toolbar)),
//                        isDisplayed()));
//        appCompatImageButton3.perform(click());
//
//        ViewInteraction textView = onView(
//                allOf(withId(R.id.main_textview_user_email), withText("vhaldir@gmail.com"),
//                        childAtPosition(
//                                childAtPosition(
//                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
//                                        2),
//                                2),
//                        isDisplayed()));
//        textView.check(matches(withText("vhaldir@gmail.com")));
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction textView2 = onView(
//                allOf(withId(R.id.main_textview_user_email), withText("vhaldir@gmail.com"),
//                        childAtPosition(
//                                childAtPosition(
//                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
//                                        2),
//                                2),
//                        isDisplayed()));
//        textView2.check(matches(isDisplayed()));
//
//        ViewInteraction textView3 = onView(
//                allOf(withId(R.id.main_textview_username), withText("Victor Neves"),
//                        childAtPosition(
//                                childAtPosition(
//                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
//                                        2),
//                                1),
//                        isDisplayed()));
//        textView3.check(matches(withText("Victor Neves")));
//
//    }
//
//    private static Matcher<View> childAtPosition(
//            final Matcher<View> parentMatcher, final int position) {
//
//        return new TypeSafeMatcher<View>() {
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("Child at position " + position + " in parent ");
//                parentMatcher.describeTo(description);
//            }
//
//            @Override
//            public boolean matchesSafely(View view) {
//                ViewParent parent = view.getParent();
//                return parent instanceof ViewGroup && parentMatcher.matches(parent)
//                        && view.equals(((ViewGroup) parent).getChildAt(position));
//            }
//        };
//    }
//}
