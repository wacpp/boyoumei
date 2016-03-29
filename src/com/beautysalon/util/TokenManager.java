package com.beautysalon.util;

import android.graphics.Bitmap.Config;

import com.example.testgallary.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class TokenManager {

  private String token = "";

  private String userId = "";

  private String shopid = "";

  private int currentShopState = 1;

  private static TokenManager mTokenmanager;
  public static DisplayImageOptions options;

  public synchronized static TokenManager getInstance() {
    if (mTokenmanager == null) {
      mTokenmanager = new TokenManager();
      displayImageOpt();
    }

    return mTokenmanager;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  private static void displayImageOpt() {

    options = new DisplayImageOptions.Builder()
        .showStubImage(R.drawable.ic_launcher)// 设置图片在下载期间显示的图片
        .showImageForEmptyUri(R.drawable.ic_launcher)// 设置图片Uri为空或是错误的时候显示的图片
        .showImageOnFail(R.drawable.ic_launcher)// 设置图片加载/解码过程中错误时候显示的图片
        .cacheInMemory(true)// 是否緩存都內存中
        .cacheOnDisc(true)// 是否緩存到sd卡上
        .displayer(new SimpleBitmapDisplayer())// 设置图片的默認显示方式 :
        .bitmapConfig(Config.RGB_565)// 设置为RGB565比起默认的ARGB_8888要节省大量的内存
        .delayBeforeLoading(100)// 载入图片前稍做延时可以提高整体滑动的流畅度
        .build();

  }

  public String getShopid() {
    return shopid;
  }

  public void setShopid(String shopid) {
    this.shopid = shopid;
  }

  public int getCurrentShopState() {
    return currentShopState;
  }

  public void setCurrentShopState(int currentShopState) {
    this.currentShopState = currentShopState;
  }
}
