package com.kingyon.elevator.uis.adapters.adapterone;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.kingyon.elevator.R;
import com.kingyon.elevator.application.AppContent;
import com.kingyon.elevator.constants.Constants;
import com.kingyon.elevator.entities.CouponItemEntity;
import com.kingyon.elevator.utils.CommonUtil;
import com.kingyon.elevator.utils.RoleUtils;
import com.leo.afbaselibrary.uis.adapters.ItemViewDelegate;
import com.leo.afbaselibrary.uis.adapters.MultiItemTypeAdapter;
import com.leo.afbaselibrary.uis.adapters.holders.CommonHolder;
import com.leo.afbaselibrary.utils.TimeUtil;

import java.util.List;

/**
 * Created by GongLi on 2019/1/10.
 * Email：lc824767150@163.com
 */

public class MyCouponsNormalAdapter extends MultiItemTypeAdapter<Object> {

    CouponItemEntity item;
    public MyCouponsNormalAdapter(Context context, List<Object> mItems) {
        super(context, mItems);
        addItemViewDelegate(1, new CouponItemDelegate());
        addItemViewDelegate(2, new CouponTypeDelegate());
    }

    private class CouponItemDelegate implements ItemViewDelegate<Object> {
        @Override
        public int getItemViewLayoutId() {
            return R.layout.fragment_my_coupons_coupon;
        }

        @Override
        public boolean isForViewType(Object item, int position) {
            return item instanceof CouponItemEntity;
        }

        @Override
        public void convert(CommonHolder holder, Object o, int position) {
            item = (CouponItemEntity) o;

            holder.setSelected(R.id.ll_bg, item.isChoosed());
            holder.setVisible(R.id.ll_bg, item.isExpand());

            boolean discount = TextUtils.equals(Constants.CouponType.DISCOUNT, item.getCoupontype());
            holder.setTextNotHide(R.id.tv_discounts_1, discount ? CommonUtil.getMayTwoFloat(item.getDiscount()) : "￥");
            holder.setTextNotHide(R.id.tv_discounts_2, discount ? "折" : CommonUtil.getMayTwoFloat(item.getMoney()));
            holder.setTextSize(R.id.tv_discounts_1, discount ? 32 : 14);
            holder.setTextSize(R.id.tv_discounts_2, discount ? 14 : 32);
            holder.setTextNotHide(R.id.tv_condition, String.format("满%s可用", CommonUtil.getMayTwoFloat(item.getCouponCondition())));
            holder.setTextNotHide(R.id.tv_name, discount ? "折扣券" : "代金券");
            holder.setBackgroundRes(R.id.ll_juan,discount ? R.mipmap.bg_wallet_discount:R.mipmap.bg_wallet_voucher);

            holder.setTextNotHide(R.id.tv_number, String.format("×%s", item.getCouponsCount()));
            holder.setVisible(R.id.tv_number, RoleUtils.getInstance().roleBeTarget(Constants.RoleType.PARTNER, AppContent.getInstance().getMyUserRole()));

            String adType = item.getAdType() != null ? item.getAdType() : "";
            boolean business = adType.contains(Constants.PLAN_TYPE.BUSINESS);
            boolean diy = adType.contains(Constants.PLAN_TYPE.DIY);
            boolean info = adType.contains(Constants.PLAN_TYPE.INFORMATION);
            String result;
            if (business && diy && info) {
                result = "全部";
            } else if (business && diy && !info) {
                result = "商业广告,DIY";
            } else if (business && !diy && info) {
                result = "商业广告,便民服务";
            } else if (!business && diy && info) {
                result = "DIY,便民服务";
            } else if (business && !diy && !info) {
                result = "仅商业广告可用";
            } else if (!business && diy && !info) {
                result = "仅DIY可用";
            } else if (!business && !diy && info) {
                result = "仅便民服务可用";
            } else {
                result = "全部";
            }
            holder.setTextNotHide(R.id.tv_range, String.format("适用：%s", result));
            holder.setTextNotHide(R.id.tv_expier_time, String.format("过期时间：%s", TimeUtil.getYMdTime(item.getExpiredDate())));
        }
    }

    private class CouponTypeDelegate implements ItemViewDelegate<Object> {
        @Override
        public int getItemViewLayoutId() {
            return R.layout.fragment_my_coupons_type;
        }

        @Override
        public boolean isForViewType(Object item, int position) {
            return item instanceof String;
        }

        @Override
        public void convert(CommonHolder holder, Object o, int position) {
            String couponType = o.toString();
            boolean voucher = TextUtils.equals(Constants.CouponType.VOUCHER, couponType);
            holder.setTextNotHide(R.id.tv_name, voucher ? "代金券" : "折扣券");
            holder.setTextDrawableLeft(R.id.tv_name, voucher ? R.mipmap.ic_card_dai : R.mipmap.ic_card_zhe);
            holder.setVisible(R.id.tv_num,false);
            int num = 0;
//            if (voucher) {
//                num = item.getCouponsCount()+num;
//                holder.setText(R.id.tv_num, num+"");
//            }else {
//                num = item.getCouponsCount()+num;
//                holder.setText(R.id.tv_num, num+"");
//            }
        }
    }
}
