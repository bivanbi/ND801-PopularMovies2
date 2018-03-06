package com.example.android.p022popularmovies2;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
 *          - clicking a poster would fire an intent to DetailActivity to display various movie attributes
 *          <p>
 *          this is our "main" class that includes
 *          - asynctask data loading
 *          - click handling click on movie list items
 *          - menu events - spinner item select
 */
public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        AdapterView.OnItemSelectedListener, LoaderManager.LoaderCallbacks<ArrayList<MovieData>> {

    private static final String TAG = MainActivity.class.getSimpleName();

    //  extra name to send MovieData object with when calling DetailActivity
    private static final String INTENT_EXTRA_NAME_MOVIEDATA = "MOVIE_DATA";
    private static final String LOADER_ARGUMENT_SORT_BY = "SORT_BY";
    private static final String SORT_BY_SPINNER_STATE = "SORT_BY_SPINNER";

    private static final int MOVIEDATA_LOADER_ID = 1;

    //  movie list views TODO ButterKnife
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

    /**
     * this is the main entry point to our activity
     * <p>
     * TODO Stage 2: restore instance state to maintain activity persistence across lifecycle events
     *
     * @param savedInstanceState is the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (null != savedInstanceState) {
            mSortBySpinnerIndex = savedInstanceState.getInt(SORT_BY_SPINNER_STATE);
        }

        String sortBy =
                getResources().getStringArray(
                        R.array.themoviedb_sort_by_query_values)[mSortBySpinnerIndex];


        //  TODO if favourites are shown, we should continue without network, as they are cached
        //  if not connected to network, display error to let user know
        mIsConnectedToNetwork = NetworkUtils.isConnectedToNetwork(this);


        //  TODO nice to have: have an overlay button showing warning - no network

        //  do the hard work: load and display movie list data

        //  TODO use content provider, probably for the whole project
        //ContentResolver resolver = getContentResolver();
        //Cursor cursor = resolver.query(uri, null, null, null, null);
        /*
        if (!mIsConnectedToNetwork && !sortBy.equals(getString(R.string
        .themoviedb_source_favourites))) {
            fallbackToFavourites();
        } else {
            mNoNetworkLoadFavouriteLayout.setVisibility(View.INVISIBLE);
            Bundle args = new Bundle();
            args.putString(LOADER_ARGUMENT_SORT_BY, sortBy);
            getLoaderManager().initLoader(MOVIEDATA_LOADER_ID, args, this);
        }*/

        loadMovieData(sortBy);

        //  int spanCount = getResources().getInteger(R.integer.grid_layout_column_span);
        //  layout manager to display movie poster grid
        GridLayoutManager layoutManager = DisplayUtilities.getOptimalGridLayoutManager(this,
                getResources().getInteger(R.integer.grid_layout_target_pixel_width),
                getResources().getInteger(R.integer.grid_layout_minimum_number_of_columns));

        //  bind our layoutmanager to our recyclerview
        mRecyclerView.setLayoutManager(layoutManager);

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SORT_BY_SPINNER_STATE, mSortBySpinnerIndex);
    }

    /**
     * method to initiate data loading and make responsible view visible
     *
     * @param sortBy    sort by popularity or user rating
     */
    private void loadMovieData(String sortBy) {
        mIsConnectedToNetwork = NetworkUtils.isConnectedToNetwork(this);

        if (!mIsConnectedToNetwork && !sortBy.equals(
                getString(R.string.themoviedb_source_favourites))) {
            fallbackToFavourites();
        } else {
            mNoNetworkLoadFavouriteLayout.setVisibility(View.INVISIBLE);
            Bundle args = new Bundle();
            args.putString(LOADER_ARGUMENT_SORT_BY, sortBy);
            getLoaderManager().restartLoader(MOVIEDATA_LOADER_ID, args, this);
        }
    }

    /**
     * method to make display view visible and error message invisible
     */
    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * method to make display view invisible and error message visible
     */
    private void showErrorMessage(String message) {
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
        mSortBySpinnerIndex = position;
        String sortBy = getResources().getStringArray(R.array.themoviedb_sort_by_query_values)[position];
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


    @Override
    public Loader<ArrayList<MovieData>> onCreateLoader(int id, Bundle args) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        return new MovieDataLoader(this, args.getString(LOADER_ARGUMENT_SORT_BY));
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieData>> loader,
            ArrayList<MovieData> movieDataArrayList) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (null != movieDataArrayList) {
            showMovieDataView();
            mMovieAdapter.setMovieDataArrayList(movieDataArrayList);
        } else {
            showErrorMessage(getResources().getString(R.string.main_error_message));
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieData>> loader) {
        mMovieAdapter.setMovieDataArrayList(null);
    }

    public class NoNetworkFallbackToFavouritesOnClickListener implements View.OnClickListener {
        private final String TAG =
                NoNetworkFallbackToFavouritesOnClickListener.class.getSimpleName();

        @Override
        public void onClick(View v) {
            //  TODO in case of network inavailability, offer the user the option to view favourites
            //  TODO network error message is still shown, need to work through movieloader
            int favouriteSpinnerIndex = mSpinnerArrayAdapter.getPosition(
                    getResources().getString(R.string.themoviedb_favourites));
            Log.d(TAG, "onClick: favourite spinner index: " + favouriteSpinnerIndex);
            mSortBySpinner.setSelection(favouriteSpinnerIndex);
            mSortBySpinnerIndex = favouriteSpinnerIndex;
            //  TODO hide dialog if button is clicked
            mNoNetworkLoadFavouriteLayout.setVisibility(View.INVISIBLE);
            //  TODO check if invisible views still receive onclick events
        }
    }

}
