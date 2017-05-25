package com.just.library;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.DownloadListener;

import java.io.File;

/**
 * Created by cenxiaozhong on 2017/5/13.
 * source code  https://github.com/Justson/AgentWeb
 */

public class DefaultDownLoaderImpl implements DownloadListener {

    private Context mContext;
    private boolean isForce;
    private boolean enableIndicator;

    private static int NoticationID = 1;

    public DefaultDownLoaderImpl(Context context, boolean isforce, boolean enableIndicator) {
        this.mContext = context;
        this.isForce = isforce;
        this.enableIndicator = enableIndicator;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        Log.i("Info", "url:" + url + " useraget:" + userAgent + " content:" + contentDisposition + "  mine:" + mimetype + "  c:" + contentLength);

        File mFile = getFile(contentDisposition, url);
        if (mFile != null && mFile.exists() && mFile.length() >= contentLength) {

            Intent mIntent = null;
            /*if (mContext.getApplicationInfo().targetSdkVersion > Build.VERSION_CODES.N) {

                mIntent=new Intent(Intent.ACTION_VIEW);
                mIntent.setDataAndType(FileProvider.getUriForFile(mContext,mContext.getPackageName(),mFile), "application/vnd.android.package-archive");
                mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                mIntent = AgentWebUtils.getFileIntent(mFile);
            }*/
            mIntent=AgentWebUtils.getFileIntent(mFile);
            if (mIntent != null)
                mContext.startActivity(mIntent);
            return;
        }
        if (mFile != null)
            new RealDownLoader(new DownLoadTask(NoticationID++, url, isForce, enableIndicator, mContext, mFile, contentLength, R.drawable.download)).execute();
    }

    private File getFile(String contentDisposition, String url) {

        try {
            String filename = "";
            if (!TextUtils.isEmpty(contentDisposition) && contentDisposition.contains("filename") && contentDisposition.endsWith("filename")) {

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

            Log.i("Info", "file:" + filename);
            File mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + AgentWebConfig.DOWNLOAD_PATH, filename);
            if (!mFile.exists())
                mFile.createNewFile();
            return mFile;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
