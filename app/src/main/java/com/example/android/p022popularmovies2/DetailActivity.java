package com.example.android.p022popularmovies2;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.p022popularmovies2.adapters.ReviewAdapter;
import com.example.android.p022popularmovies2.adapters.VideoAdapter;
import com.example.android.p022popularmovies2.data.MovieContract;
import com.example.android.p022popularmovies2.data.MovieData;
import com.example.android.p022popularmovies2.data.MovieReview;
import com.example.android.p022popularmovies2.data.MovieVideo;
import com.example.android.p022popularmovies2.loaders.MovieReviewLoader;
import com.example.android.p022popularmovies2.loaders.MovieVideoLoader;
import com.example.android.p022popularmovies2.loaders.PosterCacheLoader;
import com.example.android.p022popularmovies2.utilities.NetworkUtils;
import com.example.android.p022popularmovies2.utilities.TheMovieDbUtils;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

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
 *          DetailActivity to display movie poster and various attributes.
 *          <p>
 *          our Activity class that has all the required methods to take care of the movie details
 *          job
 */
public class DetailActivity extends AppCompatActivity implements
        VideoAdapter.VideoAdapterOnClickHandler,
        ReviewAdapter.ReviewAdapterOnClickHandler {
    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final String INTENT_EXTRA_NAME_MOVIEDATA = "MOVIE_DATA";

    private static final String LOADER_ARGUMENT_MOVIE_ID = "MOVIE_ID";
    private static final int MOVIEREVIEW_LOADER_ID = 2;
    private static final int MOVIEVIDEO_LOADER_ID = 3;
    private static final int POSTER_CACHE_LOADER_ID = 5;
    private static final String POSTER_CACHE_LOADER_ARGUMENT_OPERATION = "OPERATION";
    //  cache views where movie attributes will be displayed
    @BindView(R.id.imageview_detail_poster)
    ImageView mMoviePosterImageView;
    @BindView(R.id.textview_detail_title)
    TextView mMovieTitleTextView;
    @BindView(R.id.textview_detail_original_title)
    TextView mMovieOriginalTitleTextView;
    @BindView(R.id.textview_detail_user_rating)
    TextView mMovieUserRatingTextView;
    @BindView(R.id.textview_detail_synopsis)
    TextView mMovieOverviewTextView;
    @BindView(R.id.textview_detail_release_date)
    TextView mMovieReleaseDateTextView;
    @BindView(R.id.imageview_detail_favourite)
    ImageView mMoviePosterFavouriteStar;
    @BindView(R.id.imageview_detail_favourite_mask)
    ImageView mMoviePosterFavouriteMask;
    @BindView(R.id.recyclerview_reviews)
    RecyclerView mReviewsRecyclerView;
    @BindView(R.id.recyclerview_videos)
    RecyclerView mVideosRecyclerView;
    @BindView(R.id.progressbar_detail_poster_loading)
    ProgressBar mPosterLoadingProgressBar;
    @BindView(R.id.progressbar_detail_reviews_loading)
    ProgressBar mReviewsLoadingProgressBar;
    @BindView(R.id.progressbar_detail_video_loading)
    ProgressBar mVideosLoadingProgressBar;
    @BindView(R.id.textview_detail_review_load_error_message)
    TextView mReviewsErrorMessageTextView;
    @BindView(R.id.textview_detail_video_load_error_message)
    TextView mVideosErrorMessageTextView;
    private ReviewAdapter mReviewAdapter;
    private VideoAdapter mVideoAdapter;
    //  the moviedata object with all the required attribute values
    private MovieData mMovieData;

    private View.OnClickListener mToggleFavouriteOnClickListener;

    /**
     * constructor to our detailactivity
     *
     * @param savedInstanceState is the saved instance state if activity is being restored
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);
        //  get and display MovieData from calling intent

        Intent callingIntent = getIntent();
        if (null == callingIntent) {
            Log.e(TAG, "onCreate: received null intent");
            return;
        }
        getMovieDataFromItentExtra(callingIntent);

        showMovieData();
        //  set up layout managers and adapters
        setupLayoutManagers();
        setupLoaders();
        setupToggleFavourite();
    }

    /**
     * helper method to get movie data from intent extra, null on not found
     * @param callingIntent on
     */
    private void getMovieDataFromItentExtra(Intent callingIntent) {
        if (!callingIntent.hasExtra(INTENT_EXTRA_NAME_MOVIEDATA)) {
            Log.e(TAG,
                    "onCreate: received intent has no extra named " + INTENT_EXTRA_NAME_MOVIEDATA);
        }

        mMovieData = callingIntent.getParcelableExtra(INTENT_EXTRA_NAME_MOVIEDATA);
    }

    /**
     * method to check for network connectivity and start loading of reviews and videos,
     * and display message to let the user know if there is no network connectivity
     */
    private void setupLoaders() {
        LoaderManager loaderManager = getLoaderManager();
        Bundle args = new Bundle();
        args.putLong(LOADER_ARGUMENT_MOVIE_ID, mMovieData.getMovieId());

        if (NetworkUtils.isConnectedToNetwork(this)) {
            loaderManager.initLoader(MOVIEVIDEO_LOADER_ID, args,
                    new MovieVideoLoaderCallbacks());

            loaderManager.initLoader(MOVIEREVIEW_LOADER_ID, args,
                    new MovieReviewLoaderCallbacks());
        } else {
            //  notify user about network not being accessible
            mVideosRecyclerView.setVisibility(View.GONE);
            mVideosErrorMessageTextView.setText(getString(R.string.error_message_no_network));
            mVideosErrorMessageTextView.setVisibility(View.VISIBLE);

            mReviewsRecyclerView.setVisibility(View.GONE);
            mReviewsErrorMessageTextView.setText(getString(R.string.error_message_no_network));
            mReviewsErrorMessageTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * method to setup layout managers for reviews and videos
     */
    private void setupLayoutManagers() {
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mReviewAdapter = new ReviewAdapter(this,
                getString(R.string.movie_details_review_author_format_string));
        mReviewsRecyclerView.setAdapter(mReviewAdapter);

        mVideosRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mVideoAdapter = new VideoAdapter(this, new ShareClickDispatcher());
        mVideosRecyclerView.setAdapter(mVideoAdapter);
    }

    /**
     * setup onclick listeners to receive user actions on Favourite icon
     */
    private void setupToggleFavourite() {
        updateFavouriteView();
        if (null == mToggleFavouriteOnClickListener) {
            mToggleFavouriteOnClickListener = new ToggleFavouriteOnClickHandler();
        }
        mMoviePosterFavouriteStar.setOnClickListener(mToggleFavouriteOnClickListener);
        mMoviePosterFavouriteMask.setOnClickListener(mToggleFavouriteOnClickListener);
    }
    /**
     * method to actually display movie details. Takes no argument as required data is already
     * present in global variables.
     */
    private void showMovieData() {
        mPosterLoadingProgressBar.setVisibility(View.VISIBLE);
        TheMovieDbUtils.loadMovieImageIntoView(DetailActivity.this, mMoviePosterImageView,
                mMovieData,
                getResources().getString(R.string.themoviedb_image_resolution_large),
                new PosterLoadIntoViewCallbacks());

        //  fill textviews
        mMovieTitleTextView.setText(mMovieData.getTitle());
        mMovieOriginalTitleTextView.setText(mMovieData.getOriginalTitle());

        //  display float values with correct locale, eg. 5.6 vs 5,6
        Locale locale = getResources().getConfiguration().locale;
        mMovieUserRatingTextView.setText(
                String.format(locale, "%.1f", mMovieData.getVoteAverage()));
        mMovieOverviewTextView.setText(mMovieData.getOverview());

        //  display year only, we are rarely interested in the exact release date, and it saves room
        //  should release date be missing from the data, getReleaseYearFromReleaseDate will take
        //  care of returning a placeholder text
        mMovieReleaseDateTextView.setText(
                TheMovieDbUtils.getReleaseYearFromReleaseDate(this, mMovieData.getReleaseDate()));
    }

    /**
     * method to handle clicks on movie review list items.
     *
     * @param movieReview is the review that has been clicked
     */
    @Override
    public void onClick(MovieReview movieReview) {
        //  for future development, for instance, expand / collapse on click
    }

    /**
     * method to handle clicks on movie video list items.
     *
     * @param movieVideo is the video that has been clicked
     */
    @Override
    public void onClick(MovieVideo movieVideo) {
        MovieVideo.playMovieVideo(this, movieVideo);
    }

    /**
     * update views to reflect actual status of isFavourite of actual movie data
     */
    public void updateFavouriteView() {
        if (mMovieData.isFavourite()) {
            mMoviePosterFavouriteStar.setImageResource(android.R.drawable.star_big_on);
            mMoviePosterFavouriteStar.setContentDescription(
                    getResources().getString(R.string.click_here_to_remove_movie_from_favourites));
            mMoviePosterFavouriteMask.setContentDescription(
                    getResources().getString(R.string.click_here_to_remove_movie_from_favourites));
        } else {
            mMoviePosterFavouriteStar.setImageResource(android.R.drawable.star_big_off);
            mMoviePosterFavouriteStar.setContentDescription(
                    getResources().getString(R.string.click_here_to_add_movie_to_favourites));
            mMoviePosterFavouriteMask.setContentDescription(
                    getResources().getString(R.string.click_here_to_add_movie_to_favourites));
        }
    }

    /**
     * orchestrator method to remove movie from favourites, incl. deleting movie poster from cache
     */
    private void removeFavourite() {
        int result = removeMovieFromSql();
        Log.d(TAG, "removeFavourite: removeMovieFromSql returned " + result);

        Toast.makeText(DetailActivity.this,
                getResources().getString(R.string.toast_removed_from_favourites),
                Toast.LENGTH_SHORT).show();

        LoaderManager loaderManager = getLoaderManager();
        Bundle args = new Bundle();
        args.putInt(POSTER_CACHE_LOADER_ARGUMENT_OPERATION,
                PosterCacheLoader.OPERATION_REMOVE_FROM_CACHE);

        Log.d(TAG, "removeFavourite: calling poster cache loader with operation "
                + args.getInt(POSTER_CACHE_LOADER_ARGUMENT_OPERATION));

        loaderManager.restartLoader(POSTER_CACHE_LOADER_ID, args,
                new PosterCacheLoaderCallbacks());

        mMovieData.setFavourite(false);
        updateFavouriteView();
    }

    /**
     * orchestrator method to add movie to favourites, incl. caching movie poster
     */
    private void addFavourite() {
        if (null != addMovieToSql()) {
            Toast.makeText(DetailActivity.this,
                    getResources().getString(R.string.toast_marked_as_favourite),
                    Toast.LENGTH_SHORT).show();

            //  if there is movie poster, save it to cache
            if (null != mMovieData.getPosterPath()) {
                LoaderManager loaderManager = getLoaderManager();
                Bundle args = new Bundle();
                args.putInt(POSTER_CACHE_LOADER_ARGUMENT_OPERATION,
                        PosterCacheLoader.OPERATION_ADD_TO_CACHE);

                Log.d(TAG, "addFavourite: calling poster cache loader with operation "
                        + args.getInt(POSTER_CACHE_LOADER_ARGUMENT_OPERATION));

                loaderManager.restartLoader(POSTER_CACHE_LOADER_ID, args,
                        new PosterCacheLoaderCallbacks());
            }

            mMovieData.setFavourite(true);
            updateFavouriteView();
        }
    }

    /**
     * method to add movie to favourite SQLite database
     * @return Uri pointing to newly created entry, null on failure
     */
    private Uri addMovieToSql() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.FavouriteMovieEntry._ID, mMovieData.getMovieId());
        contentValues.put(MovieContract.FavouriteMovieEntry.COLUMN_NAME_TITLE,
                mMovieData.getTitle());
        contentValues.put(MovieContract.FavouriteMovieEntry.COLUMN_NAME_ORIGINAL_TITLE,
                mMovieData.getOriginalTitle());
        contentValues.put(MovieContract.FavouriteMovieEntry.COLUMN_NAME_OVERVIEW,
                mMovieData.getOverview());
        contentValues.put(MovieContract.FavouriteMovieEntry.COLUMN_NAME_RELEASE_DATE,
                mMovieData.getReleaseDate());
        contentValues.put(MovieContract.FavouriteMovieEntry.COLUMN_NAME_POSTER_PATH,
                mMovieData.getPosterPath());
        contentValues.put(MovieContract.FavouriteMovieEntry.COLUMN_NAME_POPULARITY,
                mMovieData.getPopularity());
        contentValues.put(MovieContract.FavouriteMovieEntry.COLUMN_NAME_VOTE_AVERAGE,
                mMovieData.getVoteAverage());
        return getContentResolver().insert(
                MovieContract.FavouriteMovieEntry.CONTENT_URI, contentValues);
    }

    /**
     * method to remove from SQLight favourites
     * @return number of affected rows, should be 1 if everything works as expected
     */
    private int removeMovieFromSql() {
        return getContentResolver()
                .delete(ContentUris.withAppendedId(
                        MovieContract.FavouriteMovieEntry.CONTENT_URI,
                        mMovieData.getMovieId()),
                        null,
                        null);
    }

    /**
     * class to receive callbacks from review loader
     */
    public class MovieReviewLoaderCallbacks implements
            LoaderManager.LoaderCallbacks<ArrayList<MovieReview>> {
        private String TAG = MovieReviewLoaderCallbacks.class.getSimpleName();

        MovieReviewLoaderCallbacks() {
        }

        @Override
        public Loader<ArrayList<MovieReview>> onCreateLoader(int id, Bundle args) {
            //mLoadingIndicator.setVisibility(View.VISIBLE);
            mReviewsLoadingProgressBar.setVisibility(View.VISIBLE);
            return new MovieReviewLoader(DetailActivity.this,
                    args.getLong(LOADER_ARGUMENT_MOVIE_ID));
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<MovieReview>> loader,
                ArrayList<MovieReview> movieReviewArrayList) {
            mReviewsLoadingProgressBar.setVisibility(View.INVISIBLE);
            //mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (null != movieReviewArrayList) {
                mReviewAdapter.setMovieReviewArrayList(movieReviewArrayList);
            } else {
                Log.e(TAG, "onLoadFinished() called with null movieReviewArrayList");
                mReviewsErrorMessageTextView.setText(
                        getString(R.string.details_reviews_error_message));
                mReviewsErrorMessageTextView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<MovieReview>> loader) {
            mReviewAdapter.setMovieReviewArrayList(null);
        }
    }

    /**
     * class to receive callbacks from movie video loader
     */
    public class MovieVideoLoaderCallbacks implements
            LoaderManager.LoaderCallbacks<ArrayList<MovieVideo>> {

        private final String TAG = MovieVideoLoaderCallbacks.class.getSimpleName();

        MovieVideoLoaderCallbacks() {
        }

        @Override
        public Loader<ArrayList<MovieVideo>> onCreateLoader(int id, Bundle args) {
            mVideosLoadingProgressBar.setVisibility(View.VISIBLE);
            return new MovieVideoLoader(DetailActivity.this,
                    args.getLong(LOADER_ARGUMENT_MOVIE_ID));
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<MovieVideo>> loader,
                ArrayList<MovieVideo> movieVideoArrayList) {

            mVideosLoadingProgressBar.setVisibility(View.INVISIBLE);
            Log.i(TAG, "finished loading movie videos");
            //mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (null != movieVideoArrayList) {
                Log.i(TAG,
                        "video arraylist contains " + movieVideoArrayList.size() + "elements");
                //showMovieDataView();
                mVideoAdapter.setMovieVideoArrayList(movieVideoArrayList);
            } else {
                Log.e(TAG, "onLoadFinished() called with null movieVideoArrayList");
                //showErrorMessage(getResources().getString(R.string.main_error_message));
                mVideosErrorMessageTextView.setText(
                        getString(R.string.details_videos_error_message));
                mVideosErrorMessageTextView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<MovieVideo>> loader) {
            mVideoAdapter.setMovieVideoArrayList(null);
        }
    }

    /**
     * class to receive onclick events from favourite button
     */
    public class ToggleFavouriteOnClickHandler implements View.OnClickListener {
        ToggleFavouriteOnClickHandler() {
        }

        @Override
        public void onClick(View v) {
            if (mMovieData.isFavourite()) {
                removeFavourite();
            } else {
                addFavourite();
            }
        }
    }

    /**
     * class to receive callbacks from poster cache loader
     */
    public class PosterCacheLoaderCallbacks implements
            LoaderManager.LoaderCallbacks<Integer> {

        private final String TAG = PosterCacheLoaderCallbacks.class.getSimpleName();
        private int mOperation;

        PosterCacheLoaderCallbacks() {
        }

        @Override
        public Loader<Integer> onCreateLoader(int id, Bundle args) {
            mOperation = args.getInt(POSTER_CACHE_LOADER_ARGUMENT_OPERATION);
            Log.d(TAG, "onCreateLoader called with operation " + mOperation);

            return new PosterCacheLoader(DetailActivity.this, mOperation, mMovieData);
        }

        @Override
        public void onLoadFinished(Loader<Integer> loader,
                Integer result) {
            Log.i(TAG, "finished loading movie videos");
            if (PosterCacheLoader.RESULT_CODE_SUCCESS == result) {
                Log.i(TAG, " cache operation (" + mOperation + ") success: "
                        + mMovieData.getTitle());
            } else {
                Log.e(TAG, " cache operation (" + mOperation + ") failed, code: "
                        + result + ", title: " + mMovieData.getTitle());
            }
        }

        @Override
        public void onLoaderReset(Loader<Integer> loader) {
        }
    }

    /**
     * callback class to receive success/failure callbacks from Picasso
     */
    public class PosterLoadIntoViewCallbacks
            implements TheMovieDbUtils.LoadMovieImageIntoViewCallback {
        @Override
        public void onSuccess(View view, MovieData movieData, String imageSize) {
            mPosterLoadingProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onError(View view, MovieData movieData, String imageSize) {
            mPosterLoadingProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(DetailActivity.this,
                    getString(R.string.details_poster_load_failed), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * interface implementation to receive and relay clicks on Share button on movie video
     */
    public class ShareClickDispatcher {
        ShareClickDispatcher() {
        }

        public void onClick(MovieVideo movieVideo) {
            MovieVideo.shareMovieVideo(DetailActivity.this, mMovieData, movieVideo);
        }
    }
}
