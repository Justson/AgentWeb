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
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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
	 * 通知ID，默认从1开始
	 */
	private volatile static AtomicInteger NOTICATION_ID = new AtomicInteger(1);
	/**
	 * 下载监听，DownloadListener#onStart 下载的时候触发，DownloadListener#result下载结束的时候触发
	 * 4.0.0 每一次下载都会触发这两个方法，4.0.0以下只有触发下载才会回调这两个方法。
	 */
	private DownloadListener mDownloadListener;
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
	 * 当前下载链接
	 */
	private String mUrl;
	/**
	 * mContentDisposition ，提取文件名 ，如果ContentDisposition不指定文件名，则从url中提取文件名
	 */
	private String mContentDisposition;
	/**
	 * 文件大小
	 */
	private long mContentLength;
	/**
	 * 文件类型
	 */
	private String mMimetype;
	/**
	 * AbsAgentWebUIController
	 */
	private WeakReference<AbsAgentWebUIController> mAgentWebUIController;
	/**
	 * ExtraServiceImpl
	 */
	private ExtraServiceImpl mExtraServiceImpl;
	/**
	 * UA
	 */
	private String mUserAgent;
	/**
	 * ExtraServiceImpl
	 */
	private ExtraServiceImpl mCloneExtraServiceImpl = null;
	/**
	 * 进度回调
	 */
	private volatile DownloadingListener mDownloadingListener;
	/**
	 * 根据p3c，预编译正则，提升性能。
	 */
	private Pattern mPattern = Pattern.compile(".*filename=(.*)");

	DefaultDownloadImpl(ExtraServiceImpl extraServiceImpl) {
		if (!extraServiceImpl.mIsCloneObject) {
			this.bind(extraServiceImpl);
			this.mExtraServiceImpl = extraServiceImpl;
		} else {
			this.mCloneExtraServiceImpl = extraServiceImpl;
		}
	}

	private void bind(ExtraServiceImpl extraServiceImpl) {
		this.mActivityWeakReference = new WeakReference<Activity>(extraServiceImpl.mActivity);
		this.mContext = extraServiceImpl.mActivity.getApplicationContext();
		this.mDownloadListener = extraServiceImpl.mDownloadListener;
		this.mDownloadingListener = extraServiceImpl.mDownloadingListener;
		this.mPermissionListener = extraServiceImpl.mPermissionInterceptor;
		this.mAgentWebUIController = new WeakReference<AbsAgentWebUIController>(AgentWebUtils.getAgentWebUIControllerByWebView(extraServiceImpl.mWebView));
	}


	@Override
	public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
		onDownloadStartInternal(url, userAgent, contentDisposition, mimetype, contentLength, null);
	}


	private synchronized void onDownloadStartInternal(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, ExtraServiceImpl extraServiceImpl) {

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
				.setUrl(this.mUrl = url)
				.setMimetype(this.mMimetype = mimetype)
				.setContentDisposition(this.mContentDisposition = contentDisposition)
				.setContentLength(this.mContentLength = contentLength)
				.setUserAgent(this.mUserAgent = userAgent);
		this.mCloneExtraServiceImpl = mCloneExtraServiceImpl;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			List<String> mList = null;
			if ((mList = checkNeedPermission()).isEmpty()) {
				preDownload();
			} else {
				Action mAction = Action.createPermissionsAction(mList.toArray(new String[]{}));
				ActionActivity.setPermissionListener(getPermissionListener());
				ActionActivity.start(mActivityWeakReference.get(), mAction);
			}
		} else {
			preDownload();
		}
	}

	private ActionActivity.PermissionListener getPermissionListener() {
		return new ActionActivity.PermissionListener() {
			@Override
			public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras) {
				if (checkNeedPermission().isEmpty()) {
					preDownload();
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

	private void preDownload() {

		// true 表示用户取消了该下载事件。
		if (null != this.mDownloadListener
				&& this.mDownloadListener
				.onStart(this.mUrl,
						this.mUserAgent,
						this.mContentDisposition,
						this.mMimetype,
						this.mContentLength,
						this.mCloneExtraServiceImpl)) {
			return;
		}
		File mFile = getFile(mContentDisposition, mUrl);
		// File 创建文件失败
		if (null == mFile) {
			LogUtils.e(TAG, "新建文件失败");
			return;
		}
		if (mFile.exists() && mFile.length() >= mContentLength && mContentLength > 0) {

			// true 表示用户处理了下载完成后续的通知用户事件
			if (null != this.mDownloadListener && this.mDownloadListener.onResult(mFile.getAbsolutePath(), mUrl, null)) {
				return;
			}

			Intent mIntent = AgentWebUtils.getCommonFileIntentCompat(mContext, mFile);
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

		}


		// 该链接是否正在下载
		if (ExecuteTasksMap.getInstance().contains(mUrl)
				|| ExecuteTasksMap.getInstance().contains(mFile.getAbsolutePath())) {
			if (null != mAgentWebUIController.get()) {
				mAgentWebUIController.get().onShowMessage(
						mActivityWeakReference.get()
								.getString(R.string.agentweb_download_task_has_been_exist),
						TAG.concat("|preDownload"));
			}
			return;
		}


		// 移动数据
		if (!this.mCloneExtraServiceImpl.isForceDownload() &&
				AgentWebUtils.checkNetworkType(mContext) > 1) {

			showDialog(mFile);
			return;
		}
		performDownload(mFile);
	}

	private void forceDownload(final File file) {

		this.mCloneExtraServiceImpl.setForceDownload(true);
		performDownload(file);


	}

	private void showDialog(final File file) {

		Activity mActivity;
		if (null == (mActivity = mActivityWeakReference.get()) || mActivity.isFinishing()) {
			return;
		}
		AbsAgentWebUIController mAgentWebUIController;
		if (null != (mAgentWebUIController = this.mAgentWebUIController.get())) {
			mAgentWebUIController.onForceDownloadAlert(mUrl, createCallback(file));
		}

	}

	private Handler.Callback createCallback(final File file) {
		return new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				forceDownload(file);
				return true;
			}
		};
	}

	private void performDownload(File file) {

		try {

			ExecuteTasksMap.getInstance().addTask(mUrl, file.getAbsolutePath());
			if (null != mAgentWebUIController.get()) {
				mAgentWebUIController.get()
						.onShowMessage(mActivityWeakReference.get().getString(R.string.agentweb_coming_soon_download) + ":" + file.getName(), TAG.concat("|performDownload"));
			}
			DownloadTask mDownloadTask = new DownloadTask(NOTICATION_ID.incrementAndGet(),
					this.mDownloadListenerAdapter,
					mContext, file,
					this.mCloneExtraServiceImpl);
			new Downloader().download(mDownloadTask);
			this.mUrl = null;
			this.mContentDisposition = null;
			this.mContentLength = -1;
			this.mMimetype = null;
			this.mUserAgent = null;

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
			return AgentWebUtils.createFileByName(mContext, fileName, !this.mCloneExtraServiceImpl.isOpenBreakPointDownload());
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
		Matcher m = mPattern.matcher(contentDisposition.toLowerCase());
		if (m.find()) {
			return m.group(1);
		} else {
			return "";
		}
	}

	private DownloadListenerAdapter mDownloadListenerAdapter = new DownloadListenerAdapter() {
		@Override
		public void onProgress(String url, long downloaded, long length, long useTime) {
			if (null != mDownloadingListener) {
				synchronized (mDownloadingListener) {
					if (null != mDownloadingListener) {
						mDownloadingListener.onProgress(url, downloaded, length, useTime);
					}
				}
			}
		}

		@Override
		public void onBindService(String url, DownloadingService downloadingService) {
			if (null != mDownloadingListener) {
				synchronized (mDownloadingListener) {
					mDownloadingListener.onBindService(url, downloadingService);
				}
			}

		}

		@Override
		public void onUnbindService(String url, DownloadingService downloadingService) {
			if (null != mDownloadingListener) {
				synchronized (mDownloadingListener) {
					mDownloadingListener.onUnbindService(url, downloadingService);
				}
			}
		}

		@Override
		public boolean onResult(String path, String url, Throwable e) {
			ExecuteTasksMap.getInstance().removeTask(path);
			return null != mDownloadListener && mDownloadListener.onResult(path, url, e);
		}
	};


	/**
	 * 静态缓存当前正在下载的任务 mUrl
	 * i -> mUrl
	 * i+1 -> path
	 */
	static class ExecuteTasksMap extends ReentrantReadWriteLock {

		private LinkedList<String> mTasks = null;
		private static volatile ExecuteTasksMap sInstance = null;

		private ExecuteTasksMap() {
			super(false);
			mTasks = new LinkedList();
		}


		static ExecuteTasksMap getInstance() {

			if (null == sInstance) {
				synchronized (ExecuteTasksMap.class) {
					if (null == sInstance) {
						sInstance = new ExecuteTasksMap();
					}
				}
			}
			return sInstance;
		}

		void removeTask(String path) {

			try {
				writeLock().lock();
				int position = -1;
				if ((position = mTasks.indexOf(path)) == -1) {
					return;
				}
				mTasks.remove(position);
				mTasks.remove(position - 1);
			} finally {
				writeLock().unlock();
			}

		}

		void addTask(String url, String path) {

			try {
				writeLock().lock();
				mTasks.add(url);
				mTasks.add(path);
			} finally {
				writeLock().unlock();
			}
		}

		// 加锁读
		boolean contains(String url) {

			try {
				readLock().lock();
				return mTasks.contains(url);
			} finally {
				readLock().unlock();
			}

		}
	}


	public static DefaultDownloadImpl create(@NonNull Activity activity,
	                                         @NonNull WebView webView,
	                                         @Nullable DownloadListener downloadListener,
	                                         @NonNull DownloadingListener downloadingListener,
	                                         @Nullable PermissionInterceptor permissionInterceptor) {
		return new ExtraServiceImpl()
				.setActivity(activity)
				.setWebView(webView)
				.setDownloadListener(downloadListener)
				.setPermissionInterceptor(permissionInterceptor)
				.setDownloadingListener(downloadingListener)
				.create();
	}

	public static class ExtraServiceImpl extends AgentWebDownloader.ExtraService implements Cloneable, Serializable {
		private transient Activity mActivity;
		private transient DownloadListener mDownloadListener;
		private transient PermissionInterceptor mPermissionInterceptor;
		private transient WebView mWebView;
		private DefaultDownloadImpl mDefaultDownload;
		protected String mUrl;
		protected String mUserAgent;
		protected String mContentDisposition;
		protected String mMimetype;
		protected long mContentLength;
		private boolean mIsCloneObject = false;
		private transient DownloadingListener mDownloadingListener;

		public ExtraServiceImpl setDownloadingListener(DownloadingListener downloadingListener) {
			this.mDownloadingListener = downloadingListener;
			return this;
		}


//        public static final int PENDDING = 1001;
//        public static final int DOWNLOADING = 1002;
//        public static final int FINISH = 1003;
//        public static final int ERROR = 1004;
//        private AtomicInteger state = new AtomicInteger(PENDDING);

		@Override
		public String getUrl() {
			return mUrl;
		}

		@Override
		protected ExtraServiceImpl setUrl(String url) {
			this.mUrl = url;
			return this;
		}

		@Override
		public String getUserAgent() {
			return mUserAgent;
		}

		@Override
		protected ExtraServiceImpl setUserAgent(String userAgent) {
			this.mUserAgent = userAgent;
			return this;
		}


		@Override
		public String getContentDisposition() {
			return mContentDisposition;
		}

		@Override
		protected ExtraServiceImpl setContentDisposition(String contentDisposition) {
			this.mContentDisposition = contentDisposition;
			return this;
		}


		@Override
		public String getMimetype() {
			return mMimetype;
		}

		@Override
		protected ExtraServiceImpl setMimetype(String mimetype) {
			this.mMimetype = mimetype;
			return this;
		}

		@Override
		public long getContentLength() {
			return mContentLength;
		}

		@Override
		protected ExtraServiceImpl setContentLength(long contentLength) {
			this.mContentLength = contentLength;
			return this;
		}

		ExtraServiceImpl setActivity(Activity activity) {
			mActivity = activity;
			return this;
		}

		ExtraServiceImpl setDownloadListener(DownloadListener downloadListeners) {
			this.mDownloadListener = downloadListeners;
			return this;
		}

		ExtraServiceImpl setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
			mPermissionInterceptor = permissionInterceptor;
			return this;
		}

		ExtraServiceImpl setWebView(WebView webView) {
			this.mWebView = webView;
			return this;
		}


		@Override
		protected ExtraServiceImpl clone() throws CloneNotSupportedException {
			ExtraServiceImpl mExtraServiceImpl = (ExtraServiceImpl) super.clone();
			mExtraServiceImpl.mIsCloneObject = true;
			mExtraServiceImpl.mActivity = null;
			mExtraServiceImpl.mDownloadListener = null;
			mExtraServiceImpl.mPermissionInterceptor = null;
			mExtraServiceImpl.mWebView = null;
			return mExtraServiceImpl;
		}

		DefaultDownloadImpl create() {
			return this.mDefaultDownload = new DefaultDownloadImpl(this);
		}

		@Override
		public synchronized void performReDownload() {

			LogUtils.i(TAG, "performReDownload:" + mDefaultDownload);
			if (null != this.mDefaultDownload) {
				this.mDefaultDownload
						.onDownloadStartInternal(
								getUrl(),
								getUserAgent(),
								getContentDisposition(),
								getMimetype(),
								getContentLength(), this);
			}
		}

	}


}
