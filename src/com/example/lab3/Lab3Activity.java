package com.example.lab3;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.R;


public class Lab3Activity extends Activity{
	private EditText eTxtURL = null;
	private EditText eTxtMethod = null;
	private EditText eTxtContentType = null;
	private EditText eTxtContent = null;
	private TextView tViwResult = null;
	private Button btnSend = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lab3);
		
		eTxtURL = (EditText) findViewById(R.id.editViewURL);
		eTxtMethod = (EditText) findViewById(R.id.editViewMethod);
		eTxtContentType = (EditText) findViewById(R.id.editViewContentType);
		eTxtContent = (EditText) findViewById(R.id.editViewContent);
		tViwResult = (TextView) findViewById(R.id.editViewResult);
		btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(btnSendOnClick);
		
		HttpService httpService = new HttpService(10);
		httpService.start();
	}
	
	private Button.OnClickListener btnSendOnClick = new Button.OnClickListener() {
		@Override
		public void onClick(View v) {
			String url = eTxtURL.getText().toString();
			String method = eTxtMethod.getText().toString();
			String contentType = eTxtContentType.getText().toString();
			String content = eTxtContent.getText().toString();
			String result = "";
			AsyncHttpClient http = new AsyncHttpClient();
		
			if ( method.equals("POST") ) {
				result = http.post(url, contentType, content);
				tViwResult.setText(result);
			}		
		}
	};	
}
