package com.just.agentweb;

import android.webkit.WebView;

/**
 * 邮箱 cenxiaozhong.qqcom@qq.com
 * source code  https://github.com/Justson/AgentWeb
 */

public interface IAgentWebSettings<T extends android.webkit.WebSettings> {

    IAgentWebSettings toSetting(WebView webView);

    T getWebSettings();

}
