package com.example.lab1;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BeaconScanner {
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothManager bluetoothManager = null;
	private Context ctx = null;
	private LeScanCallback callback = null;
	
	public BeaconScanner(Context context,LeScanCallback callback) {
		this.ctx = context;
		bluetoothManager = (BluetoothManager) ctx
				.getSystemService(ctx.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		this.callback = callback;
	}

	@SuppressWarnings("deprecation")
	public void start() {
		if (mBluetoothAdapter.isEnabled())
			mBluetoothAdapter.startLeScan(callback);
	}

	public void stop() {
		if (mBluetoothAdapter.isEnabled())
			mBluetoothAdapter.stopLeScan(callback);
	}
}
