package com.example.android.p022popularmovies2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.p022popularmovies2.data.MovieContract.FavouriteMovieEntry;

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
 *          SQLite helper for favourite movie database
 */

class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "PopularMovies.db";

    // CREATE query
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FavouriteMovieEntry.TABLE_NAME + " (" +
                    FavouriteMovieEntry._ID + " INTEGER PRIMARY KEY," +
                    FavouriteMovieEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FavouriteMovieEntry.COLUMN_NAME_ORIGINAL_TITLE + " TEXT," +
                    FavouriteMovieEntry.COLUMN_NAME_RELEASE_DATE + " DATE," +
                    FavouriteMovieEntry.COLUMN_NAME_POSTER_PATH + " TEXT," +
                    FavouriteMovieEntry.COLUMN_NAME_POPULARITY + " REAL," +
                    FavouriteMovieEntry.COLUMN_NAME_VOTE_AVERAGE + " REAL," +
                    FavouriteMovieEntry.COLUMN_NAME_OVERVIEW + ")";

    // DROP query
    private static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + FavouriteMovieEntry.TABLE_NAME;

    /**
     * constructor to our helper
     *
     * @param context is the calling context
     */
    MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * method to create SQL entries table
     *
     * @param db is the db handler to SQL database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    /**
     * method to upgrade SQL table, usually after upgrading app itself
     *
     * @param db         is the db handler
     * @param oldVersion is the old database version number
     * @param newVersion is the new database version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }
}
