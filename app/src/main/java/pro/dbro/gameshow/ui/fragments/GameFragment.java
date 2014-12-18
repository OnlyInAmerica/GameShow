package pro.dbro.gameshow.ui.fragments;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pro.dbro.gameshow.R;
import pro.dbro.gameshow.SoundEffectHandler;
import pro.dbro.gameshow.model.Category;
import pro.dbro.gameshow.model.Game;
import pro.dbro.gameshow.model.Player;
import pro.dbro.gameshow.model.Question;
import pro.dbro.gameshow.ui.QuestionAnsweredListener;


public class GameFragment extends Fragment implements QuestionAnsweredListener {
    private String TAG = getClass().getSimpleName();

    private Game mGame;
    private SoundEffectHandler mSoundFxHandler;

    @InjectView(R.id.playerGroup) RadioGroup mPlayerGroup;
    @InjectView(R.id.tableLayout) TableLayout mTable;

    private GameListener mListener;

    public static GameFragment newInstance(Game game) {
        GameFragment fragment = new GameFragment(game);
        Log.d("GameFragment", String.format("Creating GameFragment for game with %d players", game.players.size()));
        return fragment;
    }

    public GameFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public GameFragment(Game mGame) {
        super();
        this.mGame = mGame;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mSoundFxHandler = SoundEffectHandler.getInstance(getActivity());
        mSoundFxHandler.playSound(SoundEffectHandler.SoundType.FILL_BOARD);
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_game, container, false);
        ButterKnife.inject(this, root);

//        Typeface gameShowFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gyparody.ttf");
        Typeface tileFont     = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Swiss_911_Extra_Compressed.ttf");

        List<Player> players = mGame.players;

        for (Player player : players) {
            RadioButton playerButton = new RadioButton(getActivity());
            playerButton.setFocusable(false);
            playerButton.setBackgroundResource(R.drawable.player_bg);
            setPlayerScoreOnTextView(player, playerButton);
            playerButton.setTypeface(tileFont);
            playerButton.setButtonDrawable(null);
            playerButton.setTextSize(34);
            playerButton.setTextColor(getResources().getColor(R.color.text_player_score));
            playerButton.setPadding(8, 0, 8, 8);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(16, 0, 16, 8);
            playerButton.setLayoutParams(params);
            mPlayerGroup.addView(playerButton);
        }
        setCurrentPlayer(players.get(0));

