package com.example.beacon;

public class BeaconBase {
	private String mac;
	private String uuid;
	private int major;
	private int minor;
	private int rssi;
	static private double n = 2.92;
	static private int A = -70; 
	
	protected BeaconBase(String mac, String uuid, int major, int minor) {
		this.mac = mac;
		this.uuid = uuid;
		this.major = major;
		this.minor = minor;
	}

	static public double toMeter(double rssi) {
		double tmp = -(rssi - A) / (10 * n);
		return Math.pow(10, tmp);
	}
	
	public void setBenchmark(int dBmAtOneMeter) {
		A = dBmAtOneMeter;
	}
	
	public String getUUID() {
		return uuid;
	}
	
	public int getMajor() {
		return major;
	}
	
	public int getMinor() {
		return minor;
	}
	
	public String getMAC() {
		return mac;
	}
	
	public void setRSSI(int rssi) {
		this.rssi = rssi;
	}
	
	public int getRSSI() {
		return rssi;
	}
}

