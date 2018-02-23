package com.example.android.p021popularmovies1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.p021popularmovies1.utilities.DisplayUtilities;
import com.example.android.p021popularmovies1.utilities.TheMovieDbUtils;

import java.util.ArrayList;

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
 *          Main Activity:
 *          - display popular / highest voted movie posters
 *          - clicking a poster would fire an intent to DetailActivity to display various movie attributes
 *          <p>
 *          TODO Stage 2: - implement AsyncTaskLoader and CursorLoader
 *          TODO Stage 2: - implement SQL table to hold favourite movies
 *          TODO Stage 2:  - implement Content Provider
 *          TODO Stage 2:  - implement lifecycle data persistence
 *          <p>
 *          TODO Stage 2:  - extend MovieData class with ID field
 *          TODO Stage 2:  - extend details screen with videos and links to reviews, "add/remove favourite"
 *          TODO Stage 2:  - add option to main screen to show favourites
 *          TODO Stage 2:  - extend MovieData class with ID field
 *          TODO Stage 2:  - load movie details with links to trailers and reviews
 *          <p>
 *          TODO Stage 2:  - optional: extend main screen to add a star to favourited movies
 *          TODO Stage 2:  - optional: Extend the favorites ContentProvider to store favorite movie data for offline viewing
 *          TODO Stage 2:  - optional: Extend ContentProvider to store posters for offline viewing
 *          TODO Stage 2:  - optional: Implement sharing functionality to allow the user to share the first trailer’s
 *          TODO Stage 2:  - optional: YouTube URL from the movie details screen.
 *          <p>
 *          this is our "main" class that includes
 *          - asynctask data loading
 *          - click handling click on movie list items
 *          - menu events - spinner item select
 */
public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieAdapterOnClickHandler, AdapterView.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    //  extra name to send MovieData object with when calling DetailActivity
    private static final String INTENT_EXTRA_NAME_MOVIEDATA = "MOVIE_DATA";

    //  movie list views
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

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

        mRecyclerView = findViewById(R.id.recyclerview_movielist);
        mErrorMessageDisplay = findViewById(R.id.textview_main_error_message);
        mLoadingIndicator = findViewById(R.id.progressbar_main_loading);

        //  TODO Stage 2: implement some sort of calculation for optimal column count based on actual screen
        //  TODO Stage 2: size. Android's built-in small-normal-large etc. classification is not enough.
        //  int spanCount = getResources().getInteger(R.integer.grid_layout_column_span);
        //  layout manager to display movie poster grid
        GridLayoutManager layoutManager = DisplayUtilities.getOptimalGridLayoutManager(this,
                        getResources().getInteger(R.integer.grid_layout_target_pixel_width),
                        getResources().getInteger(R.integer.grid_layout_minimum_number_of_columns));

        //  bind our layoutmanager to our recyclerview
        mRecyclerView.setLayoutManager(layoutManager);

        //  bind click handler to recyclerview so user may interact with movie posters
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        //  do the hard work: load and display movie list data
        //  TODO Stage 2: sortBy parameter and / or movie list data from savedInstanceState?
        String defaultSortBy =
                getResources().getStringArray(R.array.themoviedb_sort_by_query_values)[0];

        loadMovieData(defaultSortBy);
    }

    /**
     * method to initiate data loading and make responsible view visible
     *
     * @param sortBy    sort by popularity or user rating
     */
    private void loadMovieData(String sortBy) {
        showMovieDataView();
        new FetchMovieDataAsyncTask().execute(sortBy);
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
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
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

        Log.d(TAG, "onClick: sending data: " + movieData.toString());
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
        Spinner sortBySpinner = (Spinner) sortByMenuItem.getActionView();

        //  create an ArrayAdapter so our Spinner will have predefined values the user can select
        ArrayAdapter<CharSequence> sortByAdapter = ArrayAdapter.createFromResource(this,
                R.array.themoviedb_sort_by_spinner_text,
                android.R.layout.simple_spinner_dropdown_item);

        //  use default Android simple spinner dropdown layout
        sortByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //  bind adapter to our spinner
        sortBySpinner.setAdapter(sortByAdapter);
        //  bind callback method so we will be notified if user selects an item from our Spinner
        sortBySpinner.setOnItemSelectedListener(this);
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
        String sortBy = getResources().getStringArray(R.array.themoviedb_sort_by_query_values)[position];
        Log.d(TAG, "selected item: " + position + ", value: " + sortBy);
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

    /**
     * inner class to handle tasks around background data loading:
     * - show loading indicator
     * - load data in the background
     * - hide loading indicator / display error or data as needed when data loading completed
     * - send loaded data to our MovieAdapter so it can do its job updating RecyclerView
     * <p>
     * Should be implemented as static method to prevent memory leaks, but it is beyond the scope
     * of this project.
     */
    public class FetchMovieDataAsyncTask extends AsyncTask<String, Void, ArrayList<MovieData>> {
        @Override

        /*
         * method called before executing background task. Currently it does only one thing:
         * make loading indicator visible. Takes no parameter, as our LoadingIndicator view is
         * already "cached" in mLoadingIndicator global variable from our MainActivity
         */
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        /**
         * this is our background task that takes care of getting data
         * TODO Stage 2: it will probably be a cursor, and params may need update
         *
         * @param params two strings: sort by (popularity or rating) and order by (asc, desc)
         * @return MovieData arraylist of loaded data or null on error
         */
        @Override
        protected ArrayList<MovieData> doInBackground(String... params) {

            //  expects exactly 1 parameter
            if (1 != params.length) {
                return null;
            }

            String sortBy = params[0];

            try {
                return TheMovieDbUtils.getMovieDbData(MainActivity.this, sortBy);
            } catch (Exception e) {
                //  TODO Stage 2: optional: display more information on error to user
                e.printStackTrace();
                return null;
            }
        }

        /**
         * method that gets called after loading is finished.
         * - on success, it sends data to our MovieAdapter
         * - on error it display error message
         *
         * @param movieDataArrayList is the arraylist or null returned by doInBackground method
         */
        @Override
        protected void onPostExecute(ArrayList<MovieData> movieDataArrayList) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (null != movieDataArrayList) {
                showMovieDataView();
                mMovieAdapter.setMovieDataArrayList(movieDataArrayList);
            } else {
                showErrorMessage();
            }
        }
    }
}
