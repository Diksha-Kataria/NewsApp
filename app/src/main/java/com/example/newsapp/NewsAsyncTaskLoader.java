package com.example.newsapp;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsAsyncTaskLoader extends AsyncTaskLoader<NewsResponse> {
    private NewsResponse response;
    private ArrayList<News> news;
    private Activity context;

    public NewsAsyncTaskLoader(Activity context, ArrayList<News> news) {
        super(context);
        this.news = news;
        response = new NewsResponse();
        this.context = context;
    }


    @Nullable
    @Override
    public NewsResponse loadInBackground() {
        URL url = createUrl();
        String jsonResponse = "";
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
            if(isConnected) {
                try {
                    jsonResponse = makeHttpRequest(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(response.getError().isEmpty()) {
                    response = extractFeatureFromJson(jsonResponse);
                }
            } else {
                response.setError(context.getString(R.string.internet_error_text));
            }
        return response;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private URL createUrl() {
        URL newsUrl = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Constants.SCHEME)
                .authority(Constants.AUTHORITY)
                .appendPath(Constants.PATH)
                .appendQueryParameter(Constants.QUERY_PARAMETER_1, Constants.QUERY_PARAMETER_VALUE)
                .appendQueryParameter(Constants.QUERY_PARAMETER_3, Constants.CONTRIBUTER)
                .appendQueryParameter(Constants.QUERY_PARAMETER_2, Constants.API_KEY);
        String myUrl = builder.build().toString();
        try {
            newsUrl = new URL(myUrl);
        } catch (MalformedURLException exception) {
            return null;
        }
        return newsUrl;
    }

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        InputStream errorStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            errorStream = urlConnection.getErrorStream();
            if(errorStream == null) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                jsonResponse = readErrorFromStream(errorStream);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private String readErrorFromStream(InputStream errorStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (errorStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(errorStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        try {
            JSONObject baseJsonResponse = new JSONObject(output.toString()).getJSONObject(Constants.RESPONSE);
            String errorMessage = baseJsonResponse.getString(Constants.MESSAGE);
            response.setError(errorMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    private NewsResponse extractFeatureFromJson(String responseJSON) {
        NewsResponse newsResponse = new NewsResponse();
        ArrayList<News> news = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(responseJSON).getJSONObject(Constants.RESPONSE);
            JSONArray newsResults = baseJsonResponse.getJSONArray(Constants.RESULT);

            for (int i = 0; i < newsResults.length(); i++) {
                JSONObject article = newsResults.getJSONObject(i);
                String title = article.getString(Constants.TITLE);
                String webUrl = article.getString(Constants.URL);
                String sectionName = article.getString(Constants.SECTION_NAME);
                String date = article.getString(Constants.DATE);
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                    Date articleDate;
                    articleDate = inputFormat.parse(date);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd yyyy");
                    date = outputFormat.format(articleDate);
                }catch(Exception e){
                    e.printStackTrace();
                }

                String type = article.getString(Constants.TYPE);
                JSONArray tagsArray = article.getJSONArray(Constants.TAG);
                String authorName = "";
                if(tagsArray.length()>0) {
                    JSONObject author = tagsArray.getJSONObject(0);
                    authorName = author.getString(Constants.TITLE);
                }

                news.add(new News(title, date, webUrl, sectionName, type, authorName));
            }
            newsResponse.setNews(news);
            return newsResponse;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deliverResult(NewsResponse data) {
        super.deliverResult(data);
    }
}
