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
		//�����������ӡ�
		String path = "http://192.168.1.112:8080/news/a.jpg";
		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		if(connection.getResponseCode()==200){
			//����֮ǰ���ڱ��ش�����Ŀ���ļ���ͬ��С�Ŀհ��ļ�������Ҫ���Ŀ���ļ��Ĵ�С��
			int length = connection.getContentLength();
			//�������ص������ļ���λ�úʹ�С��
			RandomAccessFile raf = new RandomAccessFile("F:\\temp\\copy.jpg", "rw");
			raf.setLength(length);
			raf.close();
			//�հ׹̶���С���ļ��Ѿ����ú��ˣ����Կ�ʼ�����߳������ˡ�
			//����3���߳�����,�����ÿ���߳����صĳ��ȡ����賤��Ϊ10b����ô���Ǵ�����0-9b.
			//�߳�0 0-2b, �߳�1 3-5b,�߳�6-9b
			for(int i = 0;i<3;i++){
				int start = i*(length/3);//(length/3)��ƽ��ÿ���߳����صĳ��� *i����ʼ *��i+1)�ǽ�������
				int end = (i+1)*(length/3)-1;
				//���һ���̵߳Ľ���λ��Ҫ������һ�¡�
				if(i==2){
					end = length-1;
				}
				//�������������̡߳�
				new MyThread(start,end,i).start();
				
			}
			
			
		}

	}
	
	static class MyThread extends Thread{
		//�����������������ݱ������������ء�
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
			//���������������������Դ
			try {
				//��Ϥ�����������ʽ
				URL url = new URL("http://192.168.1.112:8080/news/a.jpg");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				//��ͬ�ĵط����ڣ�����ָ�����ȵ�����
				connection.setRequestProperty("Range", "bytes="+start+"-"+end);
				if(connection.getResponseCode()==206){
					//���Կ�ʼ��ȡ�ʹ���������
					InputStream is = connection.getInputStream();
					//����ʹ��RandomAccessFile������һ��ʵʱˢ�µĹ��� rwd
					RandomAccessFile raf = new RandomAccessFile("F:\\temp\\copy.jpg", "rwd");
					//!!��λ�洢��λ�ã���
					raf.seek(start);
					byte[] by = new byte[1024];
					int len;
					while((len=is.read(by))!=-1){
						raf.write(by,0,len);
					}
					is.close();
					raf.close();
					System.out.println("�߳�"+id+"������"+start+"--"+end);
				}
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

}
