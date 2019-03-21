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
		//ִ���첽����
		MyTask task = new MyTask();
		task.execute("��һ������","�ڶ�������","����������");//�����Ǵ���doInBackground()��;
	}
	
	class MyTask extends AsyncTask<String, Integer, String>{
		private ProgressDialog dialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// ��ʼ��,���ؽ���
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.show();
		}
		/**
		 * @param �����ɷ��͵�һ����������  <�ɱ����>
		 * Ҫ��������onProgressUpdate(�ڶ���������������),��Ҫ����publishProgress(�ڶ���������������);<�ɱ����>
		 * @return �����ɷ��͵�������������,����ֵ���ݸ� onPostExecute(���͵�������������);
		 */
		@Override
		protected String doInBackground(String... params) {
			//���߳�ִ�к�ʱ����
			System.out.println("doInBackground params[0]:"+params[0]+params[1]+params[2]);
			for (int i = 1; i <= 10; i++) {
				System.out.println("doInBackground:"+Thread.currentThread().getName()+i);
				SystemClock.sleep(1000);
				publishProgress(i,10);//���÷���onProgressUpdate���½���
			}
			
			return "����doInBackground�ķ���ֵ,��Ӧ���͵���������";										
		}
	
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			dialog.setMax(values[1]);
			dialog.setProgress(values[0]);
		}
		@Override
		protected void onPostExecute(String result) {	//����doInBackground�������Ĳ���
			System.out.println("onPostExecute result:"+result);
			super.onPostExecute(result);
			dialog.dismiss();
		}
	}

}
