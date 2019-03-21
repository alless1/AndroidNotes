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
 * 银行数据库的data access object
 */
public class BankDBDao {
	private BankDBOpenHelper helper;
	public BankDBDao(Context context) {
		helper = new BankDBOpenHelper(context);
	}
	/**
	 * 添加一条账户信息
	 * @param name 姓名
	 * @param money 钱
	 * @return 代表添加在数据库的行号id 如果返回-1代表添加失败,用户已经存在
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
	 * 删除一条数据库的记录
	 * @param name
	 * @return 是否删除成功
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
	 * 修改用户的账户信息
	 * @param name 要修改储户的姓名
	 * @param money 新的账户余额
	 * @return 是否修改成功
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
	 * 查询某个用户有多少钱
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
	 * 查询某个用户是否存在
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
	 * 返回所有的储户信息
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
