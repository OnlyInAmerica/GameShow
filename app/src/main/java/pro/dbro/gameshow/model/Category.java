package pro.dbro.gameshow.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidbrodsky on 11/22/14.
 */
public class Category implements Serializable{

    public static final int REQUIRED_QUESTIONS = 5;

    public String title;
    public int jServiceId;

    public Category(String title) {
        this.title = title;
    }

    public List<Question> questions = new ArrayList<>();

    public void addQuestion(Question question) {
        questions.add(question);
    }
}
