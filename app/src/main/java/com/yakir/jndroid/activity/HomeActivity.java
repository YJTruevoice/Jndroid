package com.yakir.jndroid.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yakir.jndroid.R;
import com.yakir.jndroid.activity.base.BaseActivity;
import com.yakir.jndroid.view.fragment.home.HomeFragment;
import com.yakir.jndroid.view.fragment.home.MessageFragment;
import com.yakir.jndroid.view.fragment.home.MineFragment;

/**
 * @author yakir
 * @function 主页Activity 创建fragment并切换
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    /**
     * UI控件
     */
    private RelativeLayout mHomeLayout;
    private RelativeLayout mPondLayout;
    private RelativeLayout mMessageLayout;
    private RelativeLayout mMineLayout;
    private TextView mHomeView;
    private TextView mPondView;
    private TextView mMessageView;
    private TextView mMineView;
    /**
     * 变量
     */
    private Fragment mCurrent;
    private FragmentManager fm;
    private HomeFragment mHomeFragment;
    private Fragment mCommonFragmentOne;
    private MineFragment mMineFragment;
    private MessageFragment mMessageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_layout);
        // 初始化控件
        initView();
        // 添加默认显示fragment
        mHomeFragment = new HomeFragment();
        fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content_layout, mHomeFragment);
        fragmentTransaction.commit();
    }

    private void initView() {
        mHomeLayout = findView(R.id.home_layout_view);
        mHomeLayout.setOnClickListener(this);
        mPondLayout = findView(R.id.pond_layout_view);
        mPondLayout.setOnClickListener(this);
        mMessageLayout = findView(R.id.message_layout_view);
        mMessageLayout.setOnClickListener(this);
        mMineLayout = findView(R.id.mine_layout_view);
        mMineLayout.setOnClickListener(this);

        mHomeView = findView(R.id.home_image_view);
        mPondView = findView(R.id.fish_image_view);
        mMessageView = findView(R.id.message_image_view);
        mMineView = findView(R.id.mine_image_view);
        mHomeView.setBackgroundResource(R.mipmap.tab_home_select);
    }

    private <T> T findView(int id) {
        return (T) findViewById(id);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        switch (v.getId()) {
            case R.id.home_layout_view:
//                changeStatusBarColor(R.color.color_fed952);
                mHomeView.setBackgroundResource(R.mipmap.tab_home_select);
                mPondView.setBackgroundResource(R.mipmap.tab_home_normal);
                mMessageView.setBackgroundResource(R.mipmap.tab_home_normal);
                mMineView.setBackgroundResource(R.mipmap.tab_home_normal);

                hideFragment(mCommonFragmentOne, fragmentTransaction);
                hideFragment(mMessageFragment, fragmentTransaction);
                hideFragment(mMineFragment, fragmentTransaction);
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
                    fragmentTransaction.add(R.id.content_layout, mHomeFragment);
                } else {
                    mCurrent = mHomeFragment;
                    fragmentTransaction.show(mHomeFragment);
                }
                break;
            case R.id.message_layout_view:
//                changeStatusBarColor(R.color.color_e3e3e3);
                mMessageView.setBackgroundResource(R.mipmap.tab_home_select);
                mHomeView.setBackgroundResource(R.mipmap.tab_home_normal);
                mPondView.setBackgroundResource(R.mipmap.tab_home_normal);
                mMineView.setBackgroundResource(R.mipmap.tab_home_normal);

                hideFragment(mCommonFragmentOne, fragmentTransaction);
                hideFragment(mHomeFragment, fragmentTransaction);
                hideFragment(mMineFragment, fragmentTransaction);
                if (mMessageFragment == null) {
                    mMessageFragment = new MessageFragment();
                    fragmentTransaction.add(R.id.content_layout, mMessageFragment);
                } else {
                    mCurrent = mMessageFragment;
                    fragmentTransaction.show(mMessageFragment);
                }
                break;
            case R.id.mine_layout_view:
//                changeStatusBarColor(R.color.white);
                mMineView.setBackgroundResource(R.mipmap.tab_home_select);
                mHomeView.setBackgroundResource(R.mipmap.tab_home_normal);
                mPondView.setBackgroundResource(R.mipmap.tab_home_normal);
                mMessageView.setBackgroundResource(R.mipmap.tab_home_normal);
                hideFragment(mCommonFragmentOne, fragmentTransaction);
                hideFragment(mMessageFragment, fragmentTransaction);
                hideFragment(mHomeFragment, fragmentTransaction);
                if (mMineFragment == null) {
                    mMineFragment = new MineFragment();
                    fragmentTransaction.add(R.id.content_layout, mMineFragment);
                } else {
                    mCurrent = mMineFragment;
                    fragmentTransaction.show(mMineFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    /**
     * 隐藏Fragment
     *
     * @param fragment
     * @param ft
     */
    private void hideFragment(Fragment fragment, FragmentTransaction ft) {
        if (null != fragment) {
            ft.hide(fragment);
        }
    }
}
