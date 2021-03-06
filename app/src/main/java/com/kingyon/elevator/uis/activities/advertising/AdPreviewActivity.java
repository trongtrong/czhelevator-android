package com.kingyon.elevator.uis.activities.advertising;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kingyon.elevator.R;
import com.kingyon.elevator.constants.Constants;
import com.kingyon.elevator.entities.ADEntity;
import com.kingyon.elevator.utils.CommonUtil;
import com.kingyon.elevator.utils.MusicUtils;
import com.kingyon.elevator.utils.StatusBarUtil;
import com.kingyon.elevator.view.AlwaysMarqueeTextView;
import com.leo.afbaselibrary.uis.activities.BaseSwipeBackActivity;
import com.leo.afbaselibrary.utils.GlideUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GongLi on 2019/1/21.
 * Email：lc824767150@163.com
 */

public class AdPreviewActivity extends BaseSwipeBackActivity {


    @BindView(R.id.video_view)
    StandardGSYVideoPlayer video_view;
    @BindView(R.id.fl_video)
    FrameLayout flVideo;
    @BindView(R.id.img_image)
    ImageView imgImage;
    @BindView(R.id.ll_incise)
    LinearLayout llIncise;
    @BindView(R.id.img_top_back)
    ImageView imgTopBack;
    @BindView(R.id.tv_title)
    AlwaysMarqueeTextView tvTitle;
    @BindView(R.id.rl_top)
    RelativeLayout rlTop;
    private ADEntity entity;
    private String type;
    private boolean firstPlay = true;
    private boolean hasVideo;

    @Override
    protected String getTitleText() {
        entity = getIntent().getParcelableExtra(CommonUtil.KEY_VALUE_1);
        type = getIntent().getStringExtra(CommonUtil.KEY_VALUE_2);
        return "广告";
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_ad_preview;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        StatusBarUtil.setHeadViewPadding(this, rlTop);
        StatusBarUtil.setTransparent(this, false);
        if (type.equals("order")){
            tvTitle.setText(entity.getName());
            switch (entity.getTypeAdvertise()) {
                case Constants.AD_SCREEN_TYPE.FULL_VIDEO:
                    flVideo.setVisibility(View.VISIBLE);
                    imgImage.setVisibility(View.GONE);
                    hasVideo = true;
                    initVideoPlay(entity.getUrlVideo());
                    break;
                case Constants.AD_SCREEN_TYPE.FULL_IMAGE:
                    flVideo.setVisibility(View.GONE);
                    imgImage.setVisibility(View.VISIBLE);
                    GlideUtils.loadImage(this, entity.getUrlImate(), imgImage);
                    break;
                case Constants.AD_SCREEN_TYPE.VIDEO_IMAGE:
                    flVideo.setVisibility(View.VISIBLE);
                    imgImage.setVisibility(View.VISIBLE);
                    hasVideo = true;
                    initVideoPlay(entity.getUrlVideo());
                    GlideUtils.loadImage(this, entity.getUrlImate(), imgImage);
                    break;
                default:
                    flVideo.setVisibility(View.GONE);
                    imgImage.setVisibility(View.GONE);
                    break;
            }
        }else {
            tvTitle.setText(entity.getTitle());
            switch (entity.getScreenType()) {
                case Constants.AD_SCREEN_TYPE.FULL_VIDEO:
                    flVideo.setVisibility(View.VISIBLE);
                    imgImage.setVisibility(View.GONE);
                    hasVideo = true;
                    initVideoPlay(entity.getVideoUrl());
                    break;
                case Constants.AD_SCREEN_TYPE.FULL_IMAGE:
                    flVideo.setVisibility(View.GONE);
                    imgImage.setVisibility(View.VISIBLE);
                    GlideUtils.loadImage(this, entity.getImageUrl(), imgImage);
                    break;
                case Constants.AD_SCREEN_TYPE.VIDEO_IMAGE:
                    flVideo.setVisibility(View.VISIBLE);
                    imgImage.setVisibility(View.VISIBLE);
                    hasVideo = true;
                    initVideoPlay(entity.getVideoUrl());
                    GlideUtils.loadImage(this, entity.getImageUrl(), imgImage);
                    break;
                default:
                    flVideo.setVisibility(View.GONE);
                    imgImage.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void initVideoPlay(String VideoUrl) {
        video_view.setUp(VideoUrl, true, "");
        video_view.getBackButton().setVisibility(View.GONE);
        video_view.getFullscreenButton().setVisibility(View.GONE);
        video_view.startPlayLogic();
    }

    @Override
    public void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
        if (!TextUtils.isEmpty(entity.getBgMusic()) && MusicUtils.getInstance().isPlaying(entity.getBgMusic())) {
            MusicUtils.getInstance().pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        GSYVideoManager.onResume();
    }

    private void startPlay() {
        if (!TextUtils.isEmpty(entity.getBgMusic()) && !MusicUtils.getInstance().isPlaying(entity.getBgMusic())) {
            MusicUtils.getInstance().play(entity.getBgMusic());
        }
    }

    @Override
    protected void onDestroy() {
        GSYVideoManager.releaseAllVideos();
        if (!TextUtils.isEmpty(entity.getBgMusic())) {
            MusicUtils.getInstance().clear();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.fl_video, R.id.img_top_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_video:
                startPlay();
                break;
            case R.id.img_top_back:
                finish();
                break;
        }
    }
}
