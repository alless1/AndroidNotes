package com.alless.reader.widget;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;

import com.alless.reader.demo.R;
import com.alless.reader.utils.DPUtils;

/**
 * Author:chengjie
 * Date:2018/7/10
 * Description:
 */
public class PopupMenuHelper implements View.OnClickListener {

    private final PopupWindow popupMenu;
    private View mTop_layout;
    private View mBottom_layout;
    private Activity mActivity;
    private View mAttachLayout;
    private PopupMenuListener mListener;
    private final static int DURATION_TIME = 200;

    public PopupMenuHelper(Activity activity, View attachLayout, PopupMenuListener listener) {
        mActivity = activity;
        mAttachLayout = attachLayout;
        mListener = listener;
        //初始化
        View view = View.inflate(activity, R.layout.popup_menu_helper, null);
        initView(view);
        popupMenu = new PopupWindow(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        popupMenu.setContentView(view);
        popupMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupMenu.setFocusable(true);
    }

    private void initView(View view) {
        mBottom_layout = view.findViewById(R.id.bottom_layout);
        mTop_layout = view.findViewById(R.id.top_layout);

        view.findViewById(R.id.tv_back).setOnClickListener(this);
        view.findViewById(R.id.tv_pre).setOnClickListener(this);
        view.findViewById(R.id.tv_next).setOnClickListener(this);
        view.findViewById(R.id.main_layout).setOnClickListener(this);
        view.findViewById(R.id.tv_search).setOnClickListener(this);
        view.findViewById(R.id.tv_directory).setOnClickListener(this);
        view.findViewById(R.id.tv_rotate).setOnClickListener(this);
        view.findViewById(R.id.tv_brightness).setOnClickListener(this);
        view.findViewById(R.id.tv_font_size).setOnClickListener(this);

    }


    public void show() {
        if (popupMenu.isShowing()) {
            popupMenu.dismiss();
        } else {
            int topViewHeight = DPUtils.dip2px(mActivity, 60);//根据布局
            int bottomViewHeight = DPUtils.dip2px(mActivity, 80);
            startAnimation(mTop_layout, 0, 0, -topViewHeight, 0, DURATION_TIME);
            startAnimation(mBottom_layout, 0, 0, bottomViewHeight, 0, DURATION_TIME);//动画是相对整体的
            popupMenu.showAtLocation(mAttachLayout, Gravity.LEFT | Gravity.TOP, 0, 0);
        }
    }

    public void hide() {
        if (popupMenu.isShowing()) {
            int topViewHeight = DPUtils.dip2px(mActivity, 60);//根据布局
            int bottomViewHeight = DPUtils.dip2px(mActivity, 80);
            startAnimation(mTop_layout, 0, 0, 0, -topViewHeight, DURATION_TIME);
            startAnimation(mBottom_layout, 0, 0, 0, bottomViewHeight, DURATION_TIME, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    popupMenu.dismiss();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
    }


    private static final String TAG = "PopupMenuHelper";

    @Override
    public void onClick(View v) {
        hide();
        switch (v.getId()) {
            case R.id.tv_back:
                mListener.onBack();
                break;
            case R.id.tv_search:
                mListener.onSearch();
                break;
            case R.id.tv_pre:
                mListener.onPreChapter();
                break;
            case R.id.tv_directory:
                mListener.onOpenDirectory();
                break;
            case R.id.tv_rotate:
                mListener.onScreenRotate();
                break;
            case R.id.tv_brightness:
                mListener.onBrightness();
                break;
            case R.id.tv_font_size:
                mListener.onFontSize();
                break;
            case R.id.tv_next:
                mListener.onNextChapter();
                break;
            case R.id.main_layout:

                break;
        }
    }

    private void startAnimation(View view, float fromX, float toX, float fromY,
                                float toY, long duration) {
        TranslateAnimation animation = new TranslateAnimation(fromX, toX,
                fromY, toY);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    private void startAnimation(View view, float fromX, float toX, float fromY,
                                float toY, long duration, Animation.AnimationListener listener) {
        TranslateAnimation animation = new TranslateAnimation(fromX, toX,
                fromY, toY);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(duration);
        view.startAnimation(animation);
        animation.setAnimationListener(listener);
    }


    public interface PopupMenuListener {
        void onBack();

        void onSearch();

        void onPreChapter();

        void onNextChapter();

        void onOpenDirectory();

        void onScreenRotate();

        void onBrightness();

        void onFontSize();
    }
}
