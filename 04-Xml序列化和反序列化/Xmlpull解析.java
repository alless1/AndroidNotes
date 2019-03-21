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
		//pull解析和XmlSerializer序列化是相反的。
		//创建解析器
		XmlPullParser pull = Xml.newPullParser();
		try {
			//初始化，输入流，编码格式。这里输入流用资产目录下的文件。
			pull.setInput(this.getAssets().open("001.xml"),"utf-8");
			//开始解析,获取指针位置,文件头的位置,type = 0;
			int type = pull.getEventType();
			//循环条件，指针不在文档结束位置。
			while(type!=pull.END_DOCUMENT){
				//判断指针位置，是否是开始标签位置
				if(type==pull.START_TAG){
					//继续判断是否是需求的开始标签。
					//getName()获取当前标签名。
					//nextText()获取标签中的内容，并且，光标会跳过文本移动到结束标签位置。
					if("name".equals(pull.getName())){
						Log.d("tag","uid:"+pull.getAttributeValue(null,"uid"));
						Log.d("tag", "name:"+pull.nextText());
					}else if("age".equals(pull.getName())){
						Log.d("tag", "age:"+pull.nextText());
					}else if("gender".equals(pull.getName())){
						Log.d("tag", "gender:"+pull.nextText());
					}
				}
				//移动指针
				type = pull.next();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
