package com.example.newsapp;

import java.util.ArrayList;

public class NewsResponse {
   private ArrayList<News> news;
    private String error = "";

    public ArrayList<News> getNews() {
        return news;
    }

    public void setNews(ArrayList<News> news) {
        this.news = news;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
