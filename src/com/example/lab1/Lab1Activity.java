package com.example.lab1;

import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.app.ActionBar.LayoutParams;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.example.R;
import com.example.beacon.BeaconParser;
import com.example.beacon.BeaconScanner;
import com.example.beacon.BeaconThread;
import com.example.beacon.BluetoothLowEnergy;

public class Lab1Activity extends Activity {
	/* UI */
	private Button btnStart = null;
	private CheckBox cBoxAvg = null;
	private LinearLayout lLayChart = null;
	private boolean flgStart = false;
	
	/* Chart */
	private LinkedList[] dataSet = new LinkedList[4];
	private String[] dataSetName = { "Beacon1", "Beacon2", "Beacon3", "Beacon4" };
	private int[] dataSetColors = { Color.BLUE, Color.GREEN,
			Color.rgb(0xFF, 0xA5, 0x00), Color.RED };
	private PointStyle[] dataSetStyles = { PointStyle.CIRCLE,
			PointStyle.CIRCLE, PointStyle.CIRCLE, PointStyle.CIRCLE };
	private String title = "Signal Strength";
	private XYSeries[] series;
	private XYMultipleSeriesDataset mDataset;
	private GraphicalView chart;
	private XYMultipleSeriesRenderer renderer;
	
	/* Beacon Scanner */
	private BeaconScanner bs = null;
	/* Handler*/
	private Handler handler = null;
	
	private BeaconThread thread = null;
	
	private double avgRssi = -59.0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lab1);
		initListeners();
		initHandler();
		initChart();
		inspectBluetoohAvailable();
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
	
	
	
	private void initListeners() {
		btnStart = (Button) findViewById(R.id.sig_btnStart);
		cBoxAvg = (CheckBox) findViewById(R.id.sig_cBoxWinAvg);
		lLayChart = (LinearLayout) findViewById(R.id.sig_lLayChart);

		btnStart.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flgStart == false) {
					btnStart.setText("關閉");
					resetChart();
					startBeaconScanner(false);
				} else {
					btnStart.setText("啟動");
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
	
	
	// 
	private void handleBeacon(Bundle data){
		String mac = data.getString("address");
		int rssi = data.getInt("rssi");
		byte[] scanRecord = data.getByteArray("scanRecord");
		
		String uuid = BeaconParser.getUUID(scanRecord);
		int major = BeaconParser.getMajor(scanRecord);
		int minor = BeaconParser.getMinor(scanRecord);
		
		if (cBoxAvg.isChecked()) {
			avgRssi = (avgRssi + rssi) / 2 ;
			dataSet[0].add((double)avgRssi);
		} else {
			dataSet[0].add((double)rssi);
		}
		
		updateChart(dataSet);
	}
	
	private void sendMessage(int what, Bundle data) {
		if (handler != null) {
			Message msg = handler.obtainMessage();
			msg.what = what;
			msg.setData(data);
			handler.sendMessage(msg);
		}
	}

	private void initChart() {
		for (int i = 0; i < dataSet.length; i++)
			dataSet[i] = new LinkedList<Double>();

		series = new XYSeries[dataSet.length];
		for (int i = 0; i < dataSet.length; i++)
			series[i] = new XYSeries(dataSetName[i]);

		// 創建一個數據集的實例，這個數據集將被用來創建圖表
		mDataset = new XYMultipleSeriesDataset();

		// 將點集添加到這個數據集中
		for (int i = 0; i < dataSet.length; i++)
			mDataset.addSeries(series[i]);

		// 以下都是曲線的樣式和屬性等等的設置，renderer相當於一個用來給圖表做渲染的句柄
		renderer = buildRenderer(dataSetColors, 
						dataSetStyles, true, dataSet.length);

		// 設置好圖表的樣式
		setChartSettings(renderer);

		// 生成圖表
		chart = ChartFactory.getLineChartView(getApplicationContext(),
						mDataset, renderer);

		// 將圖表添加到布局中去
		lLayChart.removeAllViews();
		lLayChart.addView(chart, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	private void resetChart() {
		initChart();
	}
	
	private XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles, boolean fill,int len) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		for (int i = 0; i < len; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			r.setFillPoints(fill);
			renderer.addSeriesRenderer(r); // 將座標變成線加入圖中顯示
		}
		return renderer;
	}

	private void setChartSettings(XYMultipleSeriesRenderer renderer) {
		// 有關對圖表的渲染可參看api文檔

		renderer.setChartTitle(title);
		renderer.setXAxisMin(0);
		renderer.setXAxisMax(100);
		renderer.setYAxisMin(-20);
		renderer.setYAxisMax(-100);
		renderer.setAxesColor(Color.WHITE);
		renderer.setLabelsColor(Color.WHITE);
		renderer.setShowGrid(true);
		renderer.setGridColor(Color.LTGRAY);
		renderer.setXLabels(20);
		renderer.setYLabels(20);
		renderer.setXTitle("Time");
		renderer.setYTitle("dBm");
		renderer.setYLabelsAlign(Align.LEFT);
		renderer.setPointSize((float) 2);
		renderer.setShowLegend(false);
	}

	private void updateChart(LinkedList<Double>[] list) {
		int beaconLen = dataSetName.length;
		// 移除數據集中舊的點集
		for (int i = 0; i < beaconLen; i++)
			mDataset.removeSeries(series[i]);

		// 點集先清空，為了做成新的點集而准備
		for (int i = 0; i < beaconLen; i++)
			series[i].clear();

		for (int i = 0; i < beaconLen; i++) {
			for (int k = 0; k < list[i].size(); k++) {
				series[i].add(k, (double) list[i].get(k));
			}
		}

		// 在數據集中添加新的點集
		for (int i = 0; i < beaconLen; i++)
			mDataset.addSeries(series[i]);

		// update chart
		chart.invalidate();
	}
	
	private void inspectBluetoohAvailable() {

		final BluetoothLowEnergy ble = new BluetoothLowEnergy(getApplicationContext());
		if ( !ble.isEnabled() ) {
			AlertDialog.Builder builder = new AlertDialog.Builder(Lab1Activity.this);
			builder.setTitle("Navigation");
			builder.setMessage("你的藍牙裝置未開啟，是否開啟藍芽？");
			builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int i) {
					finish();
				}
			});

			builder.setNegativeButton("確認", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int i) {
					ble.enable();
				}	
			});
			builder.show();
		}
	}
}
