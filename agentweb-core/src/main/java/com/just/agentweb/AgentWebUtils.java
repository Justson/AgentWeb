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

package com.just.agentweb;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.os.EnvironmentCompat;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.just.agentweb.AgentWebConfig.AGENTWEB_FILE_PATH;
import static com.just.agentweb.AgentWebConfig.FILE_CACHE_PATH;


/**
 * @author cenxiaozhong
 * @since 1.0.0
 */
public class AgentWebUtils {

	private static final String TAG = AgentWebUtils.class.getSimpleName();
	private static Handler mHandler = null;

	private AgentWebUtils() {
		throw new UnsupportedOperationException("u can't init me");
	}

	public static int dp2px(Context context, float dipValue) {

		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}


	static final void clearWebView(WebView m) {

		if (m == null) {
			return;
		}
		if (Looper.myLooper() != Looper.getMainLooper()) {
			return;
		}
		m.loadUrl("about:blank");
		m.stopLoading();
		if (m.getHandler() != null) {
			m.getHandler().removeCallbacksAndMessages(null);
		}
		m.removeAllViews();
		ViewGroup mViewGroup = null;
		if ((mViewGroup = ((ViewGroup) m.getParent())) != null) {
			mViewGroup.removeView(m);
		}
		m.setWebChromeClient(null);
		m.setWebViewClient(null);
		m.setTag(null);
		m.clearHistory();
		m.destroy();
		m = null;


	}

	static String getAgentWebFilePath(Context context) {
		if (!TextUtils.isEmpty(AGENTWEB_FILE_PATH)) {
			return AGENTWEB_FILE_PATH;
		}
		String dir = getDiskExternalCacheDir(context);
		File mFile = new File(dir, FILE_CACHE_PATH);
		try {
			if (!mFile.exists()) {
				mFile.mkdirs();
			}
		} catch (Throwable throwable) {
			LogUtils.i(TAG, "create dir exception");
		}
		LogUtils.i(TAG, "path:" + mFile.getAbsolutePath() + "  path:" + mFile.getPath());
		return AGENTWEB_FILE_PATH = mFile.getAbsolutePath();

	}


	public static File createFileByName(Context context, String name, boolean cover) throws IOException {

		String path = getAgentWebFilePath(context);
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		File mFile = new File(path, name);
		if (mFile.exists()) {
			if (cover) {
				mFile.delete();
				mFile.createNewFile();
			}
		} else {
			mFile.createNewFile();
		}

		return mFile;
	}

	public static int checkNetworkType(Context context) {

		int netType = 0;
		//连接管理对象
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		//获取NetworkInfo对象
		@SuppressLint("MissingPermission") NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		switch (networkInfo.getType()) {
			case ConnectivityManager.TYPE_WIFI:
			case ConnectivityManager.TYPE_WIMAX:
			case ConnectivityManager.TYPE_ETHERNET:
				return 1;

			case ConnectivityManager.TYPE_MOBILE:
				switch (networkInfo.getSubtype()) {
					case TelephonyManager.NETWORK_TYPE_LTE:  // 4G
					case TelephonyManager.NETWORK_TYPE_HSPAP:
					case TelephonyManager.NETWORK_TYPE_EHRPD:
						return 2;
					case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
					case TelephonyManager.NETWORK_TYPE_CDMA:
					case TelephonyManager.NETWORK_TYPE_EVDO_0:
					case TelephonyManager.NETWORK_TYPE_EVDO_A:
					case TelephonyManager.NETWORK_TYPE_EVDO_B:
						return 3;

					case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
					case TelephonyManager.NETWORK_TYPE_EDGE:
						return 4;

					default:
						return netType;
				}

			default:

				return netType;
		}

	}

