package com.itheima.Downloads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
//断点下载，在多线程下载的基础上，对每一个线程建一个状态文件，记录下载位置信息，随时更新。
public class Downloads {
	public static int count = 3;
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//创建网络连接。
		String path = "http://192.168.1.112:8080/news/G.mp3";
		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		if(connection.getResponseCode()==200){
			//下载之前先在本地创建和目标文件相同大小的空白文件。所以要获得目标文件的大小。
			int length = connection.getContentLength();
			//设置下载到本地文件的位置和大小。
			RandomAccessFile raf = new RandomAccessFile("F:\\temp\\copy.mp3", "rw");
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
		//定义变量记录断点下载
		private int nearId;
		
		
		public MyThread(int start, int end, int id) {
			super();
			this.start = start;
			this.end = end;
			this.id = id;
			//初始化的时候断点位置为起始位置
			nearId = start;
		}



		public void run(){
			//向网络请求，下载需求的资源
			try {
				//先建一个断点记录文件
				File file = new File("F:\\temp\\"+id+".txt");
				//如果断点记录有内容，就取出来。读取内容，赋值给start.
				if(file.exists()&&file.length()>0){
					BufferedReader br = new BufferedReader(new FileReader(file));
					start=Integer.parseInt(br.readLine());
					br.close();
				}
				
				//熟悉的网络请求格式
				URL url = new URL("http://192.168.1.112:8080/news/G.mp3");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				//不同的地方在于，请求指定长度的数据
				connection.setRequestProperty("Range", "bytes="+start+"-"+end);
				if(connection.getResponseCode()==206){
					//可以开始读取和存入数据了
					InputStream is = connection.getInputStream();
					//还是使用RandomAccessFile存入有一个实时刷新的功能 rwd
					RandomAccessFile raf = new RandomAccessFile("F:\\temp\\copy.mp3", "rwd");
					//!!定位存储的位置！！
					raf.seek(start);
					byte[] by = new byte[1024];
					int len;
					while((len=is.read(by))!=-1){
						raf.write(by,0,len);
						//断点记录文件写入，还是使用RandomAccessFile存入有一个实时刷新 rwd
						nearId +=len;
						RandomAccessFile ra = new RandomAccessFile(file, "rwd");
						ra.write(String.valueOf(nearId).getBytes());
						ra.close();
					}
					is.close();
					raf.close();
					System.out.println("线程"+id+"下载了"+start+"--"+end);
					//所有线程下载完了，把记录文件删除掉。
					//synchronized(Downloads.class){
						count--;
						if(count==0){
							for(int i=0;i<3;i++){
								File f = new File("F:\\temp\\"+i+".txt");
								f.delete();
							}
						}
						
					//}
					
					
					
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

}
