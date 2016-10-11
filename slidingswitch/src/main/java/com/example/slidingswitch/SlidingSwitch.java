package com.example.slidingswitch;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.example.slidingswitch.dependence.SimpleSpringListener;
import com.example.slidingswitch.dependence.Spring;
import com.example.slidingswitch.dependence.SpringConfig;
import com.example.slidingswitch.dependence.SpringSystem;
import com.example.slidingswitch.dependence.SpringUtil;


/**
 * Created by XmacZone on 16/4/1.
 */
public class SlidingSwitch extends View{
    private SpringSystem springSystem;
    private Spring spring ;
    /** */
    private float radius;
    /** 开启颜色*/
    private int onColor = Color.parseColor("#4ebb7f");
    /** 关闭颜色*/
    private int offBorderColor = Color.parseColor("#dadbda");
    /** 灰色带颜色*/
    private int offColor = Color.parseColor("#ffffff");
    /** 手柄颜色*/
    private int spotColor = Color.parseColor("#ffffff");
    /** 边框颜色*/
    private int borderColor = offBorderColor;
    /** 画笔*/
    private Paint paint ;
    /** 开关状态*/
    public boolean toggleOn = false;
    /** 边框大小*/
    private int borderWidth = 2;
    /** 垂直中心*/
    private float centerY;
    /** 按钮的开始和结束位置*/
    private float startX, endX;
    /** 手柄X位置的最小和最大值*/
    private float spotMinX, spotMaxX;
    /**手柄大小 */
    private int spotSize ;
    /** 手柄X位置*/
    private float spotX;
    /** 关闭时内部灰色带高度*/
    private float offLineWidth;
    /** */
    private RectF rect = new RectF();
    /** 默认使用动画*/
    private boolean defaultAnimate = true;
    private int start = 0;//起始点x坐标
    private int end;//结束点x坐标
    private int moveDistance = 0;//移动距离
    private boolean isEnd = false;//判断是否在底部
    private boolean isEndPositin = false;//判断位置到底部
    private OnToggleChanged listener;

