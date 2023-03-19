/*
 * Copyright (C)  Justson(https://github.com/Justson/AgentWeb)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * @author cenxiaozhong
 * @since 1.0.0
 */
public class WebIndicator extends BaseIndicatorView implements BaseIndicatorSpec {
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
    public static final int MAX_DECELERATE_SPEED_DURATION = 450;
    /**
     * 结束动画时长 ， Fade out 。
     */
    public static final int DO_END_ANIMATION_DURATION = 600;
    /**
     * 当前匀速动画最大的时长
     */
    private int mCurrentMaxUniformSpeedDuration = MAX_UNIFORM_SPEED_DURATION;
    /**
     * 当前加速后减速动画最大时长
     */
    private int mCurrentMaxDecelerateSpeedDuration = MAX_DECELERATE_SPEED_DURATION;
    /**
     * 结束动画时长
     */
    private int mCurrentDoEndAnimationDuration = DO_END_ANIMATION_DURATION;
    /**
     * 当前进度条的状态
     */
    private int indicatorStatus = 0;
    public static final int UN_START = 0;
    public static final int STARTED = 1;
    public static final int FINISH = 2;
    private float mCurrentProgress = 0F;
    /**
     * 默认的高度
     */
    public int mWebIndicatorDefaultHeight = 3;

    public WebIndicator(Context context) {
        this(context, null);
    }

    public WebIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        mWebIndicatorDefaultHeight = AgentWebUtils.dp2px(context, 3);
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
            h = mWebIndicatorDefaultHeight;
        }
        this.setMeasuredDimension(w, h);
    }


    @Override
    protected void onDraw(Canvas canvas) {
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawRect(0, 0, mCurrentProgress / 100 * Float.valueOf(this.getWidth()), this.getHeight(), mPaint);
    }

    @Override
    public void show() {
        if (getVisibility() == View.GONE) {
            this.setVisibility(View.VISIBLE);
            mCurrentProgress = 0f;
            startAnim(false);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mTargetWidth = getMeasuredWidth();
        int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        if (mTargetWidth >= screenWidth) {
            mCurrentMaxDecelerateSpeedDuration = MAX_DECELERATE_SPEED_DURATION;
            mCurrentMaxUniformSpeedDuration = MAX_UNIFORM_SPEED_DURATION;
            mCurrentDoEndAnimationDuration = MAX_DECELERATE_SPEED_DURATION;
        } else {
            //取比值
            float rate = this.mTargetWidth / Float.valueOf(screenWidth);
            mCurrentMaxUniformSpeedDuration = (int) (MAX_UNIFORM_SPEED_DURATION * rate);
            mCurrentMaxDecelerateSpeedDuration = (int) (MAX_DECELERATE_SPEED_DURATION * rate);
            mCurrentDoEndAnimationDuration = (int) (DO_END_ANIMATION_DURATION * rate);

        }
        LogUtils.i("WebProgress", "CURRENT_MAX_UNIFORM_SPEED_DURATION" + mCurrentMaxUniformSpeedDuration);
    }

    public void setProgress(float progress) {
        if (getVisibility() == View.GONE) {
            setVisibility(View.VISIBLE);
        }
        if (progress < 95f) {
            return;
        }
        if (indicatorStatus != FINISH) {
            startAnim(true);
        }
    }

    @Override
    public void hide() {
        indicatorStatus = FINISH;
    }

    private void startAnim(boolean isFinished) {
        float v = isFinished ? 100 : 95;
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
        }
        mCurrentProgress = mCurrentProgress == 0f ? 0.00000001f : mCurrentProgress;
        if (!isFinished) {
            AnimatorSet animatorSet = new AnimatorSet();

            float p1 = v * 0.60f;
            float p2 = v;
            ValueAnimator animator = ValueAnimator.ofFloat(mCurrentProgress, p1);
            ValueAnimator animator0 = ValueAnimator.ofFloat(p1, p2);
            float residue = 1f - mCurrentProgress / 100 - 0.05f;
            long duration = (long) (residue * mCurrentMaxUniformSpeedDuration);
            long duration6 = (long) (duration * 0.6f);
            long duration4 = (long) (duration * 0.4f);
            animator.setInterpolator(new LinearInterpolator());
            animator.setDuration(duration4);
            animator.addUpdateListener(mAnimatorUpdateListener);

            animator0.setInterpolator(new LinearInterpolator());
            animator0.setDuration(duration6);
            animator0.addUpdateListener(mAnimatorUpdateListener);
            animatorSet.play(animator0).after(animator);
            animatorSet.start();
            this.mAnimator = animatorSet;
        } else {
            ValueAnimator segment95Animator = null;
            if (mCurrentProgress < 95f) {
                segment95Animator = ValueAnimator.ofFloat(mCurrentProgress, 95);
                float residue = 1f - mCurrentProgress / 100f - 0.05f;
                segment95Animator.setDuration((long) (residue * mCurrentMaxDecelerateSpeedDuration));
                segment95Animator.setInterpolator(new DecelerateInterpolator());
                segment95Animator.addUpdateListener(mAnimatorUpdateListener);
            }
            ObjectAnimator mObjectAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
            mObjectAnimator.setDuration(mCurrentDoEndAnimationDuration);
            ValueAnimator mValueAnimatorEnd = ValueAnimator.ofFloat(95f, 100f);
            mValueAnimatorEnd.setDuration(mCurrentDoEndAnimationDuration);
            mValueAnimatorEnd.addUpdateListener(mAnimatorUpdateListener);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(mObjectAnimator, mValueAnimatorEnd);
            if (segment95Animator != null) {
                AnimatorSet animatorSet0 = new AnimatorSet();
                animatorSet0.play(animatorSet).after(segment95Animator);
                animatorSet = animatorSet0;
            }
            animatorSet.addListener(mAnimatorListenerAdapter);
            animatorSet.start();
            mAnimator = animatorSet;
        }
        indicatorStatus = STARTED;
    }

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float t = (float) animation.getAnimatedValue();
            WebIndicator.this.mCurrentProgress = t;
            WebIndicator.this.invalidate();
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
        if (indicatorStatus == FINISH && mCurrentProgress == 100f) {
            setVisibility(GONE);
            mCurrentProgress = 0f;
            this.setAlpha(1f);
        }
        indicatorStatus = UN_START;
    }

    @Override
    public void reset() {
        mCurrentProgress = 0;
        if (mAnimator != null && mAnimator.isStarted()) {
            mAnimator.cancel();
        }
    }

    @Override
    public void setProgress(int newProgress) {
        setProgress(Float.valueOf(newProgress));
    }


    @Override
    public LayoutParams offerLayoutParams() {
        return new LayoutParams(-1, mWebIndicatorDefaultHeight);
    }
}
