package com.alex.yyf.spider.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.json.JSONException;

import com.alex.yyf.spider.domain.drug.Drug;
import com.alex.yyf.spider.domain.drug.DrugDto;
import com.alex.yyf.spider.domain.drug.DrugListDto;
import com.alex.yyf.spider.qiniu.config.QiniuConfig;
import com.alex.yyf.spider.qiniu.util.QiniuUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qiniu.api.auth.AuthException;

@Log4j
public class DrugUtil {

	private static final String list_uri = "http://api.yi18.net/drug/list";
	private static final String detail_uri = "http://api.yi18.net/drug/show";
	private static final String host = "http://www.yi18.net/";
	private static Map<String,String> map = Maps.newHashMap();
	private static List<String> error_img = Lists.newArrayList();
	private static List<String> error_query = Lists.newArrayList();
	
	public static List<String> query(int pageNo, int pageSize, String type) throws AuthException, JSONException, IOException{
		Map<String,String> params = Maps.newHashMap();
		params.put("page", String.valueOf(pageNo));
		params.put("limit", String.valueOf(pageSize));
		params.put("type", type);
		
		String response = HttpClientUtils.post(list_uri, params);
		System.out.println(response);
		
		DrugListDto dto =  JsonUtils.fromJson(response, DrugListDto.class);
		
		List<String> drugs = Lists.newArrayList();
		
		for(Drug drug : dto.getYi18()){
			
			drugs.add(getDetail(drug.getId()).toString());
		}
		
		return drugs;
	}
	
	public static Drug getDetail(Long id) throws AuthException, JSONException, IOException{
		Map<String,String> params = Maps.newHashMap();
		params.put("id", String.valueOf(id));
		String response = HttpClientUtils.post(detail_uri, params);
//		System.out.println(response);
		
		DrugDto dto = JsonUtils.fromJson(response, DrugDto.class);
		String img = dto.getYi18().getImage();
		String url = host + img;
		String key = img.substring(img.lastIndexOf('/')+1);
		dto.getYi18().setImage(QiniuConfig.getDrugImgURL(key));
		
//		upload(url,key);
		map.put(key, url);
		
		dto.getYi18().setMessage(HTMLReplaceUtils.handle1(dto.getYi18().getMessage()));
		return dto.getYi18();
	}
	
//	public static void upload(String url,String key) throws AuthException, JSONException, IOException{
//		System.out.println(QiniuUtil.uploadFileFromURLWithReffered(QiniuConfig.getDrugBuckerName(), key, QiniuConfig.getMimeType(), url));
//	}
	
	private static void handleUrl(Map<String,String> map) throws AuthException, JSONException, IOException{
		
			List<String> keys = Lists.newArrayList(map.keySet());
		try{
			QiniuUtil.checkExsit(QiniuConfig.getDrugBuckerName(), keys);
			
			log.info("upload " + keys.size() + " images! keys = " + keys);
			for(String key : keys){
				QiniuUtil.uploadFileFromURLWithReffered(QiniuConfig.getDrugBuckerName(), key, 
						QiniuConfig.getMimeType(), map.get(key));
			}
		}catch(Exception e){
			log.error(e);
			for(String key : keys){
				error_img.add(key + "\t" + map.get(key));
			}
			
			
		}
	}
	
	public static void main(String args[]) throws IOException, AuthException, JSONException{
		int pageNo = 1;
		List<String> outs = Lists.newArrayList();
		while(true){
			System.out.println("current page " + pageNo);
			try{
				List<String> drugs = query(pageNo++,50,"id");
				if(drugs.size() == 0){
					break;
				}
				outs.addAll(drugs);
				handleUrl(map);
				map.clear();
			}catch(Exception e){
				log.error(e);
				error_query.add(String.valueOf(pageNo));
			}
		}
		
		FileUtils.write(outs, "D:\\t.sql");
		FileUtils.write(error_img, "D:\\error_img.txt");
		FileUtils.write(error_query, "D:\\error_query.txt");
	}
}
