package pro.dbro.gameshow.ui;

import android.view.ViewGroup;

/**
  * Created by davidbrodsky on 11/22/14.
  */
 public interface QuestionAnsweredListener {

    public static enum QuestionResult { NO_RESPONSE, INCORRECT, CORRECT }

    public void onQuestionAnswered(ViewGroup questionTile, int answeringPlayerIdx, QuestionResult result, int wager);
 }
