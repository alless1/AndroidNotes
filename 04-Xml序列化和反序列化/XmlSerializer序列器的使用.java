package com.itheima.serializer;
import java.io.File;
import java.io.FileOutputStream;
import org.xmlpull.v1.XmlSerializer;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Toast;
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //ֱ����StringBuilderƴ��xml��ʽ�ļ������bug�������õ��˹ȸ�api XmlSerializer ���л�����
    public void createXml(View v){
    	try {
    		//��ȡ�ֻ��ļ���Ŀ¼�����������·����
			FileOutputStream fos = new FileOutputStream(new File(getFilesDir(),"001.xml"));
			//���xml���л�����
			XmlSerializer xml = Xml.newSerializer();
			//��ʽ�����л�������������������뷽ʽ��
			xml.setOutput(fos, "utf-8");
			//��ʼдxml�ļ�ͷ�����룬�Ƿ�����ļ���
			xml.startDocument("utf-8", true);
			//��ʼд����ǩ<person>����ǩ���ǶԳƵģ��п�ʼ���н����������ռ䣬û�о�дnull;
			xml.startTag(null, "person");
			//д�ӱ�ǩ<name>
			xml.startTag(null, "name");
			//д��ǩ������ uid = "9527" ���ı� ������
			xml.attribute(null, "uid", "9527");
			xml.text("����");
			xml.endTag(null, "name");
			//ͬ�� д��������ǩ�ı��� ��
			xml.startTag(null, "age");
			xml.text("23");
			xml.endTag(null, "age");
			xml.startTag(null, "gender");
			xml.text("��");
			xml.endTag(null, "gender");
			//�رո���ǩ��û�в�����
			xml.endDocument();
			//д���� ������
			fos.close();
			Toast.makeText(MainActivity.this, "������", 0);
			
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "����ʧ��", 0);
			Log.d("tag", "�����쳣"+e.getStackTrace());
			e.printStackTrace();
		}
    	
    }

    
}
