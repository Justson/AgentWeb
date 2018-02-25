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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;
import android.widget.EditText;


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
		if (mAskOpenOtherAppDialog == null) {
			mAskOpenOtherAppDialog = new AlertDialog
					.Builder(mActivity)//
					.setMessage(mResources.getString(R.string.agentweb_leave_app_and_go_other_page,
							AgentWebUtils.getApplicationName(mActivity)))//
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
		mAlertDialog = new AlertDialog.Builder(mActivity)//
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

		if (mConfirmDialog == null) {
			mConfirmDialog = new AlertDialog.Builder(mActivity)//
					.setMessage(message)//
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
		if (mPromptDialog == null) {

			final EditText et = new EditText(mActivity);
			et.setText(defaultValue);
			mPromptDialog = new AlertDialog.Builder(mActivity)//
					.setView(et)//
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
