package com.alex.yyf.spider.utils;

import java.io.InputStream;
import java.security.MessageDigest;

/**
 * 
 * 
 * 2014-3-22
 */
public class MD5Utils {
	private static char hexDigits[] = {
		'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'
	};
	
	public static String md5(Object ... params) {
		if(params == null || params.length <= 0) {
			throw new RuntimeException("...");
		}
		StringBuffer sb = new StringBuffer();
		for(Object o : params) {
			sb.append(o.toString());
		}
		return md5(sb.toString());
	}
	
	public static String md5(InputStream is) {
		String ret = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			byte[] dataBytes = new byte[1024];
			int nread = 0;
			while((nread = is.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
//				System.out.print(dataBytes);
//				for(byte b : dataBytes) {
//					System.out.print(b);
//				}
//				System.out.println();
//				System.out.println(nread);
			}
			byte[] mdbytes = md.digest();
			ret = md5byte2Str(mdbytes);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static String md5(String str) {
		String ret = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bs = str.getBytes("UTF-8");
//			System.out.println(bs);
//			for(byte b : bs) {
//				System.out.print(b);
//			}
//			System.out.println();
//			System.out.println(bs.length);
			md.reset();
			md.update(bs);
			byte[] md5bytes = md.digest();
			return md5byte2Str(md5bytes);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static String md5byte2Str(byte[] md5bytes) {
		char md5chars[] = new char[32];
		int k = 0;
		for(byte b : md5bytes) {
			md5chars[k++] = hexDigits[b >>> 4 & 0xf];
			md5chars[k++] = hexDigits[b & 0xf];
		}
		return new String(md5chars);
	}
	
	public static void main(String args[]){
		String ss = "100299561,1000391262,1000347757,100297709,1000010231,1104260399,1000010089,1116670290,1116620016,1110620161,1112530032,1000039426,9128930967,100293707,100285150,1000369438,1000353340,1000392524,1000376686,1000341380,1118220181,1115410200,1118220133,1107660210,1115900229,1116120051,1105420249,1118220176,1000167156,1000265702,1000362966,1000386144,1105620056,1118100333,1000057889,1181991015,1111030060,1000028561,1112830082,1116790103";
		String[] sa = ss.split(",");
		for(String s : sa){
			System.out.println(md5(s));
		}
//		System.out.println(md5("1118910026"));
//		System.out.println(md5("100299561"));
	}
}
