package com.example.android.p022popularmovies2.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.p022popularmovies2.DetailActivity;
import com.example.android.p022popularmovies2.R;
import com.example.android.p022popularmovies2.data.MovieVideo;

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

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {
    private static final String TAG = VideoAdapter.class.getSimpleName();
    private final VideoAdapterOnClickHandler mVideoAdapterOnClickHandler;
    private ArrayList<MovieVideo> mMovieVideoArrayList;
    private DetailActivity.ShareClickDispatcher mShareClickDispatcher;

    public VideoAdapter(VideoAdapterOnClickHandler videoAdapterOnClickHandler,
            DetailActivity.ShareClickDispatcher shareClickDispatcher) {
        mVideoAdapterOnClickHandler = videoAdapterOnClickHandler;
        mShareClickDispatcher = shareClickDispatcher;
    }

    @NonNull
    @Override
    public VideoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
            int viewType) {
        int layoutIdForListItem = R.layout.movie_video_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(layoutIdForListItem, parent, false);

        return new VideoAdapterViewHolder(view);
    }

    /**
     * method to bind actual movie data to view
     *
     * @param holder   is the ViewHolder to bind data to
     * @param position is the index of MovieReview in MovieReviewArrayList to be bound to
     *                 ViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull VideoAdapterViewHolder holder, int position) {
        MovieVideo movieVideo = mMovieVideoArrayList.get(position);
        holder.mVideoNameTextView.setText(movieVideo.getName());
    }

    /**
     * method to return item count in this Adapter
     *
     * @return integer with item count
     */
    @Override
    public int getItemCount() {
        if (null == mMovieVideoArrayList) {
            return 0;
        }
        return mMovieVideoArrayList.size();
    }

    /**
     * method to set a new ArrayList to this adapter.
     *
     * @param arrayList is the new MovieData ArrayList
     */
    public void setMovieVideoArrayList(ArrayList<MovieVideo> arrayList) {
        Log.i(TAG, "updating Movie Review List, current count: " + getItemCount());
        mMovieVideoArrayList = arrayList;
        notifyDataSetChanged();
    }

    /**
     * interface definition for adapter onclick handler
     */
    public interface VideoAdapterOnClickHandler {
        void onClick(MovieVideo movieVideo);
    }

    /**
     * inner class to implement ViewHolder pattern to speed up scrolling
     */
    public class VideoAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.textview_video_name)
        TextView mVideoNameTextView;
        @BindView(R.id.movie_detail_video_share_imageview)
        ImageView mVideoShareImageView;

        /**
         * constructor to ViewHolder object
         *
         * @param view is the view that should be held
         */
        VideoAdapterViewHolder(View view) {
            super(view);
            //  onClick listeners will be set up later in the code with ButterKnife's @OnClick
            ButterKnife.bind(this, view);
        }

        /**
         * a method to receive onClick events and then "relay" MovieTraler
         * to VideoAdapter's onClick handler.
         * <p>
         * View parameter is only required to implement onClick method. Unused here.
         *
         * @param v is the view that as been clicked
         */
        @Override
        @OnClick({R.id.textview_video_name})
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mVideoAdapterOnClickHandler.onClick(mMovieVideoArrayList.get(adapterPosition));
        }

        /**
         * method to relay onclick events on share button
         */
        @OnClick({R.id.movie_detail_video_share_imageview})
        void onShareClick() {
            int adapterPosition = getAdapterPosition();
            mShareClickDispatcher.onClick(mMovieVideoArrayList.get(adapterPosition));
        }
    }
}
