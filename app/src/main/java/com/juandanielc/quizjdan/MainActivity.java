package com.juandanielc.quizjdan;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.juandanielc.quizjdan.data.Question;
import com.juandanielc.quizjdan.data.Quiz;
import com.juandanielc.quizjdan.data.QuizRepository;

public class MainActivity extends AppCompatActivity implements QuizRepository.Callback {

    private static final String KEY_LAST_ANSWER = "lastAnswer";
    private static final String KEY_QUESTION_ANSWERED = "question_answered";
    private static final String KEY_INDEX_QUESTION = "index_question";
    private static final String KEY_SCORE = "score";
    private static final int TYPE_SING = 0;
    private static final int KEY_JSON = 1;
    private static final int TYPE_REMOTE = 2;
    private static final int TYPE_REMOTE_FROM_LIST = 3;
    public static final String QUIZ_ID = "quiz_id";

    private Animation animationOut;
    private Animation animationIn;
    private Animation animationOut2;
    private Animation animationIn2;

    private TextView questionText;
    private ImageView resultImage;
    private Drawable imageCorrect;
    private Drawable imageIncorrect;

    private Button falseButton;
    private Button trueButton;
    private Button nextButton;

    private Quiz quiz;
    private int index;
    private int scoreValue = 0;
    private boolean questionAnswered;
    private boolean lastAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trueButton = (Button) findViewById(R.id.button_true);
        falseButton = (Button) findViewById(R.id.button_false);
        nextButton = (Button) findViewById(R.id.button_next);
        questionText = (TextView) findViewById(R.id.question_text);
        resultImage = (ImageView) findViewById(R.id.result_image);
        imageCorrect = getResources().getDrawable(R.drawable.correct, null);
        imageIncorrect = getResources().getDrawable(R.drawable.incorrect, null);

        index = 0;
        //quiz = new QuizRepository(this).getQuiz(1);
        //quiz = Quiz.getInstance();
        //quiz = new QuizRepository(this).getRemoteQuiz(); no works because needs un hilo.

        int type = TYPE_REMOTE_FROM_LIST;

        switch (type) {
            case TYPE_SING:
                quiz = Quiz.getInstance();
                break;
            case TYPE_REMOTE:
                new QuizRepository(this).getRemoteQuiz(0, this);
                break;
            case KEY_JSON:
                quiz = new QuizRepository(this).getQuiz(1);
                break;
            case TYPE_REMOTE_FROM_LIST:
                int id = getIntent().getIntExtra(QUIZ_ID, -1);
                new QuizRepository(this).getRemoteQuiz(id, this);
                break;
        }

        animationOut = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        animationOut.setDuration(1_000);

        animationIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        animationIn.setDuration(1_000);

        animationOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                return;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                return;
            }
        });

        animationIn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                return;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                falseButton.setEnabled(true);
                trueButton.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                return;
            }
        });

        animationOut2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        animationOut2.setDuration(2_000);

        animationIn2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        animationIn2.setDuration(5_00);

        trueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        falseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });

        if (savedInstanceState != null) {
            questionAnswered = savedInstanceState.getBoolean(KEY_QUESTION_ANSWERED, false);
            index = savedInstanceState.getInt(KEY_INDEX_QUESTION, 0);
            scoreValue = savedInstanceState.getInt(KEY_SCORE, 0);

            if (questionAnswered) {
                //lastAnswer = savedInstanceState.getBoolean(KEY_LAST_ANSWER);
                //checkAnswer(lastAnswer);
                index = 0;
            }
        }

        if (type == TYPE_SING || type ==  KEY_JSON) {
            showQuestion();

            if (questionAnswered) {
                checkAnswer(lastAnswer);
            }
        }
    }

    private void checkAnswer(boolean answer) {
        questionAnswered = true;
        lastAnswer = answer;

        if (answer == getQuestion().getAnswer()) {
            resultImage.setImageDrawable(imageCorrect);
            scoreValue++;
        } else {
            resultImage.setImageDrawable(imageIncorrect);
        }

        resultImage.startAnimation(animationIn2);
        resultImage.setVisibility(View.VISIBLE);
        falseButton.setEnabled(false);
        trueButton.setEnabled(false);
        nextButton.setEnabled(true);
    }

    private void showQuestion() {
        questionText.startAnimation(animationIn);
        questionText.setText(getQuestion().getStatement());
    }

    private Question getQuestion() {
        return quiz.getQuestions().get(index);
    }

    private void nextQuestion() {
        index++;
        if (index >= quiz.getQuestions().size()) {
            index = 0;
            Intent resultIntent = new Intent(this, ResultActivity.class);
            resultIntent.putExtra(ResultActivity.KEY_SCORE, scoreValue);
            resultIntent.putExtra(ResultActivity.KEY_TOTAL, Quiz.getInstance().getQuestions().size());
            startActivity(resultIntent);
            scoreValue = 0;
        }
        resultImage.startAnimation(animationOut2);
        resultImage.setVisibility(View.INVISIBLE);
        questionAnswered = false;
        nextButton.setEnabled(false);
        questionText.startAnimation(animationOut);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_LAST_ANSWER, lastAnswer);
        outState.putBoolean(KEY_QUESTION_ANSWERED, questionAnswered);
        outState.putInt(KEY_INDEX_QUESTION, index);
        outState.putInt(KEY_SCORE, scoreValue);
    }

    @Override
    public void onFailure() {
        Toast.makeText(this, "Unable to retrieve quiz", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess(Quiz quiz) {
        this.quiz = quiz;
        Toast.makeText(this, "Quiz loaded", Toast.LENGTH_LONG).show();
        //This used to live in onCreate but we moved it here
        showQuestion();

        if (questionAnswered) {
            checkAnswer(lastAnswer);
        }

    }
}
