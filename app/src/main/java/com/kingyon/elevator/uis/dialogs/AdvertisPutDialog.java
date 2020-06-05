package com.kingyon.elevator.uis.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.blankj.utilcode.util.LogUtils;
import com.kingyon.elevator.R;
import com.kingyon.elevator.nets.CustomApiCallback;
import com.kingyon.elevator.nets.NetService;
import com.leo.afbaselibrary.nets.exceptions.ApiException;
import com.leo.afbaselibrary.uis.activities.BaseActivity;
import com.leo.afbaselibrary.utils.ToastUtils;

import java.util.List;

import static com.czh.myversiontwo.utils.CodeType.ADV_BUSINESS;
import static com.czh.myversiontwo.utils.CodeType.ADV_DAY;
import static com.czh.myversiontwo.utils.CodeType.ADV_INFORMATION;

/**
 * Created By Admin  on 2020/4/16
 * Email : 163235610@qq.com
 * Author:Mrczh
 * Instructions:自己删除
 */
public class AdvertisPutDialog extends Dialog implements View.OnClickListener{
    protected BaseActivity mContext;
    View tv_day,tv_business,tv_convenience,share_btn_cancel;
    protected ProgressDialog promotWaitBar;
    private int planId;
    private String planName;

    /**
     *  planId 小区id
     *  planName 计划名字
     *
     * */
    public AdvertisPutDialog(@NonNull BaseActivity context,int planId,String planName) {
        super(context, com.kingyon.library.social.R.style.ShareDialog);
        this.mContext = context;
        this.planId = planId;
        this.planName = planName;
        setContentView(getLayoutId());
        Window window = getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            window.setWindowAnimations(com.kingyon.library.social.R.style.dialog_show_anim);
            window.setGravity(Gravity.BOTTOM);
        }
        promotWaitBar = new ProgressDialog(mContext);
        promotWaitBar.setMessage(getContext().getResources().getString(
                com.kingyon.library.social.R.string.hold_on));
    }


    protected int getLayoutId() {
        return R.layout.dialog_advertis_put;
    }

    public void setPaddingTop(int top) {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.y = top;
            window.setAttributes(lp);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        tv_convenience = findViewById(R.id.tv_convenience);
        tv_business = findViewById(R.id.tv_business);
        tv_day = findViewById(R.id.tv_day);
        share_btn_cancel = findViewById(R.id.share_btn_cancel);

        tv_day.setOnClickListener(this);
        share_btn_cancel.setOnClickListener(this);
        tv_business.setOnClickListener(this);
        tv_convenience.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_convenience:
                /*便民*/
                httpAdd(ADV_INFORMATION);
                break;
            case R.id.tv_day:
                /*day*/
                httpAdd(ADV_DAY);
                break;
            case R.id.tv_business:
                /*商业*/
                httpAdd(ADV_BUSINESS);
                break;
            case R.id.share_btn_cancel:
                dismiss();
                break;

        }

    }

    private void httpAdd(String type) {
        LogUtils.e(planId,planName,type);
        NetService.getInstance().plansAddCells(type, String.valueOf(planId))
                .compose(mContext.bindLifeCycle())
                .subscribe(new CustomApiCallback<String>() {
                    @Override
                    protected void onResultError(ApiException ex) {
                        ToastUtils.showToast(mContext,ex.getDisplayMessage(),1000);
                    }

                    @Override
                    public void onNext(String s) {
                        ToastUtils.showToast(mContext,"添加成功",1000);
                        dismiss();
                    }
                });

    }

}