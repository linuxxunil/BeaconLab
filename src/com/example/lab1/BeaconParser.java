package com.example.lab1;

public class BeaconParser {
	static private int uuidStart = 9;
	static private int uuidEnd = 24;
	static private int majorEnd = 26;
	static private int minorEnd = 28;
	static private String uuid = "";
	static private int major, minor;

	static public String getUUID(byte[] scanRecord) {
		uuid = "";
		for (int i = uuidStart; i <= uuidEnd; i++) {
			uuid += String.format("%02x", scanRecord[i]);
			if (i == 12 || i == 14 || i == 16 || i == 18)
				uuid += "-";
		}
		return uuid;
	}

	static public int getMajor(byte[] scanRecord) {
		int mask = 0xFF;
		major = 0;
		major = scanRecord[majorEnd] & mask;
		major += (scanRecord[majorEnd - 1] & mask) << 8;
		return major;
	}

	static int getMinor(byte[] scanRecord) {
		int mask = 0xFF;
		minor = 0;
		minor = scanRecord[minorEnd] & mask;
		minor += (scanRecord[minorEnd - 1] & mask) << 8;

		return minor;
	}
}
