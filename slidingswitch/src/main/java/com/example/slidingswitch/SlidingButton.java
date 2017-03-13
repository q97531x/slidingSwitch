package com.example.slidingswitch;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

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

        valueAnimator = new ValueAnimator();

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

    }
}
