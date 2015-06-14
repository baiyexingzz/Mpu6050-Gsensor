package com.bytereal.sensor6050demo.ui;


import com.bytereal.sensor6050demo.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

public class LoginActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		 Animation  myAnimation_Alpha=new AlphaAnimation(0.1f, 1.0f);
	     myAnimation_Alpha.setDuration(3000);    //动画持续时间2s
	     LinearLayout ll = (LinearLayout) findViewById(R.id.ll_log);
	     ll.startAnimation(myAnimation_Alpha);
		//BaseApp.getInstance().addActivity(this);
		
		Thread waitThread = new Thread() {     
			public void run() { 
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, PeripheralActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
			}   
		};   
		waitThread.start();
	}
}
