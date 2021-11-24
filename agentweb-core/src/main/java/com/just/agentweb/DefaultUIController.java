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

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;


/**
 * @author cenxiaozhong
 * @date 2017/12/8
 * @since 3.0.0
 */
public class DefaultUIController extends AbsAgentWebUIController {

	private AlertDialog mAlertDialog;
	protected AlertDialog mConfirmDialog;
	private JsPromptResult mJsPromptResult = null;
	private JsResult mJsResult = null;
	private AlertDialog mPromptDialog = null;
	private Activity mActivity;
	private WebParentLayout mWebParentLayout;
	private AlertDialog mAskOpenOtherAppDialog = null;
	private ProgressDialog mProgressDialog;
	private Resources mResources = null;

	@Override
	public void onJsAlert(WebView view, String url, String message) {
		AgentWebUtils.toastShowShort(view.getContext().getApplicationContext(), message);
	}

	@Override
	public void onOpenPagePrompt(WebView view, String url, final Handler.Callback callback) {
		LogUtils.i(TAG, "onOpenPagePrompt");
		Activity mActivity;
		if ((mActivity = this.mActivity) == null || mActivity.isFinishing()) {
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			if (mActivity.isDestroyed()) {
				return;
			}
		}
		if (mAskOpenOtherAppDialog == null) {
			mAskOpenOtherAppDialog = new AlertDialog
					.Builder(mActivity)
					.setMessage(mResources.getString(R.string.agentweb_leave_app_and_go_other_page,
							AgentWebUtils.getApplicationName(mActivity)))
					.setTitle(mResources.getString(R.string.agentweb_tips))
					.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (callback != null) {
								callback.handleMessage(Message.obtain(null, -1));
							}
						}
					})//
					.setPositiveButton(mResources.getString(R.string.agentweb_leave), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (callback != null) {
								callback.handleMessage(Message.obtain(null, 1));
							}
						}
					})
					.create();
		}
		mAskOpenOtherAppDialog.show();
	}

	@Override
	public void onJsConfirm(WebView view, String url, String message, JsResult jsResult) {
		onJsConfirmInternal(message, jsResult);
	}

	@Override
	public void onSelectItemsPrompt(WebView view, String url, final String[] ways, final Handler.Callback callback) {
		showChooserInternal(ways, callback);
	}

	@Override
	public void onForceDownloadAlert(String url, final Handler.Callback callback) {
		onForceDownloadAlertInternal(callback);
	}

	private void onForceDownloadAlertInternal(final Handler.Callback callback) {
		Activity mActivity;
		if ((mActivity = this.mActivity) == null || mActivity.isFinishing()) {
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			if (mActivity.isDestroyed()) {
				return;
			}
		}
		AlertDialog mAlertDialog = null;
		mAlertDialog = new AlertDialog.Builder(mActivity)
				.setTitle(mResources.getString(R.string.agentweb_tips))
				.setMessage(mResources.getString(R.string.agentweb_honeycomblow))
				.setNegativeButton(mResources.getString(R.string.agentweb_download), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (dialog != null) {
							dialog.dismiss();
						}
						if (callback != null) {
							callback.handleMessage(Message.obtain());
						}
					}
				})//
				.setPositiveButton(mResources.getString(R.string.agentweb_cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (dialog != null) {
							dialog.dismiss();
						}
					}
				}).create();
		mAlertDialog.show();
	}

	private void showChooserInternal(String[] ways, final Handler.Callback callback) {
		Activity mActivity;
		if ((mActivity = this.mActivity) == null || mActivity.isFinishing()) {
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			if (mActivity.isDestroyed()) {
				return;
			}
		}
		mAlertDialog = new AlertDialog.Builder(mActivity)
				.setSingleChoiceItems(ways, -1, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						LogUtils.i(TAG, "which:" + which);
						if (callback != null) {
							Message mMessage = Message.obtain();
							mMessage.what = which;
							callback.handleMessage(mMessage);
						}

					}
				}).setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						dialog.dismiss();
						if (callback != null) {
							callback.handleMessage(Message.obtain(null, -1));
						}
					}
				}).create();
		mAlertDialog.show();
	}

	private void onJsConfirmInternal(String message, JsResult jsResult) {
		LogUtils.i(TAG, "activity:" + mActivity.hashCode() + "  ");
		Activity mActivity = this.mActivity;
		if (mActivity == null || mActivity.isFinishing()) {
			toCancelJsresult(jsResult);
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			if (mActivity.isDestroyed()) {
				toCancelJsresult(jsResult);
				return;
			}
		}

		if (mConfirmDialog == null) {
			mConfirmDialog = new AlertDialog.Builder(mActivity)
					.setMessage(message)
					.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							toDismissDialog(mConfirmDialog);
							toCancelJsresult(mJsResult);
						}
					})//
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							toDismissDialog(mConfirmDialog);
							if (mJsResult != null) {
								mJsResult.confirm();
							}

						}
					})
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							dialog.dismiss();
							toCancelJsresult(mJsResult);
						}
					})
					.create();

		}
		mConfirmDialog.setMessage(message);
		this.mJsResult = jsResult;
		mConfirmDialog.show();
	}


	private void onJsPromptInternal(String message, String defaultValue, JsPromptResult jsPromptResult) {
		Activity mActivity = this.mActivity;
		if (mActivity == null || mActivity.isFinishing()) {
			jsPromptResult.cancel();
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			if (mActivity.isDestroyed()) {
				jsPromptResult.cancel();
				return;
			}
		}
		if (mPromptDialog == null) {
			final EditText et = new EditText(mActivity);
			et.setText(defaultValue);
			mPromptDialog = new AlertDialog.Builder(mActivity)
					.setView(et)
					.setTitle(message)
					.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							toDismissDialog(mPromptDialog);
							toCancelJsresult(mJsPromptResult);
						}
					})//
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							toDismissDialog(mPromptDialog);

							if (mJsPromptResult != null) {
								mJsPromptResult.confirm(et.getText().toString());
							}

						}
					})
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							dialog.dismiss();
							toCancelJsresult(mJsPromptResult);
						}
					})
					.create();
		}
		this.mJsPromptResult = jsPromptResult;
		mPromptDialog.show();
	}

	@Override
	public void onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult jsPromptResult) {
		onJsPromptInternal(message, defaultValue, jsPromptResult);
	}

	@Override
	public void onMainFrameError(WebView view, int errorCode, String description, String failingUrl) {

		LogUtils.i(TAG, "mWebParentLayout onMainFrameError:" + mWebParentLayout);
		if (mWebParentLayout != null) {
			mWebParentLayout.showPageMainFrameError();
		}
	}

	@Override
	public void onShowMainFrame() {
		if (mWebParentLayout != null) {
			mWebParentLayout.hideErrorLayout();
		}
	}

	@Override
	public void onLoading(String msg) {
		Activity mActivity;
		if ((mActivity = this.mActivity) == null || mActivity.isFinishing()) {
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			if (mActivity.isDestroyed()) {
				return;
			}
		}
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(mActivity);
		}
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setMessage(msg);
		mProgressDialog.show();

	}

	@Override
	public void onCancelLoading() {
		Activity mActivity;
		if ((mActivity = this.mActivity) == null || mActivity.isFinishing()) {
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			if (mActivity.isDestroyed()) {
				return;
			}
		}
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
		mProgressDialog = null;
	}

	@Override
	public void onShowMessage(String message, String from) {
		if (!TextUtils.isEmpty(from) && from.contains("performDownload")) {
			return;
		}
		AgentWebUtils.toastShowShort(mActivity.getApplicationContext(), message);
	}

	@Override
	public void onPermissionsDeny(String[] permissions, String permissionType, String action) {
//		AgentWebUtils.toastShowShort(mActivity.getApplicationContext(), "权限被冻结");
	}

	@Override
	public void onShowSslCertificateErrorDialog(final WebView view, final SslErrorHandler handler, final SslError error) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
		String sslErrorMessage;
		switch (error.getPrimaryError()) {
			case SslError.SSL_UNTRUSTED:
				sslErrorMessage = mActivity.getString(R.string.agentweb_message_show_ssl_untrusted);
				break;
			case SslError.SSL_EXPIRED:
				sslErrorMessage = mActivity.getString(R.string.agentweb_message_show_ssl_expired);
				break;
			case SslError.SSL_IDMISMATCH:
				sslErrorMessage = mActivity.getString(R.string.agentweb_message_show_ssl_hostname_mismatch);
				break;
			case SslError.SSL_NOTYETVALID:
				sslErrorMessage = mActivity.getString(R.string.agentweb_message_show_ssl_not_yet_valid);
				break;
			default:
				sslErrorMessage = mActivity.getString(R.string.agentweb_message_show_ssl_error);
		}
		sslErrorMessage += mActivity.getString(R.string.agentweb_message_show_continue);
		alertDialog.setTitle(mActivity.getString(R.string.agentweb_title_ssl_error));
		alertDialog.setMessage(sslErrorMessage);
		alertDialog.setPositiveButton(R.string.agentweb_continue, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Ignore SSL certificate errors
				handler.proceed();
			}
		});

		alertDialog.setNegativeButton(R.string.agentweb_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				handler.cancel();
			}
		});
		alertDialog.show();


	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	public void onPermissionRequest(final PermissionRequest request) {
		final String[] resources = request.getResources();
		Set<String> resourcesSet = new HashSet<>(Arrays.asList(resources));
		ArrayList<String> permissions = new ArrayList<>(resourcesSet.size());
		if (resourcesSet.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
			permissions.add(Manifest.permission.CAMERA);
		}
		if (resourcesSet.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
			permissions.add(Manifest.permission.RECORD_AUDIO);
		}
		if(permissions.isEmpty()){
			request.grant(resources);
			return;
		}

		final List<String> denyPermission = AgentWebUtils.getDeniedPermissions(mActivity, permissions.toArray(new String[]{}));
		if (denyPermission.isEmpty()) {
			request.grant(resources);
		} else {
			Action action = Action.createPermissionsAction(denyPermission.toArray(new String[]{}));
			action.setPermissionListener(new AgentActionFragment.PermissionListener() {
				@Override
				public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras) {
					List<String> deny = AgentWebUtils.getDeniedPermissions(mActivity, denyPermission.toArray(new String[]{}));
					if (deny.isEmpty()) {
						request.grant(resources);
					} else {
						request.deny();
					}

				}
			});
			AgentActionFragment.start(mActivity, action);
		}

	}

	private void toCancelJsresult(JsResult result) {
		if (result != null) {
			result.cancel();
		}
	}


	@Override
	protected void bindSupportWebParent(WebParentLayout webParentLayout, Activity activity) {
		this.mActivity = activity;
		this.mWebParentLayout = webParentLayout;
		mResources = this.mActivity.getResources();

	}
}
