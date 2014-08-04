package com.alex.yyf.spider.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * 
 * 2014-3-22
 */
public class JsonUtils {
	
	private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	/**
	 * 
	 * @param <T>
	 * @param o
	 * @return
	 */
	public static <T> String toJson(T o) {
		String s = "";
		if(o == null) {
			return s;
		}
		
		s = gson.toJson(o);
				
		return s;
	}
	
	/**
	 * 从json转到对象
	 * @param <T>
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T fromJson(String json, Class<T> clazz) {	
		return gson.fromJson(json, clazz);
	}	
}
