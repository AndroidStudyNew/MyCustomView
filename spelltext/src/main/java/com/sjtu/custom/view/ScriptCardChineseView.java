package com.sjtu.custom.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tel003 on 2016/4/22.
 */
public class ScriptCardChineseView extends View {

    Activity mActivity;
    //设置汉字拼音之间margin
    private int marginTop = 8;
    //拼音画笔
    private Paint paintPinyin;
    //汉字画笔
    private Paint paintHanzi;
    //传参
    List<String> list;
    String str;
    //每行的高度
    private int line_height;
    //一共多少行
    private int row2;
    private int width1;
    private int width2;
    private int height1;

    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final int DEFAULT_TEXTSIZE_HANZI = 30;
    private static final int DEFAULT_TEXTSIZE_PINYIN = 24;

    private int mTextColor = DEFAULT_BORDER_COLOR;
    private int mTextSizeHanzi = DEFAULT_TEXTSIZE_HANZI;
    private int mTextSizePinyin = DEFAULT_TEXTSIZE_PINYIN;

    public ScriptCardChineseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity) context;
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        list = initData();
        int row = 0;
        row2 = 0;
        width1 = 0;
        width2 = 0;
        int width_hanzi, width_pinyin, width;
        int startX_hanzi, startY_hanzi, startX_pinyin, startY_pinyin;
        int black_size = paintHanzi.measureText(" ") > paintPinyin.measureText(" ")
                ? (int) paintHanzi.measureText(" ") : (int) paintPinyin.measureText(" ");
        System.out.println("--h--" + getHeight());
        for (int i = 0, sum = 0; i < list.size(); i++) {
            String[] strs = list.get(i).split(" ");
            width_hanzi = (int) paintHanzi.measureText(strs[0]);
            width_pinyin = (int) paintPinyin.measureText(strs[1]);
            if (width_hanzi >= width_pinyin) {
                //汉字长
                width = width_hanzi;
                startX_hanzi = sum;
                startX_pinyin = sum + (width_hanzi - width_pinyin) / 2;
                sum += width;
                startY_pinyin = maxTextSize(mTextSizePinyin, mTextSizeHanzi) + marginTop + row * line_height;
                startY_hanzi = 2 * startY_pinyin - row * line_height;
            } else {
                //拼音长
                width = width_pinyin;
                startX_pinyin = sum;
                startX_hanzi = sum + (width_pinyin - width_hanzi) / 2;
                sum += width;
                startY_pinyin = maxTextSize(mTextSizePinyin, mTextSizeHanzi) + marginTop + row * line_height;
                startY_hanzi = 2 * startY_pinyin - row * line_height;
            }
            //画第一行
            if (sum <= getWidth()) {
                Log.e("zrg-1-row", "x=" + startX_hanzi + ";y" + startY_hanzi);
                canvas.drawText(strs[1], startX_pinyin, startY_pinyin, paintPinyin);
                canvas.drawText(strs[0], startX_hanzi, startY_hanzi, paintHanzi);
                if (sum >= getWidth() - black_size) {
                    //不需要画空格
                } else {
                    //画空格
                    sum += black_size;
                    canvas.drawText(" ", startX_pinyin, startY_pinyin, paintPinyin);
                    canvas.drawText(" ", startX_hanzi, startY_hanzi, paintHanzi);
                }
                width1 = sum;
            } else {
                //换行画
                sum = 0;
                if (width_hanzi >= width_pinyin) {
                    //汉字长
                    width = width_hanzi;
                    startX_hanzi = sum;
                    startX_pinyin = sum + (width_hanzi - width_pinyin) / 2;
                    sum += width;
                    startY_pinyin = maxTextSize(mTextSizePinyin, mTextSizeHanzi) + marginTop + (row + 1) * line_height;
                    startY_hanzi = 2 * startY_pinyin - (row + 1) * line_height;
                    canvas.drawText(strs[1], startX_pinyin, startY_pinyin, paintPinyin);
                    canvas.drawText(strs[0], startX_hanzi, startY_hanzi, paintHanzi);
                    sum += black_size;
                    Log.e("zrg-2-row", "x=" + startX_hanzi + ";y" + startY_hanzi);
                    canvas.drawText(" ", startX_pinyin, startY_pinyin, paintPinyin);
                    canvas.drawText(" ", startX_hanzi, startY_hanzi, paintHanzi);
                } else {
                    //拼音长
                    width = width_pinyin;
                    startX_pinyin = sum;
                    startX_hanzi = sum + (width_pinyin - width_hanzi) / 2;
                    sum += width;
                    startY_pinyin = maxTextSize(mTextSizePinyin, mTextSizeHanzi) + marginTop + (row + 1) * line_height;
                    startY_hanzi = 2 * startY_pinyin - (row + 1) * line_height;
                    canvas.drawText(strs[1], startX_pinyin, startY_pinyin, paintPinyin);
                    canvas.drawText(strs[0], startX_hanzi, startY_hanzi, paintHanzi);
                    sum += black_size;
                    Log.e("zrg-2-row", "x=" + startX_hanzi + ";y" + startY_hanzi);
                    canvas.drawText(" ", startX_pinyin, startY_pinyin, paintPinyin);
                    canvas.drawText(" ", startX_hanzi, startY_hanzi, paintPinyin);
                }
                row++;
                ++row2;
                width2 = getWidth();
                Log.e("-zrg-row:", row + "");
                Log.e("-zrg-row2:", row2 + "");
            }
        }
    }

    public void initPaint() {
        paintHanzi = new Paint();
        paintHanzi.setAntiAlias(true);
        paintHanzi.setColor(mTextColor);
        paintHanzi.setTextSize(mTextSizeHanzi);
        paintPinyin = new Paint();
        paintPinyin.setAntiAlias(true);
        paintPinyin.setColor(mTextColor);
        paintPinyin.setTextSize(mTextSizePinyin);
        invalidate();
    }

    public List<String> initData() {
        list = new ArrayList<>();
        if (str != null && !str.equals("")) {
            String[] contents = str.split("\\)");
            //去掉了()  （）  ‘ '
            Pattern p = Pattern.compile("[`~!@#$^&*=|{}:;,\\\\[\\\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“。，、？]");
            Matcher m;
            for (int i = 0; i < contents.length; i++) {
                //第一个是字符
                m = p.matcher(contents[i]);
                if (contents[i].contains("(")) {
                    if (!m.find()) {
                        list.add(contents[i].replace("(", " "));
                    } else {
                        String punctuation = contents[i].charAt(0) + "";
                        list.add(punctuation + " " + punctuation);
                        list.add(contents[i].replace(punctuation, "").replace("(", " "));
                    }
                } else {
                    list.add(contents[i] + " " + contents[i]);
                }
            }
        }
        return list;
    }

    private int maxTextSize(int mTextSizePinyin, int mTextSizeHanzi) {
        return mTextSizeHanzi > mTextSizePinyin ? mTextSizeHanzi : mTextSizePinyin;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int heightSize = measureHeight(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        line_height = measureHeight(heightMeasureSpec, paintHanzi) + measureHeight(heightMeasureSpec, paintPinyin) + 3 * marginTop;
        int line_height2 = measureListWidth(paintPinyin, paintHanzi, 0, widthSize);
//        int width = measureWidth(widthMeasureSpec);
//        setMeasuredDimension(widthSize, (row2+1)*line_height);
        setMeasuredDimension(widthSize, line_height2);
        Log.e("-zrg-line_height--", line_height + "--line_height2--" + line_height2 + "---row2--" + row2);

//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//                MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, expandSpec);
//        super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec);
    }

    private int measureHeight(int measureSpec, Paint paint) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);


        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int) (-paint.ascent() + paint.descent()) + 2 +
                    getPaddingTop() + getPaddingBottom();
