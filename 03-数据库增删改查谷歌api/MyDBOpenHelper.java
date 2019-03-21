package com.itheima.dbcreate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * �൱��File��
 * @author Administrator
 *
 */
public class MyDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * @param context ������
	 * @param name ���ݿ��ļ�������
	 * @param factory ���������α���� null����Ĭ�ϵ��α깤��
	 * @param version ���ݿ�İ汾�� ��1��ʼ
	 */
	public MyDBOpenHelper(Context context) {
		super(context, "itheima.db", null, 1);
	}

	/**
	 * ���ݿ��һ�α�������ʱ����ã�������ݿ��Ѿ��������Ͳ���ִ����һ�����
	 * @param db ����ľ������Ǵ������������ݿ�
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("�����������ݿⱻ�����ˡ��ʺϳ�ʼ�����ݿ�ı�ṹ");
		//������
		db.execSQL("create table info (_id integer primary key autoincrement, name varchar(20), phone varchar(20)) ");
	}
	/**
	 * �����ݿ�İ汾��Ҫ���µ�ʱ����õķ���
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("onupgrade ���ݿⱻ������ �����������ʺ��޸����ݿ�ı�ṹ");
		//db.execSQL("alter table info add money varchar(10)");

	}
}
