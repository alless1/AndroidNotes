package com.itheima.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	public void click(View v){
		//执行异步任务
		MyTask task = new MyTask();
		task.execute("第一个参数","第二个参数","第三个参数");//参数是传给doInBackground()的;
	}
	
	class MyTask extends AsyncTask<String, Integer, String>{
		private ProgressDialog dialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// 初始化,加载界面
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.show();
		}
		/**
		 * @param 类型由泛型第一个参数决定  <可变参数>
		 * 要传参数给onProgressUpdate(第二个参数决定类型),需要调用publishProgress(第二个参数决定类型);<可变参数>
		 * @return 类型由泛型第三个参数决定,返回值传递给 onPostExecute(泛型第三个参数类型);
		 */
		@Override
		protected String doInBackground(String... params) {
			//子线程执行耗时操作
			System.out.println("doInBackground params[0]:"+params[0]+params[1]+params[2]);
			for (int i = 1; i <= 10; i++) {
				System.out.println("doInBackground:"+Thread.currentThread().getName()+i);
				SystemClock.sleep(1000);
				publishProgress(i,10);//调用方法onProgressUpdate更新进度
			}
			
			return "我是doInBackground的返回值,对应泛型第三个参数";										
		}
	
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			dialog.setMax(values[1]);
			dialog.setProgress(values[0]);
		}
		@Override
		protected void onPostExecute(String result) {	//接收doInBackground传过来的参数
			System.out.println("onPostExecute result:"+result);
			super.onPostExecute(result);
			dialog.dismiss();
		}
	}

}
