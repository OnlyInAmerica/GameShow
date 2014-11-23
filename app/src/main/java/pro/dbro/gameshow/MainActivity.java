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
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import pro.dbro.gameshow.model.Game;
import pro.dbro.gameshow.model.Player;
import pro.dbro.gameshow.model.Question;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity {

    public static final int ANSWER_QUESTION = 0;

    private ViewGroup mLastQuestionView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            AssetManager assetManager = getAssets();
            InputStream ims = assetManager.open("games/default.json");

            Gson gson = new Gson();
            Reader reader = new InputStreamReader(ims);

            Game game = gson.fromJson(reader, Game.class);

            Player sav = new Player("Savvy J");
            Player dbro = new Player("dbro");
            game.players.add(sav);
            game.players.add(dbro);

            JeopardyClient client = new JeopardyClient(this);
            client.completeGame(game, new JeopardyClient.GameCompleteCallback() {
                @Override
                public void onGameComplete(Game game) {
                    GameFragment fragment = GameFragment.newInstance(game);
                    getFragmentManager().beginTransaction()
                            .add(R.id.container, fragment, "gameFrag")
                            .commit();
                }
            });
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
//            ((ViewClickHandler) getFragmentManager().findFragmentByTag("gameFrag")).onViewClicked(getCurrentFocus());
            getCurrentFocus().setTransitionName("sharedValue");
            Intent intent = new Intent(this, QuestionActivity.class);
            intent.putExtra("value", ((TextView) getCurrentFocus().findViewById(R.id.value)).getText());
            intent.putExtra("question", (Question) getCurrentFocus().getTag());
            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="robot"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, getCurrentFocus(), "sharedValue");
            // start the new activity
            mLastQuestionView = (ViewGroup) getCurrentFocus();
            startActivityForResult(intent, ANSWER_QUESTION, options.toBundle());
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    @Override
         protected void onActivityResult(int requestCode, int resultCode, Intent data) {
             // Check which request we're responding to
             if (requestCode == ANSWER_QUESTION) {

                 ((QuestionAnsweredListener) getFragmentManager().findFragmentByTag("gameFrag")).onQuestionAnswered(mLastQuestionView);
                 if (resultCode == QuestionActivity.ANSWERED_CORRECT) {

                 } else {

                 }
             }
         }
}
