package pro.dbro.gameshow;

import android.app.Activity;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GameFragment newInstance(String param1, String param2) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
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

        String[] categories = new String[] {"Dogs", "Nails", "Tacos", "Scandal", "Kauai", "SpaNight"};
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        Typeface gameShowFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gyparody.ttf");
        Typeface tileFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/swiss_911.ttf");
        TextView headerTitle = (TextView)root.findViewById(R.id.header);
        headerTitle.setTypeface(gameShowFont);

        TableLayout table = (TableLayout) root.findViewById(R.id.tableLayout);

        final int NUM_COLS = 6;
        final int NUM_ROWS = 6;

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
                    ((TextView) tile.findViewById(R.id.title)).setText(categories[y].toUpperCase());
                } else {
                    tile = (ViewGroup) inflater.inflate(R.layout.question_tile, row, false);
                    ((TextView) tile.findViewById(R.id.title)).setText(String.format("$%d", (x + 1) * 100));
                }

                tile.setLayoutParams(params);

                ((TextView) tile.findViewById(R.id.title)).setTypeface(tileFont);
                row.addView(tile);
            }
        }
        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
