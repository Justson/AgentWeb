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
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebView;

import com.just.agentweb.AbsAgentWebUIController;
import com.just.agentweb.Action;
import com.just.agentweb.ActionActivity;
import com.just.agentweb.AgentWebPermissions;
import com.just.agentweb.AgentWebUtils;
import com.just.agentweb.LogUtils;
import com.just.agentweb.PermissionInterceptor;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cenxiaozhong
 * @date 2017/5/13
 */
public class DefaultDownloadImpl implements android.webkit.DownloadListener {
	/**
	 * Application Context
	 */
	private Context mContext;
	/**
	 * 下载监听，DownloadListener#onStart 下载的时候触发，DownloadListener#result下载结束的时候触发
	 * 4.0.0 每一次下载都会触发这两个方法，4.0.0以下只有触发下载才会回调这两个方法。
	 */
	private ConcurrentHashMap<String, DownloadListener> mDownloadListeners = new ConcurrentHashMap<>();

	private ConcurrentHashMap<String, ExtraServiceImpl> mExtraServiceImpls = new ConcurrentHashMap<>();
	/**
	 * Activity
	 */
	private WeakReference<Activity> mActivityWeakReference = null;
	/**
	 * TAG 用于打印，标识
	 */
	private static final String TAG = DefaultDownloadImpl.class.getSimpleName();
	/**
	 * 权限拦截
	 */
	private PermissionInterceptor mPermissionListener = null;

	/**
	 * AbsAgentWebUIController
	 */
	private WeakReference<AbsAgentWebUIController> mAgentWebUIController;
	/**
	 * ExtraServiceImpl
	 */
	private ExtraServiceImpl mExtraServiceImpl;
	/**
	 * 根据p3c，预编译正则，提升性能。
	 */
	private static Pattern DISPOSITION_PATTERN = Pattern.compile(".*filename=(.*)");

	DefaultDownloadImpl(ExtraServiceImpl extraServiceImpl) {
		if (!extraServiceImpl.mIsCloneObject) {
			this.bind(extraServiceImpl);
			this.mExtraServiceImpl = extraServiceImpl;
		}
	}

	private void bind(ExtraServiceImpl extraServiceImpl) {
		this.mActivityWeakReference = new WeakReference<Activity>(extraServiceImpl.mActivity);
		this.mContext = extraServiceImpl.mActivity.getApplicationContext();
		if (extraServiceImpl.mDownloadListener != null && !TextUtils.isEmpty(extraServiceImpl.mUrl)) {
			this.mDownloadListeners.put(extraServiceImpl.mUrl, extraServiceImpl.mDownloadListener);
		}
		this.mPermissionListener = extraServiceImpl.mPermissionInterceptor;
		this.mAgentWebUIController = new WeakReference<AbsAgentWebUIController>(AgentWebUtils.getAgentWebUIControllerByWebView(extraServiceImpl.mWebView));
	}


