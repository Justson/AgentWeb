package com.just.agentweb;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import java.lang.ref.WeakReference;

/**
 * Created by cenxiaozhong on 2017/5/24.
 */

public class AgentWebJsInterfaceCompat implements AgentWebCompat {

    private WeakReference<AgentWeb> mReference = null;
    private FileChooserImpl mFileChooser;
    private WeakReference<Activity> mActivityWeakReference = null;

    AgentWebJsInterfaceCompat(AgentWeb agentWeb, Activity activity) {
        mReference = new WeakReference<AgentWeb>(agentWeb);
        mActivityWeakReference = new WeakReference<Activity>(activity);
    }


    @JavascriptInterface
    public void uploadFile() {


        if (mActivityWeakReference.get() != null && mReference.get() != null) {
            mFileChooser = new FileChooserImpl.Builder()
                    .setActivity(mActivityWeakReference.get())
                    .setJSChannelCallback(new FileChooserImpl.JSChannelCallback() {
                        @Override
                        public void call(String value) {
                            if (mReference.get() != null)
                                mReference.get().getJSEntraceAccess().quickCallJs("uploadFileResult", value);
                        }
                    }).setFileUploadMsgConfig(mReference.get().getDefaultMsgConfig().getChromeClientMsgCfg().getFileUploadMsgConfig())
                    .setPermissionInterceptor(mReference.get().getPermissionInterceptor())
                    .setWebView(mReference.get().getWebCreator().getWebView())
                    .build();
            mFileChooser.openFileChooser();
        }


    }

}
