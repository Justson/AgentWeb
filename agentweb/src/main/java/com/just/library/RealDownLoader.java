package com.just.library;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by cenxiaozhong on 2017/5/13.
 */

public class RealDownLoader extends AsyncTask<Void, Integer, Integer> {

    private DownLoadTask mDownLoadTask;
    private long loaded = 0;
    private long totals = -1;
    private long tmp = 0;
    private long begin = 0;
    private long used = 1;
    private long mTimeLast = 0;
    private long mSpeed = 0;

    private static final int TIME_OUT=30000000;
    private Notity mNotity;

    private static final int ERROR_LOAD=-5;

    RealDownLoader(DownLoadTask downLoadTask) {


        this.mDownLoadTask = downLoadTask;
        this.totals=mDownLoadTask.getLength();
        checkNullTask(downLoadTask);
    }
    private void checkNullTask(DownLoadTask downLoadTask){

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        buildNotify(new Intent(),mDownLoadTask.getId(),"正在下载中");
    }

    private boolean checkDownLoaderCondition() {

        if (!checknet())
            return false;

        if (mDownLoadTask.getLength() - mDownLoadTask.getFile().length() > AgentWebUtils.getAvailableStorage()) {
            return false;
        }

        return true;
    }

    private boolean checknet() {
        if (!mDownLoadTask.isForce()) {

            return AgentWebUtils.checkWifi(mDownLoadTask.getContext());
        }else{
            return AgentWebUtils.checkNetwork(mDownLoadTask.getContext());
        }


    }

    @Override
    protected Integer doInBackground(Void... params) {
        int result=ERROR_LOAD;
        try {
            begin=System.currentTimeMillis();
            if (!checkDownLoaderCondition())
                return ERROR_LOAD;
           result = doDownLoad();
        } catch (Exception e) {

            e.printStackTrace();
            return ERROR_LOAD;
        }

        return result;
    }

    private int doDownLoad() throws IOException {

        HttpURLConnection mHttpURLConnection = createUrlConnection(mDownLoadTask.getUrl());




        if (mDownLoadTask.getFile().length() > 0) {

            mHttpURLConnection.addRequestProperty("Range", "bytes=" + (tmp = mDownLoadTask.getFile().length()) + "-");
        }

        mHttpURLConnection.connect();
        if (mHttpURLConnection.getResponseCode() != 200&&mHttpURLConnection.getResponseCode()!=206) {

            return ERROR_LOAD;
        }


        return doDownLoad(mHttpURLConnection.getInputStream(),new LoadingRandomAccessFile(mDownLoadTask.getFile()));


    }

    private HttpURLConnection createUrlConnection(String url) throws IOException {


        HttpURLConnection mHttpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        mHttpURLConnection.setRequestProperty("Accept", "application/*");
        mHttpURLConnection.setConnectTimeout(5000);
        return mHttpURLConnection;
    }

    private long time=0;
    @Override
    protected void onProgressUpdate(Integer... values) {


       long current=System.currentTimeMillis();
        used=current-begin;


        Log.i("Info","progress:"+((tmp+loaded)/Float.valueOf(totals)*100)+ "tmp:"+tmp+"  load=:"+loaded+"  total:"+totals);

        long c=System.currentTimeMillis();
        if(mNotity!=null&&c-time>800){
            time=c;
            mNotity.setProgress(100, (int) ((tmp+loaded)/Float.valueOf(totals)*100),false);
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {

        if(integer==ERROR_LOAD){
            Toast.makeText(mDownLoadTask.getContext(),"下载失败出错了",Toast.LENGTH_SHORT).show();
            return;
        }

        if(mDownLoadTask.isEnableIndicator()){

            if(mNotity!=null)
                mNotity.cancel(mDownLoadTask.getId());

            Intent intent=AgentWebUtils.getFileIntent(mDownLoadTask.getFile());
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent rightPendIntent = PendingIntent.getActivity(mDownLoadTask.getContext(),
                    0x110, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mNotity.setProgressFinish("点击打开",rightPendIntent);


        }

    }

    private void buildNotify(Intent intent,int id,String progressHint) {
        Log.i("Info","progress:"+progressHint);
        if(mDownLoadTask.isEnableIndicator()){

            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent rightPendIntent = PendingIntent.getActivity(mDownLoadTask.getContext(),
                    0x33, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            int smallIcon = mDownLoadTask.getDrawableRes();
            String ticker = "您有一条新通知";
            mNotity = new Notity(mDownLoadTask.getContext(),id);
            mNotity.notify_progress(rightPendIntent, smallIcon, ticker, "文件下载", progressHint, false, false, false);
            mNotity.sent();
        }
    }

    private int doDownLoad(InputStream in, RandomAccessFile out) throws IOException {

        byte[] buffer = new byte[102400];
        BufferedInputStream bis = new BufferedInputStream(in, 102400);
        try {

            out.seek(out.length());

            int bytes = 0;
            long previousBlockTime = -1;

            while (!isCancelled()) {
                int n = bis.read(buffer, 0, 102400);
                if (n == -1) {
                    break;
                }
                out.write(buffer, 0, n);
                bytes += n;

                if(!checknet()){
                    Log.i("Info","network");
                    return ERROR_LOAD;
                }

                if (mSpeed != 0) {
                    previousBlockTime = -1;
                } else if (previousBlockTime == -1) {
                    previousBlockTime = System.currentTimeMillis();
                } else if ((System.currentTimeMillis() - previousBlockTime) > TIME_OUT) {
                    Log.i("Info","timeout");
                  return ERROR_LOAD;
                }
            }
            return bytes;
        } finally {
            out.close();
            bis.close();
            in.close();
        }
    }

    private final class LoadingRandomAccessFile extends RandomAccessFile {

        public LoadingRandomAccessFile(File file) throws FileNotFoundException {
            super(file, "rw");
        }

        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {

            super.write(buffer, offset, count);
            loaded += count;
            publishProgress(0);

        }
    }
}
