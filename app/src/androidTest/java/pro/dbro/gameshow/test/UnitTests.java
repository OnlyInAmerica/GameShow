package pro.dbro.gameshow.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.ArrayList;
import java.util.List;

import pro.dbro.gameshow.model.Category;
import pro.dbro.gameshow.model.Game;
import pro.dbro.gameshow.model.Player;
import pro.dbro.gameshow.model.Question;

/**
 * Created by davidbrodsky on 11/29/14.
 */
public class UnitTests extends AndroidTestCase {

    @SmallTest
    public void testPlayerTurnAdvancement() throws Throwable {

        Game game = new Game();
        addFourPlayersToGame(game);

        // Test two full cycles through players
        for (int x = 0; x < 2; x++) {
            for (Player player : game.players) {
                assertEquals(game.getCurrentPlayer(), player);
                game.advanceCurrentPlayer();
            }
        }
    }

    /**
     * Test Game selection of DailyDoubles, tracking of highest available question value,
     * and maximum DailyDouble wager per player.
     *
     * @throws Throwable
     */
    @SmallTest
    public void testQuestionManagement() throws Throwable {

        Game game = new Game();
        fullyPopulateGame(game);

        assertEquals(game.getMaxDailyDoubleWagerForPlayer(game.getCurrentPlayer()), 600);
        assertEquals(game.getHighestAvailabeQuestionValue(), 600);

        int numDailyDoubles = 0;
        int numQuestionsAnswered = 0;

        List<Category> categories = game.categories;

        for (Category category : categories) {
            List<Question> questions = category.questions;
            for (Question question : questions) {
                game.makeQuestionCandidateForDailyDouble(question);

                if (question.isDailyDouble) numDailyDoubles++;
                assertEquals(question.isAnswered, false);
                game.markQuestionAnswered(question);
                numQuestionsAnswered++;
                assertEquals(question.isAnswered, true);

                assertEquals(game.getNumQuestionsAnswered(), numQuestionsAnswered);
            }
        }

        assertTrue(numDailyDoubles == 2);
        assertEquals(game.getHighestAvailabeQuestionValue(), 0);

        Player aPlayer = game.players.get(0);
        assertEquals(game.getMaxDailyDoubleWagerForPlayer(aPlayer), 0);
        aPlayer.score = 400;
        assertEquals(game.getMaxDailyDoubleWagerForPlayer(aPlayer), 400);
        Question aQuestion = categories.get(0).questions.get(0);
        aQuestion.isAnswered = false;
        aQuestion.value = 1400;
        assertEquals(game.getMaxDailyDoubleWagerForPlayer(aPlayer), 1400);
    }

    private void addFourPlayersToGame(Game game) {
        String[] playerNames = new String[] { "alfred", "bort", "calyx", "darrel"};

        ArrayList<Player> players = new ArrayList<>();
        for (String playerName : playerNames) {
            players.add(new Player(playerName));
        }
        game.addPlayers(players);
    }

    /**
     * Fully populate four player game with question values ranging from 200 to 600.
     */
    private void fullyPopulateGame(Game game) {
        String[] categoryNames = new String[] { "one", "two", "three", "four", "five", "six"};
        String[] questionPrompts = new String[] { "one", "two", "three", "four", "five"};
        ArrayList<String> questionChoices = new ArrayList<String>() {{ add("whatever"); }};

        addFourPlayersToGame(game);

        for (String categoryName : categoryNames) {
            Category category = new Category(categoryName);
            int questionValue = 100;
            for (String questionPrompt : questionPrompts) {

                Question question = new Question();
                question.prompt = questionPrompt;
                question.value = questionValue += 100;
                question.choices = questionChoices;

                category.addQuestion(question);
            }
            game.addCategory(category);
        }
        assertEquals(categoryNames.length * questionPrompts.length, game.countQuestions());
    }
}