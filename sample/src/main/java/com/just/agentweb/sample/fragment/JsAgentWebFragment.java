package com.just.agentweb.sample.fragment;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.LinearLayout;

import com.just.agentweb.sample.common.AndroidInterface;
import com.just.agentweb.sample.R;

import org.json.JSONObject;

/**
 * Created by cenxiaozhong on 2017/5/26.
 * source code  https://github.com/Justson/AgentWeb
 */

public class JsAgentWebFragment extends AgentWebFragment {

    public static final JsAgentWebFragment getInstance(Bundle bundle) {

        JsAgentWebFragment mJsAgentWebFragment = new JsAgentWebFragment();
        if (bundle != null){
            mJsAgentWebFragment.setArguments(bundle);
        }

        return mJsAgentWebFragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        LinearLayout mLinearLayout= (LinearLayout) view;
        LayoutInflater.from(getContext()).inflate(R.layout.fragment_js,mLinearLayout,true);
        super.onViewCreated(view, savedInstanceState);


        if(mAgentWeb!=null){
            //注入对象
            mAgentWeb.getJsInterfaceHolder().addJavaObject("android",new AndroidInterface(mAgentWeb,this.getActivity()));
        }
        view.findViewById(R.id.callJsNoParamsButton).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.callJsOneParamsButton).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.callJsMoreParamsButton).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.jsJavaCommunicationButton).setOnClickListener(mOnClickListener);



    }

    private View.OnClickListener mOnClickListener=new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onClick(View v) {


            switch (v.getId()){

                case R.id.callJsNoParamsButton:
                    mAgentWeb.getJsAccessEntrace().quickCallJs("callByAndroid");
                    break;

                case R.id.callJsOneParamsButton:
                    mAgentWeb.getJsAccessEntrace().quickCallJs("callByAndroidParam","Hello ! Agentweb");
                    break;

                case R.id.callJsMoreParamsButton:
                    mAgentWeb.getJsAccessEntrace().quickCallJs("callByAndroidMoreParams", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            Log.i("Info","value:"+value);
                        }
                    },getJson(),"say:", " Hello! Agentweb");

                    break;
                case R.id.jsJavaCommunicationButton:
                    mAgentWeb.getJsAccessEntrace().quickCallJs("callByAndroidInteraction","你好Js");
                    break;
            }

        }
    };

    private String getJson(){

        String result="";
        try {

            JSONObject mJSONObject=new JSONObject();
            mJSONObject.put("id",1);
            mJSONObject.put("name","Agentweb");
            mJSONObject.put("age",18);
            result= mJSONObject.toString();
        }catch (Exception e){

        }

        return result;
    }


}
