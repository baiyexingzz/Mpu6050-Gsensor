package com.bytereal.sensor6050demo.ui.mpu6050;


import android.R.integer;
import android.content.Context;
import android.hardware.SensorEvent;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class GlSurfaceView extends GLSurfaceView {

	private static final String TAG = "GlSurfaceView";
	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private final float TRACKBALL_SCALE_FACTOR = 36.0f;
	private Render mRenderer;
	private float mPreviousX;
	private float mPreviousY;

	public GlSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GlSurfaceView(Context context) {
		super(context);
		init();
	}


	private void init() {
		mRenderer = new Render();
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		Log.i(TAG, "finshed!");
	}

	/**
	 * 重力传感�?
	 */
	public void onSensorChanged(SensorEvent event) {
		//传感器获取�?发生改变，在此处�?
		float x = event.values[0]; //手机横向翻滚
		//x>0 说明当前手机左翻 x<0右翻
		float y = event.values[1]; //手机纵向翻滚
		//y>0 说明当前手机下翻 y<0上翻
		float z = event.values[2]; //屏幕的朝�?
		//z>0 手机屏幕朝上 z<0 手机屏幕朝下
		Log.i(TAG, "x="+x +"  y="+y+ " z="+z);
		x +=x;
		y += y;
		mRenderer.mAngleX -= x;
		mRenderer.mAngleY += y;
		requestRender();
	}

	public void onMpu6050Sensor(float angleX, float angleY, float angleZ) {
		mRenderer.mAngleX = angleX;
		mRenderer.mAngleY = angleY;
		mRenderer.mAngleZ = angleZ;
		requestRender();
	}
	
	/**
	 * 轨迹�?
	 */
	@Override public boolean onTrackballEvent(MotionEvent e) {
		Log.i(TAG, "onTrackballEvent");
		mRenderer.mAngleX += e.getX() * TRACKBALL_SCALE_FACTOR;
		mRenderer.mAngleY += e.getY() * TRACKBALL_SCALE_FACTOR;
		requestRender();
		return true;
	}


	

	/**
	 * 触屏事件响应
	 */
	/*@Override public boolean onTouchEvent(MotionEvent e) {
		Log.i(TAG, "onTouchEvent");
		float x = e.getX();
		float y = e.getY();
		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			float dx = x - mPreviousX;
			float dy = y - mPreviousY;
			Log.i(TAG, "dx="+dx + "  dy="+dy);
			//向左 dx > 0 ; 向上 dy > 0
			mRenderer.mAngleX += dx ;//* TOUCH_SCALE_FACTOR;
			mRenderer.mAngleY += dy ;//* TOUCH_SCALE_FACTOR;
			requestRender();
		case MotionEvent.ACTION_DOWN:
			mPreviousX = x;
			mPreviousY = y;
		}
		//mPreviousX = x;
		//mPreviousY = y;
		return true;
	}*/
	
	
	
}
