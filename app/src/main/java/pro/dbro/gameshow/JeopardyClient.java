package pro.dbro.gameshow;

import android.content.Context;
import android.text.Html;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import pro.dbro.gameshow.model.Category;
import pro.dbro.gameshow.model.Game;
import pro.dbro.gameshow.model.Question;

/**
 * Created by davidbrodsky on 11/22/14.
 */
public class JeopardyClient {
    public final String TAG = getClass().getSimpleName();

    public interface GameCompleteCallback {
        public void onGameComplete(Game game);
    }

    private Context mContext;

    public JeopardyClient(Context context) {
        mContext = context;
    }

    public void completeGame(final Game game, final GameCompleteCallback cb) {
        final int categoriesMissing = Game.REQUIRED_CATEGORIES - game.categories.size();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int categoryOffset = (int) (Math.random() * 16690);
                    JsonArray categories = Ion.with(mContext)
                                 .load(String.format("http://jservice.io/api/categories?offset=%d&count=%d", categoryOffset, categoriesMissing))
                                 .asJsonArray().get();

                    for (JsonElement categoryJsonElement : categories) {
                        JsonObject categoryJson = categoryJsonElement.getAsJsonObject();
                        final Category category = new Category();
                        category.title = categoryJson.get("title").getAsString();
                        category.jServiceId = categoryJson.get("id").getAsInt();

                        game.addCategory(category);

                        //Log.i(TAG, "Fetching questions for category " + category.title);
                        JsonArray questions = Ion.with(mContext)
                                .load(String.format("http://jservice.io/api/clues?category=%d&count=%d", category.jServiceId, Category.REQUIRED_QUESTIONS))
                                .asJsonArray().get();

                        //Log.i(TAG, String.format("Got %d questions for category %s", questions.size(), category.title));

                        for (int x = 0; x < questions.size() && category.questions.size() < Category.REQUIRED_QUESTIONS; x++ ) {

                            JsonObject questionJson = questions.get(x).getAsJsonObject();

                            if (questionJson.get("question").getAsString().replace(" ","").length() == 0) continue;

                            Question question = new Question();
                            question.prompt = questionJson.get("question").getAsString();
                            if (!questionJson.get("value").isJsonNull()) {
                                question.value = questionJson.get("value").getAsInt();
                            } else {
                                question.value = 400;
                            }
                            question.choices = new ArrayList<>();
                            question.choices.add(Html.fromHtml(questionJson.get("answer").getAsString()).toString());
                            question.correctChoice = 0;
                            category.addQuestion(question);

                            //Log.i(TAG, String.format("Added %d/%d questions", ++totalQuestionsAdded,
                            //        Category.REQUIRED_QUESTIONS * Game.REQUIRED_CATEGORIES));
                        }
                    }

                    boolean selectedDailyDouble = false;
                    while (!selectedDailyDouble) {
                        int categoryIdx = (int) (Math.random() * (Game.REQUIRED_CATEGORIES - 1));
                        int questionIdx = (int) (Math.random() * (Category.REQUIRED_QUESTIONS - 1));
                        if (game.categories.get(categoryIdx).questions.size() > questionIdx) {
                            game.categories.get(categoryIdx).questions.get(questionIdx)
                                    .isDailyDouble = true;
                            selectedDailyDouble = true;
                            Log.d(TAG, String.format("Daily double is %s for %d",
                                    game.categories.get(categoryIdx).title,
                                    game.categories.get(categoryIdx).questions.get(questionIdx).value));
                        }
                    }

                    cb.onGameComplete(game);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
