package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<NewsResponse> {
    ListView listView;
    ArrayList<News> news = new ArrayList<>();
    NewsApdater newsApdater;
    TextView emptyText;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsApdater = new NewsApdater(this, news);
        listView = findViewById(R.id.list_view);
        emptyText = findViewById(R.id.empty_text);
        progressBar = findViewById(R.id.progress_bar);
        listView.setAdapter(newsApdater);
        getSupportLoaderManager().initLoader(0,null,this);

    }

    @NonNull
    @Override
    public Loader<NewsResponse> onCreateLoader(int id, @Nullable Bundle args) {
        NewsAsyncTaskLoader newsAsyncTaskLoader = new NewsAsyncTaskLoader(this, news );
        newsAsyncTaskLoader.forceLoad();
        return newsAsyncTaskLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<NewsResponse> loader, NewsResponse data) {
        if(data.getError().isEmpty()) {
            if(data.getNews().size()>0) {
                news.clear();
                news.addAll(data.getNews());
                newsApdater.notifyDataSetChanged();
            } else {
                emptyText.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this,data.getError(),Toast.LENGTH_LONG).show();
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<NewsResponse> loader) {

    }
}