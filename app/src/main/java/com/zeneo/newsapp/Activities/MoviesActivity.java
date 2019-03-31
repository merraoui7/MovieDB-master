package com.zeneo.newsapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zeneo.newsapp.Adapters.PopularListAdapter;
import com.zeneo.newsapp.Adapters.VideoListAdapter;
import com.zeneo.newsapp.DATABASE.DataContract;
import com.zeneo.newsapp.DATABASE.DataHelper;
import com.zeneo.newsapp.ImagesDisplay;
import com.zeneo.newsapp.Models.Movies;
import com.zeneo.newsapp.Models.Videos;
import com.zeneo.newsapp.R;
import com.zeneo.newsapp.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.zeneo.newsapp.R.color.colorAccent;


public class MoviesActivity extends AppCompatActivity {

    ImageView bd,pr,pr1,bd1;
    TextView tt,st,rl,olg,rt,bg,gr,rv,desc,rating,backdropstxt,posterstxt;
    RatingBar ratingBar;
    GetDetailsFromApi getDetailsFromApi;
    GetResFromApi getResFromApi,getResFromApi1;
    RecyclerView recList,simList;
    String id;
    String url1,url2,url3,url4,url5,url6;
    SQLiteDatabase db,db2;
    DataHelper.FavoriteHelper sqlfavorite;
    DataHelper.WatchListHelper sqlwatchlist;
    ImageView favor,watchlist;
    String title,imgurl,type;
    List<Movies> castList = new ArrayList<>();
    RecyclerView videosRecycler,castRecycler;
    List<Videos> videosList = new ArrayList<>();
    Toolbar toolbar;
    LinearLayout layout1,layout2,layout3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");



        id = getIntent().getStringExtra("id");
        url1="https://api.themoviedb.org/3/movie/"+id+"?api_key=5d173b53167711178472dc9d98603e31&language=en-US";
        url2="https://api.themoviedb.org/3/movie/"+id+"/recommendations?api_key=5d173b53167711178472dc9d98603e31&language=en-US&page=1";
        url3="https://api.themoviedb.org/3/movie/"+id+"/similar?api_key=5d173b53167711178472dc9d98603e31&language=en-US&page=1";
        url4="https://api.themoviedb.org/3/movie/"+id+"/images?api_key=5d173b53167711178472dc9d98603e31&language=en-US&include_image_language=null";
        url5="https://api.themoviedb.org/3/movie/"+id+"/videos?api_key=5d173b53167711178472dc9d98603e31&language=en-US";
        url6="https://api.themoviedb.org/3/movie/"+id+"/credits?api_key=5d173b53167711178472dc9d98603e31\n";
        linkViews();
        setImagesSize();


