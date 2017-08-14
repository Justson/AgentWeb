package com.just.library.agentweb;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.just.library.IWebLayout;

/**
 * Created by cenxiaozhong on 2017/7/1.
 *  source CODE  https://github.com/Justson/AgentWeb
 */

public class SmartRefreshWebFragment extends BounceWebFragment {

    public static SmartRefreshWebFragment getInstance(Bundle bundle){

        SmartRefreshWebFragment mSmartRefreshWebFragment =new SmartRefreshWebFragment();
        if(mSmartRefreshWebFragment !=null)
            mSmartRefreshWebFragment.setArguments(bundle);

        return mSmartRefreshWebFragment;
    }

    private SmartRefreshWebLayout mSmartRefreshLayout=null;

    @Override
    public String getUrl() {
        return super.getUrl();
    }


    protected IWebLayout getWebLayout(){
        return this.mSmartRefreshLayout=new SmartRefreshWebLayout(this.getActivity());
    }




    protected void addBGChild(FrameLayout frameLayout) {

        frameLayout.setBackgroundColor(Color.TRANSPARENT);
        /*TextView mTextView=new TextView(frameLayout.getContext());
        mTextView.setText("技术由 AgentWeb 提供");
        mTextView.setTextSize(16);
        mTextView.setTextColor(Color.parseColor("#727779"));
        frameLayout.setBackgroundColor(Color.parseColor("#272b2d"));
        FrameLayout.LayoutParams mFlp=new FrameLayout.LayoutParams(-2,-2);
        mFlp.gravity= Gravity.CENTER_HORIZONTAL;
        mFlp.topMargin= AgentWebUtils.dp2px(frameLayout.getContext(),15);
        frameLayout.addView(mTextView,0,mFlp);*/
    }


}
