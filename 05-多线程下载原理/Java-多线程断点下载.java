package com.itheima.Downloads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
//�ϵ����أ��ڶ��߳����صĻ����ϣ���ÿһ���߳̽�һ��״̬�ļ�����¼����λ����Ϣ����ʱ���¡�
public class Downloads {
	public static int count = 3;
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//�����������ӡ�
		String path = "http://192.168.1.112:8080/news/G.mp3";
		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		if(connection.getResponseCode()==200){
			//����֮ǰ���ڱ��ش�����Ŀ���ļ���ͬ��С�Ŀհ��ļ�������Ҫ���Ŀ���ļ��Ĵ�С��
			int length = connection.getContentLength();
			//�������ص������ļ���λ�úʹ�С��
			RandomAccessFile raf = new RandomAccessFile("F:\\temp\\copy.mp3", "rw");
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
		//���������¼�ϵ�����
		private int nearId;
		
		
		public MyThread(int start, int end, int id) {
			super();
			this.start = start;
			this.end = end;
			this.id = id;
			//��ʼ����ʱ��ϵ�λ��Ϊ��ʼλ��
			nearId = start;
		}



		public void run(){
			//���������������������Դ
			try {
				//�Ƚ�һ���ϵ��¼�ļ�
				File file = new File("F:\\temp\\"+id+".txt");
				//����ϵ��¼�����ݣ���ȡ��������ȡ���ݣ���ֵ��start.
				if(file.exists()&&file.length()>0){
					BufferedReader br = new BufferedReader(new FileReader(file));
					start=Integer.parseInt(br.readLine());
					br.close();
				}
				
				//��Ϥ�����������ʽ
				URL url = new URL("http://192.168.1.112:8080/news/G.mp3");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				//��ͬ�ĵط����ڣ�����ָ�����ȵ�����
				connection.setRequestProperty("Range", "bytes="+start+"-"+end);
				if(connection.getResponseCode()==206){
					//���Կ�ʼ��ȡ�ʹ���������
					InputStream is = connection.getInputStream();
					//����ʹ��RandomAccessFile������һ��ʵʱˢ�µĹ��� rwd
					RandomAccessFile raf = new RandomAccessFile("F:\\temp\\copy.mp3", "rwd");
					//!!��λ�洢��λ�ã���
					raf.seek(start);
					byte[] by = new byte[1024];
					int len;
					while((len=is.read(by))!=-1){
						raf.write(by,0,len);
						//�ϵ��¼�ļ�д�룬����ʹ��RandomAccessFile������һ��ʵʱˢ�� rwd
						nearId +=len;
						RandomAccessFile ra = new RandomAccessFile(file, "rwd");
						ra.write(String.valueOf(nearId).getBytes());
						ra.close();
					}
					is.close();
					raf.close();
					System.out.println("�߳�"+id+"������"+start+"--"+end);
					//�����߳��������ˣ��Ѽ�¼�ļ�ɾ������
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