        if(new InternetConnection().chechInternet(getApplicationContext())){
            getData();
            layout3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }else {
            final View view1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.no_internet,null);
            TextView tryAgain= view1.findViewById(R.id.tryagainbtn);
            tryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (new InternetConnection().chechInternet(getApplicationContext())){
                        layout3.removeAllViews();
                        layout3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        getData();
                    }else {
                        layout3.removeAllViews();
                        layout3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        layout3.addView(view1);
                    }
                }
            });
            view1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            layout3.removeAllViews();
            layout3.addView(view1);
        }


        sqlfavorite = new DataHelper.FavoriteHelper(this);
        db = sqlfavorite.getWritableDatabase();
        sqlwatchlist = new DataHelper.WatchListHelper(this);
        db2 = sqlwatchlist.getWritableDatabase();

        toolbar.setNavigationIcon(R.drawable.ic_back);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MoviesActivity.this, ImagesDisplay.class);
                i.putExtra("url",url4);
                i.putExtra("type","poster");
                startActivity(i);
            }
        });
        bd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MoviesActivity.this, ImagesDisplay.class);
                i.putExtra("url",url4);
                i.putExtra("type","backdrop");
                startActivity(i);
            }
        });

    }




    @Override
    protected void onStart() {
        super.onStart();
        boolean isExist = sqlfavorite.CheckIsDataAlreadyInDBorNot(DataContract.FavoriteEntry.TABLE_NAME,id);
        if (isExist){
            favor.setImageResource(R.drawable.favorite);
        }else {
            favor.setImageResource(R.drawable.favorite_desactive);
        }

        boolean count3 = sqlwatchlist.CheckIsDataAlreadyInDBorNot(DataContract.WatchListEntry.TABLE_NAME,id);
        if (count3){
            watchlist.setImageResource(R.drawable.ic_watchlist_added);
        }else {
            watchlist.setImageResource(R.drawable.ic_watchlist_add);
        }

        favor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isExist = sqlfavorite.CheckIsDataAlreadyInDBorNot(DataContract.FavoriteEntry.TABLE_NAME,id);
                title = tt.getText().toString();
                imgurl = getDetailsFromApi.getImageurl();
                if (isExist){
                    sqlfavorite.delete(DataContract.FavoriteEntry.TABLE_NAME,id,"movie");
                    favor.setImageResource(R.drawable.favorite_desactive);
                }else {
                    sqlfavorite.insert(DataContract.FavoriteEntry.TABLE_NAME,title,imgurl,id,"movie");
                    favor.setImageResource(R.drawable.favorite);
                }
            }
        });
        watchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean count3 = sqlwatchlist.CheckIsDataAlreadyInDBorNot(DataContract.WatchListEntry.TABLE_NAME,id);

                title = tt.getText().toString();
                imgurl = getDetailsFromApi.getImageurl();
                if (count3){
                   sqlwatchlist.delete(DataContract.WatchListEntry.TABLE_NAME,id,"movie");
                    watchlist.setImageResource(R.drawable.ic_watchlist_add);
                }else {
                    sqlwatchlist.insert(DataContract.WatchListEntry.TABLE_NAME,title,imgurl,id,"movie");
                    watchlist.setImageResource(R.drawable.ic_watchlist_added);
                }
            }
        });



    }

    public void linkViews(){
        bd = (ImageView)findViewById(R.id.mov_backdrop);
        pr = (ImageView)findViewById(R.id.mov_poster);
        pr1 = (ImageView)findViewById(R.id.pr1);
        bd1 = (ImageView)findViewById(R.id.bd1);
        tt = (TextView)findViewById(R.id.title_mov);
        st = (TextView)findViewById(R.id.mov_status);
        rl = (TextView)findViewById(R.id.mov_release);
        olg = (TextView)findViewById(R.id.mov_olg);
        rt = (TextView)findViewById(R.id.mov_runtime);
        bg = (TextView)findViewById(R.id.mov_budget);
        desc = (TextView)findViewById(R.id.mov_desc);
        gr = (TextView)findViewById(R.id.mov_genre);
        rv = (TextView)findViewById(R.id.mov_revenue);
        recList = (RecyclerView)findViewById(R.id.mov_rec_list);
        simList = (RecyclerView)findViewById(R.id.mov_sim_list);
        favor = (ImageView)findViewById(R.id.mov_favor);
        watchlist = (ImageView)findViewById(R.id.watch_list);
        rating = (TextView)findViewById(R.id.rattxt);
        ratingBar = (RatingBar)findViewById(R.id.ratingbar);
        videosRecycler = findViewById(R.id.video_list);
        backdropstxt = findViewById(R.id.backdropstxt);
        posterstxt = findViewById(R.id.posterstxt);
        castRecycler = findViewById(R.id.cast_list);
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
    }

    public void setImagesSize(){
        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int width = size.x;
        bd.setMaxHeight((int) (width*0.7));

    }

    public void getData(){
        getDetailsFromApi = new GetDetailsFromApi(bd,pr,tt,st,rl,olg,rt,bg,desc,gr,rv);
        getDetailsFromApi.setContext(MoviesActivity.this);
        new getDetails().execute();
        getResFromApi = new GetResFromApi(recList,MoviesActivity.this,url2,"movierec","hori",false);
        getResFromApi.getDetails();
        getResFromApi1 = new GetResFromApi(simList,MoviesActivity.this,url3,"movierec","hori",false);
        getResFromApi1.getDetails();
        //GetResFromApi getResFromApi3 = new GetResFromApi(getApplicationContext(),vidViewPager,url5,getWindowManager());
        //getResFromApi3.getVideosForSliders();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        db2.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main2,menu);
        menu.findItem(R.id.action_share).setIcon(R.drawable.ic_share_white);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.themoviedb.org/movie/"+id);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class getDetails extends AsyncTask<String ,String ,String > {
        HttpHandler sh = new HttpHandler();
        String tt=null,st=null,rl=null,olg=null,rt=null,bg=null,gr = "",rv=null,bd=null,pr=null,desc=null;
        float rating;
        String backdrops,posters;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Glide.with(getApplicationContext()).load(bd)
                        .into(MoviesActivity.this.bd);
                Glide.with(getApplicationContext()).load(imgurl).apply(RequestOptions.centerCropTransform().error(com.zeneo.newsapp.R.drawable.bg_null))
                        .into(MoviesActivity.this.pr);
                Glide.with(getApplicationContext())
                        .load(bd).apply(RequestOptions.centerCropTransform().override(1200,800))
                        .into(MoviesActivity.this.bd1);
                Glide.with(getApplicationContext()).load(imgurl)
                        .into(MoviesActivity.this.pr1);
            }catch (IllegalArgumentException e){

            }
            MoviesActivity.this.tt.setText(tt);
            MoviesActivity.this.st.setText(st);
            MoviesActivity.this.rl.setText(rl);
            MoviesActivity.this.olg.setText(olg);
            MoviesActivity.this.olg.setAllCaps(true);
            MoviesActivity.this.rt.setText(rt);
            MoviesActivity.this.bg.setText(bg);
            MoviesActivity.this.gr.setText(gr);
            MoviesActivity.this.rv.setText(rv);
            MoviesActivity.this.desc.setText(desc);
            ratingBar.setRating(rating/2);
            MoviesActivity.this.rating.setText(String.valueOf(rating));
            posterstxt.setText(posters);
            backdropstxt.setText(backdrops);

            VideoListAdapter adapter = new VideoListAdapter(videosList,MoviesActivity.this);
            videosRecycler.setLayoutManager(new LinearLayoutManager(MoviesActivity.this,LinearLayout.HORIZONTAL,false));
            videosRecycler.setAdapter(adapter);

            PopularListAdapter adapter1 = new PopularListAdapter(castList,MoviesActivity.this);
            castRecycler.setLayoutManager(new LinearLayoutManager(MoviesActivity.this,LinearLayout.HORIZONTAL,false));
            castRecycler.setAdapter(adapter1);
            layout1.setVisibility(View.GONE);
            layout2.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layout1.setVisibility(View.VISIBLE);
            layout2.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonStr = sh.makeServiceCall(url1);
            String jsonStr1 = sh.makeServiceCall(url5);
            String jsonStr2 = sh.makeServiceCall(url4);
            String jsonStr3 = sh.makeServiceCall(url6);

            try {
                JSONObject ob = new JSONObject(jsonStr);
                JSONObject ob1 = new JSONObject(jsonStr1);
                JSONObject ob2 = new JSONObject(jsonStr2);
                JSONObject ob3 = new JSONObject(jsonStr3);
                bd = "https://image.tmdb.org/t/p/original"+ob.getString("backdrop_path");
                imgurl = "https://image.tmdb.org/t/p/w500"+ob.getString("poster_path");
                rating =(float)ob.getDouble("vote_average");
                tt = ob.getString("title");
                st = ob.getString("status");
                rl = ob.getString("release_date");
                olg = ob.getJSONArray("spoken_languages").getJSONObject(0).getString("name");
                rt = new NumToTime().converter(ob.getInt("runtime"));
                bg = String.valueOf(ob.getInt("budget"))+"$";
                for (int i = 0 ; i<ob.getJSONArray("genres").length();i++){
                    JSONArray grarr = ob.getJSONArray("genres");
                    if (i==ob.getJSONArray("genres").length()-1){
                        gr += grarr.getJSONObject(i).getString("name");
                    }
                    else {
                        gr += grarr.getJSONObject(i).getString("name")+" • ";
                    }
                }
                rv = String.valueOf(ob.getInt("revenue"))+"$";
                Log.i("TAG",bd);
                desc = ob.getString("overview");

                JSONArray results1 = ob1.getJSONArray("results");

                for (int i = 0 ; i < results1.length() ; i++ ){
                    String img_url = "https://img.youtube.com/vi/"+results1.getJSONObject(i).getString("key")+"/hqdefault.jpg";
                    String title = results1.getJSONObject(i).getString("name");
                    String vid_url = "https://www.youtube.com/watch?v="+results1.getJSONObject(i).getString("key");
                    videosList.add(new Videos(results1.getJSONObject(i).getString("key"),img_url,title,vid_url));
                }

                backdrops = String.valueOf(ob2.getJSONArray("backdrops").length()) + " Backdrop";
                posters = String.valueOf(ob2.getJSONArray("posters").length()) + " Poster";

                JSONArray results3 = ob3.getJSONArray("cast");
                for (int i = 0 ; i < results3.length() ; i++){
                    String titles = results3.getJSONObject(i).getString("name");
                    String imgurl ="https://image.tmdb.org/t/p/w500"+ results3.getJSONObject(i).getString("profile_path");
                    int id = results3.getJSONObject(i).getInt("id");
                    castList.add(new Movies(titles,imgurl,id,"castp"));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return imgurl;
        }
    }

}
