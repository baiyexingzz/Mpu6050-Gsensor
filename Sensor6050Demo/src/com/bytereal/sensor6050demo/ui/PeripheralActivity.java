package com.bytereal.sensor6050demo.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.bytereal.sensor6050demo.BaseApp;
import com.bytereal.sensor6050demo.Constans;
import com.bytereal.sensor6050demo.R;
import com.bytereal.sensor6050demo.util.StringUtil;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class PeripheralActivity extends Activity implements OnItemClickListener, OnClickListener {
	private final String TAG = "PeripheralActivity";
	private static final long SCAN_PERIOD = 10000;
	private Button mScanButton;
	private ListView mDeviceListView;
	private boolean isScan = false;
	private Handler mHandler = new Handler();
	private BluetoothAdapter mBluetoothAdapter;
	//蓝牙设备集合
	private ArrayList<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();
	private ArrayList<HashMap<String, Object>> listItem; // ListView的数据源用于显示在界面，这里是一个HashMap的列表
	private SimpleAdapter listItemAdapter; // ListView的适配器
	private static final int REQUEST_ENABLE_BT = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.peripheral);
		BaseApp.getInstance().addActivity(this);

		initView();
		initBlue();
		setupView();
	}

	/**
	 * 检查手机蓝牙
	 */
	private void initBlue() {

		// 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}

		// 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// 检查设备上是否支持蓝牙
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
		//并在onActivityResult中获取返回值
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//拒绝开启蓝牙,退出应用
		if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED) {
			finish();
		}
	}
	/**
	 * 控件初始化工作, 监听
	 */
	private void setupView() {
		listItem = new ArrayList<HashMap<String, Object>>();
		listItemAdapter = new SimpleAdapter(this, listItem, R.layout.listview,
				new String[]{"image", "title", "text"},
				new int[]{R.id.ItemImage, R.id.ItemTitle, R.id.ItemText});
		mDeviceListView.setAdapter(listItemAdapter);

		mDeviceListView.setOnItemClickListener(this);
		mScanButton.setOnClickListener(this);
	}


	/**
	 * 寻找控件
	 */
	private void initView() {
		mScanButton = (Button) findViewById(R.id.bt_scan);
		mDeviceListView = (ListView) findViewById(R.id.lv_device);
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.bt_scan:
			scanLeDevice(!isScan);
			break;

		default:
			break;
		}

	}

	private void scanLeDevice(final boolean enable) {
		if (enable) {	//true 开始扫描
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					mScanButton.setText(R.string.start_scan);
					isScan = false;
				}
			}, SCAN_PERIOD);	//延迟10s 执行 -> 扫描10s 后停止扫描
			mScanButton.setText(R.string.stop_scan);
			isScan = true;
			deleteItem();
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mScanButton.setText(R.string.start_scan);
			isScan = false;
		}
	}


	/**
	 * 扫描蓝牙 mLeDevices.add
	 * addItem
	 */
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {                	
					int majorid,minorid;
					majorid = scanRecord[25]*256 + scanRecord[26];
					minorid = scanRecord[27]*256 + scanRecord[28];
					addItem(device.getName(),device.getAddress()+" UUID:"+StringUtil.bytesToHex(scanRecord,9,16)+" MajorID:"+majorid+" MinorID:"+minorid+" RSSI:"+rssi);
					mLeDevices.add(device);
				//	MyLog.i(TAG, device.getName());
				}
			});
		}
	};

	private void addItem(String devname,String address)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("image", R.drawable.icon);
		map.put("title", devname);
		map.put("text", address);
		listItem.add(map);
		listItemAdapter.notifyDataSetChanged();

	}

	private void deleteItem()
	{
		listItem.clear();
		listItemAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
		final BluetoothDevice device = mLeDevices.get(position);
		if (device == null) return;
		final Intent intent = new Intent();
		intent.setClass(PeripheralActivity.this, MainActivity.class);
		intent.putExtra(MainActivity.EXTRAS_DEVICE_NAME, device.getName());
		intent.putExtra(MainActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
		if (isScan) {
			scanLeDevice(false);
		}
		startActivity(intent);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK ) {			
			Constans.exit_ask(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
