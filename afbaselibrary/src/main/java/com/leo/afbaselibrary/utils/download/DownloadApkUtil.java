package com.leo.afbaselibrary.utils.download;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.leo.afbaselibrary.R;
import com.leo.afbaselibrary.uis.activities.BaseActivity;
import com.leo.afbaselibrary.utils.AFUtil;
import com.leo.afbaselibrary.utils.ScreenUtil;
import com.leo.afbaselibrary.utils.TimeUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by gongli on 2017/1/19 11:25
 * email: lc824767150@163.com
 */

public class DownloadApkUtil {
    public static int DownloadApkCallBackType = 4254;
    public static int DOWNLOAD_STARTED = -1;
    public static int DOWNLOAD_PAUSED = -2;
    public static int DOWNLOAD_ERROR = -3;
    public static int DOWNLOAD_WAIT = -4;
    private DownloadProgressDialog downloadDialog;
    private String newVersionName;
    private int versionCode;

    private DownloadApkUtil(Context context) {
        versionCode = AFUtil.getVersionCode(context);
    }

    private static DownloadApkUtil downloadApkUtil;

    public static DownloadApkUtil getInstance(Context context) {
        if (downloadApkUtil == null) {
            downloadApkUtil = new DownloadApkUtil(context);
        }
        return downloadApkUtil;
    }

    private String getApkPath(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "update";
    }

    private String getDownloadPath(Context context) {
        return getApkPath(context) + File.separator + getApplicationName(context) + "_" + newVersionName + ".apk";
    }

    public void isDownloadNewVersion(BaseActivity baseActivity, VersionInfo versionInfo) {
        isDownloadNewVersion(baseActivity, versionInfo, true);
    }

    public void isDownloadNewVersion(final BaseActivity baseActivity, final VersionInfo versionInfo, boolean showIsNew) {
        if (versionInfo == null) {
            showHintDialog(baseActivity, "??????????????????", showIsNew);
            return;
        }
        newVersionName = versionInfo.getVersionName();
        int newVersionCode = versionInfo.getVersionCode();
        if (newVersionCode > versionCode && !TextUtils.isEmpty(versionInfo.getUrl())) {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            baseActivity.checkPermission(new BaseActivity.CheckPermListener() {
                @Override
                public void agreeAllPermission() {
                    /*??????????????????*/
                    showHasNewVersionDialog(baseActivity, versionInfo);
                }
            }, "?????????????????????????????????????????????????????????????????????????????????", perms);
        } else {
//            if (TextUtils.isEmpty(versionInfo.getUrl())) {
//                showHintDialog(context, "??????????????????", showIsNew);
//            } else {
            showHintDialog(baseActivity, "??????????????????", showIsNew);
//            }
        }
    }

    private void showHintDialog(Context context, String info, boolean showIsNew) {
        if (showIsNew) {
            new AlertDialog.Builder(context)
                    .setMessage(info)
                    .setPositiveButton("??????", null)
                    .show();
        }
    }

