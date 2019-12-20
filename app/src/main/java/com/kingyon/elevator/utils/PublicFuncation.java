package com.kingyon.elevator.utils;

import android.content.Context;
import android.os.Environment;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created By SongPeng  on 2019/11/29
 * Email : 1531603384@qq.com
 * 一些公共的方法
 */
public class PublicFuncation {

    /**
     * 验证邮箱地址是否正确
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }


    /**
     * 验证手机号码
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        boolean flag = false;
        try {
            Pattern p = Pattern.compile("^((1[0-9][0-9]))\\d{8}$");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * md5加密
     * @param s
     * @return
     */
    public static String md5Digest(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getDiskCacheDir(Context context) {
        String cachePath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
            LogUtils.e("执行外置内存卡的缓存");
        } else {
            cachePath = context.getCacheDir().getPath();
            LogUtils.e("执行内置内存卡的缓存");
        }
        return cachePath;
    }


    /**
     * 创建缓存目录
     */
    public static void creatCacheFolder(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();//创建缓存目录
                LogUtils.e("目录", "创建成功");
            }
        } catch (Exception e) {
            LogUtils.e("创建缓存目录", e.toString());
        }
    }
}
