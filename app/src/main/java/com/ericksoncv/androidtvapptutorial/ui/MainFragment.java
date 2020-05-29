package com.ericksoncv.androidtvapptutorial.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.leanback.app.BrowseFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import android.app.LoaderManager;
import android.content.Loader;

import com.ericksoncv.androidtvapptutorial.data.VideoItemLoader;
import com.ericksoncv.androidtvapptutorial.ui.presenter.CardPresenter;
import com.ericksoncv.androidtvapptutorial.ui.background.PicassoBackgroundManager;
import com.ericksoncv.androidtvapptutorial.R;
import com.ericksoncv.androidtvapptutorial.ui.background.SimpleBackgroundManager;
import com.ericksoncv.androidtvapptutorial.data.MovieProvider;
import com.ericksoncv.androidtvapptutorial.model.Movie;
import com.ericksoncv.androidtvapptutorial.recommendation.RecommendationFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainFragment extends BrowseFragment {
    private static final String TAG = MainFragment.class.getSimpleName();

    private ArrayObjectAdapter mRowsAdapter;
    private static final int GRID_ITEM_WIDTH = 300;
    private static final int GRID_ITEM_HEIGHT = 200;

    private static final String GRID_STRING_ERROR_FRAGMENT = "ErrorFragment";
    private static final String GRID_STRING_GUIDED_STEP_FRAGMENT = "GuidedStepFragment";
    private static final String GRID_STRING_VERTICAL_GRID_FRAGMENT = "VerticalGridFragment";
    private static final String GRID_STRING_RECOMMENDATION = "Recommendation";
    private static final String GRID_STRING_SPINNER = "Spinner";

    private static SimpleBackgroundManager simpleBackgroundManager = null;
    private static PicassoBackgroundManager picassoBackgroundManager = null;

    ArrayList<Movie> mItems = MovieProvider.getMovieItems();
    private static int recommendationCounter = 0;

    private static final int VIDEO_ITEM_LOADER_ID = 1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        setupUIElements();

        loadRows();
        getLoaderManager().initLoader(VIDEO_ITEM_LOADER_ID, null, new MainFragmentLoaderCallbacks());

        setupEventListeners();

        picassoBackgroundManager = new PicassoBackgroundManager(getActivity());
        picassoBackgroundManager.updateBackgroundWithDelay("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/10/RIMG0656.jpg");
    }

    private void setupEventListeners(){
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
        setOnItemViewClickedListener(new ItemViewClickedListener());

        // Existence of this method make In-app search icon visible
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if(item instanceof Movie){
                Movie movie = (Movie) item;
                Log.d(TAG,"Item: "+item.toString());
                Intent intent = new Intent(getActivity(),DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE,movie);

                getActivity().startActivity(intent);
            }else if(item instanceof String){
                if (item == "ErrorFragment") {
                    Intent intent = new Intent(getActivity(), ErrorActivity.class);
                    startActivity(intent);
                } else if (item == "GuidedStepFragment") {
                    Intent intent = new Intent(getActivity(), GuidedStepActivity.class);
                    startActivity(intent);
                }else if (item == GRID_STRING_RECOMMENDATION) {
                    Log.v(TAG, "onClick recommendation. counter " + recommendationCounter);
                    RecommendationFactory recommendationFactory = new RecommendationFactory(getActivity().getApplicationContext());
                    Movie movie = mItems.get(recommendationCounter % mItems.size());
                    recommendationFactory.recommend(recommendationCounter, movie, NotificationCompat.PRIORITY_HIGH);
                    Toast.makeText(getActivity(), "Recommendation sent (item " + recommendationCounter +")", Toast.LENGTH_SHORT).show();
                    recommendationCounter++;
                }else if(item == GRID_STRING_SPINNER){
                    new ShowSpinnerTask().execute();
                }
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            /*if(item instanceof String){
                simpleBackgroundManager.clearBackground();
            }else if(item instanceof Movie){
                simpleBackgroundManager.updateBackground(getActivity().getDrawable(R.drawable.movie));
            }*/
            if(item instanceof String){
                picassoBackgroundManager.updateBackgroundWithDelay("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/10/RIMG0656.jpg");
            }else if(item instanceof Movie){
                picassoBackgroundManager.updateBackgroundWithDelay(((Movie)item).getCardImageUrl());
            }
        }
    }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.videos_by_google_banner));
        setTitle("Hello Android TV!"); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    private void loadRows(){
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        /* GridItemPresenter */
        HeaderItem gridItemPresenterHeader = new HeaderItem(0,"GridItemPresenter");

        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        //gridRowAdapter.add("Item 1");
        gridRowAdapter.add("ErrorFragment");
        gridRowAdapter.add("GuidedStepFragment");
        gridRowAdapter.add(GRID_STRING_RECOMMENDATION);
        gridRowAdapter.add(GRID_STRING_SPINNER);
        mRowsAdapter.add(new ListRow(gridItemPresenterHeader,gridRowAdapter));

        /* CardPresenter */
        HeaderItem cardPresenterHeader = new HeaderItem(1,"CardPresenter");
        CardPresenter cardPresenter = new CardPresenter();
        ArrayObjectAdapter cardRowAdapter = new ArrayObjectAdapter(cardPresenter);

        /*for(int i=0; i<10; i++){
            Movie movie = new Movie();
            //movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02580.jpg");
            if(i%3 == 0) {
                movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02580.jpg");
            } else if (i%3 == 1) {
                movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02630.jpg");
            } else {
                movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02529.jpg");
            }
            movie.setTitle("title"+i);
            movie.setStudio("studio"+i);
            String description = "Lorem ipsum dolor sit amet, qui mundi vivendum cu. Mazim dicant possit te his. Quo solet dicant prodesset eu, pri deseruisse concludaturque ea, saepe maiorum sea et. Impetus discere sed at. Vim eu novum erant integre, te tale voluptatibus est. Facer labores te mel.\n" +
                    "\n" +
                    "Dictas denique qualisque mea id, cu mei verear fabellas. Mel no autem nusquam, viderer oblique te mei. At minimum corpora consulatu vim. Cibo nominavi vis no, in verterem vulputate eos, essent iriure cu vel. Ius ferri expetendis ad, omnes aeterno nominati id his, eum debitis lobortis comprehensam id.\n" +
                    "\n" +
                    "Illud dicit nostrud sit no. Eu quod nostro pro. Ut gubergren mnesarchum has, nostro detracto scriptorem et quo, no illud phaedrum recteque sea. Ad his summo probatus recusabo. Qui amet tale viris et, ei his quodsi torquatos adipiscing. Laudem malorum no eum, accusam mandamus sit ex, est ut tractatos dissentiet. Dictas feugiat usu et, an his cibo appareat placerat, eu quis dignissim qui.\n" +
                    "\n" +
                    "Euripidis neglegentur eu per, denique singulis vel cu, malis dolore ne duo. Cum no iracundia persecuti expetendis. Vim alii dolore malorum at, veniam perfecto salutandi cu nec, vix ad nonumes consulatu scripserit. At sit nonumy dolores aliquando, eu nam sumo legere. Eu maiorum adipisci torquatos his, vidit appareat eos no.\n" +
                    "\n" +
                    "Solet laboramus no quo, cu aperiam inermis vix. Eum animal graecis id, ne quodsi abhorreant sit. Tale persequeris te qui. Labitur invenire explicari in vix."
                    + "Lorem ipsum dolor sit amet, qui mundi vivendum cu. Mazim dicant possit te his. Quo solet dicant prodesset eu, pri deseruisse concludaturque ea, saepe maiorum sea et. Impetus discere sed at. Vim eu novum erant integre, te tale voluptatibus est. Facer labores te mel.\n" +
                    "\n" +
                    "Dictas denique qualisque mea id, cu mei verear fabellas. Mel no autem nusquam, viderer oblique te mei. At minimum corpora consulatu vim. Cibo nominavi vis no, in verterem vulputate eos, essent iriure cu vel. Ius ferri expetendis ad, omnes aeterno nominati id his, eum debitis lobortis comprehensam id.\n" +
                    "\n" +
                    "Illud dicit nostrud sit no. Eu quod nostro pro. Ut gubergren mnesarchum has, nostro detracto scriptorem et quo, no illud phaedrum recteque sea. Ad his summo probatus recusabo. Qui amet tale viris et, ei his quodsi torquatos adipiscing. Laudem malorum no eum, accusam mandamus sit ex, est ut tractatos dissentiet. Dictas feugiat usu et, an his cibo appareat placerat, eu quis dignissim qui.\n" +
                    "\n" +
                    "Euripidis neglegentur eu per, denique singulis vel cu, malis dolore ne duo. Cum no iracundia persecuti expetendis. Vim alii dolore malorum at, veniam perfecto salutandi cu nec, vix ad nonumes consulatu scripserit. At sit nonumy dolores aliquando, eu nam sumo legere. Eu maiorum adipisci torquatos his, vidit appareat eos no.\n" +
                    "\n" +
                    "Solet laboramus no quo, cu aperiam inermis vix. Eum animal graecis id, ne quodsi abhorreant sit. Tale persequeris te qui. Labitur invenire explicari in vix.";
            movie.setDescription(description);
            cardRowAdapter.add(movie);
        }*/

        ArrayList<Movie> mItems = MovieProvider.getMovieItems();
        for (Movie movie : mItems) {
            cardRowAdapter.add(movie);
        }

        mRowsAdapter.add(new ListRow(cardPresenterHeader, cardRowAdapter));

        //mRowsAdapter.add(new ListRow(cardPresenterHeader,cardRowAdapter));

        /* set */
        setAdapter(mRowsAdapter);
    }

    private class MainFragmentLoaderCallbacks implements LoaderManager.LoaderCallbacks<LinkedHashMap<String, List<Movie>>>{
        @Override
        public Loader<LinkedHashMap<String, List<Movie>>> onCreateLoader(int id, Bundle args) {
            /* Create new Loader */
            Log.d(TAG, "onCreateLoader");
            if(id == VIDEO_ITEM_LOADER_ID) {
                Log.d(TAG, "create VideoItemLoader");
                return new VideoItemLoader(getActivity());
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<LinkedHashMap<String, List<Movie>>> loader, LinkedHashMap<String, List<Movie>> data) {
            Log.d(TAG, "onLoadFinished");
            /* Loader data has prepared. Start updating UI here */
            switch (loader.getId()) {
                case VIDEO_ITEM_LOADER_ID:
                    Log.d(TAG, "VideoLists UI update");

                    mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

                    int index = 0;
                    /* GridItemPresenter */
                    HeaderItem gridItemPresenterHeader = new HeaderItem(index, "GridItemPresenter");
                    index++;

                    GridItemPresenter mGridPresenter = new GridItemPresenter();
                    ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
                    gridRowAdapter.add(GRID_STRING_ERROR_FRAGMENT);
                    gridRowAdapter.add(GRID_STRING_GUIDED_STEP_FRAGMENT);
                    gridRowAdapter.add(GRID_STRING_RECOMMENDATION);
                    gridRowAdapter.add(GRID_STRING_SPINNER);
                    mRowsAdapter.add(new ListRow(gridItemPresenterHeader, gridRowAdapter));

                    /* CardPresenter */
                    CardPresenter cardPresenter = new CardPresenter();

                    if (null != data) {
                        for (Map.Entry<String, List<Movie>> entry : data.entrySet()) {
                            ArrayObjectAdapter cardRowAdapter = new ArrayObjectAdapter(cardPresenter);
                            List<Movie> list = entry.getValue();

                            for (int j = 0; j < list.size(); j++) {
                                cardRowAdapter.add(list.get(j));
                            }
                            HeaderItem header = new HeaderItem(index, entry.getKey());
                            index++;
                            mRowsAdapter.add(new ListRow(header, cardRowAdapter));
                        }
                    } else {
                        Log.e(TAG, "An error occurred fetching videos");
                    }
                    /* Set */
                    setAdapter(mRowsAdapter);
            }
        }

        @Override
        public void onLoaderReset(Loader<LinkedHashMap<String, List<Movie>>> loader) {
            Log.d(TAG, "onLoadReset");
            /* When it is called, Loader data is now unavailable due to some reason. */

        }
    }

    private class GridItemPresenter extends Presenter{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH,GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {

        }
    }

    private class ShowSpinnerTask extends AsyncTask<Void, Void, Void> {
        SpinnerFragment mSpinnerFragment;

        @Override
        protected void onPreExecute() {
            mSpinnerFragment = new SpinnerFragment();
            getFragmentManager().beginTransaction().add(R.id.main_browse_fragment, mSpinnerFragment).commit();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Do some background process here.
            // It just waits 5 sec in this Tutorial
            SystemClock.sleep(5000);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getFragmentManager().beginTransaction().remove(mSpinnerFragment).commit();
        }
    }
}
