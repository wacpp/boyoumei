package com.example.gallery;

import java.io.IOException;

import com.example.testgallary.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class BigImgActivity extends Activity {
	private ImageView imageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.img);
		imageView = (ImageView)findViewById(R.id.waterfall_image);
		Intent intent = getIntent();
		String imageName = intent.getStringExtra("imageName");
		AssetManager assetManager = this.getAssets();
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(assetManager.open(imageName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		imageView.setImageBitmap(bitmap);
	}
}
