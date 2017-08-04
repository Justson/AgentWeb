package com.just.library;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.webkit.DownloadListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cenxiaozhong on 2017/5/13.
 * source CODE  https://github.com/Justson/AgentWeb
 */

public class DefaultDownLoaderImpl implements DownloadListener, DownLoadResultListener {

    private Context mContext;
    private boolean isForce;
    private boolean enableIndicator;
    private volatile static int NoticationID = 1;
    private List<DownLoadResultListener> mDownLoadResultListeners;
    private LinkedList<String> mList = new LinkedList<>();
    private WeakReference<Activity> mActivityWeakReference = null;
    private DefaultMsgConfig.DownLoadMsgConfig mDownLoadMsgConfig=null;

    public DefaultDownLoaderImpl(Activity context, boolean isforce, boolean enableIndicator, List<DownLoadResultListener> downLoadResultListeners, DefaultMsgConfig.DownLoadMsgConfig msgConfig) {
        mActivityWeakReference = new WeakReference<Activity>(context);
        this.mContext = context.getApplicationContext();
        this.isForce = isforce;
        this.enableIndicator = enableIndicator;
        this.mDownLoadResultListeners = downLoadResultListeners;
        this.mDownLoadMsgConfig=msgConfig;
    }


    @Override
    public synchronized void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

//        LogUtils.i("Info", "  package:" + mContext.getPackageName() + "  userAgent:" + userAgent + " contentDisposition:" + contentDisposition + "  mine:" + mimetype + "  c:" + contentLength + "   url:" + url);


        File mFile = getFile(contentDisposition, url);
        if (mFile != null && mFile.exists() && mFile.length() >= contentLength) {

            Intent mIntent = AgentWebUtils.getIntentCompat(mContext, mFile);
            try {
                if (mIntent != null)
                    mContext.startActivity(mIntent);
                return;
            }catch (Throwable throwable){
                if(LogUtils.isDebug())
                    throwable.printStackTrace();
            }

        }

        if (mList.contains(url)) {

            AgentWebUtils.toastShowShort(mContext, mDownLoadMsgConfig.getTaskHasBeenExist());
            return;
        }

        if (mFile == null)
            return;

        if (AgentWebUtils.checkNetworkType(mContext) > 1) { //移动数据

            showDialog(url, contentLength, mFile);
            return;
        }

        performDownLoad(url, contentLength, mFile);


    }

    private void forceDown(final String url, final long contentLength, final File file) {

        isForce = true;
        performDownLoad(url, contentLength, file);


    }

    private void showDialog(final String url, final long contentLength, final File file) {

        Activity mActivity;
        if ((mActivity = mActivityWeakReference.get()) == null)
            return;

        AlertDialog mAlertDialog = null;
        mAlertDialog = new AlertDialog.Builder(mActivity)//
                .setTitle(mDownLoadMsgConfig.getTips())//
                .setMessage(mDownLoadMsgConfig.getHoneycomblow())//
                .setNegativeButton(mDownLoadMsgConfig.getDownLoad(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null)
                            dialog.dismiss();
                        forceDown(url, contentLength, file);
                    }
                })//
                .setPositiveButton(mDownLoadMsgConfig.getCancel(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (dialog != null)
                            dialog.dismiss();
                    }
                }).create();

        mAlertDialog.show();

    }

    private void performDownLoad(String url, long contentLength, File file) {

        mList.add(url);
        mList.add(file.getAbsolutePath());
        //并行下载.
        /*new RealDownLoader(new DownLoadTask(NoticationID++, url, this, isForce, enableIndicator, mContext, file, contentLength, R.mipmap.download)).executeOnExecutor(Executors.newCachedThreadPool(),(Void[])null);*/
        //默认串行下载.
        new RealDownLoader(new DownLoadTask(NoticationID++, url, this, isForce, enableIndicator, mContext, file, contentLength,mDownLoadMsgConfig, R.mipmap.download)).execute();
    }


    private File getFile(String contentDisposition, String url) {

        try {
            String filename = "";
            if (!TextUtils.isEmpty(contentDisposition) && contentDisposition.contains("filename") && !contentDisposition.endsWith("filename")) {

                int position = contentDisposition.indexOf("filename");
                filename = contentDisposition.substring(position + 1);
            }
            if (TextUtils.isEmpty(filename) && !TextUtils.isEmpty(url) && !url.endsWith("/")) {

                int p = url.lastIndexOf("/");
                if (p != -1)
                    filename = url.substring(p + 1);
                if (filename.contains("?")) {
                    int index = filename.indexOf("?");
                    filename = filename.substring(0, index);

                }
            }

            if (TextUtils.isEmpty(filename)) {

                filename = System.currentTimeMillis() + "";
            }

            LogUtils.i("Info", "file:" + filename);
            File mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + AgentWebConfig.DOWNLOAD_PATH, filename);
            if (!mFile.exists())
                mFile.createNewFile();
            return mFile;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void success(String path) {


        removeTask(path);

        if (AgentWebUtils.isEmptyCollection(mDownLoadResultListeners)) {
            return;
        }
        for (DownLoadResultListener mDownLoadResultListener : mDownLoadResultListeners) {
            mDownLoadResultListener.success(path);
        }
    }


    @Override
    public void error(String path, String resUrl, String cause, Throwable e) {

        removeTask(path);

        if (AgentWebUtils.isEmptyCollection(mDownLoadResultListeners)) {
            AgentWebUtils.toastShowShort(mContext, mDownLoadMsgConfig.getDownLoadFail());
            return;
        }

        for (DownLoadResultListener mDownLoadResultListener : mDownLoadResultListeners) {
            mDownLoadResultListener.error(path, resUrl, cause, e);
        }
    }

    private synchronized void removeTask(String path) {
        if (AgentWebUtils.isEmptyCollection(mList))
            return;

        int index = mList.indexOf(path);
        if (index == -1)
            return;
        //LogUtils.i("Info", "index:" + index + "paths:" + mList + "   path:" + path);
        mList.remove(index);
        mList.remove(index - 1);
    }
}
