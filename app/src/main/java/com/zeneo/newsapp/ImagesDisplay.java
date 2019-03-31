package com.zeneo.newsapp;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.zeneo.newsapp.UI.ImageSliderAdapter;
import com.zeneo.newsapp.util.HttpHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ImagesDisplay extends AppCompatActivity {

    ViewPager viewPager;
    String url;
    String type;
    List<String> list;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_display);

        viewPager = findViewById(R.id.viewpager);

        url = getIntent().getStringExtra("url");
        list = new ArrayList<>();
        type = getIntent().getStringExtra("type");

        if(type.equals("backdrops")) {

        }else {
        }

        new AsyncTask<Void, Void, Void>() {
            HttpHandler sh = new HttpHandler();
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... voids) {
                String jsonStr = sh.makeServiceCall(url);
                try {
                    if(type.equals("backdrop")){
                        JSONObject ob = new JSONObject(jsonStr);
                        JSONArray bd=ob.getJSONArray("backdrops");
                        for (int i=0;i<bd.length();i++){
                            list.add("https://image.tmdb.org/t/p/original"+bd.getJSONObject(i).getString("file_path"));
                        }
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }else if (type.equals("profile")){
                        JSONObject ob = new JSONObject(jsonStr);
                        JSONArray bd=ob.getJSONArray("profiles");
                        for (int i=0;i<bd.length();i++){
                            list.add("https://image.tmdb.org/t/p/original"+bd.getJSONObject(i).getString("file_path"));
                        }
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    } else if (type.equals("poster")){
                        JSONObject ob = new JSONObject(jsonStr);
                        JSONArray bd=ob.getJSONArray("posters");
                        for (int i=0;i<bd.length();i++){
                            list.add("https://image.tmdb.org/t/p/original"+bd.getJSONObject(i).getString("file_path"));
                        }
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else if (type.equals("still")){
                        JSONObject ob = new JSONObject(jsonStr);
                        JSONArray bd=ob.getJSONArray("stills");
                        for (int i=0;i<bd.length();i++){
                            list.add("https://image.tmdb.org/t/p/original"+bd.getJSONObject(i).getString("file_path"));
                        }
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }


                }catch (JSONException e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                viewPager.setAdapter(new ImageSliderAdapter(list,getApplicationContext()));
            }
        }.execute();

    }
}
