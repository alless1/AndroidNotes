package com.itheima.pull;
import org.xmlpull.v1.XmlPullParser;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//pull������XmlSerializer���л����෴�ġ�
		//����������
		XmlPullParser pull = Xml.newPullParser();
		try {
			//��ʼ�����������������ʽ���������������ʲ�Ŀ¼�µ��ļ���
			pull.setInput(this.getAssets().open("001.xml"),"utf-8");
			//��ʼ����,��ȡָ��λ��,�ļ�ͷ��λ��,type = 0;
			int type = pull.getEventType();
			//ѭ��������ָ�벻���ĵ�����λ�á�
			while(type!=pull.END_DOCUMENT){
				//�ж�ָ��λ�ã��Ƿ��ǿ�ʼ��ǩλ��
				if(type==pull.START_TAG){
					//�����ж��Ƿ�������Ŀ�ʼ��ǩ��
					//getName()��ȡ��ǰ��ǩ����
					//nextText()��ȡ��ǩ�е����ݣ����ң����������ı��ƶ���������ǩλ�á�
					if("name".equals(pull.getName())){
						Log.d("tag","uid:"+pull.getAttributeValue(null,"uid"));
						Log.d("tag", "name:"+pull.nextText());
					}else if("age".equals(pull.getName())){
						Log.d("tag", "age:"+pull.nextText());
					}else if("gender".equals(pull.getName())){
						Log.d("tag", "gender:"+pull.nextText());
					}
				}
				//�ƶ�ָ��
				type = pull.next();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
