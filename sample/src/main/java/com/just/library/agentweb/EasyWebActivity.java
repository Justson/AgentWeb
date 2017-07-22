package com.just.library.agentweb;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.library.BaseAgentWebActivity;

/**
 * Created by cenxiaozhong on 2017/7/22.
 * <p>
 * 使用 AgentWeb 的方法有两种， 第一种是组合 ， 第二种是继承 。
 * <p>
 * EasyWebActivity 演示的是继承使用 。
 */
public class EasyWebActivity extends BaseAgentWebActivity {

    private TextView mTitleTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        LinearLayout mLinearLayout = (LinearLayout) this.findViewById(R.id.container);
        Toolbar mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("");
        mTitleTextView = (TextView) this.findViewById(R.id.toolbar_title);
        this.setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EasyWebActivity.this.finish();


            }
        });
    }


    @NonNull
    @Override
    protected ViewGroup getAgentWebParent() {
        return (ViewGroup) this.findViewById(R.id.container);
    }

    @Override
    protected void setTitle(WebView view, String title) {
       mTitleTextView.setText(title);
    }

    @Nullable
    @Override
    protected String getUrl() {
        return "http://www.baidu.com";
    }
}
