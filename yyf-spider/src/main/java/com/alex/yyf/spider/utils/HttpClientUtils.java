package com.alex.yyf.spider.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author nick
 * 2013-3-19
 */
@Log4j
public class HttpClientUtils {

    /**
     * 发送JSON类型的POST请求，当遇到异常时返回""
     * 
     * @param url
     * @param jsonStr
     * @param charSet	default=UTF-8
     * @return
     */
    public static String postJson(String url, String jsonStr, String charSet) {
        if (null == charSet) {
            charSet = "UTF-8";
        }
        HttpClient httpClient = createClient();
        HttpPost post = new HttpPost(url);
        try {
            @SuppressWarnings("deprecation")
            StringEntity se = new StringEntity(jsonStr, "application/json", charSet);
            post.setEntity(se);
            HttpResponse response = httpClient.execute(post);
            return paseResponse(response, charSet);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return "";
        }
    }

    /**
     * post a json with client configure
     * 
     * @param url
     * @param jsonStr
     * @param charSet
     * @param retry
     * @param waiting
     * @return "" when meet exception.
     */
    public static String postJsonWithConfigure(String url, String jsonStr, String charSet, int retry, int waiting) {
        if (null == charSet) {
            charSet = "UTF-8";
        }
        HttpClient httpClient = createClient(retry, waiting);
        HttpPost post = new HttpPost(url);
        try {
            @SuppressWarnings("deprecation")
            StringEntity se = new StringEntity(jsonStr, "application/json", charSet);
            post.setEntity(se);
            HttpResponse response = httpClient.execute(post);
            return paseResponse(response, charSet);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return "";
        }
    }
    
    public static String getWithReferer(String url, String referer, int retry, int timeout){
        DefaultHttpClient httpclient = new DefaultHttpClient();
        String body = null;
        BwopHttpRequestRetryHandler retryHandler = new BwopHttpRequestRetryHandler(retry);
        httpclient.setHttpRequestRetryHandler(retryHandler);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);

        log.debug("create httppost:" + url);
        HttpGet get = new HttpGet(url);
        get.setHeader("Referer", referer);
        body = invoke(httpclient, get);

        httpclient.getConnectionManager().shutdown();

        return body;
    }
    
    public static InputStream getWithRefererForInputStream(String url, String referer, int retry, int timeout){
        DefaultHttpClient httpclient = new DefaultHttpClient();
        InputStream body = null;
        BwopHttpRequestRetryHandler retryHandler = new BwopHttpRequestRetryHandler(retry);
        httpclient.setHttpRequestRetryHandler(retryHandler);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);

        log.debug("create httppost:" + url);
        HttpGet get = new HttpGet(url);
        get.setHeader("Referer", referer);
        body = invokeForInputStream(httpclient, get);

//        httpclient.getConnectionManager().shutdown();

        return body;
    }
    

    public static String post(String url, Map<String, String> params) {
        HttpClient httpclient = createClient(1,500000);
        String body = null;

        log.debug("create httppost:" + url);
        HttpPost post = postForm(url, params);

        body = invoke(httpclient, post);
        httpclient.getConnectionManager().shutdown();

        return body;
    }
    

    /**
     * 
     * @param url
     * @param params
     * @param retry
     * @param timeout
     * @return
     */
    public static String postWithConfigure(String url, Map<String, String> params, int retry, int timeout) {
        HttpClient httpclient = createClient(retry, timeout);
        String body = null;

        log.debug("create httppost:" + url);
        HttpPost post = postForm(url, params);

        body = invoke(httpclient, post);
        httpclient.getConnectionManager().shutdown();

        return body;
    }

    public static HttpResponse postWithResponse(String url, Map<String, String> params) {
        HttpClient httpclient = createClient();

        log.debug("create httppost:" + url);
        HttpPost post = postForm(url, params);

        HttpResponse response = sendRequest(httpclient, post);
        httpclient.getConnectionManager().shutdown();

        return response;
    }

    public static String post(String url, Map<String,String> params,int retry,int timeout) {
        HttpClient httpClient = createClient(retry,timeout);
        String body = null;

        log.debug("create httppost:" + url);
        HttpPost post = postForm(url, params);

        body = invoke(httpClient, post);
        httpClient.getConnectionManager().shutdown();

        return body;
    }

