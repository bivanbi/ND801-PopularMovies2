package com.example.android.p022popularmovies2.utilities;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.ImageView;

import com.example.android.p022popularmovies2.BuildConfig;
import com.example.android.p022popularmovies2.R;
import com.example.android.p022popularmovies2.data.MovieContract;
import com.example.android.p022popularmovies2.data.MovieData;
import com.example.android.p022popularmovies2.data.MovieReview;
import com.example.android.p022popularmovies2.data.MovieVideo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
 *          <p>
 *          <p>
 *          Class to provide helper methods to download and parse movie attributes from
 *          themoviedb.org
 *          <p>
 *          Prototype of JSON response from themoviedb.org search
 *          {
 *          "page": 1,
 *          "total_results": 347210,
 *          "total_pages": 17361,
 *          "results": [
 *          {
 *          "vote_count": 5727,
 *          "id": 211672,
 *          "video": false,
 *          "vote_average": 6.4,
 *          "title": "Minions",
 *          "popularity": 526.313015,
 *          "poster_path": "\/q0R4crx2SehcEEQEkYObktdeFy.jpg",
 *          "original_language": "en",
 *          "original_title": "Minions",
 *          "genre_ids": [
 *          10751,
 *          16,
 *          12,
 *          35
 *          ],
 *          "backdrop_path": "\/qLmdjn2fv0FV2Mh4NBzMArdA0Uu.jpg",
 *          "adult": false,
 *          "overview": "Minions Stuart, Kevin and Bob are recruited by Scarlet Overkill, a
 *          super-villain who, alongside her inventor husband Herb, hatches a plot to take over the
 *          world.",
 *          "release_date": "2015-06-17"
 *          },
 *          { next obect in array...
 */

public class TheMovieDbUtils {
    private static final String TAG = TheMovieDbUtils.class.getSimpleName();
    private static final String MOVIE_LIST_BASE_URL = "http://api.themoviedb.org/3/movie/";

    private static final String MOVIE_VIDEO_LIST_URL_SUFFIX = "/videos";
    private static final String MOVIE_REVIEW_LIST_URL_SUFFIX = "/reviews";

    private static final String MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String MOVIE_QUERY_KEY_API_KEY = "api_key";

    /**
     * helper method to load image in to a given view by calling Picasso or providing a
     * placeholder image if movie poster URL is a String of 'null'
     *
     * @param context   is the calling context
     * @param view      is the view that the image should be added to
     * @param movieData is the object holding all necessary movie attributes
     * @param imageSize is the requested image size, see strings.xml - themoviedb_image_resolution_*
     */
    public static void loadMovieImageIntoView(final Context context, View view,
            final MovieData movieData, String imageSize, LoadMovieImageIntoViewCallback callback) {
        final ImageView imageView = (ImageView) view;

        final long movieId = movieData.getMovieId();
        File cacheFile;
        final String imageUrl = constructImageUrl(movieData.getPosterPath(), imageSize);

        if (null == imageUrl) {
            Log.i(TAG, "loadMovieImageIntoView: " + movieId + ": movie has no image");
            imageView.setImageResource(android.R.drawable.star_big_off);
            callback.onSuccess(view, movieData, imageSize);
            return;
        }

        if (movieData.isFavourite()) {
            Log.i(TAG, "loadMovieImageIntoView: " + movieId
                    + ": movie is favourite, using cache file");

            cacheFile = FileUtils.getMoviePosterCacheFile(context, movieData, imageSize);

            if (null != cacheFile) {
                Log.d(TAG, "loadMovieImageIntoView: cacheFile: " + cacheFile.toString());
                if (cacheFile.exists()) {
                    Log.d(TAG, "loadMovieImageIntoView: " + movieId + ": using cached file "
                            + cacheFile.toString());
                    loadMovieImageFromFileIntoView(context, imageView, movieData, imageSize,
                            cacheFile, callback);
                } else {
                    Log.d(TAG,
                            "loadMovieImageIntoView: " + movieId + ": cached file does not exist: "
                                    + cacheFile.toString());
                    loadMovieImageFromUrlIntoFileThenIntoView(context, imageView, movieData,
                            imageSize, imageUrl, cacheFile, callback);
                }
            } else {
                Log.e(TAG, "getCachedPosterImagePathName " + movieId + ": returned null");
                callback.onError(view, movieData, imageSize);
            }
        } else {
            //Log.d(TAG, "loadMovieImageIntoView: " + movieId + ": loading from URL: " + imageUrl);
            loadMovieImageFromUrlIntoView(context, imageView, movieData, imageSize, imageUrl,
                    callback);
        }
    }

    private static void loadMovieImageFromFileIntoView(Context context,
            final ImageView imageView,
            final MovieData movieData,
            final String imageSize,
            final File cacheFile,
            final LoadMovieImageIntoViewCallback callback) {

        final long movieId = movieData.getMovieId();

        //  use cached file
        Picasso.with(context).load(cacheFile)
                .placeholder(android.R.drawable.star_big_off)
                .error(android.R.drawable.ic_dialog_alert)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "loadMovieImageFromFileIntoView:  " + movieId + ": loaded "
                                + cacheFile.toString());
                        callback.onSuccess(imageView, movieData, imageSize);
                    }

                    @Override
                    public void onError() {
                        Log.e(TAG,
                                "loadMovieImageFromFileIntoView: " + movieId
                                        + ": Picasso: failed to load " + cacheFile.toString()
                                        + " into imageView");
                        callback.onError(imageView, movieData, imageSize);
                    }
                });
    }

    private static void loadMovieImageFromUrlIntoView(
            Context context,
            final ImageView imageView,
            final MovieData movieData,
            final String imageSize,
            final String imageUrl,
            final LoadMovieImageIntoViewCallback callback) {

        final long movieId = movieData.getMovieId();

        Picasso.with(context).load(imageUrl)
                .placeholder(android.R.drawable.star_big_off)
                .error(android.R.drawable.ic_dialog_alert)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess(imageView, movieData, imageSize);
                    }

                    @Override
                    public void onError() {
                        Log.e(TAG, "loadMovieImageFromUrlIntoView: " + movieId + ": failed to load "
                                + imageUrl + " into imageView");
                        callback.onError(imageView, movieData, imageSize);
                    }
                });
    }

    private static void loadMovieImageFromUrlIntoFileThenIntoView(final Context context,
            final ImageView imageView,
            final MovieData movieData,
            final String imageSize,
            final String imageUrl,
            final File file,
            final LoadMovieImageIntoViewCallback callback) {

        final long movieId = movieData.getMovieId();

        Log.i(TAG, "loadMovieImageFromUrlIntoFileThenIntoView: " + movieId
                + ": " + imageUrl + ", cache file: " + file.getAbsolutePath());

        boolean result = NetworkUtils.downloadFileFromUrlIntoFile(imageUrl, file);

        //  if saving to cache fails, still try to simply download into view
        if (!result) {
            Log.e(TAG, "loadMovieImageFromUrlIntoFileThenIntoView: downloadFileFromUrlIntoFile("
                    + imageUrl + ", " + file.toString() + ") failed");
            loadMovieImageFromUrlIntoView(context, imageView, movieData, imageSize, imageUrl,
                    callback);
            return;
        }

        loadMovieImageFromFileIntoView(context, imageView, movieData, imageSize, file, callback);
    }

    /**
     * method to assemble an absolute URL to the poster image
     *
     * @param posterUrl is the relative URL to poster image or String 'null'
     * @param imageSize is the requested image size, see strings.xml - themoviedb_image_resolution_*
     * @return String with absolute image URL or null if movie has no poster
     */
    private static String constructImageUrl(String posterUrl, String imageSize) {
        if (posterUrl.equals("null")) {
            return null;
        }
        return MOVIE_IMAGE_BASE_URL + imageSize + posterUrl;
    }

    /**
     * method to construct an absolute URL to the themoviedb.org movie list with the required
     * parameters such as sort attribute and order
     *
     * @param sortBy is the attribute to sort by (popularity or user rating)
     * @return an URL object containing absolute URL to the movie list
     */
    private static URL constructMovieListUrl(String sortBy) {

        Uri builtUri = Uri.parse(MOVIE_LIST_BASE_URL + sortBy).buildUpon()
                .appendQueryParameter(MOVIE_QUERY_KEY_API_KEY, BuildConfig.THE_MOVIE_DB_V3_API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "failed to build URL: " + builtUri.toString());
            e.printStackTrace();
        }

        return url;
    }

    /**
     * method to construct an absolute URL to the themoviedb.org movie video list with the
     * required
     * parameters such as movie ID
     *
     * @param movieId is the ID of movie to get videos for
     * @return an URL object containing absolute URL to the movie video list
     */
    private static URL constructMovieVideoListUrl(Long movieId) {

        Uri builtUri = Uri.parse(MOVIE_LIST_BASE_URL + movieId + MOVIE_VIDEO_LIST_URL_SUFFIX)
                .buildUpon()
                .appendQueryParameter(MOVIE_QUERY_KEY_API_KEY, BuildConfig.THE_MOVIE_DB_V3_API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "failed to build URL: " + builtUri.toString());
            e.printStackTrace();
        }

        return url;
    }

    /**
     * method to construct an absolute URL to the themoviedb.org movie review list with the required
     * parameters such as movie ID
     *
     * @param movieId is the ID of movie to get videos for
     * @return an URL object containing absolute URL to the movie video list
     */
    private static URL constructMovieReviewListUrl(Long movieId) {

        Uri builtUri = Uri.parse(MOVIE_LIST_BASE_URL + movieId + MOVIE_REVIEW_LIST_URL_SUFFIX)
                .buildUpon()
                .appendQueryParameter(MOVIE_QUERY_KEY_API_KEY, BuildConfig.THE_MOVIE_DB_V3_API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "failed to build URL: " + builtUri.toString());
            e.printStackTrace();
        }

        return url;
    }

    /**
     * method to fetch and parse movie list data
     *
     * @param sortBy is the sort attribute (popularity or user rating)
     * @return ArrayList of MovieData or null on error
     * @throws IOException   on HTTP request error
     * @throws JSONException on JSON parsing error
     */
    public static ArrayList<MovieData> getMovieDbData(String sortBy,
            @Nullable Cursor favourites,
            boolean loadFavourites)
            throws IOException, JSONException {

        if (loadFavourites) {
            if (null == favourites) {
                Log.e(TAG, "getMovieDbData:loadFavourites: favourites is null!");
                return null;
            }
            return favouritesCursorToMovieDataArrayList(favourites);
        }
        // build URL
        URL movieListUrl = constructMovieListUrl(sortBy);

        Log.d(TAG, "getMovieDbData: URL: " + movieListUrl.toString());

        String jsonResponse = NetworkUtils.getResponseFromHttpUrl(movieListUrl);
        ArrayList<MovieData> movieData = MovieData.parseJsonMovieData(jsonResponse);

        return mapFavouritesToMovieDataArrayList(favourites, movieData);

    }

    private static ArrayList<MovieData> mapFavouritesToMovieDataArrayList(Cursor favourites,
            ArrayList<MovieData> movieData) {

        if (null == movieData) {
            return null;
        }

        if (null == favourites) {
            Log.e(TAG, "getMovieDbData: favourites is null!");
            return movieData;
        }

        if (0 == favourites.getCount()) {
            Log.i(TAG, "getMovieDbData: favourites is empty");
            return movieData;
        }

        //  set favourite bit on moviedata
        //  first, set up a Map with movie IDs
        LongSparseArray<Boolean> favouriteMovieIdsMap = new LongSparseArray<>(movieData.size());

        int movieIdColumnIndex = favourites.getColumnIndex(MovieContract.FavouriteMovieEntry._ID);
        int movieTitleColumnIndex = favourites.getColumnIndex(
                MovieContract.FavouriteMovieEntry.COLUMN_NAME_TITLE);

        Log.d(TAG,
                "getMovieDbData: making a map of " + favourites.getCount() + " favourite entries");
        if (favourites.moveToFirst()) {
            do {
                favouriteMovieIdsMap.put(favourites.getLong(movieIdColumnIndex), true);
                Log.d(TAG, "getMovieDbData: found favourite: "
                        + favourites.getString(movieTitleColumnIndex) + " "
                        + favourites.getLong(movieIdColumnIndex));
                // do what you need with the cursor here
            } while (favourites.moveToNext());
        }

        //  now we have a map, iterate over themoviedb results
        for (int i = 0; i < movieData.size(); i++) {
            if (null != favouriteMovieIdsMap.get(movieData.get(i).getMovieId())) {
                Log.d(TAG, "getMovieDbData: setting favourite on " + movieData.get(i).getMovieId());
                movieData.get(i).setFavourite(true);
                Log.d(TAG,
                        "getMovieDbData: checkback favourite: " + movieData.get(i).isFavourite());
            }
        }
        return movieData;
    }

    private static ArrayList<MovieData> favouritesCursorToMovieDataArrayList(Cursor favourites) {
        //  now we have a map, iterate over themoviedb results
        if (null == favourites) {
            Log.e(TAG, "getMovieDbData: favourites is null!");
            return null;
        }

        ArrayList<MovieData> movieData = new ArrayList<>();
        if (0 == favourites.getCount()) {
            return movieData;
        }

        int movieIdColumnIndex =
                favourites.getColumnIndex(MovieContract.FavouriteMovieEntry._ID);
        int movieTitleColumnIndex =
                favourites.getColumnIndex(MovieContract.FavouriteMovieEntry.COLUMN_NAME_TITLE);
        int movieOriginalTitleColumnIndex =
                favourites.getColumnIndex(
                        MovieContract.FavouriteMovieEntry.COLUMN_NAME_ORIGINAL_TITLE);
        int movieOverviewColumnIndex =
                favourites.getColumnIndex(MovieContract.FavouriteMovieEntry.COLUMN_NAME_OVERVIEW);
        int moviePosterPathColumnIndex =
                favourites.getColumnIndex(
                        MovieContract.FavouriteMovieEntry.COLUMN_NAME_POSTER_PATH);
        int moviePopularityColumnIndex =
                favourites.getColumnIndex(MovieContract.FavouriteMovieEntry.COLUMN_NAME_POPULARITY);
        int movieVoteAverageColumnIndex =
                favourites.getColumnIndex(
                        MovieContract.FavouriteMovieEntry.COLUMN_NAME_VOTE_AVERAGE);
        int movieReleaseDateColumnIndex =
                favourites.getColumnIndex(
                        MovieContract.FavouriteMovieEntry.COLUMN_NAME_RELEASE_DATE);


        if (favourites.moveToFirst()) {
            do {
                Log.d(TAG, "favouritesCursorToMovieDataArrayList: " + favourites.getString(
                        movieTitleColumnIndex));
                movieData.add(new MovieData(favourites.getLong(movieIdColumnIndex),
                        favourites.getString(movieTitleColumnIndex),
                        favourites.getString(movieOriginalTitleColumnIndex),
                        favourites.getString(moviePosterPathColumnIndex),
                        favourites.getString(movieOverviewColumnIndex),
                        favourites.getDouble(moviePopularityColumnIndex),
                        favourites.getDouble(movieVoteAverageColumnIndex),
                        favourites.getString(movieReleaseDateColumnIndex),
                        true
                ));
            } while (favourites.moveToNext());
        }

        return movieData;

    }

    /**
     * method to fetch and parse movie list data
     *
     * @param movieId is the ID of the movie to get video list for
     * @return ArrayList of MovieVideo or null on error
     * @throws IOException   on HTTP request error
     * @throws JSONException on JSON parsing error
     */
    public static ArrayList<MovieVideo> getMovieVideoData(Long movieId)
            throws IOException, JSONException {
        // build URL
        URL movieVideoListUrl = constructMovieVideoListUrl(movieId);

        Log.d(TAG, "getMovieDbData: URL: " + movieVideoListUrl.toString());
        String jsonResponse = NetworkUtils.getResponseFromHttpUrl(movieVideoListUrl);
        //return parseJson(jsonResponse);
        return MovieVideo.parseJson(jsonResponse);
    }

    /**
     * method to fetch and parse movie list data
     *
     * @param movieId is the ID of the movie to get video list for
     * @return ArrayList of MovieVideo or null on error
     * @throws IOException   on HTTP request error
     * @throws JSONException on JSON parsing error
     */
    public static ArrayList<MovieReview> getMovieReviewData(Long movieId)
            throws IOException, JSONException {
        // build URL
        URL movieReviewListUrl = constructMovieReviewListUrl(movieId);

        Log.d(TAG, "getMovieDbData: URL: " + movieReviewListUrl.toString());
        String jsonResponse = NetworkUtils.getResponseFromHttpUrl(movieReviewListUrl);
        return MovieReview.parseJson(jsonResponse);
    }

    /**
     * method to extract year from releaseDate String
     *
     * @param context     is the calling context
     * @param releaseDate is the String release date in SQL notation: YYYY-MM-DD
     * @return String year on SQL notation, placeholder if release date is not specified,
     * releaseDate itself otherwise
     */
    public static String getReleaseYearFromReleaseDate(Context context, String releaseDate) {
        if (null == releaseDate || releaseDate.isEmpty()) {
            return context.getResources().getString(
                    R.string.themoviedb_missing_release_date_placeholder);
        } else if (releaseDate.contains("-")) {
            return TextUtils.split(releaseDate, "-")[0];
        } else {
            return releaseDate;
        }
    }

    public static boolean addPosterImageToCache(Context context, MovieData movieData,
            String imageSize) {

        long movieId = movieData.getMovieId();
        final String imageUrl = constructImageUrl(movieData.getPosterPath(), imageSize);

        if (null == imageUrl) {
            Log.i(TAG, "addPosterImageToCache: " + movieId + ": movie has no image");
            return true;
        }

        File saveFile = FileUtils.getMoviePosterCacheFile(context, movieData, imageSize);
        if (null == saveFile) {
            Log.e(TAG, "addPosterImageToCache: getMoviePosterCacheFile returned null");
            return false;
        }

        boolean result = NetworkUtils.downloadFileFromUrlIntoFile(imageUrl, saveFile);
        if (!result) {
            Log.e(TAG, "addPosterImageToCache: downloadFileFromUrlIntoFile returned false");
            return false;
        }

        return true;
    }


    public interface LoadMovieImageIntoViewCallback {
        void onSuccess(View view, MovieData movieData, String imageSize);

        void onError(View view, MovieData movieData, String imageSize);
    }
}
