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
	 * 添加一条数据
	 */
	public void add(View view) {
		// 执行下面的一行代码，数据库是不会别创建的了。
		MyDBOpenHelper helper = new MyDBOpenHelper(this);
		// 如果想创建数据库必须执行,下一行代码
		SQLiteDatabase db = helper.getWritableDatabase();
		Random random = new Random();
		// db.execSQL("insert into info (name,phone) values (?,?)", new Object[]
		// {
		// "王五" + random.nextInt(100), "110-" + random.nextInt(100) });
		ContentValues values = new ContentValues();
		values.put("name", "王五" + random.nextInt(100));
		values.put("phone", "110-" + random.nextInt(100));
		long id = db.insert("info", null, values);// 通过组拼sql语句
		db.close();
		if (id != -1) {
			Toast.makeText(this, "添加成功,在第" + id + "行", 0).show();
		} else {
			Toast.makeText(this, "添加失败", 0).show();
		}
	}

	/**
	 * 删除一条数据
	 */
	public void delete(View view) {
		// 执行下面的一行代码，数据库是不会别创建的了。
		MyDBOpenHelper helper = new MyDBOpenHelper(this);
		// 如果想创建数据库必须执行,下一行代码
		SQLiteDatabase db = helper.getWritableDatabase();
		// db.execSQL("delete from info ");
		int result = db.delete("info", null, null);
		db.close();
		if (result == 0) {
			Toast.makeText(this, "删除失败", 0).show();
		} else {
			Toast.makeText(this, "删除了"+result+"条记录", 0).show();
			// 再去查询一次。
		}
	}

	/**
	 * 修改一条数据
	 */
	public void update(View view) {
		// 执行下面的一行代码，数据库是不会别创建的了。
		MyDBOpenHelper helper = new MyDBOpenHelper(this);
		// 如果想创建数据库必须执行,下一行代码
		SQLiteDatabase db = helper.getWritableDatabase();
		//db.execSQL("update info set phone=?", new Object[] { "8888" });
		ContentValues values = new ContentValues();
		values.put("phone", "99999");
		int result = db.update("info", values, null, null);
		db.close();
		if (result == 0) {
			Toast.makeText(this, "修改了0条记录", 0).show();
		} else {
			Toast.makeText(this, "修改了"+result+"条记录", 0).show();
		}
	}

	/**
	 * 查询全部数据
	 */
	public void query(View view) {
		// 执行下面的一行代码，数据库是不会别创建的了。
		MyDBOpenHelper helper = new MyDBOpenHelper(this);
		// 如果想创建数据库必须执行,下一行代码
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
		// 记得用完数据库 关闭cursor
		cursor.close();
		db.close();
	}

}
