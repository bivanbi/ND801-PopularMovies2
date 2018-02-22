package com.example.android.p021popularmovies1;

import android.os.Parcel;
import android.os.Parcelable;

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
 *          class to hold movie attributes like Title, Release Date, synopsis etc.
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
                    movieData.setTitle(source.readString());
                    movieData.setOriginalTitle(source.readString());
                    movieData.setPosterUrl(source.readString());
                    movieData.setOverview(source.readString());
                    movieData.setPopularity(source.readDouble());
                    movieData.setVoteAverage(source.readDouble());
                    movieData.setReleaseDate(source.readString());

                    return movieData;
                }

                @Override
                public MovieData[] newArray(int size) {
                    return new MovieData[0];
                }
            };
    private String mTitle;
    private String mOriginalTitle;
    private String mPosterUrl;
    private String mOverview;
    private double mPopularity;
    private double mVoteAverage;
    private String mReleaseDate;

    /**
     * a very basic constructor, practical when adding movie attributes one by one
     */
    public MovieData() {
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
    public String getPosterUrl() {
        return mPosterUrl;
    }

    /**
     * setter method to movie poster relative URL
     *
     * @param mPosterUrl String with movie poster relative URL
     */
    public void setPosterUrl(String mPosterUrl) {
        this.mPosterUrl = mPosterUrl;
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
     * method to get MovieData attributes as a single string
     *
     * @return String with moviedata attributes
     */
    @Override
    public String toString() {
        return "MovieData{" +
                "mTitle='" + mTitle + '\'' +
                ", mOriginalTitle='" + mOriginalTitle + '\'' +
                ", mPosterUrl='" + mPosterUrl + '\'' +
                ", mOverview='" + mOverview + '\'' +
                ", mPopularity=" + mPopularity +
                ", mVoteAverage=" + mVoteAverage +
                ", mReleaseDate='" + mReleaseDate + '\'' +
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
        dest.writeString(mTitle);
        dest.writeString(mOriginalTitle);
        dest.writeString(mPosterUrl);
        dest.writeString(mOverview);
        dest.writeDouble(mPopularity);
        dest.writeDouble(mVoteAverage);
        dest.writeString(mReleaseDate);
    }
}
