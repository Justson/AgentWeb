package com.just.library.agentweb;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.library.AgentWeb;
import com.just.library.AgentWebUtils;

/**
 * Created by cenxiaozhong on 2017/7/1.
 */

public class TwinklingWebFragment extends AgentWebFragment {

    public static  TwinklingWebFragment getInstance(Bundle bundle){

        TwinklingWebFragment mTwinklingWebFragment=new TwinklingWebFragment();
        if(mTwinklingWebFragment!=null)
            mTwinklingWebFragment.setArguments(bundle);

        return mTwinklingWebFragment;
    }


    @Override
    public String getUrl() {
        return super.getUrl();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent((ViewGroup) view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//
                .setIndicatorColorWithHeight(-1, 2)//
                .setWebSettings(getSettings())//
                .setWebViewClient(mWebViewClient)
                .setWebChromeClient(mWebChromeClient)
                .setReceivedTitleCallback(mCallback)
                .setWebLayout(new WebLayout(this.getActivity()))
                .setSecurityType(AgentWeb.SecurityType.strict)
                .addDownLoadResultListener(mDownLoadResultListener)
                .createAgentWeb()//
                .ready()//
                .go(getUrl());



        addChildren((FrameLayout) mAgentWeb.getWebCreator().getGroup());
        initView(view);

    }





    protected void addChildren(FrameLayout frameLayout) {


        TextView mTextView=new TextView(frameLayout.getContext());
        mTextView.setText("技术由 AgentWeb 提供");
        mTextView.setTextColor(Color.parseColor("#727779"));
        frameLayout.setBackgroundColor(Color.parseColor("#272b2d"));
        FrameLayout.LayoutParams mFlp=new FrameLayout.LayoutParams(-2,-2);
        mFlp.gravity= Gravity.CENTER_HORIZONTAL;
        mFlp.topMargin= AgentWebUtils.dp2px(frameLayout.getContext(),10);
        frameLayout.addView(mTextView,0,mFlp);
    }


}
