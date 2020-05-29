package com.ericksoncv.androidtvapptutorial.data;


import android.content.Context;
import android.util.Log;

import android.content.AsyncTaskLoader;

import com.ericksoncv.androidtvapptutorial.model.Movie;

import org.json.JSONException;

import java.util.LinkedHashMap;
import java.util.List;

public class VideoItemLoader extends AsyncTaskLoader<LinkedHashMap<String, List<Movie>>> {

    private static final String TAG = VideoItemLoader.class.getSimpleName();

    public VideoItemLoader(Context context) {
        super(context);
    }

    @Override
    public LinkedHashMap<String, List<Movie>> loadInBackground() {
        Log.d(TAG, "loadInBackground");

        /*
         * Executed in background thread.
         * Prepare data here, it may take long time (Database access, URL connection, etc).
         * return value is used in onLoadFinished() method in Activity/Fragment's LoaderCallbacks.
         */
        //LinkedHashMap<String, List<Movie>> videoLists = prepareData();
        LinkedHashMap<String, List<Movie>> videoLists = null;
        try {
            videoLists = VideoProvider.buildMedia(getContext());
        } catch (JSONException e) {
            Log.e(TAG, "buildMedia failed", e);
            //cancelLoad();
        }
        return videoLists;
    }

    @Override
    protected void onStartLoading() {
        //super.onStartLoading();
        forceLoad();
    }

    private LinkedHashMap<String, List<Movie>> prepareData() {
        LinkedHashMap<String, List<Movie>> videoLists = new LinkedHashMap<>();
        List<Movie> videoList = MovieProvider.getMovieItems();
        videoLists.put("category 1", videoList);
        videoLists.put("category 2", videoList);
        videoLists.put("category 3", videoList);
        return videoLists;
    }
}