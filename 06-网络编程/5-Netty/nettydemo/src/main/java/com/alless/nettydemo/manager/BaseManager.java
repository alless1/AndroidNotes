package com.alless.nettydemo.manager;

import android.content.Context;

public class BaseManager {

	protected static Context ctx;

	public void setContext(Context context) {
		if (context == null) {
			throw new RuntimeException("context is null");
		}
		
		ctx = context;
	}
}
