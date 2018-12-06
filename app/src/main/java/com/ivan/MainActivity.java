package com.ivan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.ivan.activity.ScrollingActivity;
import com.ivan.activity.TestOneActivity;
import com.ivan.adapter.ComFragmentAdapter;
import com.ivan.fragment.ArticleFragment;
import com.ivan.fragment.DynamicFragment;
import com.ivan.fragment.QuestionFragment;
import com.ivan.utils.ScreenUtil;
import com.ivan.utils.StatusBarUtil;
import com.ivan.view.ColorFlipPagerTitleView;
import com.ivan.view.JudgeNestedScrollView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ivan.activity.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();


    ImageView ivBack;
    ImageView ivMenu;

    ImageView ivHeader;
    SmartRefreshLayout refreshLayout;
    Toolbar toolbar;
    ViewPager viewPager;
    JudgeNestedScrollView scrollView;
    ButtonBarLayout buttonBarLayout;
    MagicIndicator magicIndicator;
    MagicIndicator magicIndicatorTitle;
    int toolBarPositionY = 0;
    private int mOffset = 0;
    private int mScrollY = 0;
    private String[] mTitles = new String[]{"动态", "文章", "问答"};
    private List<String> mDataList = Arrays.asList(mTitles);
//    private ContentObserver mNavigationStatusObserver = new ContentObserver(new Handler()) {
//        @Override
//        public void onChange(boolean selfChange) {
//            dealWithHuaWei();
//            int navigationBarIsMin = Settings.System.getInt(getContentResolver(), "navigationbar_is_min", 0);
//            if (navigationBarIsMin == 1) {
//                Log.d(TAG, "onChange: ------------------导航键隐藏了");
//            } else {
//                Log.d(TAG, "onChange: ------------------导航键显示了");
//            }
//        }
//    };

    @Override
    public int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        ivBack = findViewById(R.id.iv_back);
        toolbar = findViewById(R.id.toolbar);
        ivMenu = findViewById(R.id.iv_menu);
        ivHeader = findViewById(R.id.iv_header);
        refreshLayout = findViewById(R.id.refreshLayout);
        viewPager = findViewById(R.id.view_pager);
        scrollView = findViewById(R.id.scrollView);
        buttonBarLayout = findViewById(R.id.buttonBarLayout);
        magicIndicator = findViewById(R.id.magic_indicator);
        magicIndicatorTitle = findViewById(R.id.magic_indicator_title);
        initView();
    }

    @Override
    public void initData() {
        findViewById(R.id.tv_username).setOnClickListener(this);
        findViewById(R.id.iv_avatar).setOnClickListener(this);
        findViewById(R.id.tv_authentication).setOnClickListener(this);
        findViewById(R.id.tv_edit_info).setOnClickListener(this);
        findViewById(R.id.tv_position).setOnClickListener(this);
    }

    private void initView() {

        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, toolbar);
        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderPulling(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset / 2;
                ivHeader.setTranslationY(mOffset - mScrollY);
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }

            @Override
            public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int bottomHeight, int extendHeight) {
                mOffset = offset / 2;
                ivHeader.setTranslationY(mOffset - mScrollY);
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }
        });

        //判断是否是华为手机并且是否有虚拟导航键
//        if (DeviceUtil.isHUAWEI() && DeviceUtil.checkDeviceHasNavigationBar(this.getApplicationContext())) {
//            getContentResolver().registerContentObserver(Settings.System.getUriFor
//                    ("navigationbar_is_min"), true, mNavigationStatusObserver);
//        }
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                dealWithViewPager();
            }
        });
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            int lastScrollY = 0;
            int h = DensityUtil.dp2px(170);
            int color = ContextCompat.getColor(getApplicationContext(), R.color.mainWhite) & 0x00ffffff;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int[] location = new int[2];
                magicIndicator.getLocationOnScreen(location);
                int yPosition = location[1];
                if (yPosition < toolBarPositionY) {
                    magicIndicatorTitle.setVisibility(View.VISIBLE);
                    scrollView.setNeedScroll(false);
                } else {
                    magicIndicatorTitle.setVisibility(View.GONE);
                    scrollView.setNeedScroll(true);

                }

                if (lastScrollY < h) {
                    scrollY = Math.min(h, scrollY);
                    mScrollY = scrollY > h ? h : scrollY;
                    buttonBarLayout.setAlpha(1f * mScrollY / h);
                    toolbar.setBackgroundColor(((255 * mScrollY / h) << 24) | color);
                    ivHeader.setTranslationY(mOffset - mScrollY);
                }
                if (scrollY == 0) {
                    ivBack.setImageResource(R.drawable.back_white);
                    ivMenu.setImageResource(R.drawable.icon_menu_white);
                } else {
                    ivBack.setImageResource(R.drawable.back_black);
                    ivMenu.setImageResource(R.drawable.icon_menu_black);
                }

                lastScrollY = scrollY;
            }
        });
        buttonBarLayout.setAlpha(0);
        toolbar.setBackgroundColor(0);


        viewPager.setAdapter(new ComFragmentAdapter(getSupportFragmentManager(), getFragments()));
        viewPager.setOffscreenPageLimit(10);
        initMagicIndicator();
        initMagicIndicatorTitle();
    }

    private void dealWithViewPager() {
        toolBarPositionY = toolbar.getHeight();
        ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        params.height = ScreenUtil.getScreenHeightPx(getApplicationContext()) - toolBarPositionY - magicIndicator.getHeight() + 1;
        viewPager.setLayoutParams(params);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(DynamicFragment.getInstance());
        fragments.add(ArticleFragment.getInstance());
        fragments.add(QuestionFragment.getInstance());
        return fragments;
    }

    private void initMagicIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.65f);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorFlipPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(MainActivity.this, R.color.mainBlack));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(MainActivity.this, R.color.mainBlack));
                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index, false);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 2));
                indicator.setLineWidth(UIUtil.dip2px(context, 20));
                indicator.setRoundRadius(UIUtil.dip2px(context, 3));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(ContextCompat.getColor(MainActivity.this, R.color.mainRed));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    private void initMagicIndicatorTitle() {
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.65f);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorFlipPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(MainActivity.this, R.color.mainBlack));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(MainActivity.this, R.color.mainBlack));
                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index, false);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 2));
                indicator.setLineWidth(UIUtil.dip2px(context, 20));
                indicator.setRoundRadius(UIUtil.dip2px(context, 3));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(ContextCompat.getColor(MainActivity.this, R.color.mainRed));
                return indicator;
            }
        });
        magicIndicatorTitle.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicatorTitle, viewPager);

    }

    /**
     * 处理华为虚拟键显示隐藏问题导致屏幕高度变化，ViewPager的高度也需要重新测量
     */
//    private void dealWithHuaWei() {
//        flActivity.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                dealWithViewPager();
//                flActivity.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            }
//        });
//    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_username:
                toast("SiberiaDante");
                break;
            case R.id.iv_avatar:
                toast("头像");
                break;
            case R.id.tv_authentication:
                Intent intent = new Intent(MainActivity.this, TestOneActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_edit_info:
                toast("编辑资料");
                break;
            case R.id.tv_position:
                toast("中国·GitHub");
                break;
        }
    }
}
