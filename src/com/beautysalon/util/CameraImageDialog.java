package com.beautysalon.util;

import com.example.testgallary.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class CameraImageDialog extends Dialog {
	private CameraClickListener cameraListener = null;
	private NoClickListener noListener = null;
	private PhoneClickListener phoneListener = null;
	private RelativeLayout camemaRl = null;
	private RelativeLayout phoneRl = null;
	private RelativeLayout noRl = null;

	public CameraImageDialog(Context context) {
		super(context, R.style.dialog);
	}

	public interface CameraClickListener {
		public void clickListener();
	}

	public CameraImageDialog(Context context, int theme) {
		super(context, theme);
	}

	public interface NoClickListener {
		public void clickListener();
	}

	public interface PhoneClickListener {
		public void clickListener();
	}

	public void setCameraClickListener(CameraClickListener ok) {
		this.cameraListener = ok;
	}

	public void setNoClickListener(NoClickListener no) {
		this.noListener = no;
	}

	public void setPhoneClickListener(PhoneClickListener phone) {
		this.phoneListener = phone;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.cameraimagedialog);
		//par.setAlpha(40);
		camemaRl = (RelativeLayout) findViewById(R.id.carema_rl);
		camemaRl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cameraListener != null) {
					cameraListener.clickListener();
				}
			}
		});
		phoneRl = (RelativeLayout) findViewById(R.id.photo_rl);
		phoneRl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (phoneListener != null)
					phoneListener.clickListener();
			}
		});
		noRl = (RelativeLayout) findViewById(R.id.cancel_rl);
		noRl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (noListener != null)
					noListener.clickListener();
			}
		});
	}
}
