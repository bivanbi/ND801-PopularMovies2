package com.example.android.p022popularmovies2.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.p022popularmovies2.data.MovieData;

import java.io.File;

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
 *          MovieAdapter to be bound to RecyclerView to help display a list of movie reviews
 *
 * class to provide methods to store, retrieve and delete fileson internal storage
 */

public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    private static final String FILE_NAME_EXTENSION_PNG = ".png";
    private static final String FILE_NAME_EXTENSION_JPEG = ".jpg";

    private static final String MOVIE_POSTER_CACHE_MAIN_DIRECTORY = "favourite_movie_posters";
    private static final String MOVIE_POSTER_CACHE_DIRECTORY_FORMAT_STRINT = "%d";
    private static final String MOVIE_POSTER_CACHE_FILENAME_FORMAT_STRING = "movie_poster_%d_%s%s";

    /**
     * create and get poster cache directory
     *
     * @param context   calling context
     * @param movieData movie data cache directory should be created for
     * @return File pointing to cache directory, null on failure
     */
    private static File getPosterCacheDir(Context context, MovieData movieData) {
        String dirName = constructPosterCacheDirName(context, movieData);
        File baseDir = context.getFilesDir();
        Log.d(TAG, "getPosterCacheDir: basedir: " + baseDir.toString());

        File posterCacheMainDir = new File(baseDir.toString() + "/"
                + MOVIE_POSTER_CACHE_MAIN_DIRECTORY);

        Log.d(TAG, "getPosterCacheDir: maindir: " + posterCacheMainDir.toString());
        if (!posterCacheMainDir.exists() && !posterCacheMainDir.mkdir()) {
            Log.e(TAG, "getMoviePosterCacheFile: failed to create directory "
                    + posterCacheMainDir.toString());
            return null;
        }

        File posterCacheDir = new File(posterCacheMainDir + "/" + dirName);
        Log.d(TAG, "getPosterCacheDir: cachedir: " + posterCacheDir.toString());
        if (!posterCacheDir.exists() && !posterCacheDir.mkdir()) {
            Log.e(TAG, "getPosterCacheDir: failed to create directory "
                    + posterCacheDir.toString());
            return null;
        }

        return posterCacheDir;
    }

    /**
     * create name for poster cache directory
     * @param context is the calling context
     * @param movieData is the movie data
     * @return String with directory name
     */
    private static String constructPosterCacheDirName(Context context, MovieData movieData) {
        String dirName = String.format(context.getResources().getConfiguration().locale,
                MOVIE_POSTER_CACHE_DIRECTORY_FORMAT_STRINT,
                movieData.getMovieId());
        Log.d(TAG, "constructPosterCacheDirName: " + dirName);
        return dirName;
    }

    /**
     * method to construct image cache file name based on movieData
     * @param context is the calling context
     * @param movieData is the movie data
     * @param imageSize is the image size string used in themoviedb.org API
     * @return String with cache file name
     */
    @Nullable
    private static String constructImageCacheFileName(Context context, MovieData movieData,
            String imageSize) {

        String posterPath = movieData.getPosterPath();
        if (null == posterPath) {
            return null;
        }

        String extension = getImageExtensionFromPath(posterPath);
        if (null == extension) {
            return null;
        }

        String fileName = String.format(context.getResources().getConfiguration().locale,
                MOVIE_POSTER_CACHE_FILENAME_FORMAT_STRING,
                movieData.getMovieId(),
                imageSize, extension.toLowerCase());
        Log.d(TAG, "constructImageCacheFileName: " + fileName);
        return fileName;
    }

    /**
     * get image extension from path
     * @param path is the pathname to original (to be downloaded) image
     * @return extension with leading dot e.g: .jpg
     */
    private static String getImageExtensionFromPath(String path) {
        String extension = getFileNameExtension(path);
        if (null == extension) {
            return null;
        }

        extension = extension.toLowerCase();

        if (!extension.equals(FILE_NAME_EXTENSION_JPEG) && !extension.equals(
                FILE_NAME_EXTENSION_PNG)) {
            Log.e(TAG, "getImageExtensionFromPath: file extension " + extension
                    + " from path " + path + " is unknown");
            return null;
        }

        return extension;
    }

    /**
     * method to get File object pointing to poster cache file
     *
     * @param context is the calling context
     * @param movieData is the movie data
     * @param imageSize is the image size string used in themoviedb.org API
     * @return File pointing to poster cache file, null on error
     */
    @Nullable
    static File getMoviePosterCacheFile(Context context,
            MovieData movieData, String imageSize) {

        File posterCacheDir = getPosterCacheDir(context, movieData);
        if (null == posterCacheDir) {
            Log.e(TAG, "getMoviePosterCacheFile: getPosterCacheDir returned null");
            return null;
        }
        String filename = constructImageCacheFileName(context, movieData, imageSize);
        if (null == filename) {
            Log.e(TAG, "getMoviePosterCacheFile: constructImageCacheFileName returned null");
            return null;
        }
        return new File(posterCacheDir, filename);
    }

    /**
     * method to delete directory along with contained files
     *
     * @param directory is the File object pointing to directory
     * @return true on success
     */
    private static boolean deleteDirectory(File directory) {

        if (!directory.exists()) {
            return true;
        }

        if (!directory.isDirectory()) {
            Log.e(TAG, "deleteDirectory(" + directory.toString()
                    + "): is not a directory");
            return false;
        }

        boolean result;

        for (File file : directory.listFiles()) {
            result = file.delete();
            if (!result) {
                Log.e(TAG, "deleteDirectory(" + directory.toString()
                        + "): file(" + file.toString() + ").delete() returned false ");
            }
        }

        result = directory.delete();
        if (!result) {
            Log.e(TAG, "deleteDirectory(" + directory.toString()
                    + ").delete() returned false");
        }
        return result;
    }

    /**
     * method to split filename to filename and extension like this: read.me -> 'read' and '.me'
     * @param filenameWithExtension is the original filename with extension
     * @return String[] with first element filename, second element extension, null on failure
     */
    @Nullable
    private static String[] splitFileNameAndExtension(String filenameWithExtension) {
        //  in UNIXes, filenames beginning with a dot are considered hidden files and do not
        //  designate an extension
        if (filenameWithExtension.length() < 2) {
            //  if filename is shorter than two chars, then it surely will not contain extension
            return new String[]{filenameWithExtension, null};
        }

        int lastDotIndex = filenameWithExtension.lastIndexOf(".");

        //  lastDotIndex will point to the '.' itself so returned extension will contain it.
        if (lastDotIndex >= filenameWithExtension.length()) {
            Log.e(TAG, "splitFileNameAndExtension: error with lengths: filename length: "
                    + filenameWithExtension.length() + ", lastDotIndex: " + lastDotIndex);
            return null;
        }

        if (lastDotIndex < 0) {
            //  does not contain extension at all
            return new String[]{filenameWithExtension, null};
        }

        if (0 == lastDotIndex) {
            //  dot is at the first position, it is probably a UNIX hidden file
            return new String[]{filenameWithExtension, null};
        }

        Log.d(TAG, "splitFileNameAndExtension: string: " + filenameWithExtension
                + ", length: " + filenameWithExtension.length()
                + " lastDotIndex: " + lastDotIndex);
        return new String[]{
                filenameWithExtension.substring(0, lastDotIndex),
                filenameWithExtension.substring(lastDotIndex)
        };
    }

    /**
     * method to get filename extension from filenmae
     * @param fileName original filename with extension
     * @return String extension on success, null on failure
     */
    @Nullable
    private static String getFileNameExtension(String fileName) {
        String[] fileNameParts = splitFileNameAndExtension(fileName);

        if (null == fileNameParts || null == fileNameParts[1] || fileNameParts[1].equals("")) {
            return null;
        }

        return fileNameParts[1];
    }


    public static boolean deletePosterCache(Context context, MovieData movieData) {

        File cacheDir = getPosterCacheDir(context, movieData);
        if (null == cacheDir) {
            Log.e(TAG, "deletePosterCache failed on " + movieData.getMovieId());
            return false;
        }

        return FileUtils.deleteDirectory(cacheDir);
    }

}
