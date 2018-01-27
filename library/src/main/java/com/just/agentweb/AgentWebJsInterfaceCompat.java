package com.just.agentweb;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import java.lang.ref.WeakReference;

/**
 * Created by cenxiaozhong on 2017/5/24.
 */

public class AgentWebJsInterfaceCompat {

    private WeakReference<AgentWeb> mReference = null;
    private FileChooser mFileChooser;
    private WeakReference<Activity> mActivityWeakReference = null;

    AgentWebJsInterfaceCompat(AgentWeb agentWeb, Activity activity) {
        mReference = new WeakReference<AgentWeb>(agentWeb);
        mActivityWeakReference = new WeakReference<Activity>(activity);
    }


    @JavascriptInterface
    public void uploadFile() {
        uploadFile("*/*");
    }

    public void uploadFile(String acceptType){
        if (mActivityWeakReference.get() != null && mReference.get() != null) {
            mFileChooser = new FileChooser.Builder()
                    .setActivity(mActivityWeakReference.get())
                    .setJsChannelCallback(new FileChooser.JsChannelCallback() {
                        @Override
                        public void call(String value) {
                            if (mReference.get() != null)
                                mReference.get().getJsAccessEntrace().quickCallJs("uploadFileResult", value);
                        }
                    }).setFileUploadMsgConfig(mReference.get().getDefaultMsgConfig().getChromeClientMsgCfg().getFileUploadMsgConfig())
                    .setPermissionInterceptor(mReference.get().getPermissionInterceptor())
                    .setAcceptType(acceptType)
                    .setWebView(mReference.get().getWebCreator().getWebView())
                    .build();
            mFileChooser.openFileChooser();
        }
    }

}
