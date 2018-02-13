package com.just.agentweb.download;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cenxiaozhong on 2018/2/12.
 */

/**
 * CancelDownloadInformer 缓存当前所有 Downloader，
 * 如果用户滑动通知取消下载，通知相应 Downloader 取消下载。
 */
public final class CancelDownloadInformer {
    private ConcurrentHashMap<String, CancelDownloadRecipient> mRecipients = null;

    private CancelDownloadInformer() {
        mRecipients = new ConcurrentHashMap<>();
    }

    static CancelDownloadInformer getInformer() {
        return InformerHolder.INSTANCE;
    }

    public void cancelAction(String url) {
        CancelDownloadRecipient mCancelDownloadRecipient = mRecipients.get(url);
        if (null != mCancelDownloadRecipient) {
            mCancelDownloadRecipient.cancelDownload();
        }
    }

    public void addRecipient(String url, CancelDownloadRecipient recipient) {
        if (null != url && null != recipient) {
            mRecipients.put(url, recipient);
        }
    }

    public void removeRecipient(String url) {
        if (null != null) {
            this.mRecipients.remove(url);
        }
    }

    private static class InformerHolder {
        private static final CancelDownloadInformer INSTANCE = new CancelDownloadInformer();
    }
}
