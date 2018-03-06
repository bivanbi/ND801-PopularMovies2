package com.example.android.p022popularmovies2.utilities;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Udacity Android Developer Nanodegree - Project Popular Movies stage 2
 *
 * @author balazs.lengyak@gmail.com
 * @version 2.0
 *          <p>
 *          - Inspired by dozens of online found examples (both visual and code design),
 *          - Might contain traces of code from official Android Developer documentation and
 *          default templates from Android Studio
 *          - getGridViewOptimalNumberOfColumns code received from Udacity reviewer
 *          <p>
 *              class to provide common JSON methods
 */

public class JSONUtils {

    private static final String TAG = JSONUtils.class.getSimpleName();

    /**
     * method to safely parse JSON string into json object, will catch json exception
     *
     * @param json is the JSON as string
     * @return is the JSONObject parsed from json string
     */
    @Nullable
    public static JSONObject safeNewJSONObject(String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "JSON string parsing failed: " + e.toString());
            return null;
        }
    }
}
