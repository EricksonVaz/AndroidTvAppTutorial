package com.ericksoncv.androidtvapptutorial.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.SpeechRecognitionCallback;

import com.ericksoncv.androidtvapptutorial.ui.presenter.CardPresenter;
import com.ericksoncv.androidtvapptutorial.common.Utils;
import com.ericksoncv.androidtvapptutorial.data.MovieProvider;
import com.ericksoncv.androidtvapptutorial.model.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends androidx.leanback.app.SearchFragment implements androidx.leanback.app.SearchFragment.SearchResultProvider {

    private static final String TAG = SearchFragment.class.getSimpleName();

    private static final int REQUEST_SPEECH = 0x00000010;

    private ArrayObjectAdapter mRowsAdapter;
    private ArrayList<Movie> mItems = MovieProvider.getMovieItems();

    private String mQuery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        setSearchResultProvider(this);
        //setOnItemViewClickedListener(new ItemViewClickedListener());
        if (!Utils.hasPermission(getActivity(), Manifest.permission.RECORD_AUDIO)) {
            Log.v(TAG, "no permission RECORD_AUDIO");
            // SpeechRecognitionCallback is not required and if not provided recognition will be handled
            // using internal speech recognizer, in which case you must have RECORD_AUDIO permission
            setSpeechRecognitionCallback(new SpeechRecognitionCallback() {
                @Override
                public void recognizeSpeech() {
                    Log.v(TAG, "recognizeSpeech");
                    try {
                        startActivityForResult(getRecognizerIntent(), REQUEST_SPEECH);
                    } catch (ActivityNotFoundException e) {
                        Log.e(TAG, "Cannot find activity for speech recognizer", e);
                    }
                }
            });
        }
    }

    public boolean hasResults() {
        return mRowsAdapter.size() > 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult requestCode=" + requestCode +
                " resultCode=" + resultCode +
                " data=" + data);

        switch (requestCode) {
            case REQUEST_SPEECH:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        setSearchQuery(data, true);
                        break;
                }
        }
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        Log.d(TAG, "getResultsAdapter");
        // Delete previously implemented mock code.
        // mRowsAdapter (Search result) is already prepared in loadRows method
        return mRowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery){
        Log.i(TAG, String.format("Search Query Text Change %s", newQuery));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i(TAG, String.format("Search Query Text Submit %s", query));
        mQuery = query;
        loadRows();
        return true;
    }

    private void loadRows() {
        // offload processing from the UI thread
        new AsyncTask<String, Void, ListRow>() {
            private final String query = mQuery;

            @Override
            protected void onPreExecute() {
                mRowsAdapter.clear();
            }

            @Override
            protected ListRow doInBackground(String... params) {
                final List<Movie> result = new ArrayList<>();
                for (Movie movie : mItems) {
                    // Main logic of search is here.
                    // Just check that "query" is contained in Title or Description or not. (NOTE: excluded studio information here)
                    if (movie.getTitle().toLowerCase(Locale.ENGLISH)
                            .contains(query.toLowerCase(Locale.ENGLISH))
                            || movie.getDescription().toLowerCase(Locale.ENGLISH)
                            .contains(query.toLowerCase(Locale.ENGLISH))) {
                        result.add(movie);
                    }
                }

                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
                listRowAdapter.addAll(0, result);
                HeaderItem header = new HeaderItem("Search Results");
                return new ListRow(header, listRowAdapter);
            }

            @Override
            protected void onPostExecute(ListRow listRow) {
                mRowsAdapter.add(listRow);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
