package com.bytereal.sensor6050demo;

import java.util.ArrayList;
import java.util.HashMap;

import com.bytereal.sensor6050demo.service.BluetoothLeService;
import com.bytereal.sensor6050demo.ui.LoginActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;



public class Constans {
	public static boolean DEBUG = true;
	
	public static BluetoothLeService mBluetoothLeService;
	public static ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
	public static ArrayList<BluetoothGattService> gattServiceObject = new ArrayList<BluetoothGattService>();
	
	public static String http_get(String url){
        try{
			HttpGet httpRequest = new HttpGet(url);
			  
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
			HttpConnectionParams.setSoTimeout(httpParameters, 10000);
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpResponse httpResponse = client.execute(httpRequest);
			
			if(httpResponse.getStatusLine().getStatusCode()== 200)
			{
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				strResult = strResult.trim();
				return strResult;
			}else{
				return "{status:503}";
			}   
        }catch(Exception e){
        	//e.printStackTrace();
        	return "{status:500,error:"+e.toString();
        }
	}


	public static void exit_ask(final Activity act){
		AlertDialog dialog = new AlertDialog.Builder(act).setIcon(android.R.drawable.btn_star).setTitle("Exit the APP锛�)").setPositiveButton("Yes", new OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub		
				BaseApp.getInstance().exit();
			}	
		}).setNegativeButton("Cancel", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub									
			}				
		}).create();
		dialog.show();	
	}	
	
	public static void info_dialog(final Activity act,String title,String info){
		AlertDialog.Builder builder = new AlertDialog.Builder(act);		
		builder.setTitle(title);
		builder.setMessage(info);
		builder.setPositiveButton("纭畾", null);
		builder.create().show();
	}
	
	public static void logout(final Activity act){
		//info_dialog(act,"鎻愰啋","浼氳瘽宸插け鏁堬紝璇烽噸鏂扮櫥闄�);
		Intent intent = new Intent();
		intent.setClass(act, LoginActivity.class);
		act.startActivity(intent);
		act.finish();																	
	}
	
	public static String getVersionName(Context ctx) {
		String versionName = null;
		try {
			versionName = ctx.getPackageManager().getPackageInfo("com.bytereal.RealTagTools", 0).versionName;
		} catch (NameNotFoundException e) {			
		} catch (Exception e) {
		}
	 
		return versionName;
	}	
	public static int getVersionCode(Context ctx) {
		int versionCode = 0;
		try {
			versionCode = ctx.getPackageManager().getPackageInfo("com.bytereal.RealTagTools", 0).versionCode;
		} catch (NameNotFoundException e) {
		} catch (Exception e) {
		}
	 
		return versionCode;
	}	
	
	public static JSONObject json_decode(String result){
		try {
			return (JSONObject) new JSONTokener(result).nextValue();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		} catch (Exception e){
			//e.printStackTrace();
			return null;
		}
	}
	public static String json_getString(JSONObject obj, String key){
		try {
			return obj.getString(key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
	}
	public static String json_getString_2(JSONObject obj, String key1,String key2){
		try {
			return obj.getJSONObject(key1).getString(key2);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}catch (Exception e){
			return null;
		}
	}
	
	public static int json_getInteger(JSONObject obj, String key){
		try {
			return Integer.valueOf(obj.getInt(key));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return 0;
		}catch (Exception e){
			//e.printStackTrace();
			return 0;
		}
	}
	public static int json_getInteger_2(JSONObject obj, String key1, String key2){
		try {
			return Integer.valueOf(obj.getJSONObject(key1).getInt(key2));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return 0;
		}catch (Exception e){
			return 0;
		}
	}
	public static int json_getarraysize(JSONObject obj, String key1){
		try {
			return obj.getJSONArray(key1).length();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return 0;
		}catch (Exception e){
			return 0;
		}
	}	
	public static int json_getInteger_3(JSONObject obj, String key1, int idx, String key2){
		try {
			return Integer.valueOf(obj.getJSONArray(key1).getJSONObject(idx).getInt(key2));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return 0;
		}catch (Exception e){
			return 0;
		}
	}		
	
	public static void goto_web(final Activity act){
		AlertDialog dialog = new AlertDialog.Builder(act).setIcon(android.R.drawable.btn_star).setTitle("璁块棶鏄撳揩閫氫富椤靛悧锛�").setPositiveButton("鏄殑", new OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub		
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://wap.ekcall.com"));
				act.startActivity(intent);			
			}	
		}).setNegativeButton("鍙栨秷", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub									
			}				
		}).create();
		dialog.show();		
	}
}
