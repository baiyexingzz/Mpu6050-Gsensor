package com.bytereal.sensor6050demo.util;

public class StringUtil {

	public static String bytesToHex(byte[] bytes,int begin,int length) {
		StringBuilder sbuf = new StringBuilder();
		for(int idx=begin; idx < begin+length; idx++) {
			int intVal = bytes[idx] & 0xff;
			if (intVal < 0x10) sbuf.append("0");
			sbuf.append(Integer.toHexString(intVal).toUpperCase());
		}
		return sbuf.toString();
	}
}
