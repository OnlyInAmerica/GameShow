package pro.dbro.gameshow;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class QuestionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        String value = getIntent().getExtras().getString("value");
        String prompt = getIntent().getExtras().getString("prompt");
        Typeface tileFont = Typeface.createFromAsset(getAssets(), "fonts/swiss_911.ttf");
        ((TextView) findViewById(R.id.value)).setTypeface(tileFont);
        ((TextView) findViewById(R.id.value)).setText(value);

        ((TextView) findViewById(R.id.prompt)).setText(prompt);
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
}
