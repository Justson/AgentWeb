package com.just.library;

import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/12/8.
 */

public class DefaultDesignUIController extends AgentWebUIController {


    @Override
    protected void onJsAlert(WebView view, String url, String message) {

        /*Activity mActivity = this.mActivityWeakReference.get();
        if (mActivity == null || mActivity.isFinishing()) {
            result.cancel();
            return true;
        }
        //
        try {
            AgentWebUtils.show(view,
                    message,
                    Snackbar.LENGTH_SHORT,
                    Color.WHITE,
                    mActivity.getResources().getColor(R.color.black),
                    null,
                    -1,
                    null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            if (LogUtils.isDebug())
                LogUtils.i(TAG, throwable.getMessage());
        }*/

    }
}
