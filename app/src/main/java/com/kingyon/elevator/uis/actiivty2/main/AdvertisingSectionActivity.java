package com.kingyon.elevator.uis.actiivty2.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.LogUtils;
import com.kingyon.elevator.R;
import com.kingyon.elevator.mvpbase.MvpBaseActivity;
import com.kingyon.elevator.presenter.presenter2.AdvertisingSectionPresenter;
import com.kingyon.elevator.uis.adapters.adaptertwo.AdvertisingSectionAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.czh.myversiontwo.utils.Constance.ACTIVITY_MAIN2_ADVERTISING;

/**
 * @Created By Admin  on 2020/4/23
 * @Email : 163235610@qq.com
 * @Author:Mrczh
 * @Instructions:广告专区
 */
@Route(path = ACTIVITY_MAIN2_ADVERTISING)
public class AdvertisingSectionActivity extends MvpBaseActivity<AdvertisingSectionPresenter> {
    @BindView(R.id.edit_search)
    EditText editSearch;
    @BindView(R.id.tv_bake)
    TextView tvBake;
    @BindView(R.id.rv_list)
    RecyclerView rvList;

    @Override
    public AdvertisingSectionPresenter initPresenter() {
        return new AdvertisingSectionPresenter(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertising_section);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                LogUtils.e(i);
//                return (i%2) == 0? 2 : 1;
//                if (i%2==0){
//                    return 2;
//                }else {
//                    return 1;
//                }
                return (2 - i % 2);
            }
        });
        rvList.setLayoutManager(gridLayoutManager);
        AdvertisingSectionAdapter advertisingSectionAdapter = new AdvertisingSectionAdapter(this);
        rvList.setAdapter(advertisingSectionAdapter);

    }

    @OnClick(R.id.tv_bake)
    public void onViewClicked() {
        finish();
    }
}
