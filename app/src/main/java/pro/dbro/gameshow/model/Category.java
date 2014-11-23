package pro.dbro.gameshow.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidbrodsky on 11/22/14.
 */
public class Category {

    public static final int REQUIRED_QUESTIONS = 6;

    public String title;
    public int jServiceId;

    public List<Question> questions;

    public void addQuestion(Question question) {
        if (questions == null) questions = new ArrayList<>();
        questions.add(question);
    }
}
