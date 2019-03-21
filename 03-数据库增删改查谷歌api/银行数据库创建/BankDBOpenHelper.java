package com.itheima.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * �������ݿ�򿪵İ�����
 * @author Administrator
 *
 */
public class BankDBOpenHelper extends SQLiteOpenHelper {

	public BankDBOpenHelper(Context context) {
		super(context, "bank.db", null, 1);
	}
	//���ݿ��һ�α��������õķ������ʺ������ݿ��ṹ�ĳ�ʼ��
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table account (_id integer primary key autoincrement,name varchar(20),money varchar(2))");
	}
	//���ݿ�汾���µ�ʱ����õķ���
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
