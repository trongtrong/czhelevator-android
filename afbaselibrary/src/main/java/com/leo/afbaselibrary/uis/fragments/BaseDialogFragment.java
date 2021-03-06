package com.leo.afbaselibrary.uis.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leo.afbaselibrary.mvp.presenters.BasePresenter;
import com.leo.afbaselibrary.mvp.views.IBaseView;
import com.leo.afbaselibrary.uis.activities.BaseActivity;
import com.leo.afbaselibrary.utils.RxCheckLifeCycleTransformer;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.subjects.BehaviorSubject;

/**
 * Created by GongLi on 2018/7/23.
 * Email：lc824767150@163.com
 */

public abstract class BaseDialogFragment extends AppCompatDialogFragment implements IBaseView {
    protected View mRoot;
    protected Unbinder unbinder;
    protected BasePresenter<IBaseView> mPresenter;
    protected BehaviorSubject<RxCheckLifeCycleTransformer.LifeCycleEvent> eventBehaviorSubject = BehaviorSubject.create();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot = LayoutInflater.from(getActivity()).inflate(getContentViewId(), container, false);
        unbinder = ButterKnife.bind(this, mRoot);
        mPresenter = new BasePresenter<IBaseView>(getActivity(), mRoot);
        eventBehaviorSubject.onNext(RxCheckLifeCycleTransformer.LifeCycleEvent.CREATE_VIEW);
        init(savedInstanceState);
        return mRoot;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        eventBehaviorSubject.onNext(RxCheckLifeCycleTransformer.LifeCycleEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override
    public final <T extends View> T getView(int id) {
        return mPresenter.getView(id);
    }

    @Override
    public void showToast(String message) {
        mPresenter.showToast(message);
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void startActivity(Class clazz) {
        mPresenter.startActivity(clazz, null);
    }

    @Override
    public void startActivity(Class clazz, Bundle bundle) {
        mPresenter.startActivity(clazz, bundle);
    }

    @Override
    public void startActivityForResult(Class clazz, int requestCode) {
        mPresenter.startActivityForResult(clazz, requestCode, null);
    }

    @Override
    public void startActivityForResult(Class clazz, int requestCode, Bundle bundle) {
        mPresenter.startActivityForResult(clazz, requestCode, bundle);
    }

    @Override
    public abstract int getContentViewId();

    @Override
    public abstract void init(Bundle savedInstanceState);

    @Override
    public void onResume() {
        super.onResume();
        eventBehaviorSubject.onNext(RxCheckLifeCycleTransformer.LifeCycleEvent.RESUME);
//        mobResume();
    }

    private void mobResume() {
        MobclickAgent.onPageStart(getClass().getSimpleName());//fragment页面统计
    }

    @Override
    public void onPause() {
        super.onPause();
//        mobPause();
    }

    private void mobPause() {
        MobclickAgent.onPageEnd(getClass().getSimpleName());//fragment页面统计
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventBehaviorSubject.onNext(RxCheckLifeCycleTransformer.LifeCycleEvent.DESTROY);
        dealLeackCanary();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        eventBehaviorSubject.onNext(RxCheckLifeCycleTransformer.LifeCycleEvent.DETACH);
    }

    /**
     * 把Observable的生命周期与Activity绑定
     *
     * @param <D>
     * @return
     */
    protected <D> RxCheckLifeCycleTransformer<D> bindLifeCycle() {
        return new RxCheckLifeCycleTransformer<D>(eventBehaviorSubject);
    }


    protected abstract void dealLeackCanary();


    public void showProgressDialog(String message) {
        if (getActivity() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.showProgressDialog(message,true);
        }
    }

    public void hideProgress() {
        if (getActivity() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.hideProgress();
        }
    }
}
