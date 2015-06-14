package com.bytereal.sensor6050demo.ui.mpu6050;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.bytereal.sensor6050demo.Constans;
import com.bytereal.sensor6050demo.R;
import com.bytereal.sensor6050demo.logs.MyLog;
import com.bytereal.sensor6050demo.service.BluetoothLeService;

public class Mpu3DActivity extends Activity {
	public static final String SERVERID ="SERVERID";
	public static final String CHARAID ="CHARAID";
	private static final String TAG = "Mpu3DActivity";
	GlSurfaceView mGLSurfaceView;
	BluetoothGattCharacteristic characteristic;
	boolean flag = true;
	int saveCnt = 0;
	String GxStr="",GyStr="",GzStr="",AxStr="",AyStr="",AzStr="";
	
	private TextView textViewGx, textViewGy, textViewGz, textViewAx, textViewAy, textViewAz ,textViewCnt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		super.onCreate(savedInstanceState);
		mGLSurfaceView = new GlSurfaceView(this);
	//setContentView(mGLSurfaceView);
		setContentView(R.layout.showg);
		
		textViewGx = (TextView)findViewById(R.id.textView_gx);
		textViewGy = (TextView)findViewById(R.id.textView_gy);
		textViewGz = (TextView)findViewById(R.id.textView_gz);
		textViewAx = (TextView)findViewById(R.id.textView_ax);
		textViewAy = (TextView)findViewById(R.id.textView_ay);
		textViewAz = (TextView)findViewById(R.id.textView_az);
		textViewCnt = (TextView)findViewById(R.id.textView_saveCnt);
		
		
		Intent intent = getIntent();
		int servidx = intent.getIntExtra(SERVERID, -1);
		String uuidString = intent.getStringExtra(CHARAID);
		MyLog.i(TAG, "servid="+servidx + " uuid="+uuidString);
		UUID uuid = UUID.fromString(uuidString);


		BluetoothGattService  gattService = Constans.gattServiceObject.get(servidx);  
		characteristic = gattService.getCharacteristic(uuid) ;
		if (characteristic == null) {
			Toast.makeText(this, getString(R.string.mpu6050_sensor_fail), Toast.LENGTH_LONG).show();
			finish();
		}
		
		Constans.mBluetoothLeService.readCharacteristic(characteristic);
	}

	@Override
	protected void onResume() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onResume();
		mGLSurfaceView.onResume();
		registerReceiver(mGattUpdateReceiver, new IntentFilter(BluetoothLeService.ACTION_DATA_AVAILABLE));
	}

	@Override
	protected void onPause() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
		mGLSurfaceView.onPause();
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		  if (keyCode == KeyEvent.KEYCODE_BACK) {// ��keyCode�����˳��¼�ֵʱ
			  MyLog.i(TAG, "keyback@");
	            finish();
	            return false;
	        } else {
	            return super.onKeyDown(keyCode, event);
	        }
	}
	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			System.out.println("action = " + action);
			if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                byte []data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				//getFFB6(data);
				
				double gx = (data[0]+data[1]*256)/32768.0*16*9.8;
				double gy = (data[2]+data[3]*256)/32768.0*16*9.8;
				double gz = (data[4]+data[5]*256)/32768.0*16*9.8;
				
				double ax = (data[6]+data[7]*256)/32768.0*2000;
				double ay = (data[8]+data[9]*256)/32768.0*2000;
				double az = (data[10]+data[11]*256)/32768.0*2000;
				
				GxStr = String .format("%.3f",gx);
				GyStr = String .format("%.3f",gy);
				GzStr = String .format("%.3f",gz);
				AxStr = String .format("%.3f",ax);
				AyStr = String .format("%.3f",ay);
				AzStr = String .format("%.3f",az);
				
				GxStr = ("Gx == " + GxStr +" m/s2");
				GyStr = ("Gy == " + GyStr +" m/s2");
				GzStr = ("Gz == " + GzStr +" m/s2");
					
				AxStr = ("Ax == " + AxStr + " `/s");
				AyStr = ("Ay == " + AyStr + " `/s");
				AzStr = ("Az == " + AzStr + " `/s");
				
				

				textViewGx.setText(GxStr);	
				textViewGy.setText(GyStr);	
				textViewGz.setText(GzStr);	
				
				textViewAx.setText(AxStr);	
				textViewAy.setText(AyStr);	
				textViewAz.setText(AzStr);
				
				if(SaveFiles(data)){
					saveCnt++;
					textViewCnt.setText("Save Count " + String.valueOf(saveCnt));
				}
				Constans.mBluetoothLeService.readCharacteristic(characteristic);
			}
		}
	};

	private void getFFB6(byte[] packet){
	//	Log.i(TAG, "data.len="+data.length());
        float[] q = new float [4];
        for (int ii=0, i=13; i<20; i+=2) {
            q[ii++] =  (short)(((packet[i-1]&255) | (packet[i]&255) <<8)) / 16384.0f;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i=0; i<q.length; i++)
            buffer.append("  q["+i+"]="+q[i]);
        //	Log.d(TAG, buffer.toString());

        float []data = new float[3];
        data[0] = (float) Math.atan2(2*q[1]*q[2] - 2*q[0]*q[3], 2*q[0]*q[0] + 2*q[1]*q[1] - 1);   // psi
        data[1] = (float) -Math.asin(2*q[1]*q[3] + 2*q[0]*q[2]);                              // theta
        data[2] = (float) Math.atan2(2*q[2]*q[3] - 2*q[0]*q[1], 2*q[0]*q[0] + 2*q[3]*q[3] - 1);   // phi
        //�Ƕ�
        data[0] = data[0] * 180.0f / 3.14f;
        data[1] = data[1] * 180.0f / 3.14f;
        data[2] = data[2] * 180.0f / 3.14f;
        mGLSurfaceView.onMpu6050Sensor(data[2], data[1], data[0]);
	//	mGLSurfaceView.onMpu6050Sensor(Angel_accX, Angel_accY, Angel_accZ);
		Constans.mBluetoothLeService.readCharacteristic(characteristic);
	}
	
	private boolean SaveFiles(byte []data){

		
		try{

			FileOutputStream outStream = new FileOutputStream(android.os.Environment.getExternalStorageDirectory()+File.separator+"GAdata"+".txt",true);
			OutputStreamWriter writer = new OutputStreamWriter(outStream,"utf8");

			writer.write(GxStr+" " + GyStr + " "+ GzStr+" "+AxStr+" "+AyStr+" "+AzStr+"   ");
		   
		    SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss SSS");

		    Date curDate = new Date(System.currentTimeMillis());//获取当前时间

		    String str = formatter.format(curDate);

		    writer.write(str);
		    writer.write("\n");
		    writer.flush();
		    writer.close();//记得关闭

		    outStream.close();
		}
		catch(IOException e){
			return false;
		}
		
		finally{
			 
		}
		return true;
	}
	
	
	
}
