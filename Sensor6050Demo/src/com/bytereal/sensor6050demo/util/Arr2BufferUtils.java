package com.bytereal.sensor6050demo.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 *2014��7��22��16:27:23
 * @author Luv
 *
 */
public class Arr2BufferUtils {

	public static IntBuffer getBuffer(int [] vertices) {
		IntBuffer   buff;
		//int -> 4byte
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
		vbb.order(ByteOrder.nativeOrder()); //设置字节顺序 ->nativeOrder 本机字节顺序

		buff = vbb.asIntBuffer();
		buff.put(vertices);
		buff.position(0); //设置缓冲区起始位�?
		return buff;
	}

	public static ByteBuffer getBuffer(byte [] arr) {
		ByteBuffer buff = ByteBuffer.allocateDirect(arr.length);
		buff.put(arr);
		buff.position(0);
		return buff;
	}
	
	public static FloatBuffer getBuffer(float [] arr) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(arr.length * 4);  
		vbb.order(ByteOrder.nativeOrder());  
		FloatBuffer vertexBuffer = vbb.asFloatBuffer();  
		vertexBuffer.put(arr);  
		vertexBuffer.position(0);  
		return vertexBuffer;
	}
	
	public static ShortBuffer getBuffer(short [] arr) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(arr.length * 2);  
		vbb.order(ByteOrder.nativeOrder());  
		ShortBuffer vertexBuffer = vbb.asShortBuffer();  
		vertexBuffer.put(arr);  
		vertexBuffer.position(0);  
		return vertexBuffer;
	}
}