        populateBoard(mGame, mTable, inflater);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        if (mSoundFxHandler != null) {
            mSoundFxHandler.release();
            mSoundFxHandler = null;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (GameListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onQuestionAnswered(ViewGroup questionTile,
                                   int answeringPlayerIdx,
                                   QuestionResult result,
                                   int wager) {

        mGame.markQuestionAnswered((pro.dbro.gameshow.model.Question) questionTile.getTag());
        if (mGame.getNumQuestionsAnswered() == 1) makeCategoriesNotFocusable();
        questionTile.setFocusable(false);
        questionTile.findViewById(R.id.value).setVisibility(View.INVISIBLE);
        questionTile.findViewById(R.id.dollarSign).setVisibility(View.INVISIBLE);

        Player answeringPlayer = mGame.players.get(answeringPlayerIdx);

        switch (result) {
            case CORRECT:
                incrementPlayerScore(answeringPlayer, wager);
                setCurrentPlayer(answeringPlayer);
                break;
            case INCORRECT:
                incrementPlayerScore(answeringPlayer, -wager);
                advanceCurrentPlayerIgnoring(answeringPlayer);
                break;
            case NO_RESPONSE:
                // Do nothing
                break;
        }

        if (mGame.isComplete()) {
            mListener.onGameComplete(mGame.getWinners());
        }
    }

    private void advanceCurrentPlayerIgnoring(Player ignored) {
        advanceCurrentPlayer();
        if (mGame.getCurrentPlayer().equals(ignored))
            advanceCurrentPlayer();
    }

    private void advanceCurrentPlayer() {
        Player nextPlayer = mGame.advanceCurrentPlayer();
        ((RadioButton) mPlayerGroup.getChildAt(mGame.getPlayerNumber(nextPlayer)))
                .setChecked(true);
    }

    private void setCurrentPlayer(Player player) {
        mGame.setCurrentPlayer(player);
        ((RadioButton) mPlayerGroup.getChildAt(mGame.getPlayerNumber(player)))
                .setChecked(true);
    }

    private void incrementPlayerScore(Player player, int value) {
        updatePlayerScore(player, value, true);
    }

    private void updatePlayerScore(Player player, int value, boolean delta) {
        int playerNumber = mGame.getPlayerNumber(player);
        player.score = (delta ? player.score + value : value);

        setPlayerScoreOnTextView(player, ((RadioButton) mPlayerGroup.getChildAt(playerNumber)));
    }

    private void setPlayerScoreOnTextView(Player player, TextView view) {
        int startSpan = 0;
        String text = String.format("%s %d", player.name.toUpperCase(), player.score);
        int endSpan = player.name.length();
        Spannable spanRange = new SpannableString(text);
        TextAppearanceSpan tas = new TextAppearanceSpan(view.getContext(), R.style.PlayerNameText);
        spanRange.setSpan(tas, startSpan, endSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(spanRange);
    }

    public interface GameListener {
        public void onGameComplete(List<Player> winners);
    }

    private void makeCategoriesNotFocusable() {
        TableRow firstRow = (TableRow) mTable.getChildAt(0);

        for(int colNum = 0; colNum < firstRow.getChildCount(); colNum++) {
            firstRow.getChildAt(colNum).setFocusable(false);
        }
    }

    private void populateBoard(Game game, TableLayout table, LayoutInflater inflater) {
        if (inflater == null) inflater = (LayoutInflater) table.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Typeface tileFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Swiss_911_Extra_Compressed.ttf");

        final int NUM_COLS = game.categories.size();
        final int NUM_ROWS = Category.REQUIRED_QUESTIONS + 1; // +1 for header

        for (int rowNum = 0; rowNum < NUM_ROWS; rowNum++) {
            TableRow row = new TableRow(getActivity());
            TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1f);

            if (rowNum == 0) rowParams.setMargins(0, 0, 0, 20);

            row.setLayoutParams(rowParams);
            row.setGravity(Gravity.CENTER_HORIZONTAL);
            row.setWeightSum(NUM_COLS);
            mTable.addView(row);

            for (int colNum = 0; colNum < NUM_COLS; colNum++) {
                TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
                params.setMargins(5, 5, 5, 5);

                if (rowNum == 0) {
                    TextView categoryTile = (TextView) inflater.inflate(R.layout.category_tile, row, false);
                    bindCategoryTile(game.categories.get(colNum), categoryTile);
                    categoryTile.setLayoutParams(params);
                    categoryTile.setTypeface(tileFont);
                    row.addView(categoryTile);
                    startFadeInAnimation(categoryTile, (long) (3400 * Math.random()));

                } else {
                    ViewGroup questionTile = (ViewGroup) inflater.inflate(R.layout.question_tile, row, false);
                    ((TextView) questionTile.findViewById(R.id.dollarSign)).setTypeface(tileFont);
                    if (game.categories.get(colNum).questions.size() > (rowNum - 1)) {
                        bindQuestionTile(game.categories.get(colNum).questions.get(rowNum - 1),
                                questionTile);
                        startFadeInAnimation(questionTile, (long) (3400 * Math.random()));
                    } else {
                        questionTile.setFocusable(false);
                        questionTile.findViewById(R.id.dollarSign).setVisibility(View.INVISIBLE);
                    }
                    ((TextView) questionTile.findViewById(R.id.value)).setTypeface(tileFont);
                    questionTile.setLayoutParams(params);
                    row.addView(questionTile);
                }
            }
        }
    }

    private void bindQuestionTile(Question question, ViewGroup questionTile) {
        ((TextView) questionTile.findViewById(R.id.value)).setText(String.format("%d",
                question.value));
        questionTile.setTag(question);

        questionTile.findViewById(R.id.dollarSign).setVisibility(View.VISIBLE);
        questionTile.findViewById(R.id.value).setVisibility(View.VISIBLE);
        questionTile.setFocusable(true);
    }

    private void bindCategoryTile(Category category, TextView categoryTile) {
        categoryTile.setText(category.title.toUpperCase());
        categoryTile.setTag(category);
    }

    public void populateCategory(Category category) {
        int colIdx = mGame.categories.indexOf(category);

        int numRows = mTable.getChildCount();

        for(int rowNum = 0; rowNum < numRows; rowNum++) {
            TableRow row = (TableRow) mTable.getChildAt(rowNum);

            if (rowNum == 0) {
                // Category cell
                TextView categoryTile = (TextView) row.getChildAt(colIdx);
                bindCategoryTile(category, categoryTile);
                startFadeInAnimation(categoryTile, 100 * rowNum);
            } else {
                // Question cell
                ViewGroup questionTile = (ViewGroup) row.getChildAt(colIdx);
                if (category.questions.size() > rowNum - 1)
                    bindQuestionTile(category.questions.get(rowNum - 1), questionTile);
                else {
                    questionTile.setFocusable(false);
                    questionTile.findViewById(R.id.dollarSign).setVisibility(View.INVISIBLE);
                    questionTile.findViewById(R.id.value).setVisibility(View.INVISIBLE);
                }
                startFadeInAnimation(questionTile, 100 * rowNum);
            }
        }
    }

    private void startFadeInAnimation(View target, long delay) {
        target.setAlpha(0f);
        ObjectAnimator anim = ObjectAnimator.ofFloat(target, "alpha", 0f, 1f);
        anim.setDuration(300);
        anim.setStartDelay(delay);
        anim.start();
    }

}
