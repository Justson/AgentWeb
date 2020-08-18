package com.just.agentweb.sample.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.flyingpigeon.library.ServiceManager;
import com.flyingpigeon.library.annotations.thread.MainThread;
import com.flyingpigeon.library.annotations.Route;


/**
 * @author cenxiaozhong
 * @since 1.0.0
 */
public class RemoteWebViewlActivity extends WebActivity {

    public static final String TAG = RemoteWebViewlActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ServiceManager.getInstance().publish(this);
    }

    @Override
    public String getUrl() {
        String url = getIntent().getStringExtra("url_key");
        Log.e(TAG, " url:" + url);
        return url;
    }

    @Route("hello/kit")
    @MainThread
    public void load() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServiceManager.getInstance().unpublish(this);
    }
}
