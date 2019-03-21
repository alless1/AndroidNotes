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
		//查询关心的控件
		et_qq = (EditText) findViewById(R.id.et_qq);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		cb_remember = (CheckBox) findViewById(R.id.cb_remember);
		Log.i(Tag,"oncreate 被调用");
		//完成数据的回显。
		readSavedData();
	}
	//读取保存的数据
	private void readSavedData() {
		// getFilesDir() == /data/data/包名/files/  获取文件的路径 一般系统是不会清理的。 用户手工清理，系统会有提示。
		// getCacheDir()==  /data/data/包名/cache/ 缓存文件的路径 当系统内存严重不足的时候 系统会自动的清除缓存 用户手工清理系统没有提示
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
	 * 登陆按钮的点击事件,在点击事件里面获取数据
	 * @param view
	 */
	public void login(View view){
		final String qq = et_qq.getText().toString().trim();
		final String pwd = et_pwd.getText().toString().trim();
		if(TextUtils.isEmpty(qq)||TextUtils.isEmpty(pwd)){
			Toast.makeText(this, "qq号码或者密码不能为空", 0).show();
			return;
		}
		//判断用户是否勾选记住密码。
		if(cb_remember.isChecked()){
			//保存密码
			Log.i(Tag,"保存密码");
			try {
//				File file = new File(getFilesDir(),"info.txt");
//				FileOutputStream fos = new FileOutputStream(file);
				FileOutputStream fos = this.openFileOutput("info.txt", 0);
				//214342###abcdef
				fos.write((qq+"###"+pwd).getBytes());
				fos.close();
				Toast.makeText(this, "保存成功", 0).show();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, "保存失败", 0).show();
			}
		}else{
			//无需保存密码
			Log.i(Tag,"无需保存密码");
		}
		//登陆的操作，网络请求
		new Thread(){
			public void run() {
				try {
					//post请求提交数据
					String path = getString(R.string.serverip);
					URL url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					//重要：记得设置请求方式post
					conn.setRequestMethod("POST");
					//重要：记得设置数据的类型
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					String data = "qq="+URLEncoder.encode(qq, "utf-8")+"&password="+URLEncoder.encode(pwd, "utf-8");
					//重要，记得设置数据的长度
					conn.setRequestProperty("Content-Length", String.valueOf(data.length()));
					//重要，记得给服务器写数据
					conn.setDoOutput(true);//声明要给服务器写数据
					//重要，把数据写给服务器。
					conn.getOutputStream().write(data.getBytes());
					
					int code = conn.getResponseCode();
					if(code == 200){
						InputStream is = conn.getInputStream();
						String result = StreamTools.readFromStream(is);
						showToastInAnyThread(result);
					}else{
						showToastInAnyThread("请求失败："+code);
					}
				} catch (Exception e) {
					e.printStackTrace();
					showToastInAnyThread("请求失败");
				}
			};
		}.start();
	}
	
	
	/**
	 * 显示土司
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
