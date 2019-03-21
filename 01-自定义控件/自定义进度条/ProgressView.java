package com.heima.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heima.mobilesafe.R;

public class ProgressView extends LinearLayout {
	private TextView mTvTitle;
	private TextView mTvLeft;
	private TextView mTvRight;
	public ProgressView(Context context) {
		super(context, null);
	}
	public ProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 1.填充控件,和一般界面加载布局不一样,控件以自身布局ViewGroup为父类填充.
		View.inflate(context, R.layout.progressbar_view, this);
		// 2.寻找自定义控件的子控件.(就可以在代码中对他们进行赋值,显示)
		mTvTitle = (TextView) findViewById(R.id.tv_pro_title);
		mTvLeft = (TextView) findViewById(R.id.tv_pro_left);
		mTvRight = (TextView) findViewById(R.id.tv_pro_right);
		/**
		 * 3.寻找自定义View中需要设置的属性值.(是为了让xml配置中设置的参数,显示到屏幕上) 有两种方式
		 */
		// (1)获取属性集(对应的是arrts文件里面的),方法参数eclipse没有明确提示,注意R资源的写法.
		/*
		 * TypedArray ta = context.obtainStyledAttributes(attrs,
		 * R.styleable.ProgressStatusView); String title =
		 * ta.getString(R.styleable.ProgressStatusView_psvTitle); String left =
		 * ta.getString(R.styleable.ProgressStatusView_psvLeft); String right =
		 * ta.getString(R.styleable.ProgressStatusView_psvRight);
		 */
		// (2)直接获取,需要命名空间的参数.
		String title = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.heima.mobilesafe",
				"psvTitle");
		String left = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.heima.mobilesafe",
				"psvLeft");
		String right = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.heima.mobilesafe",
				"psvRight");

		// 4.把xml文件中设置的属性和子控件关联,显示出来.
		mTvTitle.setText(title);
		mTvLeft.setText(title);
		mTvRight.setText(title);

	}

	// 5.为了在代码里也能设置控件显示结果,给出设置方法.
	public void setTitle(String title) {
		mTvTitle.setText(title);
	}
	public void setLeft(String left) {
		mTvLeft.setText(left);
	}
	public void setRight(String right) {
		mTvRight.setText(right);
	}

}
