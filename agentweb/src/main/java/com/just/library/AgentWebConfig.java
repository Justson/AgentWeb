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


     static final String AGENTWEB_CACHE_PATCH="/agentweb_cache";

    static final String DOWNLOAD_PATH="download";

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


    public static String getCachePath(Context context){
        return context.getCacheDir().getAbsolutePath()+AGENTWEB_CACHE_PATCH;
    }
    public static String getDatabasesCachePath(Context context){
        return context.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
    }

    public static void removeAllCookies(Context context){
        if (CookieSyncManager.getInstance() == null)
            CookieSyncManager.createInstance(context);
        CookieManager cm = CookieManager.getInstance();
        cm.removeAllCookie();
    }

    /**
     *
     * okhttp
     *
     *
     */

    public static void syncCookieToWebView(String url, String cookies) {


        CookieManager mCookieManager=CookieManager.getInstance();
        mCookieManager.setCookie(url,cookies);
    }


}
