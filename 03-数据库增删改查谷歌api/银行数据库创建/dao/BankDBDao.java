package com.itheima.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.db.BankDBOpenHelper;

/**
 * �������ݿ��data access object
 */
public class BankDBDao {
	private BankDBOpenHelper helper;
	public BankDBDao(Context context) {
		helper = new BankDBOpenHelper(context);
	}
	/**
	 * ���һ���˻���Ϣ
	 * @param name ����
	 * @param money Ǯ
	 * @return ������������ݿ���к�id �������-1�������ʧ��,�û��Ѿ�����
	 */
	public long add(String name,float money){
		if(isUserExist(name)){
			return -1;
		}
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("money", money);
		long rowID = db.insert("account", null, values);
		db.close();
		return rowID;
	}
	/**
	 * ɾ��һ�����ݿ�ļ�¼
	 * @param name
	 * @return �Ƿ�ɾ���ɹ�
	 */
	public boolean delete(String name){
		SQLiteDatabase db = helper.getWritableDatabase();
		int result = db.delete("account", "name=?", new String[]{name});
		db.close();
		if(result>0){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * �޸��û����˻���Ϣ
	 * @param name Ҫ�޸Ĵ���������
	 * @param money �µ��˻����
	 * @return �Ƿ��޸ĳɹ�
	 */
	public boolean update(String name,float money){
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("money", money);
		int result = db.update("account", values, "name=?", new String[]{name});
		db.close();
		if(result>0){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * ��ѯĳ���û��ж���Ǯ
	 * @param name
	 * @return
	 */
	public float getUserMoney(String name){
		float money = 0;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("account", new String[]{"money"}, "name=?", new String[]{name}, null, null, null);
		if(cursor.moveToNext()){
			money = cursor.getFloat(0);
		}cursor.close();
		db.close();
		return money;
	}
	
	/**
	 * ��ѯĳ���û��Ƿ����
	 * @param name
	 * @return 
	 */
	public boolean isUserExist(String name){
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("account", null, "name=?", new String[]{name}, null, null, null);
		if(cursor.moveToNext()){
			result = true;
		}cursor.close();
		db.close();
		return result;
	}
	/**
	 * �������еĴ�����Ϣ
	 * @return
	 */
	public List<Map<String,Object>> findAllUser(){
		List<Map<String,Object>> allUsers = new ArrayList<Map<String,Object>>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("account", new String[]{"_id","name","money"}, null,null, null, null, null);
		while(cursor.moveToNext()){
			Map<String,Object> user = new HashMap<String, Object>();
			user.put("_id", cursor.getInt(0));
			user.put("name", cursor.getString(1));
			user.put("money", cursor.getFloat(2));
			allUsers.add(user);
		}
		cursor.close();
		db.close();
		return allUsers;
	}
}
