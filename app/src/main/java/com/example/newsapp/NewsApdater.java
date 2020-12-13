package com.example.newsapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NewsApdater extends ArrayAdapter<News> {
    Activity currentContext;
    ArrayList<News> news;

    public NewsApdater(Activity context, ArrayList<News> news) {
        super(context,0,news);
        this.news = news;
        currentContext = context;
    }

    @Nullable
    @Override
    public News getItem(int position) {
        return news.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_news_list, parent,false);
        }

        final News currentNews = getItem(position);

        TextView authorNameTextView = convertView.findViewById(R.id.author_name);
        assert currentNews != null;
        authorNameTextView.setText(currentNews.getAuthorTitle());

        TextView newsTitleTextView = convertView.findViewById(R.id.news_title);
        assert currentNews != null;
        newsTitleTextView.setText(currentNews.getNewsTitle());

        TextView newsTextView = convertView.findViewById(R.id.news_text);
        newsTextView.setText(currentNews.getSectionName());

        TextView textView = convertView.findViewById(R.id.tv_text);
        textView.setText(currentNews.getDate());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(currentNews.getWebUrl()));
               currentContext.startActivity(i);
            }
        });

        return convertView;
    }
}

