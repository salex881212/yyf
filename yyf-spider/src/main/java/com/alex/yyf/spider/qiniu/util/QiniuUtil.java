package com.alex.yyf.spider.qiniu.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;

import com.alex.yyf.spider.qiniu.config.QiniuConfig;
import com.alex.yyf.spider.utils.HttpClientUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.BatchStatRet;
import com.qiniu.api.rs.EntryPath;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.api.rs.RSClient;

public class QiniuUtil {

	public static String getToken(String buckerName) throws AuthException, JSONException{
		Mac mac = new Mac(QiniuConfig.getAccessKey(), QiniuConfig.getSecretKey());
        // 请确保该bucket已经存在
        PutPolicy putPolicy = new PutPolicy(buckerName);
        return putPolicy.token(mac);
	}
	
	public static int uploadFileFromLocal(String buckerName, String key, String file) throws AuthException, JSONException{
		String token = getToken(buckerName);
		PutExtra extra = new PutExtra();
        PutRet ret = IoApi.putFile(token, key, file, extra);
        return ret.getStatusCode();
	}
	
	public static int uploadFileFromURLWithReffered(String buckerName, String key, String mimeType, String file) throws AuthException, JSONException, IOException{
		String token = getToken(buckerName);
		PutExtra extra = new PutExtra();
		extra.mimeType = mimeType;
		InputStream is = HttpClientUtils.getWithRefererForInputStream(file, "baidu.com", 2, 30000);
        PutRet ret = IoApi.Put(token, key, is, extra);
        
		is.close();
        return ret.statusCode;
	}
	
	public static void checkExsit(String buckerName, List<String> keys){
		 Mac mac = new Mac(QiniuConfig.getAccessKey(), QiniuConfig.getSecretKey());
		 RSClient rs = new RSClient(mac);
		 List<EntryPath> entries = new ArrayList<EntryPath>();
		 for(String key : keys){
			 EntryPath e = new EntryPath();
		     e.bucket = buckerName;
		     e.key = key;
		     entries.add(e);
		 }
		 BatchStatRet bsRet = rs.batchStat(entries);
//		 System.out.println(bsRet);
	     JSONArray jsonArr = JSONArray.parseArray(bsRet.toString());
	     if(jsonArr.size()!=keys.size()){
	    	 return;
	     }
	     
	     for(int i=jsonArr.size()-1;i>=0;i--){
	    	 JSONObject jsonObj = (JSONObject)(jsonArr.get(i));
	    	 if(jsonObj.getIntValue("code") == 200){
	    		 keys.remove(i);
	    	 }
	     }
	}
	
	public static void main(String args[]) throws AuthException, JSONException, IOException{
//		System.out.println(uploadFileFromURL("wx-img","image-test2.jpg","image/jpg","http://t12.baidu.com/it/u=416592824,17409986&fm=58"));
		checkExsit("wx-img",Arrays.asList(new String[]{"2.jpg","1.jpg"}));
	}
}