//    public static String get(String url) {
//        DefaultHttpClient httpclient = new DefaultHttpClient();
//        String body = null;
//
//        log.debug("create httppost:" + url);
//        HttpGet get = new HttpGet(url);
//        body = invoke(httpclient, get);
//
//        httpclient.getConnectionManager().shutdown();
//
//        return body;
//    }
    
    
    public static HttpClient createClient() {
        return createClient(2,5000);
    }

    /**
     * 重试，超时
     * @param retry
     * @param timeout
     * @return
     */
    public static HttpClient createClient(int retry, int timeout) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        //DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(retry,false);
        BwopHttpRequestRetryHandler retryHandler = new BwopHttpRequestRetryHandler(retry);
        httpClient.setHttpRequestRetryHandler(retryHandler);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
        return httpClient;
    }

    private static String invoke(HttpClient httpClient,
            HttpUriRequest httpost) {
        return invoke(httpClient, httpost, "UTF-8");
    }
    
    private static String invoke(HttpClient httpClient, HttpUriRequest httpost, String encode) {
    	String body = null;
    	HttpResponse response = sendRequest(httpClient, httpost);
    	if(response.getStatusLine().getStatusCode() == 200) {
    		body = paseResponse(response, encode);
    	} else {
    		log.error("http error code:"+response.getStatusLine().getStatusCode());
    	}
    	
    	return body;
    }
    
    private static InputStream invokeForInputStream(HttpClient httpClient, HttpUriRequest httpost) {
    	InputStream body = null;
    	HttpResponse response = sendRequest(httpClient, httpost);
    	if(response.getStatusLine().getStatusCode() == 200) {
    		body = parseResponseForInputStream(response);
    	} else {
    		log.error("http error code:"+response.getStatusLine().getStatusCode());
    	}
    	
    	return body;
    }

    private static String paseResponse(HttpResponse response) {
        HttpEntity entity = response.getEntity();

        String body = null;
        try {
            body = EntityUtils.toString(entity);
            log.debug(body);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        return body;
    }
    
    private static InputStream parseResponseForInputStream(HttpResponse response) {
        HttpEntity entity = response.getEntity();

        InputStream body = null;
        try {
            body = entity.getContent();
            log.debug(body);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        return body;
    }

    @SuppressWarnings("unused")
    private static String paseResponse(HttpResponse response, String charset) {
        if (null==charset) {
            charset="UTF-8";
        }
        HttpEntity entity = response.getEntity();

        String body = null;
        try {
            body = EntityUtils.toString(entity, charset);
            log.debug(body);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        return body;
    }

    private static HttpResponse sendRequest(HttpClient httpclient,
            HttpUriRequest httpost) {
        log.debug("execute post...");
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpost);
        } catch (ClientProtocolException e) {
            log.warn(e.getMessage(), e);
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
        return response;
    }

    private static HttpPost postForm(String url, Map<String, String> params) {

        HttpPost httpost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }

        try {
            log.debug("set utf-8 form entity to httppost");
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage(), e);
        }

        return httpost;
    }
    
//    public static String get(String uri, Map<String, String> params) throws Exception {
//    	DefaultHttpClient httpclient = new DefaultHttpClient();
//    	String body = null;
//    	
//    	HttpGet get = new HttpGet(uri);
//    	URIBuilder u = new URIBuilder(get.getURI());
//    	for(Map.Entry<String, String> entry : params.entrySet()) {
//    		u.addParameter(entry.getKey(), entry.getValue());
//    	}
//    	((HttpRequestBase)get).setURI(u.build());
//    	body = invoke(httpclient, get);
//    	httpclient.getConnectionManager().shutdown();
//    	return body;
//    }
    
    public static String get(String uri, Map<String, String> params, String encode, int connectionTimeout, int soTimeout) throws Exception {
    	String url = null;
    	String ret = null;
//    	RequestConfig config = RequestConfig.custom()
//    		.setSocketTimeout(soTimeout)
//    		.setConnectTimeout(connectionTimeout)
//    		.setConnectionRequestTimeout(connectionTimeout).build();
    	
    	if(params != null && params.size() > 0) {
    		StringBuilder sb = new StringBuilder();
        	for(Map.Entry<String, String> entry : params.entrySet()) {
        		sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
        	}
        	sb.deleteCharAt(sb.lastIndexOf("&"));
        	if(uri.contains("?")) {
        		url = uri = "&" + sb.toString();
        	} else {
        		url = uri + "?" + sb.toString();
        	}        	
    	} else {
    		url = uri;
    	}
    	
    	HttpClient httpClient = new DefaultHttpClient();
    	HttpGet get = new HttpGet(url);
    	HttpParams httpParams = httpClient.getParams();
    	HttpConnectionParams.setConnectionTimeout(httpParams, 30000);  
        HttpConnectionParams.setSoTimeout(httpParams, 30000); 
//    	get.setConfig(config);
    	get.setHeader("Connection", "close");
    	
    	try {
    		HttpResponse response = httpClient.execute(get);
    		try {
    			HttpEntity entity = response.getEntity();
    			try {
    				if(entity != null) {
    					ret = EntityUtils.toString(entity, encode);
    				}
    			} finally {
    				if(entity != null) {
    					entity.getContent().close();
    				}
    			}
    		} catch(Exception e) {
    			log.error(String.format("get error, url:%s", url), e);
    			return ret;
    		} finally {
    			if(response != null) {
    				httpClient.getConnectionManager().shutdown();
    			}
    		}
    	} catch(SocketTimeoutException e) {
    		log.error(String.format("socket timeout url:%s", url), e);
    	} catch(Exception e) {
    		log.error(String.format("get error, url:%s", url), e);
    	} finally {
    		if(httpClient!=null){
    			httpClient.getConnectionManager().shutdown();
    		}
    	}
    	return ret;
    }
    
    public static void main(String args[]) throws IOException, InterruptedException {
//    	HttpClient client = new DefaultHttpClient();
//    	HttpHead head = new HttpHead("http://share.mogujie.com/cps/data/baidu/products_full.txt");
//    	HttpResponse response = client.execute(head);
//    	
//    	Header[] s = response.getAllHeaders();
//    	
//    	for(Header h : s) {
//    		System.out.println(h.getName()+":"+h.getValue());
//    	}
    	
    	URL url = new URL("http://share.mogujie.com/cps/data/baidu/products_full.txt");
    	URLConnection connection = url.openConnection();
    	connection.connect();
    	long time = connection.getLastModified();
    	
    	Date date = new Date(time);
    	System.out.println(time);
    	System.out.println(System.currentTimeMillis());
    	System.out.println(date);
    	System.out.println(date.toGMTString());
    }
}
