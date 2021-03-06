package com.kingyon.elevator.uis.activities.cooperation;

import android.os.Bundle;
import android.os.Parcel;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.kingyon.elevator.R;
import com.kingyon.elevator.customview.MyActionBar;
import com.kingyon.elevator.data.DataSharedPreferences;
import com.kingyon.elevator.entities.BindAccountEntity;
import com.kingyon.elevator.entities.CooperationInfoNewEntity;
import com.kingyon.elevator.entities.entities.UserCashTypeListEnity;
import com.kingyon.elevator.mvpbase.MvpBaseActivity;
import com.kingyon.elevator.presenter.AddNewBankCardPresenter;
import com.kingyon.elevator.utils.CommonUtil;
import com.kingyon.elevator.utils.MyActivityUtils;
import com.kingyon.elevator.utils.RuntimeUtils;
import com.kingyon.elevator.view.AddNewBankCardView;
import com.zhaoss.weixinrecorded.util.EventBusConstants;
import com.zhaoss.weixinrecorded.util.EventBusObjectEntity;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNewBankCardActivity extends MvpBaseActivity<AddNewBankCardPresenter> implements AddNewBankCardView {

    @BindView(R.id.my_action_bar)
    MyActionBar myActionBar;
    @BindView(R.id.tv_zhname)
    TextView tvZhname;
    @BindView(R.id.tv_conent)
    TextView tvConent;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.ll_name)
    LinearLayout llName;
    @BindView(R.id.et_zh)
    EditText etZh;
    @BindView(R.id.ll_zh)
    LinearLayout llZh;
    @BindView(R.id.et_kfh)
    EditText etKfh;
    @BindView(R.id.ll_kfh)
    LinearLayout llKfh;
    @BindView(R.id.view_kfh)
    View viewKfh;
    @BindView(R.id.tv_confirm_bind)
    TextView tvConfirmBind;
    private String bingType;

    private CooperationInfoNewEntity entity;
    BindAccountEntity bindAccountEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_bank_card);
        ButterKnife.bind(this);
        bindAccountEntity = new BindAccountEntity();
        entity = getIntent().getParcelableExtra(CommonUtil.KEY_VALUE_1);
        if (entity == null) {
            entity = new CooperationInfoNewEntity();
        }
        bingType = getIntent().getStringExtra("value1");
        tvName.setText(DataSharedPreferences.getUesrName());
        switch (bingType){
            case "3":
                tvZhname.setText("??????????????????");
                tvConent.setText("????????????????????????????????????????????????????????????????????????");
                etZh.setHint("????????????????????????????????????");
                llKfh.setVisibility(View.GONE);
                viewKfh.setVisibility(View.GONE);
                break;
            case "2":
                tvZhname.setText("?????????????????????");
                tvConent.setText("????????????????????????????????????????????????????????????????????????");
                llKfh.setVisibility(View.GONE);
                etZh.setHint("???????????????????????????????????????");
                viewKfh.setVisibility(View.GONE);
                break;
            case "1":
                tvZhname.setText("???????????????");
                tvConent.setText("???????????????????????????????????????????????????????????????");
                etZh.setHint("???????????????????????????????????????");
                llKfh.setVisibility(View.GONE);
                viewKfh.setVisibility(View.GONE);
                llKfh.setVisibility(View.VISIBLE);
                viewKfh.setVisibility(View.VISIBLE);
                break;
                default:
        }
    }

    @Override
    public AddNewBankCardPresenter initPresenter() {
        return new AddNewBankCardPresenter(this);
    }

    @OnClick({R.id.tv_confirm_bind})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_confirm_bind:
                presenter.checkBindAccountData(bingType,
                        etZh.getText().toString().trim(),
                        tvName.getText().toString().trim(),
                        etKfh.getText().toString().trim());
                break;
        }
    }

    @Override
    public void bindSuccess(String bingType, String account, String name, String kaihuhang) {
        Parcel in =  Parcel.obtain();
        Bundle bundle = new Bundle();
        bundle.putParcelable(CommonUtil.KEY_VALUE_1, entity);
        UserCashTypeListEnity bindAccountEntity = new UserCashTypeListEnity(in);
        bindAccountEntity.cashType = Integer.parseInt(bingType);
        bindAccountEntity.cashAccount = account;
        bindAccountEntity.cashName = name;
        bindAccountEntity.openingBank = kaihuhang;
        bundle.putParcelable(CommonUtil.KEY_VALUE_2, bindAccountEntity);
        MyActivityUtils.goActivity(AddNewBankCardActivity.this, CooperationWithdrawActivity.class, bundle);

        EventBus.getDefault().post(new EventBusObjectEntity(EventBusConstants.ReflashBindAccountList, null));
        finish();
    }

}
