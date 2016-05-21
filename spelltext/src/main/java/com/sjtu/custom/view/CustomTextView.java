package com.sjtu.custom.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.sjtu.custom.R;
import com.sjtu.custom.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by CharlesZhu on 2016/5/11.
 */
public class CustomTextView extends View {

    private static final String REGULAR_PATTERN = "[`~!@#$^&*=|{}:;,\\\\[\\\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“。，、？]";
    private static final String TAG = "CustomTextView";
    private String mText;

    private int mTextColor;
    private int mTextSize;

    private int mTextSpellColor;

    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mSpellBound;
    private Rect mBound;
    private Paint mPaint;
    private Paint mSpellPaint;

    private int mLinePadding;
    private int mLineHeight;
    private int mMeasureWidthSize;
    private int mMeasureHeightSize;

    List<String> initTextToList;

    public CustomTextView(Context context) {
        this(context,null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 获得我自定义的样式属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * 获得自定义的样式属性
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomTextView,defStyleAttr,0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomTextView_ctext:
                    mText = a.getString(attr);
                    break;
                case R.styleable.CustomTextView_ctextColor:
                    // 默认颜色设置为黑色
                    mTextColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CustomTextView_ctextSpellColor:
                    // 默认颜色设置为黑色
                    mTextSpellColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CustomTextView_ctextSize:
                    // 默认设置为16sp，TypeValue也可以把sp转化为px
                    mTextSize = a.getDimensionPixelSize(attr,(int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomTextView_ctextLinePadding:
                    mLinePadding = a.getDimensionPixelSize(attr,(int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    break;
            }
        }
        a.recycle();

        Log.e(TAG,"mTextSize:" + mTextSize + ", mTextSize:" + mTextSize + "  mLinePadding:" + mLinePadding);
        /**
         * 获取绘制文本的宽高
         */
        mPaint = new Paint();
        mSpellPaint = new Paint();
        mBound = new Rect();
        mSpellBound = new Rect();
    }

    public void seText(String str) {
        mText = str;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        initTextToList = textToList();
        mPaint.setTextSize(mTextSize);
        mPaint.getTextBounds("测试",0,"测试".length(),mBound);
        mPaint.setColor(mTextColor);

        mSpellPaint.setTextSize(mTextSize);
        mSpellPaint.getTextBounds("ceshi",0,"ceshi".length(),mSpellBound);
        mSpellPaint.setColor(mTextSpellColor);

        //计算行高
        mLineHeight = getPaddingTop() + mBound.height() + mSpellBound.height() + getPaddingBottom() + mLinePadding;


        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        drawReal(mSpellPaint,mPaint,widthSize,null);
        int width;
        int height;

        //计算view的高度
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height =getPaddingTop() + mMeasureHeightSize + getPaddingBottom() + 2 * mLinePadding;
        }

        //计算view的宽度
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = getPaddingLeft() + mMeasureWidthSize + getPaddingRight();
        }

        Log.e(TAG,"width:" + width + ",height:" + height);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawReal(mSpellPaint,mPaint,getWidth(),canvas);
    }

    private void drawReal(Paint spellPaint, Paint paint, int widthSize, Canvas canvas) {
        if (initTextToList == null || initTextToList.size() == 0) {
            return;
        }
        int tempMeasureWidth = 0;
        int len = initTextToList.size();
        int row = 0;
        int templinelength = 0;
        if (canvas == null) {
            canvas = new Canvas();
        }
        int width_hanzi, width_pinyin, width;
        int startX_hanzi, startY_hanzi, startX_pinyin, startY_pinyin;
        int black_size = spellPaint.measureText(" ") > paint.measureText(" ")
                ? (int) spellPaint.measureText(" ") : (int) paint.measureText(" ");
        for (int i = 0; i < len; i++) {
            String[] strs = initTextToList.get(i).split(" ");
            width_hanzi = (int) spellPaint.measureText(strs[0]);
            width_pinyin = (int) paint.measureText(strs[1]);
            if (width_hanzi >= width_pinyin) {
                //汉字长
                width = width_hanzi;
                startX_hanzi = templinelength;
                startX_pinyin = startX_hanzi + (width_hanzi - width_pinyin) / 2;
            } else {
                //拼音长
                width = width_pinyin;
                startX_pinyin = templinelength;
                startX_hanzi = startX_pinyin + (width_pinyin - width_hanzi) / 2;
            }
            templinelength += width;
            if (templinelength > widthSize) { //换行
                row++;
                templinelength = 0;
                if (width_hanzi >= width_pinyin) {
                    //汉字长
                    width = width_hanzi;
                    startX_hanzi = templinelength;
                    startX_pinyin = startX_hanzi + (width_hanzi - width_pinyin) / 2;
                } else {
                    //拼音长
                    width = width_pinyin;
                    startX_pinyin = templinelength;
                    startX_hanzi = startX_pinyin + (width_pinyin - width_hanzi) / 2;
                }
                templinelength += width;
            }
            startY_pinyin = mLinePadding + row * mLineHeight;
            startY_hanzi = startY_pinyin + mSpellBound.height() * 4 / 3;
            canvas.drawText(strs[1] + " ", startX_pinyin + black_size, startY_pinyin, spellPaint);
            canvas.drawText(strs[0] + " ", startX_hanzi + black_size, startY_hanzi, paint);
            if (templinelength < widthSize - black_size) {//画空格
                templinelength += black_size;
            }
            tempMeasureWidth = templinelength + black_size;
        }
        mMeasureWidthSize = (row == 0 ? tempMeasureWidth : widthSize);
        mMeasureHeightSize = row * mLineHeight + mLinePadding;
    }

    private List<String> textToList() {
        List<String> mTextToList = new ArrayList<>();
        if (mText != null && !mText.equals("")) {
            String[] contents = mText.split("\\)");
            //去掉了()  （）  ‘ '
            Pattern p = Pattern.compile(REGULAR_PATTERN);
            Matcher m;
            for (int i = 0; i < contents.length; i++) {
                //第一个是字符
                m = p.matcher(contents[i]);
                if (contents[i].contains("(")) {
                    if (!m.find()) {
                        mTextToList.add(contents[i].replace("(", " "));
                    } else {
                        String punctuation = contents[i].charAt(0) + "";
                        mTextToList.add(punctuation + " " + punctuation);
                        mTextToList.add(contents[i].replace(punctuation, "").replace("(", " "));
                    }
                } else {
                    mTextToList.add(contents[i] + " " + contents[i]);
                }
            }
        }
        return mTextToList;
    }

}
