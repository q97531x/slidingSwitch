package com.example.slidingswitch;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by weeboos on 2017/3/13.
 */

public class SlidingButton extends View {

    //创建画笔
    private Paint paint;
    //长宽
    private int width,height;
    //属性动画
    private ValueAnimator valueAnimator;
    //圆的半径
    private float radius = 20f;
    //圆的初始坐标
    private float circleStartX,circleStartY;
    //偏移量
    private float offSetLength;
    //小圆移动的目的地坐标
    private float destinationX,destinationY;

    public SlidingButton(Context context) {
        super(context);
    }

    public SlidingButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlidingButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        //初始化一些参数
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);   //画笔抗锯齿
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);

        circleStartX = 0;
        circleStartY = height/2;

        valueAnimator = ValueAnimator.ofFloat(0,1);
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.setRepeatCount(0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                offSetLength = destinationX*((float)valueAnimator.getAnimatedValue());
                invalidate();
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width= w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawLine(0,height/2,width,height/2,paint);
        canvas.drawCircle(circleStartX+radius+offSetLength,height/2,radius,paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //按下事件
                valueAnimator.setFloatValues(0,1);
                circleStartX = destinationX;
                destinationX = event.getX();
                valueAnimator.start();
                break;
            case MotionEvent.ACTION_UP:
                //抬起事件
                if(event.getX()>width/2){
                    //点击的位置大于控件宽度的一半,将其移动至终点
                    valueAnimator.setFloatValues(0,1);

                    destinationX = width-radius*2-circleStartX;
                    circleStartX = 0;
                    valueAnimator.start();
                }else {
                    valueAnimator.setFloatValues(1,0);
                    destinationX = event.getX();
                    valueAnimator.start();
                }
                break;
        };
        return true;
    }
}
