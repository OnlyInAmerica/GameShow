/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package pro.dbro.gameshow;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GameFragment fragment = GameFragment.newInstance();
        getFragmentManager().beginTransaction()
                .add(R.id.container, fragment, "gameFrag")
                .commit();
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
//            ((ViewClickHandler) getFragmentManager().findFragmentByTag("gameFrag")).onViewClicked(getCurrentFocus());
            getCurrentFocus().setTransitionName("sharedValue");
            Intent intent = new Intent(this, QuestionActivity.class);
            intent.putExtra("value", ((TextView) getCurrentFocus().findViewById(R.id.value)).getText());
            intent.putExtra("prompt", (String) getCurrentFocus().getTag());
            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="robot"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, getCurrentFocus(), "sharedValue");
            // start the new activity
            startActivity(intent, options.toBundle());
            return true;
        }
        return false;
    }
}
