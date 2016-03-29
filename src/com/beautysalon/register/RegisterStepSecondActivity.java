package com.beautysalon.register;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.redcdn.log.CustomLog;

import com.beautysalon.main.BaseActivity;
import com.beautysalon.po.CityTypePO;
import com.beautysalon.po.UserPO;
import com.beautysalon.spiner.widget.AbstractSpinerAdapter;
import com.beautysalon.spiner.widget.CustemObject;
import com.beautysalon.spiner.widget.CustemSpinerAdapter;
import com.beautysalon.spiner.widget.SpinerPopWindow;
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

public class RegisterStepSecondActivity extends BaseActivity {

  private Button btnRet = null;
  private EditText etShopName = null;
  // private EditText etShopInfo = null;
  private Button btnUpload1 = null;
  private Button btnUpload2 = null;
  private Button btnStepFinish = null;
  private CameraImageDialog cid = null;
  private TextView tvServiceArea;
  private TextView tvServiceType;
  private ImageButton mBtnServiceType;
  private ImageButton mBtnServiceArea;
  private List<CustemObject> areaList = new ArrayList<CustemObject>();
  private List<CustemObject> typeList = new ArrayList<CustemObject>();
  private AbstractSpinerAdapter<CustemObject> mTypeAdapter;
  private AbstractSpinerAdapter<CustemObject> mAreaAdapter;
  private Uri mOutPutFileUri = null;
  private final int IMAGE_CODE = 0;
  private final int CAMERA = 1;
  private final String IMAGE_TYPE = "image/*";
  private OnUploadProcessListener upLoadTest;
  private UserPO userPO = new UserPO();
  // 保存图片的路径
  private ArrayList<String> datas = new ArrayList<String>();

  // 营业执照path
  private String upLoadPath1 = "";

  // 税务登记
  private String upLoadPath2 = "";
  private String tempPath = "";
  private int currentUpLoadStep = 0;// 1加载营业执照2税务登记

  private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
  private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
  private static final int PHOTO_REQUEST_CUT = 3;// 结果
  // 创建一个以当前时间为名称的文件
  File tempFile = null;

  File tempFilePath;

  private TextView tvUpload1 = null;
  private TextView tvUpload2 = null;