    private void showHasNewVersionDialog(final BaseActivity baseActivity, final VersionInfo versionInfo) {
        String versionName = TextUtils.isEmpty(versionInfo.getVersionName()) ? "" : "v???\nV" + versionInfo.getVersionName();
        String timeInfo = versionInfo.getUpdateTime() == 0 ? "" : "???????????????\n" + TimeUtil.getAllTimeNoSecond(versionInfo.getUpdateTime());
        String contentInfo = TextUtils.isEmpty(versionInfo.getContent()) ? "" : "???????????????\n" + versionInfo.getContent();

        //?????????????????????
        String message = (TextUtils.isEmpty(versionName) ? versionName : versionName + "\n") + timeInfo;
        message = (TextUtils.isEmpty(message) ? message : message + "\n") + contentInfo;
        SpannableString messageSpanString = new SpannableString(message);

        if (message.contains("????????????")) {
            ForegroundColorSpan updateTimeColor = new ForegroundColorSpan(baseActivity.getResources().getColor(R.color.colorAccent));
            messageSpanString.setSpan(updateTimeColor, message.indexOf("???????????????"), message.indexOf("???????????????") + 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (message.contains("???????????????")) {
            ForegroundColorSpan updateTimeColor = new ForegroundColorSpan(baseActivity.getResources().getColor(R.color.colorAccent));
            messageSpanString.setSpan(updateTimeColor, message.indexOf("???????????????"), message.indexOf("???????????????") + 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (message.contains("???????????????")) {
            ForegroundColorSpan updateContentColor = new ForegroundColorSpan(baseActivity.getResources().getColor(R.color.colorAccent));
            messageSpanString.setSpan(updateContentColor, message.indexOf("???????????????"), message.indexOf("???????????????") + 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        AbsoluteSizeSpan messageSizeSpan = new AbsoluteSizeSpan(ScreenUtil.dp2px(14));
        messageSpanString.setSpan(messageSizeSpan, 0, message.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //???????????????????????????
        String title = "????????????";
        SpannableString titleSpanString = new SpannableString(title);
        AbsoluteSizeSpan titleSizeSpan = new AbsoluteSizeSpan(ScreenUtil.sp2px(18));
        titleSpanString.setSpan(titleSizeSpan, 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        final AlertDialog dialog = new AlertDialog.Builder(baseActivity).create();
        dialog.show();  //??????????????????window.setContentView??????show
        Window window = dialog.getWindow();
        window.setContentView(R.layout.version_dialog);
        TextView tv_content =  window.findViewById(R.id.tv_content);
        ImageView tv_close =  window.findViewById(R.id.tv_close);
        TextView tv_version =  window.findViewById(R.id.tv_version);
        TextView tv_next =  window.findViewById(R.id.tv_next);

        tv_content.setText(contentInfo);
        tv_version.setText(versionName);
        //????????????????????????????????????
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadNewApk(baseActivity, versionInfo.getUrl(), versionInfo.isMandatory());
            }
        });
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

//        AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity).setTitle(titleSpanString)
//                .setMessage(messageSpanString)
//                .setPositiveButton("????????????", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
        if (!versionInfo.isMandatory()) {
//            builder.setNegativeButton("????????????", null);
            tv_close.setVisibility(View.VISIBLE);
        } else {
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    boolean result;
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        if (baseActivity instanceof BaseActivity) {
                            ((BaseActivity) baseActivity).finish();
                        }
                        result = true;
                    } else {
                        result = false;
                    }
                    return result;
                }
            });
        }
//        AlertDialog alertDialog = builder.create();
//        alertDialog.setCancelable(!versionInfo.isMandatory());
//        alertDialog.setCanceledOnTouchOutside(!versionInfo.isMandatory());
//        alertDialog.show();
    }

    private void downloadNewApk(final Context context, String url, boolean mandatory) {
        if (downloadDialog == null) {
            downloadDialog = new DownloadProgressDialog(context);
        }
        downloadDialog.setCancelable(!mandatory);
        downloadDialog.setCanceledOnTouchOutside(!mandatory);
        if (mandatory) {
            downloadDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    boolean result;
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        downloadDialog.dismiss(true);
                        if (context instanceof BaseActivity) {
                            ((BaseActivity) context).finish();
                        }
                        result = true;
                    } else {
                        result = false;
                    }
                    return result;
                }
            });
        }
        downloadDialog.setDownloadTask(creatDownloadFileTask(context, url, getDownloadPath(context), DownloadApkCallBackType));
        downloadDialog.show();
        downloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onDestroy();
            }
        });
    }

    public BaseDownloadTask creatDownloadFileTask(final Context context, String url, String path, final int type) {
        BaseDownloadTask baseDownloadTask = FileDownloader.getImpl().create(url);
        baseDownloadTask.setPath(path)
                .setListener(new FileDownloadListener() {

                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        EventBus.getDefault().post(new ApkDownloadProgressEntity(type, DownloadApkUtil.DOWNLOAD_STARTED));
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        int currentProgress = (int) (((long) soFarBytes) * 100 / totalBytes);
                        EventBus.getDefault().post(new ApkDownloadProgressEntity(type, currentProgress));
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        EventBus.getDefault().post(new ApkDownloadProgressEntity(type, 100));
                        installApk(context, task.getPath());
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        EventBus.getDefault().post(new ApkDownloadProgressEntity(type, DownloadApkUtil.DOWNLOAD_PAUSED));
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        EventBus.getDefault().post(new ApkDownloadProgressEntity(type, DownloadApkUtil.DOWNLOAD_ERROR));
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        EventBus.getDefault().post(new ApkDownloadProgressEntity(type, DownloadApkUtil.DOWNLOAD_WAIT));
                    }
                });
        return baseDownloadTask;
    }

    private void installApk(Context context, String path) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        String path = getDownloadPath(context);
