package com.ivan.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


/**
 * Copyright (C), 2018-2018, 912租房网
 * Author: 张一帆
 * Date: 2018/12/6 0006 上午 10:07
 * Description:
 */

public abstract class BaseActivity extends AppCompatActivity {
    public static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeSetContentView();
        setContentView(setLayoutId());
        initView(savedInstanceState);
        initData();
    }

    public void beforeSetContentView() {
    }

    public abstract int setLayoutId();

    public abstract void initView(Bundle savedInstanceState);

    public abstract void initData();
}