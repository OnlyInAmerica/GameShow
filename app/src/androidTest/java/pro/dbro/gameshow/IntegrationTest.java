package pro.dbro.gameshow;

/**
 * Created by davidbrodsky on 11/28/14.
 */
import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.spoon.Spoon;

import java.util.concurrent.atomic.AtomicBoolean;

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

    private static final int TIME_SCALE = 1; // Adjust time scale for events optimized to reduce test time at expense of human readability

    private MainActivity mainActivity;
    private Button addPlayerBtn;

    public IntegrationTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false);

        mainActivity = getActivity();
        addPlayerBtn = (Button) mainActivity.findViewById(R.id.addPlayerBtn);
    }

    /**
     * Test to make sure that spinner values are persisted across activity restarts.
     *
     * <p>Launches the main activity, sets a spinner value, closes the activity, then relaunches
     * that activity. Checks to make sure that the spinner values match what we set them to.
     */
    // BEGIN_INCLUDE (test_name)
    public void testSpinnerValuePersistedBetweenLaunches() throws InterruptedException {
        // END_INCLUDE (test_name)

        Instrumentation instrumentation = getInstrumentation();

        final MainActivity mainActivity = getActivity();

        String[] playerNames = new String[] { "Alice", "Bob", "Charlie", "Devon" };
        ViewGroup playerContainer = ((ViewGroup) mainActivity.findViewById(R.id.playerContainer));
        boolean arePlayersEntered = playerContainer.getChildCount() > 1;

        if (!arePlayersEntered) {
            sendKeys(KeyEvent.KEYCODE_BACK,         // Collapse keyboard
                    KeyEvent.KEYCODE_DPAD_DOWN,     // Select Add Player
                    KeyEvent.KEYCODE_DPAD_CENTER,   // Add 3 more players
                    KeyEvent.KEYCODE_DPAD_CENTER,
                    KeyEvent.KEYCODE_DPAD_CENTER);
        }

        for (int x = 0; x < playerNames.length; x++) {
            sendKeys(KeyEvent.KEYCODE_DPAD_UP);
            typeInputToView(playerNames[x], (EditText) playerContainer.getChildAt(x));
        }

        Thread.sleep(150);
        Spoon.screenshot(mainActivity, "player_selection");

        Thread.sleep(5 * 1000); // Give JeopardyClient time to populate Game Board

        TouchUtils.clickView(this, mainActivity.findViewById(R.id.playBtn));

        Thread.sleep(4000); // Fill game board animation
        Spoon.screenshot(mainActivity, "game_board");

        // Answer every question
        int numQuestions = mainActivity.mGame.countQuestions();
        sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT); // Select first question
        for(int x = 0; x < numQuestions; x++) {
            Thread.sleep(50 * TIME_SCALE);

            Instrumentation.ActivityMonitor questionActivityMonitor =
                    instrumentation.addMonitor(QuestionActivity.class.getName(), null, false);

            Log.i(TAG, "Select Question");
            sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);

            Log.i(TAG, "QuestionActivity launch timeout begins now");
            // Register we are interested in the authentication activiry...
//            assertTrue(getInstrumentation().checkMonitorHit(questionActivityMonitor, 1));
//            getInstrumentation().waitForMonitor()
            Activity questionActivity = getInstrumentation().waitForMonitorWithTimeout(questionActivityMonitor, 5 * 1000);
            assertNotNull(questionActivity);
            Thread.sleep(5 * 1000); // Wait for question animation (possibly inc. DailyDouble)
            Spoon.screenshot(mainActivity, "question");

            Log.i(TAG, "Select Speak Answer");
            sendKeys(KeyEvent.KEYCODE_DPAD_CENTER); // Select Speak Answer

            // Speech recognition instantly fails
            Thread.sleep(500);

            final AtomicBoolean answeredCorrect = new AtomicBoolean(false);

            answeredCorrect.set(
                    ((TextView) questionActivity.findViewById(R.id.choice1)).getText().length() > 0);

            Thread.sleep(2000);
            if (!answeredCorrect.get()) {
                // Select "I'm Right" / "I'm Wrong"
                if (Math.random() < .5) {
                    // Select "I'm Right" occasionally
                    sendKeys(KeyEvent.KEYCODE_DPAD_LEFT);
                    Log.i(TAG, "Move to I'm Right");
                    Thread.sleep(50 * TIME_SCALE);
                }
                sendKeys(KeyEvent.KEYCODE_DPAD_CENTER); // Select "I'm Wrong"
                Log.i(TAG, "Select I'm right/wrong");
                Thread.sleep(100 * TIME_SCALE);
            } else {
                Log.wtf(TAG, "WTF, how could we speak the right answer without speaking?");
            }

            // Select Player
            int whichPlayer = (int) (Math.random() * 4);
            for (int y = 0; y < whichPlayer; y++) {
                Log.i(TAG, "Select player to right");
                sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT); // Select a player to right
                Thread.sleep(20 * TIME_SCALE);
            }
            sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
            Log.i(TAG, "Select target player");
            Thread.sleep(500 * TIME_SCALE); // Wait for QuestionActivity fadeout animation
        }
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