//        intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
//        if (context != null) {
//            context.startActivity(intent);
//        }
        File apkFile = new File(path);
        if (apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //???????????????AndroidN?????????????????????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, AFUtil.getAppProcessName(context) + ".provider", apkFile);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        }
    }

    public void clearAsync(final Context context, final ApkFileClearListener listener) {
        Thread clearThread = new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(getApkPath(context));
                boolean flag = true;
                if (file.exists() && file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files == null || files.length < 1) {
                        return;
                    }
                    String appName = getApplicationName(context);
                    for (File childFile : files) {
                        if (childFile.getName().contains(appName) && !childFile.delete()) {
                            flag = false;
                        }
                    }
                }
                if (listener != null) {
                    listener.deleted(flag);
                }
            }
        });
        clearThread.start();
    }

    public boolean clear(Context context) {
        File file = new File(getApkPath(context));
        boolean flag;
        if (file.exists()) {
            if (file.isDirectory()) {
                flag = deleteFilesInDir(file);
            } else if (file.isFile()) {
                flag = file.delete();
            } else {
                return false;
            }
        } else {
            return false;
        }
        return flag;
    }

    private boolean deleteFilesInDir(final File dir) {
        if (dir == null) return false;
        // ?????????????????????true
        if (!dir.exists()) return true;
        // ??????????????????false
        if (!dir.isDirectory()) return false;
        // ?????????????????????????????????
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) return false;
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) return false;
                }
            }
        }
        return true;
    }

    private boolean deleteDir(final File dir) {
        if (dir == null) return false;
        // ?????????????????????true
        if (!dir.exists()) return true;
        // ??????????????????false
        if (!dir.isDirectory()) return false;
        // ?????????????????????????????????
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) return false;
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * @return size
     */
    public long getCacheSize(Context context) {
        File file = new File(getApkPath(context));
        long size = 0;
        if (file.exists()) {
            if (file.isDirectory()) {
                size = getFolderSize(file);
            } else {
                size = file.length();
            }
        }
        return size;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param file file
     * @return size
     */
    private long getFolderSize(File file) {
        long size = 0;
        File[] fileList = file.listFiles();
        for (File aFileList : fileList) {
            if (aFileList.isDirectory()) {
                size = size + getFolderSize(aFileList);
            } else {
                size = size + aFileList.length();
            }
        }
        return size;
    }

    public interface ApkFileClearListener {
        void deleted(boolean success);
    }

    /**
     * ??????????????????
     */
    private String getApplicationName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            ApplicationInfo info = manager.getApplicationInfo(context.getPackageName(), 0);
            return (String) manager.getApplicationLabel(info);
        } catch (Exception e) {
            e.printStackTrace();
            return "" + System.currentTimeMillis();
        }
    }

    public void onDestroy() {
        if (downloadDialog != null) {
            downloadDialog = null;
        }
    }
}
