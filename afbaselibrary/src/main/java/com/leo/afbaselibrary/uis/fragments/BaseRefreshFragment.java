package com.leo.afbaselibrary.uis.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;

import com.leo.afbaselibrary.R;


/**
 * created by arvin on 16/11/21 20:51
 * email：1035407623@qq.com
 */
public abstract class BaseRefreshFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    protected SwipeRefreshLayout mLayoutRefresh;

    @Override
    public void init(Bundle savedInstanceState) {
        mLayoutRefresh = getView(R.id.pre_refresh);
        mLayoutRefresh.setColorSchemeResources(R.color.colorAccent);
        mLayoutRefresh.setOnRefreshListener(this);
    }

    public void autoRefresh() {
        mLayoutRefresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLayoutRefresh.setRefreshing(true);
                onRefresh();
            }
        }, 100);
    }

    protected void refreshComplete() {
        mLayoutRefresh.setRefreshing(false);
    }
}
