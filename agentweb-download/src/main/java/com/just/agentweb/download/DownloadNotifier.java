/*
 * Copyright (C)  Justson(https://github.com/Justson/AgentWeb)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.just.agentweb.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.just.agentweb.AgentWebUtils;
import com.just.agentweb.LogUtils;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.just.agentweb.AgentWebConfig.AGENTWEB_VERSION;
/**
 * @author cenxiaozhong
 * @date 2018/5/13
 */
public class DownloadNotifier {

    private static final int FLAG = Notification.FLAG_INSISTENT;
    int requestCode = (int) SystemClock.uptimeMillis();
    private int NOTIFICATION_ID;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private NotificationCompat.Builder mBuilder;
    private Context mContext;
    private String mChannelId = "";

    public DownloadNotifier(Context context, int ID) {
        this.NOTIFICATION_ID = ID;
        mContext = context;
        // 获取系统服务来初始化对象
        mNotificationManager = (NotificationManager) mContext
                .getSystemService(NOTIFICATION_SERVICE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mBuilder = new NotificationCompat.Builder(mContext, mChannelId = mContext.getPackageName().concat(AGENTWEB_VERSION));
                NotificationChannel mNotificationChannel = new NotificationChannel(mChannelId, AgentWebUtils.getApplicationName(context), NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
                mNotificationManager.createNotificationChannel(mNotificationChannel);
            } else {
                mBuilder = new NotificationCompat.Builder(mContext);
            }
        } catch (Throwable ignore) {
            if (LogUtils.isDebug()) {
                ignore.printStackTrace();
            }
        }

    }


    public void notifyProgress(PendingIntent pendingIntent, int smallIcon,
                               String ticker, String title, String content, boolean sound, boolean vibrate, boolean lights, PendingIntent pendingIntentCancel) {

        setCompatBuilder(pendingIntent, smallIcon, ticker, title, content, sound, vibrate, lights, pendingIntentCancel);

    }

    /**
     * 设置在顶部通知栏中的各种信息
     *
     * @param pendingIntent
     * @param smallIcon
     * @param ticker
     * @param pendingIntentCancel
     */
    private void setCompatBuilder(PendingIntent pendingIntent, int smallIcon, String ticker,
                                  String title, String content, boolean sound, boolean vibrate, boolean lights, PendingIntent pendingIntentCancel) {
//        // 如果当前Activity启动在前台，则不开启新的Activity。
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        // 当设置下面PendingIntent.FLAG_UPDATE_CURRENT这个参数的时候，常常使得点击通知栏没效果，你需要给notification设置一个独一无二的requestCode
//        // 将Intent封装进PendingIntent中，点击通知的消息后，就会启动对应的程序
//        PendingIntent pIntent = PendingIntent.getActivity(mContext,
//                requestCode, intent, FLAG);

        mBuilder.setContentIntent(pendingIntent);// 该通知要启动的Intent
        mBuilder.setSmallIcon(smallIcon);// 设置顶部状态栏的小图标
        mBuilder.setTicker(ticker);// 在顶部状态栏中的提示信息

        mBuilder.setContentTitle(title);// 设置通知中心的标题
        mBuilder.setContentText(content);// 设置通知中心中的内容
        mBuilder.setWhen(System.currentTimeMillis());


		/*
         * 将AutoCancel设为true后，当你点击通知栏的notification后，它会自动被取消消失,
		 * 不设置的话点击消息后也不清除，但可以滑动删除
		 */
        mBuilder.setAutoCancel(true);
        // 将Ongoing设为true 那么notification将不能滑动删除
        // notifyBuilder.setOngoing(true);
        /*
         * 从Android4.1开始，可以通过以下方法，设置notification的优先级，
		 * 优先级越高的，通知排的越靠前，优先级低的，不会在手机最顶部的状态栏显示图标
		 */
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        /*
         * Notification.DEFAULT_ALL：铃声、闪光、震动均系统默认。
		 * Notification.DEFAULT_SOUND：系统默认铃声。
		 * Notification.DEFAULT_VIBRATE：系统默认震动。
		 * Notification.DEFAULT_LIGHTS：系统默认闪光。
		 * notifyBuilder.setDefaults(Notification.DEFAULT_ALL);
		 */
        int defaults = 0;

        mBuilder.setDeleteIntent(pendingIntentCancel);


        if (sound) {
            defaults |= Notification.DEFAULT_SOUND;
        }
        if (vibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (lights) {
            defaults |= Notification.DEFAULT_LIGHTS;
        }


        mBuilder.setDefaults(defaults);
    }

    public void setProgress(int maxprogress, int currentprogress, boolean exc) {
        mBuilder.setProgress(maxprogress, currentprogress, exc);
        sent();
    }

    public void setContentText(String text) {

        mBuilder.setContentText(text);
    }

    public boolean hasDeleteContent() {
        return mBuilder.getNotification().deleteIntent != null;
    }

    public void setDelecte(PendingIntent intent) {
        mBuilder.getNotification().deleteIntent = intent;
    }

    public void setProgressFinish(String content, PendingIntent pendingIntent) {
        mBuilder.setContentText(content);
        mBuilder.setProgress(100, 100, false);
        mBuilder.setContentIntent(pendingIntent);
        sent();
    }

    /**
     * 发送通知
     */
    void sent() {


        mNotification = mBuilder.build();
        //LogUtils.i("Info","send:"+NOTIFICATION_ID+"  nocation:"+mNotification+"  ");
        // 发送该通知
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }


    /**
     * 根据id清除通知
     */
    public void cancel(int id) {
        mNotificationManager.cancel(id);
    }
}
