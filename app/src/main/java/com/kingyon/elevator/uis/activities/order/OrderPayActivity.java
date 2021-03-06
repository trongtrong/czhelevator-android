package com.kingyon.elevator.uis.activities.order;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.kingyon.elevator.R;
import com.kingyon.elevator.application.AppContent;
import com.kingyon.elevator.constants.Constants;
import com.kingyon.elevator.entities.FreshOrderEntity;
import com.kingyon.elevator.entities.OrderDetailsEntity;
import com.kingyon.elevator.nets.CustomApiCallback;
import com.kingyon.elevator.nets.NetService;
import com.kingyon.elevator.uis.activities.user.MyWalletActivity;
import com.kingyon.elevator.uis.dialogs.TipDialog;
import com.kingyon.elevator.utils.CommonUtil;
import com.kingyon.elevator.utils.RuntimeUtils;
import com.kingyon.paylibrary.PayListener;
import com.kingyon.paylibrary.alipay.AliPayUtils;
import com.kingyon.paylibrary.entitys.PayWay;
import com.kingyon.paylibrary.wechatpay.WxPayStatusEntity;
import com.kingyon.paylibrary.wechatpay.WxPayUtils;
import com.leo.afbaselibrary.listeners.IWeakHandler;
import com.leo.afbaselibrary.nets.entities.DataEntity;
import com.leo.afbaselibrary.nets.entities.WxPayEntity;
import com.leo.afbaselibrary.nets.exceptions.ApiException;
import com.leo.afbaselibrary.nets.exceptions.PayApiException;
import com.leo.afbaselibrary.nets.exceptions.ResultException;
import com.leo.afbaselibrary.uis.activities.BaseStateRefreshingActivity;
import com.leo.afbaselibrary.utils.TimeUtil;
import com.leo.afbaselibrary.utils.WeakHandler;
import com.zhaoss.weixinrecorded.util.EventBusConstants;
import com.zhaoss.weixinrecorded.util.EventBusObjectEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by GongLi on 2019/1/4.
 * Email???lc824767150@163.com
 */

