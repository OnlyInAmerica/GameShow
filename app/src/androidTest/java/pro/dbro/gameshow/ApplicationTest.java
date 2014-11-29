package pro.dbro.gameshow;

import android.app.Application;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.google.android.apps.common.testing.ui.espresso.ViewInteraction;
import com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions;
import com.squareup.spoon.Spoon;

import pro.dbro.gameshow.model.Question;
import pro.dbro.gameshow.ui.activities.MainActivity;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withTagValue;
import static org.hamcrest.Matchers.is;

/**
 * Espresso tests.
 * Disabled until espresso updated for Android 5
 * see: https://code.google.com/p/android-test-kit/issues/detail?id=84
 */
public class ApplicationTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public ApplicationTest(String pkg, Class<MainActivity> activityClass) {
        super(pkg, activityClass);
    }

//    public ApplicationTest() {
//        super(MainActivity.class);
//    }
//
//    protected void setUp() throws Exception {
//        super.setUp();
//        getActivity();
//    }
//
//    protected void tearDown() throws Exception {
//        super.tearDown();
//    }
//
//    public void test() throws InterruptedException {
//
//        Spoon.screenshot(getActivity(), "choose_players");
//
//        for (int x = 0; x < 4; x++) {
//            onView(withId(R.id.addPlayerBtn)).perform(click());
//        }
//
//        onView(withTagValue(is((Object) "player-0")))
//                .perform(typeText("Barack Obama"));
//        onView(withTagValue(is((Object) "player-1")))
//                .perform(typeText("John Boehner"));
//        onView(withTagValue(is((Object) "player-2")))
//                .perform(typeText("Carl Sagan"));
//        onView(withTagValue(is((Object) "player-3")))
//                .perform(typeText("Lynn Conway"));
//
//        Thread.sleep(5000); // Wait for Jeopardy questions to load
//
//        onView(withId(R.id.addPlayerBtn))
//                .perform(click());
//
//        Thread.sleep(3500); // Wait for board filling animation
//
//        Spoon.screenshot(getActivity(), "Game Board");
//
//        Question first = getActivity().mGame.categories.get(0).questions.get(0);
//        onView(withTagValue(is((Object) first))).perform(click());
//        Spoon.screenshot(getActivity(), "select_first_question");
//        getActivity().showQuestionActivityForQuestionView((android.view.ViewGroup) getActivity().getCurrentFocus());
//        Spoon.screenshot(getActivity(), "first_question_prompt");
//
//    }
}