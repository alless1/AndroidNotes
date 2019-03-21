package com.itheima.dbcreate;

import java.util.Random;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	/**
	 * ���һ������
	 */
	public void add(View view) {
		// ִ�������һ�д��룬���ݿ��ǲ���𴴽����ˡ�
		MyDBOpenHelper helper = new MyDBOpenHelper(this);
		// ����봴�����ݿ����ִ��,��һ�д���
		SQLiteDatabase db = helper.getWritableDatabase();
		Random random = new Random();
		// db.execSQL("insert into info (name,phone) values (?,?)", new Object[]
		// {
		// "����" + random.nextInt(100), "110-" + random.nextInt(100) });
		ContentValues values = new ContentValues();
		values.put("name", "����" + random.nextInt(100));
		values.put("phone", "110-" + random.nextInt(100));
		long id = db.insert("info", null, values);// ͨ����ƴsql���
		db.close();
		if (id != -1) {
			Toast.makeText(this, "��ӳɹ�,�ڵ�" + id + "��", 0).show();
		} else {
			Toast.makeText(this, "���ʧ��", 0).show();
		}
	}

	/**
	 * ɾ��һ������
	 */
	public void delete(View view) {
		// ִ�������һ�д��룬���ݿ��ǲ���𴴽����ˡ�
		MyDBOpenHelper helper = new MyDBOpenHelper(this);
		// ����봴�����ݿ����ִ��,��һ�д���
		SQLiteDatabase db = helper.getWritableDatabase();
		// db.execSQL("delete from info ");
		int result = db.delete("info", null, null);
		db.close();
		if (result == 0) {
			Toast.makeText(this, "ɾ��ʧ��", 0).show();
		} else {
			Toast.makeText(this, "ɾ����"+result+"����¼", 0).show();
			// ��ȥ��ѯһ�Ρ�
		}
	}

	/**
	 * �޸�һ������
	 */
	public void update(View view) {
		// ִ�������һ�д��룬���ݿ��ǲ���𴴽����ˡ�
		MyDBOpenHelper helper = new MyDBOpenHelper(this);
		// ����봴�����ݿ����ִ��,��һ�д���
		SQLiteDatabase db = helper.getWritableDatabase();
		//db.execSQL("update info set phone=?", new Object[] { "8888" });
		ContentValues values = new ContentValues();
		values.put("phone", "99999");
		int result = db.update("info", values, null, null);
		db.close();
		if (result == 0) {
			Toast.makeText(this, "�޸���0����¼", 0).show();
		} else {
			Toast.makeText(this, "�޸���"+result+"����¼", 0).show();
		}
	}

	/**
	 * ��ѯȫ������
	 */
	public void query(View view) {
		// ִ�������һ�д��룬���ݿ��ǲ���𴴽����ˡ�
		MyDBOpenHelper helper = new MyDBOpenHelper(this);
		// ����봴�����ݿ����ִ��,��һ�д���
		SQLiteDatabase db = helper.getReadableDatabase();
		//Cursor cursor = db.rawQuery("select * from info", null);
		Cursor cursor = db.query("info", new String[]{"name","phone","_id"}, null, null, null, null, null);
		while (cursor.moveToNext()) {
			String name = cursor.getString(0);
			String phone = cursor.getString(1);
			String id = cursor.getString(2);
			System.out.println("id:" + id + "--name:" + name + "--phone"
					+ phone);
			System.out.println("----");
		}
		// �ǵ��������ݿ� �ر�cursor
		cursor.close();
		db.close();
	}

}
