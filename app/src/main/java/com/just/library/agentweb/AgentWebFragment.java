package com.just.library.agentweb;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.LinearLayout;

import com.just.library.AgentWeb;
import com.just.library.WebDefaultSettingsManager;

/**
 * Created by cenxiaozhong on 2017/5/15.
 */

public class AgentWebFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        AgentWeb mAgentWeb= AgentWeb.with(this.getActivity(),this)//
        .configRootView((ViewGroup) view,new LinearLayout.LayoutParams(-1,-1))//
        .useDefaultIndicator()//
        .setWebSettings(WebDefaultSettingsManager.getInstance())//
        .createAgentWeb()//
        .ready()//
        .go("http://www.mi.com");




    }
}
