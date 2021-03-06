package com.kingyon.elevator.uis.widgets;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.EditText;

import com.leo.afbaselibrary.utils.ScreenUtil;

/**
 * Created by GongLi on 2019/2/13.
 * Email：lc824767150@163.com
 */

public class AutoAdjustSizeEditText extends EditText {

    // 最小字体
    public static final float DEFAULT_MIN_TEXT_SIZE = 8.0f;
    // 最大字体
    public static final float DEFAULT_MAX_TEXT_SIZE = 40.0f;

    private float maxTextSize = DEFAULT_MAX_TEXT_SIZE;
    private float minTextSize = DEFAULT_MIN_TEXT_SIZE;

    private Paint textPaint;

    public AutoAdjustSizeEditText(Context context) {
        super(context);
    }

    public AutoAdjustSizeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initialise() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        if (this.textPaint == null) {
            this.textPaint = new Paint();
            this.textPaint.set(this.getPaint());
        }
//        this.maxTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.maxTextSize, displayMetrics);
//        if (DEFAULT_MIN_TEXT_SIZE >= maxTextSize) {
//            this.maxTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.maxTextSize, displayMetrics);
//        }
//        this.maxTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.maxTextSize, displayMetrics);
//        this.minTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.minTextSize, displayMetrics);
    }

    /**
     * Re size the font so the specified text fits in the text box * assuming
     * the text box is the specified width.
     */
    public void fitText(String text, int textWidth, int textHeight) {
        if (!TextUtils.isEmpty(text) && textWidth > 0 && textHeight > 0) {
            int availableHeight = textHeight - getPaddingTop() - getPaddingBottom();
            float trySize = maxTextSize;
            // 先用最大字体写字
            textPaint.setTextSize(ScreenUtil.sp2px(trySize));
            while (trySize > minTextSize && getFontHeight(textPaint) > availableHeight) {
                trySize -= 1;
                // 保证大于最小字体
                if (trySize <= minTextSize) {
                    trySize = minTextSize;
                    break;
                }
                // 再次用新字体写字
                textPaint.setTextSize(ScreenUtil.sp2px(trySize));
            }
            // 先用最大字体写字
            textPaint.setTextSize(ScreenUtil.sp2px(trySize));
            // 如果最大字体>最小字体 && 最大字体画出字的宽度>单行可见文字宽度

            // 单行可见文字宽度
            int availableWidth = textWidth - getPaddingLeft() - getPaddingRight();
            while ((trySize > minTextSize) && (textPaint.measureText(text) > availableWidth)) {
                // 最大字体小一号
                trySize -= 1;
                // 保证大于最小字体
                if (trySize <= minTextSize) {
                    trySize = minTextSize;
                    break;
                }
                // 再次用新字体写字
                textPaint.setTextSize(ScreenUtil.sp2px(trySize));
            }
            this.setTextSize(trySize);
        }
    }


    /**
     * 重写setText
     * 每次setText的时候
     *
     * @param text
     * @param type
     */
    @Override
    public void setText(CharSequence text, BufferType type) {
        this.initialise();
        String textString = text.toString();
        float trySize = maxTextSize;
        if (this.textPaint == null) {
            this.textPaint = new Paint();
            this.textPaint.set(this.getPaint());
        }
        this.textPaint.setTextSize(ScreenUtil.sp2px(trySize));
        // 计算设置内容前 内容占据的宽度
        int textWidth = (int) this.textPaint.measureText(textString);
        // 拿到宽度和内容，进行调整
        this.fitText(textString, getWidth(), getHeight());
        super.setText(text, type);
    }


    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        this.fitText(text.toString(), getWidth(), getHeight());
    }


    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     *
     * @param w    Current width of this view.
     * @param h    Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // 如果当前view的宽度 != 原来view的宽度
        if (w != oldw) this.fitText(this.getText().toString(), getWidth(), getHeight());
    }

    /**
     * @return 返回指定的文字高度
     */
    public float getFontHeight(Paint textPaint) {
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        //文字基准线的下部距离-文字基准线的上部距离 = 文字高度
        return fm.bottom - fm.top;
    }

    public void setMaxTextSize(float maxTextSize) {
        this.maxTextSize = maxTextSize;
    }

    public void setMinTextSize(float minTextSize) {
        this.minTextSize = minTextSize;
    }
}