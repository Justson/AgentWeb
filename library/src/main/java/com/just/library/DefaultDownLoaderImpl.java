package com.just.library;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.webkit.DownloadListener;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cenxiaozhong on 2017/5/13.
 * source CODE  https://github.com/Justson/AgentWeb
 */

public class DefaultDownLoaderImpl implements DownloadListener ,DownLoadResultListener{

    private Context mContext;
    private boolean isForce;
    private boolean enableIndicator;

    private static int NoticationID = 1;

    private static ArrayMap<String,String> mTaskMap=new ArrayMap<>();
    private List<DownLoadResultListener>mDownLoadResultListeners;

    private LinkedList<String> mList=new LinkedList<>();

    public DefaultDownLoaderImpl(Context context, boolean isforce, boolean enableIndicator, List<DownLoadResultListener>downLoadResultListeners) {
        this.mContext = context;
        this.isForce = isforce;
        this.enableIndicator = enableIndicator;
        this.mDownLoadResultListeners=downLoadResultListeners;
    }


    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        LogUtils.i("Info", "url:" + url + "  package:"+mContext.getPackageName()+"  useraget:" + userAgent + " content:" + contentDisposition + "  mine:" + mimetype + "  c:" + contentLength);

        File mFile = getFile(contentDisposition, url);
        if (mFile != null && mFile.exists() && mFile.length() >= contentLength) {

            Intent mIntent=AgentWebUtils.getIntentCompat(mContext,mFile);
            if (mIntent != null)
                mContext.startActivity(mIntent);
            return;
        }

        if(mList.contains(url)){

            AgentWebUtils.toastShowShort(mContext,"该任务已经存在 ， 请勿重复点击下载!");
            return;
        }

        if (mFile != null){
            mList.add(url);
            mList.add(mFile.getAbsolutePath());
            mDownLoadResultListeners.add(this);
            //默认串行下载.
            new RealDownLoader(new DownLoadTask(NoticationID++, url,mDownLoadResultListeners, isForce, enableIndicator, mContext, mFile, contentLength, R.mipmap.download)).execute();
        }
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

    }



    @Override
    public void error(String path,String resUrl,String cause, Throwable e) {

        removeTask(path);
    }

    private synchronized void removeTask(String path) {
        if(AgentWebUtils.isEmptyCollection(mList))
            return;

        int index=mList.indexOf(path);
        LogUtils.i("Info","index:"+index+"paths:"+mList+"   path:"+path);
        mList.remove(index);
        mList.remove(index-1);
    }
}
