package com.example.android.p022popularmovies2.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.p022popularmovies2.data.MovieVideo;
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
 *          MovieAdapter to be bound to RecyclerView to help display a list of movie videos
 *
 *          AsyncTaskLoader for getting ArrayList of MovieData
 */

public class MovieVideoLoader extends AsyncTaskLoader<ArrayList<MovieVideo>> {

    private final static String TAG = MovieVideoLoader.class.getSimpleName();
    private final ArrayList<MovieVideo> mMovieVideoArrayList = null;
    private long mMovieId = 0;

    public MovieVideoLoader(Context context, long movieId) {
        super(context);
        Log.d(TAG, "constructing MovieVideoLoader movieId " + movieId);
        mMovieId = movieId;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Override
    public ArrayList<MovieVideo> loadInBackground() {
        try {
            Log.d(TAG, "spinner loadInBackground sortBy " + mMovieId);
            return TheMovieDbUtils.getMovieVideoData(mMovieId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
