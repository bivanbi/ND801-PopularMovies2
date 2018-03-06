package com.example.android.p022popularmovies2.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.android.p022popularmovies2.utilities.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
 *          class to hold movie attributes like Title, Release Date, synopsis etc. and also
 *          to provide static method to parse from themoviedb.org JSON string
 *          <p>
 *          Implements parcelable so MovieData object can be passed as intent extra,
 *          stored and restored on Activity lifecycle events.
 *          See {@link Parcelable} for details
 */

public class MovieData implements Parcelable {
    /**
     * method to re-create MovieData object from Parcel.
     * <p>
     * See {@link Parcelable.Creator} for details
     */
    public static final Parcelable.Creator<MovieData> CREATOR =
            new Parcelable.Creator<MovieData>() {

                @Override
                public MovieData createFromParcel(Parcel source) {
                    MovieData movieData = new MovieData();
                    movieData.setMovieId(source.readLong());
                    movieData.setTitle(source.readString());
                    movieData.setOriginalTitle(source.readString());
                    movieData.setPosterPath(source.readString());
                    movieData.setOverview(source.readString());
                    movieData.setPopularity(source.readDouble());
                    movieData.setVoteAverage(source.readDouble());
                    movieData.setReleaseDate(source.readString());
                    movieData.setFavourite(source.readInt() == 1);

                    return movieData;
                }

                @Override
                public MovieData[] newArray(int size) {
                    return new MovieData[0];
                }
            };
    private static final String TAG = MovieData.class.getSimpleName();
    private static final String JSON_ATTRIBUTE_RESULTS = "results";
    private static final String JSON_ATTRIBUTE_ID = "id";
    private static final String JSON_ATTRIBUTE_TITLE = "title";
    private static final String JSON_ATTRIBUTE_POPULARITY = "popularity";
    private static final String JSON_ATTRIBUTE_VOTE_AVERAGE = "vote_average";
    private static final String JSON_ATTRIBUTE_POSTER_PATH = "poster_path";
    private static final String JSON_ATTRIBUTE_ORIGINAL_TITLE = "original_title";
    private static final String JSON_ATTRIBUTE_OVERVIEW = "overview";
    private static final String JSON_ATTRIBUTE_RELEASE_DATE = "release_date";
    private long mMovieId;
    private String mTitle;
    private String mOriginalTitle;
    private String mPosterPath;
    private String mOverview;
    private double mPopularity;
    private double mVoteAverage;
    private String mReleaseDate;
    private boolean mIsFavourite;
    /**
     * a very basic constructor, practical when adding movie attributes one by one
     */
    public MovieData() {
    }

    /**
     * constructor to set values at creation time
     *
     * @param movieId       is the movie ID
     * @param title         is the movie title
     * @param originalTitle is the movie original title
     * @param posterPath    is the relative path to movie poster
     * @param overview      is the synopsis of the movie
     * @param popularity    is the popularity of the movie
     * @param voteAverage   is the user vote average
     * @param releaseDate   is the release date
     * @param isFavourite   is the flag to indicate if the movie has been favourited
     */
    public MovieData(long movieId, String title, String originalTitle, String posterPath,
            String overview, double popularity, double voteAverage, String releaseDate,
            boolean isFavourite) {
        mMovieId = movieId;
        mTitle = title;
        mOriginalTitle = originalTitle;
        mPosterPath = posterPath;
        mOverview = overview;
        mPopularity = popularity;
        mVoteAverage = voteAverage;
        mReleaseDate = releaseDate;
        mIsFavourite = isFavourite;
    }

    /**
     * method to parse movie data array from JSON string returned by HTTP request
     *
     * @param json is the String representation of JSON formatted data
     * @return ArrayList of MovieData objects on success, null on failure
     * @throws JSONException exception thrown if JSON parsing fails
     */
    public static ArrayList<MovieData> parseJsonMovieData(String json) throws JSONException {

        //  parse JSON string
        JSONObject moviesJsonObject = JSONUtils.safeNewJSONObject(json);
        if (null == moviesJsonObject) {
            return null;
        }

        //  get results array from JSON object
        JSONArray resultsJsonArray = moviesJsonObject.optJSONArray(JSON_ATTRIBUTE_RESULTS);

        ArrayList<MovieData> movieDataArrayList = new ArrayList<>();

        //  convert JSONArray into ArrayList for MovieData objects
        for (int i = 0; i < resultsJsonArray.length(); i++) {
            JSONObject movieDataJsonObject = resultsJsonArray.getJSONObject(i);

            MovieData movieData = new MovieData();
            movieData.setMovieId(movieDataJsonObject.optLong(JSON_ATTRIBUTE_ID));
            movieData.setTitle(movieDataJsonObject.optString(JSON_ATTRIBUTE_TITLE));
            movieData.setOriginalTitle(
                    movieDataJsonObject.optString(JSON_ATTRIBUTE_ORIGINAL_TITLE));
            movieData.setPopularity(movieDataJsonObject.optDouble(JSON_ATTRIBUTE_POPULARITY));
            movieData.setVoteAverage(movieDataJsonObject.optDouble(JSON_ATTRIBUTE_VOTE_AVERAGE));
            movieData.setPosterPath(movieDataJsonObject.optString(JSON_ATTRIBUTE_POSTER_PATH));
            movieData.setOverview(movieDataJsonObject.optString(JSON_ATTRIBUTE_OVERVIEW));
            movieData.setReleaseDate(movieDataJsonObject.optString(JSON_ATTRIBUTE_RELEASE_DATE));

            Log.d(TAG, "Downloaded Movie Data #" + movieDataArrayList.size() + ": "
                    + movieData.toString());
            movieDataArrayList.add(movieData);
        }

        return movieDataArrayList;
    }

