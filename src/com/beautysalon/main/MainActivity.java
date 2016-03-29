package com.beautysalon.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.beautysalon.login.ImgSwitchActivity;
import com.example.gallery.TaoBaoImgShowActivity;
import com.example.testgallary.R;

public class MainActivity extends Activity {
	//幻灯片演示效果
	private Button btnfir;
	//仿淘宝详情页面图片浏览效果
	private Button btnsec;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		addEvn();
	}
	void init(){
		btnfir = (Button)findViewById(R.id.firbtn);
		btnsec = (Button)findViewById(R.id.secbtn);
	}
	void addEvn(){
		btnfir.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, ImgSwitchActivity.class);
				startActivity(intent);
			}
		});
		
		btnsec.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, TaoBaoImgShowActivity.class);
				startActivity(intent);
			}
		});
	}
	
}
