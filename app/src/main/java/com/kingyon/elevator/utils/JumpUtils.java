package com.kingyon.elevator.utils;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.kingyon.elevator.constants.Constants;
import com.kingyon.elevator.entities.ADEntity;
import com.kingyon.elevator.entities.BannerEntity;
import com.kingyon.elevator.entities.MsgNoticeEntity;
import com.kingyon.elevator.entities.NormalMessageEntity;
import com.kingyon.elevator.entities.NormalOptionEntity;
import com.kingyon.elevator.entities.NormalParamEntity;
import com.kingyon.elevator.entities.UserEntity;
import com.kingyon.elevator.uis.activities.HtmlActivity;
import com.kingyon.elevator.uis.activities.MainActivity;
import com.kingyon.elevator.uis.activities.advertising.AdPreviewActivity;
import com.kingyon.elevator.uis.activities.cooperation.CooperationActivity;
import com.kingyon.elevator.uis.activities.homepage.CellDetailsActivity;
import com.kingyon.elevator.uis.activities.homepage.RecommendListActivity;
import com.kingyon.elevator.uis.activities.homepage.WikiDetailsActivity;
import com.kingyon.elevator.uis.activities.homepage.WikiListActivity;
import com.kingyon.elevator.uis.activities.order.OrderDetailsActivity;
import com.kingyon.elevator.uis.activities.property.PropertyActivity;
import com.kingyon.elevator.uis.activities.user.FeedBackDetailsActivity;
import com.kingyon.elevator.uis.activities.user.IdentityCompanyActivity;
import com.kingyon.elevator.uis.activities.user.IdentityPersonActivity;
import com.kingyon.elevator.uis.activities.user.IdentitySuccessActivity;
import com.kingyon.elevator.uis.activities.user.InviteActivity;
import com.kingyon.elevator.uis.activities.user.InviteListActivity;
import com.kingyon.elevator.uis.activities.user.MessageDetailsActivity;
import com.kingyon.elevator.uis.activities.user.MyAdActivity;
import com.kingyon.elevator.uis.activities.user.MyCouponsActivty;
import com.kingyon.elevator.uis.activities.user.MyInvoiceActivity;
import com.leo.afbaselibrary.uis.activities.BaseActivity;
import com.leo.afbaselibrary.utils.AFUtil;

/**
 * Created by GongLi on 2018/9/13.
 * Email???lc824767150@163.com
 */

public class JumpUtils {
    private static JumpUtils jumpUtils;

    public static JumpUtils getInstance() {
        if (jumpUtils == null) {
            jumpUtils = new JumpUtils();
        }
        return jumpUtils;
    }

    public void jumpToRoleMain(BaseActivity baseActivity, UserEntity userEntity) {
        if (userEntity == null) {
            return;
        }
        jumpToRoleMain(baseActivity, userEntity.getRole());
    }

    public void jumpToRoleMain(BaseActivity baseActivity, String role) {
//        if (TextUtils.isEmpty(role)) {
//            baseActivity.startActivity(MainActivity.class);
//        }
        baseActivity.startActivity(MainActivity.class);
    }

    public void onBannerClick(BaseActivity activity, BannerEntity item) {
        if (item == null || TextUtils.isEmpty(item.getType())) {
            return;
        }
        switch (item.getType()) {
            case Constants.BANNER_TYPE.LINK:
                if (item.getLink() != null && item.getLink().startsWith("http://jiali.gzzhkjw.com")) {
                    boolean success = AFUtil.openHtmlBySystem(activity, item.getLink());
                    if (!success) {
                        HtmlActivity.start(activity, "", item.getLink());
                    }
                } else {
                    HtmlActivity.start(activity, "", item.getLink());
                }
                break;
            case Constants.BANNER_TYPE.IMAGE:
                break;
            case Constants.BANNER_TYPE.EXAMPLE:
                ADEntity example = item.getExample();
                if (example == null) {
                    activity.startActivity(RecommendListActivity.class);
                } else {
                    jumpToAdPreview(activity, example,"ad");
                }
                break;
            case Constants.BANNER_TYPE.BAIKE:
                NormalOptionEntity baike = item.getBaike();
                if (baike == null) {
                    activity.startActivity(WikiListActivity.class);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putLong(CommonUtil.KEY_VALUE_1, baike.getObjectId());
                    bundle.putString(CommonUtil.KEY_VALUE_2, baike.getTitle());
                    activity.startActivity(WikiDetailsActivity.class, bundle);
                }
                break;
            case Constants.BANNER_TYPE.INVITE:
                activity.startActivity(InviteActivity.class);
                break;
            case Constants.BANNER_TYPE.CELL:
                Bundle bundle = new Bundle();
                bundle.putLong(CommonUtil.KEY_VALUE_1, item.getJumpId());
                activity.startActivity(CellDetailsActivity.class, bundle);
                break;
        }
    }

