package com.example.android.p022popularmovies2.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

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
 *
 *          Helper class with network related utilities
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final int DOWNLOAD_STREAM_BUFFER_SIZE = 4 * 1024; // bytes


    /**
     * helper method to tell if phone is connected to network
     *
     * @param context is the calling context
     * @return boolean true if connected, false otherwise
     */
    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if (null != connectivityManager) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        if (null == networkInfo) {
            Log.e(TAG,
                    "isConnectedToNetwork: connectivityManager.getActiveNetworkInfo() returned "
                            + "null");
            return false;
        }

        return networkInfo.isConnected();
    }

    /**
     * method to query HTTP server and fetch response, which will be a JSON string on success
     *
     * @param url is the URL to query
     * @return String with server response
     * @throws IOException on error
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean downloadFileFromUrlIntoFile(String url, File saveFile) {

        InputStream inputStream = null;
        OutputStream outputStream = null;

        boolean isSuccess;
        try {
            inputStream = (InputStream) new URL(url).getContent();
            outputStream = new FileOutputStream(saveFile);

            byte[] buffer = new byte[DOWNLOAD_STREAM_BUFFER_SIZE];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            isSuccess = true;
        } catch (Exception e) {
            Log.e(TAG, "downloadFileFromUrlIntoFile: failed to save URL " + url + " to "
                    + saveFile.toString() + ": "
                    + e.getMessage());
            return false;
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "downloadFileFromUrlIntoFile: failed to close stream " + url
                        + ", file " + saveFile.toString() + ": " + e.getMessage());
            }
        }

        return isSuccess;
    }
}
