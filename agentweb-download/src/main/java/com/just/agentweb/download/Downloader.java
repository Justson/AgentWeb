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
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.SparseArray;

import com.just.agentweb.AgentWebUtils;
import com.just.agentweb.LogUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UnknownFormatConversionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author cenxiaozhong
 * @date 2017/5/13
 */
public class Downloader extends AsyncTask<Void, Integer, Integer> implements AgentWebDownloader<DownloadTask>, CancelDownloadRecipient {

	/**
	 * 下载参数
	 */
	private volatile DownloadTask mDownloadTask;
	/**
	 * 已经下载的大小
	 */
	private volatile long mLoaded = 0L;
	/**
	 * 总大小
	 */
	private long mTotals = -1L;
	/**
	 *
	 */
	private long mTmp = 0;
	/**
	 * 耗时
	 */
	private long mUsedTime = 0L;
	/**
	 * 上一次更新通知的时间
	 */
	private long mLastTime = 0L;
	/**
	 * 下载开始时间
	 */
	private volatile long mBeginTime = 0L;
	/**
	 * 当前下载平均速度
	 */
	private volatile long mAverageSpeed = 0;
	/**
	 * 下载异常，回调给用户的异常
	 */
	private volatile Throwable mThrowable;
	/**
	 * 下载最大时长
	 */
	private long mDownloadTimeOut = Long.MAX_VALUE;
	/**
	 * 连接超时
	 */
	private int mConnectTimeOut = 10000;
	/**
	 * 通知
	 */
	private DownloadNotifier mDownloadNotifier;

	private static final int ERROR_LOAD = 0x406;

	private static final String TAG = Downloader.class.getSimpleName();
	/**
	 * true 表示用户已经取消下载
	 */
	private AtomicBoolean mIsCanceled = new AtomicBoolean(false);
	/**
	 * true  表示终止下载
	 */
	private AtomicBoolean mIsShutdown = new AtomicBoolean(false);


	public static final int ERROR_NETWORK_CONNECTION = 0x400;
	public static final int ERROR_NETWORK_STATUS = 0x401;
	public static final int ERROR_STORAGE = 0x402;
	public static final int ERROR_SHUTDOWN = 0x405;
	public static final int ERROR_TIME_OUT = 0x403;
	public static final int ERROR_USER_CANCEL = 0x404;
	public static final int SUCCESSFUL = 0x200;

	private static final SparseArray<String> DOWNLOAD_MESSAGE = new SparseArray<>();

	static {

		DOWNLOAD_MESSAGE.append(ERROR_NETWORK_CONNECTION, "Network connection error . ");
		DOWNLOAD_MESSAGE.append(ERROR_NETWORK_STATUS, "Connection status code non-200 and non-206 .");
		DOWNLOAD_MESSAGE.append(ERROR_STORAGE, "Insufficient memory space . ");
		DOWNLOAD_MESSAGE.append(ERROR_SHUTDOWN, "Shutdown . ");
		DOWNLOAD_MESSAGE.append(ERROR_TIME_OUT, "Download time is overtime . ");
		DOWNLOAD_MESSAGE.append(ERROR_USER_CANCEL, "The user canceled the download .");
		DOWNLOAD_MESSAGE.append(ERROR_LOAD, "IO Error . ");
		DOWNLOAD_MESSAGE.append(SUCCESSFUL, "Download successful . ");
	}

	Downloader() {

	}

	private void checkNullTask(DownloadTask downloadTask) {

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (null != mDownloadTask.getDownloadListener()) {
			mDownloadTask.getDownloadListener().onBindService(mDownloadTask.getUrl(), this);
		}

		CancelDownloadInformer.getInformer().addRecipient(mDownloadTask.getUrl(), this);
		buildNotify(new Intent(), mDownloadTask.getId(),
				mDownloadTask.getContext().getString(R.string.agentweb_coming_soon_download));
	}

