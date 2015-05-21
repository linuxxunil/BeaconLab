package com.example.lab2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class HttpService extends Thread {
	private BlockingQueue<String> queue;
	private boolean running = true;
	
	public HttpService(int requestSize) {
		queue = new ArrayBlockingQueue<String>(requestSize);
	}
	
	public void run() {
		HttpClient http = new HttpClient();
		while (running) {
			String data;
			try {
				data = queue.take();
				String[] split = data.split("?");
				http.post(split[0], split[1]);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean offer(String url,String parm) {
		String data = url + "?" + parm;
		return queue.offer(data);
	} 
}
