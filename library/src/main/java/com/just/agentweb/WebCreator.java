package com.just.agentweb;

import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * Created by cenxiaozhong .
 * source code  https://github.com/Justson/AgentWeb
 */

public interface WebCreator extends IWebIndicator {
    WebCreator create();

    WebView getWebView();

    FrameLayout getWebParentLayout();
}
