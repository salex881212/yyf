package com.alex.yyf.spider.qiniu.config;

public class QiniuConfig {

	public static String getAccessKey(){
		return "b7lErKg2K_3pY0lSq7T_m9vbGxuZQpkNrxO8EOg1";
	}
	
	public static String getSecretKey(){
		return "UBC0Ce2W0PtVIQua2CJV6gdjW5FcwIm-d0YJxVrS";
	}
	
	public static String getWxImgURL(String key){
		return "http://wx-img.qiniudn.com/" + key;
	}
	
	public static String getDrugImgURL(String key){
		return "http://alex-drug.qiniudn.com/" + key;
	}
	
	public static String getBuckerName(){
		return "wx-img";
	}
	
	public static String getDrugBuckerName(){
		return "alex-drug";
	}
	
	public static String getMimeType(){
		return "image/jpg";
	}
}
