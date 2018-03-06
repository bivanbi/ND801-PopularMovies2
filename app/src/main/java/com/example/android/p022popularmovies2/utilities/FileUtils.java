package com.example.android.p022popularmovies2.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.p022popularmovies2.data.MovieData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * TODO documentation
 * Created by bivanbi on 2018.03.03..
 */

public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    private static final String FILE_NAME_EXTENSION_PNG = ".png";
    private static final Bitmap.CompressFormat COMPRESS_FORMAT_PNG = Bitmap.CompressFormat.PNG;

    private static final String FILE_NAME_EXTENSION_JPEG = ".jpg";
    private static final Bitmap.CompressFormat COMPRESS_FORMAT_JPEG = Bitmap.CompressFormat.JPEG;

    private static final String MOVIE_POSTER_CACHE_MAIN_DIRECTORY = "favourite_movie_posters";
    private static final String MOVIE_POSTER_CACHE_DIRECTORY_FORMAT_STRINT = "%d";
    private static final String MOVIE_POSTER_CACHE_FILENAME_FORMAT_STRING = "movie_poster_%d_%s%s";

    static File getPosterCacheDir(Context context, MovieData movieData) {
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


    private static String constructPosterCacheDirName(Context context, MovieData movieData) {
        String dirName = String.format(context.getResources().getConfiguration().locale,
                MOVIE_POSTER_CACHE_DIRECTORY_FORMAT_STRINT,
                movieData.getMovieId());
        Log.d(TAG, "constructPosterCacheDirName: " + dirName);
        return dirName;
    }

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

    static void saveBitmapToFile(Bitmap bitmap, String filename,
            Bitmap.CompressFormat format, int quality) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bitmap.compress(format, quality, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static boolean deleteDirectory(File directory) {

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

    @Nullable
    private static String getFileNameExtension(String fileName) {
        String[] fileNameParts = splitFileNameAndExtension(fileName);

        if (null == fileNameParts || null == fileNameParts[1] || fileNameParts[1].equals("")) {
            return null;
        }

        return fileNameParts[1];
    }


    static Bitmap.CompressFormat getCompressFormatFromFileName(String fileName) {

        String extension = getFileNameExtension(fileName);

        if (null == extension) {
            return null;
        }

        switch (extension.toLowerCase()) {
            case FILE_NAME_EXTENSION_JPEG:
                return COMPRESS_FORMAT_JPEG;

            case FILE_NAME_EXTENSION_PNG:
                return COMPRESS_FORMAT_PNG;

            default:
                return null;
        }
    }

    public static boolean deletePosterCache(Context context, MovieData movieData) {
        String dirName = constructPosterCacheDirName(context, movieData);

        File cacheDir = getPosterCacheDir(context, movieData);
        if (null == cacheDir) {
            Log.e(TAG, "deletePosterCache failed on " + movieData.getMovieId());
            return false;
        }

        return FileUtils.deleteDirectory(cacheDir);
    }

}