	@Override
	public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
		onDownloadStartInternal(url, userAgent, contentDisposition, mimetype, contentLength, null);
	}


	synchronized void onDownloadStartInternal(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, ExtraServiceImpl extraServiceImpl) {

		if (null == mActivityWeakReference.get() || mActivityWeakReference.get().isFinishing()) {
			return;
		}
		if (null != this.mPermissionListener) {
			if (this.mPermissionListener.intercept(url, AgentWebPermissions.STORAGE, "download")) {
				return;
			}
		}

		ExtraServiceImpl mCloneExtraServiceImpl = null;
		if (null == extraServiceImpl) {
			try {
				mCloneExtraServiceImpl = (ExtraServiceImpl) this.mExtraServiceImpl.clone();
			} catch (CloneNotSupportedException ignore) {
				if (LogUtils.isDebug()) {
					ignore.printStackTrace();
				}
				LogUtils.i(TAG, " clone object failure !!! ");
				return;
			}
		} else {
			mCloneExtraServiceImpl = extraServiceImpl;
		}
		mCloneExtraServiceImpl
				.setUrl(url)
				.setMimetype(mimetype)
				.setContentDisposition(contentDisposition)
				.setContentLength(contentLength)
				.setUserAgent(userAgent);
		this.mExtraServiceImpls.put(url, mCloneExtraServiceImpl);
		if (mCloneExtraServiceImpl.mDownloadListener != null && !TextUtils.isEmpty(mCloneExtraServiceImpl.mUrl)) {
			this.mDownloadListeners.put(mCloneExtraServiceImpl.mUrl, mCloneExtraServiceImpl.mDownloadListener);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			List<String> mList = null;
			if ((mList = checkNeedPermission()).isEmpty()) {
				preDownload(url);
			} else {
				Action mAction = Action.createPermissionsAction(mList.toArray(new String[]{}));
				ActionActivity.setPermissionListener(getPermissionListener(url));
				ActionActivity.start(mActivityWeakReference.get(), mAction);
			}
		} else {
			preDownload(url);
		}
	}

	private ActionActivity.PermissionListener getPermissionListener(final String url) {
		return new ActionActivity.PermissionListener() {
			@Override
			public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras) {
				if (checkNeedPermission().isEmpty()) {
					preDownload(url);
				} else {
					if (null != mAgentWebUIController.get()) {
						mAgentWebUIController
								.get()
								.onPermissionsDeny(
										checkNeedPermission().
												toArray(new String[]{}),
										AgentWebPermissions.ACTION_STORAGE, "Download");
					}
					LogUtils.e(TAG, "储存权限获取失败~");
				}

			}
		};
	}

	private List<String> checkNeedPermission() {
		List<String> deniedPermissions = new ArrayList<>();
		if (!AgentWebUtils.hasPermission(mActivityWeakReference.get(), AgentWebPermissions.STORAGE)) {
			deniedPermissions.addAll(Arrays.asList(AgentWebPermissions.STORAGE));
		}
		return deniedPermissions;
	}

	private void preDownload(String url) {
		ExtraServiceImpl extraService = mExtraServiceImpls.get(url);
		DownloadListener downloadListener = mDownloadListeners.get(extraService.mUrl);
		// true 表示用户取消了该下载事件。
		if (null != downloadListener
				&& downloadListener
				.onStart(extraService.mUrl,
						extraService.mUserAgent,
						extraService.mContentDisposition,
						extraService.mMimetype,
						extraService.mContentLength,
						extraService)) {
			return;
		}
		File file = getFile(extraService.mContentDisposition, extraService.mUrl);
		// File 创建文件失败
		if (null == file) {
			LogUtils.e(TAG, "新建文件失败");
			return;
		}
		if (file.exists() && file.length() >= extraService.mContentLength && extraService.mContentLength > 0) {
			// true 表示用户处理了下载完成后续的通知用户事件
			if (null != downloadListener && downloadListener.onResult(null, Uri.fromFile(file), extraService.mUrl, extraService)) {
				return;
			}
			Intent mIntent = AgentWebUtils.getCommonFileIntentCompat(mContext, file);
			try {
//                mContext.getPackageManager().resolveActivity(mIntent)
				if (null != mIntent) {
					if (!(mContext instanceof Activity)) {
						mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					}
					mContext.startActivity(mIntent);
				}
				return;
			} catch (Throwable throwable) {
				if (LogUtils.isDebug()) {
					throwable.printStackTrace();
				}
			}
			return;
		}
		extraService.setFile(file);
		// 移动数据
		if (!extraService.isForceDownload() &&
				AgentWebUtils.checkNetworkType(mContext) > 1) {

			showDialog(url);
			return;
		}
		// 该链接是否正在下载
		if (ExecuteTasksMap.getInstance().contains(extraService.mUrl)
				|| ExecuteTasksMap.getInstance().contains(file.getAbsolutePath())) {
			if (null != mAgentWebUIController.get()) {
				mAgentWebUIController.get().onShowMessage(
						mActivityWeakReference.get()
								.getString(R.string.agentweb_download_task_has_been_exist),
						TAG.concat("|preDownload"));
			}
			return;
		}
		performDownload(url);
	}

	private void forceDownload(final String url) {
		ExtraServiceImpl extraService = mExtraServiceImpls.get(url);
		extraService.setForceDownload(true);
		performDownload(url);
	}

	private void showDialog(final String url) {
		Activity mActivity;
		if (null == (mActivity = mActivityWeakReference.get()) || mActivity.isFinishing()) {
			return;
		}
		ExtraServiceImpl extraService = mExtraServiceImpls.get(url);
		AbsAgentWebUIController mAgentWebUIController;
		if (null != (mAgentWebUIController = this.mAgentWebUIController.get())) {
			mAgentWebUIController.onForceDownloadAlert(extraService.mUrl, createCallback(extraService.getUrl()));
		}
	}

	private Handler.Callback createCallback(final String url) {
		return new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				forceDownload(url);
				return true;
			}
		};
	}

	private void performDownload(String url) {
		try {
			ExtraServiceImpl extraService = mExtraServiceImpls.get(url);
			if (null != mAgentWebUIController.get()) {
				mAgentWebUIController.get()
						.onShowMessage(mActivityWeakReference.get().getString(R.string.agentweb_coming_soon_download) + ":" + extraService.getFile().getName(), TAG.concat("|performDownload"));
			}
			/*DownloadTask mDownloadTask = new DownloadTask(NOTICATION_ID.incrementAndGet(),
					this.mSimpleDownloadListener,
					mContext, file,
					this.mCloneExtraServiceImpl);*/
			extraService.setDownloadListener(mSimpleDownloadListener);
			new Downloader().download(extraService);
		} catch (Throwable ignore) {
			if (LogUtils.isDebug()) {
				ignore.printStackTrace();
			}
		}
	}

	private File getFile(String contentDisposition, String url) {
		String fileName = "";
		try {
			fileName = getFileNameByContentDisposition(contentDisposition);
			if (TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(url)) {
				Uri mUri = Uri.parse(url);
				fileName = mUri.getPath().substring(mUri.getPath().lastIndexOf('/') + 1);
			}
			if (!TextUtils.isEmpty(fileName) && fileName.length() > 64) {
				fileName = fileName.substring(fileName.length() - 64, fileName.length());
			}
			if (TextUtils.isEmpty(fileName)) {
				fileName = AgentWebUtils.md5(url);
			}
			if (fileName.contains("\"")) {
				fileName = fileName.replace("\"", "");
			}
			ExtraServiceImpl extraService = mExtraServiceImpls.get(url);
			return AgentWebUtils.createFileByName(mContext, fileName, !extraService.isOpenBreakPointDownload());
		} catch (Throwable e) {
			if (LogUtils.isDebug()) {
				LogUtils.i(TAG, "fileName:" + fileName);
				e.printStackTrace();
			}
		}
		return null;
	}

	private String getFileNameByContentDisposition(String contentDisposition) {
		if (TextUtils.isEmpty(contentDisposition)) {
			return "";
		}
		Matcher m = DISPOSITION_PATTERN.matcher(contentDisposition.toLowerCase());
		if (m.find()) {
			return m.group(1);
		} else {
			return "";
		}
	}

	private SimpleDownloadListener mSimpleDownloadListener = new SimpleDownloadListener() {
		@Override
		public boolean onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra) {
			return false;
		}

		@Override
		public void onProgress(String url, long downloaded, long length, long useTime) {
			DownloadListener downloadingListener = DefaultDownloadImpl.this.mDownloadListeners.get(url);
			if (null != downloadingListener) {
				downloadingListener.onProgress(url, downloaded, length, useTime);
			}
		}

		@Override
		public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
			try {
				DownloadListener downloadListener = mDownloadListeners.remove(url);
				return null != downloadListener && downloadListener.onResult(throwable, path, url, extra);
			} finally {
				mExtraServiceImpls.remove(url);
			}
		}
	};


	public static DefaultDownloadImpl create(@NonNull Activity activity,
	                                         @NonNull WebView webView,
	                                         @Nullable SimpleDownloadListener downloadListener,
	                                         @Nullable PermissionInterceptor permissionInterceptor) {
		ExtraServiceImpl extraService = new ExtraServiceImpl()
				.setActivity(activity)
				.setWebView(webView)
				.setPermissionInterceptor(permissionInterceptor);
		extraService.setDownloadListener(downloadListener);
		return extraService.create();
	}

}