    /**
     * getter method to movie ID
     *
     * @return long movie ID
     */
    public long getMovieId() {
        return mMovieId;
    }

    /**
     * setter method to movie ID
     *
     * @param mMovieId long movie ID
     */
    public void setMovieId(long mMovieId) {
        this.mMovieId = mMovieId;
    }

    /**
     * getter method to movie Title
     *
     * @return String with Title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * setter method to Title
     *
     * @param mTitle String with movie title
     */
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    /**
     * getter method to movie original title
     *
     * @return String with movie original title
     */
    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    /**
     * setter method to movie original title
     *
     * @param mOriginalTitle String with movie original title
     */
    public void setOriginalTitle(String mOriginalTitle) {
        this.mOriginalTitle = mOriginalTitle;
    }

    /**
     * getter method to movie poster relative URL
     *
     * @return String with movie poster relative URL
     */
    public String getPosterPath() {
        return mPosterPath;
    }

    /**
     * setter method to movie poster relative URL
     *
     * @param mPosterPath String with movie poster relative URL
     */
    public void setPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }

    /**
     * getter method to movie synopsis
     *
     * @return String with movie synopsis
     */
    public String getOverview() {
        return mOverview;
    }

    /**
     * setter method to movie synopsis
     *
     * @param mOverview String with movie synopsis
     */
    public void setOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    /**
     * getter method to movie user rating
     *
     * @return double with movie user rating
     */
    public double getVoteAverage() {
        return mVoteAverage;
    }

    /**
     * setter method to movie user rating
     *
     * @param mVoteAverage double String with movie user rating
     */
    public void setVoteAverage(double mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    /**
     * getter method to movie release date
     *
     * @return String with movie release date
     */
    public String getReleaseDate() {
        return mReleaseDate;
    }

    /**
     * setter method to movie release date
     *
     * @param mReleaseDate String with movie release date
     */
    public void setReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    /**
     * getter method to movie popularity
     *
     * @return double with movie popularity
     */
    public double getPopularity() {
        return mPopularity;
    }

    /**
     * setter method to movie popularity
     *
     * @param mPopularity double with movie popularity
     */
    public void setPopularity(double mPopularity) {
        this.mPopularity = mPopularity;
    }

    /**
     * method to get favourite flag
     *
     * @return boolean true if movie has been favourited
     */
    public boolean isFavourite() {
        return mIsFavourite;
    }

    /**
     * method to set favourite flag on movie
     *
     * @param favourite boolean true means movie has been favourited
     */
    public void setFavourite(boolean favourite) {
        mIsFavourite = favourite;
    }

    /**
     * method to get MovieData attributes as a single string
     *
     * @return String with moviedata attributes
     */
    @Override
    public String toString() {
        return "MovieData{" +
                "mMovieId=" + mMovieId +
                ", mTitle='" + mTitle + '\'' +
                ", mOriginalTitle='" + mOriginalTitle + '\'' +
                ", mPosterPath='" + mPosterPath + '\'' +
                ", mOverview='" + mOverview + '\'' +
                ", mPopularity=" + mPopularity +
                ", mVoteAverage=" + mVoteAverage +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                ", isFavourite=" + mIsFavourite +
                '}';
    }

    /**
     * required to implement Parcelable. Always return 0. The documentation of Parcelable is not
     * quite helpful on this matter, but it has something to do with this object having
     * FileDescriptors or not
     *
     * @return integer of 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * method to write object attributes to Parcel object so it can be saved on android lifecycle
     * events and sent along with intents as extra
     * <p>
     * See {@link Parcelable#writeToParcel(Parcel, int)} for details
     *
     * @param dest  is the Parcel object to write data to
     * @param flags Additional flags about how the object should be written
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mMovieId);
        dest.writeString(mTitle);
        dest.writeString(mOriginalTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
        dest.writeDouble(mPopularity);
        dest.writeDouble(mVoteAverage);
        dest.writeString(mReleaseDate);
        dest.writeInt(mIsFavourite ? 1 : 0);
    }
}
