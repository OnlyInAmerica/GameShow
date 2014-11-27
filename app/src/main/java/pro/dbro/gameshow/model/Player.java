package pro.dbro.gameshow.model;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by davidbrodsky on 11/22/14.
 */
public class Player implements Serializable{

    public String name;
    public int score;
    public Set<Question> questionsAnswered;

    public Player(String name) {
        this.name = name;
    }
}
