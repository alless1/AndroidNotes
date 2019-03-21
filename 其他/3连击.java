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
		// 三击事件
		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//src拷贝的源数组.
				//srcPos 从源数组的哪个位置开始拷贝
				//dst 把数据拷贝到哪里去
				//dstPos 从目标数组的哪个位置开始拷贝.
				//length 拷贝的数据的长度
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					Toast.makeText(getApplicationContext(), "三击了", 0).show();
				}
			}
		});
	}
}
