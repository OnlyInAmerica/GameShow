package pro.dbro.gameshow;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pro.dbro.gameshow.model.Player;

public class ChoosePlayerFragment extends Fragment {

    private static final int MAX_PLAYERS = 4;

    @InjectView(R.id.title)
    TextView mTitleView;

    @InjectView(R.id.playerContainer)
    LinearLayout mPlayerContainer;

    private OnPlayersSelectedListener mListener;

    public ChoosePlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_choose_player, container, false);
        ButterKnife.inject(this, root);

        Typeface gameShowFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gyparody.ttf");
        mTitleView.setTypeface(gameShowFont);

        addNewPlayerEntryView(mPlayerContainer);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPlayersSelectedListener) activity;
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

    @OnClick(R.id.addPlayerBtn)
    public void onAddPlayerButtonClicked(View view) {
        if (mPlayerContainer.getChildCount() < MAX_PLAYERS) addNewPlayerEntryView(mPlayerContainer);
    }

    @OnClick(R.id.removePlayerBtn)
    public void onRemovePlayerButtonClicked(View view) {
        if (mPlayerContainer.getChildCount() > 0) mPlayerContainer.removeViewAt(mPlayerContainer.getChildCount() - 1);
    }

    @OnClick(R.id.playBtn)
    public void onPlayersSelected(View view) {
        ArrayList<Player> players = new ArrayList<>();

        Log.i("players", "PlayerContainer has children: " + mPlayerContainer.getChildCount());
        for(int x = 0; x < mPlayerContainer.getChildCount(); x++) {
            Player player = new Player(((TextView) mPlayerContainer.getChildAt(x)).getText().toString());
            players.add(player);
            Log.i("players", "Add player : " + player.name);

        }
        mListener.onPlayersSelected(players);
    }

    private void addNewPlayerEntryView(ViewGroup container) {
        EditText playerEntry = new EditText(getActivity());
        playerEntry.setHint("New Player");
        playerEntry.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user_white_small, 0, 0, 0);
        container.addView(playerEntry);
    }

    public interface OnPlayersSelectedListener {
        // TODO: Update argument type and name
        public void onPlayersSelected(List<Player> players);
    }

}
