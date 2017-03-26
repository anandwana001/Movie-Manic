package and.com.moviemanic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import and.com.moviemanic.adapter.MovieAdapter;
import and.com.moviemanic.model.Movie;
import and.com.moviemanic.network.MovieAsyncTaskLoader;
import and.com.moviemanic.touch.RecyclerTouchListener;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>{

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    private List<Movie> movieArrayList;
    private MovieAdapter mAdapter;
    private String sort;
    private ConnectivityManager cm;
    private NetworkInfo netInfo;
    private Parcelable mListState;
    private GridLayoutManager mLayoutManager;
    private String LIST_STATE_KEY = "list_state";
    private static final int LOADER_ID = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        movieArrayList = new ArrayList<>();
        mAdapter = new MovieAdapter(movieArrayList, MainActivity.this);
        mLayoutManager = new GridLayoutManager(MainActivity.this,2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            getSupportLoaderManager().initLoader(LOADER_ID,null,this);
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_net), Toast.LENGTH_SHORT).show();
        }
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Movie movie = movieArrayList.get(position);
                Intent movieIntent = new Intent(MainActivity.this, DetailActivity.class);
                movieIntent.putExtra("movie", movie);
                startActivity(movieIntent);
            }
            @Override
            public void onLongClick(View view, int position) {}
        }));
        movieArrayList.clear();
        getSupportLoaderManager().restartLoader(LOADER_ID,null,this).forceLoad();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, mListState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int id = item.getItemId();
        if (id == R.id.popular) {
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                if(sort == "popular"){
                    Toast.makeText(this, "Popular Movies Showing", Toast.LENGTH_SHORT).show();
                }else {
                    movieArrayList.clear();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("sort", "popular");
                    editor.commit();
                    getSupportLoaderManager().restartLoader(LOADER_ID, null, this).forceLoad();
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_net), Toast.LENGTH_SHORT).show();
            }
        }else if (id == R.id.top_rated) {
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                if(sort == "top_rated"){
                    Toast.makeText(this, "Top Rated Movies Showing", Toast.LENGTH_SHORT).show();
                }else{
                    movieArrayList.clear();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("sort", "top_rated");
                    editor.commit();
                    getSupportLoaderManager().restartLoader(LOADER_ID,null,this).forceLoad();
                }
            }else {
                Toast.makeText(this, getResources().getString(R.string.no_net), Toast.LENGTH_SHORT).show();
            }
        }else if(id == R.id.fav_list){
            Intent intent = new Intent(this,FavouriteActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id,Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String mSort = sharedPreferences.getString("sort", "popular");
        sort = mSort;
        return new MovieAsyncTaskLoader(this,mSort);
    }
    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        movieArrayList = data;
        mAdapter = new MovieAdapter(data,this);
        recyclerView.setAdapter(mAdapter);
        mLayoutManager.onRestoreInstanceState(mListState);
    }
    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
    }
}
