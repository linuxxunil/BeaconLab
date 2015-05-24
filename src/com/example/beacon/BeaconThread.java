package com.example.beacon;

public class BeaconThread extends Thread{
	protected boolean running = true;
	public void myStart() {
		running = true;
		start();
	}
	public void myStop() {
		running = false;
	}
}
