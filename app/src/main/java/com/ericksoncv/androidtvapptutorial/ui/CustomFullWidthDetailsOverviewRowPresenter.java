package com.ericksoncv.androidtvapptutorial.ui;

import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.RowPresenter;

public class CustomFullWidthDetailsOverviewRowPresenter extends FullWidthDetailsOverviewRowPresenter {
    public CustomFullWidthDetailsOverviewRowPresenter(Presenter detailsPresenter) {
        super(detailsPresenter);
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);
        this.setState((ViewHolder)holder,FullWidthDetailsOverviewRowPresenter.STATE_SMALL);
    }
}
