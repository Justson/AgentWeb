package com.just.library;

import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/5/14.
 * source code  https://github.com/Justson/AgentWeb
 */

public class ChromeClientCallbackManager {


    private ReceivedTitleCallback mReceivedTitleCallback;
    private GeoLocation mGeoLocation;

    public ReceivedTitleCallback getReceivedTitleCallback() {
        return mReceivedTitleCallback;
    }



    public ChromeClientCallbackManager setReceivedTitleCallback(ReceivedTitleCallback receivedTitleCallback) {
        mReceivedTitleCallback = receivedTitleCallback;
        return this;
    }
    public ChromeClientCallbackManager setGeoLocation(GeoLocation geoLocation){
       this.mGeoLocation=geoLocation;
        return this;
    }

    public interface ReceivedTitleCallback{
         void onReceivedTitle(WebView view, String title);
    }



    public static class GeoLocation {
        /*1 表示定位开启, 0 表示关闭*/
        public int tag=1;


    }
}
