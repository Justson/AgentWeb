package com.just.library;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by cenxiaozhong on 2017/5/13.
 * source CODE  https://github.com/Justson/AgentWeb
 */

public class RealDownLoader extends AsyncTask<Void, Integer, Integer> implements Observer{

    private DownLoadTask mDownLoadTask;
    private long loaded = 0;
    private long totals = -1;
    private long tmp = 0;
    private long begin = 0;
    private long used = 1;
    private long mTimeLast = 0;
    private long mSpeed = 0;

    private static final int TIME_OUT = 30000000;
    private Notity mNotity;

    private static final int ERROR_LOAD = 200;


    private AtomicBoolean atomic=new AtomicBoolean(false);

    private static Observable mObservable=new Observable(){
        @Override
        public synchronized void setChanged() {
            super.setChanged();
        }
    };


    RealDownLoader(DownLoadTask downLoadTask) {


        this.mDownLoadTask = downLoadTask;
        this.totals = mDownLoadTask.getLength();
        checkNullTask(downLoadTask);


    }

    private void checkNullTask(DownLoadTask downLoadTask) {

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mObservable.addObserver(this);
        buildNotify(new Intent(), mDownLoadTask.getId(), "正在下载中");
    }

    private boolean checkDownLoaderCondition() {


        if (mDownLoadTask.getLength() - mDownLoadTask.getFile().length() > AgentWebUtils.getAvailableStorage()) {
            LogUtils.i("Info", " 空间不足");
            return false;
        }

        return true;
    }

    private boolean checknet() {
        if (!mDownLoadTask.isForce()) {

            return AgentWebUtils.checkWifi(mDownLoadTask.getContext());
        } else {
            return AgentWebUtils.checkNetwork(mDownLoadTask.getContext());
        }


    }

    private Exception e;
    @Override
    protected Integer doInBackground(Void... params) {
        int result = ERROR_LOAD;
        try {
            begin = System.currentTimeMillis();
            if (!checkDownLoaderCondition())
                return DownLoadErrorMsg.STORAGE_ERROR.CODE;
            if(!checknet())
                return DownLoadErrorMsg.NETWORK_ERROR_CONNECTION.CODE;
            result = doDownLoad();

        } catch (Exception e) {

            this.e=e;
            LogUtils.i("Info", "doInBackground   Exception:" + e.getMessage());
            e.printStackTrace();

        }

        return result;
    }

    private int doDownLoad() throws IOException {

        HttpURLConnection mHttpURLConnection = createUrlConnection(mDownLoadTask.getUrl());


        if (mDownLoadTask.getFile().length() > 0) {

            mHttpURLConnection.addRequestProperty("Range", "bytes=" + (tmp = mDownLoadTask.getFile().length()) + "-");
        }

        try {
            mHttpURLConnection.connect();
            if (mHttpURLConnection.getResponseCode() != 200 && mHttpURLConnection.getResponseCode() != 206) {

                return DownLoadErrorMsg.NETWORK_ERROR_STATUS_CODE.CODE;
            }

            return doDownLoad(mHttpURLConnection.getInputStream(), new LoadingRandomAccessFile(mDownLoadTask.getFile()));
        } finally {
            if (mHttpURLConnection != null)
                mHttpURLConnection.disconnect();
        }


    }

    private HttpURLConnection createUrlConnection(String url) throws IOException {


        HttpURLConnection mHttpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        mHttpURLConnection.setRequestProperty("Accept", "application/*");
        mHttpURLConnection.setConnectTimeout(5000);
        return mHttpURLConnection;
    }

    private long time = 0;

