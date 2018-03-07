package com.example.android.p022popularmovies2.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Udacity Android Developer Nanodegree - Project Popular Movies stage 2
 *
 * @author balazs.lengyak@gmail.com
 * @version 2.0
 *          <p>
 *          - Inspired by dozens of online found examples (both visual and code design),
 *          - Might contain traces of code from official Android Developer documentation and
 *          default templates from Android Studio
 *          <p>
 *
 *          Contract to storing data in SQLite
 */

public class MovieContract {

    static final String PATH_FAVOURITE_MOVIES = "favourite_movies";
    static final String AUTHORITY = "com.example.android.p022popularmovies2";
    // The base content URI = "content://" + <authority>
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    //  prevent accidental instantiation by declaring constructor private
    private MovieContract() {
    }

    //  table contents
    public static class FavouriteMovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVOURITE_MOVIES).build();
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_POPULARITY = "popularity";
        public static final String COLUMN_NAME_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_NAME_POSTER_PATH = "poster_path";
        public static final String COLUMN_NAME_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_RELEASE_DATE = "release_date";
        static final String TABLE_NAME = "movies";
    }
}
