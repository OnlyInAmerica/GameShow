package pro.dbro.gameshow.test;

/**
 * Created by davidbrodsky on 11/28/14.
 */
import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.spoon.Spoon;

import pro.dbro.gameshow.R;
import pro.dbro.gameshow.model.Question;
import pro.dbro.gameshow.ui.activities.MainActivity;
import pro.dbro.gameshow.ui.activities.QuestionActivity;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 *
 * <p>To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.example.android.activityinstrumentation.MainActivityTest \
 * quux.tests/android.test.InstrumentationTestRunner
 *
 * <p>Individual tests are defined as any method beginning with 'test'.
 *
 * <p>ActivityInstrumentationTestCase2 allows these tests to run alongside a running
 * copy of the application under inspection. Calling getActivity() will return a
 * handle to this activity (launching it if needed).
 */
public class IntegrationTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public static final String TAG = "IntegrationTest";

    private MainActivity mainActivity;

    public IntegrationTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false);

        mainActivity = getActivity();
    }

    /**
     * Make sure an entire game can be played without throwing an exception.
     *
     * Ensures two DailyDouble occurs.
     *
     */

    @LargeTest
    public void testGameCompletesWithoutException() throws InterruptedException {

        Instrumentation instrumentation = getInstrumentation();

        String[] playerNames = new String[] { "Alice", "Bob", "Charlie", "Devon" };
        ViewGroup playerContainer = ((ViewGroup) mainActivity.findViewById(R.id.playerContainer));
        assertNotNull(playerContainer);
        int playersEntered = playerContainer.getChildCount();

        if (playersEntered == 1 && ((TextView) playerContainer.getChildAt(0)).getText().length() == 0) {
            // Focused on first player name EditText
            sendKeys(KeyEvent.KEYCODE_BACK);        // dismiss keyboard
            instrumentation.waitForIdleSync();
            sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);   // move to add player button
        } else {
            // Focus begins on play button
            sendKeys(KeyEvent.KEYCODE_DPAD_LEFT,
                     KeyEvent.KEYCODE_DPAD_LEFT);
        }

        // Click add player button until we have all players
        for (int x = 0; x < (playerNames.length - playersEntered); x++) {
            sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
            instrumentation.waitForIdleSync();
        }

        // Make sure each player name field has some value
        for (int x = 0; x < playerNames.length; x++) {
            sendKeys(KeyEvent.KEYCODE_DPAD_UP);
            if ( ((EditText) playerContainer.getChildAt(x)).getText().length() == 0)
                typeInputToView(playerNames[x], (EditText) playerContainer.getChildAt(x));
        }

        Spoon.screenshot(mainActivity, "player_selection");

        Thread.sleep(5 * 1000); // Give JeopardyClient time to populate Game Board

        TouchUtils.clickView(this, mainActivity.findViewById(R.id.playBtn));

        Thread.sleep(4000); // Fill game board animation
        Spoon.screenshot(mainActivity, "game_board");

        // Answer every question
        int numDailyDoubles = 0;
        int numQuestions = mainActivity.mGame.countQuestions();
        sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT); // Select first question
        for(int x = 0; x < numQuestions; x++) {

            Instrumentation.ActivityMonitor questionActivityMonitor =
                    instrumentation.addMonitor(QuestionActivity.class.getName(), null, false);

            sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);

            Activity questionActivity = getInstrumentation().waitForMonitorWithTimeout(questionActivityMonitor, 5 * 1000);
            assertNotNull(questionActivity);

            Question currentQuestion = (Question) questionActivity.getIntent().getExtras().getSerializable("question");

            if (currentQuestion.isDailyDouble) Thread.sleep(2600); // Wait for DailyDouble animation

            Spoon.screenshot(mainActivity, "question");

            if (currentQuestion.isDailyDouble) {
                numDailyDoubles++;
                sendKeys(KeyEvent.KEYCODE_DPAD_CENTER); // Select Speak Wager
                Thread.sleep(1000); // Wait for Speech recognition to fail
                sendKeys(KeyEvent.KEYCODE_DPAD_DOWN); // Select second suggested wager
                instrumentation.waitForIdleSync();
                sendKeys(KeyEvent.KEYCODE_DPAD_CENTER); // Lock in wager
                instrumentation.waitForIdleSync();
            }

            sendKeys(KeyEvent.KEYCODE_DPAD_CENTER); // Select Speak Answer
            instrumentation.waitForIdleSync();

            // Wait for Speech recognition to fail
            Thread.sleep(500);
            boolean displayingSpeechResult = false;
            for (int speechRetry = 0; speechRetry < 2; speechRetry++) {
                displayingSpeechResult = ((TextView) questionActivity.findViewById(R.id.prompt)).getText().toString().contains("Heard");
                if (displayingSpeechResult) break;
                Thread.sleep(500);
            }
            if (!displayingSpeechResult) {
                Log.i(TAG, "Speech recognition failed to finish on question " + x);
            }
            assertTrue(displayingSpeechResult);

            // Select "I'm Right" / "I'm Wrong"
            if (Math.random() < .5) {
                // Select "I'm Right" occasionally
                sendKeys(KeyEvent.KEYCODE_DPAD_LEFT);
                instrumentation.waitForIdleSync();
            }

            if (currentQuestion.isDailyDouble) {
                // If daily double, question will be complete

                Instrumentation.ActivityMonitor mainActivityMonitor =
                        instrumentation.addMonitor(MainActivity.class.getName(), null, false);

                sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);  // Select "I'm Wrong"

                mainActivity = (MainActivity) getInstrumentation().waitForMonitorWithTimeout(mainActivityMonitor, 5 * 1000);

            } else {

                sendKeys(KeyEvent.KEYCODE_DPAD_CENTER); // Select "I'm Wrong"
                instrumentation.waitForIdleSync();

                // Select Player
                int whichPlayer = (int) (Math.random() * 4);
                for(int y = 0; y < whichPlayer; y++) {
                    // Begins focused on first player (leftmost in button group)
                    sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT);
                    instrumentation.waitForIdleSync();
                }

                Instrumentation.ActivityMonitor mainActivityMonitor =
                        instrumentation.addMonitor(MainActivity.class.getName(), null, false);

                sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);

                mainActivity = (MainActivity) getInstrumentation().waitForMonitorWithTimeout(mainActivityMonitor, 5 * 1000);

            }
            assertNotNull(mainActivity);
        }
        assertTrue(numDailyDoubles == 2);
    }

    private void typeInputToView(String input, final EditText target) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                target.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(input);
        getInstrumentation().waitForIdleSync();
    }
}