package com.beautysalon.store;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.redcdn.log.CustomLog;

import com.beautysalon.main.BaseActivity;
import com.beautysalon.po.ShopInfoPO;
import com.beautysalon.util.CommUtils;
import com.beautysalon.util.ConstConfig;
import com.beautysalon.util.DisplayImageListener;
import com.beautysalon.util.TokenManager;
import com.example.testgallary.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class StoreInfoActivity extends BaseActivity {

  private int pageIndex = 0;
  List<Integer> t;
  int nn = 1;
  int tableColumnId = 0;
  public final static int TOUCH_MOVING = 20;
  private Button storeInfoBtnFlipper = null;
  private GestureDetector mGestureDetector;
  private ShopInfoPO shopInfoPO = new ShopInfoPO();
  private Button btnRet;
  private Button storeInfoEdit;
  private ImageView ivStoreInfoRes;
  private TextView etStoreName;
  private TextView etStoreArea;
  private TextView etStoreNumber;
  private TextView etStoreTime;
  private TextView etStoreDetail;
  private DisplayImageListener mDisplayImageListener = null;
  private ImageLoader imageLoader = null;

  private ImageView imageView1 = null;
  private ImageView imageView2 = null;
  private ImageView imageView3 = null;
  private ImageView imageView4 = null;
  private ImageView imageView5 = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.store_info);
    initUI();
    mDisplayImageListener = new DisplayImageListener();
    imageLoader = ImageLoader.getInstance();
    StoreInfoActivity.this.showLoadingView("请求数据中...", new OnCancelListener() {

      @Override
      public void onCancel(DialogInterface dialog) {
        CustomLog.e(TAG, "cancle。。。。");
      }
    });

    initWebData();

    // initViewFlipper();
    // initGuest();
  }

  private void showImage(String url) {

    // imageLoader.displayImage(url, ivStoreInfoRes,
    // TokenManager.getInstance().options, mDisplayImageListener);

    // final ImageView mImageView = (ImageView) findViewById(R.id.image);
    // String imageUrl =
    // "https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg";
    CustomLog.d(TAG, "url..." + url);
    // ImageSize mImageSize = new ImageSize(ivStoreInfoRes.getWidth(),
    // ivStoreInfoRes.getHeight());
    //
    // ImageLoader.getInstance().loadImage(url,
    // mImageSize,TokenManager.getInstance().options
    // new SimpleImageLoadingListener() {
    //
    // @Override
    // public void onLoadingComplete(String imageUri, View view,
    // Bitmap loadedImage) {
    // super.onLoadingComplete(imageUri, view, loadedImage);
    // ivStoreInfoRes.setImageBitmap(loadedImage);
    // }
    //
    // });
    // ImageLoader imageLoader = ImageLoader.getInstance();
    // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
    imageLoader.displayImage(url, ivStoreInfoRes, TokenManager.options,
        mDisplayImageListener);
  }

  private void initWebData() {
    AsyncHttpClient httpClient = new AsyncHttpClient();

    RequestParams param = new RequestParams();
    param.put(ConstConfig.TOKEN, TokenManager.getInstance().getToken());
    param.put(ConstConfig.USER_ID, TokenManager.getInstance().getUserId());
    CustomLog.d(TAG, "....param=" + param.toString());
    httpClient.post(CommUtils.getShopInfoUrl(), param,
        new JsonHttpResponseHandler() {
          @Override
          public void onStart() {

            super.onStart();

          }

          @Override
          public void onSuccess(int statusCode, Header[] headers,
              JSONObject response) {
            // Handle resulting parsed JSON response here
            StoreInfoActivity.this.removeLoadingView();
            CustomLog.e("请求成功。。。。cccccc", response.toString());
            try {
              if (response.getInt(ConstConfig.RETURN_CODE) == 0) {
                JSONArray jaService = new JSONArray();
                jaService = (JSONArray) response.optJSONArray("shop");
                JSONObject jsShopInfo = new JSONObject();
                jsShopInfo = (JSONObject) jaService.getJSONObject(0);
                CustomLog.e("请求成功。。。。", response.toString());
                StoreInfoActivity.this.removeLoadingView();
                shopInfoPO.setShopId(jsShopInfo.optString("shopid"));
                shopInfoPO.setShopName(jsShopInfo.optString("shopname"));
                shopInfoPO.setArea(jsShopInfo.optString("area"));
                shopInfoPO.setCity(jsShopInfo.optString("city"));
                shopInfoPO.setTelephone(jsShopInfo.optString("shopphone"));
                shopInfoPO.setBussinessTime(jsShopInfo
                    .optString("businesstime"));
                shopInfoPO.setDec(jsShopInfo.optString("shopdec"));
                JSONArray ja = (JSONArray) jsShopInfo
                    .optJSONArray(ConstConfig.SHOP_IMAGES);
                List<String> shopUrl = new ArrayList<String>();

                if (ja == null || ja.isNull(0)) {

                } else {
                  for (int i = 0; i < ja.length(); i++) {
                    try {
                      shopUrl.add(((JSONObject) ja.get(i))
                          .optString(ConstConfig.SHOP_IMG_URL));
                    } catch (JSONException e) {
                      CustomLog.e(TAG, e.toString());
                    }
                  }
                }
                CustomLog.d(TAG, "ja.." + ja.length());
                shopInfoPO.setIamgeUrl(shopUrl);
                initData();
                showImage(CommUtils.REQUEST_URL_SUFFIX
                    + shopInfoPO.getIamgeUrl().get(0));
                // initViewFlipper();
                initImageView();
                initGuest();
              } else {
                CustomLog.e(
                    TAG,
                    "............resultcode="
                        + response.getInt(ConstConfig.RETURN_CODE));
              }
            } catch (JSONException e) {
              // TODO Auto-generated catch block
              CustomLog.e(TAG, e.toString());
            }
          }

          // @Override
          // public void onSuccess(int statusCode, Header[] headers,
          // JSONArray response) {
          //
          // }
          //
          @Override
          public void onFailure(int statusCode, Header[] headers,
              Throwable throwable, JSONObject errorResponse) {
            StoreInfoActivity.this.removeLoadingView();
            CustomLog.e("请求失敗。。。。", "statusCode=" + statusCode);
          }

          @Override
          public void onFailure(int statusCode, Header[] headers,
              String errorResponse, Throwable e) {
            StoreInfoActivity.this.removeLoadingView();
            CustomLog.e(TAG, "statusCode=" + statusCode + "errorResponse="
                + errorResponse.toString());
            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
          }

        });

  }

  private void initData() {
    // btnRet.setOnClickListener(btnOnClickListener);
    storeInfoEdit.setOnClickListener(btnOnClickListener);
    CustomLog.d(TAG, "" + shopInfoPO.toString());
    etStoreArea.setText(shopInfoPO.getCity() + shopInfoPO.getArea());
    etStoreName.setText(shopInfoPO.getShopName());
    etStoreNumber.setText(shopInfoPO.getTelephone());
    etStoreTime.setText(shopInfoPO.getBussinessTime());
    etStoreDetail.setText(shopInfoPO.getDec());
    storeInfoEdit.setOnClickListener(btnOnClickListener);

    // ivStoreInfoRes.setBackground(background)
    // 調用display下載圖片

  }

  private void initGuest() {
    mGestureDetector = new GestureDetector(this,
        new GestureDetector.SimpleOnGestureListener() {
          @Override
          public boolean onFling(MotionEvent e1, MotionEvent e2,
              float velocityX, float velocityY) {
            // if (Math.abs(e1.getRawX() - e2.getRawX()) > 250) {
            // // System.out.println("水平方向移动距离过大");
            // return true;
            // }
            if (Math.abs(velocityY) < 100) {
              // System.out.println("手指移动的太慢了");

              return true;
            }

            // 手势向右 down
            if ((e2.getRawX() - e1.getRawX()) > 200) {
              // finish();//在此处控制关闭
              CustomLog.e("22222222222222", "fromXPosition");
              if (pageIndex > 0 && getPageSize() == 2) {

                pageIndex = pageIndex - 1;
                imageView1.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.VISIBLE);
                imageView3.setVisibility(View.VISIBLE);
                imageView4.setVisibility(View.INVISIBLE);
                imageView5.setVisibility(View.INVISIBLE);
                // viewFlipper.showPrevious();

              }
              return true;
            }
            // 手势向左 up
            if ((e1.getRawX() - e2.getRawX()) > 200) {
              CustomLog.e("333333333333333333", "fromXPosition");

              if (pageIndex < 1 && getPageSize() == 2) {
                pageIndex = pageIndex + 1;
                // viewFlipper.showNext();
                imageView1.setVisibility(View.INVISIBLE);
                imageView2.setVisibility(View.INVISIBLE);
                imageView3.setVisibility(View.INVISIBLE);
                imageView4.setVisibility(View.VISIBLE);
                imageView5.setVisibility(View.VISIBLE);
              }
              return true;
            }
            return super.onFling(e1, e2, velocityX, velocityY);
          }
        });
  }

  private int getPageSize() {
    CustomLog.e("getPageSize", "shopInfoPO.getIamgeUrl().size()-1 ...="
        + (shopInfoPO.getIamgeUrl().size() - 1));
    if ((shopInfoPO.getIamgeUrl().size() - 1) % 3 == 0) {
      return (shopInfoPO.getIamgeUrl().size() - 1) / 3;
    } else {
      return (shopInfoPO.getIamgeUrl().size() - 1) / 3 + 1;
    }

  }

  private void initUI() {
    btnRet = (Button) findViewById(R.id.btn_ret);
    storeInfoEdit = (Button) findViewById(R.id.store_info_edit);
    etStoreArea = (TextView) findViewById(R.id.et_store_area);
    etStoreName = (TextView) findViewById(R.id.et_store_name);
    etStoreNumber = (TextView) findViewById(R.id.et_store_number);
    etStoreTime = (TextView) findViewById(R.id.et_store_time);
    etStoreDetail = (TextView) findViewById(R.id.et_store_detail);
    ivStoreInfoRes = (ImageView) findViewById(R.id.iv_store_info_res);
    storeInfoBtnFlipper = (Button) findViewById(R.id.store_info_btn_flipper);
    storeInfoBtnFlipper.setOnTouchListener(btnOnTouchLitener);
    btnRet.setOnClickListener(btnOnClickListener);
  }

  View.OnClickListener btnOnClickListener = new View.OnClickListener() {

    @Override
    public void onClick(View v) {
      // TODO Auto-generated method stub
      switch (v.getId()) {
      case R.id.store_info_edit:
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("shopInfo", shopInfoPO);
        intent.putExtras(bundle);
        intent.setClass(StoreInfoActivity.this, StoreInfoEditActivity.class);
        startActivity(intent);
        finish();
        break;
      case R.id.btn_ret:
        CustomLog.d(TAG, "ret click.....");
        finish();
        break;
      default:
        break;
      }
    }
  };
  View.OnTouchListener btnOnTouchLitener = new View.OnTouchListener() {

    public boolean onTouch(View v, MotionEvent event) {

      mGestureDetector.onTouchEvent(event);
      return true;
    }
  };

  private void initImageView() {
    imageView1 = (ImageView) findViewById(R.id.image_view1);
    imageView2 = (ImageView) findViewById(R.id.image_view2);
    imageView3 = (ImageView) findViewById(R.id.image_view3);
    imageView4 = (ImageView) findViewById(R.id.image_view4);
    imageView5 = (ImageView) findViewById(R.id.image_view5);
    imageView4.setVisibility(View.INVISIBLE);
    imageView5.setVisibility(View.INVISIBLE);
    CustomLog.d(TAG, "initImageView00000000---------" + getPageSize());
    if (getPageSize() == 1) {
      CustomLog.d(TAG, "initImageView11111111111");
      if (shopInfoPO.getIamgeUrl().size() == 2) {
        CustomLog.d(TAG, "initImageView222222222222");
        // ImageLoader.getInstance().loadImage(
        // // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(0)),
        // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(1)),
        // mImageSize, new SimpleImageLoadingListener() {
        //
        // @Override
        // public void onLoadingComplete(String imageUri, View view,
        // Bitmap loadedImage) {
        // CustomLog.d(TAG, "initImageView3333333333333");
        // super.onLoadingComplete(imageUri, view, loadedImage);
        // imageView1.setImageBitmap(loadedImage);
        // }
        //
        // });
        //

        // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
        imageLoader.displayImage((CommUtils.REQUEST_URL_SUFFIX + shopInfoPO
            .getIamgeUrl().get(1)), imageView1, TokenManager.options,
            mDisplayImageListener);

      }
      if (shopInfoPO.getIamgeUrl().size() == 3) {
        // ImageLoader.getInstance().loadImage(
        // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(1)),
        // mImageSize, new SimpleImageLoadingListener() {
        //
        // @Override
        // public void onLoadingComplete(String imageUri, View view,
        // Bitmap loadedImage) {
        // super.onLoadingComplete(imageUri, view, loadedImage);
        // imageView1.setImageBitmap(loadedImage);
        // }
        //
        // });

        // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
        imageLoader.displayImage((CommUtils.REQUEST_URL_SUFFIX + shopInfoPO
            .getIamgeUrl().get(1)), imageView1, TokenManager.options,
            mDisplayImageListener);

        // ImageLoader.getInstance().loadImage(
        // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(2)),
        // mImageSize, new SimpleImageLoadingListener() {
        //
        // @Override
        // public void onLoadingComplete(String imageUri, View view,
        // Bitmap loadedImage) {
        // super.onLoadingComplete(imageUri, view, loadedImage);
        // imageView2.setImageBitmap(loadedImage);
        // }
        //
        // });
        // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
        imageLoader.displayImage((CommUtils.REQUEST_URL_SUFFIX + shopInfoPO
            .getIamgeUrl().get(2)), imageView2, TokenManager.options,
            mDisplayImageListener);
      }
      if (shopInfoPO.getIamgeUrl().size() == 4) {
        // ImageLoader.getInstance().loadImage(
        // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(1)),
        // mImageSize, new SimpleImageLoadingListener() {
        //
        // @Override
        // public void onLoadingComplete(String imageUri, View view,
        // Bitmap loadedImage) {
        // super.onLoadingComplete(imageUri, view, loadedImage);
        // imageView1.setImageBitmap(loadedImage);
        // }
        //
        // });

        // ImageLoader.getInstance().loadImage(
        // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(2)),
        // mImageSize, new SimpleImageLoadingListener() {
        //
        // @Override
        // public void onLoadingComplete(String imageUri, View view,
        // Bitmap loadedImage) {
        // super.onLoadingComplete(imageUri, view, loadedImage);
        // imageView2.setImageBitmap(loadedImage);
        // }
        //
        // });
        // ImageLoader.getInstance().loadImage(
        // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(3)),
        // mImageSize, new SimpleImageLoadingListener() {
        //
        // @Override
        // public void onLoadingComplete(String imageUri, View view,
        // Bitmap loadedImage) {
        // super.onLoadingComplete(imageUri, view, loadedImage);
        // imageView3.setImageBitmap(loadedImage);
        // }
        //
        // });
        // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
        imageLoader.displayImage((CommUtils.REQUEST_URL_SUFFIX + shopInfoPO
            .getIamgeUrl().get(1)), imageView1, TokenManager.options,
            mDisplayImageListener);
        // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
        imageLoader.displayImage((CommUtils.REQUEST_URL_SUFFIX + shopInfoPO
            .getIamgeUrl().get(2)), imageView2, TokenManager.options,
            mDisplayImageListener);
        // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
        imageLoader.displayImage((CommUtils.REQUEST_URL_SUFFIX + shopInfoPO
            .getIamgeUrl().get(3)), imageView3, TokenManager.options,
            mDisplayImageListener);
      }

    } else if (getPageSize() == 2) {
      if (shopInfoPO.getIamgeUrl().size() == 5) {
        // ImageLoader.getInstance().loadImage(
        // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(1)),
        // mImageSize, new SimpleImageLoadingListener() {
        //
        // @Override
        // public void onLoadingComplete(String imageUri, View view,
        // Bitmap loadedImage) {
        // super.onLoadingComplete(imageUri, view, loadedImage);
        // imageView1.setImageBitmap(loadedImage);
        // }
        //
        // });
        //
        // ImageLoader.getInstance().loadImage(
        // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(2)),
        // mImageSize, new SimpleImageLoadingListener() {
        //
        // @Override
        // public void onLoadingComplete(String imageUri, View view,
        // Bitmap loadedImage) {
        // super.onLoadingComplete(imageUri, view, loadedImage);
        // imageView2.setImageBitmap(loadedImage);
        // }
        //
        // });
        // ImageLoader.getInstance().loadImage(
        // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(3)),
        // mImageSize, new SimpleImageLoadingListener() {
        //
        // @Override
        // public void onLoadingComplete(String imageUri, View view,
        // Bitmap loadedImage) {
        // super.onLoadingComplete(imageUri, view, loadedImage);
        // imageView3.setImageBitmap(loadedImage);
        // }
        //
        // });
        // ImageLoader.getInstance().loadImage(
        // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(4)),
        // mImageSize, new SimpleImageLoadingListener() {
        //
        // @Override
        // public void onLoadingComplete(String imageUri, View view,
        // Bitmap loadedImage) {
        // super.onLoadingComplete(imageUri, view, loadedImage);
        // imageView4.setImageBitmap(loadedImage);
        // }
        //
        // });
        // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
        imageLoader.displayImage((CommUtils.REQUEST_URL_SUFFIX + shopInfoPO
            .getIamgeUrl().get(1)), imageView1, TokenManager.options,
            mDisplayImageListener);
        // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
        imageLoader.displayImage((CommUtils.REQUEST_URL_SUFFIX + shopInfoPO
            .getIamgeUrl().get(2)), imageView2, TokenManager.options,
            mDisplayImageListener);
        // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
        imageLoader.displayImage((CommUtils.REQUEST_URL_SUFFIX + shopInfoPO
            .getIamgeUrl().get(3)), imageView3, TokenManager.options,
            mDisplayImageListener);
        // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
        imageLoader.displayImage((CommUtils.REQUEST_URL_SUFFIX + shopInfoPO
            .getIamgeUrl().get(4)), imageView4, TokenManager.options,
            mDisplayImageListener);
      } else if (shopInfoPO.getIamgeUrl().size() == 6) {
        // ImageLoader.getInstance().loadImage(
        // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(1)),
        // mImageSize, new SimpleImageLoadingListener() {
        //
        // @Override
        // public void onLoadingComplete(String imageUri, View view,
        // Bitmap loadedImage) {
        // super.onLoadingComplete(imageUri, view, loadedImage);
        // imageView1.setImageBitmap(loadedImage);
        // }
        //
        // });
        //
        // ImageLoader.getInstance().loadImage(
        // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(2)),
        // mImageSize, new SimpleImageLoadingListener() {
        //
        // @Override
        // public void onLoadingComplete(String imageUri, View view,
        // Bitmap loadedImage) {
        // super.onLoadingComplete(imageUri, view, loadedImage);
        // imageView2.setImageBitmap(loadedImage);
        // }
        //
        // });
        // ImageLoader.getInstance().loadImage(
        // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(3)),
        // mImageSize, new SimpleImageLoadingListener() {
        //
        // @Override
        // public void onLoadingComplete(String imageUri, View view,
        // Bitmap loadedImage) {
        // super.onLoadingComplete(imageUri, view, loadedImage);
        // imageView3.setImageBitmap(loadedImage);
        // }
        //
        // });
        // ImageLoader.getInstance().loadImage(
        // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(4)),
        // mImageSize, new SimpleImageLoadingListener() {
        //
        // @Override
        // public void onLoadingComplete(String imageUri, View view,
        // Bitmap loadedImage) {
        // super.onLoadingComplete(imageUri, view, loadedImage);
        // imageView4.setImageBitmap(loadedImage);
        // }
        //
        // });
        // ImageLoader.getInstance().loadImage(
        // (CommUtils.REQUEST_URL_SUFFIX + shopInfoPO.getIamgeUrl().get(5)),
        // mImageSize, new SimpleImageLoadingListener() {
        //
        // @Override
        // public void onLoadingComplete(String imageUri, View view,
        // Bitmap loadedImage) {
        // super.onLoadingComplete(imageUri, view, loadedImage);
        // imageView5.setImageBitmap(loadedImage);
        // }
        //
        // });

        // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
        imageLoader.displayImage((CommUtils.REQUEST_URL_SUFFIX + shopInfoPO
            .getIamgeUrl().get(1)), imageView1, TokenManager.options,
            mDisplayImageListener);
        // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
        imageLoader.displayImage((CommUtils.REQUEST_URL_SUFFIX + shopInfoPO
            .getIamgeUrl().get(2)), imageView2, TokenManager.options,
            mDisplayImageListener);
        // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
        imageLoader.displayImage((CommUtils.REQUEST_URL_SUFFIX + shopInfoPO
            .getIamgeUrl().get(3)), imageView3, TokenManager.options,
            mDisplayImageListener);
        // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
        imageLoader.displayImage((CommUtils.REQUEST_URL_SUFFIX + shopInfoPO
            .getIamgeUrl().get(4)), imageView4, TokenManager.options,
            mDisplayImageListener);
        // 若options没有传递给ImageLoader.displayImage(…)方法，那么从配置默认显示选项(ImageLoaderConfiguration.defaultDisplayImageOptions(…))将被使用
        imageLoader.displayImage((CommUtils.REQUEST_URL_SUFFIX + shopInfoPO
            .getIamgeUrl().get(5)), imageView5, TokenManager.options,
            mDisplayImageListener);
      }
    }
  }
  // private void initViewFlipper() {
  // viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);
  // adapterList = new ArrayList<SimpleAdapter>();
  // // ll = new ArrayList<Integer>();
  // // for (int i = 0; i < 5; i++) {
  // // ll.add(R.drawable.ic_launcher);
  // // }
  // list = new GridView[3];
  // CustomLog.d(TAG, "getPageSize="+getPageSize());
  // for (int i = 0; i < getPageSize(); i++) {
  //
  // list[i] = (GridView) mInflater.inflate(R.layout.gridview, null);
  //
  // List<String> listTemp = new ArrayList<String>();
  //
  // for (int j = 0; j < pageColum; j++) {
  // if ((i * pageColum + j) < (shopInfoPO.getIamgeUrl().size() - 1)) {
  // // listTemp.add(CommUtils.REQUEST_URL_SUFFIX
  // // + shopInfoPO.getIamgeUrl().get(i * pageColum + j + 1));
  // listTemp.add(CommUtils.REQUEST_URL_SUFFIX
  // + shopInfoPO.getIamgeUrl().get(0));
  // CustomLog.d(TAG, "..........."+(CommUtils.REQUEST_URL_SUFFIX
  // + shopInfoPO.getIamgeUrl().get(i * pageColum + j + 1)));
  // }
  // }
  // CustomLog.d(TAG, "listTemp"+listTemp.size());
  // adapter = new SimpleAdapter(this, listTemp, R.layout.gridviewitem,
  // new String[] { "num" }, new int[] { R.id.itemText });
  // adapterList.add(adapter);
  // list[i].setAdapter(adapter);
  //
  // list[i].setFocusable(true);
  // list[i].requestFocus();
  // list[i].setSelector(new ColorDrawable(Color.TRANSPARENT));
  //
  // viewFlipper.addView(list[i]);
  //
  // }
  // }
}
