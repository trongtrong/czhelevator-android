package com.kingyon.elevator.uis.actiivty2.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.LogUtils;
import com.czh.myversiontwo.activity.ActivityUtils;
import com.kingyon.elevator.R;
import com.kingyon.elevator.entities.entities.ConentEntity;
import com.kingyon.elevator.entities.entities.HomeTopicConentEntity;
import com.kingyon.elevator.nets.CustomApiCallback;
import com.kingyon.elevator.nets.NetService;
import com.kingyon.elevator.uis.fragments.main2.found.topic.TopicDetailsFragment;
import com.kingyon.elevator.uis.fragments.main2.found.utilsf.CustomFragmentPagerAdapter;
import com.kingyon.elevator.utils.StatusBarUtil;
import com.kingyon.elevator.utils.utilstwo.ConentUtils;
import com.kingyon.elevator.utils.utilstwo.SharedUtils;
import com.kingyon.elevator.view.ModifyTabLayout;
import com.kingyon.library.social.ShareDialog;
import com.leo.afbaselibrary.nets.exceptions.ApiException;
import com.leo.afbaselibrary.uis.activities.BaseActivity;
import com.leo.afbaselibrary.utils.GlideUtils;
import com.leo.afbaselibrary.widgets.StateLayout;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.czh.myversiontwo.utils.Constance.ACTIVITY_MAIN2_COMMUNITY_RELEASETY;
import static com.czh.myversiontwo.utils.Constance.ACTIVITY_MAIN2_TOPIC_DETAILS;
import static com.kingyon.elevator.uis.fragments.main2.found.AttentionFragment.isRefresh;

/**
 * @Created By Admin  on 2020/4/22
 * @Email : 163235610@qq.com
 * @Author:Mrczh
 * @Instructions: 话题详情
 */
@Route(path = ACTIVITY_MAIN2_TOPIC_DETAILS)
public class TopicDetailsActivity extends BaseActivity {
    @BindView(R.id.img_topimg)
    ImageView imgTopimg;
    @BindView(R.id.tv_topic_title)
    TextView tvTopicTitle;
    @BindView(R.id.img_bake)
    ImageView imgBake;
    @BindView(R.id.img_jb)
    ImageView imgJb;
    @BindView(R.id.tv_topic_contetn)
    TextView tvTopicContetn;
    @BindView(R.id.modiftTabLayout)
    ModifyTabLayout modiftTabLayout;
    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.viewpagertab)
    SmartTabLayout viewpagertab;
    @BindView(R.id.rl_bg)
    RelativeLayout rlBg;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.img_release)
    ImageView img_release;
    HomeTopicConentEntity homeTopicConentEntity;
    @BindView(R.id.stateLayout)
    StateLayout stateLayout;
    private ShareDialog shareDialog;
    @Autowired
    String topicid;
    @Autowired
    String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
//        StatusBarUtil.setTransparent(this);
//        StatusBarUtil.setHeadViewPadding(this, llTop);
        ConentUtils.topicStr = topicid;
        ButterKnife.bind(this);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_topic_details;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        isRefresh = false;
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        StatusBarUtil.setTransparent(this);
        httpTopic();
        stateLayout.setErrorAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpTopic();
            }
        });
    }

    private void httpTopic() {
        LogUtils.e(topicid);
        stateLayout.showProgressView(getString(R.string.wait));
        NetService.getInstance().setQueryTopicConetn(1, "", "", Integer.parseInt(topicid))
                .compose(this.bindLifeCycle())
                .subscribe(new CustomApiCallback<ConentEntity<HomeTopicConentEntity>>() {
                    @Override
                    protected void onResultError(ApiException ex) {
                        LogUtils.e(ex.getDisplayMessage(), ex.getCode());
                        stateLayout.showErrorView();
                    }

                    @Override
                    public void onNext(ConentEntity<HomeTopicConentEntity> conentEntityConentEntity) {
                        homeTopicConentEntity = conentEntityConentEntity.getContent().get(0);
                        tvTopicContetn.setText(homeTopicConentEntity.content);
                        tvTopicTitle.setText(homeTopicConentEntity.title);
                        GlideUtils.loadImage(TopicDetailsActivity.this, homeTopicConentEntity.image, imgTopimg);
                        initData();

                    }
                });

    }


    private void initData() {
        CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TopicDetailsFragment().setIndex("create_time desc", homeTopicConentEntity.id,stateLayout,img_release), "最新");
        adapter.addFrag(new TopicDetailsFragment().setIndex("likes desc", homeTopicConentEntity.id,stateLayout,img_release), "最热");
        vp.setAdapter(adapter);

        vp.setOffscreenPageLimit(adapter.getCount());
        viewpagertab.setViewPager(vp);

    }

    @OnClick({R.id.img_bake, R.id.img_jb, R.id.img_release})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_bake:
                finish();
                break;
            case R.id.img_jb:
                SharedUtils.shared(this, shareDialog, homeTopicConentEntity.content, "www.baidu.com", homeTopicConentEntity.title, false);
                break;
            case R.id.img_release:
                ActivityUtils.setActivity(ACTIVITY_MAIN2_COMMUNITY_RELEASETY,
                        "topicId1", String.valueOf(homeTopicConentEntity.id),
                        "title", homeTopicConentEntity.title);
                break;
        }
    }

    public int dp2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
