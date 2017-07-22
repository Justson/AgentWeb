package com.just.library;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

/**
 * Created by cenxiaozhong on 2017/7/22.
 */

public abstract class BaseAgentWebActivity  extends AppCompatActivity{

    protected AgentWeb mAgentWeb;
    protected ViewGroup parentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(parentView = getAgentWebParent(), new LinearLayout.LayoutParams(-1, -1))//
                .useDefaultIndicator()//
                .setIndicatorColorWithHeight(getIndicatorColor(), getIndicatorHeight())
                .setReceivedTitleCallback(getReceivedTitleCallback())
                .setWebChromeClient(getWebChromeClient())
                .setWebViewClient(getWebViewClient())
                .addDownLoadResultListener(getDownLoadResultListener())
                .setAgentWebSettings(getAgentWebSettings())
                .setSecutityType(AgentWeb.SecurityType.strict)
                .createAgentWeb()//
                .ready()
                .go(getUrl());

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        LogUtils.i("Info", "result:" + requestCode + " result:" + resultCode);
        mAgentWeb.uploadFileResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }




    protected @Nullable DownLoadResultListener getDownLoadResultListener(){
        return null;
    }

    protected @Nullable ChromeClientCallbackManager.ReceivedTitleCallback getReceivedTitleCallback(){
        return null;
    }
    protected @Nullable String getUrl(){
        return null;
    }
    public AgentWebSettings getAgentWebSettings() {
        return WebDefaultSettingsManager.getInstance();
    }

    protected ViewGroup getAgentWebParent() {
        return null;
    }
    protected @Nullable WebChromeClient getWebChromeClient(){
        return null;
    }
    protected @ColorInt int getIndicatorColor(){
        return -1;
    }

    protected int getIndicatorHeight() {
        return -1;
    }
    protected @Nullable WebViewClient getWebViewClient(){
        return null;
    }
}
