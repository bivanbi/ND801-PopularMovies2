package com.example.android.p022popularmovies2.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.p022popularmovies2.R;
import com.example.android.p022popularmovies2.data.MovieReview;

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
 *
 *          Adapter to display movie reviews
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    private static final String TAG = ReviewAdapter.class.getSimpleName();
    private final ReviewAdapterOnClickHandler mReviewAdapterOnClickHandler;
    private ArrayList<MovieReview> mMovieReviewArrayList;

    private String mAuthorFormatString;

    /**
     * constructor to our adapter
     *
     * @param reviewAdapterOnClickHandler handler onclick events to be sent to
     * @param authorFormatString          format string for author attribute
     */
    public ReviewAdapter(ReviewAdapterOnClickHandler reviewAdapterOnClickHandler,
            String authorFormatString) {
        mReviewAdapterOnClickHandler = reviewAdapterOnClickHandler;
        mAuthorFormatString = authorFormatString;
    }

    /**
     * Oncreate method to our adapter
     *
     * @param parent   is the parent viewgroup
     * @param viewType is unused here
     * @return ReviewAdapter.ReviewAdapterViewHolder instance
     */
    @NonNull
    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
            int viewType) {
        int layoutIdForListItem = R.layout.movie_review_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(layoutIdForListItem, parent, false);

        return new ReviewAdapterViewHolder(view);
    }

    /**
     * method to bind actual movie review to view
     *
     * @param holder   is the ViewHolder to bind data to
     * @param position is the index of MovieReview in MovieReviewArrayList to be bound to
     *                 ViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewAdapterViewHolder holder,
            int position) {
        MovieReview movieReview = mMovieReviewArrayList.get(position);
        holder.mReviewAuthorTextView.setText(
                String.format(mAuthorFormatString, movieReview.getAuthor()));
        holder.mReviewContentTextView.setText(movieReview.getContent());
    }

    /**
     * method to return item count in this Adapter
     *
     * @return integer with item count
     */
    @Override
    public int getItemCount() {
        if (null == mMovieReviewArrayList) {
            return 0;
        }
        return mMovieReviewArrayList.size();
    }

    /**
     * method to set a new ArrayList to this adapter.
     *
     * @param arrayList is the new MovieData ArrayList
     */
    public void setMovieReviewArrayList(ArrayList<MovieReview> arrayList) {
        Log.i(TAG, "updating Movie Review List, current count: " + getItemCount());
        mMovieReviewArrayList = arrayList;
        notifyDataSetChanged();
    }

    /**
     * onclick handler interface definition
     */
    public interface ReviewAdapterOnClickHandler {
        void onClick(MovieReview movieReview);
    }

    /**
     * inner class to implement ViewHolder pattern to speed up scrolling
     */
    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.textview_review_author)
        TextView mReviewAuthorTextView;
        @BindView(R.id.textview_review_content)
        TextView mReviewContentTextView;

        /**
         * constructor to ViewHolder object
         *
         * @param view is the view that should be held
         */
        ReviewAdapterViewHolder(View view) {
            super(view);
            //  onClick listeners will be set up later in the code with ButterKnife's @OnClick
            ButterKnife.bind(this, view);
        }

        /**
         * a method to receive onClick events and then "relay" MovieReview
         * to ReviewAdapter's onClick handler.
         * <p>
         * View parameter is only required to implement onClick method. Unused here.
         *
         * @param v is the view that as been clicked
         */
        @Override
        @OnClick({R.id.textview_review_content, R.id.textview_review_author})
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mReviewAdapterOnClickHandler.onClick(mMovieReviewArrayList.get(adapterPosition));
        }
    }
}
