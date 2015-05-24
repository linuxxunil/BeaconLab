package com.example.beacon;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BeaconScanner extends BluetoothLowEnergy{
	private LeScanCallback callback = null;
	
	public BeaconScanner(Context context,LeScanCallback callback) {
		super(context);
		this.callback = callback;
	}

	@SuppressWarnings("deprecation")
	public void start() {
		if ( isEnabled() ) 
			startLeScan(callback);
	}

	public void stop() {
		if ( isEnabled())
			stopLeScan(callback);
	}
}
