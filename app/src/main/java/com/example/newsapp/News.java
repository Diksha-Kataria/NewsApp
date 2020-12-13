package com.example.newsapp;

public class News {

    private String mNewsTitle;
    private String mDate;
    private String mWebUrl;
    private String mSectionName;
    private String mType;
    private String mAuthorTitle;

    public News(String newsTitle, String date, String webUrl, String sectionName, String type, String authorTitle) {
        mNewsTitle = newsTitle;
        mDate = date;
        mWebUrl = webUrl;
        mSectionName = sectionName;
        mType = type;
        mAuthorTitle = authorTitle;
    }

    public String getNewsTitle() {
        return mNewsTitle;
    }

    public String getDate() {
        return mDate;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getType() {
        return mType;
    }

    public String getAuthorTitle () { return  mAuthorTitle; }
}
