package com.just.library;

import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/5/14.
 * source code  https://github.com/Justson/AgentWeb
 */

public class ChromeClientCallbackManager {


    private ReceivedTitleCallback mReceivedTitleCallback;

    public ReceivedTitleCallback getReceivedTitleCallback() {
        return mReceivedTitleCallback;
    }

    public ChromeClientCallbackManager setReceivedTitleCallback(ReceivedTitleCallback receivedTitleCallback) {
        mReceivedTitleCallback = receivedTitleCallback;
        return this;
    }

    public interface ReceivedTitleCallback{
         void onReceivedTitle(WebView view, String title);
    }

}
