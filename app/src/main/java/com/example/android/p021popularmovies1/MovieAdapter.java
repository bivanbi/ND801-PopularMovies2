package com.example.android.p021popularmovies1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.p021popularmovies1.utilities.TheMovieDbUtils;

import java.util.ArrayList;

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
 *          MovieAdapter to be bound to RecyclerView to help display a grid of movie posters
 */

public class MovieAdapter
        extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    private final MovieAdapterOnClickHandler mMovieAdapterOnClickHandler;
    private ArrayList<MovieData> mMovieDataArrayList;
    //  according to the books, storing context might not always be a good idea, because it
    //  can interfere with Java's Garbage Collection.
    private Context mContext;

    /**
     * constructor to MovieAdapter
     *
     * @param clickHandler onClick handler object
     */
    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mMovieAdapterOnClickHandler = clickHandler;
    }

    /**
     * method called when ViewHolder is created to inflate view, create and return ViewHolder bound
     * to this new View
     *
     * @param parent   is the parent ViewGroup the new View will be children to
     * @param viewType unused here
     * @return MovieAdapterViewHolder
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(layoutIdForListItem, parent,
                false);

        int parentWidth = parent.getWidth();
        int parentHeight = parent.getHeight();
        int widthDivider = mContext.getResources().getInteger(R.integer.grid_layout_target_pixel_width);
        int minNumberOfColumns = mContext.getResources().getInteger(R.integer.grid_layout_minimum_number_of_columns);

        int columnCount = Math.max(parentWidth / widthDivider, minNumberOfColumns);

        ViewGroup.LayoutParams params = view.getLayoutParams();

        //  calculate an optimal grid row height based on common movie poster ratio and screen width
        params.height = Math.round((float) parentWidth / (float) columnCount * (float) 1.5);
        view.setLayoutParams(params);

        return new MovieAdapterViewHolder(view);
    }

    /**
     * method to bind actual movie data to view
     *
     * @param holder   is the ViewHolder to bind data to
     * @param position is the index of MovieData in MovieDataArrayList to be bound to ViewHolder
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        MovieData movieData = mMovieDataArrayList.get(position);

        String posterUrl = movieData.getPosterUrl();

        if (posterUrl.equals("null")) {
            Log.i(TAG, "onBindViewHolder: movie " + movieData.getTitle()
                    + " has no poster image associated");
            holder.mMoviePosterImageView.setImageResource(android.R.drawable.star_big_off);
        } else {
            TheMovieDbUtils.loadMovieImageIntoView(mContext,
                    holder.mMoviePosterImageView,
                    movieData.getPosterUrl(),
                    mContext.getString(R.string.themoviedb_image_resolution_thumbnail));
        }

        String title = movieData.getTitle();
        // update contentDescription so screen readers might benefit from it
        holder.mMovieTitleTextView.setText(title);
        holder.mMoviePosterImageView.setContentDescription(title);
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
        if (null == arrayList) {
            Log.i(TAG, "updating Movie List, current count: " + getItemCount() + ", new: NULL");
        } else {
            Log.i(TAG, "updating Movie List, current count: "
                    + getItemCount() + ", new: " + arrayList.size());
        }

        mMovieDataArrayList = arrayList;
        notifyDataSetChanged();
    }

    //  define interface to be able to receive onClick events
    public interface MovieAdapterOnClickHandler {
        void onClick(MovieData movieData);
    }

    /**
     * inner class to implement ViewHolder pattern to speed up scrolling
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public final ImageView mMoviePosterImageView;
        public final TextView mMovieTitleTextView;

        /**
         * constructor to ViewHolder object
         *
         * @param view is the view that should be held
         */
        public MovieAdapterViewHolder(View view) {
            super(view);

            mMoviePosterImageView = view.findViewById(R.id.imageview_movie_list_element);
            mMovieTitleTextView = view.findViewById(R.id.textview_movie_list_element);
            mMoviePosterImageView.setOnClickListener(this);
            mMovieTitleTextView.setOnClickListener(this);
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
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mMovieAdapterOnClickHandler.onClick(mMovieDataArrayList.get(adapterPosition));
        }
    }
}
