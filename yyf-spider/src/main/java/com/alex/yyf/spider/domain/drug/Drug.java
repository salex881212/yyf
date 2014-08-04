package com.alex.yyf.spider.domain.drug;

import org.apache.commons.lang.StringEscapeUtils;

import lombok.Data;

@Data
public class Drug {

	private Long id;
	private String name;
	private String tag;
	private String message;
	private String image;
	private String ANumber;
	private String PType;
	private String categoryName;
	private Long category;
	private Double price;
	private String factory;
	
	
	@Override
	public String toString(){
		String s=  "insert into drug_info(id,name,tag,description, image,serial_number,type,category_name,category_id,price,factory,addtime, updatetime) "
				+ "values(%d,'%s','%s','%s','%s','%s','%s','%s',%d,%f,'%s',now(),now());\n";
		
		return String.format(s, id, StringEscapeUtils.escapeSql(name),StringEscapeUtils.escapeSql(tag),StringEscapeUtils.escapeSql(message), 
				StringEscapeUtils.escapeSql(image), StringEscapeUtils.escapeSql(ANumber), StringEscapeUtils.escapeSql(PType), 
				StringEscapeUtils.escapeSql(categoryName),category,price, StringEscapeUtils.escapeSql(factory));
	}
	
}
