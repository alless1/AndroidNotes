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
    //直接用StringBuilder拼接xml格式文件会出现bug，所以用到了谷歌api XmlSerializer 序列化器。
    public void createXml(View v){
    	try {
    		//获取手机文件夹目录，建立输出流路径。
			FileOutputStream fos = new FileOutputStream(new File(getFilesDir(),"001.xml"));
			//获得xml序列化器。
			XmlSerializer xml = Xml.newSerializer();
			//格式化序列化器，设置输出流，编码方式。
			xml.setOutput(fos, "utf-8");
			//开始写xml文件头。编码，是否独立文件。
			xml.startDocument("utf-8", true);
			//开始写根标签<person>。标签都是对称的，有开始就有结束。命名空间，没有就写null;
			xml.startTag(null, "person");
			//写子标签<name>
			xml.startTag(null, "name");
			//写标签的属性 uid = "9527" 和文本 张三。
			xml.attribute(null, "uid", "9527");
			xml.text("张三");
			xml.endTag(null, "name");
			//同理 写出其他标签文本内 容
			xml.startTag(null, "age");
			xml.text("23");
			xml.endTag(null, "age");
			xml.startTag(null, "gender");
			xml.text("男");
			xml.endTag(null, "gender");
			//关闭根标签，没有参数。
			xml.endDocument();
			//写完了 关流。
			fos.close();
			Toast.makeText(MainActivity.this, "已生成", 0);
			
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "操作失败", 0);
			Log.d("tag", "程序异常"+e.getStackTrace());
			e.printStackTrace();
		}
    	
    }

    
}
