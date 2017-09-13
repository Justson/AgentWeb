package com.just.library.agentweb;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.just.library.AgentWeb;

/**
 * Created by cenxiaozhong on 2017/7/1.
 * source code  https://github.com/Justson/AgentWeb
 */

public class JsbridgeWebFragment extends AgentWebFragment {

    public static JsbridgeWebFragment getInstance(Bundle bundle){

        JsbridgeWebFragment mJsbridgeWebFragment =new JsbridgeWebFragment();
        if(mJsbridgeWebFragment !=null)
            mJsbridgeWebFragment.setArguments(bundle);

        return mJsbridgeWebFragment;
    }

    private BridgeWebView mBridgeWebView;

    @Override
    public String getUrl() {
        return super.getUrl();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mBridgeWebView=new BridgeWebView(getActivity());
        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent((ViewGroup) view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//
                .setIndicatorColorWithHeight(-1, 2)//
                .setAgentWebWebSettings(getSettings())//
                .setWebViewClient(new BridgeWebViewClient(mBridgeWebView))
                .setWebChromeClient(mWebChromeClient)
                .setReceivedTitleCallback(mCallback)
                .setWebView(mBridgeWebView)
                .setSecurityType(AgentWeb.SecurityType.strict)
                .addDownLoadResultListener(mDownLoadResultListener)
                .createAgentWeb()//
                .ready()//
                .go(getUrl());




        initView(view);



        mBridgeWebView.registerHandler("submitFromWeb", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                function.onCallBack("submitFromWeb exe, response data 中文 from Java");
            }

        });

        User user = new User();
        Location location = new Location();
        location.address = "SDU";
        user.location = location;
        user.name = "Agentweb --> Jsbridge";



        mBridgeWebView.callHandler("functionInJs", new Gson().toJson(user), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                Log.i(TAG,"data:"+data);
            }
        });

        mBridgeWebView.send("hello");



    }





    static class Location {
        String address;
    }

    static class User {
        String name;
        Location location;
        String testStr;
    }





}
