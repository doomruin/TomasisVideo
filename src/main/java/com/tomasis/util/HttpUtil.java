package com.tomasis.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


/**
 * 访问服务器 解析结果
 * 
 * @author ironkey
 * 
 */
public class HttpUtil {

	
	
	public static DefaultHttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager());

	public static final String BASE_URL = null;

	public static String LIB_TOP_URL = "";
	private static String cookie = "";
	private static String location = "";
	
	public static String IO_EXCEPTION="ioexception";
	
	
	/**
	 * 一个通用的httpPost请求函数，返回String
	 */
	
	public static String httpClient_post(String url, Map<String, String> rawParams){
		String result = "1";
		HttpPost post = new HttpPost(url);
        HttpParams param = client.getParams();
        HttpConnectionParams.setConnectionTimeout(param, 30000);
        HttpConnectionParams.setSoTimeout(param, 60000);

        HttpResponse httpResponse = null;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (String key : rawParams.keySet()) {
			// 封装请求参数
			params.add(new BasicNameValuePair(key, rawParams.get(key)));
			//Log.i("param "+key+":", rawParams.get(key));
		}			
		try {
			post.setEntity(new UrlEncodedFormEntity(params,
					HTTP.UTF_8));
			httpResponse = client.execute(post);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				 result = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
			}

		} catch(ConnectionPoolTimeoutException e) {
            return "E";
        }catch(UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = IO_EXCEPTION;
		}finally {
            if(httpResponse != null) {
                try {
                    EntityUtils.consume(httpResponse.getEntity()); //会自动释放连接
                }catch (IOException e){
                    e.printStackTrace();
                    result = IO_EXCEPTION;
                }
            }
        }
        return result;
	}

}