//            if (witch == 1) {
//                Paint paint = new Paint();
//                paint.setAntiAlias(true);
//                paint.setTextSize(mTextSizeHanzi);
//                result = (int) (-paint.ascent() + paint.descent()) + 2 + getPaddingTop() + getPaddingBottom();
//            } else {
//                Paint paint2 = new Paint();
//                paint2.setAntiAlias(true);
//                paint2.setTextSize(mTextSizePinyin);
//                result = (int) (-paint2.ascent() + paint2.descent()) + 2 + getPaddingTop() + getPaddingBottom();
//            }
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

//        Paint paint = new Paint();
//        paint.setTextSize(mTextSizeHanzi);
//        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.FILL);
//        Paint paint2 = new Paint();
//        paint2.setTextSize(mTextSizePinyin);
//        paint2.setAntiAlias(true);
//        paint2.setStyle(Paint.Style.FILL);

        if (width2 == getWidth()) {
            result = width2;
        } else {
            result = width1;
        }

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureListWidth(Paint paint, Paint paint2, int startX, int widthSize) {
        list = initData();
        if (list == null || list.size() == 0) {
            return 0;
        }
        int x = startX;
        int len = list.size();
        int row = 0;
        int sum = 0;
        Canvas canvas = new Canvas();
        int width_hanzi, width_pinyin, width;
        int startX_hanzi, startY_hanzi, startX_pinyin, startY_pinyin;
        int black_size = paint.measureText(" ") > paint2.measureText(" ")
                ? (int) paint.measureText(" ") : (int) paint2.measureText(" ");
        for (int i = 0; i < len; i++) {
            String[] strs = list.get(i).split(" ");
            width_hanzi = (int) paint.measureText(strs[0]);
            width_pinyin = (int) paint2.measureText(strs[1]);
            if (width_hanzi >= width_pinyin) {
                //汉字长
                width = width_hanzi;
                startX_hanzi = sum;
                startX_pinyin = sum + (width_hanzi - width_pinyin) / 2;
                sum += width;
                startY_pinyin = maxTextSize(mTextSizePinyin, mTextSizeHanzi) + marginTop + row * line_height;
                startY_hanzi = 2 * startY_pinyin - row * line_height;
            } else {
                //拼音长
                width = width_pinyin;
                startX_pinyin = sum;
                startX_hanzi = sum + (width_pinyin - width_hanzi) / 2;
                sum += width;
                startY_pinyin = maxTextSize(mTextSizePinyin, mTextSizeHanzi) + marginTop + row * line_height;
                startY_hanzi = 2 * startY_pinyin - row * line_height;
            }

            Log.e("zrg->getMeasuredWidth", getMeasuredWidth() + ";widthSize=" + widthSize);

            //画第一行
            if (sum <= widthSize) {
                Log.e("zrg->old row->sum", sum + "");
                canvas.drawText(strs[1], startX_pinyin, startY_pinyin, paint2);
                canvas.drawText(strs[0], startX_hanzi, startY_hanzi, paint);
                if (sum >= widthSize - black_size) {
                    //不需要画空格
                } else {
                    //画空格
                    sum += black_size;
                    canvas.drawText(" ", startX_pinyin, startY_pinyin, paint2);
                    canvas.drawText(" ", startX_hanzi, startY_hanzi, paint);
                }
            } else {
                //换行画
                row++;
                sum = 0;

                Log.e("zrg->new row->sum", sum + "");

                if (width_hanzi >= width_pinyin) {
                    //汉字长
                    width = width_hanzi;
                    startX_hanzi = sum;
                    startX_pinyin = sum + (width_hanzi - width_pinyin) / 2;
                    sum += width;
                    startY_pinyin = maxTextSize(mTextSizePinyin, mTextSizeHanzi) + marginTop + row * line_height;
                    startY_hanzi = 2 * startY_pinyin - row * line_height;
                    canvas.drawText(strs[1], startX_pinyin, startY_pinyin, paintPinyin);
                    canvas.drawText(strs[0], startX_hanzi, startY_hanzi, paintHanzi);
                    sum += black_size;
                    canvas.drawText(" ", startX_pinyin, startY_pinyin, paintPinyin);
                    canvas.drawText(" ", startX_hanzi, startY_hanzi, paintHanzi);
                } else {
                    //拼音长
                    width = width_pinyin;
                    startX_pinyin = sum;
                    startX_hanzi = sum + (width_pinyin - width_hanzi) / 2;
                    sum += width;
                    startY_pinyin = maxTextSize(mTextSizePinyin, mTextSizeHanzi) + marginTop + row * line_height;
                    startY_hanzi = 2 * startY_pinyin - row * line_height;
                    canvas.drawText(strs[1], startX_pinyin, startY_pinyin, paintPinyin);
                    canvas.drawText(strs[0], startX_hanzi, startY_hanzi, paintHanzi);
                    sum += black_size;
                    canvas.drawText(" ", startX_pinyin, startY_pinyin, paintPinyin);
                    canvas.drawText(" ", startX_hanzi, startY_hanzi, paintPinyin);
                }
            }
        }
        Log.e("-zrg-measure-", row + "");
        return (row + 1) * line_height;
    }

    public void setStr(String str) {
        this.str = str.replace(" ", "").trim();
        invalidate();
    }

    public void setTextColor(int textColor) {
        if (textColor == mTextColor) {
            return;
        }
        mTextColor = textColor;
        paintHanzi.setColor(mTextColor);
        paintPinyin.setColor(mTextColor);
        invalidate();
    }

    public void setTextSizeHanzi(int textSizeHanzi) {
        if (textSizeHanzi == mTextSizeHanzi) {
            return;
        }
        mTextSizeHanzi = Math.round(textSizeHanzi * mRatio());
        paintHanzi.setTextSize(mTextSizeHanzi);
        invalidate();
    }

    public void setTextSizePinyin(int textSizePinyin) {
        if (textSizePinyin == mTextSizePinyin) {
            return;
        }
        mTextSizePinyin = Math.round(textSizePinyin * mRatio());
        paintPinyin.setTextSize(mTextSizePinyin);
        invalidate();
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        invalidate();
    }

    private float mRatio() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        float ratioWidth = (float) screenWidth / 720;
        float ratioHeight = (float) screenHeight / 1080;
        return Math.min(ratioWidth, ratioHeight);
    }
}
