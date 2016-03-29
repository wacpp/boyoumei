package com.beautysalon.viewflipper;

import java.util.List;
import java.util.Map;

import cn.redcdn.log.CustomLog;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleAdapter extends BaseAdapter {

  private LayoutInflater mInflater;
  private int resource;
  private List<String> data;
  private String[] from;
  private int[] to;
  private static int nowpos = 10000;

  public SimpleAdapter(Context context, List<String> data, int resouce,
      String[] from, int[] to) {

    this.data = data;
    this.resource = resouce;
    this.data = data;
    this.from = from;
    this.to = to;
    this.mInflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  public int getCount() {
    // TODO Auto-generated method stub
    return data == null ? 0 : data.size();
  }

  public Object getItem(int position) {
    // TODO Auto-generated method stub
    return data.get(position);
  }

  @SuppressWarnings("unchecked")
  public String get(int position, Object key) {
    Map<String, ?> map = (Map<String, ?>) getItem(position);
    return map.get(key).toString();
  }

  public long getItemId(int position) {
    // TODO Auto-generated method stub
    return position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    Log.e("getView...", "..........................test position=" + position);
    convertView = mInflater.inflate(resource, null);
    String itemUrl = data.get(position);
    int count = to.length;
    for (int i = 0; i < count; i++) {
      View v = convertView.findViewById(to[i]);
      CustomLog.d("View111111111111", v.toString());
      bindView(v, itemUrl, from[i]);
    }
    convertView.setTag(position);
    /*
     * if (position == nowpos) { TextView v = (TextView)
     * convertView.findViewById(R.id.itemText);
     * v.setTextColor(Color.parseColor("#eaf0f3")); zoomInViewSize(v, 80, 80); }
     */
    return convertView;
  }

  /**
   * ����ͼ
   * 
   * @param view
   * @param item
   * @param from
   */
  private void bindView(View view, String imgUrl, String from) {
    if (view instanceof TextView) {
      // ((TextView) view).setText(data == null ? "" : item + "");
//      ((TextView) view).setBackgroundResource(data == null ? 0 : item);
      CustomLog.d("11111111111", "imgUrl="+imgUrl);
//      ImageSize mImageSize = new ImageSize(((TextView) view).getWidth(),
//          ((TextView) view).getHeight());
      ImageSize mImageSize = new ImageSize(50,50);
      ImageLoader.getInstance().loadImage(imgUrl, mImageSize,
          new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view,
                Bitmap loadedImage) {

              super.onLoadingComplete(imageUri, view, loadedImage);
              CustomLog.d("2222222222222", "22222222222222"+view.toString());
              ((ImageView) view).setBackgroundDrawable(new BitmapDrawable(loadedImage));
            }

          });

      
      
    }
  }

  public void setSelected(int setpos) {
    nowpos = setpos;
    notifyDataSetChanged();
  }
}
