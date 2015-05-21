package com.example.lab1;

public class MyThread extends Thread{
	protected boolean running = true;
	protected void myStart() {
		running = true;
		start();
	}
	protected void myStop() {
		running = false;
	}
}
