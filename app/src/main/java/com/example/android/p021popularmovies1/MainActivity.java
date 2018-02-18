package com.example.android.p021popularmovies1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//  TODO (1) Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(imageView);

/* TODO (2) themoviedb.org API
A note on resolving poster paths with themoviedb.org API
You will notice that the API response provides a relative path to a movie poster image when you request the metadata for a specific movie.


For example, the poster path return for Interstellar is “/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg”


You will need to append a base path ahead of this relative path to build the complete url you will need to fetch the image using Picasso.


It’s constructed using 3 parts:


The base URL will look like: http://image.tmdb.org/t/p/.
Then you will need a ‘size’, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original". For most phones we recommend using “w185”.
And finally the poster path returned by the query, in this case “/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg”

Combining these three parts gives us a final url of http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg

This is also explained explicitly in the API documentation for /configuration.
 */


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
