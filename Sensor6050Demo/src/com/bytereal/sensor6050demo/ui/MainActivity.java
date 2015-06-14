package com.bytereal.sensor6050demo.ui;


import java.util.HashMap;
import java.util.List;

import com.bytereal.sensor6050demo.Constans;
import com.bytereal.sensor6050demo.R;
import com.bytereal.sensor6050demo.logs.MyLog;
import com.bytereal.sensor6050demo.model.SampleGattAttributes;
import com.bytereal.sensor6050demo.service.BluetoothLeService;
import com.bytereal.sensor6050demo.ui.mpu6050.Mpu3DActivity;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends Activity  {

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public static final int REQUESTCODE = 1;
	protected static final String TAG = "MainActivity";

	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";
	private String mDeviceName;
	private String mDeviceAddress;
	private boolean isConnected = false;
	private int servidx=-1;
	private boolean isStartActivity = false;
	private boolean isHaveMpu = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

		Constans.gattServiceData.clear();
		Constans.gattServiceObject.clear();

		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		boolean bll = bindService(gattServiceIntent, mServiceConnection,BIND_AUTO_CREATE);
		if(!bll){
			Toast.makeText(this, "Bind Service Failed!", Toast.LENGTH_SHORT).show();
			MainActivity.this.finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (Constans.mBluetoothLeService != null) {
			final boolean result = Constans.mBluetoothLeService.connect(mDeviceAddress);
			Log.d(TAG, "Connect request result=" + result);
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();

		unbindService(mServiceConnection);
		Constans.mBluetoothLeService = null;
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		//intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}
	// Handles various events fired by the Service.
	// ACTION_GATT_CONNECTED: connected to a GATT server.
	// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
	// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
	// ACTION_DATA_AVAILABLE: received data from the device. This can be a
	// result of read
	// or notification operations.
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			System.out.println("action = " + action);
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				isConnected = true;
				Constans.mBluetoothLeService.discoverServices();
				MyLog.i(TAG,mDeviceName+": Discovering services...");
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				isConnected = false;
				MyLog.i(TAG,mDeviceName+": Disconnected");
				finish();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				// Show all the supported services and characteristics on the
				// user interface.
				MyLog.i(TAG,mDeviceName+": Discovered");
				displayGattServices(Constans.mBluetoothLeService.getSupportedGattServices());
			} 
			/*
 			else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
 				status_text.setText(mDeviceName+": DATA AVAILABLE");
 				String temp = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
 			}
			 */
		}
	};

	// Code to manage Service lifecycle.

	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			Constans.mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			/**
			 * µÃµ½À¶ÑÀmanager ºÍ adapter
			 */
			if (!Constans.mBluetoothLeService.initialize()) {
				MyLog.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			Constans.mBluetoothLeService.connect(mDeviceAddress);
			MyLog.i(TAG,mDeviceName+": Connecting...");
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			Constans.mBluetoothLeService = null;
		}
	};


	private void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null)
			return;
		String uuid = null;
		String name = null;

		//ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
		//mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		int i = 0;
		for (BluetoothGattService gattService : gattServices) {


			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();
			uuid = uuid.substring(4,8);
			boolean exist = false;

			for(HashMap<String, String> sItem:Constans.gattServiceData){
				if(sItem.get(LIST_UUID).equals(uuid)){
					exist = true;
					break;
				}

			}
			if(exist){
				continue;
			}
			name = SampleGattAttributes.lookup(uuid, "Unknow Service");
			currentServiceData.put(LIST_NAME, name);
			currentServiceData.put(LIST_UUID, uuid);
			Constans.gattServiceData.add(currentServiceData);
			Constans.gattServiceObject.add(gattService);
			i++;
			if (uuid.equals("ffa0")) {
				
				Log.w(TAG, "find ffa0");
				servidx = i-1;
				/*MyLog.i("AA", "size="+Constans.gattServiceObject.size());
				MyLog.i("AA", "FIND!!!!!!!!!-----"+servidx+"  uuid="+uuid+" name="+name);
				MyLog.i("AA", "service uuid="+gattService.getUuid().toString().substring(4,8));
				MyLog.i("AA", "service uuid="+Constans.gattServiceObject.get(servidx).getUuid().toString().substring(4,8));
				 */
			}
		}
		ScanCharacteristics(Constans.gattServiceObject.get(servidx));
	}


	private void ScanCharacteristics(BluetoothGattService gattService) {
		// TODO Auto-generated method stub
		MyLog.i("AA", "service uuid="+gattService.getUuid().toString().substring(4,8));
		List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

		String uuid = null;
		String name = null;
		// Loops through available Characteristics.
		String tmpuuidString = null;
		for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
			//charas.add(gattCharacteristic);
			HashMap<String, String> currentCharaData = new HashMap<String, String>();
			uuid = gattCharacteristic.getUuid().toString();
			String tmp = uuid;
			uuid = uuid.substring(4,8);
			Log.i("AA", "Characteristic UUID="+uuid);

			if (uuid.equals("ffb6")) {
				tmpuuidString = tmp;
				Log.w(TAG, "find ffb6");
			}
			//	gattCharacteristicGroupData.add(currentCharaData);
			//	addItem(name, uuid);
		}
		if (tmpuuidString != null && !isStartActivity) {
			isStartActivity = true;
			Intent intent = new Intent(MainActivity.this, Mpu3DActivity.class);
			intent.putExtra(Mpu3DActivity.SERVERID, servidx);
			intent.putExtra(Mpu3DActivity.CHARAID, tmpuuidString);
			MyLog.i("RES", "startactivity");
			//startActivity(intent);
			startActivityForResult(intent, REQUESTCODE);
			//MainActivity.this.finish();
		}
		if (!isStartActivity) {
			Toast.makeText(this, getString(R.string.donot_have_mpu6050), 1).show();
			finish();
		}
	}

}
