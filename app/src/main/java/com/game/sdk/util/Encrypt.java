package com.game.sdk.util;

import org.apache.commons.codec.binary.Base64;

import com.game.sdk.YTAppService;

import android.util.Log;

public class Encrypt {
	
	public static String encode(String str) {
		char[] k=YTAppService.k;
		
		if (str == null) {
			return "";
		}
		str = encodeStr(str);
		StringBuffer sb = new StringBuffer();
		char[] array = str.toCharArray();
		sb.append("x");
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i] + k[(i % k.length)]);
			sb.append("_");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("y");
		return sb.toString();
	}

	public static String decode(String str) {
		char[] k=YTAppService.k;
		if (str == null) {
			return null;
		}
		if ((str.startsWith("x")) && (str.endsWith("y"))) {
			StringBuffer sb = new StringBuffer(str);
			sb.deleteCharAt(0);
			sb.deleteCharAt(sb.length() - 1);
			str = sb.toString();
			String[] strs = str.split("_");
			sb = new StringBuffer();
			for (int i = 0; i < strs.length; i++) {
				sb.append((char) (Integer.parseInt(strs[i]) - k[(i % k.length)]));
			}
			
			return decodeStr(sb.toString());
		}
		return "";
	}
	
	public static String decode2(String str) {
		char[] k=YTAppService.k;
		if (str == null) {
			return null;
		}
		
		if ((str.startsWith("x")) && (str.endsWith("y"))) {
			StringBuffer sb = new StringBuffer(str);
			sb.deleteCharAt(0);
			sb.deleteCharAt(sb.length() - 1);
			str = sb.toString();
			String[] strs = str.split("_");
			sb = new StringBuffer();
			for (int i = 0; i < strs.length; i++) {
				sb.append((char) (Integer.parseInt(strs[i]) - k[(i % k.length)]));
			}
			
			return decodeStr(sb.toString());
		}
		return "";
	}

	/**
	 * @param str
	 * @return
	 */
	public static String decodeStr(String str) {
		byte[] debytes = Base64.decodeBase64(new String(str).getBytes());
		return new String(debytes);
	}

	/**
	 * @param str
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public static String encodeStr(String str) {
		byte[] enbytes = Base64.encodeBase64Chunked(str.getBytes());
		return new String(enbytes);
	}
}
