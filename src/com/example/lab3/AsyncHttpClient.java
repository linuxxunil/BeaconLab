package com.example.lab3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;

public class AsyncHttpClient {
	private URL url = null;
	private HttpURLConnection httpConn = null;
	private String host = "http://demo.coder.com.tw/ibeacon/api/getconfig.php";
	private String method = "POST";
	private String contentType = "application/x-www-form-urlencoded; charset=utf-8";
	
	public AsyncHttpClient() {
	}
	
	private void initHttp()
			throws MalformedURLException, IOException {
		this.url = new URL(host);
		httpConn = (HttpURLConnection) this.url.openConnection();
		httpConn.setRequestMethod(method);
		httpConn.setRequestProperty("content-type", contentType);
		httpConn.setRequestProperty("host", this.url.getHost());
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		httpConn.setReadTimeout(5000);
	}

	private void connect() throws IOException {
		httpConn.connect();
	}

	private void disconnect() {
		httpConn.disconnect();
	}
	
	private void setHost(String host) {
		this.host = host;
	}
	
	private void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	private String doPost(String host, String contentType, String content) {
		String cv = "";
		int status = 0;
		OutputStream os;
		try {
			setHost(host);
			setContentType(contentType);
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

	public String post(final String host, String contentType, final String content)  {
		System.out.println(host);
		System.out.println(content);
		BackGround bg = new BackGround();
		bg.execute(host,contentType, content);
		try {
			String result = bg.get();
			System.out.println(result);
			return result;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch ( Exception e ) {
			System.out.println("POST ERROR");
			e.getStackTrace();
		}
		return null;
	}

	private class BackGround extends AsyncTask <String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			String host = params[0];
			String contentType = params[1];
			String content = params[2];
			return doPost(host, contentType, content);
		}	 
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