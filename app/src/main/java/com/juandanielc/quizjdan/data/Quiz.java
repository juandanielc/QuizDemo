package com.juandanielc.quizjdan.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JDan on 18/10/2017.
 */

public class Quiz {
    private List<Question> questions = new ArrayList<Question>();
    private static Quiz quiz;
    private String title;
    private int id;

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public static Quiz getInstance() {
        if(quiz == null) {
            quiz = new Quiz();
            quiz.id = 1;
            quiz.title = "singleton";
            quiz.addQuestion(new Question(false, "Android uses json for layouts"));
            quiz.addQuestion(new Question(true, "Ottawa is in Ontario"));
        }
        return quiz;
    }
}
