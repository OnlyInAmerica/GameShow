package pro.dbro.gameshow;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import pro.dbro.gameshow.model.Question;


public class QuestionActivity extends Activity {

    private Question question;

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

        ((TextView) findViewById(R.id.choice1)).setText(question.choices.get(0));
        findViewById(R.id.choice1).setTag(0);

        ((TextView) findViewById(R.id.choice2)).setText(question.choices.get(1));
        findViewById(R.id.choice2).setTag(1);

        ((TextView) findViewById(R.id.choice3)).setText(question.choices.get(2));
        findViewById(R.id.choice3).setTag(2);

        ((TextView) findViewById(R.id.choice4)).setText(question.choices.get(3));
        findViewById(R.id.choice4).setTag(3);
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
            int selection = (int) getCurrentFocus().getTag();
            if (selection == question.correctChoice) {
                Toast.makeText(this, "CORRECT", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "WRONG", Toast.LENGTH_SHORT).show();
            }
            finish();
            return true;
        }
        return false;
    }
}
