package com.itheima.dbcreate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 相当于File类
 * @author Administrator
 *
 */
public class MyDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * @param context 上下文
	 * @param name 数据库文件的名称
	 * @param factory 用来创建游标对象， null就用默认的游标工厂
	 * @param version 数据库的版本号 从1开始
	 */
	public MyDBOpenHelper(Context context) {
		super(context, "itheima.db", null, 1);
	}

	/**
	 * 数据库第一次被创建的时候调用，如果数据库已经创建，就不会执行这一句代码
	 * @param db 代表的就是我们创建出来的数据库
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("哈哈哈，数据库被创建了。适合初始化数据库的表结构");
		//创建表
		db.execSQL("create table info (_id integer primary key autoincrement, name varchar(20), phone varchar(20)) ");
	}
	/**
	 * 当数据库的版本需要更新的时候调用的方法
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("onupgrade 数据库被升级啦 。哈哈哈，适合修改数据库的表结构");
		//db.execSQL("alter table info add money varchar(10)");

	}
}
