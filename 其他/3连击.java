package com.itheima.three;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button bt;
	long[] mHits = new long[2];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bt = (Button) findViewById(R.id.bt);
		// �����¼�
		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//src������Դ����.
				//srcPos ��Դ������ĸ�λ�ÿ�ʼ����
				//dst �����ݿ���������ȥ
				//dstPos ��Ŀ��������ĸ�λ�ÿ�ʼ����.
				//length ���������ݵĳ���
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					Toast.makeText(getApplicationContext(), "������", 0).show();
				}
			}
		});
	}
}
