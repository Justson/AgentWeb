package com.just.library;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.webkit.WebView;

/**
 * <b>@项目名：</b> <br>
 * <b>@包名：</b><br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> <br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class WebSecurityLogicImpl implements WebSecurityCheckLogic {
    public static WebSecurityLogicImpl getInstance() {
        return new WebSecurityLogicImpl();
    }

    public WebSecurityLogicImpl(){}

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void dealHoneyComb(WebView view) {
        if (Build.VERSION_CODES.HONEYCOMB > Build.VERSION.SDK_INT || Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1)
            return;
        view.removeJavascriptInterface("searchBoxJavaBridge_");
        view.removeJavascriptInterface("accessibility");
        view.removeJavascriptInterface("accessibilityTraversal");
    }

    @Override
    public void dealJsInterface(ArrayMap<String, Object> objects,AgentWeb.SecurityType securityType) {

        if (securityType== AgentWeb.SecurityType.strict&&AgentWebConfig.WEBVIEW_TYPE!=AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE&&Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            objects.clear();
            objects = null;
            System.gc();
        }

    }
}
