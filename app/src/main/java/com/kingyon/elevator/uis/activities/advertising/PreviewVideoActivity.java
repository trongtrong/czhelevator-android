package com.kingyon.elevator.uis.activities.advertising;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.czh.myversiontwo.activity.ActivityUtils;
import com.kingyon.elevator.R;
import com.kingyon.elevator.constants.Constants;
import com.kingyon.elevator.customview.MyActionBar;
import com.kingyon.elevator.interfaces.OnItemClick;
import com.kingyon.elevator.utils.DialogUtils;
import com.kingyon.elevator.utils.MyActivityUtils;
import com.kingyon.elevator.utils.MyStatusBarUtils;
import com.kingyon.elevator.utils.MyToastUtils;
import com.kingyon.elevator.videocrop.EditVideoActivity;
import com.leo.afbaselibrary.utils.GlideUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.umeng.analytics.MobclickAgent;
import com.zhaoss.weixinrecorded.util.EventBusConstants;
import com.zhaoss.weixinrecorded.util.EventBusObjectEntity;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.czh.myversiontwo.utils.CodeType.ACCESS_VOIDE_CODE;
import static com.czh.myversiontwo.utils.Constance.ACTIVITY_MAIN2_VOIDE_RELEASETY;

/**
 * 裁剪视频之后的预览界面
 */
public class PreviewVideoActivity extends AppCompatActivity {
    private String videoPath = "";
    private long videoTime = 0;
    @BindView(R.id.video_view)
    StandardGSYVideoPlayer gsyVideoView;
    @BindView(R.id.my_action_bar)
    MyActionBar my_action_bar;
    @BindView(R.id.iv_preview_video_img)
    ImageView iv_preview_video_img;
    @BindView(R.id.tv_video_time)
    TextView tv_video_time;
    @BindView(R.id.video_view_container)
    RelativeLayout video_view_container;
    private Boolean isClickPlay = false;//是否点击了播放
    private int fromType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyStatusBarUtils.setStatusBar(this, "#ffffff");
        setContentView(R.layout.activity_preview_video);
        ButterKnife.bind(this);
        my_action_bar.setLeftIconClick(new OnItemClick() {
            @Override
            public void onItemClick(int position) {
                DialogUtils.getInstance().showBackTipsDialog(PreviewVideoActivity.this, new OnItemClick() {
                    @Override
                    public void onItemClick(int position) {
                        finish();
                    }
                });
            }
        });
        videoPath = getIntent().getStringExtra("videoPath");
        videoTime = getIntent().getLongExtra("videoTime", 0);
        fromType = getIntent().getIntExtra("fromType", 0);
        if (videoPath.equals("")) {
            MyToastUtils.showShort("视频路径为空");
            finish();
            return;
        }
        tv_video_time.setText("视频时长" + videoTime / 1000 + "s");
        GlideUtils.loadLocalFrame(this, videoPath, iv_preview_video_img);
        gsyVideoView.setUp(videoPath, true, "");
        gsyVideoView.getBackButton().setVisibility(View.GONE);
        gsyVideoView.getFullscreenButton().setVisibility(View.GONE);
        gsyVideoView.startPlayLogic();
        // video_view.start();
    }

    @OnClick({R.id.go_place_an_order})
    public void onClick(View view) {
            switch (view.getId()) {
                case R.id.go_place_an_order:
//                MyActivityUtils.goConfirmOrderActivity(PreviewVideoActivity.this,
//                        Constants.FROM_TYPE.MEDIADATA, videoPath, Constants.Materia_Type.VIDEO);
//                finish();
                    EditVideoActivity.editVideoActivity.finish();
                    if (fromType== Constants.FROM_TYPE_TO_SELECT_MEDIA.MYADSELECT){
                        EventBus.getDefault().post(new EventBusObjectEntity(EventBusConstants.VideoCropSuccessResult, videoPath));
                        finish();
                    }else if (fromType==ACCESS_VOIDE_CODE){
                        ActivityUtils.setActivity(ACTIVITY_MAIN2_VOIDE_RELEASETY,"videoPath",videoPath);
                        finish();
                    }else {
                        MyActivityUtils.goConfirmOrderActivity(PreviewVideoActivity.this,
                                Constants.FROM_TYPE.MEDIADATA, videoPath, Constants.Materia_Type.VIDEO);
                        finish();
                    }

                    break;
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        GSYVideoManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        GSYVideoManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        DialogUtils.getInstance().showBackTipsDialog(PreviewVideoActivity.this, new OnItemClick() {
            @Override
            public void onItemClick(int position) {
                finish();
            }
        });
    }
}
