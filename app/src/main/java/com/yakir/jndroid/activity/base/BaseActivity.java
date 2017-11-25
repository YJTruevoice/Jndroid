package com.yakir.jndroid.activity.base;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yakir.jndroid.R;

/**
 * @author yakir
 * @function 为所有Activity提供公共行为
 */
public class BaseActivity extends AppCompatActivity {

    public static String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getComponentName().getClassName();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
