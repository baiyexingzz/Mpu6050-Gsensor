package com.bytereal.sensor6050demo.ui.mpu6050;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



import android.opengl.GLSurfaceView.Renderer;



public class Render implements Renderer {

	private static final String TAG = "Render";
	private Cube mCube;
	public float mAngleX;
	public float mAngleY;
	public float mAngleZ;
	public Render() {
		mCube = new Cube();
	
	}

	public void onDrawFrame(GL10 gl) {
		/*
		 * Usually, the first thing one might want to do is to clear
		 * the screen. The most efficient way of doing this is to use
		 * glClear().
		 */

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		/*
		 * Now we're ready to draw some 3D objects
		 */


		gl.glMatrixMode(GL10.GL_MODELVIEW);	//模型视图
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, -3.0f);	//平移

		/**
		 *  void glRotatef(GLfloat angle,  GLfloat x,  GLfloat y,  GLfloat z);
		 *  其中,angle为旋转的角度,单位为度.重点是x,y,z表达的是�?��意�?�?应该如何取�?�?
		 *  如果说x,y,z表达的是�?��坐标(x,y,z),那么这个函数就说明了当前几何图形围着这个坐标点旋�?但往哪个方向旋转�?
		 *  �?��很明�?x,y,z表达的意思并不是坐标�?而是要围绕哪个坐标轴旋转.即其实这里的x,y,z值相当于�?��布尔值，
		 *  0.0表示假，而非零参数则表示真�?�?��如果你想让当前的几何图形围绕�?轴旋转，那么x和y都设�?，�?z设为非零值即可�?
		 *  如果这里的x,y,z的�?都设置为0.0，那么将围绕�?轴旋转�?还有�?���?��注意，如果设置的旋转值（x,y,z的�?）为正数�?
		 *  那么旋转的方向是逆时针的，如果旋转�?是负数，那么旋转的方向是顺时针的�?
		 *  OpenGL ES是基于浮点运算的，iPhone有专门的浮点运算器，�?��，要习惯OpenGL ES中使用浮点数的表达方式�?
		 */

        gl.glRotatef(mAngleX, 1, 0, 0);	//绕x旋转
        gl.glRotatef(mAngleY, 0, 1, 0);   //
        gl.glRotatef(-mAngleZ, 0, 0, 1);   //

	//	MyLog.show(TAG, "angle"+mAngleZ);
//		gl.glRotatef(mAngleZ, 0, 0, 1);
		//Enabled the vertex buffer for writing and to be used during rendering
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY); //允许设置顶点数组
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);  //允许设置颜色数组
		mCube.draw(gl);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);

		/*
		 * Set our projection matrix. This doesn't have to be done
		 * each time we draw, but usually a new projection needs to
		 * be set when the viewport is resized.
		 */

		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		/*
		 * By default, OpenGL enables features that improve quality
		 * but reduce performance. One might want to tweak that
		 * especially on software renderer.
		 */
		gl.glDisable(GL10.GL_DITHER);

		/*
		 * Some one-time OpenGL initialization can be made here
		 * probably based on features of this particular context
		 */
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
				GL10.GL_FASTEST);

		gl.glClearColor(0,0,0,0);
//		gl.glClearColor(1,1,1,1);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}

}

