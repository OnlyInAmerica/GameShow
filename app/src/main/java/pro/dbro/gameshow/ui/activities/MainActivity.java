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

package pro.dbro.gameshow.ui.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import pro.dbro.gameshow.GameManager;
import pro.dbro.gameshow.JeopardyClient;
import pro.dbro.gameshow.MusicHandler;
import pro.dbro.gameshow.R;
import pro.dbro.gameshow.SoundEffectHandler;
import pro.dbro.gameshow.model.Category;
import pro.dbro.gameshow.model.Game;
import pro.dbro.gameshow.model.Player;
import pro.dbro.gameshow.model.Question;
import pro.dbro.gameshow.ui.QuestionAnsweredListener;
import pro.dbro.gameshow.ui.fragments.ChoosePlayerFragment;
import pro.dbro.gameshow.ui.fragments.GameFragment;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity implements ChoosePlayerFragment.OnPlayersSelectedListener, GameFragment.GameListener {

    private String TAG = getClass().getSimpleName();

    public static final int REQUEST_CODE_ANSWER_QUESTION = 0;

    private ViewGroup mLastQuestionView;

    public Game mGame;      // public for testing
    JeopardyClient mClient;
    private boolean mGameReady = false;
    private boolean mAddedPlayers = false;

    SoundEffectHandler mSoundFxHandler;
    MusicHandler mMusicHandler;
    private final int MUSIC_FADE_DURATION = 4 * 1000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createGame();
        showChoosePlayerFragment();
        mSoundFxHandler = SoundEffectHandler.getInstance(this);
        Log.d(TAG, "onCreate");
    }

    private void createGame() {
        mAddedPlayers = false;
        mGameReady = false;

//        try {
//            AssetManager assetManager = getAssets();
//            InputStream ims = null;
//            ims = assetManager.open("games/default.json");
//            Gson gson = new Gson();
//            Reader reader = new InputStreamReader(ims);
//            Game game = gson.fromJson(reader, Game.class);
//            GameManager.setGame(game);
//            mGameReady = true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        mGame = GameManager.getInstance();

        mClient = new JeopardyClient(this);
        mClient.completeGame(mGame, new JeopardyClient.RequestCallback() {
            @Override
            public void onRequestComplete(Game game) {
                mGameReady = true;
                if (mAddedPlayers) addGameFragment();
            }
        });
    }

    private void showChoosePlayerFragment() {
        ChoosePlayerFragment fragment = new ChoosePlayerFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, "choosePlayerFrag")
                .commit();

        if (mMusicHandler == null) {
            mMusicHandler = new MusicHandler(this);
            mMusicHandler.load(R.raw.theme, true);
        }
        if (!mMusicHandler.isPlaying()) mMusicHandler.play(MUSIC_FADE_DURATION);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_B) {
            showQuitDialog();
            return true;
        }
        return false;
    }

    /** Public for testing */
    public void showQuestionActivityForQuestionView(ViewGroup questionTile,
                                                    Question question) {
        questionTile.setTransitionName("sharedValue");
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putExtra("question", question);

        mGame.makeQuestionCandidateForDailyDouble(question);

        if (question.isDailyDouble) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, REQUEST_CODE_ANSWER_QUESTION);
        } else {
            ActivityOptions options;
            options = ActivityOptions
                    .makeSceneTransitionAnimation(this, questionTile, "sharedValue");
            startActivityForResult(intent, REQUEST_CODE_ANSWER_QUESTION, options.toBundle());
        }

        mLastQuestionView = questionTile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CODE_ANSWER_QUESTION) {
            QuestionAnsweredListener.QuestionResult result = null;

            switch(resultCode) {
                case QuestionActivity.CORRECT:
                    result = QuestionAnsweredListener.QuestionResult.CORRECT;
                    break;
                case QuestionActivity.INCORRECT:
                    result = QuestionAnsweredListener.QuestionResult.INCORRECT;
                    break;
                case QuestionActivity.NO_RESPONSE:
                    result = QuestionAnsweredListener.QuestionResult.NO_RESPONSE;
                    break;
            }

            ((QuestionAnsweredListener) getFragmentManager().findFragmentByTag("gameFrag"))
                    .onQuestionAnswered(mLastQuestionView,
                                        data.getIntExtra("answeringPlayerIdx", -1),
                                        result,
                                        data.getIntExtra("wager", -1));
        }
    }

    @Override
    public void onPlayersSelected(List<Player> players) {
        if (mAddedPlayers) return;

        mGame.addPlayers(players);
        mAddedPlayers = true;

        if (mGameReady) {
            mMusicHandler.stop(0); // No fade. GameFragment will play board filling jingle
            addGameFragment();
        } else {
            Log.d(TAG, "Game not ready upon player selection. Will show board when game ready");
            ProgressBar bar = new ProgressBar(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                           ViewGroup.LayoutParams.WRAP_CONTENT);
            // TODO : Don't use pixel values though 1920x1080 is probably the screen res
            params.width = 100;
            params.height = 100;
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            params.setMargins(0, 0, 0, 100);
            bar.setLayoutParams(params);
            bar.setIndeterminate(true);
            ((ViewGroup) findViewById(R.id.container)).addView(bar);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMusicHandler != null) {
            mMusicHandler.release();
            mMusicHandler = null;
        }

        if (mSoundFxHandler != null) {
            mSoundFxHandler.release();
            mSoundFxHandler = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMusicHandler != null && mMusicHandler.isPlaying()) {
            mMusicHandler.stop(MUSIC_FADE_DURATION);
        }
    }

    @Override
    public void onGameComplete(List<Player> winners) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        StringBuilder winnerString = new StringBuilder();

        if (winners.size() == mGame.players.size()) {
            winnerString.append(getString(R.string.everybody_wins));
            builder.setMessage(getString(R.string.pathetic));
        } else {
            for (int x = 0; x < winners.size(); x++) {
                winnerString.append(winners.get(x).name);
                if (winners.size() > x + 1) winnerString.append(", ");
                winnerString.append(" ");
            }

            if (winners.size() > 1)
                winnerString.append(getString(R.string.win));
            else
                winnerString.append(getString(R.string.wins));
        }

        builder.setTitle(winnerString.toString());
        builder.setPositiveButton(getString(R.string.play_again), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startNewGame();
            }
        });
        builder.setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.this.finish();
            }
        });
        builder.show();
    }

    @Override
    public void onQuestionSelected(ViewGroup tile, Question question) {
        showQuestionActivityForQuestionView(tile, question);
    }

    @Override
    public void onCategorySelected(Category category) {
        replaceCategory(category);
    }

    private void replaceCategory(Category toReplace) {
        final int changedCategoryIdx = mGame.categories.indexOf(toReplace);
        mClient.replaceGameCategory(mGame, toReplace, new JeopardyClient.RequestCallback() {
            @Override
            public void onRequestComplete(Game game) {
                getGameFragment().populateCategory(game.categories.get(changedCategoryIdx));
            }
        });
    }

    private GameFragment getGameFragment() {
        return ((GameFragment) getFragmentManager().findFragmentByTag("gameFrag"));
    }

    private void startNewGame() {
        mGameReady = false;
        GameManager.reset();
        createGame();
        showChoosePlayerFragment();
    }

    private void showQuitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit?")
               .setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                       MainActivity.this.finish();
                   }
               })
               .setNegativeButton("New Game", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       startNewGame();
                   }
               })
               .show();
    }

    private void addGameFragment() {
        GameFragment fragment = GameFragment.newInstance(mGame);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, "gameFrag")
                .commit();
    }
}
