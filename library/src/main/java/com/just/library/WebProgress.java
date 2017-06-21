package com.just.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

/**
 * <b>@项目名：</b> agentweb<br>
 * <b>@包名：</b>com.just.library<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b><br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述:source CODE  https://github.com/Justson/AgentWeb</b><br>
 */

public class WebProgress extends BaseIndicatorView implements BaseProgressSpec {

    private int mColor;
    private Paint mPaint;
    private ValueAnimator mValueAnimator;

    public WebProgress(Context context) {
        this(context, null);
    }

    public WebProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private int screenWidth = 0;

    public WebProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);

    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        mPaint = new Paint();
        mColor = Color.parseColor("#1aad19");

        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);

       // context.getResources().getDisplayMetrics().
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;

    }

    public void setColor(int color) {
        this.mColor = color;
        mPaint.setColor(color);
    }

    public void setColor(String color) {
        this.setColor(Color.parseColor(color));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);

        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        if (wMode == MeasureSpec.AT_MOST) {
            w = getContext().getResources().getDisplayMetrics().widthPixels;
        }
        if (hMode == MeasureSpec.AT_MOST) {
            h = AgentWebUtils.dp2px(this.getContext(), 2);
        }
        this.setMeasuredDimension(w, h);

    }

    private float currentProgress = 0f;

    @Override
    protected void onDraw(Canvas canvas) {

     /*   Log.i("Info", "re:" + (currentProgress / 100 * Float.valueOf(this.getWidth())) + "  width:" + this.getWidth() + "  height:" + this.getHeight() + "  left:" + getLeft() + "  right:" + getRight() + "  b:" + this.getBottom() + " top:" + this.getTop());*/

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
//        super.dispatchDraw(canvas);
        canvas.drawRect(0, 0, currentProgress / 100 * Float.valueOf(this.getWidth()), this.getHeight(), mPaint);
    }

    public void show() {
        this.setVisibility(View.VISIBLE);
        currentProgress=0f;
        startAnim(-1, true);
    }

    public void setProgress(float progress) {
        if (getVisibility() == View.GONE) {
            setVisibility(View.VISIBLE);
        }
        startAnim(progress, false);
    }

    public void hide() {
        TAG = FINISH;
    }

    private int TAG = 0;

    public static final int UN_START = 0;
    public static final int STARTED = 1;
    public static final int FINISH = 2;


    private float target = 0f;

    private float weightDuration(float value, float current) {

        if (value > 70 && value < 85) {
            return 1.5f;
        } else if (value > 85) {
            return 0.8f;
        }
        return small(value, current);
    }

    private float small(float value, float current) {
        float poor = Math.abs(value - current);
        if (poor < 25) {
            return 4f;
        } else if (poor > 25 && poor < 50) {
            return 3f;
        } else {
            return 2f;
        }
    }

    private void startAnim(float value, boolean isAuto) {


        if (target == value)
            return;

        if(value<currentProgress)
            return;

        float v = (isAuto) ? 80f : value;


        if (mValueAnimator != null && mValueAnimator.isStarted()) {
            mValueAnimator.cancel();
        }
//        Log.i("Info", "currentProgress:" + currentProgress + "  value:" + v);
        currentProgress = currentProgress == 0f ? 0.00000001f : currentProgress;
        mValueAnimator = ValueAnimator.ofFloat(currentProgress, v);
        mValueAnimator.setInterpolator(new LinearInterpolator());

        long duration = (long) Math.abs((v / 100f * screenWidth) - (currentProgress / 100f * screenWidth));


        /*默认每个像素8毫秒*/
        mValueAnimator.setDuration(isAuto ? duration * 8 : (long) (duration * weightDuration(v, currentProgress)));
        mValueAnimator.addUpdateListener(mAnimatorUpdateListener);
        mValueAnimator.addListener(mAnimatorListenerAdapter);
        mValueAnimator.start();
        TAG = STARTED;
        target = v;

    }

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float t = (float) animation.getAnimatedValue();
//            Log.i("Info", "t:" + t);
            WebProgress.this.currentProgress = t;
            WebProgress.this.invalidate();

        }
    };

    private AnimatorListenerAdapter mAnimatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            doEnd();
        }
    };

    private void doEnd() {
        if (TAG == FINISH && currentProgress == 100f) {
            setVisibility(GONE);
            currentProgress = 0f;
        }
        TAG = UN_START;
    }

    @Override
    public void reset() {
        currentProgress = 0;
        if(mValueAnimator!=null&&mValueAnimator.isStarted())
            mValueAnimator.cancel();
    }

    @Override
    public void setProgress(int newProgress) {
        setProgress(Float.valueOf(newProgress));

    }


    @Override
    public FrameLayout.LayoutParams offerLayoutParams() {
        return new FrameLayout.LayoutParams(-1,AgentWebUtils.dp2px(getContext(), 2));
    }
}
