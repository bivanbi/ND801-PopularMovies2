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
 *          class to hold movie reviews and also
 *          to provide static method to parse from themoviedb.org JSON string
 *          <p>
 *          Implements parcelable so MovieData object can be passed as intent extra,
 *          stored and restored on Activity lifecycle events.
 *          See {@link Parcelable} for details
 *          <p>
 *          <p>
 *          JSON object prototype:
 *          {
 *          "id": 315837,
 *          "page": 1,
 *          "results": [
 *          {
 *          "id": "58e044e8c3a3683d3e0110b2",
 *          "author": "Salt-and-Limes",
 *          "content": "I am writing this review as someone who hasn't seen the original anime. I
 *          have also been very critical of the whitewashing of this film. With that I aside, I
 *          went
 *          to see it with willingness to give it the benefit of the doubt. \"Ghost in the Shell\"
 *          is well...a beautiful shell. \r\n\r\nThe visuals were absolutely breath taking. The
 *          action scenes flowed so beautifully with special effects. But that's all the movie had
 *          to offer. Take away the spectacles and you have a basic run-of-the-mill action movie.
 *          \r\n\r\nThe acting was fine. But honestly, the leads didn't have anything to actually
 *          sink their teeth into. Scarjo, and everyone else, was serviceable. \r\n\r\nIf you're
 *          looking for some mindless, yet stunning entertainment, then go see it.",
 *          "url": "https:\/\/www.themoviedb.org\/review\/58e044e8c3a3683d3e0110b2"
 *          },
 *          ]
 *          "total_pages": 2,
 *          "total_results": 7
 */

public class MovieReview implements Parcelable {

    /**
     * method to re-create MovieData object from Parcel.
     * <p>
     * See {@link Parcelable.Creator} for details
     */
    public static final Parcelable.Creator<MovieReview> CREATOR =
            new Parcelable.Creator<MovieReview>() {

                @Override
                public MovieReview createFromParcel(Parcel source) {

                    MovieReview movieReview = new MovieReview();
                    movieReview.setMovieId(source.readLong());
                    movieReview.setReviewId(source.readString());
                    movieReview.setAuthor(source.readString());
                    movieReview.setContent(source.readString());
                    movieReview.setUrl(source.readString());

                    return movieReview;
                }

                public MovieReview[] newArray(int size) {
                    return new MovieReview[0];
                }
            };
    private static final String TAG = MovieReview.class.getSimpleName();
    // {"id":"58e044e8c3a3683d3e0110b2","author":"Salt-and-Limes",
    // "content":"review text...","url":"https://www.themoviedb
    // .org/review/58e044e8c3a3683d3e0110b2"}
    private static final String JSON_ATTRIBUTE_MOVIE_ID = "id";
    private static final String JSON_ATTRIBUTE_RESULTS = "results";
    private static final String JSON_ATTRIBUTE_REVIEW_ID = "id";
    private static final String JSON_ATTRIBUTE_AUTHOR = "author";
    private static final String JSON_ATTRIBUTE_CONTENT = "content";
    private static final String JSON_ATTRIBUTE_URL = "url";
    private long mMovieId;
    private String mReviewId;
    private String mAuthor;
    private String mContent;
    private String mUrl;

    public MovieReview() {
    }

    /**
     * method to parse movie review data array from JSON string returned by HTTP request
     *
     * @param json is the String representation of JSON formatted data
     * @return ArrayList of MovieReview objects on success, null on failure
     * @throws JSONException exception thrown if JSON parsing fails
     */
    public static ArrayList<MovieReview> parseJson(String json) throws JSONException {
        JSONObject moviesJsonObject = JSONUtils.safeNewJSONObject(json);
        if (null == moviesJsonObject) {
            return null;
        }

        long movieId = moviesJsonObject.optLong(JSON_ATTRIBUTE_MOVIE_ID);

        JSONArray resultsJsonArray = moviesJsonObject.optJSONArray(JSON_ATTRIBUTE_RESULTS);

        ArrayList<MovieReview> movieReviewArrayList = new ArrayList<>();

        for (int i = 0; i < resultsJsonArray.length(); i++) {
            JSONObject movieDataJsonObject = resultsJsonArray.getJSONObject(i);

            MovieReview movieReview = new MovieReview();
            movieReview.setMovieId(movieId);
            movieReview.setReviewId(movieDataJsonObject.optString(JSON_ATTRIBUTE_REVIEW_ID));
            movieReview.setAuthor(movieDataJsonObject.optString(JSON_ATTRIBUTE_AUTHOR));
            movieReview.setUrl(movieDataJsonObject.optString(JSON_ATTRIBUTE_URL));
            movieReview.setContent(movieDataJsonObject.optString(JSON_ATTRIBUTE_CONTENT));

            Log.d(TAG, "Downloaded Movie Review #" + movieReviewArrayList.size() + ": "
                    + movieReview.toString());
            movieReviewArrayList.add(movieReview);
        }

        return movieReviewArrayList;
    }

    public long getMovieId() {
        return mMovieId;
    }

    public void setMovieId(long movieId) {
        mMovieId = movieId;
    }

    public String getReviewId() {
        return mReviewId;
    }

    public void setReviewId(String reviewId) {
        mReviewId = reviewId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
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
        dest.writeString(mReviewId);
        dest.writeString(mAuthor);
        dest.writeString(mContent);
        dest.writeString(mUrl);
    }
}
