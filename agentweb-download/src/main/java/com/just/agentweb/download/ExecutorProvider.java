package com.just.agentweb.download;

import com.just.agentweb.Provider;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by cenxiaozhong on 2018/2/12.
 */

public class ExecutorProvider implements Provider<Executor> {


    private final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private final int CORE_POOL_SIZE = (int) (Math.max(2, Math.min(CPU_COUNT - 1, 4)) );
    private final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private final int KEEP_ALIVE_SECONDS = 15;
    public String TAG = this.getClass().getSimpleName();
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);
    private ThreadPoolExecutor mThreadPoolExecutor;

    private final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        private SecurityManager securityManager = System.getSecurityManager();
        private ThreadGroup group = securityManager != null ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();

        @Override
        public Thread newThread(Runnable r) {
            Thread mThread = new Thread(group, r, "pool-agentweb-thread-" + mCount.getAndIncrement());
            if (mThread.isDaemon()) {
                mThread.setDaemon(false);
            }
            mThread.setPriority(Thread.MIN_PRIORITY);
            return mThread;
        }
    };


    private ExecutorProvider() {
        internalInit();
    }

    private void internalInit() {
        if (mThreadPoolExecutor != null && !mThreadPoolExecutor.isShutdown()) {
            mThreadPoolExecutor.shutdownNow();
        }
        mThreadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory);
        mThreadPoolExecutor.allowCoreThreadTimeOut(true);
    }


    static ExecutorProvider getInstance() {
        return InnerHolder.M_EXECUTOR_PROVIDER;
    }

    static class InnerHolder {
        private static final ExecutorProvider M_EXECUTOR_PROVIDER = new ExecutorProvider();
    }

    @Override
    public Executor provide() {
        return mThreadPoolExecutor;
    }

}
