package com.juandanielc.quizjdan;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.juandanielc.quizjdan.data.Quiz;
import com.juandanielc.quizjdan.data.QuizRepository;

import java.io.IOException;
import java.util.List;

public class ListActivity extends AppCompatActivity implements QuizRepository.QuizzesCallback {

    private static final String LOG_TAG = QuizRepository.class.getSimpleName();
    private ListView listView;
    private ArrayAdapter adapter;
    private SwipeRefreshLayout swipeView;
    private String refreshText = "";
    private QuizRepository quizRepository;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = (ListView) findViewById(R.id.list_view);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        quizRepository = new QuizRepository(this);

        quizRepository.getRemoteQuizzes(this);

        adapter = new ArrayAdapter(this, R.layout.quiz_list_item);

        listView.setAdapter(adapter);

        mLoadingIndicator.setVisibility(View.VISIBLE);

        swipeView = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        swipeView.setEnabled(false);

        final ListActivity listActivity = this;

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                try {
                    quizRepository.clearCache();
                    quizRepository.getRemoteQuizzes(listActivity);
                    refreshText = "Quiz list refreshed";
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Clearing cache.",e);
                }
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipeView.setEnabled(true);
                else
                    swipeView.setEnabled(false);
            }
        });
    }

    @Override
    public void onFailure() {
        Toast.makeText(this, "Could not retrieve quiz list", Toast.LENGTH_LONG).show();
        swipeView.setRefreshing(false);
    }

    @Override
    public void onSuccess(final List<Quiz> quizzes) {
        final QuizAdapter adapter = new QuizAdapter(this, quizzes);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Quiz quiz = adapter.getItem(position);
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.QUIZ_ID, quiz.getId());
                startActivity(intent);
            }
        });
        if(!refreshText.equals(""))
            Toast.makeText(this, refreshText, Toast.LENGTH_LONG).show();
        else
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        swipeView.setRefreshing(false);
    }

    class QuizAdapter extends ArrayAdapter<Quiz>{

        public QuizAdapter(@NonNull Context context, @NonNull List<Quiz> quizzes) {
            super(context, 0, quizzes);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Quiz quiz = getItem(position);

            Context context = parent.getContext();
            int quizListItem = R.layout.quiz_list_item;
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(quizListItem, parent, false);

            TextView tvQuizDescription = view.findViewById(R.id.tv_quiz_description);
            TextView tvQuizDifficulty = view.findViewById(R.id.tv_quiz_difficulty);
            TextView tvQuizTitle = view.findViewById(R.id.tv_quiz_title);

            assert quiz != null;
            tvQuizTitle.setText(quiz.getTitle());
            tvQuizDescription.setText(quiz.getDescription());
            tvQuizDifficulty.setText(quiz.getDifficulty());

            return view;
        }
    }
}