	private boolean checkSpace() {

		if (mDownloadTask.getLength() - mDownloadTask.getFile().length() > AgentWebUtils.getAvailableStorage()) {
			LogUtils.i(TAG, " 空间不足");
			return false;
		}
		return true;
	}

	private boolean checkNet() {
		if (!mDownloadTask.isForce()) {
			return AgentWebUtils.checkWifi(mDownloadTask.getContext());
		} else {
			return AgentWebUtils.checkNetwork(mDownloadTask.getContext());
		}
	}


	@Override
	protected Integer doInBackground(Void... params) {
		int result = ERROR_LOAD;
		try {
			this.mBeginTime = System.currentTimeMillis();
			if (!checkSpace()) {
				return ERROR_STORAGE;
			}
			if (!checkNet()) {
				return ERROR_NETWORK_CONNECTION;
			}
			result = doDownload();
		} catch (IOException e) {
			this.mThrowable = e;//发布
			if (LogUtils.isDebug()) {
				e.printStackTrace();
			}
		}

		return result;
	}

	private int doDownload() throws IOException {

		HttpURLConnection mHttpURLConnection = createUrlConnection(mDownloadTask.getUrl());
		if (mDownloadTask.getFile().length() > 0) {
			mHttpURLConnection.addRequestProperty("Range", "bytes=" + (mTmp = mDownloadTask.getFile().length()) + "-");
		}
		try {
			mHttpURLConnection.connect();
			boolean isSeek = false;
			int resCode = mHttpURLConnection.getResponseCode();
			if (resCode != 200 && resCode != 206) {
				return ERROR_NETWORK_STATUS;
			} else {
				isSeek = (resCode == 206);
			}

			LogUtils.i(TAG, "response code:" + mHttpURLConnection.getResponseCode());
			return doDownload(mHttpURLConnection.getInputStream(), new LoadingRandomAccessFile(mDownloadTask.getFile()), isSeek);
		} finally {
			if (mHttpURLConnection != null) {
				mHttpURLConnection.disconnect();
			}
		}

	}

	private HttpURLConnection createUrlConnection(String url) throws IOException {

		HttpURLConnection mHttpURLConnection = (HttpURLConnection) new URL(url).openConnection();
		mHttpURLConnection.setRequestProperty("Accept", "application/*");
		mHttpURLConnection.setConnectTimeout(mConnectTimeOut);
		LogUtils.i(TAG, "getDownloadTimeOut:" + mDownloadTask.getDownloadTimeOut());
		mHttpURLConnection.setReadTimeout(mDownloadTask.getBlockMaxTime());
		return mHttpURLConnection;
	}


	@Override
	protected synchronized void onProgressUpdate(Integer... values) {

		try {
			long currentTime = System.currentTimeMillis();
			this.mUsedTime = currentTime - this.mBeginTime;

			if (mUsedTime == 0) {
				this.mAverageSpeed = 0;
			} else {
				this.mAverageSpeed = mLoaded * 1000 / this.mUsedTime;
			}

			if (currentTime - this.mLastTime < 800) {
				return;
			}
			this.mLastTime = currentTime;
			if (null != mDownloadNotifier) {
				if (!mDownloadNotifier.hasDeleteContent()) {
					mDownloadNotifier.setDelecte(buildCancelContent(mDownloadTask.getContext().getApplicationContext(), mDownloadTask.getId()));
				}
				int mProgress = (int) ((mTmp + mLoaded) / Float.valueOf(mTotals) * 100);
				mDownloadNotifier.setContentText(
						mDownloadTask.getContext()
								.getString(R.string.agentweb_current_downloading_progress, (mProgress + "%"))
				);
				mDownloadNotifier.setProgress(100, mProgress, false);
			}
			if (mDownloadTask.getDownloadListener() != null) {
				mDownloadTask
						.getDownloadListener()
						.progress(mDownloadTask.getUrl(), (mTmp + mLoaded), mTotals, mUsedTime);
			}
		} catch (UnknownFormatConversionException e) {
			e.printStackTrace();
		}


	}

