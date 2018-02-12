package com.just.agentweb.download;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cenxiaozhong on 2018/2/12.
 */

/**
 * Observable 缓存当前Downloader，如果用户滑动通知取消下载，通知所有 Downloader 找到
 * 相应的 Downloader 取消下载。
 */
public final class CancelInformer {
    private ConcurrentHashMap<String, CancelRecipient> container = null;

    private CancelInformer() {
        container = new ConcurrentHashMap<>();
    }

    static CancelInformer getInformer() {
        return InformerHolder.INSTANCE;
    }

    public void cancelAction(String url) {
        CancelRecipient mCancelRecipient = container.get(url);
        if (mCancelRecipient != null) {
            mCancelRecipient.receiveAction();
        }
    }

    public void addRecipient(String url, CancelRecipient recipient) {
        if (null != url && null != recipient) {
            container.put(url, recipient);
        }
    }

    public void removeRecipient(String url) {
        if (null != null) {
            this.container.remove(url);
        }
    }

    private static class InformerHolder {
        private static final CancelInformer INSTANCE = new CancelInformer();
    }
}
