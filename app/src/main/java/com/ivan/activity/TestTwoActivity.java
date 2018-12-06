package com.ivan.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.app.Activity;
import android.widget.FrameLayout;

import com.ivan.R;

/**
 * Copyright (C), 2018-2018, 912租房网
 * Author: 张一帆
 * Date: 2018/12/6 0006 下午 4:13
 * Description:
 */

public class TestTwoActivity extends Activity {

    private float totalHeight;      //总高度
    private float toolBarHeight;    //toolBar高度
    private float offSetHeight;     //总高度 -  toolBar高度  布局位移值
    private float llHeight;         //搜索框高度

    private float llHeightOffScale;     //高度差比值
    private float llOffDistance;        //距离差
    private float llOffDistanceScale;   //距离差比值
    private FrameLayout.LayoutParams params;

    Toolbar toolbar;
    CollapsingToolbarLayout toolbarLayout;
    AppBarLayout appBar;
    EditText fab;
    RelativeLayout ll;
    TextView bac;
    FrameLayout fl;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_two);

        toolbar = findViewById(R.id.toolbar);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        appBar = findViewById(R.id.app_bar);
        fab = findViewById(R.id.fab);
        ll = findViewById(R.id.ll);
        bac = findViewById(R.id.bac);
        fl = findViewById(R.id.fl);
        image = findViewById(R.id.image);

        bac.setAlpha(0f);
        totalHeight = getResources().getDimension(R.dimen.app_bar_height);
        toolBarHeight = getResources().getDimension(R.dimen.tool_bar_height);
        offSetHeight = totalHeight - toolBarHeight;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(TestTwoActivity.this, TestActivity.class));
            }
        });

        /**
         *   移动效果值／最终效果值 =  移动距离／ 能移动总距离（确定）
         */
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                //第一次进入获取高度，以及差值 高度差比值
                if (llHeight == 0) {
                    llHeight = ll.getMeasuredHeight();
                    params = (FrameLayout.LayoutParams) ll.getLayoutParams();

                    //算出高度偏移量比值  相对与llHeight
                    llHeightOffScale = 1.0f - (toolBarHeight / llHeight);

                    //得到滑动差值 就是布局marginTop
                    llOffDistance = params.topMargin;
                    //得到滑动比值
                    llOffDistanceScale = llOffDistance / offSetHeight;
                }

                //滑动一次 得到渐变缩放值
                float alphaScale = (-verticalOffset) / offSetHeight;

                //获取高度缩放值
                float llHeightScale = 1.0f - (llHeightOffScale * ((-verticalOffset) / offSetHeight));
                //计算maigintop值
                float distance = llOffDistance - (-verticalOffset) * llOffDistanceScale;

                image.setAlpha(1.0f - alphaScale);
                bac.setAlpha(alphaScale);
                params.height = (int) (llHeight * llHeightScale);
                params.setMargins(0, (int) distance, 0, 0);

                fl.requestLayout();


            }
        });
    }
}
