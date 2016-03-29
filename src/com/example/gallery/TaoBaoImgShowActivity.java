package com.example.gallery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.example.testgallary.R;

public class TaoBaoImgShowActivity extends Activity {
	private DetailGallery myGallery;
	private AssetManager assetManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taobaoimgshow);
		myGallery = (DetailGallery) findViewById(R.id.detail_shotcut_gallery);
		assetManager = this.getAssets();
		init();
		addEvn();
	}

	void init() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(1);
		list.add(1);
		GalleryAdapter adapter = new GalleryAdapter(list, getApplicationContext());
		myGallery.setAdapter(adapter);

	}

	void addEvn() {
		myGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TaoBaoImgShowActivity.this, BigImgActivity.class);
				intent.putExtra("imageName", arg1.getTag().toString());
				startActivity(intent);
			}
		});
	}

	class GalleryAdapter extends BaseAdapter {
		List<Integer> imagList;
		Context context;

		public GalleryAdapter(List<Integer> list, Context cx) {
			imagList = list;
			context = cx;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imagList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.img,
					null);
			Bitmap bitmap = null;
			try {
				if(position == 1 ){
					bitmap = BitmapFactory.decodeStream(assetManager.open("xpic11247_s.jpg"));
					imageView.setTag("xpic11247_s.jpg");
				}
				else{
					bitmap = BitmapFactory.decodeStream(assetManager.open("item0_pic.jpg"));
					imageView.setTag("item0_pic.jpg");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 加载图片之前进行缩放
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			float newHeight = 200;
			float newWidth = width*newHeight/height;
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			// 取得想要缩放的matrix参数
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			// 得到新的图片
			Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
			System.out.println(newbm.getHeight()+"-----------"+newbm.getWidth());
			imageView.setImageBitmap(newbm);
			// }
			return imageView;

		}
	}
}