  private List<CityTypePO> mCityTypePoList = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.register_step_second);
    // userPO = (UserPO) getIntent().getSerializableExtra("user");
    CustomLog.e(TAG, ".....");
    // 可能恢复数据
    init();
    addEvn();
    RegisterStepSecondActivity.this.showLoadingView("获取城市和地区信息中...",
        new OnCancelListener() {

          @Override
          public void onCancel(DialogInterface dialog) {
            // TODO Auto-generated method stub
            RegisterStepSecondActivity.this.removeLoadingView();
          }
        });
    initCityTypePOList();

    // setupViews();
  }

  // 初始化serviceType
  private void initCityTypePOList() {
    mCityTypePoList = new ArrayList<CityTypePO>();

    AsyncHttpClient httpClient = new AsyncHttpClient();

    RequestParams param = new RequestParams();
    // param.put(ConstConfig.TOKEN, TokenManager.getInstance().getToken());
    // param.put(ConstConfig.USER_ID, TokenManager.getInstance().getUserId());

    httpClient.post(CommUtils.getCityInfoUrl(), param,
        new JsonHttpResponseHandler() {
          @Override
          public void onStart() {
            super.onStart();

          }

          @Override
          public void onSuccess(int statusCode, Header[] headers,
              JSONObject response) {

            CustomLog.d(TAG, "response=" + response.toString());

            // Handle resulting parsed JSON response here
            try {
              if (response.getInt(ConstConfig.RETURN_CODE) == 0) {

                try {
                  if (response.getInt(ConstConfig.RETURN_CODE) == 0) {
                    CustomLog.e("请求成功。。。。", response.toString());
                    RegisterStepSecondActivity.this.removeLoadingView();
                    JSONArray ja1 = new JSONArray();
                    ja1 = response.optJSONArray("cityinfo");
                    if (ja1.length() == 0) {

                    } else {

                      for (int i = 0; i < ja1.length(); i++) {
                        CityTypePO mCityTypePO = new CityTypePO();
                        List<String> areaId = new ArrayList<String>();
                        List<String> areaName = new ArrayList<String>();
                        mCityTypePO.setCityId(((JSONObject) (ja1.get(i)))
                            .getString("districtid"));
                        mCityTypePO.setCityName(((JSONObject) (ja1.get(i)))
                            .getString("districtname"));
                        JSONArray ja2 = new JSONArray();
                        ja2 = (((JSONObject) (ja1.get(i)))
                            .optJSONArray("prodtypes"));

                        CustomLog.d(TAG, "ja2" + ja2.length());

                        if (ja2.length() > 0) {
                          for (int j = 0; j < ja2.length(); j++) {
                            if (((JSONObject) (ja2.get(j))).optString("areaid")
                                .isEmpty()) {

                            } else {
                              areaId.add(j, ((JSONObject) (ja2.get(j)))
                                  .optString("areaid"));
                              areaName.add(j, ((JSONObject) (ja2.get(j)))
                                  .optString("areaname"));
                            }
                          }
                        }
                        CustomLog.d(TAG,
                            "i=" + i + "areaId=" + areaId.toString()
                                + "areaName=" + areaName + "mCityTypePO="
                                + mCityTypePO);
                        mCityTypePO.setAreaId(areaId);
                        mCityTypePO.setAreaName(areaName);
                        mCityTypePoList.add(mCityTypePO);
                        mCityTypePO = null;
                        areaId = null;
                        areaName = null;
                      }

                      //
                      setupViews();

                    }
                  }
                } catch (JSONException e) {
                  // TODO Auto-generated catch block
                  CustomLog.e(TAG, e.toString());
                }
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
            RegisterStepSecondActivity.this.removeLoadingView();
            if (errorResponse != null) {
              CustomLog.e("请求失敗。。。。", errorResponse.toString());
            } else {
              CustomLog.e("请求失敗。。。。", statusCode + "....");
            }
          }

          @Override
          public void onFailure(int statusCode, Header[] headers,
              String errorResponse, Throwable e) {
            RegisterStepSecondActivity.this.removeLoadingView();
            CustomLog.e(TAG, "statusCode=" + statusCode + "errorResponse="
                + errorResponse.toString());
            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
          }

        });

  }

  @SuppressLint("HandlerLeak")
  Handler myHandler = new Handler() {
    public void handleMessage(Message msg) {
      switch (msg.what) {
      case 1:
        if (TokenManager.getInstance().getCurrentShopState() == 4) {
          SharedPreferences sharePreferences = getSharedPreferences(
              "shopStatus", Context.MODE_PRIVATE);
          Editor editor = sharePreferences.edit();
          editor.putInt("status", 2);
          editor.commit();

          TokenManager.getInstance().setCurrentShopState(2);
        }
        startIntent();
        finish();
        break;
      case 2:
        Toast.makeText(RegisterStepSecondActivity.this, (String) msg.obj,
            Toast.LENGTH_SHORT).show();
        break;
      default:
        break;
      }
      super.handleMessage(msg);
    }
  };

  private void init() {
    btnRet = (Button) findViewById(R.id.btn_ret);
    etShopName = (EditText) findViewById(R.id.et_shop_name);
    // etShopInfo = (EditText) findViewById(R.id.et_shop_info);
    btnUpload1 = (Button) findViewById(R.id.btn_upload1);
    btnUpload2 = (Button) findViewById(R.id.btn_upload2);
    btnStepFinish = (Button) findViewById(R.id.btn_confim_register);
    tvUpload1 = (TextView) findViewById(R.id.tv_upload1);
    tvUpload2 = (TextView) findViewById(R.id.tv_upload2);
  }

  // @Override
  // public void onResume() {
  // super.onResume();
  // }

  private void addEvn() {
    btnRet.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        finish();
      }
    });
    btnUpload1.setOnClickListener(btnClickListener);
    btnUpload2.setOnClickListener(btnClickListener);
    btnStepFinish.setOnClickListener(btnClickListener);

    upLoadTest = new OnUploadProcessListener() {

      @Override
      public void onUploadProcess(int uploadSize) {
        // TODO Auto-generated method stub

      }

      @Override
      public void initUpload(int fileSize) {
        // TODO Auto-generated method stub

      }

      @Override
      public void onUploadDone(int responseCode, String message) {

        RegisterStepSecondActivity.this.removeLoadingView();
        // TODO Auto-generated method stub
        CustomLog.e("MainActivity...", "responseCode=" + responseCode
            + "message=" + message);
        // if()
        if (responseCode == 1) {
          // Message message = new Message();
          // message.what = 1;
          //
          myHandler.sendEmptyMessage(1);
        } else {
          Message msg = new Message();
          msg.what = 2;
          msg.obj = "responseCode=" + responseCode + "message=" + message;
          myHandler.sendMessage(msg);
        }
        // try {
        // if (message.getInt(ConstConfig.RETURN_CODE) == 0) {
        // startIntent();
        // }
        // } catch (JSONException e) {
        // // TODO Auto-generated catch block
        // Log.e(TAG, e.toString());
        // }
      }
    };
    UploadUtil.getInstance().setOnUploadProcessListener(upLoadTest);

  }

  private void setupViews() {

    tvServiceArea = (TextView) findViewById(R.id.tv_service_area);
    tvServiceType = (TextView) findViewById(R.id.tv_service_type);
    mBtnServiceArea = (ImageButton) findViewById(R.id.bt_service_area);
    mBtnServiceType = (ImageButton) findViewById(R.id.bt_service_type);
    mBtnServiceArea.setOnClickListener(btnClickListener);
    mBtnServiceType.setOnClickListener(btnClickListener);

    // String[] areas = getResources().getStringArray(R.array.area);
    for (int i = 0; i < mCityTypePoList.size(); i++) {
      CustemObject object = new CustemObject();
      object.data = mCityTypePoList.get(i).getCityName();
      typeList.add(object);

    }

    // String[] types = getResources().getStringArray(R.array.type);

    // String[] areas = getResources().getStringArray(R.array.area);
    // for (int i = 0; i < areas.length; i++) {
    // CustemObject object = new CustemObject();
    // object.data = areas[i];
    // areaList.add(object);
    //
    // }
    // mAreaAdapter.refreshData(areaList, 0);
    mTypeAdapter = new CustemSpinerAdapter(this);
    mTypeAdapter.refreshData(typeList, 0);
    mAreaAdapter = new CustemSpinerAdapter(this);
    mAreaAdapter.refreshData(areaList, 0);

    mAreaSpinerPopWindow = new SpinerPopWindow(this);
    mAreaSpinerPopWindow.setAdatper(mAreaAdapter);
    mAreaSpinerPopWindow.setItemListener(areaListener);

    mTypeSpinerPopWindow = new SpinerPopWindow(this);
    mTypeSpinerPopWindow.setAdatper(mTypeAdapter);
    mTypeSpinerPopWindow.setItemListener(typeListener);

  }

  private void setAreaHero(int pos) {
    if (pos >= 0 && pos <= areaList.size()) {
      CustemObject value = areaList.get(pos);

      tvServiceArea.setText(value.toString());
    }
  }

  private void setTypeHero(int pos) {
    if (pos >= 0 && pos <= typeList.size()) {
      areaList.removeAll(areaList);
      CustemObject value = typeList.get(pos);
      for (int i = 0; i < mCityTypePoList.size(); i++) {
        if (value.toString().equals(mCityTypePoList.get(i).getCityName())) {
          CustomLog.d(TAG, mCityTypePoList.get(i).getAreaName().toString());
          for (int j = 0; j < mCityTypePoList.get(i).getAreaName().size(); j++) {
            CustemObject object = new CustemObject();

            CustomLog.d(TAG, mCityTypePoList.get(i).getAreaName().get(j));

            object.data = mCityTypePoList.get(i).getAreaName().get(j);
            areaList.add(object);

          }
          mAreaAdapter.refreshData(areaList, 0);
        }
      }

      tvServiceType.setText(value.toString());
    }
  }

  private SpinerPopWindow mAreaSpinerPopWindow;
  private SpinerPopWindow mTypeSpinerPopWindow;

  private void showAreaSpinWindow() {
    CustomLog.e("", "showSpinWindow");
    mAreaSpinerPopWindow.setWidth(tvServiceArea.getWidth());
    mAreaSpinerPopWindow.showAsDropDown(tvServiceArea);
    if (areaList.size() == 0) {
      Toast.makeText(this, "此城市沒有地區，請選擇別的城市", Toast.LENGTH_LONG).show();
    }
  }

  private void ShowTypeSpinWindow() {

    mTypeSpinerPopWindow.setWidth(tvServiceType.getWidth());
    mTypeSpinerPopWindow.showAsDropDown(tvServiceType);
  }

  private void showDialog() {

    cid = new CameraImageDialog(RegisterStepSecondActivity.this,
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
  // if (requestCode == IMAGE_CODE) {
  // String filePath = "";
  // ContentResolver resolver = getContentResolver();
  // Uri originalUri = data.getData(); // 获得图片的uri
  // if (originalUri != null) {
  // filePath = DocumentsHelper.getPath(RegisterStepSecondActivity.this,
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
  // Toast.makeText(getApplicationContext(), "无法获取图片路径", Toast.LENGTH_SHORT)
  // .show();
  // return;
  // }
  // if (BitmapUtils.isImageType(filePath)) {
  // // upLoad(getThumPath(filePath, 400));
  // if (currentUpLoadStep == 1) {
  // upLoadPath1 = getThumPath(filePath, 400);
  // tvUpload1.setText("已选择图片");
  // } else if (currentUpLoadStep == 2) {
  // upLoadPath2 = getThumPath(filePath, 400);
  // tvUpload2.setText("已选择图片");
  // }
  // } else
  // Toast.makeText(getApplicationContext(), "文件格式错误", Toast.LENGTH_SHORT)
  // .show();
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
//      Drawable drawable = new BitmapDrawable(photo);
      // img_btn.setBackgroundDrawable(drawable);
      SavePicInLocal(photo);

      if (currentUpLoadStep == 1) {
        // upLoadPath1 = getThumPath(filePath, 400);
        upLoadPath1 = tempPath;
        tvUpload1.setText("已选择图片");
      } else if (currentUpLoadStep == 2) {
        // upLoadPath2 = getThumPath(filePath, 400);
        upLoadPath2 = tempPath;
        tvUpload2.setText("已选择图片");
      }

      // upLoadPath1 = tempPath;
      CustomLog.d(TAG, "upLoadPath1=" + upLoadPath1);
      // upLoadPath1 = getThumPath(filePath, 400);

    }
  }

  @Override
  public void onResume() {
    super.onResume();

  }

  OnClickListener btnClickListener = new OnClickListener() {

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
      case R.id.bt_service_area:
        showAreaSpinWindow();
        break;
      case R.id.bt_service_type:
        ShowTypeSpinWindow();
        break;
      case R.id.btn_upload1:
        currentUpLoadStep = 1;
        showDialog();
        break;
      case R.id.btn_upload2:
        currentUpLoadStep = 2;
        showDialog();
        break;
      case R.id.btn_confim_register:
        evaluteParam();
        break;
      default:
        break;
      }
    }
  };

  private void evaluteParam() {

    // 判断几个值存不存在
    // if (etShopInfo == null || etShopInfo.getText().toString().isEmpty()) {
    // Toast.makeText(getApplicationContext(), "店铺信息未填写", Toast.LENGTH_LONG)
    // .show();
    // return;
    // }

    datas.removeAll(datas);
    if (etShopName == null || etShopName.getText().toString().isEmpty()) {
      Toast.makeText(getApplicationContext(), "店铺名称未填写", Toast.LENGTH_LONG)
          .show();
      return;
    }
    if (tvServiceArea == null || tvServiceArea.getText().toString().isEmpty()) {
      Toast.makeText(getApplicationContext(), "没有选择地区", Toast.LENGTH_LONG)
          .show();
      return;
    }
    if (tvServiceType == null || tvServiceType.getText().toString().isEmpty()) {
      Toast.makeText(getApplicationContext(), "没有选择城市", Toast.LENGTH_LONG)
          .show();
      return;
    }

    if (upLoadPath1 == null || upLoadPath1.isEmpty()) {
      Toast.makeText(getApplicationContext(), "营业执照未上传", Toast.LENGTH_LONG)
          .show();

      return;
    }
    datas.add(upLoadPath1);
    if (upLoadPath2 == null || upLoadPath2.isEmpty()) {
      Toast.makeText(getApplicationContext(), "税务登记未上传", Toast.LENGTH_LONG)
          .show();

      return;
    }
    datas.add(upLoadPath2);
    if (datas.size() != 2) {
      Toast.makeText(getApplicationContext(), "信息不全，请重新上传" + datas.size(),
          Toast.LENGTH_LONG).show();
      return;
    } else {
      Map<String, Object> param = new HashMap<String, Object>();
      param.put(ConstConfig.TOKEN, TokenManager.getInstance().getToken());
      if (TokenManager.getInstance().getCurrentShopState() == 4) {
        param.put(ConstConfig.SHOP_STATUS, "edit");
      } else {
        param.put(ConstConfig.SHOP_STATUS, "post");
      }
      param.put(ConstConfig.SHOP_ID, TokenManager.getInstance().getShopid());
      param.put(ConstConfig.SHOP_NAME, etShopName.getText().toString());
      param.put(ConstConfig.SHOP_AREA, tvServiceArea.getText().toString());
      param.put(ConstConfig.SHOP_CITY, tvServiceType.getText().toString());
      param.put(ConstConfig.SHOP_TELEPHONE, "");
      for (int i = 0; i < datas.size(); i++) {
        CustomLog.d(TAG, "datas.i=" + datas.get(i).toString());
      }
      UploadUtil.getInstance().uploadFile(datas, ConstConfig.SHOP_FILES,
          CommUtils.getUpdateShopInfoUrl(), param);
      RegisterStepSecondActivity.this.showLoadingView("正在上传注册信息...",
          new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
              // TODO Auto-generated method stub

            }
          });
      CustomLog.d(TAG, "param=" + param);
    }

  }

  private void startIntent() {
    Intent intent = new Intent();
    intent.setClass(RegisterStepSecondActivity.this,
        RegisterStepFinishedActivity.class);
    startActivity(intent);
  }

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

  AbstractSpinerAdapter.IOnItemSelectListener areaListener = new AbstractSpinerAdapter.IOnItemSelectListener() {

    @Override
    public void onItemClick(int pos) {
      if (tvServiceType != null
          && !tvServiceType.getText().toString().isEmpty()) {
        setAreaHero(pos);
      } else {
        Toast.makeText(RegisterStepSecondActivity.this, "请先选择城市",
            Toast.LENGTH_LONG).show();
      }
    }
  };
  AbstractSpinerAdapter.IOnItemSelectListener typeListener = new AbstractSpinerAdapter.IOnItemSelectListener() {

    @Override
    public void onItemClick(int pos) {
      setTypeHero(pos);
    }
  };

  private String getThumPath(String oldPath, int bitmapMaxWidth) {
    return BitmapUtils.getThumPath(oldPath, bitmapMaxWidth);
  }
}
