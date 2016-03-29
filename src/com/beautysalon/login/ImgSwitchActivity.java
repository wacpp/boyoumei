package com.beautysalon.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import cn.redcdn.crash.Crash;
import cn.redcdn.log.CustomLog;
import cn.redcdn.log.LogcatFileManager;

import com.beautysalon.main.BaseActivity;
import com.beautysalon.register.RegisterStepFirstActivity;
import com.beautysalon.store.StoreManagementActivity;
import com.beautysalon.util.ConstConfig;
import com.beautysalon.util.TokenManager;
import com.example.testgallary.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class ImgSwitchActivity extends BaseActivity {
  // private Gallery myGallery;
  // private RadioGroup gallery_points;
  // private RadioButton[] gallery_point;
  private Context context;
  private Button btnLogin = null;
  private Button btnRegister = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.imgswitch);
    context = getApplicationContext();
    initLogAndCrash();
    initDisplay();

    SharedPreferences sharedPreferences = getSharedPreferences("user",
        Context.MODE_PRIVATE);
    String token = sharedPreferences.getString(ConstConfig.SHARE_TOKEN, "");
    String userid = sharedPreferences.getString(ConstConfig.SHARE_USERID, "");
    SharedPreferences sharedPreferencesStatus = getSharedPreferences(
        "shopStatus", Context.MODE_PRIVATE);
    int currentShopStatus = sharedPreferencesStatus.getInt("status", 0);
    if (!token.isEmpty() && currentShopStatus == 3) {
      CustomLog.d(TAG, "获取内存中保存的token和userid");
      TokenManager.getInstance().setToken(token);
      if (!userid.isEmpty()) {
        TokenManager.getInstance().setUserId(userid);
      }
      Intent intent = new Intent();
      intent.setClass(ImgSwitchActivity.this, StoreManagementActivity.class);
      startActivity(intent);
      finish();
    } else {
      init();
      addEvn();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    SharedPreferences sharedPreferences = getSharedPreferences("user",
        Context.MODE_PRIVATE);
    String token = sharedPreferences.getString(ConstConfig.SHARE_TOKEN, "");
    String userid = sharedPreferences.getString(ConstConfig.SHARE_USERID, "");
    SharedPreferences sharedPreferencesStatus = getSharedPreferences(
        "shopStatus", Context.MODE_PRIVATE);
    int currentShopStatus = sharedPreferencesStatus.getInt("status", 0);

    if (!token.isEmpty() && currentShopStatus == 3) {
      CustomLog.d(TAG, "获取内存中保存的token和userid");
      TokenManager.getInstance().setToken(token);
      if (!userid.isEmpty()) {
        TokenManager.getInstance().setUserId(userid);
      }
      // Intent intent = new Intent();
      // intent.setClass(ImgSwitchActivity.this, StoreManagementActivity.class);
      // startActivity(intent);
      finish();
    }
    // Toast.makeText(ImgSwitchActivity.this, "再点击一次退出程序", Toast.LENGTH_LONG)
    // .show();
  };

  private void initDisplay() {
    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
        getApplicationContext()).threadPriority(Thread.NORM_PRIORITY - 2)// 设置线程的优先级，比ui稍低优先级
        .threadPoolSize(3) // 线程池内加载的数量，建议1~5
        // .denyCacheImageMultipleSizesInMemory()//当同一个Uri获取不同大小的图片，缓存到内存时，只缓存一个。默认会缓存多个不同的大小的相同图片
        .discCacheFileNameGenerator(new Md5FileNameGenerator())// 设置缓存文件的名字,通过Md5将url生产文件的唯一名字
        .memoryCache(new LruMemoryCache(5 * 1024 * 1024)) // 可以通过自己的内存缓存实现。LruMemoryCache：缓存只使用强引用.
                                                          // (缓存大小超过指定值时，删除最近最少使用的bitmap)
                                                          // --默认情况下使用
        // .memoryCacheSize(2 * 1024 * 1024) // 内存缓存的最大值
        .discCacheFileCount(1000)// 缓存文件的最大个数
        .tasksProcessingOrder(QueueProcessingType.FIFO)// 设置图片下载和显示的工作队列排序
        // .enableLogging() //是否打印日志用于检查错误
        .build();

    ImageLoader.getInstance().init(config);
    // File cacheDir = StorageUtils.getOwnCacheDirectory(context, Environment
    // .getExternalStorageDirectory().getPath() + "/meeting/log");// 获取到缓存的目录地址
    //
    // ImageLoaderConfiguration config = new
    // ImageLoaderConfiguration.Builder(this)
    // .threadPriority(Thread.NORM_PRIORITY - 2)
    // // 设置线程的优先级，比ui稍低优先级
    // .threadPoolSize(3)
    // // 线程池内加载的数量，建议1~5
    // .denyCacheImageMultipleSizesInMemory()
    // // 当同一个Uri获取不同大小的图片，缓存到内存时，只缓存一个。默认会缓存多个不同的大小的相同图片
    // .discCacheFileNameGenerator(new Md5FileNameGenerator())
    // // 设置缓存文件的名字,通过Md5将url生产文件的唯一名字
    // .discCacheSize(50 * 1024 * 1024)
    // // 将保存的时候的URI名称用MD5
    // .discCacheFileNameGenerator(new Md5FileNameGenerator())
    // // 加密
    // .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
    // // 将保存的时候的URI名称用HASHCODE加密
    // .tasksProcessingOrder(QueueProcessingType.LIFO)
    // .memoryCache(new LruMemoryCache(5 * 1024 * 1024)) //
    // 可以通过自己的内存缓存实现。LruMemoryCache：缓存只使用强引用.
    // // default // (缓存大小超过指定值时，删除最近最少使用的bitmap)
    // // --默认情况下使用
    // // .memoryCacheSize(2 * 1024 * 1024) // 内存缓存的最大值
    // .discCacheFileCount(1000)// 缓存文件的最大个数
    // .discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
    // .tasksProcessingOrder(QueueProcessingType.FIFO)// 设置图片下载和显示的工作队列排序
    // // .enableLogging() //是否打印日志用于检查错误
    // .build();
    //
    // ImageLoader.getInstance().init(config);

  }

  private void initLogAndCrash() {
    LogcatFileManager.getInstance().start(context.getPackageName());
    addUnCatchException();
  }

  @Override
  protected void onStop() {
    super.onStop();

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    LogcatFileManager.getInstance().stop();
  }

  private void addUnCatchException() {
    Crash crash = new Crash();
    crash.init(context, getPackageName());
  }

  void init() {
    btnLogin = (Button) findViewById(R.id.btn_login);
    btnRegister = (Button) findViewById(R.id.btn_register);
  }

  void addEvn() {
    btnLogin.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(ImgSwitchActivity.this, Login.class);
        startActivity(intent);
        // finish();
      }
    });
    btnRegister.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        CustomLog.e("11111", "111111");
        Intent intent = new Intent();
        intent
            .setClass(ImgSwitchActivity.this, RegisterStepFirstActivity.class);
        startActivity(intent);
        // finish();
      }
    });
  }

  // protected void onResume() {
  // super.onResume();
  // };

}