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

    private static final String ARG_GAME = "game";

    private Game mGame;
    private SoundEffectHandler mSoundFxHandler;

    @InjectView(R.id.playerGroup) RadioGroup mPlayerGroup;
    @InjectView(R.id.tableLayout) TableLayout mTable;

    private GameListener mListener;

    public static GameFragment newInstance(Game game) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_GAME, game);
        GameFragment fragment = new GameFragment();
        fragment.setArguments(bundle);
        Log.d("GameFragment", String.format("Creating GameFragment for game with %d players", game.players.size()));
        return fragment;
    }

    public GameFragment() {
        // Required empty public constructor
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
        Typeface tileFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Swiss_911_Extra_Compressed.ttf");

        List<Player> players = mGame.players; // NPE on restart

        for (Player player : players) {
            RadioButton playerButton = new RadioButton(getActivity());
            playerButton.setFocusable(false);
            playerButton.setClickable(false);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ARG_GAME, mGame);
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
        getActivity().setTitle("Gameboard");
        try {
            mListener = (GameListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        mGame = (Game) getArguments().getSerializable(ARG_GAME);
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

        Question answeredQuestion = (Question) questionTile.getTag();
        mGame.markQuestionAnswered(answeredQuestion);
        if (mGame.getNumQuestionsAnswered() == 1) makeCategoriesNotFocusable();
        questionTile.setFocusable(false);
        questionTile.setClickable(false);
        questionTile.findViewById(R.id.value).setVisibility(View.INVISIBLE);
        questionTile.findViewById(R.id.dollarSign).setVisibility(View.INVISIBLE);

        Player answeringPlayer = mGame.players.get(answeringPlayerIdx);

        switch (result) {
            case CORRECT:
                incrementPlayerScore(answeringPlayer, wager);
                setCurrentPlayer(answeringPlayer);
                break;
            case NO_RESPONSE:
                if (!answeredQuestion.isDailyDouble) break;
            case INCORRECT:
                incrementPlayerScore(answeringPlayer, -wager);
                advanceCurrentPlayerIgnoring(answeringPlayer);
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
        view.announceForAccessibility("Player " + player.name + " score now " + player.score);
        int endSpan = player.name.length();
        Spannable spanRange = new SpannableString(text);
        TextAppearanceSpan tas = new TextAppearanceSpan(view.getContext(), R.style.PlayerNameText);
        spanRange.setSpan(tas, startSpan, endSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(spanRange);
    }

    public interface GameListener {
        public void onGameComplete(List<Player> winners);

        public void onQuestionSelected(ViewGroup tile, Question question);

        public void onCategorySelected(Category category);
    }

    private void makeCategoriesNotFocusable() {
        TableRow firstRow = (TableRow) mTable.getChildAt(0);

        for (int colNum = 0; colNum < firstRow.getChildCount(); colNum++) {
            firstRow.getChildAt(colNum).setFocusable(false);
            firstRow.getChildAt(colNum).setClickable(false);
        }
    }

    private void populateBoard(Game game, final TableLayout table, LayoutInflater inflater) {
        if (inflater == null)
            inflater = (LayoutInflater) table.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
                    Category category = game.categories.get(colNum);
                    bindCategoryTile(category, categoryTile);
                    categoryTile.setLayoutParams(params);
                    categoryTile.setTypeface(tileFont);
                    categoryTile.setContentDescription("Category: " + category.title);
                    row.addView(categoryTile);
                    startFadeInAnimation(categoryTile, (long) (3400 * Math.random()));

                } else {
                    ViewGroup questionTile = (ViewGroup) inflater.inflate(R.layout.question_tile, row, false);
                    ((TextView) questionTile.findViewById(R.id.dollarSign)).setTypeface(tileFont);
                    if (game.categories.get(colNum).questions.size() > (rowNum - 1)) {
                        bindQuestionTile(game.categories.get(colNum).questions.get(rowNum - 1),
                                questionTile, game.categories.get(colNum));
                        startFadeInAnimation(questionTile, (long) (3400 * Math.random()));
                    } else {
                        questionTile.setFocusable(false);
                        questionTile.setClickable(false);
                        questionTile.findViewById(R.id.dollarSign).setVisibility(View.INVISIBLE);
                    }
                    ((TextView) questionTile.findViewById(R.id.value)).setTypeface(tileFont);
                    questionTile.setLayoutParams(params);
                    row.addView(questionTile);
                }
            }
        }

        table.postDelayed(new Runnable() {
            @Override
            public void run() {
                table.announceForAccessibility("Before answering any game questions, you may select a category to swap it for another");
            }
        }, 1000);
    }

    private void bindQuestionTile(Question question, ViewGroup questionTile, Category category) {
        ((TextView) questionTile.findViewById(R.id.value)).setText(String.format("%d",
                question.value));
        questionTile.setTag(question);

        questionTile.findViewById(R.id.dollarSign).setVisibility(View.VISIBLE);
        questionTile.findViewById(R.id.value).setVisibility(View.VISIBLE);
        questionTile.setContentDescription(category.title + " for " + question.value + " dollars");
        questionTile.setFocusable(true);
        questionTile.setClickable(true);

        questionTile.setOnClickListener(questionClickListener);
    }

    private void bindCategoryTile(Category category, TextView categoryTile) {
        categoryTile.setText(category.title.toUpperCase());
        categoryTile.setTag(category);

        categoryTile.setOnClickListener(categoryClickListener);
    }

    public void populateCategory(Category category) {
        int colIdx = mGame.categories.indexOf(category);

        int numRows = mTable.getChildCount();

        for (int rowNum = 0; rowNum < numRows; rowNum++) {
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
                    bindQuestionTile(category.questions.get(rowNum - 1), questionTile, mGame.categories.get(colIdx));
                else {
                    questionTile.setFocusable(false);
                    questionTile.setClickable(false);
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

    private View.OnClickListener questionClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mListener.onQuestionSelected((ViewGroup) v, (Question) v.getTag());
        }
    };

    private View.OnClickListener categoryClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mListener.onCategorySelected((Category) v.getTag());
        }
    };

}
