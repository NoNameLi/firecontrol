package cn.turing.firecontrol.datahandler.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.util.*;


/**
 * 
 * http & https 请求基础类
 * 
 * @author leiyang3
 *
 */
public class HttpClientUtil {

	final private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	public static class HttpRequestType {
		final public static String POST = "post";
		final public static String GET = "get";
	}

	/**
	 * 直接返回json
	 * 
	 * @param httpURL
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static String httpRequest(String httpURL, String requestType, Object obj,String str) throws Exception {
		String respString = "";

		if (httpURL != null && httpURL.length() > 8) {
			if (httpURL.substring(0, 8).toUpperCase().equals("HTTPS://")) {
				respString = https(httpURL, requestType, obj,str);
			} else if (httpURL.substring(0, 7).toUpperCase().equals("HTTP://")) {
				respString = http(httpURL, requestType,obj,str);
			} else {
				throw new Exception("请求异常，没有传入正确的URL信息");
			}
		} else {
			throw new Exception("请求异常，没有传入正确的URL信息");
		}

		return respString;
	}

	/**
	 * http请求
     * @param httpURL
     * @param requestType
	 * @param object
	 * @param str
	 * @return string
	 * @throws Exception
	 */
	private static String http(String httpURL, String requestType, Object object,String str) throws Exception {
		HttpClient httpclient = new DefaultHttpClient();
		String respString = "";

		try {
			HttpPost httppost = null;
			HttpGet httpget = null;
			HttpResponse response = null;
			StatusLine statusLine = null;

			try {

				logger.info("后台HTTP请求地址：" + httpURL);
				if (requestType.equalsIgnoreCase(HttpRequestType.POST)) {

					httppost = new HttpPost(httpURL);
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					StringEntity entity = new StringEntity(str, "utf-8");
					// 设置请求参数
					if ( object  instanceof HttpParams) {
						logger.info("后台HTTP请求参数：" + str);
						nvps.add(new BasicNameValuePair("params", str.toString()));
						httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
					}else if(object instanceof JSONObject){
						entity.setContentType("application/json");
						httppost.setEntity(entity);
					}else if (object instanceof String) {
						entity.setContentType("application/x-www-form-urlencoded");
						httppost.setEntity(entity);
					}if(object instanceof Map){
						Map<String,Object> map=(Map)object;
						for(String key : map.keySet()){
							nvps.add(new BasicNameValuePair(key, map.get(key).toString()));
						}
						UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps);
						httppost.setEntity(formEntity);
					}

					response = httpclient.execute(httppost);
					//response.setContentType("application/json;charset=utf-8");

				} else {
					if (str != null) {
						logger.info("后台HTTP请求参数：" + str);
						//httpURL = httpURL + "params=" + str;
						if(object instanceof Map){
							Map<String,Object> map=(Map)object;
							URIBuilder builder = new URIBuilder(httpURL);
								for (String key : map.keySet()) {
									builder.addParameter(key, map.get(key).toString());
								}
							 httpURL = builder.build().toString();

						}
					}


					httpget = new HttpGet(httpURL);

					response = httpclient.execute(httpget);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("HTTP请求异常，无法请求到地址：" + httppost.getURI());
			}

			statusLine = response.getStatusLine();

			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

				HttpEntity entity = response.getEntity();

				if (entity != null) {
					try {
						respString = EntityUtils.toString(entity);
					} catch (Exception e) {
						e.printStackTrace();
						throw new Exception("HTTP请求异常，无法正常解析请求结果");
					}
				} else {
					respString = "";
					 throw new Exception("HTTP请求异常，没有请求到结果");
				}

				if (requestType.equalsIgnoreCase(HttpRequestType.POST)) {
					httppost.abort();
				} else {
					httpget.abort();
				}
				logger.info("后台HTTP返回结果：" + respString);

				return respString;
			} else {
				throw new Exception("请求到地址：" + httpURL + "错误，HTTP错误代码：" + statusLine.getStatusCode());
			}

		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	/**
	 * https请求
	 * @param httpURL
	 * @param requestType
	 * @param object
	 * @param str
	 * @return string
	 * @throws Exception
	 */
	private static String https(String httpURL, String requestType, Object object,String str) throws Exception {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		String respString = "";

		try {

			SSLContext sslctx = null;
			SSLSocketFactory socketFactory = null;
			Scheme sch = null;

			HttpPost httppost = null;
			HttpGet httpget = null;
			HttpResponse response = null;
			StatusLine statusLine = null;

			try {
				sslctx = SSLContext.getInstance("TLS");
			//	sslctx.init(null, new TrustManager[] { new TrustAnyTrustManager() }, null);

				socketFactory = new SSLSocketFactory(sslctx);
				sch = new Scheme("https", 443, socketFactory);
				httpclient.getConnectionManager().getSchemeRegistry().register(sch);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("HTTPS请求异常，初始化或获取HTTPS证书失败");
			}

			try {

				logger.info("后台HTTP请求地址：" + httpURL);
				if (requestType.equalsIgnoreCase(HttpRequestType.POST)) {
					httppost = new HttpPost(httpURL);

					// 设置请求参数
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					StringEntity entity = new StringEntity(str, "utf-8");
					// 设置请求参数
					if ( object  instanceof HttpParams) {
						logger.info("后台HTTP请求参数：" + str);
						nvps.add(new BasicNameValuePair("params", str.toString()));
						httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
					}else if(object instanceof JSONObject){
						entity.setContentType("application/json");
						httppost.setEntity(entity);
					}else if (object instanceof String) {
						entity.setContentType("application/x-www-form-urlencoded");
						httppost.setEntity(entity);
					}if(object instanceof Map){
						Map<String,Object> map=(Map)object;
						for(String key : map.keySet()){
							nvps.add(new BasicNameValuePair(key, map.get(key).toString()));
						}
						UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps);
						httppost.setEntity(formEntity);
					}

					// httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
					response = httpclient.execute(httppost);
				} else {
					if (str != null) {
						logger.info("后台HTTP请求参数：" + str);
						//httpURL = httpURL + "params=" + str;
						if(object instanceof Map){
							Map<String,Object> map=(Map)object;
							URIBuilder builder = new URIBuilder(httpURL);
							for (String key : map.keySet()) {
								builder.addParameter(key, map.get(key).toString());
							}
							httpURL = builder.build().toString();

						}
					}

					httpget = new HttpGet(httpURL);
					response = httpclient.execute(httpget);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("HTTPS请求异常，无法请求到地址：" + httppost.getURI(), e);
				throw new Exception("HTTPS请求异常，无法请求到地址：" + httppost.getURI());
			}

			statusLine = response.getStatusLine();

			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

				HttpEntity entity = response.getEntity();

				if (entity != null) {
					try {
						respString = EntityUtils.toString(entity);
					} catch (Exception e) {
						e.printStackTrace();
						throw new Exception("HTTPS请求异常，无法正常解析请求结果");
					}
				} else {
					// throw new SystemException("HTTPS请求异常，没有请求到结果");
					respString = "";
				}

				if (requestType.equalsIgnoreCase(HttpRequestType.POST)) {
					httppost.abort();
				} else {
					httpget.abort();
				}
				logger.info("后台HTTP请求结果：" + respString);

				return respString;
			} else {
				throw new Exception("请求到地址：" + httpURL + "错误，HTTPS错误代码：" + statusLine.getStatusCode());
			}

		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
	}

	public static void main(String[] atr)throws Exception{
		HttpParams p=new BasicHttpParams();
		p.setParameter("mac", "f01c29");
		p.setParameter("startTime", "2018-01-18");
		p.setParameter("endTime","2018-12-30");
		p.setParameter("sensorType", "tem");



		/*JSONObject map=new JSONObject();
		map.put("mac", "f01c29");
		map.put("startTime", "2018-01-18");
		map.put("endTime","2018-12-30");
		map.put("sensorType", "tem");*/
		//{"mac":"004a770124007bf0","appeui":"2c26c50124194000","last_update_time":"20180819172445","data":"404019010200011030011d00e1012b00ed00eb00ef000000000000a0b95b","reserver":"null","data_type":2,"gateways":[{"fcntdown":"50","fcntup":"57","gweui":"70B3D5FFFE88B2F5","rssi":"-69","lsnr":"5.0","alti":"null","lng":"null","lati":"null"}]}

		httpRequest("http://125.94.44.140:4396/turing/zhxf/v1test",HttpRequestType.GET,p,p.toString());
	}
}