public class OrderPayActivity extends BaseStateRefreshingActivity implements IWeakHandler, TipDialog.OnOperatClickListener<Double> {
    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_ali_pay)
    TextView tvAliPay;
    @BindView(R.id.tv_wx_pay)
    TextView tvWxPay;
    @BindView(R.id.tv_balance_pay)
    TextView tvBalancePay;

    private String orderId;
    private boolean fromEdit;

    private OrderDetailsEntity detailsEntity;
    private Float myWallet;

    private long countDownTime;
    private Subscription countDownSubscribe;
    private AliPayUtils aliPayUtils;
    private WxPayUtils wxPayUtils;
    private Subscription delaySubscribe;
    private TipDialog<Double> tipDialog;

    @Override
    protected String getTitleText() {
        orderId = getIntent().getStringExtra(CommonUtil.KEY_VALUE_1);
        fromEdit = getIntent().getBooleanExtra(CommonUtil.KEY_VALUE_2, false);
        return "????????????";
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_pay_order;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);
        EventBus.getDefault().register(this);
        WeakHandler weakHandler = new WeakHandler(this);
        aliPayUtils = new AliPayUtils(this, weakHandler);
        wxPayUtils = new WxPayUtils(this);
        tvTips.setText(fromEdit ? "??????????????????" : "????????????");
    }

    @Override
    public void onRefresh() {
        NetService.getInstance().myWallet()
                .compose(this.<DataEntity<Float>>bindLifeCycle())
                .subscribe(new CustomApiCallback<DataEntity<Float>>() {
                    @Override
                    protected void onResultError(ApiException ex) {
                        showToast(ex.getDisplayMessage());
                    }

                    @Override
                    public void onNext(DataEntity<Float> floatDataEntity) {
                        if (floatDataEntity == null) {
                            throw new ResultException(9001, "???????????????????????????");
                        }
                        myWallet = floatDataEntity.getData();
                        tvBalancePay.setText(String.format("????????????%s???", CommonUtil.getMayTwoFloat(myWallet != null ? myWallet : 0)));
                    }
                });
        NetService.getInstance().orderDetatils(orderId)
                .compose(this.<OrderDetailsEntity>bindLifeCycle())
                .subscribe(new CustomApiCallback<OrderDetailsEntity>() {
                    @Override
                    protected void onResultError(ApiException ex) {
                        showToast(ex.getDisplayMessage());
                        loadingComplete(STATE_ERROR);
                    }

                    @Override
                    public void onNext(OrderDetailsEntity orderDetailsEntity) {
                        if (orderDetailsEntity == null) {
                            throw new ResultException(9001, "??????????????????");
                        }
                        detailsEntity = orderDetailsEntity;
                        if (TextUtils.equals(Constants.OrderStatus.WAIT_PAY, orderDetailsEntity.getOrderStatus())) {
                            if (orderDetailsEntity.getRemainTime() <= 0) {
                                showToast("????????????");
                                finish();
                            } else {
                                startOrderCountDown(orderDetailsEntity.getRemainTime());
                                tvPrice.setText(String.format("???%s", CommonUtil.getTwoFloat(orderDetailsEntity.getRealPrice())));
                                loadingComplete(STATE_CONTENT);
                            }
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString(CommonUtil.KEY_VALUE_1, orderId);
                            startActivity(OrderDetailsActivity.class, bundle);
                            finish();
                        }
                    }
                });
    }

    @OnClick({R.id.tv_ali_pay, R.id.tv_wx_pay, R.id.tv_balance_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_ali_pay:
                pay(Constants.PayType.ALI_PAY);
                break;
            case R.id.tv_wx_pay:
                pay(Constants.PayType.WX_PAY);
                break;
            case R.id.tv_balance_pay:
                if (detailsEntity != null) {
                    if (myWallet != null && detailsEntity.getRealPrice() > myWallet) {
                        showToast("????????????");
                        jumtToWallet();
                    } else {
                        showBalancePayDialog(detailsEntity.getRealPrice());
                    }
                } else {
                    showToast("???????????????????????????");
                }
                break;
        }
    }

    private void showBalancePayDialog(double realPrice) {
        if (tipDialog == null) {
            tipDialog = new TipDialog<>(this, this);
        }
        String price = CommonUtil.getTwoFloat(realPrice);
        String tip = String.format("?????????????????????????????????%s????", price);
        int startIndex = tip.indexOf(price);
        SpannableString spannableString = new SpannableString(tip);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)), startIndex, startIndex + price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tipDialog.show(spannableString, "??????", "????????????", realPrice);
    }


    @Override
    public void onEnsureClick(Double param) {
        pay(Constants.PayType.BALANCE_PAY);
    }

    @Override
    public void onCancelClick(Double param) {

    }

    private void pay(String payType) {
        tvAliPay.setEnabled(false);
        tvWxPay.setEnabled(false);
        showProgressDialog(getString(R.string.wait),true);
        setPayEnableDelay();
        NetService.getInstance().orderPay(orderId, payType,"")
                .compose(this.<WxPayEntity>bindLifeCycle())
                .subscribe(new CustomApiCallback<WxPayEntity>() {
                    @Override
                    protected void onResultError(ApiException ex) {

                        if (ex instanceof PayApiException) {
                            WxPayEntity payResultEntity = ((PayApiException) ex).getPayEntity();
                            if (payResultEntity != null) {
                                hideProgress();
                                int code = ex.getCode();
                                switch (code) {
                                    case 8001:
                                        wxPayUtils.payOrder(AppContent.getInstance().getGson().toJson(payResultEntity));
                                        break;
                                    case 8002:
                                        aliPayUtils.pay(payResultEntity.getSign());
                                        break;
                                    default:
                                        showToast("??????????????????");
                                        setPayEnable();
                                        break;
                                }
                            } else {
                                showToast("??????????????????");
                                hideProgress();
                                setPayEnable();
                            }
                        } else {
                            setPayEnable();
                            showToast(ex.getDisplayMessage());
                            hideProgress();
                            if (ex.getCode() == 8005) {
                                jumtToWallet();
                            }
                        }
                    }

                    @Override
                    public void onNext(WxPayEntity wxPayEntity) {
                        if (wxPayEntity == null) {
                            throw new ResultException(9000, "??????????????????");
                        }
                        hideProgress();
                        switch (wxPayEntity.getPayType()) {
                            case Constants.PayType.ALI_PAY:
                                aliPayUtils.pay(wxPayEntity.getSign());
                                break;
                            case Constants.PayType.WX_PAY:
                                wxPayUtils.payOrder(AppContent.getInstance().getGson().toJson(wxPayEntity));
                                break;
                            default:
                                showToast(getString(R.string.pay_Success));
                                EventBus.getDefault().post(new FreshOrderEntity());
                                if (detailsEntity != null) {
                                    Bundle bundle = new Bundle();
                                    detailsEntity.setPayTime(System.currentTimeMillis());
                                    bundle.putParcelable(CommonUtil.KEY_VALUE_1, detailsEntity);
                                    bundle.putString(CommonUtil.KEY_VALUE_2, Constants.PayType.BALANCE_PAY);
                                    startActivity(PaySuccessActivity.class, bundle);
                                }
                                setPayEnable();
                                finish();
                                break;
                        }
                    }
                });
    }

    private void jumtToWallet() {
        startActivity(MyWalletActivity.class);
    }

    private void setPayEnableDelay() {
        delaySubscribe = Observable.just("")
                .delay(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomApiCallback<String>() {
                    @Override
                    public void onNext(String s) {
                        tvAliPay.setEnabled(true);
                        tvWxPay.setEnabled(true);
                    }

                    @Override
                    protected void onResultError(ApiException ex) {

                    }
                });
    }

    private void startOrderCountDown(long duration) {
        closeOrderCountDown();
        this.countDownTime = duration;
        countDownSubscribe = Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .compose(this.<Long>bindLifeCycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CustomApiCallback<Long>() {
                    @Override
                    protected void onResultError(ApiException ex) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        tvTime.setText(String.format("??????%s???????????????", TimeUtil.getRemainingTimeSecond(countDownTime)));
                        if (countDownTime <= 0) {
                            closeOrderCountDown();
                            autoRefresh();
                        }
                        countDownTime -= 1000;
                    }
                });
    }

    protected void closeOrderCountDown() {
        if (countDownSubscribe != null && !countDownSubscribe.isUnsubscribed()) {
            countDownSubscribe.unsubscribe();
            countDownSubscribe = null;
        }
    }


    @Subscribe
    public void onWxPayResult(WxPayStatusEntity wxPayStatusEntity) {
        if (wxPayStatusEntity != null) {
            wxPayUtils.checkResult(wxPayStatusEntity, new PayListener() {
                @Override
                public void onPaySuccess(PayWay payWay) {
                    showToast(getString(R.string.pay_Success));
                    EventBus.getDefault().post(new FreshOrderEntity());
                    if (detailsEntity != null) {
                        Bundle bundle = new Bundle();
                        detailsEntity.setPayTime(System.currentTimeMillis());
                        bundle.putParcelable(CommonUtil.KEY_VALUE_1, detailsEntity);
                        bundle.putString(CommonUtil.KEY_VALUE_2, Constants.PayType.WX_PAY);
                        startActivity(PaySuccessActivity.class, bundle);
                    }
                    EventBus.getDefault().post(new EventBusObjectEntity(EventBusConstants.ReflashPlanList, null));
                    setPayEnable();
                    finish();
                }

                @Override
                public void onPayFailure(PayWay payWay, String reason) {
                    showToast(TextUtils.isEmpty(reason) ? getString(R.string.pay_failed) : reason);
                    setPayEnable();
                }

                @Override
                public void onPayCancel(PayWay payWay) {
                    showToast(getString(R.string.pay_canceled));
                    setPayEnable();
                }

                @Override
                public void onPayConfirm(PayWay payWay) {
                    showToast(getString(R.string.pay_on_ensure));
                    EventBus.getDefault().post(new FreshOrderEntity());
                    Bundle bundle = new Bundle();
                    bundle.putString(CommonUtil.KEY_VALUE_1, orderId);
                    startActivity(OrderDetailsActivity.class, bundle);
                    setPayEnable();
                    finish();
                }
            });
        }
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == AliPayUtils.SDK_PAY_FLAG) {
            aliPayUtils.checkResult((Map<String, String>) msg.obj, new PayListener() {
                @Override
                public void onPaySuccess(PayWay payWay) {
                    showToast(getString(R.string.pay_Success));
                    EventBus.getDefault().post(new FreshOrderEntity());
                    if (detailsEntity != null) {
                        Bundle bundle = new Bundle();
                        detailsEntity.setPayTime(System.currentTimeMillis());
                        bundle.putParcelable(CommonUtil.KEY_VALUE_1, detailsEntity);
                        bundle.putString(CommonUtil.KEY_VALUE_2, Constants.PayType.ALI_PAY);
                        startActivity(PaySuccessActivity.class, bundle);
                    }
                    EventBus.getDefault().post(new EventBusObjectEntity(EventBusConstants.ReflashPlanList, null));
                    setPayEnable();
                    finish();
                }

                @Override
                public void onPayFailure(PayWay payWay, String reason) {
                    showToast(TextUtils.isEmpty(reason) ? getString(R.string.pay_failed) : reason);
                    setPayEnable();
                }

                @Override
                public void onPayCancel(PayWay payWay) {
                    showToast(getString(R.string.pay_canceled));
                    setPayEnable();
                }

                @Override
                public void onPayConfirm(PayWay payWay) {
                    showToast(getString(R.string.pay_on_ensure));
                    EventBus.getDefault().post(new FreshOrderEntity());
                    Bundle bundle = new Bundle();
                    bundle.putString(CommonUtil.KEY_VALUE_1, orderId);
                    startActivity(OrderDetailsActivity.class, bundle);
                    setPayEnable();
                    finish();
                }
            });
        }
    }

    private void setPayEnable() {
        if (delaySubscribe != null && !delaySubscribe.isUnsubscribed()) {
            delaySubscribe.unsubscribe();
        }
        tvAliPay.setEnabled(true);
        tvWxPay.setEnabled(true);
        tvBalancePay.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (fromEdit) {
            Bundle bundle = new Bundle();
            bundle.putString(CommonUtil.KEY_VALUE_1, orderId);
            startActivity(OrderDetailsActivity.class, bundle);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        RuntimeUtils.goPlaceAnOrderEntity = null;
        super.onDestroy();
    }
}
