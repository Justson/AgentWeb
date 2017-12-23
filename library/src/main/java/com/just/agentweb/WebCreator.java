package com.just.agentweb;

import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by cenxiaozhong .
 * source code  https://github.com/Justson/AgentWeb
 */

public interface WebCreator extends ProgressManager {
    WebCreator create();

    WebView get();

    ViewGroup getGroup();
}
