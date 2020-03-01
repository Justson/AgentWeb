package com.just.agentweb.sample.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.download.library.DownloadException;
import com.download.library.DownloadImpl;
import com.download.library.DownloadListenerAdapter;
import com.download.library.DownloadTask;
import com.download.library.Downloader;
import com.download.library.Extra;
import com.download.library.Runtime;
import com.just.agentweb.sample.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

//import com.download.library.DownloadException;
//import com.download.library.DownloadImpl;
//import com.download.library.DownloadListenerAdapter;
//import com.download.library.DownloadTask;
//import com.download.library.Downloader;
//import com.download.library.Extra;
//import com.download.library.Runtime;

/**
 * @author ringle-android
 * @date 19-2-12
 * @since 1.0.0
 */
public class NativeDownloadActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private TextView mTitleTextView;
    private ArrayList<DownloadBean> mDownloadTasks = new ArrayList<DownloadBean>();
    private static final String TAG = NativeDownloadActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_download);
        createDatasource();
        mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("");
        mTitleTextView = (TextView) this.findViewById(R.id.toolbar_title);
        mTitleTextView.setText("原生下载");
        mRecyclerView = this.findViewById(R.id.download_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(new NativeDownloadAdapter());

       /*new Thread(new Runnable() {
            @Override
            public void run() {
                File file = DownloadImpl.getInstance().with(getApplicationContext()).url("http://shouji.360tpcdn.com/170918/93d1695d87df5a0c0002058afc0361f1/com.ss.android.article.news_636.apk").setDownloadingListener(new DownloadListenerAdapter() {
                    @Override
                    public void onProgress(String url, long downloaded, long length, long usedTime) {
                        super.onProgress(url, downloaded, length, usedTime);
                        Log.i(TAG, " downloaded:" + downloaded);
                    }

                    @Override
                    public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
                        Log.i(TAG, "downloaded onResult isSuccess:" + (throwable == null) + " url:" + url + " Thread:" + Thread.currentThread().getName() + " uri:" + path.toString());

                        return super.onResult(throwable, path, url, extra);
                    }
                }).get();
                Log.i(TAG, " download success:" + ((File) file).length());
            }
        }).start();*/
        /*DownloadImpl.getInstance()
                .with(getApplicationContext())
                .setEnableIndicator(true)
                .url("http://shouji.360tpcdn.com/170918/f7aa8587561e4031553316ada312ab38/com.tencent.qqlive_13049.apk")
                .enqueue(new DownloadListenerAdapter() {
                    @Override
                    public void onProgress(String url, long downloaded, long length, long usedTime) {
                        super.onProgress(url, downloaded, length, usedTime);
                        Log.i(TAG, " progress:" + downloaded + " url:" + url);
                    }

                    @Override
                    public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
                        Log.i(TAG, " path:" + path + " url:" + url + " length:" + new File(path.getPath()).length());
                        return super.onResult(throwable, path, url, extra);
                    }
                });

        File file = new File(this.getCacheDir(), "测试.apk");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DownloadImpl.getInstance()
                .with(getApplicationContext())
                .target(file)
                .url("http://shouji.360tpcdn.com/170918/93d1695d87df5a0c0002058afc0361f1/com.ss.android.article.news_636.apk")
                .enqueue(new DownloadListenerAdapter() {
                    @Override
                    public void onProgress(String url, long downloaded, long length, long usedTime) {
                        super.onProgress(url, downloaded, length, usedTime);
                        Log.i(TAG, " progress:" + downloaded + " url:" + url);
                    }

                    @Override
                    public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
                        Log.i(TAG, " path:" + path + " url:" + url + " length:" + new File(path.getPath()).length());
                        return super.onResult(throwable, path, url, extra);
                    }
                });*/

       /* DownloadImpl.getInstance()
                .with(getApplicationContext())
                .target(new File(Runtime.getInstance().getDir(this, true).getAbsolutePath() + "/" + "com.ss.android.article.news_636.apk"), this.getPackageName() + ".DownloadFileProvider")//自定义路径需指定目录和authority(FileContentProvide),需要相对应匹配才能启动通知，和自动打开文件
                .setUniquePath(false)//是否唯一路径
                .setForceDownload(true)//不管网络类型
                .setRetry(4)//下载异常，自动重试,最多重试4次
                .setBlockMaxTime(60000L) //以8KB位单位，默认60s ，如果60s内无法从网络流中读满8KB数据，则抛出异常 。
                .setConnectTimeOut(10000L)//连接10超时
                .addHeader("xx","cookie")//添加请求头
                .setDownloadTimeOut(Long.MAX_VALUE)//下载最大时长
                .setOpenBreakPointDownload(true)//打开断点续传
                .setParallelDownload(true)//打开多线程下载
                .autoOpenWithMD5("93d1695d87df5a0c0002058afc0361f1")//校验md5通过后自动打开该文件,校验失败会回调异常
//                .autoOpenIgnoreMD5()
//                .closeAutoOpen()
                .quickProgress()//快速连续回调进度，默认1.2s回调一次
                .url("http://shouji.360tpcdn.com/170918/93d1695d87df5a0c0002058afc0361f1/com.ss.android.article.news_636.apk")
                .enqueue(new DownloadListenerAdapter() {
                    @Override
                    public void onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra) {
                        super.onStart(url, userAgent, contentDisposition, mimetype, contentLength, extra);
                    }

                    @MainThread //加上该注解，自动回调到主线程
                    @Override
                    public void onProgress(String url, long downloaded, long length, long usedTime) {
                        super.onProgress(url, downloaded, length, usedTime);
                        Log.i(TAG, " progress:" + downloaded + " url:" + url);
                    }

                    @Override
                    public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
                        String md5 = Runtime.getInstance().md5(new File(path.getPath()));
                        Log.i(TAG, " path:" + path + " url:" + url + " length:" + new File(path.getPath()).length() + " md5:" + md5 + " extra.getFileMD5:" + extra.getFileMD5());
                        return super.onResult(throwable, path, url, extra);
                    }
                });*/

    }

    private class NativeDownloadAdapter extends RecyclerView.Adapter<NativeDownloadViewHolder> {
        @NonNull
        @Override
        public NativeDownloadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item_download, viewGroup, false);
            return new NativeDownloadViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final NativeDownloadViewHolder nativeDownloadViewHolder, final int i) {
            final DownloadBean downloadBean = mDownloadTasks.get(i);
            Picasso.get().load(downloadBean.imageUrl)
                    .resize(100, 100)
                    .centerCrop().
                    transform(new RoundTransform(NativeDownloadActivity.this.getApplicationContext()))
                    .into(nativeDownloadViewHolder.mIconIv);
            nativeDownloadViewHolder.mStatusButton.setEnabled(true);
            nativeDownloadViewHolder.mStatusButton.setTag(downloadBean);
            if (downloadBean.getTotalsLength() > 0L) {
                int mProgress = (int) ((downloadBean.getLoaded()) / Float.valueOf(downloadBean.getTotalsLength()) * 100);
                Log.e(TAG, "mProgress:" + mProgress + " position:" + i);
                nativeDownloadViewHolder.mProgressBar.setProgress(mProgress);
                nativeDownloadViewHolder.mCurrentProgress.setText("当前进度" + byte2FitMemorySize(downloadBean.getLoaded()) + "/" + byte2FitMemorySize(downloadBean.getTotalsLength()) + " 耗时:" + ((downloadBean.getUsedTime()) / 1000) + "s");
            } else {
                nativeDownloadViewHolder.mProgressBar.setProgress(0);
                nativeDownloadViewHolder.mCurrentProgress.setText("当前进度,已下载:" + byte2FitMemorySize(downloadBean.getLoaded()) + " 耗时:" + ((downloadBean.getUsedTime()) / 1000) + "s");
            }
            Log.e(TAG, "status:" + downloadBean.getStatus() + " position:" + i);
            if (downloadBean.getStatus() == DownloadTask.STATUS_NEW) {
                nativeDownloadViewHolder.mStatusButton.setText("开始");
            } else if (downloadBean.getStatus() == DownloadTask.STATUS_PENDDING) {
                nativeDownloadViewHolder.mStatusButton.setText("等待中...");
                nativeDownloadViewHolder.mStatusButton.setEnabled(false);
            } else if (downloadBean.getStatus() == DownloadTask.STATUS_PAUSED) {
                nativeDownloadViewHolder.mStatusButton.setText("继续");
            } else if (downloadBean.getStatus() == DownloadTask.STATUS_DOWNLOADING) {
                nativeDownloadViewHolder.mStatusButton.setText("暂停");
            } else if (downloadBean.getStatus() == DownloadTask.STATUS_CANCELED || downloadBean.getStatus() == DownloadTask.STATUS_ERROR) {
                nativeDownloadViewHolder.mStatusButton.setText("出错");
                nativeDownloadViewHolder.mStatusButton.setEnabled(false);
            } else {
                nativeDownloadViewHolder.mStatusButton.setText("已完成");
                nativeDownloadViewHolder.mStatusButton.setEnabled(false);
            }
            nativeDownloadViewHolder.mStatusButton.setOnClickListener(new View.OnClickListener() {
                long lastTime = SystemClock.elapsedRealtime();

                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - lastTime <= 500) {
                        return;
                    }
                    lastTime = SystemClock.elapsedRealtime();
                    if (downloadBean.getStatus() == DownloadTask.STATUS_NEW) {
                        nativeDownloadViewHolder.mStatusButton.setText("等待中...");
                        nativeDownloadViewHolder.mStatusButton.setEnabled(false);
                        boolean isStarted = DownloadImpl.getInstance().enqueue(downloadBean);
                        if (!isStarted) {
                            bindViewHolder(nativeDownloadViewHolder, i);
                        }
                    } else if (downloadBean.getStatus() == DownloadTask.STATUS_PENDDING) {
                    } else if (downloadBean.getStatus() == DownloadTask.STATUS_DOWNLOADING) {
                        DownloadTask downloadTask = DownloadImpl.getInstance().pause(downloadBean.getUrl());
                        if (downloadTask != null) {
                            nativeDownloadViewHolder.mStatusButton.setText("继续");
                        } else {
                            bindViewHolder(nativeDownloadViewHolder, i);
                        }
                    } else if (downloadBean.getStatus() == DownloadTask.STATUS_PAUSED) {
                        boolean isStarted = DownloadImpl.getInstance().resume(downloadBean.getUrl());
                        nativeDownloadViewHolder.mStatusButton.setText("等待中...");
                        nativeDownloadViewHolder.mStatusButton.setEnabled(false);
                        if (!isStarted) {
                            bindViewHolder(nativeDownloadViewHolder, i);
                        }
                    } else if (downloadBean.getStatus() == DownloadTask.STATUS_CANCELED) {
                    } else {
                        nativeDownloadViewHolder.mStatusButton.setEnabled(false);
                        nativeDownloadViewHolder.mStatusButton.setText("已完成");
                    }
                }
            });
            downloadBean.setDownloadListenerAdapter(new DownloadListenerAdapter() {
                @Override
                public void onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra) {
                    nativeDownloadViewHolder.mStatusButton.setText("暂停");
                    nativeDownloadViewHolder.mStatusButton.setEnabled(true);
                    Log.i(TAG, " isRunning:" + DownloadImpl.getInstance().isRunning(url));
                }

                @MainThread //回调到主线程，添加该注释
                @Override
                public void onProgress(String url, long downloaded, long length, long usedTime) {
                    if (nativeDownloadViewHolder.mStatusButton.getTag() != downloadBean) {
                        Log.e(TAG, "onProgress item recycle");
                        return;
                    }
                    int mProgress = (int) ((downloaded) / Float.valueOf(length) * 100);
                    Log.i(TAG, "onProgress:" + mProgress + " url:" + url + " Thread:" + Thread.currentThread().getName());
                    nativeDownloadViewHolder.mProgressBar.setProgress(mProgress);
                    if (length <= 0) {
                        nativeDownloadViewHolder.mCurrentProgress.setText("当前进度,已下载:" + byte2FitMemorySize(downloaded) + " 耗时:" + ((downloadBean.getUsedTime()) / 1000) + "s");
                    } else {
                        nativeDownloadViewHolder.mCurrentProgress.setText("当前进度" + byte2FitMemorySize(downloaded) + "/" + byte2FitMemorySize(length) + " 耗时:" + ((downloadBean.getUsedTime()) / 1000) + "s");

                    }
                }

                @Override
                public boolean onResult(Throwable throwable, Uri uri, String url, Extra extra) {
                    if (nativeDownloadViewHolder.mStatusButton.getTag() != downloadBean) {
                        Log.e(TAG, "item recycle");
                        return super.onResult(throwable, uri, url, extra);
                    }
                    Log.i(TAG, "onResult isSuccess:" + (throwable == null) + " url:" + url + " Thread:" + Thread.currentThread().getName() + " uri:" + uri.toString() + " isPaused:" + DownloadImpl.getInstance().isPaused(url));
                    nativeDownloadViewHolder.mStatusButton.setEnabled(false);
                    if (throwable == null) {
                        nativeDownloadViewHolder.mStatusButton.setText("已完成");
                    } else if (throwable instanceof DownloadException) {
                        DownloadException downloadException = (DownloadException) throwable;
                        if (downloadException.getCode() == Downloader.ERROR_USER_PAUSE) {
                            nativeDownloadViewHolder.mStatusButton.setText("继续");
                            nativeDownloadViewHolder.mStatusButton.setEnabled(true);
                        } else {
                            nativeDownloadViewHolder.mStatusButton.setText("出错");
                        }
                        Toast.makeText(NativeDownloadActivity.this, downloadException.getMsg(), 1).show();
                    }
                    return super.onResult(throwable, uri, url, extra);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mDownloadTasks.size();
        }
    }

    private static String byte2FitMemorySize(final long byteNum) {
        if (byteNum < 0) {
            return "";
        } else if (byteNum < 1024) {
            return String.format(Locale.getDefault(), "%.1fB", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format(Locale.getDefault(), "%.1fKB", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format(Locale.getDefault(), "%.1fMB", (double) byteNum / 1048576);
        } else {
            return String.format(Locale.getDefault(), "%.1fGB", (double) byteNum / 1073741824);
        }
    }

    private class NativeDownloadViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mProgressBar;
        Button mStatusButton;
        ImageView mIconIv;
        private final TextView mCurrentProgress;

        public NativeDownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            mIconIv = itemView.findViewById(R.id.icon_iv);
            mStatusButton = itemView.findViewById(R.id.start_button);
            mProgressBar = itemView.findViewById(R.id.progressBar);
            mProgressBar.setMax(100);
            mCurrentProgress = itemView.findViewById(R.id.current_progress);
        }
    }

    public static class DownloadBean extends DownloadTask {
        public String title;
        public String imageUrl;

        public DownloadBean(String title, String imageUrl, String url) {
            this.title = title;
            this.imageUrl = imageUrl;
            this.mUrl = url;
        }

        @Override
        protected DownloadBean setDownloadListenerAdapter(DownloadListenerAdapter downloadListenerAdapter) {
            return (DownloadBean) super.setDownloadListenerAdapter(downloadListenerAdapter);
        }

        @Override
        public DownloadBean setUrl(String url) {
            return (DownloadBean) super.setUrl(url);
        }

        @Override
        public DownloadBean setContext(Context context) {
            return (DownloadBean) super.setContext(context);
        }

        @Override
        public DownloadBean setEnableIndicator(boolean enableIndicator) {
            return (DownloadBean) super.setEnableIndicator(enableIndicator);
        }

        @Override
        public DownloadBean setRetry(int retry) {
            return (DownloadBean) super.setRetry(retry);
        }

        @Override
        public DownloadBean setQuickProgress(boolean quickProgress) {
            return (DownloadBean) super.setQuickProgress(quickProgress);
        }

        @Override
        public DownloadBean autoOpenIgnoreMD5() {
            return (DownloadBean) super.autoOpenIgnoreMD5();
        }
    }

    public static class RoundTransform implements com.squareup.picasso.Transformation {

        private Context mContext;

        public RoundTransform(Context context) {
            mContext = context;
        }

        @Override
        public Bitmap transform(Bitmap source) {

            int widthLight = source.getWidth();
            int heightLight = source.getHeight();
            int radius = dp2px(mContext, 8); // 圆角半径

            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(output);
            Paint paintColor = new Paint();
            paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);

            RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));

            canvas.drawRoundRect(rectF, radius, radius, paintColor);
