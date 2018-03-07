package com.example.android.p022popularmovies2.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.p022popularmovies2.R;
import com.example.android.p022popularmovies2.data.MovieData;
import com.example.android.p022popularmovies2.utilities.FileUtils;
import com.example.android.p022popularmovies2.utilities.TheMovieDbUtils;

import java.io.File;
import java.net.URL;

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
 *
 *              loader class for poster cache save or delete background operations
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

    /**
     * constructor when deleting files
     *
     * @param context   is the calling context
     * @param operation is the operation mode, see class public static String OPERATION_*
     * @param movieData is the movie data to operate on
     */
    public PosterCacheLoader(Context context, int operation, MovieData movieData) {
        super(context);
        mOperation = operation;
        mMovieData = movieData;
    }

    /**
     * constructor when downloading files
     *
     * @param context is the calling context
     * @param operation is the operation mode, see class public static String OPERATION_*
     * @param movieData is the movie data to operate on
     * @param url is the URL pointing to image to be downloaded
     * @param saveFile is the File object where image should be saved to
     */
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

    /**
     * background job to download or delete image
     * @return exit status code, see RESULT_CODE_*
     */
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
