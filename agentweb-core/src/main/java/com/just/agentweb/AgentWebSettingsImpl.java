package com.just.agentweb;

import android.app.Activity;
import android.webkit.DownloadListener;
import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2018/2/11.
 */

public class AgentWebSettingsImpl extends AbsAgentWebSettings {
    private AgentWeb mAgentWeb;

    @Override
    protected void bindAgentWebSupport(AgentWeb agentWeb) {
        this.mAgentWeb = agentWeb;
    }


    @Override
    public WebListenerManager setDownloader(WebView webView, DownloadListener downloadListener) {
        Class<?> clazz = null;
        Object mDefaultDownloadImpl$Extra = null;
        try {
            clazz = Class.forName("com.just.agentweb.download.DefaultDownloadImpl");
            mDefaultDownloadImpl$Extra =
                    clazz.getDeclaredMethod("create", Activity.class, WebView.class,
                            Class.forName("com.just.agentweb.download.DownloadListener"),
                            Class.forName("com.just.agentweb.download.DownloadingListener"),
                            PermissionInterceptor.class)
                            .invoke(mDefaultDownloadImpl$Extra, (Activity) webView.getContext()
                                    , webView, null, null, mAgentWeb.getPermissionInterceptor());

        } catch (Throwable ignore) {
            if (LogUtils.isDebug()) {
                ignore.printStackTrace();
            }
        }
        return super.setDownloader(webView, mDefaultDownloadImpl$Extra == null ? downloadListener : (DownloadListener) mDefaultDownloadImpl$Extra);
    }
}
