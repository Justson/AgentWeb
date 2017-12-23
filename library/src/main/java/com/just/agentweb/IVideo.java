package com.just.agentweb;

import android.view.View;
import android.webkit.WebChromeClient;

/**
 * Created by cenxiaozhong on 2017/6/10.
 * source code  https://github.com/Justson/AgentWeb
 */

public interface IVideo {


    void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback);


    void onHideCustomView();


    boolean isVideoState();

}
