package com.kingyon.elevator.utils.utilstwo;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.czh.myversiontwo.utils.QuickClickUtils;
import com.kingyon.elevator.entities.NewsSharedEntity;
import com.kingyon.elevator.uis.actiivty2.content.ContentDetailsActivity;
import com.kingyon.library.social.BaseSharePramsProvider;
import com.kingyon.library.social.ShareDialog;
import com.leo.afbaselibrary.utils.ToastUtils;

/**
 * @Created By Admin  on 2020/5/13
 * @Email : 163235610@qq.com
 * @Author:Mrczh
 * @Instructions:
 */
public class SharedUtils {

    public static  void shared(Context context,ShareDialog shareDialog,String summary,String shareUrl,String title) {
        try {
            if (QuickClickUtils.isFastClick()) {
                NewsSharedEntity newsSharedEntity = new NewsSharedEntity();
                newsSharedEntity = new NewsSharedEntity();
                newsSharedEntity.setShareContent(summary);
                newsSharedEntity.setShareLink(shareUrl);
                newsSharedEntity.setShareTitle(title);
                if (shareDialog == null) {
                    BaseSharePramsProvider<NewsSharedEntity> baseSharePramsProvider = new BaseSharePramsProvider<>(context);
                    baseSharePramsProvider.setShareEntity(newsSharedEntity);
                    shareDialog = new ShareDialog(context, baseSharePramsProvider);
                }
                shareDialog.show();
            }
        }catch (Exception e){
            ToastUtils.showToast(context,"分享没有准备好 请稍等再试",1000);
        }
    }
}