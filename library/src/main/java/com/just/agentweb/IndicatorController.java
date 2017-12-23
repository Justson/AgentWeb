package com.just.agentweb;

import android.webkit.WebView;

/**
 * Created by cenxiaozhong.
 * source code  https://github.com/Justson/AgentWeb
 */

public interface IndicatorController {

    void progress(WebView v, int newProgress);

    BaseProgressSpec offerIndicator();
}
