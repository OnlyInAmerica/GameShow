package pro.dbro.gameshow;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
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

        List<Player> players = PreferencesManager.loadPlayers(getActivity());

        if (players.size() == 0) {
            addNewPlayerEntryView(mPlayerContainer);
        } else {
            for (Player player : players) {
                addNewPlayerEntryView(mPlayerContainer, player.name);
            }
        }

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
        if (mPlayerContainer.getChildCount() > 0)
            mPlayerContainer.removeViewAt(mPlayerContainer.getChildCount() - 1);
    }

    @OnClick(R.id.playBtn)
    public void onPlayersSelected(View view) {
        ArrayList<Player> players = new ArrayList<>();

        for (int x = 0; x < mPlayerContainer.getChildCount(); x++) {
            Player player = new Player(((TextView) mPlayerContainer.getChildAt(x)).getText().toString());
            players.add(player);
        }
        PreferencesManager.savePlayers(getActivity(), players);
        mListener.onPlayersSelected(players);
    }

    private void addNewPlayerEntryView(ViewGroup container) {
        addNewPlayerEntryView(container, null);
    }

    private void addNewPlayerEntryView(ViewGroup container, String name) {
        EditText playerEntry = new EditText(getActivity());
        playerEntry.setHint(getActivity().getString(R.string.new_player));
        if (name != null) playerEntry.setText(name);
        playerEntry.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user_white_small, 0, 0, 0);
        container.addView(playerEntry);
    }

    public interface OnPlayersSelectedListener {
        public void onPlayersSelected(List<Player> players);
    }

}
