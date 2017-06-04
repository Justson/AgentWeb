package com.just.library;

import android.content.Context;
import android.os.Build;
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



    static final boolean isKikatOrBelowKikat= Build.VERSION.SDK_INT<=Build.VERSION_CODES.KITKAT;
//    static final boolean isKikatOrBelowKikat= true;



    public static final int WEBVIEW_DEFAULT_TYPE=1;
    public static final int WEBVIEW_AGENTWEB_SAFE_TYPE =2;
    public static final int WEBVIEW_CUSTOM_TYPE=3;

    static  int WEBVIEW_TYPE =WEBVIEW_DEFAULT_TYPE;


    /**
     * cookie同步
     */
    public static void syncCookieToWebView(Context context, List<String> cookies, String url) {

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



    public static void syncCookieToWebView(String url, String cookies) {


        CookieManager mCookieManager=CookieManager.getInstance();
        mCookieManager.setCookie(url,cookies);
    }


}
