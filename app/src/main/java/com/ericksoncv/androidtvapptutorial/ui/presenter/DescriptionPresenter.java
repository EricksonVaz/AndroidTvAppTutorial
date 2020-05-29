package com.ericksoncv.androidtvapptutorial.ui.presenter;

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.ericksoncv.androidtvapptutorial.model.Movie;

public class DescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    private static final String TAG = DescriptionPresenter.class.getSimpleName();

    @Override
    protected void onBindDescription(AbstractDetailsDescriptionPresenter.ViewHolder viewHolder, Object item) {
        viewHolder.getTitle().setText(((Movie) item).getTitle());
        viewHolder.getSubtitle().setText(((Movie) item).getStudio());
    }
}
