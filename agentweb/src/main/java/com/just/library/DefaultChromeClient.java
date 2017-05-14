package com.just.library;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.webkit.JsResult;
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

public class DefaultChromeClient  extends ChromeClientProgress{


    private Activity mActivity;

    public DefaultChromeClient(Activity activity,IndicatorController indicatorController) {
        super(indicatorController);
        this.mActivity=activity;
    }


    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

        AgentWebUtils.show(view,
                message,
                Snackbar.LENGTH_SHORT,
                Color.WHITE,
                mActivity.getResources().getColor(R.color.black),
                null,
                -1,
                null);
        result.confirm();

        return true;
    }
}
