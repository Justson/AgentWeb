package com.just.library.agentweb;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.just.library.BaseAgentWebFragment;

/**
 * Created by cenxiaozhong on 2017/7/22.
 */

public class EasyWebFragment extends BaseAgentWebFragment {

    private ViewGroup mViewGroup;
    private ImageView mBackImageView;
    private View mLineView;
    private ImageView mFinishImageView;
    private TextView mTitleTextView;
    private ImageView mMoreImageView;
    private PopupMenu mPopupMenu;
    public static EasyWebFragment getInstance(Bundle bundle){
        EasyWebFragment mEasyWebFragment=new EasyWebFragment();
        if(bundle==null)
            mEasyWebFragment.setArguments(bundle);
        return mEasyWebFragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mViewGroup= (ViewGroup) inflater.inflate(R.layout.fragment_agentweb, container, false);
    }

    @NonNull
    @Override
    protected ViewGroup getAgentWebParent() {
        return (ViewGroup) this.mViewGroup.findViewById(R.id.linearLayout);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    protected void initView(View view) {
        mBackImageView = (ImageView) view.findViewById(R.id.iv_back);
        mLineView = view.findViewById(R.id.view_line);
        mFinishImageView = (ImageView) view.findViewById(R.id.iv_finish);
        mTitleTextView = (TextView) view.findViewById(R.id.toolbar_title);
        mBackImageView.setOnClickListener(mOnClickListener);
        mFinishImageView.setOnClickListener(mOnClickListener);
        mMoreImageView = (ImageView) view.findViewById(R.id.iv_more);
        mMoreImageView.setVisibility(View.GONE);
        pageNavigator(View.GONE);


    }

    private void pageNavigator(int tag) {

        mBackImageView.setVisibility(tag);
        mLineView.setVisibility(tag);
    }
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            switch (v.getId()) {

                case R.id.iv_back:

                    if (!mAgentWeb.back())
                        EasyWebFragment.this.getActivity().finish();

                    break;
                case R.id.iv_finish:
                    EasyWebFragment.this.getActivity().finish();
                    break;


            }
        }


    };




}
