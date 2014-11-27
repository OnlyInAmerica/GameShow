package pro.dbro.gameshow.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by davidbrodsky on 11/22/14.
 */
public class Question implements Serializable {

    public int value;
    public String prompt;
    public List<String> choices;
    public int correctChoice;

    public boolean isDailyDouble;

    public String getAnswer() {
        if (choices.size() == 1) return choices.get(0);

        return choices.get(correctChoice);
    }
}
