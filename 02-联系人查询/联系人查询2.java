package com.heima.mobilesafe.business;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import com.heima.mobilesafe.bean.ContactBean;

public class ContactProvider {
	private static ArrayList<ContactBean> list;
	private static ContentResolver resolver;

	public static List<ContactBean> getAllContacts(Context context){
		list = new ArrayList<ContactBean>();
		resolver = context.getContentResolver();
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = {
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
			ContactsContract.CommonDataKinds.Phone.NUMBER,
			ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
		};
		
		Cursor cursor = resolver.query(uri, projection, null, null, null);
		System.out.println("cursor="+cursor);
		//if(cursor!=null){
			while(cursor.moveToNext()){
				ContactBean cb = new ContactBean();
				cb.name = cursor.getString(0);
				cb.phone = cursor.getString(1);
				cb.contactId = cursor.getString(2);
				list.add(cb);
			}
			cursor.close();
		//}
		
		return list;
		
	}
	public static Bitmap getPhoto(Context context,String contactId){
		resolver = context.getContentResolver();
		//content:contact/id
		Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));
		InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
		//流->Bitmap
		return BitmapFactory.decodeStream(is);
		
	}
}
