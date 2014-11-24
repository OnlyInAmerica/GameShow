package pro.dbro.gameshow;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pro.dbro.gameshow.model.Player;

/**
 * Created by davidbrodsky on 11/23/14.
 */
public class PreferencesManager {

    private static SharedPreferences sPrefs;

    private static final String PREFS = "prefs";

    private static final String PLAYERS = "players";

    public static void savePlayers(Context context, List<Player> players) {
        HashSet<String> playerNames = new HashSet<>();

        for (Player player : players) {
            playerNames.add(player.name);
        }

        getPrefs(context).edit().putStringSet(PLAYERS, playerNames).apply();
    }

    public static List<Player> loadPlayers(Context context) {
        Set<String> playerNames = getPrefs(context).getStringSet(PLAYERS, null);
        List<Player> players = new ArrayList<>();
        if (playerNames != null) {
            for (String playerName : playerNames) {
                players.add(new Player(playerName));
            }
        }
        return players;
    }

    private static SharedPreferences getPrefs(Context context) {
        if (sPrefs == null) sPrefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return sPrefs;
    }
}
