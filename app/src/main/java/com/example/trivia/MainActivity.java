package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.trivia.controller.MySingleton;
import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static android.provider.Telephony.BaseMmsColumns.MESSAGE_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView quesCounter;
    private TextView quesView;
    private TextView currentScoreView;
    private TextView highestScoreView;
    private Button trueButton;
    private Button falseButton;
    private ImageButton prevButton;
    private ImageButton nextButton;
    //private CardView cardView;
    private int currentQuesIndex = 0;
    private int currentScore = 0;
    private int highestScore = 0;
    private List<Question> questionList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quesCounter = findViewById(R.id.ques_counter);
        quesView = findViewById(R.id.question_textview);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        currentScoreView = findViewById(R.id.currentScore_view);
        highestScoreView = findViewById(R.id.highestScore_view);

        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        currentScoreView.setText(MessageFormat.format("Current Score: {0}", currentScore));

        SharedPreferences sharedPreferences = getSharedPreferences(MESSAGE_ID, MODE_PRIVATE);
        highestScore = sharedPreferences.getInt("highestscore", 0);
        highestScoreView.setText(MessageFormat.format("Highest Score: {0}", highestScore));


        questionList = new QuestionBank(this).getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void ProcessFinished(ArrayList<Question> questionArrayList) {

                Log.d("Main", "ProcessFinished: " + questionArrayList);

                /*The questionArrayList is an array containing all the Question objects.*/
                quesView.setText(questionArrayList.get(currentQuesIndex).getQuestion());
                quesCounter.setText(MessageFormat.format("{0} / {1}", currentQuesIndex, questionList.size()));

            }
        });
        //Log.d("Main", "onCreate: "+ questionList);
        /*Here, even though the getQuestions() is returning a list of Question object, it is showing an empty array
         * while logging the questionList. This is because, in http networking, things work Asynchronously which means
         * we are calling the getQuestion() here, which is fetching the data from the API which contains many async tasks.
         * So, it might happen that the whole task is not completed fully, while returning the list and thus, we got
         * an empty list. Therefore, we need a mechanism which would signal our Activity that all the Async tasks are
         * completed and the list is ready to be returned. We will use a callback in the getQuestions()..*/


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.prev_button:
                if (currentQuesIndex > 0) {
                    currentQuesIndex = currentQuesIndex - 1;
                    quesCounter.setText(MessageFormat.format("{0} / {1}", currentQuesIndex, questionList.size()));
                    updateQuestion();
                }
                break;

            case R.id.next_button:
                currentQuesIndex = (currentQuesIndex + 1) % questionList.size();
                quesCounter.setText(MessageFormat.format("{0} / {1}", currentQuesIndex, questionList.size()));
                updateQuestion();
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

    private void checkAnswer(boolean answer) {
        if (questionList.get(currentQuesIndex).isAnswerTrue() == answer) {
            Toast.makeText(MainActivity.this, "Correct!", Toast.LENGTH_SHORT).show();
            currentScore += 10;
            currentScoreView.setText(MessageFormat.format("Current Score: {0}", currentScore));
            fadeView();
        } else {
            Toast.makeText(MainActivity.this, "Wrong!", Toast.LENGTH_SHORT).show();
            if (currentScore > 0) {
                currentScore -= 5;
                currentScoreView.setText(MessageFormat.format("Current Score: {0}", currentScore));
            }
            shakeAnimation();
        }
    }

    private void updateQuestion() {
        quesView.setText(questionList.get(currentQuesIndex).getQuestion());
    }

    private void shakeAnimation() {
        final CardView cardView = findViewById(R.id.cardView);

        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

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
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences(MESSAGE_ID, MODE_PRIVATE);
        SharedPreferences getSharedPrefs = getSharedPreferences(MESSAGE_ID, MODE_PRIVATE);

        int highestScorePrefs = getSharedPrefs.getInt("highestscore", 0);

        if (highestScorePrefs == 0 || highestScorePrefs < currentScore) {

            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("highestscore", currentScore);
            editor.apply();
        }

    }
}
