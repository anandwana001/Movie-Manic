package and.com.moviemanic;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import and.com.moviemanic.adapter.ReviewAdapter;
import and.com.moviemanic.adapter.TrailerAdapter;
import and.com.moviemanic.data.MovieContract;
import and.com.moviemanic.model.Movie;
import and.com.moviemanic.model.Trailer;
import and.com.moviemanic.touch.RecyclerTouchListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity{

    @BindView(R.id.title_det)
    TextView Title;
    @BindView(R.id.thumbnail_det)
    ImageView Thumbnail;
    @BindView(R.id.overview_det)
    TextView Overview;
    @BindView(R.id.release_det)
    TextView Release;
    @BindView(R.id.vote_det)
    TextView Vote;
    @BindView(R.id.trailer_recyclerview)
    RecyclerView trailerRecyclerview;
    @BindView(R.id.revuew_recyclerview)
    RecyclerView reviewRecyclerview;
    @BindView(R.id.fav_btn)
    Button favBtn;

    private List<Trailer> trailerList;
    private List<Trailer> reviewList;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private int id;
    private static final String reviews = "reviews";
    private static final String videos = "videos";
    private Movie movie;

    private String thumbnail;
    private String title;
    private String overview;
    private String release;
    private double vote;
    private ProgressDialog progressDialog;
    private ConnectivityManager cm;
    private NetworkInfo netInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        if(savedInstanceState != null){
            movie = savedInstanceState.getParcelable("movie_detail");
        }

        movie = getIntent().getExtras().getParcelable("movie");
        trailerList = new ArrayList<>();
        reviewList = new ArrayList<>();
        trailerAdapter = new TrailerAdapter(trailerList,this);
        reviewAdapter = new ReviewAdapter(reviewList);
        trailerRecyclerview.setLayoutManager(new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
        reviewRecyclerview.setLayoutManager(new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
        trailerRecyclerview.setAdapter(trailerAdapter);
        reviewRecyclerview.setAdapter(reviewAdapter);

        id=movie.getId();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            new fetchAsyncTaskTrailer().execute();
            new fetchAsyncTaskReview().execute();
        }else {
            Toast.makeText(this, getResources().getString(R.string.no_net), Toast.LENGTH_SHORT).show();
        }

        SnapHelper snapHelperStart = new GravitySnapHelper(Gravity.START);
        snapHelperStart.attachToRecyclerView(trailerRecyclerview);
        snapHelperStart.attachToRecyclerView(reviewRecyclerview);

        thumbnail = movie.getThumbnail();
        Picasso.with(DetailActivity.this).load(thumbnail).into(Thumbnail);
        title= movie.getTitle();
        Title.setText("" + title);
        overview = movie.getOverview();
        Overview.setText("" + overview);
        release = movie.getRelease();
        Release.setText("Release Date: " + release);
        vote = movie.getVote();
        Vote.setText("Ratings: " + vote);
        if(chkfav(id))
            favBtn.setText("Liked");
        else
            favBtn.setText("Like");

        trailerRecyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), trailerRecyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Trailer trailer = trailerList.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }else
                    Toast.makeText(DetailActivity.this,"No Application to perform this action",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLongClick(View view, int position) {}
        }));
    }

    private class fetchAsyncTaskTrailer extends AsyncTask<String,Void,List<Trailer>>{
        @Override
        protected List<Trailer> doInBackground(String... params) {
            List<Trailer> trailerList = fetchTrailer(id);
            return trailerList;
        }
        @Override
        protected void onPostExecute(List<Trailer> trailers) {
            super.onPostExecute(trailers);
            trailerList = trailers;
            trailerAdapter = new TrailerAdapter(trailers,DetailActivity.this);
            trailerRecyclerview.setAdapter(trailerAdapter);
        }
    }
    private class fetchAsyncTaskReview extends AsyncTask<String,Void,List<Trailer>>{
        @Override
        protected List<Trailer> doInBackground(String... params) {
            List<Trailer> reviewList = fetchReview(id);
            return reviewList;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(DetailActivity.this, "Loading", "Please wait a moment!");
        }
        @Override
        protected void onPostExecute(List<Trailer> reviews) {
            super.onPostExecute(reviews);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            reviewList = reviews;
            reviewAdapter = new ReviewAdapter(reviews);
            reviewRecyclerview.setAdapter(reviewAdapter);
        }
    }

    private static List<Trailer> fetchTrailer(int id) {
        String jsonResponse= createUrl(id,videos);
        List<Trailer> trailerList = extractTrailer(jsonResponse);
        return trailerList;
    }
    private static List<Trailer> fetchReview(int id) {
        String jsonResponse= createUrl(id,reviews);
        List<Trailer> reviewList = extractReview(jsonResponse);
        return reviewList;
    }

    private static String createUrl(int id,String sort){
        String jsonResponse=null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .encodedAuthority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(""+id)
                .appendPath(sort)
                .appendQueryParameter("api_key", "3ed734db4c2a29df0805fde8290251b6");
        String movieUrl = builder.build().toString();
        try {
            jsonResponse = makehttpRequest(new URL(movieUrl));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }
    private static String makehttpRequest(URL url) throws IOException{
        String jsonResponse = "";
        if(url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
            }
        }catch (IOException e){
        }finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Trailer> extractTrailer(String jsonResponse) {
        ArrayList<Trailer> movieTrailerArrayList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray TrailersArray = baseJsonResponse.getJSONArray("results");
            for(int i=0;i<TrailersArray.length();i++){
                JSONObject currentTrailer = TrailersArray.getJSONObject(i);
                Trailer trailer = new Trailer();
                trailer.setKey(currentTrailer.getString("key"));
                trailer.setName(currentTrailer.getString("name"));
                movieTrailerArrayList.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieTrailerArrayList;
    }
    private static List<Trailer> extractReview(String jsonResponse) {
        ArrayList<Trailer> movieReviewArrayList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray ReviewsArray = baseJsonResponse.getJSONArray("results");
            for(int i=0;i<ReviewsArray.length();i++){
                JSONObject currentReview = ReviewsArray.getJSONObject(i);
                Trailer review = new Trailer();
                review.setContent(currentReview.getString("content"));
                movieReviewArrayList.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieReviewArrayList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.share){
            Trailer trailer = trailerList.get(0);
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + trailer.getKey());
            startActivity(Intent.createChooser(share, "Share link!"));
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fav_btn)
    void likeMovie(){
        if(chkfav(id)){
            removeFav(id);
            favBtn.setText("Like");
            Toast.makeText(this,"Remove From Favourite List",Toast.LENGTH_SHORT).show();
        }else {
            ContentValues testValues = new ContentValues();
            testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
            testValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            testValues.put(MovieContract.MovieEntry.COLUMN_IMAGE, thumbnail);
            testValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            testValues.put(MovieContract.MovieEntry.COLUMN_RATING, vote);
            testValues.put(MovieContract.MovieEntry.COLUMN_DATE, release);
            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,testValues);
            if(uri!=null){
                favBtn.setText("Liked");
                Toast.makeText(this,"Added to Favourite List",Toast.LENGTH_SHORT).show();
            }
         }
    }
    private void removeFav(long id){
        int number = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, String.valueOf(id),null);
    }
    private boolean chkfav(int id){
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + id, null, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
