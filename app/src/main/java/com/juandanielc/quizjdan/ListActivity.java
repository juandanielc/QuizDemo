package com.juandanielc.quizjdan;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.juandanielc.quizjdan.data.Quiz;
import com.juandanielc.quizjdan.data.QuizRepository;

import java.util.Arrays;
import java.util.List;

public class ListActivity extends AppCompatActivity implements QuizRepository.QuizzesCallback {

    private ListView listView;
    private ArrayAdapter adapter;
    private SwipeRefreshLayout swipeView;
    private String refreshText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = (ListView) findViewById(R.id.list_view);

        new QuizRepository(this).getRemoteQuizzes(this);

        List<String> strings = Arrays.asList("First", "2nd", "troisieme");
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, strings);

        listView.setAdapter(adapter);


        swipeView = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        swipeView.setEnabled(false);

        final ListActivity listActivity = this;

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                new QuizRepository(listActivity).getRemoteQuizzes(listActivity);
                refreshText = "Quiz list refreshed";
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

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_list_item_1,parent,false);
            }
            TextView textView = (TextView) convertView;

            textView.setText(String.format("Quiz %d: %s", quiz.getId() , quiz.getTitle()));
            return textView;
        }
    }
}
