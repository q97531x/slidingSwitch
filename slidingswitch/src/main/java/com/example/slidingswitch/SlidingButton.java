package com.example.slidingswitch;

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

    public SlidingButton(Context context) {
        super(context);
    }

    public SlidingButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        //初始化一些参数

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
