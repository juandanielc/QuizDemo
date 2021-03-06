package com.juandanielc.quizjdan.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class QuizRepository {

    private static final String LOG_TAG = QuizRepository.class.getSimpleName();
    private static final List<String> QUIZ_JSON = Arrays.asList("quiz.json", "quiz2.json");
    private static final String BASE_URL = "http://juandanielc.com/";
    private Context context;
    private Cache cache;

    public QuizRepository(Context context) {
        this.context = context;
    }

    public Quiz getQuiz(int index) {
        InputStream assetInputStream;
        try {
            assetInputStream = context.getAssets().open(QUIZ_JSON.get(index));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could open quiz parse json 1 step", e);
            return null;
        }

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Quiz> jsonAdapter = moshi.adapter(Quiz.class);

        try {
            return jsonAdapter.fromJson(Okio.buffer(Okio.source(assetInputStream)));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could open quiz parse json 2 step", e);
            return null;
        }
    }


    public void getRemoteQuizzes(final QuizzesCallback callback) {
        QuizService service = getQuizService();

        service.getQuizzes().enqueue(new retrofit2.Callback<List<Quiz>>() {
            @Override
            public void onResponse(@NonNull Call<List<Quiz>> call, @NonNull Response<List<Quiz>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Quiz>> call, @NonNull Throwable t) {
                callback.onFailure();
            }
        });
    }

    public void getRemoteQuiz(int id, final Callback callback) {
        Log.e(LOG_TAG, "getRemoteQuiz");
        QuizService service = getQuizService();
        Log.e(LOG_TAG, "getRemoteQuiz 2");
        service.getQuiz(id).enqueue(new retrofit2.Callback<Quiz>() {
            @Override
            public void onResponse(@NonNull Call<Quiz> call, @NonNull Response<Quiz> response) {
                Log.e(LOG_TAG, "onResponse");
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Quiz> call, @NonNull Throwable t) {
                Log.e(LOG_TAG, "onFailure");
                callback.onFailure();
            }
        });
        Log.e(LOG_TAG, "getRemoteQuiz 3");
        /*
        try {
            Response<Quiz> response = service.getQuiz().execute();
            if (!response.isSuccessful()) {
                return null;
            }
            Quiz quiz = response.body();
            return quiz;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failure to fetch data", e);
            return null;
        }
        */
    }

    private QuizService getQuizService() {
        int cacheSize = 10 * 1024 * 1024; // 10 MB

        File httpCacheDirecotory = new File(context.getCacheDir(), "http-cache");
        cache = new Cache(httpCacheDirecotory, cacheSize);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        return retrofit.create(QuizService.class);
    }

    public interface Callback {
        void onFailure();

        void onSuccess(Quiz quiz);
    }

    public interface QuizzesCallback {
        void onFailure();

        void onSuccess(List<Quiz> quizzes);
    }

    public void clearCache() throws IOException {
        cache.evictAll();
    }
}
