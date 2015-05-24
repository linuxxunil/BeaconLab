package com.example.lab3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {
	private URL url = null;
	private HttpURLConnection httpConn = null;
	private static int i = 0;
	private String sUrl = "http://demo.coder.com.tw/ibeacon/api/getconfig.php";
	private String method = "POST";
	private String contentType = "application/x-www-form-urlencoded; charset=utf-8";
	
	public HttpClient() {
	}
	
	public HttpClient(String url, String method, String contentType) {
		this.sUrl = url;
		this.method = method;
		this.contentType = contentType;
	}
	
	private void initHttp()
			throws MalformedURLException, IOException {
		this.url = new URL(sUrl);
		httpConn = (HttpURLConnection) this.url.openConnection();
		httpConn.setRequestMethod(method);
		httpConn.setRequestProperty("content-type", contentType);
		httpConn.setRequestProperty("host", this.url.getHost());
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		httpConn.setReadTimeout(10000);
	}

	private void connect() throws IOException {
		httpConn.connect();
	}

	private void disconnect() {
		httpConn.disconnect();
	}
	
	private String doPost(final String host, final String content) {
		String cv = "";
		int status = 0;
		OutputStream os;
		try {
			initHttp();
			connect();
			os = httpConn.getOutputStream();
			os.write(content.getBytes());
			status = httpConn.getResponseCode();		
			if (status == HttpURLConnection.HTTP_OK) {
				InputStream is = httpConn.getInputStream();
				byte[] data = new byte[1024];
				int idx = is.read(data);
				cv = new String(data, 0, idx);
				is.close();
				os.close();
				disconnect();
			}
		} catch (MalformedURLException e) {
			e.getStackTrace();
		} catch (IOException e) {
			e.getStackTrace();
		} 
		return cv;
	}

	public String post(final String host, final String content)  {
		System.out.println(host);
		System.out.println(content);
		String result = doPost(host,content);
		System.out.println(result);
		return result;
	}
	
	static public Map<String, String> httpParmToMap(String content) {
		String[] params = content.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String param : params) {
			String name = param.split("=")[0];
			String value = param.split("=")[1];
			map.put(name, value);
		}
		return map;
	}
}