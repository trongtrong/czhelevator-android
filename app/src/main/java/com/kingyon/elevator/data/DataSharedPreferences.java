package com.kingyon.elevator.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.kingyon.elevator.application.App;
import com.kingyon.elevator.application.AppContent;
import com.kingyon.elevator.entities.AdvertisionEntity;
import com.kingyon.elevator.entities.LatLonCache;
import com.kingyon.elevator.entities.LocationEntity;
import com.kingyon.elevator.entities.LoginResultEntity;
import com.kingyon.elevator.entities.UserEntity;
import com.kingyon.elevator.entities.entities.FingerprintEntiy;
import com.kingyon.elevator.nets.Net;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by gongli on 2017/7/17 17:39
 * email: lc824767150@163.com
 */
public class DataSharedPreferences {
    private static final String LOGIN_RESULT = "LOGIN_RESULT";
    private static final String USER_INFO = "USER_INFO";
    //    private static final String USER_MINE = "USER_MINE";
    private static final String USER_TOKEN = "USER_TOKEN";
    private static final String LOGIN_NAME = "LOGIN_NAME";
    private static final String JPUSH_ID = "JPUSH_ID";
    private static final String LOCATION_ENTITY = "LOCATION_ENTITY";
    private static final String LATLON_CACHE = "LATLON_CACHE";
    private static final String LOADING_ADVERTISING = "LOADING_ADVERTISING";

    public static final String LAST_AD_TIME = "LAST_AD_TIME";//上一次展示的时间
    public static final String LAST_AD_ID = "LAST_AD_ID";//上一次展示广告的id

    public static final String CERTIFY_ID = "CERTIFY_ID";//*保存认证ID*/

    public static final String USER_NAME = "USER_NAME";/*用户名字*/

    public static final String AD_NAME = "AD_NAME";/*保存广告名称*/



    //是否已经显示过用户隐私政策对话框
    public static final String IS_SHOW_ALREADY_PRIVACY_DIALOG = "IS_SHOW_ALREADY_PRIVACY_DIALOG";

    public static final String IS_OPEN_FINGER = "IS_OPEN_FINGER";//是否开启指纹设置

    /*保存用户id*/
    public static final String CREATATE_ACCOUNT ="CREATATE_ACCOUNT";

    /*保存用户昵称*/
    public static  final  String USER_NICKNAME ="USER_NICKNAME";

    /*保存微社区草稿*/
    public static final String SAVE_MICRO_COMMUNITY_DRAFT = "SAVE_MICRO_COMMUNITY_DRAFT";

    /*保存视频草稿*/
    public static final String SAVE_MICRO_VIDEO_DRAFT = "SAVE_MICRO_VIDEO_DRAFT";

    /*保存文章草稿*/
    public static final String SAVE_MICRO_ARTICLE_DRAFT = "SAVE_MICRO_ARTICLE_DRAFT";

    /*保存头像*/
    public static final String SAVE_PORTRAIT = "SAVE_PORTRAIT";

    /*保存发现弹窗*/
    public static final String SAVE_DIALOG = "SAVE_DIALOG";

    /*保存广场弹窗*/
    public static final String SAVE_SQUARE_DIALOG = "SAVE_SQUARE_DIALOG";

    /*保存合伙人弹窗*/
    public static final String SAVE_COOPERATION_DIALOG = "SAVE_COOPERATION_DIALOG";

    /*保存弹窗*/
    public static void saveDialog(boolean saveDialog){
        getPreferences().edit().putBoolean(SAVE_DIALOG, saveDialog).apply();
    }
    public static boolean getDialog() {
        return getPreferences().getBoolean(SAVE_DIALOG, false);
    }




    /*保存弹窗*/
    public static void saveCooperationDialog(boolean saveDialog){
        getPreferences().edit().putBoolean(SAVE_COOPERATION_DIALOG, saveDialog).apply();
    }
    public static boolean getCooperationDialog() {
        return getPreferences().getBoolean(SAVE_COOPERATION_DIALOG, false);
    }

