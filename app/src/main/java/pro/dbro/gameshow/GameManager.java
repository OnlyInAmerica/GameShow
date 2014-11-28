package pro.dbro.gameshow;

import pro.dbro.gameshow.model.Game;

/**
 * Created by davidbrodsky on 11/27/14.
 */
public class GameManager {

    private static Game sGame;

    public static Game getInstance() {
        if (sGame == null) sGame = new Game();
        return sGame;
    }

    public static void setGame(Game game) {
        sGame = game;
    }
}
