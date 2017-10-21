package com.juandanielc.quizjdan.data;

/**
 * Created by JDan on 18/10/2017.
 */

public class Question {
    private boolean answer;
    private String statement;

    public boolean getAnswer() {
        return answer;
    }

    public String getStatement() {
        return statement;
    }

    public Question(boolean answer, String statement) {
        this.answer = answer;
        this.statement = statement;
    }
}
