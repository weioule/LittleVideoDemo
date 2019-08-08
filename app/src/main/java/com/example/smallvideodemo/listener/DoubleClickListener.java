package com.example.smallvideodemo.listener;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by weioule
 * on 2019/5/16.
 */
public class DoubleClickListener implements View.OnTouchListener {
    private static int timeout = 400;//双击间四百毫秒延时
    private int clickCount = 0;//记录连续点击次数
    private Handler handler;
    private MyClickCallBack myClickCallBack;

    public interface MyClickCallBack {
        void oneClick(View v);//点击一次的回调

        void doubleClick();//双击及连续多次点击的回调
    }

    public DoubleClickListener(MyClickCallBack myClickCallBack) {
        this.myClickCallBack = myClickCallBack;
        handler = new Handler();
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            clickCount++;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (clickCount == 1) {
                        myClickCallBack.oneClick(v);
                    } else if (clickCount >= 2) {
                        myClickCallBack.doubleClick();
                    }
                    handler.removeCallbacksAndMessages(null);
                    //清空handler延时，并防内存泄漏
                    clickCount = 0;//计数清零
                }
            }, timeout);//延时timeout后执行run方法中的代码
        }
        return false;//让点击事件继续传播，方便再给View添加其他事件监听
    }
}