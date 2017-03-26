package and.com.moviemanic.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by dell on 11-03-2017.
 */

public class MovieProvider extends ContentProvider {

    public static final int TASK = 100;

    private MovieDBHelper movieDBHelper;

    @Override
    public boolean onCreate() {
        Context context= getContext();
        movieDBHelper = new MovieDBHelper(context);
        return true;
    }
    private static final UriMatcher sURI_MATCHER = buildUri();

    public static UriMatcher buildUri(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.MovieEntry.TABLE_NAME,TASK);
        return uriMatcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = movieDBHelper.getReadableDatabase();
        int match = sURI_MATCHER.match(uri);
        Cursor retCursor;
        switch(match){
            case TASK:
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Error 2 = "+uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
       return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase sqLiteDatabase = movieDBHelper.getWritableDatabase();
        int match = sURI_MATCHER.match(uri);
        Uri returnUri;
        switch(match){
            case TASK:
                long id = sqLiteDatabase.insert(MovieContract.MovieEntry.TABLE_NAME,null,values);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI,id);
                }else{
                    throw new android .database.SQLException("ERROR = "+uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Error 2 = "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = movieDBHelper.getWritableDatabase();
        int match = sURI_MATCHER.match(uri);
        int n;
        switch(match) {
            case TASK:
                n = sqLiteDatabase.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + selection,null);
                break;
            default:
                throw new UnsupportedOperationException("Error 2 = " + uri);
        }
        return n;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}