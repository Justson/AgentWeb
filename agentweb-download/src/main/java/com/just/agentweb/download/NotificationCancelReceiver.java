package com.just.agentweb.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.just.agentweb.LogUtils;

/**
 * Created by cenxiaozhong on 2018/2/12.
 */

public class NotificationCancelReceiver extends BroadcastReceiver {


    public NotificationCancelReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("com.agentweb.cancelled")) {
            try {
                String url = intent.getStringExtra("TAG");
                CancelDownloadInformer.getInformer().cancelAction(url);
            } catch (Throwable ignore) {
                if (LogUtils.isDebug()) {
                    ignore.printStackTrace();
                }
            }

        }
    }
}