//        canvas.drawRoundRect(rectF, widthLight / 5, heightLight / 5, paintColor);

            Paint paintImage = new Paint();
            paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
            canvas.drawBitmap(source, 0, 0, paintImage);
            source.recycle();
            return output;
        }

        @Override
        public String key() {
            return "roundcorner";
        }

    }

    public void createDatasource() {
        DownloadBean downloadBean = new DownloadBean("QQ", "http://p18.qhimg.com/dr/72__/t0111cb71dabfd83b21.png", "https://d71329e5c0be6cdc2b46d0df2b4bd841.dd.cdntips.com/imtt.dd.qq.com/16891/apk/06AB1F5B0A51BEFD859B2B0D6B9ED9D9.apk?mkey=5d47b9f223f7bc0d&f=1806&fsname=com.tencent.mobileqq_8.1.0_1232.apk&csr=1bbd&cip=35.247.154.248&proto=https");
        downloadBean.setQuickProgress(true);
        downloadBean.setRetry(4);
        downloadBean.setContext(this.getApplicationContext());
        mDownloadTasks.add(downloadBean);
        downloadBean = new DownloadBean("支付宝", "http://p18.qhimg.com/dr/72__/t01a16bcd9acd07d029.png", "http://shouji.360tpcdn.com/170919/e7f5386759129f378731520a4c953213/com.eg.android.AlipayGphone_115.apk");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);

        downloadBean = new DownloadBean("UC", "http://p19.qhimg.com/dr/72__/t01195d02b486ef8ebe.png", "http://shouji.360tpcdn.com/170919/9f1c0f93a445d7d788519f38fdb3de77/com.UCMobile_704.apk");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);

        downloadBean = new DownloadBean("腾讯视频", "http://p18.qhimg.com/dr/72__/t01ed14e0ab1a768377.png", "http://shouji.360tpcdn.com/170918/f7aa8587561e4031553316ada312ab38/com.tencent.qqlive_13049.apk");
        downloadBean.setContext(this.getApplicationContext());
        mDownloadTasks.add(downloadBean);

        downloadBean = new DownloadBean("头条", "http://p15.qhimg.com/dr/72__/t013d31024ae54d9c35.png", "http://shouji.360tpcdn.com/170918/93d1695d87df5a0c0002058afc0361f1/com.ss.android.article.news_636.apk");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setEnableIndicator(false);
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);

        downloadBean = new DownloadBean("应用宝", "https://pp.myapp.com/ma_icon/0/icon_5848_1565090584/96", "http://imtt.dd.qq.com/16891/myapp/channel_78665107_1000047_48e7227d3afeb842447c73c4b7af2509.apk?hsr=5848");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setEnableIndicator(false);
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);

        downloadBean = new DownloadBean("附近越爱", "https://pp.myapp.com/ma_icon/0/icon_52396134_1563435176/96", "https://wxz.myapp.com/16891/apk/66339C385B32951E838F89AFDBB8AFBF.apk?fsname=com.wangjiang.fjya_5.6.3_98.apk&hsr=4d5s");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setEnableIndicator(false);
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);

        downloadBean = new DownloadBean("白菜二手车", "https://pp.myapp.com/ma_icon/0/icon_52728407_1565231751/96", "http://imtt.dd.qq.com/16891/myapp/channel_78665107_1000047_48e7227d3afeb842447c73c4b7af2509.apk?hsr=5848&fsname=YYB.998886.dad220fda3959275efcb77f06835b974.1000047.apk");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setEnableIndicator(false);
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);

        downloadBean = new DownloadBean("老鹰抓小鸡", "https://pp.myapp.com/ma_icon/0/icon_12097212_1555095310/96", "http://183.235.254.177/cache/112.29.208.41/imtt.dd.qq.com/16891/myapp/channel_78665107_1000047_48e7227d3afeb842447c73c4b7af2509.apk?mkey=5d5016b578e7f75c&f=184b&hsr=5848&fsname=YYB.998886.2e4a1c0f5a55b75a2e7a10c0b53a3491.1000047.apk&cip=120.231.209.169&proto=http&ich_args2=6-11231103023581_c2af2d3056e749ee2654202c210b6535_10004303_9c896229d2c0f7d3903d518939a83798_e03b546f591096a2b6182b487572fb16");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setEnableIndicator(false);
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);

        downloadBean = new DownloadBean("2345手机助手", "https://pp.myapp.com/ma_icon/0/icon_10427994_1565164413/96", "https://wxz.myapp.com/16891/apk/14004450452AC52D15749001DBD0E4EA.apk?fsname=com.market2345_7.0_115.apk&hsr=4d5s");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setEnableIndicator(false);
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);

        downloadBean = new DownloadBean("随手借", "https://pp.myapp.com/ma_pic2/0/shot_12170461_3_1564367665/550", "https://fb187cdbcc69278c9f1e6ce8e7257596.dd.cdntips.com/wxz.myapp.com/16891/apk/B505BB2B5D831592D5E190BAD5E66CCA.apk?mkey=5d50161b78e7f75c&f=1026&fsname=audaque.SuiShouJie_4.11.11_49.apk&hsr=4d5s&cip=120.231.209.169&proto=https");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setEnableIndicator(false);
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);

        downloadBean = new DownloadBean("极光单词", "https://pp.myapp.com/ma_pic2/0/shot_52835037_1_1564713577/550", "https://6b7e49d6fab5c817409329478a000160.dd.cdntips.com/wxz.myapp.com/16891/apk/C721DE2D7E4538772FA98C1E9830F92F.apk?mkey=5d5017df78e7f75c&f=9870&fsname=com.qingclass.jgdc_2.0.4_9.apk&hsr=4d5s&cip=120.231.209.169&proto=https");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setEnableIndicator(false);
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);


        downloadBean = new DownloadBean("帮帮测", "https://pp.myapp.com/ma_pic2/0/shot_52499136_3_1561616032/550", "https://fb187cdbcc69278c9f1e6ce8e7257596.dd.cdntips.com/wxz.myapp.com/16891/5571F5786B8E9F15058BE615B419A28B.apk?mkey=5d50176c78e7f75c&f=8ea4&fsname=com.bangbangce.mm_4.1.4_3104.apk&hsr=4d5s&cip=120.231.209.169&proto=https");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setEnableIndicator(false);
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);

        downloadBean = new DownloadBean("速贷之家", "https://pp.myapp.com/ma_pic2/0/shot_42330202_2_1564649857/550", "https://3e25603914f997244c41c1ed7fbedfb5.dd.cdntips.com/wxz.myapp.com/16891/apk/7AADD4A8C9D404FB97378EA3CA2E69E6.apk?mkey=5d50172c78e7f75c&f=184b&fsname=com.yeer.sdzj_3.2.8_328.apk&hsr=4d5s&cip=120.231.209.169&proto=https");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setEnableIndicator(false);
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);


        downloadBean = new DownloadBean("中原消费金融", "https://pp.myapp.com/ma_pic2/0/shot_52471681_2_1565161792/550", "https://f437b8a1a8be40951a91f58666e659d0.dd.cdntips.com/wxz.myapp.com/16891/apk/B1C6CC0DB7D412DA47A3A446E28D9C09.apk?mkey=5d5014fb78e7f75c&f=24c5&fsname=com.hnzycfc.zyxj_3.0.1_52.apk&hsr=4d5s&cip=120.231.209.169&proto=https");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setEnableIndicator(false);
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);


        downloadBean = new DownloadBean("店长直聘", "https://pp.myapp.com/ma_icon/0/icon_12216213_1564373730/96", "https://f437b8a1a8be40951a91f58666e659d0.dd.cdntips.com/wxz.myapp.com/16891/apk/FA29D09A6CD550DCBEBC1D89EA392109.apk?mkey=5d5014b478e7f75c&f=1849&fsname=com.hpbr.directhires_4.31_403010.apk&hsr=4d5s&cip=120.231.209.169&proto=https");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setEnableIndicator(false);
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);


        downloadBean = new DownloadBean("淘卷吧", "https://pp.myapp.com/ma_icon/0/icon_42320744_1564583832/96", "https://11473001bb572df6cb60e7e0821a4586.dd.cdntips.com/wxz.myapp.com/16891/apk/4AA997287EEA4A96C2DFD97CEE0180AD.apk?mkey=5d50148f78e7f75c&f=24c5&fsname=com.ciyun.oneshop_7.07_69.apk&hsr=4d5s&cip=120.231.209.169&proto=https");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setEnableIndicator(false);
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);


        downloadBean = new DownloadBean("本地寻爱", "https://pp.myapp.com/ma_icon/0/icon_53268261_1564479560/96", "https://ce7ce9c885b5c04b6771ea454e096946.dd.cdntips.com/wxz.myapp.com/16891/apk/AAB98D7BDAFB390FA4D37F6CBD910992.apk?mkey=5d50142d78e7f75c&f=07b4&fsname=com.kaitai.bdxa_5.6.3_98.apk&hsr=4d5s&cip=120.231.209.169&proto=https");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setEnableIndicator(false);
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);


        downloadBean = new DownloadBean("360借条", "https://pp.myapp.com/ma_icon/0/icon_42379225_1564124706/96", "https://e2983106ebfb9f560ff3a8e230faa981.dd.cdntips.com/wxz.myapp.com/16891/apk/DEB654116EC627ABA4DB12A6E777EAAD.apk?mkey=5d5015d578e7f75c&f=1026&fsname=com.qihoo.loan_1.5.4_213.apk&hsr=4d5s&cip=120.231.209.169&proto=https");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.setEnableIndicator(false);
        downloadBean.setQuickProgress(true);
        mDownloadTasks.add(downloadBean);


        downloadBean = new DownloadBean("淘宝", "https://pp.myapp.com/ma_icon/0/icon_5080_1564463763/96", "http://shouji.360tpcdn.com/170901/ec1eaad9d0108b30d8bd602da9954bb7/com.taobao.taobao_161.apk");
        downloadBean.setContext(this.getApplicationContext());
        mDownloadTasks.add(downloadBean);

        //http://www.httpwatch.com/httpgallery/chunked/chunkedimage.aspx?0.04400023248109086

        downloadBean = new DownloadBean("分块传输，图片", "http://www.httpwatch.com/httpgallery/chunked/chunkedimage.aspx?0.04400023248109086", "http://www.httpwatch.com/httpgallery/chunked/chunkedimage.aspx?0.04400023248109086");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.autoOpenIgnoreMD5();
        mDownloadTasks.add(downloadBean);


        downloadBean = new DownloadBean("也爱直播", "https://pp.myapp.com/ma_icon/0/icon_10472625_1555686747/96", "https://a46fefcd092f5f917ed1ee349b85d3b7.dd.cdntips.com/wxz.myapp.com/16891/F9B7FA7EC195FC453AE9082F826E6B28.apk?mkey=5d4c6bdc78e5058d&f=1806&fsname=com.tiange.hz.paopao8_4.4.1_441.apk&hsr=4d5s&cip=120.229.35.120&proto=https");
        downloadBean.setContext(this.getApplicationContext());
        downloadBean.autoOpenIgnoreMD5().setQuickProgress(true);
        mDownloadTasks.add(downloadBean);

        //
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadImpl.getInstance().cancelAll();
    }
}
