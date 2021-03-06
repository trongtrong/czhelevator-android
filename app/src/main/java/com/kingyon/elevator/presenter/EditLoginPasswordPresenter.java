package com.kingyon.elevator.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kingyon.elevator.data.DataSharedPreferences;
import com.kingyon.elevator.mvpbase.BasePresenter;
import com.kingyon.elevator.nets.CustomApiCallback;
import com.kingyon.elevator.nets.NetService;
import com.kingyon.elevator.utils.CheckCodePresenter;
import com.kingyon.elevator.utils.PublicFuncation;
import com.kingyon.elevator.view.EditLoginPasswordView;
import com.leo.afbaselibrary.nets.exceptions.ApiException;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created By SongPeng  on 2019/12/3
 * Email : 1531603384@qq.com
 */
public class EditLoginPasswordPresenter extends BasePresenter<EditLoginPasswordView> {
    private int maxCount = 120;
    private Disposable mDisposable;

    public EditLoginPasswordPresenter(Context mContext) {
        super(mContext);
    }


    /**
     * 获取验证码
     *
     * @param phone
     */
    public void getVerCode(String phone) {
        if (phone.isEmpty()) {
            if (isViewAttached()) {
                getView().showShortToast("请输入手机号");
            }
            return;
        }
        if (!PublicFuncation.isMobileNO(phone)) {
            if (isViewAttached()) {
                getView().showShortToast("请输入正确的手机号");
            }
            return;
        }
        if (isViewAttached()) {
            getView().showProgressDialog("验证码发送中...", false);
        }
        NetService.getInstance().sendVerifyCode(phone, CheckCodePresenter.VerifyCodeType.RESETPASSWORD)
                .subscribe(new CustomApiCallback<String>() {
                    @Override
                    protected void onResultError(ApiException ex) {
                        if (isViewAttached()) {
                            getView().showShortToast(ex.getDisplayMessage());
                            getView().hideProgressDailog();
                        }
                        maxCount = 0;
                    }

                    @Override
                    public void onNext(String s) {
                        if (isViewAttached()) {
                            getView().showShortToast("验证码发送成功");
                            getView().hideProgressDailog();
                            startCountDownTime();
                        }
                    }
                });

    }


    //开始倒计时
    private void startCountDownTime() {
        Observable.interval(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long number) {
                        if (isViewAttached()) {
                            getView().showCountDownTime(maxCount);
                            if (maxCount == 0) {
                                maxCount = 120;
                                cancel();
                            } else {
                                maxCount--;
                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 取消订阅
     */
    public void cancel() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }


    public void resetLoginPassword(String phone, String code, String password1, String newPassword2) {
        if (phone.isEmpty()) {
            if (isViewAttached()) {
                getView().showShortToast("请输入手机号");
            }
            return;
        }
        if (!PublicFuncation.isMobileNO(phone)) {
            if (isViewAttached()) {
                getView().showShortToast("请输入正确的手机号");
            }
            return;
        }
        if (code.isEmpty()) {
            if (isViewAttached()) {
                getView().showShortToast("请输入验证码");
            }
            return;
        }
        if (password1.isEmpty()) {
            if (isViewAttached()) {
                getView().showShortToast("请输入密码");
            }
            return;
        }
        if (password1.length() < 6) {
            if (isViewAttached()) {
                getView().showShortToast("密码长度低于6位数，请继续输入");
            }
            return;
        }
        if (isViewAttached()) {
            getView().showProgressDialog("密码重置中...", false);
        }
        NetService.getInstance().resetPassword(phone, code, password1)
                .subscribe(new CustomApiCallback<String>() {
                    @Override
                    protected void onResultError(ApiException ex) {
                        if (isViewAttached()) {
                            getView().showShortToast(ex.getDisplayMessage());
                            getView().hideProgressDailog();
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        if (isViewAttached()) {
                            getView().showShortToast("密码修改成功");
                            DataSharedPreferences.clearLoginInfo();
                            getView().hideProgressDailog();
                            getView().passwordEditSuccess();
                        }
                    }
                });
    }


    public void noCodeEditPassword(String oldPwd, String newPassword, String newPassword2) {
        if (oldPwd.isEmpty()) {
            if (isViewAttached()) {
                getView().showShortToast("请输入旧密码");
            }
            return;
        }
        if (newPassword.isEmpty()) {
            if (isViewAttached()) {
                getView().showShortToast("请输入新密码");
            }
            return;
        }
        if (newPassword2.isEmpty()) {
            if (isViewAttached()) {
                getView().showShortToast("请输入新密码");
            }
            return;
        }
        if (newPassword.length() < 6) {
            if (isViewAttached()) {
                getView().showShortToast("密码长度低于6位数，请继续输入");
            }
            return;
        }
        if (!newPassword.equals(newPassword2)) {
            if (isViewAttached()) {
                getView().showShortToast("两次输入的密码不一致，请重新输入");
            }
            return;
        }
        if (isViewAttached()) {
            getView().showProgressDialog("正在提交..", false);
        }
        NetService.getInstance().changePassword(oldPwd, newPassword)
                .subscribe(new CustomApiCallback<String>() {
                    @Override
                    protected void onResultError(ApiException ex) {
                        if (isViewAttached()) {
                            getView().showShortToast(ex.getDisplayMessage());
                            getView().hideProgressDailog();
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        if (isViewAttached()) {
                            getView().showShortToast("密码修改成功");
                            DataSharedPreferences.clearLoginInfo();
                            getView().hideProgressDailog();
                            getView().passwordEditSuccess();
                        }
                    }
                });
    }

}
