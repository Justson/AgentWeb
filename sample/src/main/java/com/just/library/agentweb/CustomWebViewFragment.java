package com.just.library.agentweb;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.just.library.AgentWeb;

import us.feras.mdv.MarkdownView;

/**
 * Created by cenxiaozhong on 2017/6/17.
 * source code  https://github.com/Justson/AgentWeb
 */

public class CustomWebViewFragment extends  AgentWebFragment {


    private MarkdownView mMarkdownWebView;
    private EditText markdownEditText;

    public static final CustomWebViewFragment getInstance(Bundle bundle){

        CustomWebViewFragment mCustomWebViewFragment=new CustomWebViewFragment();
        if(bundle!=null)
            mCustomWebViewFragment.setArguments(bundle);
        return mCustomWebViewFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.markdown_view, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
        //MarkdownView 是 WebView  的一个子类
        mMarkdownWebView = new MarkdownView(getActivity());
        markdownEditText = (EditText) view.findViewById(R.id.markdownText);

        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
        lp.weight=1f;
        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent((ViewGroup) view, lp)//
                .closeDefaultIndicator()//
                .setWebViewClient(mWebViewClient)
                .setReceivedTitleCallback(mCallback)
                .setWebView(mMarkdownWebView)
                .setSecurityType(AgentWeb.SecurityType.strict)
                .createAgentWeb()//
                .ready()//
                .go(null);


        mMarkdownWebView.getSettings().setTextZoom(300);


        String text="## AgentWeb 功能\n" +
                "***\n\n" +
                "1. 支持进度条以及自定义进度条\n" +
                "2. 支持文件下载\n" +
                "3. 支持文件下载断点续传\n" +
                "4. 支持下载通知形式提示进度\n" +
                "5. 简化 Javascript 通信 \n" +
                "6. 支持 Android 4.4 Kitkat 以及其他版本文件上传\n" +
                "7. 支持注入 Cookies\n" +
                "8. 加强 Web 安全\n" +
                "9. 支持全屏播放视频\n" +
                "10. 兼容低版本 Js 安全通信\n" +
                "11. 更省电 。\n" +
                "12. 支持调起微信支付\n" +
                "13. 支持调起支付宝（请参照sample）\n" +
                "14. 默认支持定位";

        markdownEditText.setText(text);

        updateMarkdownView();

        markdownEditText.addTextChangedListener(new TextWatcher() {



            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateMarkdownView();
            }
        });


        initView(view);

    }


    private void updateMarkdownView() {
        mMarkdownWebView.loadMarkdown(markdownEditText.getText().toString());
    }
}
