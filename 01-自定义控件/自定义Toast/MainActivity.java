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
 * Toast�ص� ������ʾ����Ļ����ҳ���Ϸ�. ����:�����������ʾ
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
	 * ��ʾ�Զ�����˾
	 * @param v
	 */
	public void show(View v) {
		/**
		 *  1.��ȡ���������
		 */
		wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		/**
		 *  3.����Զ��岼��
		 */
		view = View.inflate(this, R.layout.toast_show, null);
		//Ѱ�Ҳ����еĿؼ�,������ʾ����
		TextView tv = (TextView) view.findViewById(R.id.textView1);
		tv.setText("�����Զ����Toast...");
		/**
		 *  4.���ô������
		 */
		LayoutParams params = new LayoutParams();
		params.height = LayoutParams.WRAP_CONTENT;
		params.width = LayoutParams.WRAP_CONTENT;
		//��������
		params.flags = LayoutParams.FLAG_KEEP_SCREEN_ON 
				|LayoutParams.FLAG_NOT_FOCUSABLE
				|LayoutParams.FLAG_NOT_TOUCHABLE;
		//���ظ�ʽ,���Ǳ���ɫ
		params.format = PixelFormat.TRANSLUCENT;
		//������ʽ-Toast
		params.type = LayoutParams.TYPE_TOAST;
		/**
		 *  2.��ʾ
		 */
		wm.addView(view, params);
		
	}

	/**
	 * �����Զ�����˾
	 * 
	 * @param v
	 */
	public void hide(View v) {
		if(view!=null && view.getParent() != null){
			wm.removeView(view);
		}
	}

}
