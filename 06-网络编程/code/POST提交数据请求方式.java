package com.itheima.qqlogin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String Tag = "MainActivity";
	private EditText et_qq;
	private EditText et_pwd;
	private CheckBox cb_remember;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//��ѯ���ĵĿؼ�
		et_qq = (EditText) findViewById(R.id.et_qq);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		cb_remember = (CheckBox) findViewById(R.id.cb_remember);
		Log.i(Tag,"oncreate ������");
		//������ݵĻ��ԡ�
		readSavedData();
	}
	//��ȡ���������
	private void readSavedData() {
		// getFilesDir() == /data/data/����/files/  ��ȡ�ļ���·�� һ��ϵͳ�ǲ�������ġ� �û��ֹ�����ϵͳ������ʾ��
		// getCacheDir()==  /data/data/����/cache/ �����ļ���·�� ��ϵͳ�ڴ����ز����ʱ�� ϵͳ���Զ���������� �û��ֹ�����ϵͳû����ʾ
		File file = new File(getFilesDir(),"info.txt");
		if(file.exists()&&file.length()>0){
			try {
				//FileInputStream fis = new FileInputStream(file);
				FileInputStream fis =this.openFileInput("info.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				//214342###abcdef
				String info = br.readLine();
				String qq = info.split("###")[0];
				String pwd = info.split("###")[1];
				et_qq.setText(qq);
				et_pwd.setText(pwd);
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * ��½��ť�ĵ���¼�,�ڵ���¼������ȡ����
	 * @param view
	 */
	public void login(View view){
		final String qq = et_qq.getText().toString().trim();
		final String pwd = et_pwd.getText().toString().trim();
		if(TextUtils.isEmpty(qq)||TextUtils.isEmpty(pwd)){
			Toast.makeText(this, "qq����������벻��Ϊ��", 0).show();
			return;
		}
		//�ж��û��Ƿ�ѡ��ס���롣
		if(cb_remember.isChecked()){
			//��������
			Log.i(Tag,"��������");
			try {
//				File file = new File(getFilesDir(),"info.txt");
//				FileOutputStream fos = new FileOutputStream(file);
				FileOutputStream fos = this.openFileOutput("info.txt", 0);
				//214342###abcdef
				fos.write((qq+"###"+pwd).getBytes());
				fos.close();
				Toast.makeText(this, "����ɹ�", 0).show();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, "����ʧ��", 0).show();
			}
		}else{
			//���豣������
			Log.i(Tag,"���豣������");
		}
		//��½�Ĳ�������������
		new Thread(){
			public void run() {
				try {
					//post�����ύ����
					String path = getString(R.string.serverip);
					URL url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					//��Ҫ���ǵ���������ʽpost
					conn.setRequestMethod("POST");
					//��Ҫ���ǵ��������ݵ�����
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					String data = "qq="+URLEncoder.encode(qq, "utf-8")+"&password="+URLEncoder.encode(pwd, "utf-8");
					//��Ҫ���ǵ��������ݵĳ���
					conn.setRequestProperty("Content-Length", String.valueOf(data.length()));
					//��Ҫ���ǵø�������д����
					conn.setDoOutput(true);//����Ҫ��������д����
					//��Ҫ��������д����������
					conn.getOutputStream().write(data.getBytes());
					
					int code = conn.getResponseCode();
					if(code == 200){
						InputStream is = conn.getInputStream();
						String result = StreamTools.readFromStream(is);
						showToastInAnyThread(result);
					}else{
						showToastInAnyThread("����ʧ�ܣ�"+code);
					}
				} catch (Exception e) {
					e.printStackTrace();
					showToastInAnyThread("����ʧ��");
				}
			};
		}.start();
	}
	
	
	/**
	 * ��ʾ��˾
	 * @param text
	 */
	public void showToastInAnyThread(final String text){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, text, 0).show();
			}
		});
	}
	
}
