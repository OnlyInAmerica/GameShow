package pro.dbro.gameshow.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidbrodsky on 11/22/14.
 */
public class Game implements Serializable {

    public static final int REQUIRED_CATEGORIES = 6;
    public static final int NUM_DAILY_DOUBLES = 2;

    public List<Category> categories;
    public List<Player> players;
    private Player currentPlayer;
    private int numDailyDoublesOffered = 0;
    private int numQuestionsAnswered = 0;

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

        currentPlayer = players.get(nextPlayerNumber);
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    public int getPlayerNumber(Player player) {
        return players.indexOf(player);
    }

    public void markQuestionAnswered(Question question) {
        numQuestionsAnswered++;
    }

    public int getNumQuestionsAnswered() {
        return numQuestionsAnswered;
    }

    public boolean isComplete() {
        return numQuestionsAnswered == countQuestions();
    }

    public void makeQuestionCandidateForDailyDouble(Question question) {
        if (numDailyDoublesOffered == NUM_DAILY_DOUBLES) return;

        int numQuestions = countQuestions();
        float percentQuestionsRemaining = (numQuestions - numQuestionsAnswered) / (float) numQuestions;

        if (percentQuestionsRemaining < .66) {
            if (Math.random() < (1 / (numQuestions - numQuestionsAnswered))) {
                question.isDailyDouble = true;
                numDailyDoublesOffered++;
            }
        }
    }

}
