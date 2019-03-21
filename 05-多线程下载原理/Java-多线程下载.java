package com.itheima.Downloads;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.RandomAccess;

public class Downloads {
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//创建网络连接。
		String path = "http://192.168.1.112:8080/news/a.jpg";
		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		if(connection.getResponseCode()==200){
			//下载之前先在本地创建和目标文件相同大小的空白文件。所以要获得目标文件的大小。
			int length = connection.getContentLength();
			//设置下载到本地文件的位置和大小。
			RandomAccessFile raf = new RandomAccessFile("F:\\temp\\copy.jpg", "rw");
			raf.setLength(length);
			raf.close();
			//空白固定大小的文件已经设置好了，可以开始设置线程下载了。
			//假设3个线程下载,搞清楚每个线程下载的长度。假设长度为10b，那么就是从索引0-9b.
			//线程0 0-2b, 线程1 3-5b,线程6-9b
			for(int i = 0;i<3;i++){
				int start = i*(length/3);//(length/3)是平均每个线程下载的长度 *i是起始 *（i+1)是结束。。
				int end = (i+1)*(length/3)-1;
				//最后一个线程的结束位置要再设置一下。
				if(i==2){
					end = length-1;
				}
				//将参数传给多线程。
				new MyThread(start,end,i).start();
				
			}
			
			
		}

	}
	
	static class MyThread extends Thread{
		//定义三个变量。根据变量条件来下载。
		private int start;
		private int end;
		private int id;
		
		
		
		public MyThread(int start, int end, int id) {
			super();
			this.start = start;
			this.end = end;
			this.id = id;
		}



		public void run(){
			//向网络请求，下载需求的资源
			try {
				//熟悉的网络请求格式
				URL url = new URL("http://192.168.1.112:8080/news/a.jpg");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				//不同的地方在于，请求指定长度的数据
				connection.setRequestProperty("Range", "bytes="+start+"-"+end);
				if(connection.getResponseCode()==206){
					//可以开始读取和存入数据了
					InputStream is = connection.getInputStream();
					//还是使用RandomAccessFile存入有一个实时刷新的功能 rwd
					RandomAccessFile raf = new RandomAccessFile("F:\\temp\\copy.jpg", "rwd");
					//!!定位存储的位置！！
					raf.seek(start);
					byte[] by = new byte[1024];
					int len;
					while((len=is.read(by))!=-1){
						raf.write(by,0,len);
					}
					is.close();
					raf.close();
					System.out.println("线程"+id+"下载了"+start+"--"+end);
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

}
