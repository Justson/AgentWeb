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

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.just.agentweb.AgentWebUtils;
import com.just.agentweb.LogUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.just.agentweb.AgentWebConfig.AGENTWEB_VERSION;

/**
 * @author cenxiaozhong
 * @date 2018/5/13
 */
public class DownloadNotifier {

	private static final int FLAG = Notification.FLAG_INSISTENT;
	int requestCode = (int) SystemClock.uptimeMillis();
	private int mNotificationId;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private NotificationCompat.Builder mBuilder;
	private Context mContext;
	private String mChannelId = "";
	private volatile boolean mAddedCancelAction = false;
	private String mUrl;
	private File mFile;
	private static final String TAG = DownloadNotifier.class.getSimpleName();
	private NotificationCompat.Action mAction;

	DownloadNotifier(Context context, int id) {
		this.mNotificationId = id;
		mContext = context;
		// 获取系统服务来初始化对象
		mNotificationManager = (NotificationManager) mContext
				.getSystemService(NOTIFICATION_SERVICE);

		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				mBuilder = new NotificationCompat.Builder(mContext,
						mChannelId = mContext.getPackageName().concat(AGENTWEB_VERSION));
				NotificationChannel mNotificationChannel = new NotificationChannel(mChannelId,
						AgentWebUtils.getApplicationName(context),
						NotificationManager.IMPORTANCE_LOW);
				NotificationManager mNotificationManager = (NotificationManager) mContext
						.getSystemService(NOTIFICATION_SERVICE);
				mNotificationManager.createNotificationChannel(mNotificationChannel);
				mNotificationChannel.enableLights(false);
				mNotificationChannel.enableVibration(false);
				mNotificationChannel.setSound(null,null);
			} else {
				mBuilder = new NotificationCompat.Builder(mContext);
			}
		} catch (Throwable ignore) {
			if (LogUtils.isDebug()) {
				ignore.printStackTrace();
			}
		}
	}


	void initBuilder(DownloadTask downloadTask) {
		String title = TextUtils.isEmpty(downloadTask.getFile().getName()) ?
				mContext.getString(R.string.agentweb_file_download) :
				downloadTask.getFile().getName();

		if (title.length() > 20) {
			title = "..." + title.substring(title.length() - 20, title.length());
		}
		mBuilder.setContentIntent(PendingIntent.getActivity(mContext, 200, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
		// 设置顶部状态栏的小图标
		mBuilder.setSmallIcon(downloadTask.getDrawableRes());
		// 在顶部状态栏中的提示信息
		mBuilder.setTicker(mContext.getString(R.string.agentweb_trickter));
		// 设置通知中心的标题
		mBuilder.setContentTitle(title);
		// 设置通知中心中的内容
		mBuilder.setContentText(mContext.getString(R.string.agentweb_coming_soon_download));
		mBuilder.setWhen(System.currentTimeMillis());
		mBuilder.setAutoCancel(true);
		mBuilder.setPriority(NotificationCompat.PRIORITY_LOW);
		int defaults = 0;
		this.mUrl = downloadTask.getUrl();
		this.mFile = downloadTask.getFile();
		mBuilder.setDeleteIntent(buildCancelContent(mContext, downloadTask.getId(), downloadTask.getUrl()));
		mBuilder.setDefaults(defaults);
	}

	private PendingIntent buildCancelContent(Context context, int id, String url) {

		Intent intentCancel = new Intent(context, NotificationCancelReceiver.class);
		intentCancel.setAction(NotificationCancelReceiver.ACTION);
		intentCancel.putExtra("TAG", url);
		PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(context, id << 3, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);
		LogUtils.i(TAG, "id<<3:" + (id << 3));
		return pendingIntentCancel;
	}

	private void setProgress(int maxprogress, int currentprogress, boolean exc) {
		mBuilder.setProgress(maxprogress, currentprogress, exc);
		sent();
	}


	private boolean hasDeleteContent() {
		return mBuilder.getNotification().deleteIntent != null;
	}

	private void setDelecte(PendingIntent intent) {
		mBuilder.getNotification().deleteIntent = intent;
	}

	/**
	 * 发送通知
	 */
	private void sent() {


		mNotification = mBuilder.build();
		// 发送该通知
		mNotificationManager.notify(mNotificationId, mNotification);
	}


	void onPreDownload() {
		sent();
	}


	void onDownloading(int progress) {

		if (!this.hasDeleteContent()) {
			this.setDelecte(buildCancelContent(mContext, mNotificationId, mUrl));
		}
		if (!mAddedCancelAction) {
			mAddedCancelAction = true;
			mAction = new NotificationCompat.Action(R.drawable.ic_cancel_transparent_2dp,
					mContext.getString(android.R.string.cancel),
					buildCancelContent(mContext, mNotificationId, mUrl));
			mBuilder.addAction(mAction);

		}
		mBuilder.setContentText(mContext.getString(R.string.agentweb_current_downloading_progress, (progress + "%")));
		this.setProgress(100, progress, false);
		sent();
	}

	void onDownloadFinished() {


		try {
			/**
			 * 用反射获取 mActions 该 Field , mBuilder.mActions 防止迭代该Field域访问不到，或者该Field
			 * 改名导致程序崩溃。
			 */
			Class<? extends NotificationCompat.Builder> clazz = mBuilder.getClass();
			Field mField = clazz.getDeclaredField("mActions");
			ArrayList<NotificationCompat.Action> mActions = null;
			if (null != mField) {
				mActions = (ArrayList<NotificationCompat.Action>) mField.get(mBuilder);
			}
			int index = -1;
			if (null != mActions && (index = mActions.indexOf(mAction)) != -1) {
				mActions.remove(index);
			}

		} catch (Throwable ignore) {
			if (LogUtils.isDebug()) {
				ignore.printStackTrace();
			}
		}
		Intent mIntent = AgentWebUtils.getCommonFileIntentCompat(mContext, mFile);
		setDelecte(null);
		if (null != mIntent) {
			if (!(mContext instanceof Activity)) {
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			PendingIntent rightPendIntent = PendingIntent
					.getActivity(mContext,
							mNotificationId << 4, mIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);

			mBuilder.setContentText(mContext.getString(R.string.agentweb_click_open));
			mBuilder.setProgress(100, 100, false);
			mBuilder.setContentIntent(rightPendIntent);
			sent();
		}
	}


	/**
	 * 根据id清除通知
	 */
	void cancel() {
		mNotificationManager.cancel(mNotificationId);
	}
}
