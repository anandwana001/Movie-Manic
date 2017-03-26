package and.com.moviemanic.network;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import and.com.moviemanic.model.Movie;

/**
 * Created by dell on 10-03-2017.
 */

public class MovieAsyncTaskLoader extends AsyncTaskLoader<List<Movie>>{

    private static final String baseurl = "http://image.tmdb.org/t/p/w185/";
    private static String sort;

    public MovieAsyncTaskLoader(Context context, String sort) {
        super(context);
        this.sort = sort;
    }

    private static URL createUrl(){
        URL url = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .encodedAuthority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(sort)
                .appendQueryParameter("api_key", "3ed734db4c2a29df0805fde8290251b6");
        String movieUrl = builder.build().toString();
        try {
            return new URL(movieUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    @Override
    public List<Movie> loadInBackground() {
        List<Movie> movieList= null;
        URL url = createUrl();
        String jsonResponse = null;
        try {
            jsonResponse = makehttpRequest(url);
        }catch (IOException e){}
        movieList = extractMovies(jsonResponse);
        return movieList;
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
    private static List<Movie> extractMovies(String moviesJSON){
        ArrayList<Movie> movieArrayList = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(moviesJSON);
            JSONArray MoviesArray = baseJsonResponse.getJSONArray("results");
            for(int i=0;i<MoviesArray.length();i++){
                JSONObject currentMovie = MoviesArray.getJSONObject(i);
                Movie movie = new Movie();
                movie.setThumbnail(baseurl.concat(currentMovie.getString("poster_path").replace("\\","/")));
                movie.setOverview(currentMovie.getString("overview"));
                movie.setRelease(currentMovie.getString("release_date"));
                movie.setId(currentMovie.getInt("id"));
                movie.setTitle(currentMovie.getString("original_title"));
                movie.setVote(currentMovie.getInt("vote_average"));
                movieArrayList.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieArrayList;
    }
}
