package com.itheima.db.provider;

import com.itheima.db.BankDBOpenHelper;
import com.itheima.db.dao.BankDBDao;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * 银行内部的内线， 用来提供数据（双重间谍）
 */
public class BankInfoProvider extends ContentProvider {
	private static final int ACCOUNT = 1;
	private static final int SINGLE_ACCOUNT = 2;
	private BankDBOpenHelper helper;
	// 定义一个uri的匹配器 ，识别器
	private static UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		// 训练匹配器
		mUriMatcher.addURI("com.itheima.db", "account", ACCOUNT);
		// content://com.itheima.db/account 访问accout表里面的全部数据
		// content://com.itheima.db/account/8 访问accout表里面的第8条数据
		mUriMatcher.addURI("com.itheima.db", "account/#", SINGLE_ACCOUNT);
	}

	@Override
	public boolean onCreate() {
		helper = new BankDBOpenHelper(getContext());
		return false;
	}

	// 查询的方法
	@Override
	public Cursor query(Uri uri, String[] columns, String selection,
			String[] selectionArgs, String sortOrder) {
		int code = mUriMatcher.match(uri);
		if (code == ACCOUNT) {
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.query("account", columns, selection,
					selectionArgs, null, null, null);
			return cursor;
		} else {
			throw new IllegalArgumentException("根据法律规定，你无权查看数据。");
		}
	}

	// vnd.android.cursor.item 单条记录
	// vnd.android.cursor.dir 多条记录
	@Override
	public String getType(Uri uri) {
		int result = mUriMatcher.match(uri);
		if (result == ACCOUNT) {
			// 多条记录
			return "vnd.android.cursor.dir/account";
		} else if (result == SINGLE_ACCOUNT) {
			// 单条记录
			return "vnd.android.cursor.item/account";
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int code = mUriMatcher.match(uri);
		if (code == ACCOUNT) {
			SQLiteDatabase db = helper.getWritableDatabase();
			long id = db.insert("account", null, values);
			db.close();
			//后面程序，通知这个uri的数据变化了
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse("content://com.itheima.db/account/" + id);
		} else {
			throw new IllegalArgumentException("根据法律规定，你无权添加数据。");
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int code = mUriMatcher.match(uri);
		if (code == ACCOUNT) {
			SQLiteDatabase db = helper.getWritableDatabase();
			int result = db.delete("account", selection, selectionArgs);
			db.close();
			//后面程序，通知这个uri的数据变化了
			getContext().getContentResolver().notifyChange(uri, null);
			return result;
		} else {
			throw new IllegalArgumentException("根据法律规定，你无权删除数据。");
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int code = mUriMatcher.match(uri);
		if (code == ACCOUNT) {
			SQLiteDatabase db = helper.getWritableDatabase();
			int result = db.update("account", values, selection, selectionArgs);
			db.close();
			//后面程序，通知这个uri的数据变化了
			getContext().getContentResolver().notifyChange(uri, null);
			return result;
		} else {
			throw new IllegalArgumentException("根据法律规定，你无权修改数据。");
		}
	}

}
