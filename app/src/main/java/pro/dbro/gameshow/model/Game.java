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

    public Game() {
        categories = new ArrayList<>();
        players = new ArrayList<>();
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void addPlayers(List<Player> players) {
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

}
