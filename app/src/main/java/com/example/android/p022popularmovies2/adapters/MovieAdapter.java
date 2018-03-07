package com.example.android.p022popularmovies2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.p022popularmovies2.R;
import com.example.android.p022popularmovies2.data.MovieData;
import com.example.android.p022popularmovies2.utilities.TheMovieDbUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
 *          MovieAdapter to be bound to RecyclerView to help display a grid of movie posters
 */

public class MovieAdapter
        extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    private final MovieAdapterOnClickHandler mMovieAdapterOnClickHandler;
    private final int nonFavouriteBackgroundColor;
    private final int favouriteBackgroundColor;
    private ArrayList<MovieData> mMovieDataArrayList;
    //  according to the books, storing context might not always be a good idea, because it
    //  can interfere with Java's Garbage Collection.
    private Context mContext;

    /**
     * constructor to MovieAdapter
     *
     * @param clickHandler onClick handler object
     */
    public MovieAdapter(MovieAdapterOnClickHandler clickHandler,
            int nonFavouriteBackgroundColor,
            int favouriteBackgroundColor) {
        mMovieAdapterOnClickHandler = clickHandler;
        this.nonFavouriteBackgroundColor = nonFavouriteBackgroundColor;
        this.favouriteBackgroundColor = favouriteBackgroundColor;
    }

    /**
     * method called when ViewHolder is created to inflate view, create and return ViewHolder bound
     * to this new View
     *
     * @param parent   is the parent ViewGroup the new View will be children to
     * @param viewType unused here
     * @return MovieAdapterViewHolder
     */
    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(layoutIdForListItem, parent,
                false);

        return new MovieAdapterViewHolder(view);
    }

    /**
     * method to bind actual movie data to view
     *
     * @param holder   is the ViewHolder to bind data to
     * @param position is the index of MovieData in MovieDataArrayList to be bound to ViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        MovieData movieData = mMovieDataArrayList.get(position);

        String posterUrl = movieData.getPosterPath();

        //  check if movie has poster
        if (posterUrl.equals("null")) {
            Log.i(TAG, "onBindViewHolder: movie " + movieData.getTitle()
                    + " has no poster image associated");
            holder.mMoviePosterImageView.setImageResource(android.R.drawable.star_big_off);
        } else {
            //  initiate loading poster into view
            holder.mMoviePosterLoadingProgressbar.setVisibility(View.VISIBLE);
            TheMovieDbUtils.loadMovieImageIntoView(mContext,
                    holder.mMoviePosterImageView,
                    movieData,
                    mContext.getString(R.string.themoviedb_image_resolution_thumbnail), holder);
        }

        String title = movieData.getTitle();

        // update contentDescription so screen readers might benefit from it
        holder.mMovieTitleTextView.setText(title);
        holder.mMoviePosterImageView.setContentDescription(title);

        //  if movie is favourite, indicate it with title background color and icon on poster
        if (movieData.isFavourite()) {
            holder.mMovieTitleTextView.setBackgroundColor(favouriteBackgroundColor);
            holder.mMovieFavouriteImageView.setVisibility(View.VISIBLE);
        } else {
            holder.mMovieTitleTextView.setBackgroundColor(nonFavouriteBackgroundColor);
            holder.mMovieFavouriteImageView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * method to return item count in this Adapter
     *
     * @return integer with item count
     */
    @Override
    public int getItemCount() {
        if (null == mMovieDataArrayList) {
            return 0;
        }
        return mMovieDataArrayList.size();
    }

    /**
     * method to set a new ArrayList to this adapter.
     *
     * @param arrayList is the new MovieData ArrayList
     */
    public void setMovieDataArrayList(ArrayList<MovieData> arrayList) {
        Log.i(TAG, "updating Movie List, current count: " + getItemCount());
        mMovieDataArrayList = arrayList;
        notifyDataSetChanged();
    }

    /**
     * onclick interface definition for handling onclick events on movie list items
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(MovieData movieData);
    }

    /**
     * inner class to implement ViewHolder pattern to speed up scrolling
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, TheMovieDbUtils.LoadMovieImageIntoViewCallback {

        @BindView(R.id.imageview_movie_list_item_poster)
        ImageView mMoviePosterImageView;
        @BindView(R.id.textview_movie_list_element)
        TextView mMovieTitleTextView;
        @BindView(R.id.imageview_movie_list_item_favourite)
        ImageView mMovieFavouriteImageView;
        @BindView(R.id.progressbar_movie_list_item_poster_loading)
        ProgressBar mMoviePosterLoadingProgressbar;

        /**
         * constructor to ViewHolder object
         *
         * @param view is the view that should be held
         */
        MovieAdapterViewHolder(View view) {
            super(view);

            //  onClick listeners will be set up later in the code with ButterKnife's @OnClick
            ButterKnife.bind(this, view);
        }

        /**
         * a method to receive onClick events and then "relay" MovieData
         * to MovieAdapter's onClick handler.
         * <p>
         * View parameter is only required to implement onClick method. Unused here
         *
         * @param v is the view that as been clicked
         */
        @Override
        @OnClick({R.id.imageview_movie_list_item_poster, R.id.textview_movie_list_element})
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mMovieAdapterOnClickHandler.onClick(mMovieDataArrayList.get(adapterPosition));
        }

        /**
         * method to receive success callback from TheMovieDbUtils.loadMovieImageIntoView
         *
         * @param view      is the view the poster has been loaded into
         * @param movieData is the movieData object whose poster has been loaded
         * @param imageSize is the size string of the image - see strings.xml for examples
         */
        @Override
        public void onSuccess(View view, MovieData movieData, String imageSize) {
            mMoviePosterLoadingProgressbar.setVisibility(View.INVISIBLE);
        }

        /**
         * method to receive failure callback from TheMovieDbUtils.loadMovieImageIntoView
         *
         * @param view      is the view the poster should have been loaded into
         * @param movieData is the movieData object whose poster should have been loaded
         * @param imageSize is the size string of the image - see strings.xml for examples
         */
        @Override
        public void onError(View view, MovieData movieData, String imageSize) {
            mMoviePosterLoadingProgressbar.setVisibility(View.INVISIBLE);
        }
    }


}
