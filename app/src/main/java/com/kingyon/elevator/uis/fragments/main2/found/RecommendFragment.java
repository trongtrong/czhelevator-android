package com.kingyon.elevator.uis.fragments.main2.found;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.kingyon.elevator.R;
import com.kingyon.elevator.data.DataSharedPreferences;
import com.kingyon.elevator.entities.entities.ConentEntity;
import com.kingyon.elevator.entities.entities.QueryRecommendEntity;
import com.kingyon.elevator.entities.entities.QueryRecommendTopEntity;
import com.kingyon.elevator.nets.CustomApiCallback;
import com.kingyon.elevator.nets.NetService;
import com.kingyon.elevator.uis.adapters.adaptertwo.AttentionAdapter;
import com.kingyon.elevator.uis.adapters.adaptertwo.RecommendtopAdapter;
import com.kingyon.elevator.uis.fragments.main2.found.utilsf.FoundFragemtUtils;
import com.kingyon.elevator.utils.PublicFuncation;
import com.leo.afbaselibrary.nets.exceptions.ApiException;
import com.leo.afbaselibrary.uis.activities.BaseActivity;
import com.leo.afbaselibrary.widgets.StateLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created By Admin  on 2020/4/14
 * Email : 163235610@qq.com
 * Author:Mrczh
 * Instructions:推荐
 */
public class RecommendFragment extends FoundFragemtUtils {
    @BindView(R.id.rv_attention_top)
    RecyclerView rvAttentionTop;
    @BindView(R.id.rv_attention_list1)
    RecyclerView rvAttentionList;
    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout smartRefreshLayout;
    Unbinder unbinder;
    @BindView(R.id.rl_error)
    RelativeLayout rlError;
    @BindView(R.id.rl_null)
    RelativeLayout rlNull;
    StateLayout stateLayout;
    private View view;

    // 标志位，标志已经初始化完成。
    int page = 1;
    AttentionAdapter attentionAdapter;
    RecommendtopAdapter recommendtopAdapter;
    List<QueryRecommendEntity> recommendEntityList = new ArrayList<>();
    String account;
    @Override
    public int getContentViewId() {
        return R.layout.fragment_recommend;
    }


    @Override
    public void init(Bundle savedInstanceState) {
        LogUtils.e("推荐");

        smartRefreshLayout.setEnableRefresh(true);
        smartRefreshLayout.setEnableAutoLoadMore(true);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.e("推荐");
        stateLayout = getView().findViewById(R.id.stateLayout);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                httpTop();
                recommendEntityList.clear();
                if (stateLayout!=null){
                    stateLayout.showProgressView(getString(R.string.wait));
                }
                httpRecommend(1, "");
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page++;
                httpTop();
                httpRecommend(page, "");
            }
        });

    }

    public SmartRefreshLayout getSmartRefreshLayout() {
        return smartRefreshLayout;
    }

    @Override
    protected void dealLeackCanary() {

    }

    private void httpRecommend(int page, String title) {
        LogUtils.e(page, title,account);
        NetService.getInstance().setQueryRecommend(page, title,account)
                .compose(this.bindLifeCycle())
                .subscribe(new CustomApiCallback<ConentEntity<QueryRecommendEntity>>() {
                    @Override
                    protected void onResultError(ApiException ex) {
                        closeRefresh();
                        LogUtils.e(ex.getDisplayMessage(),ex.getCode());
                        stateLayout.showContentView();
                        if (ex.getCode()==-102){
                            if (page>1) {
                                ToastUtils.showShort("已经没有更多了");
                                smartRefreshLayout.finishLoadMoreWithNoMoreData();
                            }else {
                                rvAttentionList.setVisibility(View.GONE);
                                rlError.setVisibility(View.GONE);
                                rlNull.setVisibility(View.VISIBLE);
                            }
                        }else {
                            rvAttentionList.setVisibility(View.GONE);
                            rlError.setVisibility(View.VISIBLE);
                            rlNull.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNext(ConentEntity<QueryRecommendEntity> conentEntity) {
                        closeRefresh();
                        stateLayout.showContentView();
                        if (conentEntity.getContent().size()==0&& page==1){
                            rvAttentionList.setVisibility(View.GONE);
                            rlError.setVisibility(View.GONE);
                            rlNull.setVisibility(View.VISIBLE);
                        }else {
                            rvAttentionList.setVisibility(View.VISIBLE);
                            rlError.setVisibility(View.GONE);
                            rlNull.setVisibility(View.GONE);
                            dataAdd(conentEntity);
                        }
                    }
                });
    }
    private void dataAdd(ConentEntity<QueryRecommendEntity> conentEntity) {
        for (int i = 0;i<conentEntity.getContent().size();i++){
            QueryRecommendEntity queryRecommendEntity = new QueryRecommendEntity();
            queryRecommendEntity = conentEntity.getContent().get(i);
            recommendEntityList.add(queryRecommendEntity);
        }
        if (attentionAdapter == null || page == 1) {
            attentionAdapter = new AttentionAdapter((BaseActivity) getActivity());
            attentionAdapter.addData(recommendEntityList);
            attentionAdapter.setHasStableIds(true);
            rvAttentionList.setAdapter(attentionAdapter);
            rvAttentionList.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        } else {
            attentionAdapter.addData(recommendEntityList);
            attentionAdapter.notifyDataSetChanged();
        }
    }

    private void httpTop() {
        NetService.getInstance().setQueryRecommendTop()
                .compose(this.bindLifeCycle())
                .subscribe(new CustomApiCallback<ConentEntity<QueryRecommendTopEntity>>() {
                    @Override
                    protected void onResultError(ApiException ex) {
                        LogUtils.e(ex.getDisplayMessage());
                        closeRefresh();
                        rvAttentionTop.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(ConentEntity<QueryRecommendTopEntity> conentEntity) {
                        closeRefresh();
                        rvAttentionTop.setVisibility(View.VISIBLE);
                        recommendtopAdapter = new RecommendtopAdapter(getActivity(), conentEntity);
                        rvAttentionTop.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rvAttentionTop.setAdapter(recommendtopAdapter);
                    }
                });

    }

    public void closeRefresh() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        view = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected void lazyLoad() {
//        if (recommendEntityList.size()<0) {
        account = DataSharedPreferences.getCreatateAccount();

//        if (PublicFuncation.isIntervalTenMin()) {
            if (smartRefreshLayout != null) {
                smartRefreshLayout.autoRefresh(100);
            } else {
                if (stateLayout!=null){
                        stateLayout.showProgressView(getString(R.string.wait));
                    }
                    httpTop();
                    httpRecommend(page, "");
                }
//            }

    }

    @OnClick(R.id.rl_error)
    public void onViewClicked() {
        recommendEntityList.clear();
        if (smartRefreshLayout != null) {
            smartRefreshLayout.autoRefresh(100);
        } else {
            if (stateLayout!=null){
                stateLayout.showProgressView(getString(R.string.wait));
            }
            httpTop();
            httpRecommend(page, "");
        }

    }

}


