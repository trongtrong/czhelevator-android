package com.kingyon.elevator.uis.activities.salesman;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.LogUtils;
import com.kingyon.elevator.R;
import com.kingyon.elevator.entities.CellItemEntity;
import com.kingyon.elevator.entities.entities.ConentEntity;
import com.kingyon.elevator.nets.CustomApiCallback;
import com.kingyon.elevator.nets.NetService;
import com.kingyon.elevator.uis.activities.devices.CellEditActivity;
import com.kingyon.elevator.utils.CommonUtil;
import com.leo.afbaselibrary.listeners.OnClickWithObjects;
import com.leo.afbaselibrary.nets.exceptions.ApiException;
import com.leo.afbaselibrary.nets.exceptions.ResultException;
import com.leo.afbaselibrary.uis.activities.BaseStateRefreshingLoadingActivity;
import com.leo.afbaselibrary.uis.adapters.BaseAdapter;
import com.leo.afbaselibrary.uis.adapters.MultiItemTypeAdapter;
import com.leo.afbaselibrary.uis.adapters.holders.CommonHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by GongLi on 2018/12/24.
 * Email：lc824767150@163.com
 * 6-17小区管理
 */

public class SalesmanActivity extends BaseStateRefreshingLoadingActivity<CellItemEntity> {

    @BindView(R.id.edit_search)
    EditText editSearch;

    @Override
    protected String getTitleText() {
        return "小区管理";
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_salesman;
    }

    @Override
    protected MultiItemTypeAdapter<CellItemEntity> getAdapter() {
        return new BaseAdapter<CellItemEntity>(this, R.layout.activity_salesman_item, mItems) {
            @Override
            protected void convert(CommonHolder holder, CellItemEntity item, int position) {
                holder.setTextNotHide(R.id.tv_name, item.getCellName());
                holder.setTextNotHide(R.id.tv_region, item.getRegionName());
                holder.setTextNotHide(R.id.tv_lift_num, String.format("%s部电梯", item.getLiftNum()));
                holder.setOnClickListener(R.id.tv_edit, new OnClickWithObjects(item) {
                    @Override
                    public void onClick(View view, Object[] objects) {
                        onEditClick(((CellItemEntity) objects[0]));
                    }
                });
                holder.setVisible(R.id.tv_edit, item.isCanEdit());
            }
        };
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                LogUtils.e(s, start, count, after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogUtils.e(s, start, count, before);

            }

            @Override
            public void afterTextChanged(Editable s) {
                LogUtils.e(s.toString());
                mItems.clear();
                httpData(1, s.toString());

            }
        });

    }

    private void onEditClick(CellItemEntity entity) {
        if (entity != null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(CommonUtil.KEY_VALUE_1, true);
            bundle.putLong(CommonUtil.KEY_VALUE_2, entity.getObjctId());
            startActivityForResult(CellEditActivity.class, CommonUtil.REQ_CODE_1, bundle);
        }
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, CellItemEntity item, int position) {
        super.onItemClick(view, holder, item, position);
        if (item != null) {
            Bundle bundle = new Bundle();
            bundle.putLong(CommonUtil.KEY_VALUE_1, item.getObjctId());
            bundle.putString(CommonUtil.KEY_VALUE_2, item.getCellName());
            bundle.putString(CommonUtil.KEY_VALUE_3, item.getRegionName());
            startActivity(SalesCellBuildActivity.class, bundle);
        }
    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, CellItemEntity item, int position) {
        if (item != null && item.isCanEdit()) {
            showDeleteDialog(item);
        }
        return true;
    }

    private void showDeleteDialog(final CellItemEntity item) {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("删除以后无法找回，确定要删除它吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onDelete(item);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void onDelete(CellItemEntity item) {
        NetService.getInstance().removeCell(item.getObjctId())
                .compose(this.<String>bindLifeCycle())
                .subscribe(new CustomApiCallback<String>() {
                    @Override
                    protected void onResultError(ApiException ex) {
                        showToast(ex.getDisplayMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        showToast("操作成功");
                        autoRefresh();
                    }
                });
    }

    @Override
    protected void loadData(final int page) {
        httpData(page, "");
    }
    private void httpData(int page, String keywords) {
        NetService.getInstance().partnerCellList(null, null, page, keywords)
                .compose(this.<ConentEntity<CellItemEntity>>bindLifeCycle())
                .subscribe(new CustomApiCallback<ConentEntity<CellItemEntity>>() {
                    @Override
                    protected void onResultError(ApiException ex) {
                        showToast(ex.getDisplayMessage());
                        loadingComplete(false, 100000);
                    }

                    @Override
                    public void onNext(ConentEntity<CellItemEntity> cellItemEntityPageListEntity) {
                        if (cellItemEntityPageListEntity == null) {
                            throw new ResultException(9001, "返回参数异常");
                        }
                        if (FIRST_PAGE == page) {
                            mItems.clear();
                        }
                        if (cellItemEntityPageListEntity.getContent() != null) {
                            mItems.addAll(cellItemEntityPageListEntity.getContent());
                        }
                        loadingComplete(true, cellItemEntityPageListEntity.getTotalPages());
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        onfresh();
    }

    @OnClick(R.id.tv_add)
    public void onViewClicked() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(CommonUtil.KEY_VALUE_1, false);
        startActivityForResult(CellEditActivity.class, CommonUtil.REQ_CODE_1, bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
