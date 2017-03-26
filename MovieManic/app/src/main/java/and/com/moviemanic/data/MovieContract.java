package and.com.moviemanic.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dell on 11-03-2017.
 */

public class MovieContract {
    public static final String AUTHORITY = "and.com.android.moviemanic";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_TASK = "movie";

    public static final class MovieEntry implements BaseColumns{
        public static final Uri CONTENT_URI=
                BASE_URI.buildUpon().appendPath(PATH_TASK).build();

        public static final String _ID = BaseColumns._ID;
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_DATE = "date";
    }
}
