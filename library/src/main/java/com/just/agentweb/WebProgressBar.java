package com.just.agentweb;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by cenxiaozhong .
 * source code  https://github.com/Justson/AgentWeb .
 */

public class WebProgressBar extends BaseIndicatorView implements BaseProgressSpec {

    /**
     * 进度条颜色
     */
    private int mColor;
    /**
     * 进度条的画笔
     */
    private Paint mPaint;
    /**
     * 进度条动画
     */
    private Animator mAnimator;
    /**
     * 控件的宽度
     */
    private int mTargetWidth = 0;

    /**
     * 默认匀速动画最大的时长
     */
    public static final int MAX_UNIFORM_SPEED_DURATION = 8 * 1000;
    /**
     * 默认加速后减速动画最大时长
     */
    public static final int MAX_DECELERATE_SPEED_DURATION = 800;
    /**
     * 结束动画时长 ， Fade out 。
     */
    public static final int DO_END_ANIMATION_DURATION = 600;

    /**
     * 当前匀速动画最大的时长
     */
    private static int CURRENT_MAX_UNIFORM_SPEED_DURATION = MAX_UNIFORM_SPEED_DURATION;
    /**
     * 当前加速后减速动画最大时长
     */
    private static int CURRENT_MAX_DECELERATE_SPEED_DURATION = MAX_DECELERATE_SPEED_DURATION;

    /**
     * 标志当前进度条的状态
     */
    private int TAG = 0;
    public static final int UN_START = 0;
    public static final int STARTED = 1;
    public static final int FINISH = 2;

    /**
     * 默认的高度
     */
    public static int WEB_PROGRESS_DEFAULT_HEIGHT = 3;

    public WebProgressBar(Context context) {
        this(context, null);
    }

    public WebProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

        mTargetWidth = context.getResources().getDisplayMetrics().widthPixels;
        WEB_PROGRESS_DEFAULT_HEIGHT = AgentWebUtils.dp2px(context, 3);

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
            w = w <= getContext().getResources().getDisplayMetrics().widthPixels ? w : getContext().getResources().getDisplayMetrics().widthPixels;
        }
        if (hMode == MeasureSpec.AT_MOST) {
            h = WEB_PROGRESS_DEFAULT_HEIGHT;
        }
        this.setMeasuredDimension(w, h);

    }

    private float currentProgress = 0f;

    @Override
    protected void onDraw(Canvas canvas) {

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawRect(0, 0, currentProgress / 100 * Float.valueOf(this.getWidth()), this.getHeight(), mPaint);
    }

    public void show() {

        if (getVisibility() == View.GONE) {
            this.setVisibility(View.VISIBLE);
            currentProgress = 0f;
            startAnim(false);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mTargetWidth = getMeasuredWidth();
        int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        if (mTargetWidth >= screenWidth) {
            CURRENT_MAX_DECELERATE_SPEED_DURATION = MAX_DECELERATE_SPEED_DURATION;
            CURRENT_MAX_UNIFORM_SPEED_DURATION = MAX_UNIFORM_SPEED_DURATION;
        } else {
            //取比值
            float rate = this.mTargetWidth / Float.valueOf(screenWidth);
            CURRENT_MAX_UNIFORM_SPEED_DURATION = (int) (MAX_UNIFORM_SPEED_DURATION * rate);
            CURRENT_MAX_DECELERATE_SPEED_DURATION = (int) (MAX_DECELERATE_SPEED_DURATION * rate);

        }

        LogUtils.i("WebProgress", "CURRENT_MAX_UNIFORM_SPEED_DURATION" + CURRENT_MAX_UNIFORM_SPEED_DURATION);
    }

    public void setProgress(float progress) {
        if (getVisibility() == View.GONE) {
            setVisibility(View.VISIBLE);
        }
        if (progress < 95f)
            return;
        if (TAG != FINISH) {
            startAnim(true);
        }
    }

    public void hide() {
        TAG = FINISH;
    }


    private float target = 0f;


    private void startAnim(boolean isFinished) {


        float v = isFinished ? 100 : 95;


        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
        }
        currentProgress = currentProgress == 0f ? 0.00000001f : currentProgress;

        LogUtils.i("WebProgress", "currentProgress:" + currentProgress + " v:" + v + "  :" + (1f - currentProgress));
        if (!isFinished) {
            ValueAnimator mAnimator = ValueAnimator.ofFloat(currentProgress, v);
            float residue = 1f - currentProgress / 100 - 0.05f;
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.setDuration((long) (residue * CURRENT_MAX_UNIFORM_SPEED_DURATION));
            mAnimator.addUpdateListener(mAnimatorUpdateListener);
            mAnimator.start();
            this.mAnimator = mAnimator;
        } else {

            ValueAnimator segment95Animator = null;
            if (currentProgress < 95f) {
                segment95Animator = ValueAnimator.ofFloat(currentProgress, 95);
                float residue = 1f - currentProgress / 100f - 0.05f;
                segment95Animator.setInterpolator(new LinearInterpolator());
                segment95Animator.setDuration((long) (residue * CURRENT_MAX_DECELERATE_SPEED_DURATION));
                segment95Animator.setInterpolator(new DecelerateInterpolator());
                segment95Animator.addUpdateListener(mAnimatorUpdateListener);
            }


            ObjectAnimator mObjectAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
            mObjectAnimator.setDuration(DO_END_ANIMATION_DURATION);
            ValueAnimator mValueAnimatorEnd = ValueAnimator.ofFloat(95f, 100f);
            mValueAnimatorEnd.setDuration(DO_END_ANIMATION_DURATION);
            mValueAnimatorEnd.addUpdateListener(mAnimatorUpdateListener);

            AnimatorSet mAnimatorSet = new AnimatorSet();
            mAnimatorSet.playTogether(mObjectAnimator, mValueAnimatorEnd);

            if (segment95Animator != null) {
                AnimatorSet mAnimatorSet1 = new AnimatorSet();
                mAnimatorSet1.play(mAnimatorSet).after(segment95Animator);
                mAnimatorSet = mAnimatorSet1;
            }
            mAnimatorSet.addListener(mAnimatorListenerAdapter);
            mAnimatorSet.start();
            mAnimator = mAnimatorSet;
        }

        TAG = STARTED;
        target = v;

    }

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float t = (float) animation.getAnimatedValue();
            WebProgressBar.this.currentProgress = t;
            WebProgressBar.this.invalidate();

        }
    };

    private AnimatorListenerAdapter mAnimatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            doEnd();
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        /**
         * animator cause leak , if not cancel;
         */
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
            mAnimator = null;
        }
    }

    private void doEnd() {
        if (TAG == FINISH && currentProgress == 100f) {
            setVisibility(GONE);
            currentProgress = 0f;
            this.setAlpha(1f);
        }
        TAG = UN_START;
    }

    @Override
    public void reset() {
        currentProgress = 0;
        if (mAnimator != null && mAnimator.isStarted())
            mAnimator.cancel();
    }

    @Override
    public void setProgress(int newProgress) {
        setProgress(Float.valueOf(newProgress));

    }


    @Override
    public LayoutParams offerLayoutParams() {
        return new LayoutParams(-1, WEB_PROGRESS_DEFAULT_HEIGHT);
    }
}
