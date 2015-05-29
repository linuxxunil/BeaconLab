package com.example.lab2;

import java.text.DecimalFormat;

import com.example.R;
import com.example.beacon.BeaconBase;
import com.example.beacon.BeaconParser;
import com.example.beacon.BeaconScanner;
import com.example.beacon.BeaconThread;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


public class Lab2Activity extends Activity {
    private Button btnStart;
    private CheckBox cBoxAvg;
    private TextView tViwValue;
    private boolean flgStart;
    private BeaconScanner bs;
    private Handler handler;
    private BeaconThread thread;
    private double avgRssi = -59.0;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lab2);
        initListeners();
        initHandler();
        initBeaconScanner();
    }

    private void initBeaconScanner() {
		bs = new BeaconScanner(getApplicationContext(),
				new LeScanCallback() {
					@Override
					public void onLeScan(BluetoothDevice device, int rssi,
								byte[] scanRecord) {
					Bundle data = new Bundle();
					data.putString("address", device.getAddress());
					data.putInt("rssi", rssi);
					data.putByteArray("scanRecord", scanRecord);
					sendMessage(0, data);
				}
		});
    }

	private void startBeaconScanner(boolean filter) {
		if (filter){
			bs.start();
		} else {
			thread = new BeaconThread() {
				@Override
				public void run() {
					while (running) {
						try {
							bs.start();
							Thread.sleep(1000);
							bs.stop();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			thread.myStart();
		}
	}

	private void stopBeaconScanner(boolean filter) {
		if ( filter ) bs.stop();
		else thread.myStop();
	}

    private void initListeners()
    {
        btnStart = (Button)findViewById(R.id.btnStart);
        cBoxAvg = (CheckBox)findViewById(R.id.cBoxAvg);
        tViwValue = (TextView)findViewById(R.id.tViwValue);
        btnStart.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flgStart == false) {
					btnStart.setText("Ãö³¬");
					startBeaconScanner(false);
				} else {
					btnStart.setText("±Ò°Ê");
					stopBeaconScanner(false);
				}
				flgStart ^= true;
			}
		});
    }

    private void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				handleBeacon(msg.getData());
			}
		};
    }

    private void handleBeacon(Bundle data) {
        String mac = data.getString("address");
        int rssi = data.getInt("rssi");
        byte scanRecord[] = data.getByteArray("scanRecord");
        String uuid = BeaconParser.getUUID(scanRecord);
        int major = BeaconParser.getMajor(scanRecord);
        int minor = BeaconParser.getMinor(scanRecord);
        double meter = 0; 
		if (cBoxAvg.isChecked()) {
			avgRssi = (avgRssi + rssi) / 2 ;
			meter = BeaconBase.toMeter(avgRssi);
		} else {
			meter = BeaconBase.toMeter(rssi);
		}
        
        DecimalFormat df = new DecimalFormat("#.##");
        tViwValue.setText(df.format(meter));
    }

    private void sendMessage(int what, Bundle data)
    {
        if(handler != null)
        {
            Message msg = handler.obtainMessage();
            msg.what = what;
            msg.setData(data);
            handler.sendMessage(msg);
        }
    }
}
