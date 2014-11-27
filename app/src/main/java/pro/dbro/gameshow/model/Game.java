package pro.dbro.gameshow.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidbrodsky on 11/22/14.
 */
public class Game {

    public static final int REQUIRED_CATEGORIES = 6;

    public List<Category> categories;
    public List<Player> players;
    private Player currentPlayer;

    public Game() {
        categories = new ArrayList<>();
        players = new ArrayList<>();
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void addPlayers(List<Player> players) {
        currentPlayer = players.get(0);
        this.players.addAll(players);
    }

    public int countQuestions() {
        int sum = 0;
        for (Category category : categories) {
            sum += category.questions.size();
        }
        return sum;
    }

    public List<Player> getWinners() {
        ArrayList<Player> winners = new ArrayList<>();
        int topScore = 0;

        for (Player player : players) {
            if (player.score > topScore) {
                winners.clear();
                topScore = player.score;
                winners.add(player);
            } else if (player.score == topScore) {
                winners.add(player);
            }
        }

        return winners;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player advanceCurrentPlayer() {
        int currentPlayerNumber = players.indexOf(currentPlayer);
        int nextPlayerNumber = (currentPlayerNumber == players.size() - 1) ?
                0 : currentPlayerNumber + 1;

        return players.get(nextPlayerNumber);
    }

    public int getPlayerNumber(Player player) {
        return players.indexOf(player);
    }

}
