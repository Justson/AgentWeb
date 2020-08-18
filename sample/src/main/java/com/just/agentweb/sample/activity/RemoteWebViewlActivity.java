package com.just.agentweb.sample.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.flyingpigeon.library.Pigeon;
import com.flyingpigeon.library.ServiceManager;
import com.flyingpigeon.library.annotations.Route;
import com.flyingpigeon.library.annotations.thread.MainThread;
import com.just.agentweb.sample.api.Api;
import com.just.agentweb.sample.provider.ServiceProvider;
import com.queue.library.GlobalQueue;


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
        GlobalQueue.getMainQueue().postRunnable(new Runnable() {
            @Override
            public void run() {
                sayYes();
            }
        }, 500);
    }

    private void sayYes() {
        Pigeon pigeon = Pigeon.newBuilder(this.getApplicationContext()).setAuthority(ServiceProvider.class).build();
        Api api = pigeon.create(Api.class);
        api.onReady();
    }

    @Override
    public String getUrl() {
        String url = getIntent().getStringExtra("url_key");
        Log.e(TAG, " url:" + url);
        return url;
    }

    /**
     * follow this , you could invoke this method anywhere
     * Pigeon pigeon = Pigeon.newBuilder(this.getApplicationContext()).setAuthority("WebServiceProvider.class").build();
     * pigeon.route("hello/kit","http://baidu.com").resquestLarge().fly();
     *
     * @param url
     */
    @Route("hello/kit")
    @MainThread
    public void loadNewUrl(String url) {
        mAgentWeb.getUrlLoader().loadUrl(url);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServiceManager.getInstance().unpublish(this);
    }
}
