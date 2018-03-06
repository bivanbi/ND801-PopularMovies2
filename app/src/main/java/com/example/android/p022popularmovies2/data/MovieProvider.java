package com.example.android.p022popularmovies2.data;

import static com.example.android.p022popularmovies2.data.MovieContract.FavouriteMovieEntry
        .TABLE_NAME;

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
import android.util.Log;

import java.util.Locale;


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
 *          provider to our favourite movie database
 */

public class MovieProvider extends ContentProvider {
    public static final int FAVOURITE_MOVIES = 100;
    public static final int FAVOURITE_MOVIE_WITH_ID = 101;
    private static final String TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //  URI definitions
    static {
        sUriMatcher.addURI(
                MovieContract.AUTHORITY,
                MovieContract.PATH_FAVOURITE_MOVIES,
                FAVOURITE_MOVIES);
        sUriMatcher.addURI(
                MovieContract.AUTHORITY,
                MovieContract.PATH_FAVOURITE_MOVIES + "/#",
                FAVOURITE_MOVIE_WITH_ID);
    }

    private SQLiteDatabase mDb;
    private MovieDbHelper mMovieDbHelper;

    /**
     * method to get a cursor to data or dataset
     *
     * @param uri           is the uri pointing to the required data
     * @param projection    is currently unused
     * @param selection     parametrized WHERE (filter) clause
     * @param selectionArgs arguments to selection parameters
     * @param sortOrder     sort by definition
     * @return cursor pointing to result set, null on failure
     */
    @Nullable
    public Cursor query(
            @NonNull Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

        Context context = getContext();
        if (null == context) {
            Log.e(TAG, "query: getContext() returned null");
            return null;
        }

        switch (sUriMatcher.match(uri)) {
            case FAVOURITE_MOVIES:
                mDb = mMovieDbHelper.getReadableDatabase();
                return mDb.query(MovieContract.FavouriteMovieEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        sortOrder
                );

            case FAVOURITE_MOVIE_WITH_ID:

                Locale locale = context.getResources().getConfiguration().locale;
                String mySelection = MovieContract.FavouriteMovieEntry._ID + "=?";
                String[] mySelectionArgs = {String.format(locale, "%d", ContentUris.parseId(uri))};

                mDb = mMovieDbHelper.getReadableDatabase();
                return mDb.query(MovieContract.FavouriteMovieEntry.TABLE_NAME,
                        null,
                        mySelection,
                        mySelectionArgs,
                        null,
                        null,
                        null
                );

            default:
                throw new UnsupportedOperationException("query received unknown URI: " + uri);
        }
    }

    /**
     * insert method
     *
     * @param uri    is the uri pointing to the required data destination
     * @param values array of values to be inserted
     * @return Uri pointing to the newly created record or null on failure
     */
    @Nullable
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Uri returnUri;

        Context context = getContext();
        if (null == context) {
            Log.e(TAG, "query: getContext() returned null");
            return null;
        }

        switch (sUriMatcher.match(uri)) {
            case FAVOURITE_MOVIES:
                // Inserting values into tasks table
                mDb = mMovieDbHelper.getWritableDatabase();
                long id = mDb.insert(TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(
                            MovieContract.FavouriteMovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("query received unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    /**
     * oncreate method to our provider
     *
     * @return true on success
     */
    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }

    /**
     * delete method
     *
     * @param uri           is the uri pointing to the required data
     * @param selection     parametrized WHERE (filter) clause
     * @param selectionArgs are the arguments for the above parameters
     * @return number of rows affected
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
            @Nullable String[] selectionArgs) {

        Context context = getContext();
        if (null == context) {
            Log.e(TAG, "query: getContext() returned null");
            return 0;
        }

        switch (sUriMatcher.match(uri)) {
            case FAVOURITE_MOVIE_WITH_ID:
                Locale locale = context.getResources().getConfiguration().locale;
                String mySelection = MovieContract.FavouriteMovieEntry._ID + "=?";
                String[] mySelectionArgs = {String.format(locale, "%d", ContentUris.parseId(uri))};
                mDb = mMovieDbHelper.getWritableDatabase();
                return mDb.delete(MovieContract.FavouriteMovieEntry.TABLE_NAME,
                        mySelection,
                        mySelectionArgs);

            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    /**
     * update method, currently unused, required for implementation
     *
     * @param uri           is the uri pointing to data source
     * @param values        new values
     * @param selection     parametrized WHERE (filter) clause
     * @param selectionArgs arguments to above parameters
     * @return number of rows affected
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
            @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * method to return MIME type for a given URI
     *
     * @param uri is the uri pointing to data source
     * @return string representation of myme type
     */
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
