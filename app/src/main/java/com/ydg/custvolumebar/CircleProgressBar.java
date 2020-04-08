package com.ydg.custvolumebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;


import static android.content.ContentValues.TAG;

public class CircleProgressBar extends View {

    private Paint progressPaint;    //draw circle paint
    private Paint bgPaint;

    private RectF bgRectF;
    private RectF barRectF;

    private int progressNum;        //可以更新的进度条数值
    private int maxNum;           //max value

    private float barRadius;      // The radius of the progress bar or its background.
    private float bgRadius1;        // The radius of outer circle of bg
    private float bgRadius2;        // The radius of middle circle of bg
    private float bgRadius3;        // The radius of inner circle of bg

    private int bgColor1;           // The color of outer circle of bg
    private int bgColor2;           // The color of middle circle of bg
    private int bgColor3;           // The color of inner circle of bg

    private float startAngle;       // The angle of the start point of progress bar
    private float sweepAngle;       // The angle of the whole of progress bar

    private int startColor;         // The start color of the sweep gradient.
    private int midColor;           // The middle color of the sweep gradient.
    private int endColor;           // The end color of the sweep gradient.
    private int barBgColor;         // The color of the bg of progress bar.

    private float progressSweepAngle; //进度条圆弧扫过的角度
    private float barWidth;         // The width of the progress bar or its BG.

    private float textSize;
    private float lineSpace;

    private float centerX;
    private float centerY;

//定义一个外部监听器，侦听bar条改变这一事件并做一些外部事件的处理。
    private OnProgressChangeListener mOnProgressChangeListener;

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setOnProgressChangeListener(OnProgressChangeListener l){
        mOnProgressChangeListener = l;
    }
    public OnProgressChangeListener getOnProgressChangeListener(){
        return mOnProgressChangeListener;
    }

// 处理外部监听器监听事件
    private void onProgressChange(int value){
        mOnProgressChangeListener.onProgressChange();
    }

    private void init(Context context, AttributeSet attrs){

        bgRectF = new RectF();
        barRectF = new RectF();

        bgPaint = new Paint();
        progressPaint = new Paint();
    //进度条默认值：
        progressNum = 80;
        maxNum = 100;

    // 属性获取：
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.CircleProgressBar);
        startAngle = typedArray.getFloat(R.styleable.CircleProgressBar_start_angle,0);
        sweepAngle = typedArray.getFloat(R.styleable.CircleProgressBar_sweep_angle, 360);

        barWidth = typedArray.getDimension(R.styleable.CircleProgressBar_bar_width, DpOrPxUtils.dip2px(context,5));
        startColor = typedArray.getColor(R.styleable.CircleProgressBar_start_color, Color.GRAY);
        midColor = typedArray.getColor(R.styleable.CircleProgressBar_mid_color, Color.GRAY);
        endColor = typedArray.getColor(R.styleable.CircleProgressBar_end_color, Color.GRAY);
        barBgColor = typedArray.getColor(R.styleable.CircleProgressBar_bar_bg_color, Color.GRAY);

        barRadius = typedArray.getDimension(R.styleable.CircleProgressBar_bar_bg_radius, 0);
        bgRadius1 = typedArray.getDimension(R.styleable.CircleProgressBar_bg_radius1, 0);
        bgRadius2 = typedArray.getDimension(R.styleable.CircleProgressBar_bg_radius2, 0);
        bgRadius3 = typedArray.getDimension(R.styleable.CircleProgressBar_bg_radius3, 0);

        bgColor1 = typedArray.getColor(R.styleable.CircleProgressBar_bg_color1, Color.BLACK);
        bgColor2 = typedArray.getColor(R.styleable.CircleProgressBar_bg_color2, Color.RED);
        bgColor3 = typedArray.getColor(R.styleable.CircleProgressBar_bg_color3, Color.GREEN);

        textSize = typedArray.getFloat(R.styleable.CircleProgressBar_text_size, 20.0f);
        lineSpace = typedArray.getFloat(R.styleable.CircleProgressBar_line_space, 5.0f);

