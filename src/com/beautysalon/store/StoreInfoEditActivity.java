package com.beautysalon.store;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.redcdn.log.CustomLog;

import com.beautysalon.main.BaseActivity;
import com.beautysalon.po.ShopInfoPO;
import com.beautysalon.util.BitmapUtils;
import com.beautysalon.util.CameraImageDialog;
import com.beautysalon.util.CameraImageDialog.CameraClickListener;
import com.beautysalon.util.CameraImageDialog.NoClickListener;
import com.beautysalon.util.CameraImageDialog.PhoneClickListener;
import com.beautysalon.util.CommUtils;
import com.beautysalon.util.ConstConfig;
import com.beautysalon.util.TokenManager;
import com.beautysalon.util.UploadUtil;
import com.beautysalon.util.UploadUtil.OnUploadProcessListener;
import com.example.testgallary.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class StoreInfoEditActivity extends BaseActivity {

  private static final String TAG = StoreInfoEditActivity.class.getSimpleName();

  private Button btnRet = null;
  private EditText etShopName = null;
  private EditText etShopPhone = null;
  private EditText etShopTime = null;
  private EditText etShopDetail = null;
  private EditText etShopArea = null;
  private Button btnUpload1 = null;
  private Button btnUpload2 = null;
  private Button btnStoreFinish = null;

  private TextView tvUpLoad1 = null;

  private CameraImageDialog cid = null;
  private Uri mOutPutFileUri = null;
  private final int IMAGE_CODE = 0;
  private final int CAMERA = 1;
  private final String IMAGE_TYPE = "image/*";
  private OnUploadProcessListener upLoadTest;
  private ShopInfoPO shopInfoPO = new ShopInfoPO();
  // 保存图片的路径
  private ArrayList<String> datas = new ArrayList<String>();

  // 店铺信息path
  private String upLoadPath1 = "";

  // 店铺环境
  private List<String> upLoadPath2 = new ArrayList<String>();

  private String tempPath = "";

  private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
  private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
  private static final int PHOTO_REQUEST_CUT = 3;// 结果
  // 创建一个以当前时间为名称的文件
  File tempFile = null;

  File tempFilePath;

  private int currentUpLoadStep = 0;// 1加载营业执照2税务登记
  private final String PHONE_REGEXP = "^((147)|(13[0-9])|(15[^4,\\D])|(17[6-7])|(18[0-9]))\\d{8}$";

  private final String TIME_REGEXP = "^(([1-9]{1})|([0-1][0-9])|([1-2][0-3])):([0-5][0-9])-(([1-9]{1})|([0-1][0-9])|([1-2][0-3])):([0-5][0-9])$";

  private final String NUMBER_REGEXP = "^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.store_info_edit);
    shopInfoPO = (ShopInfoPO) getIntent().getSerializableExtra("shopInfo");
    init();
    initData();
    addEvn();

  }

  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(android.os.Message msg) {
      StoreInfoEditActivity.this.removeLoadingView();
      if (msg.what == 1) {

        Intent intent = new Intent();
        intent.setClass(StoreInfoEditActivity.this, StoreInfoActivity.class);
        startActivity(intent);
        finish();

      } else {
        Toast.makeText(getApplicationContext(), "responseCode=" + msg.what,
            Toast.LENGTH_SHORT).show();

      }

    };
  };

  private boolean isLocalNumber(String number) {
    if (TextUtils.isEmpty(number)) {
      return false;
    }
    Pattern p = Pattern.compile(NUMBER_REGEXP);
    return p.matcher(number).matches();
  }

  private boolean isPhoneNumber(String phone) {
    if (TextUtils.isEmpty(phone)) {
      return false;
    }
    Pattern p = Pattern.compile(PHONE_REGEXP);
    return p.matcher(phone).matches();
  }

  private boolean isBussinessTime(String time) {
    if (TextUtils.isEmpty(time)) {
      return false;
    }
    CustomLog.d(TAG, "time=" + time);
    Pattern p = Pattern.compile(TIME_REGEXP);
    return p.matcher(time).matches();
  }

  private void init() {
    btnRet = (Button) findViewById(R.id.btn_ret);
    etShopDetail = (EditText) findViewById(R.id.et_shop_detail);

    etShopName = (EditText) findViewById(R.id.et_shop_name);

    etShopArea = (EditText) findViewById(R.id.et_shop_area);
    etShopTime = (EditText) findViewById(R.id.et_shop_time);
    etShopPhone = (EditText) findViewById(R.id.et_shop_phone);
    btnUpload1 = (Button) findViewById(R.id.btn_register_step_img);
    btnUpload2 = (Button) findViewById(R.id.btn_confim_register);
    btnStoreFinish = (Button) findViewById(R.id.btn_store_finish);
    tvUpLoad1 = (TextView) findViewById(R.id.tv_upload1);
    etShopArea.setEnabled(false);
  }

  private void initData() {
    etShopDetail.setText(shopInfoPO.getDec() + "");
    etShopName.setText(shopInfoPO.getShopName() + "");
    etShopArea.setText(shopInfoPO.getCity() + shopInfoPO.getArea() + "");
    etShopPhone.setText(shopInfoPO.getTelephone() + "");
    etShopTime.setText(shopInfoPO.getBussinessTime() + "");
    // etShopTime.setText(shopInfoPO.get)
  }

  private void addEvn() {
    btnRet.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        Intent intent = new Intent();
        intent.setClass(StoreInfoEditActivity.this, StoreInfoActivity.class);
        startActivity(intent);
        finish();
      }
    });
    btnUpload1.setOnClickListener(btnClickListener);
    btnUpload2.setOnClickListener(btnClickListener);
    btnStoreFinish.setOnClickListener(btnClickListener);

    upLoadTest = new OnUploadProcessListener() {

      @Override
      public void onUploadProcess(int uploadSize) {
        // TODO Auto-generated method stub

      }

      @Override
      public void onUploadDone(int responseCode, String message) {
        // TODO Auto-generated method stub
        CustomLog.e("MainActivity...", "responseCode=" + responseCode
            + "message=" + message);
        // startIntent();
        mHandler.sendEmptyMessage(responseCode);
        //
      }

      @Override
      public void initUpload(int fileSize) {
        // TODO Auto-generated method stub

      }
    };

  }

  private void showDialog() {

    cid = new CameraImageDialog(StoreInfoEditActivity.this,
        R.style.contact_del_dialog);
    cid.setCameraClickListener(new CameraClickListener() {

      @Override
      public void clickListener() {
        camera();
      }
    });
    cid.setPhoneClickListener(new PhoneClickListener() {

      @Override
      public void clickListener() {
        photoFile();
      }
    });
    cid.setNoClickListener(new NoClickListener() {

      @Override
      public void clickListener() {
        cid.dismiss();
      }
    });
    Window window = cid.getWindow();

    window.setGravity(Gravity.BOTTOM);

    cid.show();
    WindowManager windowManager = getWindowManager();
    Display display = windowManager.getDefaultDisplay();
    WindowManager.LayoutParams lp = cid.getWindow().getAttributes();
    lp.width = (int) (display.getWidth()); // 设置宽度
    cid.getWindow().setAttributes(lp);
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (cid != null)
      cid.dismiss();
  }

  // @Override
  // public void onActivityResult(int requestCode, int resultCode, Intent data)
  // {
  //
  // if (resultCode != RESULT_OK) {
  // return;
  // }
  // CustomLog.e(TAG, "resultCode=" + resultCode + "requestCode=" + requestCode
  // + "path=" + data.getData().getPath());
  // Bitmap bp = null;
  // if (requestCode == CAMERA) {
  // Uri imageUri = null;
  // if (data != null) {
  // if (data.hasExtra("data") && data.getData() != null) {
  //
  // if (currentUpLoadStep == 1) {
  // upLoadPath1 = getThumPath(data.getData().getPath(), 400);
  // } else if (currentUpLoadStep == 2) {
  // upLoadPath2.add(getThumPath(data.getData().getPath(), 400));
  // }
  // // upLoad(getThumPath(data.getData().getPath(), 400));
  // }
  // } else {
  //
  // if (currentUpLoadStep == 1) {
  // upLoadPath1 = getThumPath(mOutPutFileUri.getPath(), 400);
  // } else if (currentUpLoadStep == 2) {
  // upLoadPath2.add(getThumPath(mOutPutFileUri.getPath(), 400));
  // }
  //
  // // upLoad(getThumPath(mOutPutFileUri.getPath(), 400));
  // }
  //
  // } else {
  // if (requestCode == IMAGE_CODE) {
  // String filePath = "";
  // ContentResolver resolver = getContentResolver();
  // Uri originalUri = data.getData(); // 获得图片的uri
  // if (originalUri != null) {
  // filePath = DocumentsHelper.getPath(StoreInfoEditActivity.this,
  // originalUri);
  // }
  //
  // if (TextUtils.isEmpty(filePath)) {
  // filePath = data.getDataString();
  // if (!TextUtils.isEmpty(filePath) && filePath.startsWith("file:///")) {
  // filePath = filePath.replace("file://", "");
  // try {
  // // java的文件系统是linux,而编码格式是UTF-8的编码格式
  // filePath = URLDecoder.decode(filePath, "UTF-8");
  // } catch (UnsupportedEncodingException e) {
  // e.printStackTrace();
  // }
  // }
  // }
  // if (filePath == null || filePath.equalsIgnoreCase("")) {
  // Toast.makeText(getApplicationContext(), "无法获取图片路径",
  // Toast.LENGTH_SHORT).show();
  // return;
  // }
  // if (BitmapUtils.isImageType(filePath)) {
  // // upLoad(getThumPath(filePath, 400));
  // if (currentUpLoadStep == 1) {
  // upLoadPath1 = getThumPath(filePath, 400);
  // Bitmap pic = BitmapFactory.decodeFile(filePath);
  // btnUpload1.setBackgroundDrawable(new BitmapDrawable(pic));
  // } else if (currentUpLoadStep == 2) {
  // upLoadPath2.add(getThumPath(filePath, 400));
  // switch (upLoadPath2.size()) {
  // case 1:
  // CustomLog.d(TAG, "upLoadPath2 1");
  // tvUpLoad1.setText("已添加一张图片，最多可以添加五张图片");
  // break;
  // case 2:
  // CustomLog.d(TAG, "upLoadPath2 2");
  // tvUpLoad1.setText("已添加两张图片，最多可以添加五张图片");
  // break;
  // case 3:
  // CustomLog.d(TAG, "upLoadPath2 3");
  // tvUpLoad1.setText("已添加三张图片，最多可以添加五张图片");
  // break;
  // case 4:
  // CustomLog.d(TAG, "upLoadPath2 4");
  // tvUpLoad1.setText("已添加四张图片，最多可以添加五张图片");
  // break;
  // case 5:
  // CustomLog.d(TAG, "upLoadPath2 5");
  // tvUpLoad1.setText("已添加五张图片，最多可以添加五张图片");
  // break;
  // default:
  // break;
  // }
  // }
  // } else
  // Toast.makeText(getApplicationContext(), "文件格式错误", Toast.LENGTH_SHORT)
  // .show();
  // }
  // }
  // }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // TODO Auto-generated method stub

    switch (requestCode) {
    case PHOTO_REQUEST_TAKEPHOTO:
      tempFile = new File(Environment.getExternalStorageDirectory().getPath()
          + "/meeting/imgCache/" + Calendar.getInstance().getTimeInMillis()
          + ".jpg"); // 以时间秒为文件名
      File temp = new File(Environment.getExternalStorageDirectory().getPath()
          + "/meeting/imgCache/");// 自已项目 文件夹
      if (!temp.exists()) {
        temp.mkdir();
      }

      // tempFile = new File(Environment.getExternalStorageDirectory(),
      // getPhotoFileName());
      startPhotoZoom(Uri.fromFile(tempFile), 150);
      break;

    case PHOTO_REQUEST_GALLERY:
      if (data != null) {
        startPhotoZoom(data.getData(), 150);
        CustomLog.d(TAG, "path=====" + data.getData());
      }
      break;

    case PHOTO_REQUEST_CUT:
      if (data != null)
        setPicToView(data);
      break;
    }
    super.onActivityResult(requestCode, resultCode, data);

  }

  private void startPhotoZoom(Uri uri, int size) {
    Intent intent = new Intent("com.android.camera.action.CROP");

    intent.setDataAndType(uri, "image/*");
    // crop为true是设置在开启的intent中设置显示的view可以剪裁
    intent.putExtra("crop", "true");

    // aspectX aspectY 是宽高的比例
    intent.putExtra("aspectX", 1);
    intent.putExtra("aspectY", 1);

    // outputX,outputY 是剪裁图片的宽高
    intent.putExtra("outputX", size);
    intent.putExtra("outputY", size);
    intent.putExtra("return-data", true);
    startActivityForResult(intent, PHOTO_REQUEST_CUT);
  }

  // 保存拍摄的照片到手机的sd卡
  private void SavePicInLocal(Bitmap bitmap) {

    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    ByteArrayOutputStream baos = null; // 字节数组输出流
    try {
      baos = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
      byte[] byteArray = baos.toByteArray();// 字节数组输出流转换成字节数组

      tempPath = Environment.getExternalStorageDirectory().getPath()
          + "/meeting/imgCache/" + Calendar.getInstance().getTimeInMillis()
          + ".jpg";

      tempFile = new File(tempPath); // 以时间秒为文件名
      File temp = new File(Environment.getExternalStorageDirectory().getPath()
          + "/meeting/imgCache/");// 自已项目 文件夹
      if (!temp.exists()) {
        temp.mkdir();
      }

      // 将字节数组写入到刚创建的图片文件中
      fos = new FileOutputStream(tempFile);
      bos = new BufferedOutputStream(fos);
      bos.write(byteArray);

    } catch (Exception e) {
      e.printStackTrace();

    } finally {
      if (baos != null) {
        try {
          baos.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      if (bos != null) {
        try {
          bos.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      if (fos != null) {
        try {
          fos.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

    }

  }

  // 将进行剪裁后的图片显示到UI界面上
  private void setPicToView(Intent picdata) {
    Bundle bundle = picdata.getExtras();
    if (bundle != null) {
      Bitmap photo = bundle.getParcelable("data");
      SavePicInLocal(photo);

      if (currentUpLoadStep == 1) {
        upLoadPath1 = tempPath;

        Drawable drawable = new BitmapDrawable(photo);
        btnUpload1.setBackgroundDrawable(drawable);

        // btnUpload1.setBackgroundDrawable(new BitmapDrawable(pic));
      } else if (currentUpLoadStep == 2) {
        upLoadPath2.add(tempPath);
        switch (upLoadPath2.size()) {
        case 1:
          CustomLog.d(TAG, "upLoadPath2 1");
          tvUpLoad1.setText("已添加一张图片，最多可以添加五张图片");
          break;
        case 2:
          CustomLog.d(TAG, "upLoadPath2 2");
          tvUpLoad1.setText("已添加两张图片，最多可以添加五张图片");
          break;
        case 3:
          CustomLog.d(TAG, "upLoadPath2 3");
          tvUpLoad1.setText("已添加三张图片，最多可以添加五张图片");
          break;
        case 4:
          CustomLog.d(TAG, "upLoadPath2 4");
          tvUpLoad1.setText("已添加四张图片，最多可以添加五张图片");
          break;
        case 5:
          CustomLog.d(TAG, "upLoadPath2 5");
          tvUpLoad1.setText("已添加五张图片，最多可以添加五张图片");
          break;
        default:
          break;
        }
      }

      // if (currentUpLoadStep == 1) {
      // // upLoadPath1 = getThumPath(filePath, 400);
      // upLoadPath1 = tempPath;
      // tvUpload1.setText("已选择图片");
      // } else if (currentUpLoadStep == 2) {
      // // upLoadPath2 = getThumPath(filePath, 400);
      // upLoadPath2 = tempPath;
      // tvUpload2.setText("已选择图片");
      // }

      // upLoadPath1 = tempPath;
      CustomLog.d(TAG, "upLoadPath1=" + upLoadPath1);
      // upLoadPath1 = getThumPath(filePath, 400);

    }
  }

  OnClickListener btnClickListener = new OnClickListener() {

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
      case R.id.btn_register_step_img:
        currentUpLoadStep = 1;
        showDialog();
        break;
      case R.id.btn_confim_register:
        currentUpLoadStep = 2;
        if (upLoadPath2.size() < 5) {
          showDialog();
        } else {
          Toast.makeText(getApplicationContext(), "已选择了五张图片，最多选择五张图片",
              Toast.LENGTH_LONG).show();
        }
        break;
      case R.id.btn_store_finish:
        evaluteParam();
        break;
      default:
        break;
      }
    }
  };

  private void evaluteParam() {

    // 判断几个值存不存在
    if (etShopName == null || etShopName.getText().toString().isEmpty()) {
      Toast.makeText(getApplicationContext(), "店铺名称未填写", Toast.LENGTH_LONG)
          .show();
      return;
    }
    if (etShopArea == null || etShopArea.getText().toString().isEmpty()) {
      Toast.makeText(getApplicationContext(), "所在地区未填写", Toast.LENGTH_LONG)
          .show();
      return;
    }

    if (etShopDetail == null || etShopDetail.getText().toString().isEmpty()) {
      Toast.makeText(getApplicationContext(), "详细信息未填写", Toast.LENGTH_LONG)
          .show();
      return;
    }
    if (etShopPhone == null || etShopPhone.getText().toString().isEmpty()) {
      Toast.makeText(getApplicationContext(), "联系方式未填写", Toast.LENGTH_LONG)
          .show();
      return;
    }

    if (!isPhoneNumber(etShopPhone.getText().toString())
        && !isLocalNumber(etShopPhone.getText().toString())) {
      Toast.makeText(getApplicationContext(),
          "请输入有效联系人是，固定电话格式参照021-55555555,谢谢合作!", Toast.LENGTH_LONG).show();
      return;
    }
    // if (!CommUtils.isPhoneNumberValid(etShopPhone.getText().toString())) {
    // Toast.makeText(getApplicationContext(), "请输入有效地联系方式，谢谢合作!",
    // Toast.LENGTH_LONG).show();
    // return;
    // }
    if (etShopTime == null || etShopTime.getText().toString().isEmpty()) {
      Toast.makeText(getApplicationContext(), "营业时间未填写", Toast.LENGTH_LONG)
          .show();
      return;
    }
    if (!isBussinessTime(etShopTime.getText().toString())) {
      Toast.makeText(getApplicationContext(),
          "请输入有效地营业时间，格式参照09:00-11:00,谢谢合作!", Toast.LENGTH_LONG).show();
      return;
    }
    if (upLoadPath1 != null && !upLoadPath1.isEmpty()) {

      datas.add(upLoadPath1);

    } else {
    }
    if (upLoadPath2 != null && !upLoadPath2.isEmpty()) {

      datas.addAll(upLoadPath2);

    } else {
    }
    if (datas.size() < 2) {
      // Toast.makeText(getApplicationContext(), "信息不全，请重新上传",
      // Toast.LENGTH_LONG)
      // .show();
      // return;
      AsyncHttpClient httpClient = new AsyncHttpClient();

      final RequestParams httpParam = new RequestParams();
      // Map<String, Object> param = new HashMap<String, Object>();
      httpParam.put(ConstConfig.TOKEN, TokenManager.getInstance().getToken());
      httpParam.put(ConstConfig.SHOP_STATUS, "edit");
      httpParam
          .put(ConstConfig.SHOP_ID, TokenManager.getInstance().getShopid());
      httpParam.put(ConstConfig.SHOP_NAME, etShopName.getText().toString());
      httpParam.put(ConstConfig.SHOP_AREA, shopInfoPO.getArea().toString());
      httpParam.put(ConstConfig.SHOP_CITY, shopInfoPO.getCity().toString());
      httpParam.put(ConstConfig.SHOP_TELEPHONE, etShopPhone.getText()
          .toString());
      httpParam.put("shop.businesstime", etShopTime.getText().toString());
      httpParam.put("shop.shopdec", etShopDetail.getText().toString());
      httpParam.put("files", "");
      // param.put(ConstConfig.USER_ID,
      // TokenManager.getInstance().getUserId());
      StoreInfoEditActivity.this.showLoadingView("上传服务中...",
          new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
              // TODO Auto-generated method stub
              StoreInfoEditActivity.this.removeLoadingView();
            }
          });
      httpClient.post(CommUtils.getProduceServiceUrl(), httpParam,
          new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
              CustomLog.d(TAG, "onStart httpParam=" + httpParam.toString());
              super.onStart();

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                JSONObject response) {
              StoreInfoEditActivity.this.removeLoadingView();
              CustomLog.d(TAG, "response=" + response.toString());

              // Handle resulting parsed JSON response here
              try {
                if (response.getInt(ConstConfig.RETURN_CODE) == 0) {
                  CustomLog.d(TAG, "success=" + response.toString());
                  startIntent();
                  finish();
                }
              } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
              StoreInfoEditActivity.this.removeLoadingView();
              CustomLog.e("请求失敗。。。。", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                String errorResponse, Throwable e) {
              StoreInfoEditActivity.this.removeLoadingView();
              CustomLog.e(TAG, "statusCode=" + statusCode + "errorResponse="
                  + errorResponse.toString());
              // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

          });

    } else {

      Map<String, Object> param = new HashMap<String, Object>();
      param.put(ConstConfig.TOKEN, TokenManager.getInstance().getToken());
      param.put(ConstConfig.SHOP_STATUS, "edit");
      param.put(ConstConfig.SHOP_ID, TokenManager.getInstance().getShopid());
      param.put(ConstConfig.SHOP_NAME, etShopName.getText().toString());
      param.put(ConstConfig.SHOP_AREA, shopInfoPO.getArea().toString());
      param.put(ConstConfig.SHOP_CITY, shopInfoPO.getCity().toString());
      param.put(ConstConfig.SHOP_TELEPHONE, etShopPhone.getText().toString());
      param.put("shop.businesstime", etShopTime.getText().toString());
      param.put("shop.shopdec", etShopDetail.getText().toString());
      for (int i = 0; i < datas.size(); i++) {
        CustomLog.d(TAG, "datas.i=" + datas.get(i).toString());
      }

      UploadUtil.getInstance().uploadFile(datas, ConstConfig.SHOP_FILES,
          CommUtils.getUpdateShopInfoUrl(), param);
      UploadUtil.getInstance().setOnUploadProcessListener(upLoadTest);
      StoreInfoEditActivity.this.showLoadingView("上传数据中...",
          new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
              // TODO Auto-generated method stub
              StoreInfoEditActivity.this.removeLoadingView();
            }
          });
    }

  }

  private void startIntent() {
    Intent intent = new Intent();
    intent.setClass(StoreInfoEditActivity.this, StoreInfoActivity.class);
    startActivity(intent);
  }

  // private void startIntent() {
  // Intent intent = new Intent();
  // intent.setClass(StoreInfoEditActivity.this,
  // RegisterStepFinishedActivity.class);
  // startActivity(intent);
  // }

  private void camera() {
    String state = Environment.getExternalStorageState();
    if (state.equals(Environment.MEDIA_MOUNTED)) {

      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      // 文件夹aaaa
      String path = Environment.getExternalStorageDirectory().toString()
          + "/aaaa";
      File path1 = new File(path);
      if (!path1.exists()) {
        path1.mkdirs();
      }
      File file = new File(path1, System.currentTimeMillis() + ".jpg");
      mOutPutFileUri = Uri.fromFile(file);
      if (mOutPutFileUri != null) {
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
        startActivityForResult(intent, 1);
      } else {
        Toast.makeText(getApplicationContext(), "图片路径设置失败", Toast.LENGTH_SHORT)
            .show();
      }

    } else {
      Toast.makeText(getApplicationContext(), "sd卡不存在，请检查", Toast.LENGTH_SHORT)
          .show();
    }

  }

  // private void photoFile() {
  // String state = Environment.getExternalStorageState();
  // if (state.equals(Environment.MEDIA_MOUNTED)) {
  // Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
  //
  // getAlbum.setType(IMAGE_TYPE);
  // startActivityForResult(getAlbum, IMAGE_CODE);
  // } else {
  // Toast.makeText(getApplicationContext(), "sd卡不存在，请检查", Toast.LENGTH_SHORT)
  // .show();
  // }
  //
  // }

  private void photoFile() {
    String state = Environment.getExternalStorageState();
    if (state.equals(Environment.MEDIA_MOUNTED)) {
      // Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
      //
      // getAlbum.setType(IMAGE_TYPE);
      // startActivityForResult(getAlbum, IMAGE_CODE);

      Intent intent = new Intent(Intent.ACTION_PICK, null);
      intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
          IMAGE_TYPE);
      startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

    } else {
      Toast.makeText(getApplicationContext(), "sd卡不存在，请检查", Toast.LENGTH_SHORT)
          .show();
    }

  }

  private String getThumPath(String oldPath, int bitmapMaxWidth) {
    return BitmapUtils.getThumPath(oldPath, bitmapMaxWidth);
  }
}
