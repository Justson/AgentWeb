package com.just.library;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.DownloadListener;
import android.widget.Toast;

import java.io.File;

/**
 * Created by cenxiaozhong on 2017/5/13.
 */

public class DefaultDownLoaderImpl implements DownloadListener {

    private Context mContext;
    private boolean isForce;
    private boolean enableIndicator;

    private static   int NoticationID=1;
    public DefaultDownLoaderImpl(Context context, boolean isforce, boolean enableIndicator) {
        this.mContext = context;
        this.isForce = isforce;
        this.enableIndicator = enableIndicator;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        Log.i("Info", "url:" + url + " useraget:" + userAgent + " content:" + contentDisposition + "  mine:" + mimetype + "  c:" + contentLength);

        File mFile=getFile(contentDisposition, url);
        if(mFile.exists()&&mFile.length()>=contentLength){
            Toast.makeText(mContext,"该文件已经存在", Toast.LENGTH_SHORT).show();
            mFile.delete();
            mFile=getFile(contentDisposition,url);
            /*Intent mIntent=AgentWebUtils.getFileIntent(mFile);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(mIntent);*/
//            return;
        }
        new RealDownLoader(new DownLoadTask(NoticationID++, url, isForce, enableIndicator, mContext, mFile, contentLength,R.drawable.download)).execute();
    }

    private File getFile(String contentDisposition, String url) {

        try {
            String filename = "";
            if (!TextUtils.isEmpty(contentDisposition) && contentDisposition.contains("filename")&&contentDisposition.endsWith("filename")) {

                int position = contentDisposition.indexOf("filename");
                filename = contentDisposition.substring(position + 1);
            }
            if (TextUtils.isEmpty(filename) && !TextUtils.isEmpty(url)&&!url.endsWith("/")) {

                int p = url.lastIndexOf("/");
                if (p != -1)
                    filename = url.substring(p + 1);
            }

            if (TextUtils.isEmpty(filename)) {

                filename = System.currentTimeMillis() + "";
            }

            Log.i("Info", "file:" + filename);
            File mFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
            if (!mFile.exists())
                mFile.createNewFile();
            return mFile;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
