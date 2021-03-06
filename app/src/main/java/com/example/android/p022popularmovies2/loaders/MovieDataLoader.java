package com.example.android.p022popularmovies2.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.android.p022popularmovies2.R;
import com.example.android.p022popularmovies2.data.MovieContract;
import com.example.android.p022popularmovies2.data.MovieData;
import com.example.android.p022popularmovies2.utilities.TheMovieDbUtils;

import java.util.ArrayList;

/**
 * Udacity Android Developer Nanodegree - Project Popular Movies stage 2
 *
 * @author balazs.lengyak@gmail.com
 * @version 2.0
 *          <p>
 *          - Inspired by dozens of online found examples (both visual and code design),
 *          - Might contain traces of code from official Android Developer documentation and
 *          default templates from Android Studio
 *          - getGridViewOptimalNumberOfColumns code received from Udacity reviewer
 *          <p>
 *          MovieAdapter to be bound to RecyclerView to help display a grid of movie posters
 *
 *          AsyncTaskLoader for getting ArrayList of MovieData
 */

public class MovieDataLoader extends AsyncTaskLoader<ArrayList<MovieData>> {

    private final static String TAG = MovieDataLoader.class.getSimpleName();
    private String mSortBy = null;
    private boolean mLoadFavouritesOnly;

    private ArrayList<MovieData> mMovieData;

    /**
     * constructor for our class
     *
     * @param context is the calling context
     * @param sortBy  is the sortby parameter to view most popular / top rated / favourite movies
     */
    public MovieDataLoader(Context context, String sortBy) {
        super(context);
        mSortBy = sortBy;
        Log.d(TAG, "MovieDataLoader constructor: sortby:" + mSortBy);
        if (mSortBy.equals(
                context.getResources().getString(R.string.themoviedb_source_favourites))) {
            mSortBy = MovieContract.FavouriteMovieEntry.COLUMN_NAME_TITLE;
            mLoadFavouritesOnly = true;
        }
    }

    @Override
    protected void onStartLoading() {
        if (null != mMovieData) {
            Log.d(TAG, "onStartLoading: delivering cached result with " + mMovieData.size()
                    + " movies");
            deliverResult(mMovieData);
        } else {
            Log.d(TAG, "onStartLoading: takeContentChanged, doing forceLoad");
            forceLoad();
        }
    }


    @Override
    protected void onStopLoading() {
        Log.d(TAG, "onStopLoading called");
        cancelLoad();
    }

    /**
     * background thread to load movies from either themoviedb.org or from favourite database
     * @return ArrayList with MovieData objects
     */
    @Override
    public ArrayList<MovieData> loadInBackground() {
        try {

            Log.d(TAG, "loadInBackground: querying favourite movie list sort by " + mSortBy);
            Cursor favourites = getContext().getContentResolver().query(
                    MovieContract.FavouriteMovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    MovieContract.FavouriteMovieEntry.COLUMN_NAME_TITLE);

            if (null == favourites) {
                Log.e(TAG, "loadInBackground: favourites is null");
            } else {
                Log.d(TAG,
                        "loadInBackground: favourites has " + favourites.getCount() + " entries");
            }

            ArrayList<MovieData> movieData = TheMovieDbUtils.getMovieDbData(mSortBy, favourites,
                    mLoadFavouritesOnly);
            if (null == movieData) {
                Log.d(TAG, "loadInBackground: getMovieDbData returned null");
            } else {
                Log.d(TAG,
                        "loadInBackground: moviedbutils returned " + movieData.size() + " movies");
            }
            return movieData;
        } catch (Exception e) {
            Log.e(TAG, "loadInBackground: got exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(ArrayList<MovieData> data) {
        Log.d(TAG, "deliverResult called with " + data.size() + " movies");
        mMovieData = data;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        Log.d(TAG, "onReset called");
        mMovieData = null;
        super.onReset();
    }
}
