package pro.dbro.gameshow.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by davidbrodsky on 11/22/14.
 */
public class Question implements Serializable {

    public int value;
    public String prompt;
    public List<String> choices;
    public int correctChoice;
}
