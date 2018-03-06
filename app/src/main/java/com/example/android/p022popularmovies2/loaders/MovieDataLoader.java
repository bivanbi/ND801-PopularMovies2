package com.example.android.p022popularmovies2.loaders;

import android.content.AsyncTaskLoader;
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
    private final ArrayList<MovieData> mMovieDataArrayList = null;
    private String mSortBy = null;
    private boolean mLoadFavouritesOnly;
    /*
    public MovieDataLoader(Context context, String sortBy) {
        super(context);
        Log.d(TAG, "MovieDataLoader constructor without favourites cursor");
        mSortBy = sortBy;
    }
    */

    public MovieDataLoader(Context context, String sortBy) {
        super(context);
        Log.d(TAG, "MovieDataLoader constructor with favourites cursor");
        mSortBy = sortBy;
        if (mSortBy.equals(
                context.getResources().getString(R.string.themoviedb_source_favourites))) {
            mSortBy = MovieContract.FavouriteMovieEntry.COLUMN_NAME_TITLE;
            mLoadFavouritesOnly = true;
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


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
            return TheMovieDbUtils.getMovieDbData(mSortBy, favourites, mLoadFavouritesOnly);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
