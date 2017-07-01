package com.just.library;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/7/1.
 */

public interface IWebLayout<T extends WebView,V extends ViewGroup> {

    /**
     *
     * @return WebView 的父控件
     */
    @NonNull V getLayout();

    /**
     *
     * @return 返回 WebView  或 WebView 的子View ，返回null AgentWeb 内部会自动创建适当 WebView
     */
    @Nullable T getWeb();
}
