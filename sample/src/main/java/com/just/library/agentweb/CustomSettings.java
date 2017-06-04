package com.just.library.agentweb;

import android.webkit.WebView;

import com.just.library.WebDefaultSettingsManager;
import com.just.library.WebSettings;

/**
 * Created by cenxiaozhong on 2017/5/26.
 */

public class CustomSettings extends WebDefaultSettingsManager {
    protected CustomSettings() {
        super();
    }


    @Override
    public WebSettings toSetting(WebView webView) {
        super.toSetting(webView);
        getWebSettings().setBlockNetworkImage(false);//是否阻塞加载网络图片  协议http or https
        getWebSettings().setAllowFileAccess(false); //允许加载本地文件html  file协议, 这可能会造成不安全 , 建议重写关闭
        getWebSettings().setAllowFileAccessFromFileURLs(false); //通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭
        getWebSettings().setAllowUniversalAccessFromFileURLs(false);//允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源
        return this;
    }
}
