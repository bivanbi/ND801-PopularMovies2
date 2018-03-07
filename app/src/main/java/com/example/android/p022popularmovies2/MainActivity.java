package com.example.android.p022popularmovies2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.p022popularmovies2.adapters.MovieAdapter;
import com.example.android.p022popularmovies2.data.MovieData;
import com.example.android.p022popularmovies2.loaders.MovieDataLoader;
import com.example.android.p022popularmovies2.utilities.DisplayUtilities;
import com.example.android.p022popularmovies2.utilities.NetworkUtils;

import java.util.ArrayList;

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
 *          - using ButterKnife as instructed by reviewers
 *          <p>
 *          Main Activity:
 *          - display popular / highest voted movie posters
 *          - clicking a poster would fire an intent to DetailActivity to display various movie
 *          attributes
 *          <p>
 *          this is our "main" class that includes
 *          - asynctask data loading
 *          - click handling click on movie list items
 *          - menu events - spinner item select
 */
public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        AdapterView.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    //  extra name to send MovieData object with when calling DetailActivity
    private static final String INTENT_EXTRA_NAME_MOVIEDATA = "MOVIE_DATA";
    private static final String LOADER_ARGUMENT_SORT_BY = "SORT_BY";
    private static final String SORT_BY_SPINNER_STATE = "SORT_BY_SPINNER";
    private static final String LAYOUT_MANAGER_STATE = "LAYOUT_MANAGER_STATE";

    private static final int MOVIEDATA_LOADER_ID = 1;

    @BindView(R.id.recyclerview_movielist)
    RecyclerView mRecyclerView;
    @BindView(R.id.textview_main_error_message)
    TextView mErrorMessageDisplay;
    @BindView(R.id.progressbar_main_loading)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.layout_no_network_load_favourite)
    View mNoNetworkLoadFavouriteLayout;
    @BindView(R.id.button_main_no_network_load_favourites)
    Button mNoNetworkLoadFavouriteButton;
    Spinner mSortBySpinner;
    private MovieAdapter mMovieAdapter;
    private int mSortBySpinnerIndex = 0;
    private ArrayAdapter<CharSequence> mSpinnerArrayAdapter;
    private boolean mIsConnectedToNetwork = false;

    private Parcelable mLayoutManagerRestoreState = null;
    private boolean mIsSpinnerChanged = false;

    /**
     * this is the main entry point to our activity
     * <p>
     *
     * @param savedInstanceState is the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate called");

        ButterKnife.bind(this);

        //  if not connected to network, display error to let user know
        mIsConnectedToNetwork = NetworkUtils.isConnectedToNetwork(this);


        GridLayoutManager gridLayoutManager = DisplayUtilities.getOptimalGridLayoutManager(this,
                getResources().getInteger(R.integer.grid_layout_target_pixel_width),
                getResources().getInteger(R.integer.grid_layout_minimum_number_of_columns));

        if (null != savedInstanceState) {
            Log.d(TAG, "onCreate: restoring state");
            mSortBySpinnerIndex = savedInstanceState.getInt(SORT_BY_SPINNER_STATE);
            //  actual state restoration will take place at onLoadFinished()
            mLayoutManagerRestoreState = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE);
        }

        //  bind our layoutmanager to our recyclerview
        mRecyclerView.setLayoutManager(gridLayoutManager);

        int nonFavouriteBackgroundColor =
                ContextCompat.getColor(this, R.color.movieListItemTextBackground);

        int favouriteBackgroundColor =
                ContextCompat.getColor(this, R.color.favouriteMovieListItemTextBackground);

        //  bind click handler to recyclerview so user may interact with movie posters
        mMovieAdapter = new MovieAdapter(this, nonFavouriteBackgroundColor,
                favouriteBackgroundColor);
        mRecyclerView.setAdapter(mMovieAdapter);
    }


    private void fallbackToFavourites() {
        Log.d(TAG, "fallbackToFavourites");
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mNoNetworkLoadFavouriteLayout.setVisibility(View.VISIBLE);
        mNoNetworkLoadFavouriteButton.setOnClickListener(
                new NoNetworkFallbackToFavouritesOnClickListener());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: saving state: spinnerindex: " + mSortBySpinnerIndex);
        outState.putInt(SORT_BY_SPINNER_STATE, mSortBySpinnerIndex);
        outState.putParcelable(LAYOUT_MANAGER_STATE,
                mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    /**
     * method to initiate data loading and make responsible view visible
     *
     * @param sortBy sort by popularity or user rating
     */
    private void loadMovieData(String sortBy) {
        Log.d(TAG, "loadMovieData: called, sortBy: " + sortBy);
        mIsConnectedToNetwork = NetworkUtils.isConnectedToNetwork(this);

        if (!mIsConnectedToNetwork && !sortBy.equals(
                getString(R.string.themoviedb_source_favourites))) {
            Log.d(TAG, "loadMovieData: fallback to favourites");
            fallbackToFavourites();
        } else {
            Log.d(TAG, "loadMovieData: normal load");
            mNoNetworkLoadFavouriteLayout.setVisibility(View.INVISIBLE);
            Bundle args = new Bundle();
            args.putString(LOADER_ARGUMENT_SORT_BY, sortBy);
            //getLoaderManager().initLoader(MOVIEDATA_LOADER_ID, args, this);
            getSupportLoaderManager()
                    .restartLoader(MOVIEDATA_LOADER_ID, args, new MovieLoaderCallbacks());
        }
    }

    /**
     * method to make display view visible and error message invisible
     */
    private void showMovieDataView() {
        Log.d(TAG, "showMovieDataView called");
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * method to make display view invisible and error message visible
     */
    private void showErrorMessage(String message) {
        Log.d(TAG, "showErrorMessage called");
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setText(message);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * method to handle clicks on movie list items. Will start DetailActivity with a single extra,
     * the MovieData object bound clicked view
     *
     * @param movieData is the movie that has been clicked
     */
    @Override
    public void onClick(MovieData movieData) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(INTENT_EXTRA_NAME_MOVIEDATA, movieData);
        startActivity(intent);
    }

    /**
     * method to create menus. In Stage 1, it is only a spinner to select move list sorting.
     *
     * @param menu is the Menu resource to work on
     * @return always true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.d(TAG, "onCreateOptionsMenu called");
        //  inflate menu
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem sortByMenuItem = menu.findItem(R.id.spinner_sort_by);
        mSortBySpinner = (Spinner) sortByMenuItem.getActionView();
        mSortBySpinner.setSelection(mSortBySpinnerIndex);

        //  create an ArrayAdapter so our Spinner will have predefined values the user can select
        mSpinnerArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.themoviedb_sort_by_spinner_text,
                android.R.layout.simple_spinner_dropdown_item);

        //  use default Android simple spinner dropdown layout
        mSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //  bind adapter to our spinner
        mSortBySpinner.setAdapter(mSpinnerArrayAdapter);
        //  bind callback method so we will be notified if user selects an item from our Spinner
        mSortBySpinner.setOnItemSelectedListener(this);
        mSortBySpinner.setSelection(mSortBySpinnerIndex);

        //  let onLoadFinished know that spinner has changed so it will reset recyclerview
        //  to position 0
        return true;
    }

    /**
     * method to handel onItemSelected events on our Spinner
     *
     * @param parent   is the AdapterView
     * @param view     is the involved view
     * @param position is the index of the selected item
     * @param id       unused here
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected called");
        mSortBySpinnerIndex = position;
        String sortBy = getResources().getStringArray(
                R.array.themoviedb_sort_by_query_values)[position];
        mIsSpinnerChanged = true;
        loadMovieData(sortBy);
    }

    /**
     * this method will be called if no option is selected in spinner.
     *
     * @param parent is the AdapterView
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    public class MovieLoaderCallbacks implements
            LoaderManager.LoaderCallbacks<ArrayList<MovieData>> {

        private final String TAG = MovieLoaderCallbacks.class.getSimpleName();

        @NonNull
        @Override
        public Loader<ArrayList<MovieData>> onCreateLoader(int id,
                Bundle args) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            Log.d(TAG, "onCreateLoader called, sortBy: " + args.getString(LOADER_ARGUMENT_SORT_BY));
            return new MovieDataLoader(MainActivity.this, args.getString(LOADER_ARGUMENT_SORT_BY));
        }

        @Override
        public void onLoadFinished(
                @NonNull Loader<ArrayList<MovieData>> loader,
                ArrayList<MovieData> movieDataArrayList) {

            Log.d(TAG, "onLoadFinished called, sortBy index: " + mSortBySpinnerIndex
                    + ", data count: " + movieDataArrayList.size());
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            showMovieDataView();
            mMovieAdapter.setMovieDataArrayList(movieDataArrayList);

            if (null != mLayoutManagerRestoreState) {
                Log.d(TAG, "onLoadFinished: restoring loadermanager state");
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mLayoutManagerRestoreState);
                mLayoutManagerRestoreState = null;
            } else {
                Log.d(TAG, "onLoadFinished: scolling to position 0");
                if (mIsSpinnerChanged) {
                    mRecyclerView.getLayoutManager().scrollToPosition(0);
                    mIsSpinnerChanged = false;
                }
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList<MovieData>> loader) {
        }
    }

    public class NoNetworkFallbackToFavouritesOnClickListener implements View.OnClickListener {
        private final String TAG =
                NoNetworkFallbackToFavouritesOnClickListener.class.getSimpleName();

        @Override
        public void onClick(View v) {
            //  in case of network inavailability, offer the user the option to view favourites
            int favouriteSpinnerIndex = mSpinnerArrayAdapter.getPosition(
                    getResources().getString(R.string.themoviedb_favourites));
            Log.d(TAG, "onClick: favourite spinner index: " + favouriteSpinnerIndex);
            mSortBySpinner.setSelection(favouriteSpinnerIndex);
            mSortBySpinnerIndex = favouriteSpinnerIndex;
            //  hide dialog if button is clicked
            mNoNetworkLoadFavouriteLayout.setVisibility(View.INVISIBLE);
        }
    }
}
