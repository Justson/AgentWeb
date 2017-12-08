package com.just.library;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by cenxiaozhong on 2017/12/8.
 */

public class WebParentLayout extends FrameLayout {
    private AgentWebUIController mAgentWebUIController;

    public WebParentLayout(@NonNull Context context) {
        super(context);
    }

    public WebParentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WebParentLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    void bindController(AgentWebUIController agentWebUIController) {
        this.mAgentWebUIController = agentWebUIController;
    }



}