	public static long getAvailableStorage() {
		try {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory().toString());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
			} else {
				return (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
			}
		} catch (RuntimeException ex) {
			return 0;
		}
	}



	static Uri getUriFromFile(Context context, File file) {
		Uri uri = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			uri = getUriFromFileForN(context, file);
		} else {
			uri = Uri.fromFile(file);
		}
		return uri;
	}

	static Uri getUriFromFileForN(Context context, File file) {
		Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".AgentWebFileProvider", file);
		return fileUri;
	}


	static void setIntentDataAndType(Context context,
	                                 Intent intent,
	                                 String type,
	                                 File file,
	                                 boolean writeAble) {
		if (Build.VERSION.SDK_INT >= 24) {
			intent.setDataAndType(getUriFromFile(context, file), type);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			if (writeAble) {
				intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
			}
		} else {
			intent.setDataAndType(Uri.fromFile(file), type);
		}
	}


	static void setIntentData(Context context,
	                          Intent intent,
	                          File file,
	                          boolean writeAble) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent.setData(getUriFromFile(context, file));
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			if (writeAble) {
				intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
			}
		} else {
			intent.setData(Uri.fromFile(file));
		}
	}

	static String getDiskExternalCacheDir(Context context) {

		File mFile = context.getExternalCacheDir();
		if (Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(mFile))) {
			return mFile.getAbsolutePath();
		}
		return null;
	}

	static void grantPermissions(Context context, Intent intent, Uri uri, boolean writeAble) {

		int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
		if (writeAble) {
			flag |= Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
		}
		intent.addFlags(flag);
		List<ResolveInfo> resInfoList = context.getPackageManager()
				.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo resolveInfo : resInfoList) {
			String packageName = resolveInfo.activityInfo.packageName;
			context.grantUriPermission(packageName, uri, flag);
		}
	}


	private static String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
	  /* 取得扩展名 */
		String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();

      /* 依扩展名的类型决定MimeType */
		if (end.equals("pdf")) {
			type = "application/pdf";//
		} else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") ||
				end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio/*";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video/*";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png") ||
				end.equals("jpeg") || end.equals("bmp")) {
			type = "image/*";
		} else if (end.equals("apk")) {
			type = "application/vnd.android.package-archive";
		} else if (end.equals("pptx") || end.equals("ppt")) {
			type = "application/vnd.ms-powerpoint";
		} else if (end.equals("docx") || end.equals("doc")) {
			type = "application/vnd.ms-word";
		} else if (end.equals("xlsx") || end.equals("xls")) {
			type = "application/vnd.ms-excel";
		} else {
			type = "*/*";
		}
		return type;
	}


	private static WeakReference<Snackbar> snackbarWeakReference;

	static void show(View parent,
	                 CharSequence text,
	                 int duration,
	                 @ColorInt int textColor,
	                 @ColorInt int bgColor,
	                 CharSequence actionText,
	                 @ColorInt int actionTextColor,
	                 View.OnClickListener listener) {
		SpannableString spannableString = new SpannableString(text);
		ForegroundColorSpan colorSpan = new ForegroundColorSpan(textColor);
		spannableString.setSpan(colorSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		snackbarWeakReference = new WeakReference<>(Snackbar.make(parent, spannableString, duration));
		Snackbar snackbar = snackbarWeakReference.get();
		View view = snackbar.getView();
		view.setBackgroundColor(bgColor);
		if (actionText != null && actionText.length() > 0 && listener != null) {
			snackbar.setActionTextColor(actionTextColor);
			snackbar.setAction(actionText, listener);
		}
		snackbar.show();

	}

	static void dismiss() {
		if (snackbarWeakReference != null && snackbarWeakReference.get() != null) {
			snackbarWeakReference.get().dismiss();
			snackbarWeakReference = null;
		}
	}

	public static boolean checkWifi(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		}
		@SuppressLint("MissingPermission") NetworkInfo info = connectivity.getActiveNetworkInfo();
		return info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI;
	}

	public static boolean checkNetwork(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		}
		@SuppressLint("MissingPermission") NetworkInfo info = connectivity.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}

	static boolean isOverriedMethod(Object currentObject, String methodName, String method, Class... clazzs) {
		LogUtils.i(TAG, "  methodName:" + methodName + "   method:" + method);
		boolean tag = false;
		if (currentObject == null) {
			return tag;
		}
		try {
			Class clazz = currentObject.getClass();
			Method mMethod = clazz.getMethod(methodName, clazzs);
			String gStr = mMethod.toGenericString();
			tag = !gStr.contains(method);
		} catch (Exception igonre) {
			if (LogUtils.isDebug()) {
				igonre.printStackTrace();
			}
		}

		LogUtils.i(TAG, "isOverriedMethod:" + tag);
		return tag;
	}

	static Method isExistMethod(Object o, String methodName, Class... clazzs) {

		if (null == o) {
			return null;
		}
		try {
			Class clazz = o.getClass();
			Method mMethod = clazz.getDeclaredMethod(methodName, clazzs);
			mMethod.setAccessible(true);
			return mMethod;
		} catch (Throwable ignore) {
			if (LogUtils.isDebug()) {
				ignore.printStackTrace();
			}
		}
		return null;

	}

	static void clearAgentWebCache(Context context) {
		try {
			clearCacheFolder(new File(getAgentWebFilePath(context)), 0);
		} catch (Throwable throwable) {
			if (LogUtils.isDebug()) {
				throwable.printStackTrace();
			}
		}
	}

	static void clearWebViewAllCache(Context context, WebView webView) {

		try {

			AgentWebConfig.removeAllCookies(null);
			webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			context.deleteDatabase("webviewCache.db");
			context.deleteDatabase("webview.db");
			webView.clearCache(true);
			webView.clearHistory();
			webView.clearFormData();
			clearCacheFolder(new File(AgentWebConfig.getCachePath(context)), 0);

		} catch (Exception ignore) {
			//ignore.printStackTrace();
			if (AgentWebConfig.DEBUG) {
				ignore.printStackTrace();
			}
		}
	}


	static void clearWebViewAllCache(Context context) {

		try {

			clearWebViewAllCache(context, new WebView(context.getApplicationContext()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static int clearCacheFolder(final File dir, final int numDays) {

		int deletedFiles = 0;
		if (dir != null) {
			Log.i("Info", "dir:" + dir.getAbsolutePath());
		}
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {

					//first delete subdirectories recursively
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, numDays);
					}

					//then delete the files and subdirectories in this dir
					//only empty directories can be deleted, so subdirs have been done first
					if (child.lastModified() < new Date().getTime() - numDays * DateUtils.DAY_IN_MILLIS) {
						Log.i(TAG, "file name:" + child.getName());
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				Log.e("Info", String.format("Failed to clean the cache, result %s", e.getMessage()));
			}
		}
		return deletedFiles;
	}


	static void clearCache(final Context context, final int numDays) {
		Log.i("Info", String.format("Starting cache prune, deleting files older than %d days", numDays));
		int numDeletedFiles = clearCacheFolder(context.getCacheDir(), numDays);
		Log.i("Info", String.format("Cache pruning completed, %d files deleted", numDeletedFiles));
	}


	public static String[] uriToPath(Activity activity, Uri[] uris) {

		if (activity == null || uris == null || uris.length == 0) {
			return null;
		}
		try {
			String[] paths = new String[uris.length];
			int i = 0;
			for (Uri mUri : uris) {
				paths[i++] = Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2 ? getFileAbsolutePath(activity, mUri) : getRealPathBelowVersion(activity, mUri);

			}
			return paths;
		} catch (Throwable throwable) {
			if (LogUtils.isDebug()) {
				throwable.printStackTrace();
			}
		}
		return null;

	}

	private static String getRealPathBelowVersion(Context context, Uri uri) {
		String filePath = null;
		LogUtils.i(TAG, "method -> getRealPathBelowVersion " + uri + "   path:" + uri.getPath() + "    getAuthority:" + uri.getAuthority());
		String[] projection = {MediaStore.Images.Media.DATA};

		CursorLoader loader = new CursorLoader(context, uri, projection, null,
				null, null);
		Cursor cursor = loader.loadInBackground();

		if (cursor != null) {
			cursor.moveToFirst();
			filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
			cursor.close();
		}
		if (filePath == null) {
			filePath = uri.getPath();

		}
		return filePath;
	}


	static File createImageFile(Context context) {
		File mFile = null;
		try {

			String timeStamp =
					new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
			String imageName = String.format("aw_%s.jpg", timeStamp);
			mFile = createFileByName(context, imageName, true);
		} catch (Throwable e) {

		}
		return mFile;
	}


	public static void closeIO(Closeable closeable) {
		try {

			if (closeable != null) {
				closeable.close();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}


	@TargetApi(19)
	static String getFileAbsolutePath(Activity context, Uri fileUri) {

		if (context == null || fileUri == null) {
			return null;
		}

		LogUtils.i(TAG, "getAuthority:" + fileUri.getAuthority() + "  getHost:" + fileUri.getHost() + "   getPath:" + fileUri.getPath() + "  getScheme:" + fileUri.getScheme() + "  query:" + fileUri.getQuery());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, fileUri)) {
			if (isExternalStorageDocument(fileUri)) {
				String docId = DocumentsContract.getDocumentId(fileUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if (isDownloadsDocument(fileUri)) {
				String id = DocumentsContract.getDocumentId(fileUri);
				Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(fileUri)) {
				String docId = DocumentsContract.getDocumentId(fileUri);
				String[] split = docId.split(":");
				String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[]{split[1]};
				return getDataColumn(context, contentUri, selection, selectionArgs);
			}else{

			}
		} // MediaStore (and general)
		else if (fileUri.getAuthority().equalsIgnoreCase(context.getPackageName() + ".AgentWebFileProvider")) {

			String path = fileUri.getPath();
			int index = path.lastIndexOf("/");
			return getAgentWebFilePath(context) + File.separator + path.substring(index + 1, path.length());
		} else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(fileUri)) {
				return fileUri.getLastPathSegment();
			}
			return getDataColumn(context, fileUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
			return fileUri.getPath();
		}
		return null;
	}

	static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String[] projection = {MediaStore.Images.Media.DATA};
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	static Intent getInstallApkIntentCompat(Context context, File file) {

		Intent mIntent = new Intent().setAction(Intent.ACTION_VIEW);
		setIntentDataAndType(context, mIntent, "application/vnd.android.package-archive", file, false);
		return mIntent;
	}

	public static Intent getCommonFileIntentCompat(Context context, File file) {
		Intent mIntent = new Intent().setAction(Intent.ACTION_VIEW);
		setIntentDataAndType(context, mIntent, getMIMEType(file), file, false);
		return mIntent;
	}

	static Intent getIntentCaptureCompat(Context context, File file) {
		Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri mUri = getUriFromFile(context, file);
		mIntent.addCategory(Intent.CATEGORY_DEFAULT);
		mIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
		return mIntent;
	}


	static boolean isJson(String target) {
		if (TextUtils.isEmpty(target)) {
			return false;
		}

		boolean tag = false;
		try {
			if (target.startsWith("[")) {
				new JSONArray(target);
			} else {
				new JSONObject(target);
			}
			tag = true;
		} catch (JSONException ignore) {
//            ignore.printStackTrace();
			tag = false;
		}

		return tag;

	}


	public static boolean isUIThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}

	static boolean isEmptyCollection(Collection collection) {

		return collection == null || collection.isEmpty();
	}

	static boolean isEmptyMap(Map map) {

		return map == null || map.isEmpty();
	}

	private static Toast mToast = null;

	static void toastShowShort(Context context, String msg) {

		if (mToast == null) {
			mToast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(msg);
		}
		mToast.show();

	}

	@Deprecated
	static void getUIControllerAndShowMessage(Activity activity, String message, String from) {

		if (activity == null || activity.isFinishing()) {
			return;
		}
		WebParentLayout mWebParentLayout = (WebParentLayout) activity.findViewById(R.id.web_parent_layout_id);
		AbsAgentWebUIController mAgentWebUIController = mWebParentLayout.provide();
		if (mAgentWebUIController != null) {
			mAgentWebUIController.onShowMessage(message, from);
		}
	}

	public static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {
		return hasPermission(context, Arrays.asList(permissions));
	}

	public static boolean hasPermission(@NonNull Context context, @NonNull List<String> permissions) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		for (String permission : permissions) {
			int result = ContextCompat.checkSelfPermission(context, permission);
			if (result == PackageManager.PERMISSION_DENIED) {
				return false;
			}

			String op = AppOpsManagerCompat.permissionToOp(permission);
			if (TextUtils.isEmpty(op)) {
				continue;
			}
			result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
			if (result != AppOpsManagerCompat.MODE_ALLOWED) {
				return false;
			}

		}
		return true;
	}

	public static List<String> getDeniedPermissions(Activity activity, String[] permissions) {

		if (permissions == null || permissions.length == 0) {
			return null;
		}
		List<String> deniedPermissions = new ArrayList<>();
		for (int i = 0; i < permissions.length; i++) {

			if (!hasPermission(activity, permissions[i])) {
				deniedPermissions.add(permissions[i]);
			}
		}
		return deniedPermissions;

	}


	public static AbsAgentWebUIController getAgentWebUIControllerByWebView(WebView webView) {
		WebParentLayout mWebParentLayout = getWebParentLayoutByWebView(webView);
		return mWebParentLayout.provide();
	}

	//获取应用的名称
	public static String getApplicationName(Context context) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = context.getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName =
				(String) packageManager.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	static WebParentLayout getWebParentLayoutByWebView(WebView webView) {
		ViewGroup mViewGroup = null;
		if (!(webView.getParent() instanceof ViewGroup)) {
			throw new IllegalStateException("please check webcreator's create method was be called ?");
		}
		mViewGroup = (ViewGroup) webView.getParent();
		AbsAgentWebUIController mAgentWebUIController;
		while (mViewGroup != null) {

			LogUtils.i(TAG, "ViewGroup:" + mViewGroup);
			if (mViewGroup.getId() == R.id.web_parent_layout_id) {
				WebParentLayout mWebParentLayout = (WebParentLayout) mViewGroup;
				LogUtils.i(TAG, "found WebParentLayout");
				return mWebParentLayout;
			} else {
				ViewParent mViewParent = mViewGroup.getParent();
				if (mViewParent instanceof ViewGroup) {
					mViewGroup = (ViewGroup) mViewParent;
				} else {
					mViewGroup = null;
				}
			}
		}
		throw new IllegalStateException("please check webcreator's create method was be called ?");
	}

	public static void runInUiThread(Runnable runnable) {
		if (mHandler == null) {
			mHandler = new Handler(Looper.getMainLooper());
		}
		mHandler.post(runnable);
	}

	static boolean showFileChooserCompat(Activity activity,
	                                     WebView webView,
	                                     ValueCallback<Uri[]> valueCallbacks,
	                                     WebChromeClient.FileChooserParams fileChooserParams,
	                                     PermissionInterceptor permissionInterceptor,
	                                     ValueCallback valueCallback,
	                                     String mimeType,
	                                     Handler.Callback jsChannelCallback
	) {


		try {

			Class<?> clz = Class.forName("com.just.agentweb.filechooser.FileChooser");
			Object mFileChooser$Builder = clz.getDeclaredMethod("newBuilder",
					Activity.class, WebView.class)
					.invoke(null, activity, webView);
			clz = mFileChooser$Builder.getClass();
			Method mMethod = null;
			if (valueCallbacks != null) {
				mMethod = clz.getDeclaredMethod("setUriValueCallbacks", ValueCallback.class);
				mMethod.setAccessible(true);
				mMethod.invoke(mFileChooser$Builder, valueCallbacks);
			}

			if (fileChooserParams != null) {
				mMethod = clz.getDeclaredMethod("setFileChooserParams", WebChromeClient.FileChooserParams.class);
				mMethod.setAccessible(true);
				mMethod.invoke(mFileChooser$Builder, fileChooserParams);
			}

			if (valueCallback != null) {
				mMethod = clz.getDeclaredMethod("setUriValueCallback", ValueCallback.class);
				mMethod.setAccessible(true);
				mMethod.invoke(mFileChooser$Builder, valueCallback);
			}


			if (!TextUtils.isEmpty(mimeType)) {
//                LogUtils.i(TAG, Arrays.toString(clz.getDeclaredMethods()));
				mMethod = clz.getDeclaredMethod("setAcceptType", String.class);
				mMethod.setAccessible(true);
				mMethod.invoke(mFileChooser$Builder, mimeType);
			}

			if (jsChannelCallback != null) {
				mMethod = clz.getDeclaredMethod("setJsChannelCallback", Handler.Callback.class);
				mMethod.setAccessible(true);
				mMethod.invoke(mFileChooser$Builder, jsChannelCallback);
			}


			mMethod = clz.getDeclaredMethod("setPermissionInterceptor", PermissionInterceptor.class);
			mMethod.setAccessible(true);
			mMethod.invoke(mFileChooser$Builder, permissionInterceptor);

			mMethod = clz.getDeclaredMethod("build");
			mMethod.setAccessible(true);
			Object mFileChooser = mMethod.invoke(mFileChooser$Builder);

			mMethod = mFileChooser.getClass().getDeclaredMethod("openFileChooser");
			mMethod.setAccessible(true);
			mMethod.invoke(mFileChooser);

		} catch (Throwable throwable) {
			if (LogUtils.isDebug()) {
				throwable.printStackTrace();
			}
			if (valueCallbacks != null) {
				LogUtils.i(TAG, "onReceiveValue empty");
				return false;
			}
			if (valueCallback != null) {
				valueCallback.onReceiveValue(null);
			}
		}
		return true;
	}

	public static String md5(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			return new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {
			if (LogUtils.isDebug()) {
				e.printStackTrace();
			}
		}
		return "";
	}
}
