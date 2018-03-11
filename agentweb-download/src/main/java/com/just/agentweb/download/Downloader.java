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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;

import com.just.agentweb.AgentWebConfig;
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
import java.util.Map;
import java.util.UnknownFormatConversionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_PARTIAL;
import static java.net.HttpURLConnection.HTTP_SEE_OTHER;
import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

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
	private volatile long mTotals = -1L;
	/**
	 * 上一次下载，文件缓存长度
	 */
	private long mLastLoaded = 0L;
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
	private volatile long mAverageSpeed = 0L;
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


	private static final String TAG = Downloader.class.getSimpleName();
	/**
	 * true 表示用户已经取消下载
	 */
	private AtomicBoolean mIsCanceled = new AtomicBoolean(false);
	/**
	 * true  表示终止下载
	 */
	private AtomicBoolean mIsShutdown = new AtomicBoolean(false);
	/**
	 * Download read buffer size
	 */
	private static final int BUFFER_SIZE = 1024 * 8;
	/**
	 * 最多允许7次重定向
	 */
	private static final int MAX_REDIRECTS = 7;
	private static final int HTTP_TEMP_REDIRECT = 307;
	public static final int ERROR_NETWORK_CONNECTION = 0x400;
	public static final int ERROR_RESPONSE_STATUS = 0x401;
	public static final int ERROR_STORAGE = 0x402;
	public static final int ERROR_TIME_OUT = 0x403;
	public static final int ERROR_USER_CANCEL = 0x404;
	public static final int ERROR_SHUTDOWN = 0x405;
	public static final int ERROR_TOO_MANY_REDIRECTS = 0x406;
	public static final int ERROR_LOAD = 0x407;
	public static final int ERROR_SERVICE = 0x503;
	public static final int SUCCESSFUL = 0x200;

	private static final SparseArray<String> DOWNLOAD_MESSAGE = new SparseArray<>();

	static {

		DOWNLOAD_MESSAGE.append(ERROR_NETWORK_CONNECTION, "Network connection error . ");
		DOWNLOAD_MESSAGE.append(ERROR_RESPONSE_STATUS, "Response code non-200 or non-206 . ");
		DOWNLOAD_MESSAGE.append(ERROR_STORAGE, "Insufficient memory space . ");
		DOWNLOAD_MESSAGE.append(ERROR_SHUTDOWN, "Shutdown . ");
		DOWNLOAD_MESSAGE.append(ERROR_TIME_OUT, "Download time is overtime . ");
		DOWNLOAD_MESSAGE.append(ERROR_USER_CANCEL, "The user canceled the download . ");
		DOWNLOAD_MESSAGE.append(ERROR_LOAD, "IO Error . ");
		DOWNLOAD_MESSAGE.append(ERROR_SERVICE, "Service Unavailable . ");
		DOWNLOAD_MESSAGE.append(ERROR_TOO_MANY_REDIRECTS, "Too many redirects . ");
		DOWNLOAD_MESSAGE.append(SUCCESSFUL, "Download successful . ");
	}

	Downloader() {

	}

	private void checkIsNullTask(DownloadTask downloadTask) {
		//todo
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (null != mDownloadTask.getDownloadListener()) {
			mDownloadTask.getDownloadListener().onBindService(mDownloadTask.getUrl(), this);
		}

		CancelDownloadInformer.getInformer().addRecipient(mDownloadTask.getUrl(), this);
		createNotifier();
		if (null != this.mDownloadNotifier) {
			mDownloadNotifier.onPreDownload();
		}
	}

	private boolean checkSpace() {

		if (mDownloadTask.getLength() - mDownloadTask.getFile().length() > AgentWebUtils.getAvailableStorage()) {
			LogUtils.e(TAG, " 空间不足");
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
			this.mBeginTime = SystemClock.elapsedRealtime();
			if (!checkSpace()) {
				return ERROR_STORAGE;
			}
			if (!checkNet()) {
				return ERROR_NETWORK_CONNECTION;
			}
			result = doDownload();
		} catch (IOException e) {
			this.mThrowable = e;
			if (LogUtils.isDebug()) {
				e.printStackTrace();
			}
		}

		return result;
	}


	private int doDownload() throws IOException {

		int redirectionCount = 1;
		URL url = new URL(mDownloadTask.getUrl());
		HttpURLConnection mHttpURLConnection = null;
		try {

			for (; redirectionCount++ <= MAX_REDIRECTS; ) {
				if (null != mHttpURLConnection) {
					mHttpURLConnection.disconnect();
				}
				mHttpURLConnection = createUrlConnectionAndSettingHeaders(url);
				mHttpURLConnection.connect();

				final boolean isEncodingChunked = "chunked".equalsIgnoreCase(
						mHttpURLConnection.getHeaderField("Transfer-Encoding"));
				long tmpLength = -1;
				final boolean hasLength = ((tmpLength = getHeaderFieldLong(mHttpURLConnection, "Content-Length")) == -1);
				// 获取不到文件长度
				final boolean finishKnown = isEncodingChunked || hasLength;
				if (finishKnown) {
					LogUtils.e(TAG, "can't know size of download, giving up ,"
							+ "  EncodingChunked:" + isEncodingChunked
							+ "  hasLength:" + hasLength);
					return ERROR_LOAD;
				}

				int responseCode = mHttpURLConnection.getResponseCode();
				switch (responseCode) {

					case HTTP_OK:
						saveEtag(mHttpURLConnection);
						return transferData(mHttpURLConnection.getInputStream(),
								new LoadingRandomAccessFile(mDownloadTask.getFile()),
								false);
					case HTTP_PARTIAL:
						// 服务端响应文件长度不正确，或者本地文件长度被修改。
						if (tmpLength + mDownloadTask.getFile().length() != this.mTotals) {
							return ERROR_LOAD;
						}
						return transferData(mHttpURLConnection.getInputStream(),
								new LoadingRandomAccessFile(mDownloadTask.getFile()),
								true);
					case HTTP_MOVED_PERM:
					case HTTP_MOVED_TEMP:
					case HTTP_SEE_OTHER:
					case HTTP_TEMP_REDIRECT:
						final String location = mHttpURLConnection.getHeaderField("Location");
						url = new URL(url, location);
						continue;
					case HTTP_UNAVAILABLE:
					case HTTP_INTERNAL_ERROR:
						return ERROR_SERVICE;
					default:
						return ERROR_RESPONSE_STATUS;
				}
			}
			return ERROR_TOO_MANY_REDIRECTS;
		} finally {
			if (null != mHttpURLConnection) {
				mHttpURLConnection.disconnect();
			}
		}
	}

	private long getHeaderFieldLong(HttpURLConnection httpURLConnection, String name) {
		String field = httpURLConnection.getHeaderField(name);
		try {
			return null == field ? -1L : Long.parseLong(field);
		} catch (NumberFormatException e) {
			if (LogUtils.isDebug()) {
				e.printStackTrace();
			}
		}
		return -1L;
	}

	private void saveEtag(HttpURLConnection httpURLConnection) {
		String etag = httpURLConnection.getHeaderField("ETag");
		if (TextUtils.isEmpty(etag)) {
			return;
		}
		LogUtils.i(TAG, "save etag:" + etag);
		SharedPreferences mSharedPreferences = mDownloadTask.getContext().getSharedPreferences(AgentWebConfig.AGENTWEB_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(mDownloadTask.getFile().getName(), etag);
		editor.apply();
	}

	private String getEtag() {
		SharedPreferences mSharedPreferences = mDownloadTask.getContext().getSharedPreferences(AgentWebConfig.AGENTWEB_NAME, Context.MODE_PRIVATE);
		String mEtag = mSharedPreferences.getString(mDownloadTask.getFile().getName(), "-1");
		if (!TextUtils.isEmpty(mEtag) && !"-1".equals(mEtag)) {
			return mEtag;
		} else {
			return null;
		}
	}

	private HttpURLConnection createUrlConnectionAndSettingHeaders(URL url) throws IOException {

		HttpURLConnection mHttpURLConnection = (HttpURLConnection) url.openConnection();
		mHttpURLConnection.setConnectTimeout(mConnectTimeOut);
		mHttpURLConnection.setInstanceFollowRedirects(false);
		mHttpURLConnection.setReadTimeout(mDownloadTask.getBlockMaxTime());
		mHttpURLConnection.setRequestProperty("Accept", "application/*");
		mHttpURLConnection.setRequestProperty("Accept-Encoding", "identity");
		mHttpURLConnection.setRequestProperty("Connection", "close");
		mHttpURLConnection.setRequestProperty("Cookie", AgentWebConfig.getCookiesByUrl(url.toString()));
		Map<String, String> mHeaders = null;
		if (null != (mHeaders = mDownloadTask.getExtraServiceImpl().getHeaders()) &&
				!mHeaders.isEmpty()) {
			for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
				if (TextUtils.isEmpty(entry.getKey()) || TextUtils.isEmpty(entry.getValue())) {
					continue;
				}
				mHttpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}

		if (mDownloadTask.getFile().length() > 0) {
			String mEtag = "";
			if (!TextUtils.isEmpty((mEtag = getEtag()))) {
				LogUtils.i(TAG, "Etag:" + mEtag);
				mHttpURLConnection.setRequestProperty("If-Match", getEtag());
			}
			mHttpURLConnection.setRequestProperty("Range", "bytes=" + (mLastLoaded = mDownloadTask.getFile().length()) + "-");
		}

		return mHttpURLConnection;
	}


	@Override
	protected synchronized void onProgressUpdate(Integer... values) {

		try {
			long currentTime = SystemClock.elapsedRealtime();
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
				int mProgress = (int) ((mLastLoaded + mLoaded) / Float.valueOf(mTotals) * 100);
				mDownloadNotifier.onDownloading(mProgress);
			}
			if (null != mDownloadTask.getDownloadListener()) {
				mDownloadTask
						.getDownloadListener()
						.onProgress(mDownloadTask.getUrl(), (mLastLoaded + mLoaded), mTotals, mUsedTime);
			}
		} catch (UnknownFormatConversionException e) {
			e.printStackTrace();
		}


	}

	@Override
	protected void onPostExecute(Integer integer) {

		try {
			CancelDownloadInformer.getInformer().removeRecipient(mDownloadTask.getUrl());

			if (null != mDownloadTask.getDownloadListener()) {
				mDownloadTask
						.getDownloadListener()
						.onProgress(mDownloadTask.getUrl(), (mLastLoaded + mLoaded), mTotals, mUsedTime);

			}

			if (null != mDownloadTask.getDownloadListener()) {
				mDownloadTask.getDownloadListener().onUnbindService(mDownloadTask.getUrl(), this);
			}
			LogUtils.i(TAG, "msg:" + DOWNLOAD_MESSAGE.get(integer));
			boolean isCancelDispose = doCallback(integer);
			// Error
			if (integer > 0x200) {

				if (null != mDownloadNotifier) {
					mDownloadNotifier.cancel();
				}

				return;
			}
			if (mDownloadTask.isEnableIndicator()) {
				if (isCancelDispose) {
					mDownloadNotifier.cancel();
					return;
				}

				if (null != mDownloadNotifier) {
					mDownloadNotifier.onDownloadFinished();
				}
			}

			// auto open file
			if (!mDownloadTask.isAutoOpen()) {
				return;
			}
			Intent mIntent = AgentWebUtils.getCommonFileIntentCompat(mDownloadTask.getContext(), mDownloadTask.getFile());
			if (null == mIntent) {
				return;
			}
			if (!(mDownloadTask.getContext() instanceof Activity)) {
				mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			mDownloadTask.getContext().startActivity(mIntent);
		} catch (Throwable throwable) {
			if (LogUtils.isDebug()) {
				throwable.printStackTrace();
			}
		} finally {
			if (null != mDownloadTask) {
				mDownloadTask.destroy();
			}
		}
	}

	private boolean doCallback(Integer code) {
		DownloadListener mDownloadListener = null;
		if (null == (mDownloadListener = mDownloadTask.getDownloadListener())) {
			LogUtils.e(TAG, "DownloadListener has been death");
			DefaultDownloadImpl
					.ExecuteTasksMap.getInstance()
					.removeTask(mDownloadTask.getFile().getPath());
			return false;
		}
		return mDownloadListener.onResult(mDownloadTask.getFile().getAbsolutePath(),
				mDownloadTask.getUrl(), code <= 200 ? null
						: null == this.mThrowable
						? new RuntimeException("Download failed ， cause:" + DOWNLOAD_MESSAGE.get(code)) : this.mThrowable);

	}


	private void createNotifier() {

		Context mContext = mDownloadTask.getContext().getApplicationContext();
		if (null != mContext && mDownloadTask.isEnableIndicator()) {
			mDownloadNotifier = new DownloadNotifier(mContext, mDownloadTask.getId());
			mDownloadNotifier.initBuilder(mDownloadTask);
		}
	}


	private int transferData(InputStream inputStream, RandomAccessFile randomAccessFile, boolean isSeek) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		BufferedInputStream bis = new BufferedInputStream(inputStream, BUFFER_SIZE);
		RandomAccessFile out = randomAccessFile;
		try {

			if (isSeek) {
				out.seek(out.length());
			} else {
				out.seek(0);
				mLastLoaded = 0L;
			}
			int bytes = 0;

			while (!mIsCanceled.get() && !mIsShutdown.get()) {
				int n = bis.read(buffer, 0, BUFFER_SIZE);
				if (n == -1) {
					break;
				}
				out.write(buffer, 0, n);
				bytes += n;

				if ((SystemClock.elapsedRealtime() - this.mBeginTime) > mDownloadTimeOut) {
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
		} finally {
			AgentWebUtils.closeIO(out);
			AgentWebUtils.closeIO(bis);
			AgentWebUtils.closeIO(inputStream);
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
		checkIsNullTask(downloadTask);
		this.mDownloadTask = downloadTask;
		this.mTotals = mDownloadTask.getLength();
		mDownloadTimeOut = mDownloadTask.getDownloadTimeOut();
		mConnectTimeOut = mDownloadTask.getConnectTimeOut();

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
