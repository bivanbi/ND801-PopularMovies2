package com.example.android.p022popularmovies2.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.android.p022popularmovies2.R;
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
 *          class to hold movie video attributes like name, site, size etc. and also
 *          to provide static method to parse from themoviedb.org JSON string
 *          <p>
 *          Implements parcelable so MovieData object can be passed as intent extra,
 *          stored and restored on Activity lifecycle events.
 *          See {@link Parcelable} for details
 *          <p>
 *          JSON object prototype:
 *          {
 *          "id": 315837,
 *          "results": [
 *          {
 *          "id": "58375b74c3a36836a9017dea",
 *          "iso_639_1": "en",
 *          "iso_3166_1": "US",
 *          "key": "HapT0SKcyfY",
 *          "name": "Teaser #1",
 *          "site": "YouTube",
 *          "size": 1080,
 *          "type": "Teaser"
 *          },
 *          {
 */

public class MovieVideo implements Parcelable {

    /**
     * method to re-create MovieData object from Parcel.
     * <p>
     * See {@link Parcelable.Creator} for details
     */
    public static final Parcelable.Creator<MovieVideo> CREATOR =
            new Parcelable.Creator<MovieVideo>() {

                @Override
                public MovieVideo createFromParcel(Parcel source) {

                    MovieVideo movieVideo = new MovieVideo();
                    movieVideo.setMovieId(source.readLong());
                    movieVideo.setVideoId(source.readString());
                    movieVideo.setIso639(source.readString());
                    movieVideo.setIso3166(source.readString());
                    movieVideo.setKey(source.readString());
                    movieVideo.setName(source.readString());
                    movieVideo.setSite(source.readString());
                    movieVideo.setSize(source.readInt());
                    movieVideo.setType(source.readString());

                    return movieVideo;
                }

                public MovieVideo[] newArray(int size) {
                    return new MovieVideo[0];
                }
            };
    private static final String TAG = MovieVideo.class.getSimpleName();
    private static final String SITE_STRING_YOUTUBE = "YouTube";
    private static final String YOUTUBE_APP_URI_FORMAT_STRING = "vnd.youtube:%s";
    private static final String YOUTUBE_VIDEO_URL_FORMAT_STRING =
            "http://www.youtube.com/watch?v=%s";
    private static final String SHARE_ACTION_MIME_TYPE = "text/plain";
    // {"id":"58375b74c3a36836a9017dea","iso_639_1":"en","iso_3166_1":"US",
    // "key":"HapT0SKcyfY","name":"Teaser #1","site":"YouTube","size":1080,"type":"Teaser"}
    private static final String JSON_ATTRIBUTE_MOVIE_ID = "id";
    private static final String JSON_ATTRIBUTE_RESULTS = "results";
    private static final String JSON_ATTRIBUTE_VIDEO_ID = "id";
    private static final String JSON_ATTRIBUTE_ISO_639_1 = "iso_639_1";
    private static final String JSON_ATTRIBUTE_ISO_3166_1 = "iso_3166_1";
    private static final String JSON_ATTRIBUTE_KEY = "key";
    private static final String JSON_ATTRIBUTE_NAME = "name";
    private static final String JSON_ATTRIBUTE_SITE = "site";
    private static final String JSON_ATTRIBUTE_SIZE = "size";
    private static final String JSON_ATTRIBUTE_TYPE = "type";
    //  themoviedb.org movie ID this video belongs to
    private long mMovieId;
    private String mVideoId;
    private String mIso639;
    private String mIso3166;
    //  key: in URL https://www.youtube.com/watch?v=k0GooNNn6oU the key is the string after ?v=
    private String mKey;
    private String mName;
    private String mSite;
    private int size;
    private String type;

    /**
     * basic constructor
     */
    public MovieVideo() {
    }

    /**
     * method to parse movie review data array from JSON string returned by HTTP request
     *
     * @param json is the String representation of JSON formatted data
     * @return ArrayList of MovieReview objects on success, null on failure
     * @throws JSONException exception thrown if JSON parsing fails
     */
    public static ArrayList<MovieVideo> parseJson(String json) throws JSONException {
        JSONObject moviesJsonObject = JSONUtils.safeNewJSONObject(json);
        if (null == moviesJsonObject) {
            return null;
        }

        long movieId = moviesJsonObject.optLong(JSON_ATTRIBUTE_MOVIE_ID);

        JSONArray resultsJsonArray = moviesJsonObject.optJSONArray(JSON_ATTRIBUTE_RESULTS);

        ArrayList<MovieVideo> movieVideoArrayList = new ArrayList<>();

        for (int i = 0; i < resultsJsonArray.length(); i++) {
            JSONObject movieDataJsonObject = resultsJsonArray.getJSONObject(i);

            MovieVideo movieVideo = new MovieVideo();
            movieVideo.setMovieId(movieId);
            movieVideo.setVideoId(movieDataJsonObject.optString(JSON_ATTRIBUTE_VIDEO_ID));
            movieVideo.setIso639(movieDataJsonObject.optString(JSON_ATTRIBUTE_ISO_639_1));
            movieVideo.setIso3166(movieDataJsonObject.optString(JSON_ATTRIBUTE_ISO_3166_1));
            movieVideo.setKey(movieDataJsonObject.optString(JSON_ATTRIBUTE_KEY));
            movieVideo.setName(movieDataJsonObject.optString(JSON_ATTRIBUTE_NAME));
            movieVideo.setSite(movieDataJsonObject.optString(JSON_ATTRIBUTE_SITE));
            movieVideo.setSize(movieDataJsonObject.optInt(JSON_ATTRIBUTE_SIZE));
            movieVideo.setType(movieDataJsonObject.optString(JSON_ATTRIBUTE_TYPE));

            Log.d(TAG, "Downloaded Movie Video #" + movieVideoArrayList.size() + ": "
                    + movieVideo.toString());
            movieVideoArrayList.add(movieVideo);
        }

        return movieVideoArrayList;
    }

    public static void playMovieVideo(Context context, MovieVideo movieVideo) {
        //  check if we know the given site
        if (!isVideoSiteKnown(movieVideo)) {
            String errorMessage = String.format(
                    context.getString(R.string.error_message_video_site_unknown_format_string),
                    movieVideo.getSite());
            Toast.makeText(context,
                    errorMessage,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //  try to play in app
        if (playVideoInApp(context, movieVideo)) {
            return;
        }

        //  try to play in browser
        if (playVideoInBrowser(context, movieVideo)) {
            return;
        }

        Toast.makeText(context,
                context.getString(R.string.error_message_no_app_found_to_play_video),
                Toast.LENGTH_SHORT).show();
    }

    private static boolean playVideoInBrowser(Context context, MovieVideo movieVideo) {

        Uri inBrowserUri = constructVideoInBrowserUri(movieVideo);
        if (null == inBrowserUri) {
            return false;
        }

        Intent playInBrowserIntent = new Intent(Intent.ACTION_VIEW, inBrowserUri);

        if (null != playInBrowserIntent.resolveActivity(context.getPackageManager())) {
            // open in browser
            context.startActivity(playInBrowserIntent);
            return true;
        }

        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isVideoSiteKnown(MovieVideo movieVideo) {
        switch (movieVideo.getSite()) {
            case SITE_STRING_YOUTUBE:
                return true;
            default:
                return false;
        }
    }

    @Nullable
    private static Uri constructVideoInBrowserUri(MovieVideo movieVideo) {
        switch (movieVideo.getSite()) {
            case SITE_STRING_YOUTUBE:
                return Uri.parse(
                        String.format(YOUTUBE_VIDEO_URL_FORMAT_STRING, movieVideo.getKey()));
        }
        return null;
    }

    @Nullable
    private static Uri constructVideoInAppUri(MovieVideo movieVideo) {
        switch (movieVideo.getSite()) {
            case SITE_STRING_YOUTUBE:
                return Uri.parse(String.format(YOUTUBE_APP_URI_FORMAT_STRING, movieVideo.getKey()));
        }
        return null;
    }

    private static boolean playVideoInApp(Context context, MovieVideo movieVideo) {

        Uri inAppUri = constructVideoInAppUri(movieVideo);
        if (null == inAppUri) {
            return false;
        }

        Intent playInAppIntent = new Intent(Intent.ACTION_VIEW, inAppUri);
        if (null != playInAppIntent.resolveActivity(context.getPackageManager())) {
            Log.d(TAG, "playVideoInApp: playing in app: " + inAppUri.toString());
            context.startActivity(playInAppIntent);
            return true;
        }

        return false;
    }

    public static void shareMovieVideo(Context context, MovieData movieData,
            MovieVideo movieVideo) {

        //  check if we know the given site
        if (!isVideoSiteKnown(movieVideo)) {
            String errorMessage = String.format(
                    context.getString(R.string.error_message_video_site_unknown_format_string),
                    movieVideo.getSite());
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        Uri inBrowserUri = constructVideoInBrowserUri(movieVideo);

        if (null == inBrowserUri) {
            String errorMessage = String.format(
                    context.getString(R.string.error_message_cannot_construct_video_web_video_url),
                    movieVideo.getSite());
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        String message = String.format(
                context.getString(R.string.share_movie_video_message_format_string),
                movieData.getTitle(),
                movieVideo.getType().toLowerCase(),
                movieVideo.getSite(),
                inBrowserUri.toString()
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(SHARE_ACTION_MIME_TYPE);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);

        if (null != shareIntent.resolveActivity(context.getPackageManager())) {
            context.startActivity(shareIntent);
        } else {
            Toast.makeText(context,
                    context.getString(R.string.error_message_no_app_found_to_share_video),
                    Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * method to get movie ID
     *
     * @return is the movie ID the video belongs to
     */
    public long getMovieId() {
        return mMovieId;
    }

    /**
     * method to set movie ID
     *
     * @param mMovieId is the ID of the movie this video belongs to
     */
    public void setMovieId(long mMovieId) {
        this.mMovieId = mMovieId;
    }

    /**
     * method to get video ID
     *
     * @return the video ID
     */
    public String getVideoId() {
        return mVideoId;
    }

    /**
     * method to set video ID
     *
     * @param mVideoId is the video ID
     */
    public void setVideoId(String mVideoId) {
        this.mVideoId = mVideoId;
    }

    /**
     * method to get the ISO 639 attribute of the video
     *
     * @return ISO 639 attribute of video
     */
    public String getIso639() {
        return mIso639;
    }

    /**
     * method to set the ISO 639 attribute of the video
     */
    public void setIso639(String iso639) {
        this.mIso639 = iso639;
    }

    /**
     * method to get ISO 3166 attribute of the video
     *
     * @return ISO 3166 attribute
     */
    public String getIso3166() {
        return mIso3166;
    }

    /**
     * method to set ISO 3166 attribute of the video
     *
     * @param mIso3166 is the ISO 3166 attribute
     */
    public void setIso3166(String mIso3166) {
        this.mIso3166 = mIso3166;
    }

    /**
     * method to get the key that identifies the given video at the site the video is
     * available at, for instance, the v= parameter at YouTube
     *
     * @return video key to its site
     */
    public String getKey() {
        return mKey;
    }

    /**
     * method to get the key that identifies the given video at the site the video is
     * available at, for instance, the v= parameter at YouTube
     *
     * @param mKey is the movie key
     */
    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    /**
     * method to get video name (title if you like)
     *
     * @return video name
     */
    public String getName() {
        return mName;
    }

    /**
     * method to get video name (title if you like)
     *
     * @param mName is the video name
     */
    public void setName(String mName) {
        this.mName = mName;
    }

    /**
     * method to get video site, for example: YouTube
     *
     * @return video site
     */
    public String getSite() {
        return mSite;
    }

    /**
     * method to get video site, for example: YouTube
     *
     * @param mSite is the video site
     */
    public void setSite(String mSite) {
        this.mSite = mSite;
    }

    /**
     * method to get video vertical resolution as a single integer, for instance, 1080
     *
     * @return video vertical resolution
     */
    public int getSize() {
        return size;
    }

    /**
     * method to set video vertical resolution as a single integer, for instance, 1080
     *
     * @param size is the vertical resolution
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * method to get the type of the video, for instance Trailer, Teaser etc.
     *
     * @return type of the video
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        dest.writeString(mVideoId);
        dest.writeString(mIso639);
        dest.writeString(mIso3166);
        dest.writeString(mKey);
        dest.writeString(mName);
        dest.writeInt(size);
        dest.writeString(type);
    }

    @Override
    public String toString() {
        return "MovieVideo{" +
                "mMovieId=" + mMovieId +
                ", mVideoId='" + mVideoId + '\'' +
                ", mName='" + mName + '\'' +
                ", mSite='" + mSite + '\'' +
                '}';
    }
}
