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
 * �����ڲ������ߣ� �����ṩ���ݣ�˫�ؼ����
 */
public class BankInfoProvider extends ContentProvider {
	private static final int ACCOUNT = 1;
	private static final int SINGLE_ACCOUNT = 2;
	private BankDBOpenHelper helper;
	// ����һ��uri��ƥ���� ��ʶ����
	private static UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		// ѵ��ƥ����
		mUriMatcher.addURI("com.itheima.db", "account", ACCOUNT);
		// content://com.itheima.db/account ����accout�������ȫ������
		// content://com.itheima.db/account/8 ����accout������ĵ�8������
		mUriMatcher.addURI("com.itheima.db", "account/#", SINGLE_ACCOUNT);
	}

	@Override
	public boolean onCreate() {
		helper = new BankDBOpenHelper(getContext());
		return false;
	}

	// ��ѯ�ķ���
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
			throw new IllegalArgumentException("���ݷ��ɹ涨������Ȩ�鿴���ݡ�");
		}
	}

	// vnd.android.cursor.item ������¼
	// vnd.android.cursor.dir ������¼
	@Override
	public String getType(Uri uri) {
		int result = mUriMatcher.match(uri);
		if (result == ACCOUNT) {
			// ������¼
			return "vnd.android.cursor.dir/account";
		} else if (result == SINGLE_ACCOUNT) {
			// ������¼
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
			//�������֪ͨ���uri�����ݱ仯��
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse("content://com.itheima.db/account/" + id);
		} else {
			throw new IllegalArgumentException("���ݷ��ɹ涨������Ȩ������ݡ�");
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int code = mUriMatcher.match(uri);
		if (code == ACCOUNT) {
			SQLiteDatabase db = helper.getWritableDatabase();
			int result = db.delete("account", selection, selectionArgs);
			db.close();
			//�������֪ͨ���uri�����ݱ仯��
			getContext().getContentResolver().notifyChange(uri, null);
			return result;
		} else {
			throw new IllegalArgumentException("���ݷ��ɹ涨������Ȩɾ�����ݡ�");
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
			//�������֪ͨ���uri�����ݱ仯��
			getContext().getContentResolver().notifyChange(uri, null);
			return result;
		} else {
			throw new IllegalArgumentException("���ݷ��ɹ涨������Ȩ�޸����ݡ�");
		}
	}

}
