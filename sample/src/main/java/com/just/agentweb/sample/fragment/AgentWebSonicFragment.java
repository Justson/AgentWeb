package com.just.agentweb.sample.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.just.agentweb.MiddleWareWebClientBase;
import com.just.agentweb.sample.sonic.AgentWebSonic;
import com.just.agentweb.sample.sonic.SonicJavaScriptInterface;

import static com.just.agentweb.sample.sonic.SonicJavaScriptInterface.PARAM_CLICK_TIME;

/**
 * Created by cenxiaozhong on 2017/12/18.
 *
 * if you wanna use VasSonic to fast open first page , please
 * follow as sample to update your code;
 */

public class AgentWebSonicFragment extends AgentWebFragment {

    public static AgentWebSonicFragment create(Bundle bundle){

        AgentWebSonicFragment mAgentWebSonicFragment=new AgentWebSonicFragment();
        if(bundle!=null){
            mAgentWebSonicFragment.setArguments(bundle);
        }
        return mAgentWebSonicFragment;
    }

    private AgentWebSonic mAgentWebSonic;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mAgentWebSonic = new AgentWebSonic(this.getArguments().getString(URL_KEY), this.getContext());
        mAgentWebSonic.onCreateSession();
        super.onViewCreated(view, savedInstanceState);
        mAgentWeb.getJsInterfaceHolder().addJavaObject("sonic", new SonicJavaScriptInterface(mAgentWebSonic.getSonicSessionClient(), new Intent().putExtra(PARAM_CLICK_TIME,getArguments().getLong(PARAM_CLICK_TIME)).putExtra("loadUrlTime", System.currentTimeMillis())));
        mAgentWebSonic.go(mAgentWeb);

    }


    @Override
    public MiddleWareWebClientBase getMiddleWareWebClient() {
        return mAgentWebSonic.createSonicClientMiddleWare();
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mAgentWebSonic!=null){
            mAgentWebSonic.destrory();
        }
    }
}
