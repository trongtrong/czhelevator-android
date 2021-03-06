package com.leo.afbaselibrary.uis.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.leo.afbaselibrary.R;
import com.leo.afbaselibrary.uis.adapters.MultiItemTypeAdapter;
import com.leo.afbaselibrary.widgets.StateLayout;
import com.leo.afbaselibrary.widgets.emptyprovider.FadeViewAnimProvider;

/**
 * Created by gongli on 2017/5/24 13:17
 * email: lc824767150@163.com
 */

public abstract class BaseStateRefreshingLoadingActivity<T> extends BaseRefreshLoadingActivity<T> {

    protected StateLayout stateLayout;

    protected final static int STATE_CONTENT = 0;
    protected final static int STATE_EMPTY = 1;
    protected final static int STATE_PROGRESS = 2;
    protected final static int STATE_ERROR = 3;

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        stateLayout = getView(R.id.stateLayout);
        initStateLayout();
    }

    protected void initStateLayout() {
        stateLayout.setViewSwitchAnimProvider(new FadeViewAnimProvider());
        stateLayout.setErrorAndEmptyAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateLayout.showProgressView(getString(R.string.loading));
                onfresh();
            }
        });
        stateLayout.showProgressView(getString(R.string.loading));
    }

    @Override
    protected void refreshComplete(boolean loadSuccess) {
        initState(loadSuccess);
        super.refreshComplete(loadSuccess);
    }

    protected void loadingComplete(boolean loadSuccess, int totalPage) {
        initState(loadSuccess);
        super.refreshComplete(loadSuccess, totalPage);
    }

    protected void initState(boolean loadSuccess) {
        if (loadSuccess) {
            if (mCurrPage == FIRST_PAGE && mItems.size() == 0) {
                stateLayout.showEmptyView(getEmptyTip());
            } else {
                stateLayout.showContentView();
            }
        } else {
            if (mCurrPage == FIRST_PAGE) {
                stateLayout.showErrorView(getString(R.string.error));
            }
        }
    }


    protected void showState(int state) {
        switch (state) {
            case STATE_CONTENT:
                stateLayout.showContentView();
                break;
            case STATE_EMPTY:
                stateLayout.showEmptyView(getEmptyTip());
                break;
            case STATE_PROGRESS:
                stateLayout.showProgressView(getString(R.string.loading));
                break;
            case STATE_ERROR:
                stateLayout.showErrorView(getString(R.string.error));
                break;
        }
    }

    @NonNull
    protected String getEmptyTip() {
        return getString(R.string.empty);
    }
}
