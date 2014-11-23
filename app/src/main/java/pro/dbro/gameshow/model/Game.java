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

    public void addCategory(Category category) {
        if (categories == null) categories = new ArrayList<>();

        categories.add(category);
    }

    public int countQuestions() {
        int sum = 0;
        for (Category category : categories) {
            sum += category.questions.size();
        }
        return sum;
    }

}
