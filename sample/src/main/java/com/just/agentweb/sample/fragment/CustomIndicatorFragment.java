package com.just.agentweb.sample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.WebDefaultSettingsManager;
import com.just.agentweb.sample.widget.CommonIndicator;
import com.just.agentweb.sample.R;

/**
 * Created by cenxiaozhong on 2017/5/26.
 * source code  https://github.com/Justson/AgentWeb
 */

public class CustomIndicatorFragment extends AgentWebFragment {
    public static CustomIndicatorFragment getInstance(Bundle bundle){
        CustomIndicatorFragment mCustomIndicatorFragment=new CustomIndicatorFragment();
        if(bundle!=null)
            mCustomIndicatorFragment.setArguments(bundle);
        return mCustomIndicatorFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        CommonIndicator mCommonIndicator=new CommonIndicator(this.getActivity());
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(-2,-2);
        lp.gravity= Gravity.CENTER;
        ProgressBar mProgressBar=new ProgressBar(this.getActivity());
        mProgressBar.setBackground(this.getResources().getDrawable(R.drawable.indicator_shape));
        mCommonIndicator.addView(mProgressBar,lp);

        this.mAgentWeb = AgentWeb.with( this)//
                .setAgentWebParent((ViewGroup) view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//
                .setCustomIndicator(mCommonIndicator)
                .setAgentWebWebSettings(WebDefaultSettingsManager.getInstance())//
                .setWebViewClient(mWebViewClient)
                .setPermissionInterceptor(mPermissionInterceptor)
                .setReceivedTitleCallback(mCallback)
                .setSecurityType(AgentWeb.SecurityType.strict)
                .interceptUnkownScheme()
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
                .createAgentWeb()//
                .ready()//
                .go(getUrl());


        initView(view);
    }
}
