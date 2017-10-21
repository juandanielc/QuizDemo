package com.juandanielc.quizjdan.data;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by JDan on 19/10/2017.
 */

public interface QuizService {
    // @GET("cdn/quiz.json")
    @GET("cdn/quizzes/{id}.json")
    Call<Quiz> getQuiz(@Path("id") int id);

    @GET("cdn/quizzes.json")
    Call<List<Quiz>> getQuizzes();
}