	@Override
	protected void onPostExecute(Integer integer) {

		try {
			LogUtils.i(TAG, "onPostExecute:" + integer);
			CancelDownloadInformer.getInformer().removeRecipient(mDownloadTask.getUrl());

			if (mDownloadTask.getDownloadListener() != null) {
				mDownloadTask
						.getDownloadListener()
						.progress(mDownloadTask.getUrl(), (mTmp + mLoaded), mTotals, mUsedTime);

			}

			if (mDownloadTask.getDownloadListener() != null) {
				mDownloadTask.getDownloadListener().onUnbindService(mDownloadTask.getUrl(), this);
			}
			boolean isCancelDispose = doCallback(integer);
			if (integer > 0x200) {

				if (mDownloadNotifier != null) {
					mDownloadNotifier.cancel(mDownloadTask.getId());
				}
				return;
			}
			if (mDownloadTask.isEnableIndicator()) {
				if (mDownloadNotifier != null) {
					mDownloadNotifier.cancel(mDownloadTask.getId());
				}
				if (isCancelDispose) {
					return;
				}
				Intent mIntent = AgentWebUtils.getCommonFileIntentCompat(mDownloadTask.getContext(), mDownloadTask.getFile());
				if (mIntent != null) {
					if (!(mDownloadTask.getContext() instanceof Activity)) {
						mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					}
					PendingIntent rightPendIntent = PendingIntent
							.getActivity(mDownloadTask.getContext(),
									mDownloadTask.getId() << 4, mIntent,
									PendingIntent.FLAG_UPDATE_CURRENT);
					mDownloadNotifier.setProgressFinish(mDownloadTask.getContext().getString(R.string.agentweb_click_open), rightPendIntent);
				}
				return;
			}
		} catch (Throwable throwable) {
			if (LogUtils.isDebug()) {
				throwable.printStackTrace();
			}
		} finally {
			if (mDownloadTask != null) {
				mDownloadTask.destroy();
			}
		}
	}

	private boolean doCallback(Integer code) {
		DownloadListener mDownloadListener = null;
		if ((mDownloadListener = mDownloadTask.getDownloadListener()) == null) {
			LogUtils.e(TAG, "DownloadListener has been death");
			DefaultDownloadImpl.ExecuteTasksMap.getInstance().removeTask(mDownloadTask.getFile().getPath());
			return false;
		}
		return mDownloadListener.result(mDownloadTask.getFile().getAbsolutePath(),
				mDownloadTask.getUrl(), code <= 200 ? null
						: this.mThrowable == null
						? new RuntimeException("Download failed ， cause:" + DOWNLOAD_MESSAGE.get(code)) : this.mThrowable);

	}


