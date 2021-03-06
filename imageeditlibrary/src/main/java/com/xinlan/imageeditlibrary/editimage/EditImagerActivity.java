package com.xinlan.imageeditlibrary.editimage;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinlan.imageeditlibrary.BaseActivity;
import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing;
import com.xinlan.imageeditlibrary.editimage.fragment.AddTextFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.BeautyFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.CropFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.FilterListFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.PaintFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.RotateFragment;
import com.xinlan.imageeditlibrary.editimage.fragment.StickerFragment;
import com.xinlan.imageeditlibrary.editimage.utils.BitmapUtils;
import com.xinlan.imageeditlibrary.editimage.view.CropImageView;
import com.xinlan.imageeditlibrary.editimage.view.CustomPaintView;
import com.xinlan.imageeditlibrary.editimage.view.RotateImageView;
import com.xinlan.imageeditlibrary.editimage.view.StickerView;
import com.xinlan.imageeditlibrary.editimage.view.TextStickerView;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouch;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.ImageViewTouchBase;
import com.xinlan.imageeditlibrary.editimage.widget.RedoUndoController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Created By Admin  on 2020/7/29
 * @Email : 163235610@qq.com
 * @Author:Mrczh
 * @Instructions:
 */
public class EditImagerActivity extends BaseActivity {

    public static final String FILE_PATH = "file_path";
    public static final String EXTRA_OUTPUT = "extra_output";

    /**????????????????????????*/
    public String filePath;
    /**????????????????????????*/
    public String saveFilePath;
    /**????????????*/
    public ImageViewTouch mainImage;
    /** ?????????View*/
    public StickerView mStickerView;
    /**??????????????????*/
    public CropImageView mCropPanel;
    /**??????????????????*/
    public RotateImageView mRotatePanel;
    /**??????????????????View*/
    public TextStickerView mTextStickerView;
    /**??????????????????*/
    public CustomPaintView mPaintView;

    /**??????*/
    public LinearLayout ll_edit_filter;

    private LoadImageTask mLoadImageTask;

    /** ?????????????????? ??? ???*/
    private int imageWidth, imageHeight;

    /**????????????Bitmap*/
    private Bitmap mainBitmap;

    /**??????????????????bitmap*/
    private Bitmap fliterBit;

    /**????????????*/
    private Bitmap currentBitmap;

    /**????????????*/
    private RedoUndoController mRedoUndoController;


    /**??????Fragment*/
    public StickerFragment mStickerFragment;
    /**??????FliterListFragment*/
    public FilterListFragment mFilterListFragment;
    /**????????????Fragment*/
    public CropFragment mCropFragment;
    /**????????????Fragment*/
    public RotateFragment mRotateFragment;
    /**??????????????????*/
    public AddTextFragment mAddTextFragment;
    /**????????????Fragment*/
    public PaintFragment mPaintFragment;
    /**????????????Fragment*/
    public BeautyFragment mBeautyFragment;

    /**??????view*/
    HorizontalScrollView hsv_effect;
    LinearLayout ll_effect_container;

    protected int mOpTimes = 0;
    protected boolean isBeenSaved = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imager_edit);
        initView();
        getData();
    }

    private void initView() {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels / 2;
        imageHeight = metrics.heightPixels / 2;

        mainImage = (ImageViewTouch) findViewById(R.id.main_image);
        mStickerView = (StickerView) findViewById(R.id.sticker_panel);
        mCropPanel = (CropImageView) findViewById(R.id.crop_panel);
        mRotatePanel = (RotateImageView) findViewById(R.id.rotate_panel);
        mTextStickerView = (TextStickerView) findViewById(R.id.text_sticker_panel);
        mPaintView = (CustomPaintView) findViewById(R.id.custom_paint_view);
        ll_edit_filter = findViewById(R.id.ll_edit_filter);
        hsv_effect = findViewById(R.id.hsv_effect);
        ll_effect_container = findViewById(R.id.ll_effect_container);

        mRedoUndoController = new RedoUndoController(this, findViewById(R.id.redo_uodo_panel));

        mStickerFragment = StickerFragment.newInstance();
        mFilterListFragment = FilterListFragment.newInstance();
        mCropFragment = CropFragment.newInstance();
        mRotateFragment = RotateFragment.newInstance();
        mAddTextFragment = AddTextFragment.newInstance();
        mPaintFragment = PaintFragment.newInstance();
        mBeautyFragment = BeautyFragment.newInstance();
        ll_edit_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ????????????
                ProcessingImage task = new ProcessingImage();
                task.execute(6);
            }
        });
    }


    private void getData() {
        filePath = getIntent().getStringExtra(FILE_PATH);
        /*??????????????????*/
        saveFilePath = getIntent().getStringExtra(EXTRA_OUTPUT);
        loadImage(filePath);
    }
    /**
     * ????????????????????????
     *
     * @param filepath
     */
    public void loadImage(String filepath) {
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }
        mLoadImageTask = new LoadImageTask();
        mLoadImageTask.execute(filepath);
    }

    /**
     * ????????????????????????
     */
    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return BitmapUtils.getSampledBitmap(params[0], imageWidth,
                    imageHeight);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            changeMainBitmap(result, false);
        }
    }// end inner class


    /**
     * @param newBit
     * @param needPushUndoStack
     */
    public void changeMainBitmap(Bitmap newBit, boolean needPushUndoStack) {
        if (newBit == null)
            return;
        if (mainBitmap == null || mainBitmap != newBit) {
            if (needPushUndoStack) {
                mRedoUndoController.switchMainBit(mainBitmap,newBit);
                increaseOpTimes();
            }
            mainBitmap = newBit;
            mainImage.setImageBitmap(mainBitmap);
            mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        }
    }


    public void increaseOpTimes() {
        mOpTimes++;
        isBeenSaved = false;
    }

    public Bitmap getMainBit() {
        return mainBitmap;
    }

    private final class ProcessingImage extends AsyncTask<Integer, Void, Bitmap> {
        private Dialog dialog;
        private Bitmap srcBitmap;

        @Override
        protected Bitmap doInBackground(Integer... params) {
            int type = params[0];
            if (srcBitmap != null && !srcBitmap.isRecycled()) {
                srcBitmap.recycle();
            }

            srcBitmap = Bitmap.createBitmap(getMainBit().copy(
                    Bitmap.Config.ARGB_8888, true));
            return PhotoProcessing.filterPhoto(srcBitmap, type);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
            dialog.dismiss();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result == null)
                return;
            if (fliterBit != null && (!fliterBit.isRecycled())) {
                fliterBit.recycle();
            }
            fliterBit = result;
            mainImage.setImageBitmap(fliterBit);
            currentBitmap = fliterBit;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = BaseActivity.getLoadingDialog(EditImagerActivity.this, R.string.handing,
                    false);
            dialog.show();
        }

    }
}
