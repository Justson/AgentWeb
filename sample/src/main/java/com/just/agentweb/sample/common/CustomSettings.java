package com.just.agentweb.sample.common;

import android.os.Build;
import android.webkit.WebView;

import com.just.agentweb.AgentWebSettings;
import com.just.agentweb.WebDefaultSettingsManager;

/**
 * Created by cenxiaozhong on 2017/5/26.
 * source code  https://github.com/Justson/AgentWeb
 */

public class CustomSettings extends WebDefaultSettingsManager {
    public CustomSettings() {
        super();
    }


    @Override
    public AgentWebSettings toSetting(WebView webView) {
        super.toSetting(webView);
        getWebSettings().setBlockNetworkImage(false);//是否阻塞加载网络图片  协议http or https
        getWebSettings().setAllowFileAccess(false); //允许加载本地文件html  file协议, 这可能会造成不安全 , 建议重写关闭
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWebSettings().setAllowFileAccessFromFileURLs(false); //通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
            getWebSettings().setAllowUniversalAccessFromFileURLs(false);//允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
        }
        getWebSettings().setUserAgentString(getWebSettings().getUserAgentString().concat("agentweb/2.0.1"));
        return this;
    }
}
