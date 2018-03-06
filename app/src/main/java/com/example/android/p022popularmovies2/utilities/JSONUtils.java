package com.example.android.p022popularmovies2.utilities;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * TODO documentation
 * Created by bivanbi on 2018.02.26..
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
