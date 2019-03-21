package com.itheima.myToast;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

/**
 * Toast特点 可以显示在屏幕任意活动页面上方. 适用:来电归属地显示
 * 
 * @author Administrator
 * 
 */
public class MainActivity extends Activity {

	private WindowManager wm;
	private View view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	/**
	 * 显示自定义吐司
	 * @param v
	 */
	public void show(View v) {
		/**
		 *  1.获取窗体管理器
		 */
		wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		/**
		 *  3.填充自定义布局
		 */
		view = View.inflate(this, R.layout.toast_show, null);
		//寻找布局中的控件,设置显示内容
		TextView tv = (TextView) view.findViewById(R.id.textView1);
		tv.setText("我是自定义的Toast...");
		/**
		 *  4.设置窗体参数
		 */
		LayoutParams params = new LayoutParams();
		params.height = LayoutParams.WRAP_CONTENT;
		params.width = LayoutParams.WRAP_CONTENT;
		//窗体特性
		params.flags = LayoutParams.FLAG_KEEP_SCREEN_ON 
				|LayoutParams.FLAG_NOT_FOCUSABLE
				|LayoutParams.FLAG_NOT_TOUCHABLE;
		//像素格式,不是背景色
		params.format = PixelFormat.TRANSLUCENT;
		//窗体样式-Toast
		params.type = LayoutParams.TYPE_TOAST;
		/**
		 *  2.显示
		 */
		wm.addView(view, params);
		
	}

	/**
	 * 隐藏自定义吐司
	 * 
	 * @param v
	 */
	public void hide(View v) {
		if(view!=null && view.getParent() != null){
			wm.removeView(view);
		}
	}

}