    /*保存指纹状态*/
    public static void saveState(String str){
        List<FingerprintEntiy> list = DataSupport.where("userId = ? "
                , DataSharedPreferences.getCreatateAccount())
                .find(FingerprintEntiy.class);
        LogUtils.e(str,list.toString());

        if (list != null && list.size() > 0) {
            ContentValues values = new ContentValues();
            values.put("isFin", str);
            DataSupport.updateAll(FingerprintEntiy.class, values, "userId = ? "
                    , DataSharedPreferences.getCreatateAccount());
        } else {
            try {
                new FingerprintEntiy(DataSharedPreferences.getCreatateAccount(),str).saveThrows();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /*保存弹窗*/
    public static void saveSquareDialog(boolean saveDialog){
        getPreferences().edit().putBoolean(SAVE_SQUARE_DIALOG, saveDialog).apply();
    }
    public static boolean getSquareDialog() {
        return getPreferences().getBoolean(SAVE_SQUARE_DIALOG, false);
    }




    /*保存头像*/
    public static void savePortrait(String portrait){
        getPreferences().edit().putString(SAVE_PORTRAIT, portrait).apply();
    }
    public static String getPortrait() {
        return getPreferences().getString(SAVE_PORTRAIT, "");
    }

    /*保存广告名称*/
    public static void saveAdName(String adname){
        getPreferences().edit().putString(AD_NAME, adname).apply();
    }
    public static String getAdName() {
        return getPreferences().getString(AD_NAME, "");
    }

    /*保存昵称*/
    public static void saveNickName(String nickname){
        getPreferences().edit().putString(USER_NICKNAME, nickname).apply();
    }
    public static String getNickName() {
        return getPreferences().getString(USER_NICKNAME, "");
    }
    /*保存用户名字*/
    public static void saveUesrName(String username){
        getPreferences().edit().putString(USER_NAME, username).apply();
    }
    public static String getUesrName() {
        return getPreferences().getString(USER_NAME, "");
    }

    /*保存认证ID*/
    public static void saveCertifyId(String certifyId){
        getPreferences().edit().putString(CERTIFY_ID, certifyId).apply();
    }


    public static String getCertifyId() {
        return getPreferences().getString(CERTIFY_ID, "");
    }
    public static void saveCreatateAccount(String creatateAccount ){
        getPreferences().edit().putString(CREATATE_ACCOUNT, creatateAccount).apply();
    }
    public static String getCreatateAccount() {
        return getPreferences().getString(CREATATE_ACCOUNT, "");
    }


    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance().getApplicationContext());
    }

    public static void saveAdvertision(AdvertisionEntity entity) {
        getPreferences().edit().putString(LOADING_ADVERTISING, entity == null ? "" : new Gson().toJson(entity)).apply();
    }

    public static AdvertisionEntity getAdvertision() {
        return new Gson().fromJson(getPreferences().getString(LOADING_ADVERTISING, ""), AdvertisionEntity.class);
    }

    public static void saveUserString(String account) {
        getPreferences().edit().putString(USER_INFO, account).apply();
    }

    private static String getUserString() {
        return getPreferences().getString(USER_INFO, "");
    }

    public static void saveUserBean(UserEntity user) {
        saveUserString(new Gson().toJson(user));
    }

    public static UserEntity getUserBean() {
        return new Gson().fromJson(getUserString(), UserEntity.class);
    }

//    public static void saveMineString(String mineStr) {
//        getPreferences().edit().putString(USER_MINE, mineStr).apply();
//    }
//
//    private static String getMineString() {
//        return getPreferences().getString(USER_MINE, "");
//    }
//
//    public static void saveMineEntity(MineEntity mine) {
//        saveMineString(new Gson().toJson(mine));
//    }
//
//    public static MineEntity getMineEntity() {
//        return new Gson().fromJson(getMineString(), MineEntity.class);
//    }

    public static void saveLoginResult(String result) {
        getPreferences().edit().putString(LOGIN_RESULT, result).apply();
    }

    private static String getLoginResult() {
        return getPreferences().getString(LOGIN_RESULT, "");
    }

    public static void saveLoginResultBean(LoginResultEntity loginResultEntity) {
        saveLoginResult(new Gson().toJson(loginResultEntity));
    }

    public static LoginResultEntity getLoginResultBean() {
        return new Gson().fromJson(getLoginResult(), LoginResultEntity.class);
    }

    public static void saveToken(String token) {
        getPreferences().edit().putString(USER_TOKEN, token).apply();
    }

    public static String getToken() {
        return getPreferences().getString(USER_TOKEN, "");
    }

    public static void saveLoginName(String loginName) {
        getPreferences().edit().putString(LOGIN_NAME, loginName).apply();
    }

    public static String getLoginName() {
        return getPreferences().getString(LOGIN_NAME, "");
    }

    public static void saveLocationCache(LocationEntity entity) {
        if (entity != null) {
            getPreferences().edit().putString(LOCATION_ENTITY, new Gson().toJson(entity)).apply();
        }
    }

    public static LocationEntity getLocationCache() {
        String string = getPreferences().getString(LOCATION_ENTITY, "");
        LocationEntity locationEntity = new Gson().fromJson(string, LocationEntity.class);
        return locationEntity != null ? locationEntity : new LocationEntity("北京", "北京市", "110000", 116.405285D, 39.904989D);
    }

    public static String getLocationStr() {
        return getPreferences().getString(LOCATION_ENTITY, "");
    }

    public static void saveString(String key, String value) {
        getPreferences().edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return getPreferences().getString(key, "");
    }

    public static void saveBoolean(String key, boolean value) {
        getPreferences().edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return getPreferences().getBoolean(key, defaultValue);
    }

    public static void setPushRegisterId(String regId) {
        getPreferences().edit().putString(JPUSH_ID, regId).apply();
    }

    public static String getJPushId() {
        return getPreferences().getString(JPUSH_ID, "");
    }

    public static void clearLoginInfo() {
        Net.getInstance().setToken("");
        saveUserString("");
//        saveMineString("");
        saveLoginResult("");
        saveToken("");
        saveCreatateAccount("");
        saveBoolean(DataSharedPreferences.IS_OPEN_FINGER, false);
        AppContent.getInstance().clear();
    }

    public static void saveLatLon(LatLonCache latLon) {
        getPreferences().edit().putString(LATLON_CACHE, new Gson().toJson(latLon)).apply();
    }

    public static LatLonCache getLatLon() {
        String string = getPreferences().getString(LATLON_CACHE, "");
        return new Gson().fromJson(string, LatLonCache.class);
    }


    public static void saveLong(String key, long value) {
        getPreferences().edit().putLong(key, value).apply();
    }

    public static long getLong(String key) {
        return getPreferences().getLong(key,0l);
    }

    public static void saveInt(String key, int value) {
        getPreferences().edit().putInt(key, value).apply();
    }

    public static int getInt(String key) {
        return getPreferences().getInt(key,-1);
    }
}