    public void jumpToAdPreview(BaseActivity baseActivity, ADEntity entity,String type) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CommonUtil.KEY_VALUE_1, entity);
        bundle.putString(CommonUtil.KEY_VALUE_2, type);
        baseActivity.startActivity(AdPreviewActivity.class, bundle);
    }

    public void jumpToMessagePage(BaseActivity baseActivity, NormalMessageEntity entity) {
        if (entity == null) {
            return;
        }
        String messageType = entity.getType() != null ? entity.getType() : "";
        Bundle bundle = new Bundle();
        switch (messageType) {
            case "ORDER_LIST"://??????
//                EventBus.getDefault().post(new TabEntity(2, new NormalParamEntity("", "????????????")));
//                ActivityUtil.finishAllNotMain();
                MyActivityUtils.goOrderCOntainerActivity(baseActivity, new NormalParamEntity("", "????????????"));
                break;
            case "ORDER"://??????
                bundle.putLong(CommonUtil.KEY_VALUE_1, entity.getExtraId());
                baseActivity.startActivity(OrderDetailsActivity.class, bundle);
                break;
            case "ACQUIRE_COUPONS"://???????????????
                baseActivity.startActivity(MyCouponsActivty.class);
                break;
            case "AD_SUCCED"://??????????????????
                baseActivity.startActivity(MyAdActivity.class);
                break;
            case "AD_FAILED"://??????????????????
                baseActivity.startActivity(MyAdActivity.class);
                break;
            case "PROMOTE_SUCCED"://???????????????
                baseActivity.startActivity(InviteActivity.class);
                break;
            case "PROMOTE_AWARD"://??????????????????
                baseActivity.startActivity(InviteListActivity.class);
                break;
            case "FEEDBACK"://????????????????????????
                bundle.putLong(CommonUtil.KEY_VALUE_1, entity.getExtraId());
                baseActivity.startActivity(FeedBackDetailsActivity.class, bundle);
                break;
            case "PERSON_SUCCED"://??????????????????
                baseActivity.startActivity(IdentitySuccessActivity.class);
                break;
            case "PERSON_FAILED"://??????????????????
                baseActivity.startActivity(IdentityPersonActivity.class);
                break;
            case "COMPANY_SUCCED"://??????????????????
                baseActivity.startActivity(IdentitySuccessActivity.class);
                break;
            case "COMPANY_FAILED"://??????????????????
                baseActivity.startActivity(IdentityCompanyActivity.class);
                break;
            case "PARTNER_SUCCED"://?????????????????????
                baseActivity.startActivity(CooperationActivity.class);
                break;
            case "PARTNER_FAILED"://?????????????????????
                baseActivity.startActivity(CooperationActivity.class);
                break;
            case "PROPERTY_SUCCED"://??????????????????
                baseActivity.startActivity(PropertyActivity.class);
                break;
            case "PROPERTY_FAILED"://??????????????????
                baseActivity.startActivity(PropertyActivity.class);
                break;
            case "INVOICE"://??????
                baseActivity.startActivity(MyInvoiceActivity.class);
                break;
            default:
                bundle.putParcelable(CommonUtil.KEY_VALUE_1, entity);
                baseActivity.startActivity(MessageDetailsActivity.class, bundle);
                break;
        }
        //        PROMOTE_SUCCED ????????????
//        PROMOTE_AWARD ????????????
//        ACQUIRE_COUPONS ???????????????
//        PERSON_SUCCED ??????????????????
//        PERSON_FAILED ??????????????????
//        COMPANY_SUCCED ??????????????????
//        COMPANY_FAILED ??????????????????
//        AD_SUCCED ??????????????????
//        AD_FAILED ??????????????????
//        PARTNER_SUCCED ?????????????????????
//        PARTNER_FAILED ?????????????????????
//        PROPERTY_SUCCED ??????????????????
//        PROPERTY_FAILED ??????????????????
//        ORDER ??????
//        FEEDBACK ????????????
    }


    public void jumpToMessagePage(Context context, MsgNoticeEntity msgNoticeEntity) {
        if (msgNoticeEntity == null) {
            return;
        }
        NormalMessageEntity entity = new NormalMessageEntity();
        entity.setContent(msgNoticeEntity.getContent());
        entity.setObjectId(msgNoticeEntity.getId());
        entity.setTitle(msgNoticeEntity.getTitle() == null ? "" : msgNoticeEntity.getTitle());
        entity.setTime(msgNoticeEntity.getCreateTime());
        entity.setImage(msgNoticeEntity.getImage());
        entity.setLink(msgNoticeEntity.getLink());
        entity.setExtraId(msgNoticeEntity.getExtraId());
        entity.setType(msgNoticeEntity.getTypeChild());
        entity.setUnRead(msgNoticeEntity.isIsRead());
        String messageType = entity.getType() != null ? entity.getType() : "";
        Bundle bundle = new Bundle();
        switch (messageType) {
            case "ORDER_LIST"://??????
                //EventBus.getDefault().post(new TabEntity(2, new NormalParamEntity("", "????????????")));
                MyActivityUtils.goOrderCOntainerActivity(context, new NormalParamEntity("", "????????????"));
                break;
            case "ORDER"://??????
                bundle.putLong(CommonUtil.KEY_VALUE_1, entity.getExtraId());
                MyActivityUtils.goActivity(context, OrderDetailsActivity.class, bundle);
                break;
            case "ACQUIRE_COUPONS"://???????????????
                MyActivityUtils.goActivity(context, MyCouponsActivty.class);
                break;
            case "AD_SUCCED"://??????????????????
                MyActivityUtils.goActivity(context, MyAdActivity.class);
                break;
            case "AD_FAILED"://??????????????????
                LogUtils.d("????????????????????????-------------------------");
                MyActivityUtils.goActivity(context, MyAdActivity.class);
                break;
            case "PROMOTE_SUCCED"://???????????????
                MyActivityUtils.goActivity(context, InviteActivity.class);
                break;
            case "PROMOTE_AWARD"://??????????????????
                MyActivityUtils.goActivity(context, InviteListActivity.class);
                break;
            case "FEEDBACK"://????????????????????????
                bundle.putLong(CommonUtil.KEY_VALUE_1, entity.getExtraId());
                MyActivityUtils.goActivity(context, FeedBackDetailsActivity.class);
                break;
            case "PERSON_SUCCED"://??????????????????
                MyActivityUtils.goActivity(context, IdentitySuccessActivity.class);
                break;
            case "PERSON_FAILED"://??????????????????
                MyActivityUtils.goActivity(context, IdentityPersonActivity.class);
                break;
            case "COMPANY_SUCCED"://??????????????????
                MyActivityUtils.goActivity(context, IdentitySuccessActivity.class);
                break;
            case "COMPANY_FAILED"://??????????????????
                MyActivityUtils.goActivity(context, IdentityCompanyActivity.class);
                break;
            case "PARTNER_SUCCED"://?????????????????????
                MyActivityUtils.goActivity(context, CooperationActivity.class);
                break;
            case "PARTNER_FAILED"://?????????????????????
                MyActivityUtils.goActivity(context, CooperationActivity.class);
                break;
            case "PROPERTY_SUCCED"://??????????????????
                MyActivityUtils.goActivity(context, PropertyActivity.class);
                break;
            case "PROPERTY_FAILED"://??????????????????
                MyActivityUtils.goActivity(context, PropertyActivity.class);
                break;
            case "INVOICE"://??????
                MyActivityUtils.goActivity(context, MyInvoiceActivity.class);
                break;
            default:
                bundle.putParcelable(CommonUtil.KEY_VALUE_1, entity);
                MyActivityUtils.goActivity(context, MessageDetailsActivity.class,bundle);
                break;
        }
    }
}
