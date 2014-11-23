package pro.dbro.gameshow;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import pro.dbro.gameshow.model.Game;


public class GameFragment extends Fragment implements QuestionAnsweredListener {

    private Game game;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // TODO: Rename and change types and number of parameters
    public static GameFragment newInstance(Game game) {
        GameFragment fragment = new GameFragment();
        fragment.game = game;
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        Typeface gameShowFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gyparody.ttf");
        Typeface tileFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/swiss_911.ttf");
        TextView headerTitle = (TextView)root.findViewById(R.id.header);
        headerTitle.setTypeface(gameShowFont);

        TableLayout table = (TableLayout) root.findViewById(R.id.tableLayout);

        final int NUM_COLS = game.categories.size();
        final int NUM_ROWS = game.categories.get(0).questions.size();


        for (int x = 0; x < NUM_ROWS; x++) {
            TableRow row = new TableRow(getActivity());
            TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1f);

            if (x == 0) rowParams.setMargins(0,0,0,20);

            row.setLayoutParams(rowParams);
            row.setGravity(Gravity.CENTER_HORIZONTAL);
            row.setWeightSum(NUM_COLS);
            table.addView(row);


            for (int y = 0; y < NUM_COLS; y++) {
                ViewGroup tile;
                TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
                params.setMargins(10,10,10,10);

                if (x == 0) {
                    tile = (ViewGroup) inflater.inflate(R.layout.header_tile, row, false);
                    tile.setFocusable(false);
                    ((TextView) tile.findViewById(R.id.value)).setText(game.categories.get(y).title.toUpperCase());
                } else {
                    tile = (ViewGroup) inflater.inflate(R.layout.question_tile, row, false);
                    if (game.categories.get(y).questions.size() > x) {
                        ((TextView) tile.findViewById(R.id.value)).setText(String.format("$%d",
                                game.categories.get(y).questions.get(x).value));
                        tile.setTag(game.categories.get(y).questions.get(x));
                    } else {
                        tile.setFocusable(false);
                    }
                }

                tile.setLayoutParams(params);

                ((TextView) tile.findViewById(R.id.value)).setTypeface(tileFont);
                row.addView(tile);
            }
        }
        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onQuestionAnswered(ViewGroup questionTile) {
        questionTile.setFocusable(false);
        ((TextView) questionTile.findViewById(R.id.value)).setText("");
    }
}