    @Override
    protected void onProgressUpdate(Integer... values) {


        long current = System.currentTimeMillis();
        used = current - begin;


        //LogUtils.i("Info", "progress:" + ((tmp + loaded) / Float.valueOf(totals) * 100) + "tmp:" + tmp + "  load=:" + loaded + "  total:" + totals);

        long c = System.currentTimeMillis();
        if (mNotity != null && c - time > 800) {
            time = c;
            if(!mNotity.hasDeleteContent())
                mNotity.setDelecte(buildCancelContent(mDownLoadTask.getContext().getApplicationContext(),mDownLoadTask.getId()));
            mNotity.setProgress(100, (int) ((tmp + loaded) / Float.valueOf(totals) * 100), false);
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {

        LogUtils.i("Info","onPostExecute:"+integer);
        mObservable.deleteObserver(this);
        doCallback(integer);
        if (integer > 200) {

            if (mNotity != null)
                mNotity.cancel(mDownLoadTask.getId());
            return;
        }

        if (mDownLoadTask.isEnableIndicator()) {

            if (mNotity != null)
                mNotity.cancel(mDownLoadTask.getId());

            Intent intent = AgentWebUtils.getIntentCompat(mDownLoadTask.getContext(), mDownLoadTask.getFile());
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent rightPendIntent = PendingIntent.getActivity(mDownLoadTask.getContext(),
                    0x110, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mNotity.setProgressFinish("点击打开", rightPendIntent);

        }

    }

    private void doCallback(int code) {

        List<DownLoadResultListener> mDownLoadResultListeners = null;
        LogUtils.i("Info"," doCallback  mDownLoadTask.getDownLoadResultListeners():"+mDownLoadTask.getDownLoadResultListeners());
        if (AgentWebUtils.isEmptyCollection((mDownLoadResultListeners = mDownLoadTask.getDownLoadResultListeners()))){
            AgentWebUtils.toastShowShort(mDownLoadTask.getContext(),"下载失败出错了");
            return;
        }

        for (DownLoadResultListener mDownLoadResultListener : mDownLoadResultListeners) {

            if(code>200){
                mDownLoadResultListener.error(mDownLoadTask.getFile().getAbsolutePath(),mDownLoadTask.getUrl(),DownLoadErrorMsg.getCodeToMsg(code),this.e==null?new RuntimeException("下载出错 ， 原因:"+DownLoadErrorMsg.getCodeToMsg(code)):this.e);
            }else{

                mDownLoadResultListener.success(mDownLoadTask.getFile().getPath());
            }
        }


    }

    private void buildNotify(Intent intent, int id, String progressHint) {

        Context mContext=mDownLoadTask.getContext().getApplicationContext();
        if (mContext!=null&&mDownLoadTask.isEnableIndicator()) {

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent rightPendIntent = PendingIntent.getActivity(mContext,
                    0x33*id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            int smallIcon = mDownLoadTask.getDrawableRes();
            String ticker = "您有一条新通知";
            mNotity = new Notity(mContext, id);

            mNotity.notify_progress(rightPendIntent, smallIcon, ticker, "文件下载", progressHint, false, false, false, buildCancelContent(mContext,id));
            mNotity.sent();
        }
    }


    private PendingIntent buildCancelContent(Context context,int id){

        Intent intentCancel = new Intent(context, NotificationBroadcastReceiver.class);
        intentCancel.setAction("com.agentweb.notification_cancelled");
        intentCancel.putExtra("type", "type");
        intentCancel.putExtra("TAG",mDownLoadTask.getUrl());
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(context, id<<3, intentCancel, PendingIntent.FLAG_UPDATE_CURRENT);
        LogUtils.i("Info","id<<3:"+(id<<3));
        return pendingIntentCancel;
    }


    private int doDownLoad(InputStream in, RandomAccessFile out) throws IOException {

        byte[] buffer = new byte[102400];
        BufferedInputStream bis = new BufferedInputStream(in, 102400);
        try {

            out.seek(out.length());

            int bytes = 0;
            long previousBlockTime = -1;

            boolean tag=false;
            while (!atomic.get()&&(tag=true)) {
                int n = bis.read(buffer, 0, 102400);
                if (n == -1) {
                    break;
                }
                out.write(buffer, 0, n);
                bytes += n;

                if (!checknet()) {
                    LogUtils.i("Info", "network");
                    return DownLoadErrorMsg.NETWORK_ERROR_CONNECTION.CODE;
                }

                if (mSpeed != 0) {
                    previousBlockTime = -1;
                } else if (previousBlockTime == -1) {
                    previousBlockTime = System.currentTimeMillis();
                } else if ((System.currentTimeMillis() - previousBlockTime) > TIME_OUT) {
                    LogUtils.i("Info", "timeout");
                    return DownLoadErrorMsg.TIME_OUT.CODE;
                }
            }

            if (tag) {
                return DownLoadErrorMsg.USER_CANCEL.CODE;
            }
            return DownLoadErrorMsg.SUCCESSFULL.CODE;
        } finally {
            CloseUtils.closeIO(out);
            CloseUtils.closeIO(bis);

        }

    }

    private final void toCancel(){
        atomic.set(true);
    }

    @Override
    public void update(Observable o, Object arg) {

        LogUtils.i("Info","update Object    ... ");
        String url="";
        if(arg instanceof  String&& !TextUtils.isEmpty(url= (String) arg)&&url.equals(mDownLoadTask.getUrl())){
            toCancel();
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


    enum DownLoadErrorMsg {

        NETWORK_ERROR_CONNECTION(400), NETWORK_ERROR_STATUS_CODE(401) , STORAGE_ERROR(402),TIME_OUT(403),USER_CANCEL(404), SUCCESSFULL(200);

        int CODE;

        DownLoadErrorMsg(int e) {
            this.CODE = e;
        }


        public static String getCodeToMsg(int code) {
            LogUtils.i("Info",  "  CODE:" + code);
            switch (code) {


                case 400:
                    return "网络连接出错";
                case 401:
                    return "连接状态码出错 ， 非200 或者 206";
                case 402 :
                    return "内存空间不足";
                case 403:
                    return "下载时间超时";
                case 404:
                    return  "用户取消下载";
                case 200:
                    return "下载成功";
                default:
                    return "未知异常";

            }
        };


    }




    public static class NotificationBroadcastReceiver extends BroadcastReceiver  {


        public NotificationBroadcastReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();

            LogUtils.i("Info", "action:" + action);


            if (action.equals("com.agentweb.notification_cancelled")) {


                try {

                    String url=intent.getStringExtra("TAG");
                    Class<?> mClazz=mObservable.getClass();
                    Method mMethod=mClazz.getMethod("setChanged",(Class<?>[]) null);
                    mMethod.setAccessible(true);
                    mMethod.invoke(mObservable,(Object[])null);
                    mObservable.notifyObservers(url);
                    LogUtils.i("Info","size:"+mObservable.countObservers());
                }catch (NoSuchMethodException e){
                    e.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }

            }
        }


    }






}
