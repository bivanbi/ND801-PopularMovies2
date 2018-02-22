package com.example.android.p021popularmovies1.utilities;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.android.p021popularmovies1.MovieData;
import com.example.android.p021popularmovies1.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Udacity Android Developer Nanodegree - Project Popular Movies stage 1
 *
 * @author balazs.lengyak@gmail.com
 * @version 1.0
 *          <p>
 *          - Inspired by dozens of online found examples (both visual and code design),
 *          - Might contain traces of code from official Android Developer documentation and
 *          default templates from Android Studio
 *          <p>
 *          <p>
 *          Class to provide helper methods to download and parse movie attributes from themoviedb.org
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
 *          "overview": "Minions Stuart, Kevin and Bob are recruited by Scarlet Overkill, a super-villain who, alongside her inventor husband Herb, hatches a plot to take over the world.",
 *          "release_date": "2015-06-17"
 *          },
 *          { next obect in array...
 */

public class TheMovieDbUtils {
    public static final String MOVIE_QUERY_PARAM_ORDER_ASCEND = "asc";
    public static final String MOVIE_QUERY_PARAM_ORDER_DESCEND = "desc";
    private static final String TAG = TheMovieDbUtils.class.getSimpleName();
    private static final String MOVIE_DATA_BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String MOVIE_QUERY_KEY_API_KEY = "api_key";
    private static final String MOVIE_QUERY_KEY_SORT_BY = "sort_by";
    private static final String JSON_ATTRIBUTE_RESULTS = "results";
    private static final String JSON_ATTRIBUTE_TITLE = "title";
    private static final String JSON_ATTRIBUTE_POPULARITY = "popularity";
    private static final String JSON_ATTRIBUTE_VOTE_AVERAGE = "vote_average";
    private static final String JSON_ATTRIBUTE_POSTER_PATH = "poster_path";
    private static final String JSON_ATTRIBUTE_ORIGINAL_TITLE = "original_title";
    private static final String JSON_ATTRIBUTE_OVERVIEW = "overview";
    private static final String JSON_ATTRIBUTE_RELEASE_DATE = "release_date";


    /**
     * helper method to load image in to a given view by calling Picasso or providing a
     * placeholder image if movie poster URL is a String of 'null'
     *
     * @param context   is the calling context
     * @param view      is the view that the image should be added to
     * @param posterUrl is the relative URL to poster image or String 'null'
     * @param imageSize is the requested image size, see strings.xml - themoviedb_image_resolution_*
     */
    public static void loadMovieImageIntoView(Context context, View view,
                                              String posterUrl, String imageSize) {
        final ImageView imageView = (ImageView) view;
        String imageUrl = constructImageUrl(posterUrl, imageSize);

        if (null == imageUrl) {
            Log.i(TAG, "loadMovieImageIntoView: movie has no image");
            imageView.setImageResource(android.R.drawable.star_big_off);
        } else {
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    Log.e(TAG, "loadMovieImageIntoView: picasso image load failed: " + exception.toString());
                    imageView.setImageResource(android.R.drawable.star_big_off);
                }
            });

            builder.build().load(imageUrl)
                    .placeholder(android.R.drawable.star_big_off)
                    .into(imageView);
        }
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
     * @param context   is the calling context
     * @param sortBy    is the attribute to sort by (popularity or user rating)
     * @param sortOrder is the sort order, asc or desc
     * @return an URL object containing absolute URL to the movie list
     */
    private static URL constructMovieListUrl(Context context, String sortBy, String sortOrder) {

        Uri builtUri = Uri.parse(MOVIE_DATA_BASE_URL).buildUpon()
                .appendQueryParameter(MOVIE_QUERY_KEY_SORT_BY, sortBy + "." + sortOrder)
                .appendQueryParameter(MOVIE_QUERY_KEY_API_KEY, context.getString(R.string.themoviedb_v3_api_key))
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
     * @param context   is the calling context
     * @param sortBy    is the sort attribute (popularity or user rating)
     * @param sortOrder is the sort order asc or desc
     * @return ArrayList of MovieData or null on error
     * @throws IOException   on HTTP request error
     * @throws JSONException on JSON parsing error
     */
    public static ArrayList<MovieData> getMovieDbData(Context context, String sortBy, String sortOrder)
            throws IOException, JSONException {
        // build URL
        URL movieListUrl = constructMovieListUrl(context, sortBy, sortOrder);

        Log.d(TAG, "getMovieDbData: URL: " + movieListUrl.toString());

        String jsonResponse = getResponseFromHttpUrl(movieListUrl);
        return parseJsonMovieData(jsonResponse);
    }


    /**
     * method to query HTTP server and fetch response, which will be a JSON string on success
     *
     * @param url is the URL to query
     * @return String with server response
     * @throws IOException on error
     */
    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


    /**
     * method to parse movie data array from JSON string returned by HTTP request
     *
     * @param json is the String representation of JSON formatted data
     * @return ArrayList of MovieData objects on success, null on failure
     * @throws JSONException exception thrown if JSON parsing fails
     */
    private static ArrayList<MovieData> parseJsonMovieData(String json) throws JSONException {
        JSONObject moviesJsonObject = safeNewJSONObject(json);
        if (null == moviesJsonObject) {
            return null;
        }

        JSONArray resultsJsonArray = moviesJsonObject.optJSONArray(JSON_ATTRIBUTE_RESULTS);

        ArrayList<MovieData> movieDataArrayList = new ArrayList<>();

        for (int i = 0; i < resultsJsonArray.length(); i++) {
            JSONObject movieDataJsonObject = resultsJsonArray.getJSONObject(i);

            MovieData movieData = new MovieData();
            movieData.setTitle(movieDataJsonObject.optString(JSON_ATTRIBUTE_TITLE));
            movieData.setOriginalTitle(movieDataJsonObject.optString(JSON_ATTRIBUTE_ORIGINAL_TITLE));
            movieData.setPopularity(movieDataJsonObject.optDouble(JSON_ATTRIBUTE_POPULARITY));
            movieData.setVoteAverage(movieDataJsonObject.optDouble(JSON_ATTRIBUTE_VOTE_AVERAGE));
            movieData.setPosterUrl(movieDataJsonObject.optString(JSON_ATTRIBUTE_POSTER_PATH));
            movieData.setOverview(movieDataJsonObject.optString(JSON_ATTRIBUTE_OVERVIEW));
            movieData.setReleaseDate(movieDataJsonObject.optString(JSON_ATTRIBUTE_RELEASE_DATE));

            Log.d(TAG, "Downloaded Movie Data #" + movieDataArrayList.size() + ": " + movieData.toString());
            movieDataArrayList.add(movieData);
        }

        return movieDataArrayList;
    }

    /**
     * method to extract year from releaseDate String
     *
     * @param context     is the calling context
     * @param releaseDate is the String release date in SQL notation: YYYY-MM-DD
     * @return String year on SQL notation, placeholder if release date is not specified, releaseDate itself otherwise
     */
    public static String getReleaseYearFromReleaseDate(Context context, String releaseDate) {
        if (null == releaseDate || releaseDate.isEmpty()) {
            return context.getResources().getString(R.string.themoviedb_missing_release_date_placeholder);
        } else if (releaseDate.contains("-")) {
            return TextUtils.split(releaseDate, "-")[0];
        } else {
            return releaseDate;
        }
    }

    /**
     * method to safely parse JSON string into json object, will catch json exception
     *
     * @param json is the JSON as string
     * @return is the JSONObject parsed from json string
     */
    @Nullable
    private static JSONObject safeNewJSONObject(String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "JSON string parsing failed: " + e.toString());
            return null;
        }
    }
}
