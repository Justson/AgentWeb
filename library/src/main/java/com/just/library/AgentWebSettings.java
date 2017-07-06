package com.just.library;

import android.webkit.WebView;

/**
 * <b>@项目名：</b> agentweb<br>
 * <b>@包名：</b>com.just.library<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> <br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述:source CODE  https://github.com/Justson/AgentWeb</b><br>
 */

public interface AgentWebSettings<T extends android.webkit.WebSettings>{

    AgentWebSettings toSetting(WebView webView);


    T getWebSettings();






}