	private void buildNotify(Intent intent, int id, String progressHint) {

		Context mContext = mDownloadTask.getContext().getApplicationContext();
		if (mContext != null && mDownloadTask.isEnableIndicator()) {

			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent rightPendIntent = PendingIntent.getActivity(mContext,
					0x33 * id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			int smallIcon = mDownloadTask.getDrawableRes();
			String ticker = mContext.getString(R.string.agentweb_trickter);
			mDownloadNotifier = new DownloadNotifier(mContext, id);

			String title = TextUtils.isEmpty(mDownloadTask.getFile().getName()) ? mContext.getString(R.string.agentweb_file_download) : mDownloadTask.getFile().getName();

			if (title.length() > 20) {
				title = "..." + title.substring(title.length() - 20, title.length());
			}
			mDownloadNotifier.notifyProgress(rightPendIntent, smallIcon, ticker, title, progressHint, false, false, false, buildCancelContent(mContext, id));
			mDownloadNotifier.sent();
		}
	}


	private PendingIntent buildCancelContent(Context context, int id) {

		Intent intentCancel = new Intent(context, NotificationCancelReceiver.class);
		intentCancel.setAction("com.agentweb.cancelled");
		intentCancel.putExtra("type", "type");
		intentCancel.putExtra("TAG", mDownloadTask.getUrl());
		PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(context, id << 3, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);
		LogUtils.i(TAG, "id<<3:" + (id << 3));
		return pendingIntentCancel;
	}


	private int doDownload(InputStream inputStream, RandomAccessFile randomAccessFile, boolean isSeek) throws IOException {

		byte[] buffer = new byte[4 * 1024 * 10];
		try (BufferedInputStream bis = new BufferedInputStream(inputStream, 4 * 1024 * 10);
		     RandomAccessFile out = randomAccessFile) {

			if (isSeek) {
				LogUtils.i(TAG, "seek -- >" + isSeek + "  length:" + out.length());
				out.seek(out.length());
			} else {
				LogUtils.i(TAG, "seek -- >" + false + "  , length : 0");
				out.seek(0);
				mTmp = 0L;
			}
			int bytes = 0;

			while (!mIsCanceled.get() && !mIsShutdown.get()) {
				int n = bis.read(buffer, 0, 1024 * 10);
				if (n == -1) {
					break;
				}
				out.write(buffer, 0, n);
				bytes += n;

				if (!checkNet()) {
					return ERROR_NETWORK_CONNECTION;
				}

				if ((System.currentTimeMillis() - this.mBeginTime) > mDownloadTimeOut) {
					return ERROR_TIME_OUT;
				}

			}
			if (mIsCanceled.get()) {
				return ERROR_USER_CANCEL;
			}
			if (mIsShutdown.get()) {
				return ERROR_SHUTDOWN;
			}
			return SUCCESSFUL;
		}

	}

	private final void cancel() {
		mIsCanceled.set(true);
	}

	@Override
	public synchronized boolean isShutdown() {
		LogUtils.i(TAG, "" + mIsShutdown.get() + "  " + mIsCanceled.get() + "  :" + (getStatus() == Status.FINISHED));
		return mIsShutdown.get() || mIsCanceled.get() || (getStatus() == Status.FINISHED);
	}

	@Override
	public synchronized AgentWebDownloader.ExtraService shutdownNow() {

		if (getStatus() == Status.FINISHED) {
			LogUtils.e(TAG, "  Termination failed , becauce the downloader already dead !!! ");
			return null;
		}
		try {
			ExtraService mExtraService = mDownloadTask.getExtraServiceImpl();
			return mExtraService;
		} finally {
			mIsShutdown.set(true);
		}

	}

	@Override
	public void download(DownloadTask downloadTask) {
		downloadInternal(downloadTask);
	}

	private final void downloadInternal(DownloadTask downloadTask) {
		checkNullTask(downloadTask);
		this.mDownloadTask = downloadTask;
		this.mTotals = mDownloadTask.getLength();
		mDownloadTimeOut = mDownloadTask.getDownloadTimeOut();
		mConnectTimeOut = mDownloadTask.getConnectTimeOut();

		LogUtils.i(TAG, "mConnectTimeOut:" + mConnectTimeOut + " mDownloadTimeOut:" + mDownloadTimeOut);
		if (downloadTask.isParallelDownload()) {
			this.executeOnExecutor(ExecutorProvider.getInstance().provide(), (Void[]) null);
		} else {
			this.execute();
		}
	}

	@Override
	public void cancelDownload() {
		cancel();
	}

	private final class LoadingRandomAccessFile extends RandomAccessFile {

		public LoadingRandomAccessFile(File file) throws FileNotFoundException {
			super(file, "rw");
		}

		@Override
		public void write(byte[] buffer, int offset, int count) throws IOException {

			super.write(buffer, offset, count);
			mLoaded += count;
			publishProgress(0);

		}
	}

}
