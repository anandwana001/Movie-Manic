package and.com.moviemanic;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import and.com.moviemanic.adapter.FavouriteAdapter;
import and.com.moviemanic.data.MovieContract;
import and.com.moviemanic.model.Movie;
import and.com.moviemanic.touch.RecyclerTouchListener;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.recyclerview)
    RecyclerView favRecyclerview;
    private FavouriteAdapter favouriteAdapter;
    private Cursor cursor_load;
    private Movie favourite;
    private static final int TASK_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        favRecyclerview.setLayoutManager(new GridLayoutManager(this,2));
        favouriteAdapter = new FavouriteAdapter(this);
        favRecyclerview.setAdapter(favouriteAdapter);

        favRecyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), favRecyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                cursor_load.moveToPosition(position);
                favourite = new Movie(cursor_load.getString(cursor_load.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE)),
                            cursor_load.getString(cursor_load.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)),
                            cursor_load.getString(cursor_load.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)),
                            cursor_load.getString(cursor_load.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE)),
                            Double.parseDouble(cursor_load.getString(cursor_load.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING))),
                            Integer.parseInt(cursor_load.getString(cursor_load.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID))));
                Intent movieIntent = new Intent(FavouriteActivity.this, DetailActivity.class);
                movieIntent.putExtra("movie", favourite);
                startActivity(movieIntent);
            }
            @Override
            public void onLongClick(View view, int position) {}
        }));
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    deliverResult(mTaskData);
                } else {
                    forceLoad();
                }
            }
            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MovieContract.MovieEntry.COLUMN_RATING);
                } catch (Exception e) {
                    Log.e(FavouriteActivity.class.getSimpleName(), "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursor_load = data;
        favouriteAdapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favouriteAdapter.swapCursor(null);
    }
}
