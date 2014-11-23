package pro.dbro.gameshow;

import android.app.Activity;
 import android.content.Intent;
 import android.graphics.Typeface;
 import android.os.Bundle;
 import android.view.KeyEvent;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.Button;
 import android.widget.TextView;
 import android.widget.Toast;

 import java.util.List;

 import butterknife.ButterKnife;
 import butterknife.InjectView;
 import butterknife.InjectViews;
 import butterknife.OnClick;
 import pro.dbro.gameshow.model.Question;


public class QuestionActivity extends Activity {

     public static String INTENT_ACTION = "pro.dbro.gameshow.QuestionResult";
     public static int ANSWERED_CORRECT = 1;
     public static int ANSWERED_INCORRECT = 0;

     private static enum State {WILL_SHOW_ANSWER, WILL_SELECT_ANSWER, SHOWING_ANSWER}

     private Question question;
     private State state;

     @InjectView(R.id.prompt)
     TextView promptView;

     @InjectView(R.id.choiceContainer)
     ViewGroup choiceContainer;

     @InjectView(R.id.showAnswer)
     Button showAnswer;

     @InjectViews({R.id.choice1, R.id.choice2, R.id.choice3, R.id.choice4})
     List<Button> choiceViews;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_question);
         ButterKnife.inject(this);

         question = (Question) getIntent().getExtras().getSerializable("question");

         Typeface tileFont = Typeface.createFromAsset(getAssets(), "fonts/Korinna_Bold.ttf");

         promptView.setTypeface(tileFont);
         promptView.setText(question.prompt.toUpperCase());

         if (question.choices.size() > 1) {
             state = State.WILL_SELECT_ANSWER;
             for (int x = 0; x < question.choices.size(); x++) {
                 choiceViews.get(x).setText(question.choices.get(x));
                 choiceViews.get(x).setTag(x);
             }
         } else {
             state = State.WILL_SHOW_ANSWER;
             choiceContainer.setVisibility(View.GONE);
             showAnswer.setVisibility(View.VISIBLE);
         }
     }

     @OnClick(R.id.showAnswer)
     public void onShowAnswerClicked(View showAnswer) {
         promptView.setText(question.choices.get(0));

         choiceContainer.setVisibility(View.VISIBLE);
         showAnswer.setVisibility(View.GONE);

         choiceViews.get(0).setVisibility(View.INVISIBLE);

         choiceViews.get(1).setText("I got it");
         choiceViews.get(1).setTag(true);

         choiceViews.get(2).setText("I didn't");
         choiceViews.get(2).setTag(false);

         choiceViews.get(3).setVisibility(View.INVISIBLE);

         state = State.SHOWING_ANSWER;
     }

     @OnClick({R.id.choice1, R.id.choice2, R.id.choice3, R.id.choice4})
     public void onAnswerSelected(View selectedAnswerView) {
         switch (state) {
             case WILL_SELECT_ANSWER:

                 int selection = (int) selectedAnswerView.getTag();
                 if (selection == question.correctChoice) {
                     Toast.makeText(this, "CORRECT", Toast.LENGTH_SHORT).show();
                     finishWithQuestionResult(true);
                 } else {
                     Toast.makeText(this, "WRONG", Toast.LENGTH_SHORT).show();
                     finishWithQuestionResult(false);
                 }
                 break;

             case SHOWING_ANSWER:

                 boolean answeredCorrectly = (boolean) getCurrentFocus().getTag();
                 finishWithQuestionResult(answeredCorrectly);
                 break;
         }
     }

     private void finishWithQuestionResult(boolean correctAnswer) {
         Intent result = new Intent(INTENT_ACTION);
         setResult((correctAnswer ? ANSWERED_CORRECT : ANSWERED_INCORRECT), result);
         finish();
     }
 }
