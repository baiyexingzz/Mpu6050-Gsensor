package com.bytereal.sensor6050demo;

import java.util.LinkedList;
import java.util.List;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class BaseApp extends Application {
	public static Context context;
	private static Handler handler;
	//activity list
	private List<Activity> activityList = new LinkedList<Activity>();
	private static BaseApp instance;	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = getBaseContext();
		handler = new Handler();
	}
	
	public static BaseApp getInstance(){
		if(null == instance){
			instance = new BaseApp();
		}
		return instance;
	}
	
	public void addActivity(Activity activity){
		activityList.add(activity);
	}
	
	public void exit(){
		for(Activity activity:activityList){
			activity.finish();
		}
		System.exit(0);
	}	
}
