package com.itheima.contacts;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	}
	public void query(View v){
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri uri2 =Uri.parse("content://com.android.contacts/data");
		//定义一个集合存储联系人信息
		List<ContactBean> list = new ArrayList<ContactBean>();
		//1.根据表 raw_contacts查找contact_id.
		Cursor cursor = resolver.query(uri,new String[]{"contact_id"}, null, null, null);
		while(cursor.moveToNext()){
			String contact_id = cursor.getString(0);
			System.out.println("contact_id = " + contact_id);
			//new一个contactBean保存联系人信息.
			ContactBean cb = new ContactBean();
			//2.根据表data 查询 data1 mimetype  条件raw_contact_id = contact_id
			Cursor cursor2 = resolver.query(uri2, new String[]{"data1","mimetype"}, "raw_contact_id=?", new String[]{contact_id}, null);
			while(cursor2.moveToNext()){
				String data1 = cursor2.getString(0);
				String mimetype = cursor2.getString(1);
				if("vnd.android.cursor.item/name".equals(mimetype))
					cb.name = data1;
				if("vnd.android.cursor.item/phone_v2".equals(mimetype))
					cb.phone = data1;
				if("vnd.android.cursor.item/email_v2".equals(mimetype))
					cb.email = data1;
			}
			cursor2.close();
			list.add(cb);
		}
		cursor.close();
	}
	public void insert(View v){
		//1.先查询联系人总数量num;
		int num = 1;
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri uri2 =Uri.parse("content://com.android.contacts/data");
		Cursor cursor = resolver.query(uri, new String[]{"contact_id"}, null, null, "contact_id DESC");
		if(cursor.moveToFirst())
			num = cursor.getInt(0)+1;
		
		//2.在raw_contacts表插入id
		ContentValues values_id = new ContentValues();
		values_id.put("contact_id",num);
		resolver.insert(uri,values_id);
		
		//3.在data表插入联系人数据.
		//插入姓名
		ContentValues values1 = new ContentValues();
		values1.put("raw_contact_id", num);
		values1.put("data1","zhaoliu");
		values1.put("mimetype","vnd.android.cursor.item/name");
		resolver.insert(uri2, values1);
		//插入电话
		ContentValues values2 = new ContentValues();
		values2.put("raw_contact_id", num);
		values2.put("data1","8888");
		values2.put("mimetype","vnd.android.cursor.item/phone_v2");
		resolver.insert(uri2, values2);
		//插入email
		ContentValues values3 = new ContentValues();
		values3.put("raw_contact_id", num);
		values3.put("data1","999@sina.com");
		values3.put("mimetype","vnd.android.cursor.item/email_v2");
		resolver.insert(uri2, values3);
		cursor.close();
		Toast.makeText(this, "添加完成", 0).show();
	}
	
	public void delete(View v){
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		resolver.delete(uri, null, null);
		Toast.makeText(this, "清除完成", 0).show();
	}

}
