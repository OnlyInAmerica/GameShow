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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pro.dbro.gameshow.JeopardyClient;
import pro.dbro.gameshow.MusicHandler;
import pro.dbro.gameshow.R;
import pro.dbro.gameshow.model.Game;
import pro.dbro.gameshow.model.Player;
import pro.dbro.gameshow.model.Question;
import pro.dbro.gameshow.ui.fragments.ChoosePlayerFragment;
import pro.dbro.gameshow.ui.fragments.GameFragment;
import pro.dbro.gameshow.ui.QuestionAnsweredListener;

/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends Activity implements ChoosePlayerFragment.OnPlayersSelectedListener, GameFragment.GameListener {

    private String TAG = getClass().getSimpleName();

    public static final int ANSWER_QUESTION = 0;

    private ViewGroup mLastQuestionView;

    private Game mGame;
    private boolean mGameReady = false;
    private boolean mAddedPlayers = false;

    MediaPlayer mMediaPlayer;
    MusicHandler mMusicHandler;
    private final int MUSIC_FADE_DURATION = 4 * 1000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        try {
//            AssetManager assetManager = getAssets();
//            InputStream ims = assetManager.open("games/default.json");
//
//            Gson gson = new Gson();
//            Reader reader = new InputStreamReader(ims);
//
//            Game game = gson.fromJson(reader, Game.class);

        createGame();
        showChoosePlayerFragment();
    }

    private void createGame() {
        mAddedPlayers = false;
        mGameReady = false;
        mGame = new Game();

        JeopardyClient client = new JeopardyClient(this);
        client.completeGame(mGame, new JeopardyClient.GameCompleteCallback() {
            @Override
            public void onGameComplete(Game game) {
                mGameReady = true;
            }
        });
    }

    private void showChoosePlayerFragment() {
        ChoosePlayerFragment fragment = new ChoosePlayerFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, "choosePlayerFrag")
                .commit();

        mMusicHandler = new MusicHandler(this);
        mMusicHandler.load(R.raw.theme, true);
        mMusicHandler.play(MUSIC_FADE_DURATION);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_A) &&
                getCurrentFocus() != null) {
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
            boolean wasCorrect = resultCode == QuestionActivity.ANSWERED_CORRECT;

            playSound(wasCorrect ? SoundType.SUCCESS : SoundType.FAILURE);

            ((QuestionAnsweredListener) getFragmentManager().findFragmentByTag("gameFrag"))
                    .onQuestionAnswered(mLastQuestionView, wasCorrect);
        }
    }

    @Override
    public void onPlayersSelected(List<Player> players) {
        if (!mAddedPlayers) {
            mGame.addPlayers(players);
            mAddedPlayers = true;
        }

        if (mGameReady) {
            mMusicHandler.stop(MUSIC_FADE_DURATION);
            GameFragment fragment = GameFragment.newInstance(mGame);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, "gameFrag")
                    .commit();
        } else {
            Log.e(TAG, "Game not ready upon player selection");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMusicHandler != null) {
            mMusicHandler.release();
            mMusicHandler = null;
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
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
                mGameReady = false;
                createGame();
                showChoosePlayerFragment();
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

    private enum SoundType { SUCCESS, FAILURE}

    private void playSound(SoundType type) {
        if (mMediaPlayer != null) mMediaPlayer.release();

        int resId = 0;

        switch (type) {
            case SUCCESS:
                resId = R.raw.correct;
                break;
            case FAILURE:
                break;
        }

        if (resId != 0) {
            mMediaPlayer = MediaPlayer.create(this, resId);
            mMediaPlayer.start();
        }
    }
}