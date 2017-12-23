package com.just.agentweb;

import android.webkit.WebView;

/**
 * Created by cenxiaozhong.
 * source code  https://github.com/Justson/AgentWeb
 */

public class IndicatorHandler implements IndicatorController, ProgressLifeCyclic {
    BaseProgressSpec baseProgressSpec;

    @Override
    public void progress(WebView v, int newProgress) {

//        Log.i("Info", "newProgress:" + newProgress + "  v:" + v);
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
    public BaseProgressSpec offerIndicator() {
        return this.baseProgressSpec;
    }

    public void reset() {

        if (baseProgressSpec != null) {
            baseProgressSpec.reset();
        }
    }

    public void finish() {
        if (baseProgressSpec != null) {
            baseProgressSpec.hide();
        }
    }

    public void setProgressBar(int n) {
        if (baseProgressSpec != null) {
            baseProgressSpec.setProgress(n);
        }
    }

    public void showProgressBar() {

        if (baseProgressSpec != null) {
            baseProgressSpec.show();
        }
    }

    public static IndicatorHandler getInstance() {
        return new IndicatorHandler();
    }


    public IndicatorHandler inJectProgressView(BaseProgressSpec baseProgressSpec) {
        this.baseProgressSpec = baseProgressSpec;
        return this;
    }
}
