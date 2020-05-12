package com.bhavesh.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bhavesh.trivia.data.AnswerListAsyncResponse;
import com.bhavesh.trivia.data.QuestionBank;
import com.bhavesh.trivia.model.Question;
import com.bhavesh.trivia.model.Score;
import com.bhavesh.trivia.util.Prefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView questionTextView;
    private TextView questionCounterTextView;
    private Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private TextView scoreTextView;
    private TextView highScoreTextView;

    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = new Score();
        prefs = new Prefs(MainActivity.this);

        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        questionCounterTextView = findViewById(R.id.counter_text);
        questionTextView = findViewById(R.id.question_textview);
        scoreTextView = findViewById(R.id.score_text);
        highScoreTextView = findViewById(R.id.highest_score);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);

        // Get previous state
        currentQuestionIndex = prefs.getState();

        scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
        highScoreTextView.setText(MessageFormat.format("Highest Score: {0}", String.valueOf(prefs.getHighScore())));

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {

                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getStatement());
                Log.d("Inside", "onCreate: " + questionArrayList);

                questionCounterTextView.setText(MessageFormat.format("{0} / {1}", String.valueOf(currentQuestionIndex), String.valueOf(questionArrayList.size())));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev_button:
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
                    updateQuestion();
                }
                break;

            case R.id.next_button:
                goNext();
                break;

            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                break;

            case R.id.false_button:
                checkAnswer(false);
                updateQuestion();
                break;

        }
    }

    private void checkAnswer(boolean userChoice) {
        boolean correctAnswer = questionList.get(currentQuestionIndex).isAnswer();
        int toastMessageId = 0;

        if(userChoice == correctAnswer) {
            fadeView();
            toastMessageId = R.string.correct_answer;
            addPoints();
        }
        else {
            shakeAnimation();
            toastMessageId = R.string.wrong_answer;
            deductPoints();
        }

        Toast.makeText(MainActivity.this, toastMessageId,
                Toast.LENGTH_SHORT)
                .show();
    }

    private void addPoints() {
        scoreCounter += 100;
        score.setScore(scoreCounter);
        scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));

        Log.d("Score", "addPoints: " + score.getScore());
    }

    private void deductPoints() {
        scoreCounter -= 100;
        if (scoreCounter < 0)
            scoreCounter = 0;

        score.setScore(scoreCounter);
        scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));

        Log.d("Score sub", "deductPoints: " + score.getScore());
    }

    private  void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getStatement();
        questionTextView.setText(question);
        questionCounterTextView.setText(MessageFormat.format("{0} / {1}", String.valueOf(currentQuestionIndex), String.valueOf(questionList.size())));
    }

    private void fadeView() {
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void goNext() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestion();
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.saveHighScore(score.getScore());
        prefs.setState(currentQuestionIndex);
    }

}
