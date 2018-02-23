package com.example.android.p021popularmovies1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.p021popularmovies1.utilities.TheMovieDbUtils;

import java.util.Locale;

/**
 * Udacity Android Developer Nanodegree - Project Popular Movies stage 1
 *
 * @author balazs.lengyak@gmail.com
 * @version 1.1
 *          <p>
 *          - Inspired by dozens of online found examples (both visual and code design),
 *          - Might contain traces of code from official Android Developer documentation and
 *          default templates from Android Studio
 *          <p>
 *          DetailActivity to display movie poster and various attributes.
 *          TODO Stage 2: display links to trailer videos, reviews etc.
 *          TODO Stage 2: - optional: Implement sharing functionality to allow the user to share the first trailer’s
 *          TODO Stage 2: - optional: YouTube URL from the movie details screen.
 *          <p>
 *          our Activity class that has all the required methods to take care of the movie details job
 */
public class DetailActivity extends AppCompatActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final String INTENT_EXTRA_NAME_MOVIEDATA = "MOVIE_DATA";

    //  cache views where movie attributes will be displayed
    private ImageView mMoviePosterImageView;
    private TextView mMovieTitleTextView;
    private TextView mMovieOriginalTitleTextView;
    private TextView mMovieUserRatingTextView;
    private TextView mMovieOverviewTextView;
    private TextView mMovieReleaseDateTextView;

    //  the moviedata object with all the required attribute values
    private MovieData mMovieData;

    //  TODO Stage 2: restore Activity state after lifecycle events
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mMoviePosterImageView = findViewById(R.id.imageview_detail_poster);
        mMovieTitleTextView = findViewById(R.id.textview_detail_title);
        mMovieOriginalTitleTextView = findViewById(R.id.textview_detail_original_title);
        mMovieUserRatingTextView = findViewById(R.id.textview_detail_user_rating);
        mMovieOverviewTextView = findViewById(R.id.textview_detail_overview);
        mMovieReleaseDateTextView = findViewById(R.id.textview_detail_release_date);

        //  get and display MovieData from calling intent
        Intent callingIntent = getIntent();
        if (null != callingIntent) {
            if (callingIntent.hasExtra(INTENT_EXTRA_NAME_MOVIEDATA)) {
                mMovieData = callingIntent.getParcelableExtra(INTENT_EXTRA_NAME_MOVIEDATA);
                Log.d(TAG, "onCreate: received data: " + mMovieData.toString());
                showMovieData();
            } else {
                Log.e(TAG, "onCreate: received intent has no extra named " + INTENT_EXTRA_NAME_MOVIEDATA);
            }
        } else {
            Log.e(TAG, "onCreate: received null intent");
        }
    }

    /**
     * method to actually display movie details. Takes no argument as required data is already
     * present in global variables.
     */
    private void showMovieData() {
        TheMovieDbUtils.loadMovieImageIntoView(DetailActivity.this, mMoviePosterImageView,
                mMovieData.getPosterUrl(),
                getResources().getString(R.string.themoviedb_image_resolution_large));

        //  fill textviews
        mMovieTitleTextView.setText(mMovieData.getTitle());
        mMovieOriginalTitleTextView.setText(mMovieData.getOriginalTitle());

        //  display float values with correct locale, eg. 5.6 vs 5,6
        Locale locale = getResources().getConfiguration().locale;
        mMovieUserRatingTextView.setText(String.format(locale, "%.1f", mMovieData.getVoteAverage()));
        mMovieOverviewTextView.setText(mMovieData.getOverview());

        //  display year only, we are rarely interested in the exact release date, and it saves room
        //  should release date be missing from the data, getReleaseYearFromReleaseDate will take
        //  care of returning a placeholder text
        mMovieReleaseDateTextView.setText(TheMovieDbUtils.getReleaseYearFromReleaseDate(this, mMovieData.getReleaseDate()));
    }
}
