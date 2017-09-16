package com.just.library;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by cenxiaozhong on 2017/6/10.
 */

public class VideoImpl implements IVideo, EventInterceptor {


    private Activity mActivity;
    private WebView mWebView;
    private static final String TAG = VideoImpl.class.getSimpleName();
    private Set<Pair<Integer,Integer>> flags=null;
    public VideoImpl(Activity mActivity, WebView webView) {
        this.mActivity = mActivity;
        this.mWebView = webView;
        flags=new HashSet<>();

    }

    private View moiveView = null;
    private ViewGroup moiveParentView = null;
    private WebChromeClient.CustomViewCallback mCallback;

    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {

        LogUtils.i(TAG, "onShowCustomView:" + view);

        Activity mActivity;
        if ((mActivity = this.mActivity) == null||mActivity.isFinishing())
            return;
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);



        Window mWindow=mActivity.getWindow();
        Pair<Integer,Integer>mPair=null;
        //保存当前屏幕的状态
        if((mWindow.getAttributes().flags& WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)==0){
            mPair=new Pair<>(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,0);
            mWindow.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            flags.add(mPair);



        }

        if((Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)&&(mWindow.getAttributes().flags&WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)==0){
            mPair=new Pair<>(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,0);
            mWindow.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            flags.add(mPair);

        }


        if (moiveView != null) {
            callback.onCustomViewHidden();
            return;
        }

        if (mWebView != null)
            mWebView.setVisibility(View.GONE);

        if (moiveParentView == null) {
            FrameLayout mDecorView = (FrameLayout) mActivity.getWindow().getDecorView();
            moiveParentView = new FrameLayout(mActivity);
            moiveParentView.setBackgroundColor(Color.BLACK);
            mDecorView.addView(moiveParentView);
        }
        this.mCallback = callback;
        moiveParentView.addView(this.moiveView = view);


        moiveParentView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onHideCustomView() {

        LogUtils.i(TAG, "onHideCustomView:" + moiveView);
        if (moiveView == null)
            return;
        if (mActivity != null && mActivity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

       if(!flags.isEmpty()){
           for(Pair<Integer,Integer>mPair:flags){
               mActivity.getWindow().setFlags(mPair.second,mPair.first);
               LogUtils.i(TAG,"f:"+mPair.first+"  s:"+mPair.second);
           }
           flags.clear();
       }

        moiveView.setVisibility(View.GONE);
        if (moiveParentView != null && moiveView != null) {
            moiveParentView.removeView(moiveView);

        }
        if (moiveParentView != null)
            moiveParentView.setVisibility(View.GONE);

        if (this.mCallback != null)
            mCallback.onCustomViewHidden();
        this.moiveView = null;
        if (mWebView != null)
            mWebView.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean isVideoState() {
        return moiveView != null;
    }

    @Override
    public boolean event() {

        LogUtils.i(TAG, "event:" + isVideoState());
        if (isVideoState()) {
            onHideCustomView();
            return true;
        } else {
            return false;
        }

    }
}
