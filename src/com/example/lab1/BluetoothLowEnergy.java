package com.example.lab1;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Context;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothLowEnergy {
	protected BluetoothAdapter mBluetoothAdapter = null;
	protected BluetoothManager bluetoothManager = null;
	private Context ctx = null;
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public BluetoothLowEnergy(Context context) {
		this.ctx = context;
		bluetoothManager = (BluetoothManager) ctx
				.getSystemService(ctx.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}
	
	public boolean isEnabled() {
		return mBluetoothAdapter.isEnabled();
	}
	
	public boolean enable() {
		return mBluetoothAdapter.enable();
	}
	
	public boolean disable() {
		return mBluetoothAdapter.disable();
	}
	
	public void startLeScan(LeScanCallback callback) {
		mBluetoothAdapter.startLeScan(callback);
	}
	
	public void stopLeScan(LeScanCallback callback) {
		mBluetoothAdapter.stopLeScan(callback);
	}
}
