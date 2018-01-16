package com.just.agentweb;

import android.webkit.WebView;

/**
 * Created by cenxiaozhong.
 * source code  https://github.com/Justson/AgentWeb
 */

public class IndicatorHandler implements IndicatorController {
    BaseIndicatorSpec mBaseIndicatorSpec;

    @Override
    public void progress(WebView v, int newProgress) {

        if (newProgress == 0) {
            reset();
        } else if (newProgress > 0 && newProgress <= 10) {
            showProgressBar();
        } else if (newProgress > 10 && newProgress < 95) {
            setProgressBar(newProgress);
        } else {
            setProgressBar(newProgress);
            finish();
        }

    }

    @Override
    public BaseIndicatorSpec offerIndicator() {
        return this.mBaseIndicatorSpec;
    }

    public void reset() {

        if (mBaseIndicatorSpec != null) {
            mBaseIndicatorSpec.reset();
        }
    }

    public void finish() {
        if (mBaseIndicatorSpec != null) {
            mBaseIndicatorSpec.hide();
        }
    }

    public void setProgressBar(int n) {
        if (mBaseIndicatorSpec != null) {
            mBaseIndicatorSpec.setProgress(n);
        }
    }

    public void showProgressBar() {

        if (mBaseIndicatorSpec != null) {
            mBaseIndicatorSpec.show();
        }
    }

    public static IndicatorHandler getInstance() {
        return new IndicatorHandler();
    }


    public IndicatorHandler inJectProgressView(BaseIndicatorSpec baseIndicatorSpec) {
        this.mBaseIndicatorSpec = baseIndicatorSpec;
        return this;
    }
}
