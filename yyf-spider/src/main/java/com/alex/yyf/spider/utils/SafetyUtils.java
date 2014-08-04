package com.alex.yyf.spider.utils;


/**
 *
 * @author nick
 * 2014-3-26
 */
public class SafetyUtils {
	public static boolean isSafe(String sign, Object... keyvalues) {
		if(keyvalues.length == 0 || keyvalues.length %2 == 1) {
			return false;
		}
		String md5 = genMd5(keyvalues);
		return sign.equals(md5);		
	}
	
	public static String genMd5(Object... keyvalues) {
		StringBuilder sb = new StringBuilder();
		String template = "%s:%s";
		char delim = ',';
		int length = keyvalues.length / 2;
		
		for(int i=0; i<length; ++i) {
			sb.append(String.format(template, new Object[] {
					keyvalues[i*2], keyvalues[i*2+1]
			})).append(delim);
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		return MD5Utils.md5(sb.toString());
	}
	
	public static void main(String args[]) {
		String data = "{\"bills\":[{\"feedback\":\"frombaidu\",\"orderId\":\"12358475\",\"orderTime\":\"1382568748\",\"orderStatus\":\"New\",\"commissionStatus\":\"New\",\"commission\":80000,\"preCommission\":118,\"channel\":\"web\",\"elements\":[{\"outerId\":\"abeij223\",\"name\":\"iphone4s\",\"number\":1,\"price\":4100},{\"outerId\":\"abeij224\",\"name\":\"iphone5s\",\"number\":5,\"price\":4100}]}]}";
		
		String sign = genMd5(new Object[]{
			"token", "a3526adca9bd569dbb541231ee4f75c5",
			"merchant_id",6681496,
			"timestamp","1395871118611",
			"data",data	
		});
		System.out.println(sign);
	}
}
