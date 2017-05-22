package com.just.library;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.util.List;

/**
 * Created by cenxiaozhong on 2017/5/14.
 * https://github.com/Justson/AgentWeb
 */

public class AgentWebConfig {

    /**
     * cookie同步
     */
    private void syncCookieToWebView(Context context, List<String> cookies, String url) {

        if (CookieSyncManager.getInstance() == null)
            CookieSyncManager.createInstance(context);
        CookieManager cm = CookieManager.getInstance();
        cm.removeAllCookie();
        cm.setAcceptCookie(true);
        if (cookies != null) {
            for (String cookie : cookies) {
                cm.setCookie(url, cookie);
            }
        }
        CookieSyncManager.getInstance().sync();
    }


    /**
     *
     */

    public static void syncCookieToWebView(String url, String cookies) {

        CookieManager mCookieManager=CookieManager.getInstance();
        mCookieManager.setCookie(url,cookies);
    }
}