    private SlidingSwitch(Context context) {
        super(context);
    }
    public SlidingSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }
    public SlidingSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        spring.removeListener(springListener);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        spring.addListener(springListener);
    }

    public void setup(AttributeSet attrs) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);

        springSystem = SpringSystem.create();
        spring = springSystem.createSpring();
        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(50, 7));

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                toggle();
            }
        });

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ToggleButton);
        offBorderColor = typedArray.getColor(R.styleable.ToggleButton_offBorderColor, offBorderColor);
        onColor = typedArray.getColor(R.styleable.ToggleButton_onColor, onColor);
        spotColor = typedArray.getColor(R.styleable.ToggleButton_spotColor, spotColor);
        offColor = typedArray.getColor(R.styleable.ToggleButton_offColor, offColor);
        borderWidth = typedArray.getDimensionPixelSize(R.styleable.ToggleButton_tb_borderWidth, borderWidth);
        defaultAnimate = typedArray.getBoolean(R.styleable.ToggleButton_animate, defaultAnimate);
        typedArray.recycle();

        borderColor = offBorderColor;
    }


    public void toggle() {
        toggleOn = !toggleOn;
//        takeEffect(animate,1,0);
        move(toggleOn);
        invalidate();
        if(listener != null){
            listener.onToggle(toggleOn);
        }
    }

    public void toggleOn() {
        setToggleOn();
        if(listener != null){
            listener.onToggle(toggleOn);
        }
    }

    public void toggleOff() {
        setToggleOff();
        if(listener != null){
            listener.onToggle(toggleOn);
        }
    }

    /**
     * 设置显示成打开样式，不会触发toggle事件
     */
    public void setToggleOn() {
        toggleOn = true;
        move(toggleOn);
        invalidate();
    }


    /**
     * 设置显示成关闭样式，不会触发toggle事件
     */
    public void setToggleOff() {
        toggleOn = false;
        move(toggleOn);
        invalidate();
    }


    public void move(boolean animate){
        if(!animate){
            moveDistance = 0;
            isEndPositin = false;
        }else {
            moveDistance = end;
            isEndPositin = true;
        }
//        Log.e("aa", animate + "" + "move" + moveDistance + "end" + end);
//        Utils.Log("end", "width" + getWidth() + "spotX" + spotX + "radius" + radius);
//        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        Resources r = Resources.getSystem();
        if(widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST){
            widthSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }

        if(heightMode == MeasureSpec.UNSPECIFIED || heightSize == MeasureSpec.AT_MOST){
            heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    //滑动事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                start = (int)event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                moveDistance = Math.max((int) event.getX(), 10);
                moveDistance = Math.min(moveDistance, end);
                invalidate();

                break;
            case MotionEvent.ACTION_UP:
                if (isEndPositin){
                    toggleOff();
                }else {
                    toggleOn();
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        final int width = getWidth();
        final int height = getHeight();

        radius = Math.min(width, height) * 0.5f;
        centerY = radius;
        startX = radius;
        endX = width - radius;
        spotMinX = startX + borderWidth;
        spotMaxX = endX - borderWidth;
        spotSize = height - 4 * borderWidth;
        spotX = spotMinX;
        offLineWidth = 0;
        end = (int)(getWidth()-(spotMinX + 1.1f + radius));

        //用于保存状态之后,进入改变位置
        move(toggleOn);
        invalidate();
    }


    SimpleSpringListener springListener = new SimpleSpringListener(){
        @Override
        public void onSpringUpdate(Spring spring) {
            final double value = spring.getCurrentValue();
            calculateEffect(value);
        }
    };

    private int clamp(int value, int low, int high) {
        return Math.min(Math.max(value, low), high);
    }


    @Override
    public void draw(Canvas canvas) {
        //
        rect.set(0, 0, getWidth(), getHeight());
        paint.setColor(borderColor);
//		canvas.drawRoundRect(rect, radius, radius, paint);
        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, paint);

        if(offLineWidth > 0){
            final float cy = offLineWidth * 0.5f;
            rect.set(spotX - cy, centerY - cy, endX + cy, centerY + cy);
            paint.setColor(borderColor);
//			canvas.drawRoundRect(rect, cy, cy, paint);
            canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, paint);
        }

        rect.set(spotX - 1 - radius + moveDistance, centerY - radius, spotX + 1.1f + radius + moveDistance, centerY + radius);
        if(isEnd){
            paint.setColor(onColor);
        }
        if(isEndPositin){
            paint.setColor(onColor);
        }else {
            paint.setColor(borderColor);
        }
        canvas.drawRoundRect(rect, radius, radius, paint);
//		canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, paint);
        float spotR = spotSize * 0.5f;
        rect.set(spotX - spotR+moveDistance, centerY - spotR, spotX + spotR+moveDistance, centerY + spotR);
        paint.setColor(spotColor);
        canvas.drawRoundRect(rect, spotR, spotR, paint);
//		canvas.drawLine(0,getHeight()/2,getWidth(),getHeight()/2,paint);

    }

    /**
     * @param value
     */
    private void calculateEffect(final double value) {
        final float mapToggleX = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, spotMinX, spotMaxX);
        spotX = mapToggleX;

        float mapOffLineWidth = (float) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, 10, spotSize);

        offLineWidth = mapOffLineWidth;

        final int fb = Color.blue(onColor);
        final int fr = Color.red(onColor);
        final int fg = Color.green(onColor);

        final int tb = Color.blue(offBorderColor);
        final int tr = Color.red(offBorderColor);
        final int tg = Color.green(offBorderColor);

        int sb = (int) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, fb, tb);
        int sr = (int) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, fr, tr);
        int sg = (int) SpringUtil.mapValueFromRangeToRange(1 - value, 0, 1, fg, tg);

        sb = clamp(sb, 0, 255);
        sr = clamp(sr, 0, 255);
        sg = clamp(sg, 0, 255);

        borderColor = Color.rgb(sr, sg, sb);

        postInvalidate();
    }

    /**
     * @author ThinkPad
     *
     */
    public interface OnToggleChanged{
        /**
         * @param on
         */
        public void onToggle(boolean on);
    }


    public void setOnToggleChanged(OnToggleChanged onToggleChanged) {
        listener = onToggleChanged;
    }

    public boolean isAnimate() {
        return defaultAnimate;
    }
    public void setAnimate(boolean animate) {
        this.defaultAnimate = animate;
    }
}
