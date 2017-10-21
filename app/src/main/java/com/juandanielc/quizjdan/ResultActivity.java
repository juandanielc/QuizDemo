package com.juandanielc.quizjdan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private TextView resultText;
    public static final String KEY_SCORE = "score";
    public static final String KEY_TOTAL = "total";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultText = (TextView)findViewById(R.id.result_text);

        int score = getIntent().getIntExtra(KEY_SCORE, -1);
        int total = getIntent().getIntExtra(KEY_TOTAL, -1);

        resultText.setText(String.format("%d / %d", score , total));
    }
}
