package pro.dbro.gameshow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pro.dbro.gameshow.model.Question;


public class QuestionActivity extends Activity {

    public static String INTENT_ACTION = "pro.dbro.gameshow.QuestionResult";
    public static int ANSWERED_CORRECT = 1;
    public static int ANSWERED_INCORRECT = 0;

    private static enum State { SELECT, SHOWING_ANSWER }

    private Question question;
    private State state = State.SELECT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
//        String value = getIntent().getExtras().getString("value");
        question = (Question) getIntent().getExtras().getSerializable("question");
        Typeface tileFont = Typeface.createFromAsset(getAssets(), "fonts/Korinna_Bold.ttf");
//        ((TextView) findViewById(R.id.value)).setTypeface(tileFont);
//        ((TextView) findViewById(R.id.value)).setText(value);

        ((TextView) findViewById(R.id.prompt)).setTypeface(tileFont);
        ((TextView) findViewById(R.id.prompt)).setText(question.prompt.toUpperCase());

        if (question.choices.size() == 4) {
            ((TextView) findViewById(R.id.choice1)).setText(question.choices.get(0));
            findViewById(R.id.choice1).setTag(0);

            ((TextView) findViewById(R.id.choice2)).setText(question.choices.get(1));
            findViewById(R.id.choice2).setTag(1);

            ((TextView) findViewById(R.id.choice3)).setText(question.choices.get(2));
            findViewById(R.id.choice3).setTag(2);

            ((TextView) findViewById(R.id.choice4)).setText(question.choices.get(3));
            findViewById(R.id.choice4).setTag(3);
        } else {
            findViewById(R.id.choiceContainer).setVisibility(View.GONE);
            findViewById(R.id.manualAnswer).setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (question.choices.size() > 1) {
                int selection = (int) getCurrentFocus().getTag();
                if (selection == question.correctChoice) {
                    Toast.makeText(this, "CORRECT", Toast.LENGTH_SHORT).show();
                    finishWithQuestionResult(true);
                } else {
                    Toast.makeText(this, "WRONG", Toast.LENGTH_SHORT).show();
                    finishWithQuestionResult(false);
                }
            } else {
                if (state == State.SELECT) {
                    ((TextView) findViewById(R.id.prompt)).setText(question.choices.get(0));

                    findViewById(R.id.choiceContainer).setVisibility(View.VISIBLE);
                    (findViewById(R.id.manualAnswer)).setVisibility(View.GONE);

                    findViewById(R.id.choice1).setVisibility(View.INVISIBLE);

                    ((TextView) findViewById(R.id.choice2)).setText("I got it");
                    findViewById(R.id.choice2).setTag(true);

                    ((TextView) findViewById(R.id.choice3)).setText("I didn't");
                    findViewById(R.id.choice3).setTag(false);

                    findViewById(R.id.choice4).setVisibility(View.INVISIBLE);
                    state = State.SHOWING_ANSWER;
                } else if (state == State.SHOWING_ANSWER) {
                    boolean answeredCorrectly = (boolean) getCurrentFocus().getTag();
                    finishWithQuestionResult(answeredCorrectly);
                }
            }
            return true;
        }
        return false;
    }

    private void finishWithQuestionResult(boolean correctAnswer) {
        Intent result = new Intent(INTENT_ACTION);
        setResult((correctAnswer ? ANSWERED_CORRECT : ANSWERED_INCORRECT), result);
        finish();
    }
}