        typedArray.recycle();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = measureSize((int)bgRadius1, heightMeasureSpec);
        int width = measureSize((int)bgRadius1, widthMeasureSpec);
        int min = Math.min(height, width);
        setMeasuredDimension(min, min);

        centerX = min / 2;
        centerY = min / 2;
        if (centerX >= barWidth){
            barRectF.set(centerX - barRadius, centerY-barRadius, centerX+barRadius,centerY+barRadius);
        }else {
            barRectF.set(0, 0, barWidth,barWidth);
        }

    }

    private int measureSize(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY){
            return specSize;
        } else if (specMode == MeasureSpec.AT_MOST){
            result = Math.min(result, specSize);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        float radius;

        progressSweepAngle = (float)progressNum * sweepAngle / maxNum;

        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setAntiAlias(true);

    // 1. draw outer circle:
        radius = bgRadius2 + (bgRadius1 - bgRadius2) /2;
        bgPaint.setColor(bgColor1);
        bgPaint.setStrokeWidth(bgRadius1-bgRadius2);
        canvas.drawCircle(centerX, centerY, radius, bgPaint);

    // 2. draw middle circle:
        radius = bgRadius3 + (bgRadius2 - bgRadius3) /2;
        bgPaint.setColor(bgColor2);
        bgPaint.setStrokeWidth(bgRadius2-bgRadius3);
        canvas.drawCircle(centerX, centerY, radius, bgPaint);

    // 3. draw inner circle:
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(bgColor3);
        canvas.drawCircle(centerX, centerY, bgRadius3, bgPaint);

    // 4. draw bar bg:
        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setAntiAlias(true);           //设置抗锯齿
        progressPaint.setStrokeWidth(barWidth);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(barBgColor);
        canvas.drawArc(barRectF, startAngle, sweepAngle, false, progressPaint);

    // 5.draw progress bar:
        progressPaint.setAlpha(0xFF);
        progressPaint.setShader(new SweepGradient(barRectF.centerX(),barRectF.centerY(), new int[]{startColor,midColor,endColor}, null));
        canvas.save();
        canvas.rotate(startAngle-5, barRectF.centerX(),barRectF.centerY());
        canvas.drawArc(barRectF, 5, progressSweepAngle, false, progressPaint);
        progressPaint.setShader(null);
        //canvas.rotate(-startAngle, rectF.centerX(),rectF.centerY()); //这句话可有可无，因为后面调用了canvas.restore()
        canvas.restore();

    // 6.draw progress Text:
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        String progressText = String.valueOf(progressNum);

        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.WHITE);
        canvas.translate(centerX,centerY);

        float textBaseLineY = Math.abs(textPaint.ascent() + textPaint.descent()) / 2;
        float textLeftPos = -textPaint.measureText(progressText)/2;
        canvas.drawText(progressText, textLeftPos, textBaseLineY, textPaint);
    // 7.draw sound icon:
        Bitmap soundIcon;
        if (progressNum == 0){
            soundIcon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_volume_off_small);
        } else{
            soundIcon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_volume_small);
        }

        canvas.drawBitmap(soundIcon, -soundIcon.getWidth()/2, textPaint.getTextSize()/2 + lineSpace, new Paint());
    }

    private static class DpOrPxUtils {
        public static int dip2px(Context context, float dpValue){
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale +0.5f);
        }
        public static int px2dip(Context context, float pxValue){
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        }
    }

    public void setProgress(int value){
        if (value > maxNum){
            progressNum = maxNum;
        } else if (value < 0){
            progressNum = 0;
        } else
        {
            progressNum = value;
        }
        onProgressChange(value);
        //Log.i(TAG,"zhougq,setProgressValue(),value is "+progressNum);
        postInvalidate();
    }
    public int getProgress(){
        //Log.i(TAG,"zhougq,getProgressValue(),return "+progressNum);
        return progressNum;
    }

}

interface OnProgressChangeListener {
    public void onProgressChange();
}