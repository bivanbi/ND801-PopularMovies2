package com.example.android.p021popularmovies1.utilities;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;

/**
 * Udacity Android Developer Nanodegree - Project Popular Movies stage 1
 *
 * @author balazs.lengyak@gmail.com
 * @version 1.1
 *          <p>
 *          - Inspired by dozens of online found examples (both visual and code design),
 *          - Might contain traces of code from official Android Developer documentation and
 *          default templates from Android Studio
 *          - getGridViewOptimalNumberOfColumns code received from Udacity reviewer
 *          <p>
 *          MovieAdapter to be bound to RecyclerView to help display a grid of movie posters
 *
 * Helper class to provide display related methods
 */

public class DisplayUtilities {

    private static final int DISPLAY_METRICS_HEIGHT = 0;
    private static final int DISPLAY_METRICS_WIDTH = 1;

    /**
     * create and return a GridLayoutManager with column count optimized for current screen size
     *
     * @param activity is the calling activity
     * @param widthDivider is the target column width in pixels
     * @param minNumberOfColumns is the minimum number of columns
     * @return GridLayoutManager
     */
    public static GridLayoutManager getOptimalGridLayoutManager(Activity activity, int widthDivider, int minNumberOfColumns) {
        int columnCount = getOptimalGridColumnCount(activity, widthDivider, minNumberOfColumns);

        //  layout manager to display movie poster grid
        return new GridLayoutManager(activity, columnCount, GridLayoutManager.VERTICAL, false);
    }

    /**
     * calculate grid column count based on actual screen size, target column width and minimum column count
     * @param activity calling activity
     * @param widthDivider target column width in pixels
     * @param minNumberOfColumns minimum number of columns
     * @return integer grid count
     */
    private static int getOptimalGridColumnCount(Activity activity, int widthDivider, int minNumberOfColumns) {
        int width = getDisplayMetrics(activity, DISPLAY_METRICS_WIDTH);
        return Math.max(width / widthDivider, minNumberOfColumns);
    }

    /**
     * get display size in pixels
     * @param activity is the calling activity
     * @param dimension integer DISPLAY_METRIC_HEIGHT or DISPLAY_METRIC_WIDTH
     * @return display height or width in pixels
     */
    private static int getDisplayMetrics(Activity activity, int dimension) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if (DISPLAY_METRICS_HEIGHT == dimension) {
            return displayMetrics.heightPixels;
        } else {
            return displayMetrics.widthPixels;
        }
    }


}