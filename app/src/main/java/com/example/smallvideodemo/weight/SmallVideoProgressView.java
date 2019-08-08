package com.example.smallvideodemo.weight;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import com.example.smallvideodemo.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by weioule
 * on 2019/5/21.
 */
public class SmallVideoProgressView extends View {

    private int mWidth, mHeight;
    private int mDefaultWidth, mDefaultHeight;
    private int mProgressWidth;
    private int mMinProgressWidth;
    private Paint mPaint;
    private String mColor;
    private Handler mHandler;
    private int mTimePeriod = 0, currentPositrion;
    private boolean loading;
    private int max;

    public SmallVideoProgressView(Context context) {
        this(context, null);
    }

    public SmallVideoProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmallVideoProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取颜色值和最小宽高度，以及进度条最小宽度
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmallVideoProgressView);
        String color = typedArray.getString(R.styleable.SmallVideoProgressView_progressColors);
        mDefaultWidth = (int) typedArray.getDimension(R.styleable.SmallVideoProgressView_minWidth, 100);
        mDefaultHeight = (int) typedArray.getDimension(R.styleable.SmallVideoProgressView_minHeight, 5);
        mMinProgressWidth = (int) typedArray.getDimension(R.styleable.SmallVideoProgressView_minProgressWidth, 100);
        mProgressWidth = mMinProgressWidth;

        //根据正则表达式来判断颜色格式是否正确
        String regularStr = "^#[A-Fa-f0-9]{6}";
        Pattern compile = Pattern.compile(regularStr);
        if (color == null) {
            mColor = "#ffffff";
        } else {
            Matcher matcher = compile.matcher(color);
            if (matcher.matches()) {//如果颜色格式正确
                mColor = color;
            } else {
                //如果颜色格式不正确
                throw new IllegalArgumentException("wrong color string type!");
            }
        }
        typedArray.recycle();

        mPaint = new Paint();
        //设置虎逼模式为填充带边框
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //设置抗锯齿
        mPaint.setAntiAlias(true);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                invalidate();
                this.sendEmptyMessageDelayed(1, mTimePeriod);
            }
        };
    }

    public void startAnimation() {
        if (mHandler.hasMessages(1)) return;
        loading = true;
        mHandler.sendEmptyMessageDelayed(1, mTimePeriod);
        setVisibility(VISIBLE);
    }

    public void cancelAnimation() {
        setVisibility(GONE);
        if (mHandler.hasMessages(1)) mHandler.removeMessages(1);
    }


    @SuppressLint("NewApi")
    public void pause() {
        if (null != anim && anim.isRunning())
            anim.pause();
    }

    @SuppressLint("NewApi")
    public void resume() {
        setVisibility(VISIBLE);
        if (null != anim)
            anim.resume();
    }

    @SuppressLint("NewApi")
    public void cancel() {
        if (null != anim)
            anim.cancel();

        if (mHandler.hasMessages(1)) mHandler.removeMessages(1);
    }

    public void end() {
        if (null != anim) anim.end();
    }

    public void setProgress(int currentPosition, VideoView videoView) {
        //非播放状态时清掉动画任务
        if (videoView.getDuration() <= 0) {
            if (null != anim)
                anim.cancel();
            setVisibility(GONE);
            return;
        }

        if (getVisibility() == GONE) {
            setVisibility(VISIBLE);
        }

        if (mHandler.hasMessages(1)) mHandler.removeMessages(1);

        max = videoView.getDuration();
        loading = false;

        //开启动画任务
        startFakeProgress(currentPosition, videoView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //通过widthMeasureSpec,heightMeasureSpec 来获取view的测量模式和宽高
        int width = getValue(widthMeasureSpec, true);
        int height = getValue(heightMeasureSpec, false);

        //此方法用来设置设置View的具体宽高
        setMeasuredDimension(width, height);
    }

    /**
     * 获取view的宽高值
     *
     * @param measureSpec
     * @param isWidth     是否是测量宽度
     * @return
     */
    private int getValue(int measureSpec, boolean isWidth) {
        //获取测量模式
        int mode = MeasureSpec.getMode(measureSpec);
        //获取测量的值
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            /**
             * 如果父控件传递给的MeasureSpec的mode是MeasureSpec.UNSPECIFIED，就说明，父控件对自己没有任何限制，那么尺寸就选择自己需要的尺寸size
             如果父控件传递给的MeasureSpec的mode是MeasureSpec.EXACTLY，就说明父控件有明确的要求，希望自己能用measureSpec中的尺寸，这时就推荐使用MeasureSpec.getSize(measureSpec)
             如果父控件传递给的MeasureSpec的mode是MeasureSpec.AT_MOST，就说明父控件希望自己不要超出MeasureSpec.getSize(measureSpec)，如果超出了，就选择MeasureSpec.getSize(measureSpec)，否则用自己想要的尺寸就行了
             */
            case MeasureSpec.EXACTLY:
                //子view的大小已经被限定死，我们只能使用其固定大小
                return size;
            case MeasureSpec.AT_MOST:
                //父控件认为子view的大小不能超过size的值，那么我们就取size和默认值之间的最小值
                return Math.min(isWidth ? mDefaultWidth : mDefaultHeight, size);
            case MeasureSpec.UNSPECIFIED:
                //父view不限定子view的大小，我们将其值设置为默认值
                return isWidth ? mDefaultWidth : mDefaultHeight;
            default:
                return isWidth ? mDefaultWidth : mDefaultHeight;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mPaint.setStrokeWidth(mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //展示进度条
        if (!loading) {
            mPaint.setStrokeWidth(8);
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.parseColor("#55ffffff"));
            canvas.drawLine(0, 0, mWidth, 0, mPaint);

            mPaint.setColor(Color.parseColor("#B9922222"));
            if (max == 0) return;
            canvas.drawLine(0, 0, mWidth * currentPositrion / max, 0, mPaint);
            return;
        }

        //下面是加载动画

        //首先判断进度条的宽度是否大于view宽度
        if (mProgressWidth < mWidth) {
            //如果不大于，将进度条宽度增加10
            mProgressWidth += 15;//注意执行此步骤是mProgressWidth值有可能大于view宽度；
        } else {
            //如果进度条宽度大于view宽度将进度条宽度设置为初始宽度
            mProgressWidth = mMinProgressWidth;
        }
        //计算颜色透明度
        //mProgressWidth/mWidth 计算当前进度条宽度占总宽度的比例
        //255*mProgressWidth/mWidth 计算当前比例下对应的透明度值
        //由于是由不透明变成全透明，所以使用255减去其值
        int currentColorValue = 0;

        if (mWidth > 0)
            currentColorValue = 255 - 255 * mProgressWidth / mWidth;

        if (currentColorValue > 255) {
            //由于mProgressWidth有可能大于view的宽度，要保证颜色值不能大于255
            currentColorValue = 255;
        }
        if (currentColorValue < 30) {
            //此处是为了限制让其不成为全透明，如果设置为全透明，在最后阶段进度宽度渐变观察不到
            currentColorValue = 30;
        }
        //将透明度转换为16进制
        String s = Integer.toHexString(currentColorValue);
        //拼接颜色字符串并转化为颜色值
        int color = Color.parseColor("#" + s + mColor.substring(1, mColor.length()));
        //给画笔设置颜色
        mPaint.setColor(color);
        //使用canvas来画进度条（确实就是画一条线）
        canvas.drawLine(mWidth / 2 - mProgressWidth / 2, mDefaultHeight / 2, mWidth / 2 + mProgressWidth / 2, mDefaultHeight / 2, mPaint);
    }


    private ValueAnimator anim;
    private boolean stopAnim;

    private void startFakeProgress(int progress, final VideoView videoView) {
        if (null == anim) {
            anim = ValueAnimator.ofInt(0, max);
        } else {
            anim.cancel();
            anim.setIntValues(progress, max);
        }

        stopAnim = false;

        anim.setDuration(max);
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (stopAnim) return;
                currentPositrion = videoView.getCurrentPosition();
                if (currentPositrion >= videoView.getDuration()) {
                    stopAnim = true;
                    anim.end();
                    return;
                }

                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //这里可能会存在一些误差
                if (videoView.isPlaying() && currentPositrion < max - 500) {
                    currentPositrion = max;
                    invalidate();
                }
            }
        });

        anim.start();
    }
}