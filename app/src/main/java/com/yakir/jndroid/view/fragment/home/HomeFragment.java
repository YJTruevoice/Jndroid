package com.yakir.jndroid.view.fragment.home;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yakir.jndroid.R;
import com.yakir.jndroid.adapter.CourseAdapter;
import com.yakir.jndroid.model.recommand.BaseRecommandModel;
import com.yakir.jndroid.network.RequestCenter;
import com.yakir.jndroid.view.fragment.BaseFragment;
import com.yalir.okhttp.listener.DisposeDataListener;

/**
 * @author yakir
 * @function
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "ContentValue";
    /**
     * UI
     */
    private View mContentView;
    private ListView mListView;
    private TextView mCategoryView;
    private TextView mSearchView;
    private ImageView mLoadingView;

    private CourseAdapter mAdapter;
    private BaseRecommandModel mRecommandData;

    public HomeFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestRecommandData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_home_layout, container, false);
        initView();
        return mContentView;

    }

    private void initView() {
//        mQRCodeView = (TextView) mContentView.findViewById(R.id.qrcode_view);
//        mQRCodeView.setOnClickListener(this);
        mCategoryView = (TextView) mContentView.findViewById(R.id.category_view);
        mCategoryView.setOnClickListener(this);
        mSearchView = (TextView) mContentView.findViewById(R.id.search_view);
        mSearchView.setOnClickListener(this);
        mListView = (ListView) mContentView.findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);
        mLoadingView = (ImageView) mContentView.findViewById(R.id.loading_view);
        // 启动loading动画
//        AnimationDrawable anim = (AnimationDrawable) mLoadingView.getDrawable();
//        anim.start();
    }

    /**
     * 发送首页列表数据请求
     */
    private void requestRecommandData() {
        RequestCenter.requestRecommandData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                // 完成功能逻辑
                Log.e(TAG, "onSuccess" + responseObj.toString());
                mRecommandData = (BaseRecommandModel) responseObj;
                showSuccessView();
            }

            @Override
            public void onFailure(Object reasonObj) {
                // 提示网络异常
                Log.e(TAG, "onFailure" + reasonObj.toString());
            }
        });
    }

    /**
     * 请求成功显示
     */
    private void showSuccessView() {
        // 判断数据是否为空
        if (mRecommandData.data.list != null && mRecommandData.data.list.size() > 0) {
            mLoadingView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mAdapter = new CourseAdapter(mContext, mRecommandData.data.list);
            mListView.setAdapter(mAdapter);
        } else {
            showErrorView();
        }
    }

    /**
     * 无数据
     */
    private void showErrorView() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
