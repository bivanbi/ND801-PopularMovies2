package com.example.android.p022popularmovies2.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.p022popularmovies2.R;
import com.example.android.p022popularmovies2.data.MovieData;
import com.example.android.p022popularmovies2.utilities.FileUtils;
import com.example.android.p022popularmovies2.utilities.TheMovieDbUtils;

import java.io.File;
import java.net.URL;

/**
 * TODO documentation
 * Created by bivanbi on 2018.03.04..
 */

public class PosterCacheLoader extends AsyncTaskLoader<Integer> {
    public static final int OPERATION_ADD_TO_CACHE = 1;
    public static final int OPERATION_REMOVE_FROM_CACHE = 2;
    public static final int RESULT_CODE_INVALID_OPERATION = -1;
    public static final int RESULT_CODE_SUCCESS = 0;
    public static final int RESULT_CODE_FAILED = 1;
    private static final String TAG = PosterCacheLoader.class.getSimpleName();
    private int mOperation;
    private MovieData mMovieData;

    public PosterCacheLoader(Context context, int operation, MovieData movieData) {
        super(context);
        mOperation = operation;
        mMovieData = movieData;
    }

    public PosterCacheLoader(Context context, int operation, MovieData movieData,
            URL url,
            File saveFile) {
        super(context);
        mOperation = operation;
        mMovieData = movieData;
    }


    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Integer loadInBackground() {
        switch (mOperation) {
            case OPERATION_ADD_TO_CACHE:
                Log.d(TAG, "loadInBackground: ADD on "
                        + mMovieData.getMovieId());

                Context context = getContext();

                boolean result = TheMovieDbUtils.addPosterImageToCache(getContext(), mMovieData,
                        context.getResources().getString(
                                R.string.themoviedb_image_resolution_thumbnail)) &&
                        TheMovieDbUtils.addPosterImageToCache(getContext(), mMovieData,
                                context.getResources().getString(
                                        R.string.themoviedb_image_resolution_large));

                if (result) {
                    return RESULT_CODE_SUCCESS;
                }

                Log.e(TAG, "loadInBackground OPERATION_ADD_TO_CACHE: addPosterImageToCache failed");
                return RESULT_CODE_FAILED;

            case OPERATION_REMOVE_FROM_CACHE:
                Log.d(TAG, "loadInBackground REMOVE calling deletePosterCache on "
                        + mMovieData.getMovieId());
                FileUtils.deletePosterCache(getContext(), mMovieData);
                return RESULT_CODE_SUCCESS;

            default:
                Log.e(TAG, "loadInBackground called with invalid operation " + mOperation);
                return RESULT_CODE_INVALID_OPERATION;
        }
    }
}
