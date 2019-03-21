package com.itheima.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 银行数据库打开的帮助类
 * @author Administrator
 *
 */
public class BankDBOpenHelper extends SQLiteOpenHelper {

	public BankDBOpenHelper(Context context) {
		super(context, "bank.db", null, 1);
	}
	//数据库第一次被创建调用的方法，适合做数据库表结构的初始化
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table account (_id integer primary key autoincrement,name varchar(20),money varchar(2))");
	}
	//数据库版本更新的时候调用的方法
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
