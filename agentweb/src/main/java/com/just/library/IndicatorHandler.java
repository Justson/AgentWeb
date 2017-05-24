package com.just.library;

import android.webkit.WebView;

/**
 * <b>@项目名：</b> agentweb<br>
 * <b>@包名：</b>com.just.library<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
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
