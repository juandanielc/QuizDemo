package com.juandanielc.quizjdan.data;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private List<Question> questions = new ArrayList<>();
    private static Quiz quiz;
    private String title;
    private int id;
    private String difficulty;
    private String description;


    public String getDifficulty() {
        return difficulty;
    }

    public String getDescription() {
        return description;
    }
    
    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    private void addQuestion(Question question) {
        questions.add(question);
    }

    public static Quiz getInstance() {
        if(quiz == null) {
            quiz = new Quiz();
            quiz.id = 1;
            quiz.title = "singleton";
            quiz.difficulty = "easy";
            quiz.description = "Default quiz";
            quiz.addQuestion(new Question(false, "Android uses json for layouts"));
            quiz.addQuestion(new Question(true, "Ottawa is in Ontario"));
        }
        return quiz;
    }
}
