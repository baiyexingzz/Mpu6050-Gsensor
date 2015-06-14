package com.bytereal.sensor6050demo.logs;

import android.util.Log;

public class MyLog {
	private final static int  DEBUG = 1;
	private final static int RELEASE = 2;
	private static int state = DEBUG;
	
	public static void i(String tag, String msg) {
		if (state == DEBUG) {
			Log.i(tag, msg);
		}
	}
	
	public static void w(String tag, String msg) {
		if (state == DEBUG) {
			Log.w(tag, msg);
		}
	}
	
	public static void e(String tag, String msg) {
		if (state == DEBUG) {
			Log.e(tag, msg);
		}
	}
	
	public static void d(String tag, String msg) {
		if (state == DEBUG) {
			Log.d(tag, msg);
		}
	}
}
