package com.beautysalon.service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import android.os.Message;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.redcdn.log.CustomLog;

import com.beautysalon.main.BaseActivity;
import com.beautysalon.po.ProductServicePO;
import com.beautysalon.po.ServiceTypePO;
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

public class PublishServiceActivity extends BaseActivity {

  private boolean isEditService = false;

  private Button btnRet = null;
  private EditText et_publish_name = null;
  private EditText et_publish_info = null;
  private EditText et_publish_detail = null;
  private Button publishInfoEdit = null;
  private Button btnUpload2 = null;
  // private Button btnStepFinish = null;
  private CameraImageDialog cid = null;
  // private TextView tvServiceArea;
  private TextView tvServiceType;
  private ImageButton mBtnServiceType;
  // private ImageButton mBtnServiceArea;

  private TextView tvServiceTag1;
  private TextView tvServiceTag2;
  private TextView tvServiceTag3;
  private TextView tvServiceTag4;
  private TextView tvServiceTag5;
  private ImageButton mBtnServiceTag1;
  private ImageButton mBtnServiceTag2;
  private ImageButton mBtnServiceTag3;
  private ImageButton mBtnServiceTag4;
  private ImageButton mBtnServiceTag5;

  // private List<CustemObject> areaList = new ArrayList<CustemObject>();
  private List<CustemObject> typeList = new ArrayList<CustemObject>();
  private List<CustemObject> tag1 = new ArrayList<CustemObject>();
  private List<CustemObject> tag2 = new ArrayList<CustemObject>();
  private List<CustemObject> tag3 = new ArrayList<CustemObject>();
  private List<CustemObject> tag4 = new ArrayList<CustemObject>();
  private List<CustemObject> tag5 = new ArrayList<CustemObject>();

  private AbstractSpinerAdapter<CustemObject> mTypeAdapterTypeList;
  private AbstractSpinerAdapter<CustemObject> mTypeAdapterTag1;
  private AbstractSpinerAdapter<CustemObject> mTypeAdapterTag2;
  private AbstractSpinerAdapter<CustemObject> mTypeAdapterTag3;
  private AbstractSpinerAdapter<CustemObject> mTypeAdapterTag4;
  private AbstractSpinerAdapter<CustemObject> mTypeAdapterTag5;

  private Uri mOutPutFileUri = null;
  private final int IMAGE_CODE = 0;
  private final int CAMERA = 1;
  private final String IMAGE_TYPE = "image/*";
  private OnUploadProcessListener upLoadTest;
  private ProductServicePO productServicePO = new ProductServicePO();
  // 保存图片的路径
  private ArrayList<String> datas = new ArrayList<String>();

  private List<ServiceTypePO> mServiceTypePOList = null;

  // 服务图片
  private String upLoadPath1 = "";
  private String tempPath = "";
  private int currentProductId = 0;

  private TextView tvUploadPic = null;
  private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
  private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
  private static final int PHOTO_REQUEST_CUT = 3;// 结果
  // 创建一个以当前时间为名称的文件
  File tempFile = null;

  File tempFilePath;

  // String tempFile ="";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.publish_service);

    productServicePO = (ProductServicePO) getIntent().getSerializableExtra(
        "service");
    if (productServicePO != null && !productServicePO.getServiceId().isEmpty()) {
      isEditService = true;
    }
    initServiceTypePOList();
    // init();
    // addEvn();
    // setupViews();
  }

  // 初始化serviceType
  private void initServiceTypePOList() {
    mServiceTypePOList = new ArrayList<ServiceTypePO>();

    AsyncHttpClient httpClient = new AsyncHttpClient();

    RequestParams param = new RequestParams();
    param.put(ConstConfig.TOKEN, TokenManager.getInstance().getToken());
    // param.put(ConstConfig.USER_ID, TokenManager.getInstance().getUserId());

    httpClient.post(CommUtils.getServTypeUrl(), param,
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
                CustomLog.e("请求成功。。。。", response.toString());
                PublishServiceActivity.this.removeLoadingView();
                JSONArray ja1 = new JSONArray();
                ja1 = response.optJSONArray("prods");
                if (ja1.length() == 0) {

                } else {
                  CustomLog.d(TAG, "ja1=" + ja1.length());
                  for (int i = 0; i < ja1.length(); i++) {
                    ServiceTypePO mServiceTypePO = new ServiceTypePO();
                    List<String> prodTypeId = new ArrayList<String>();
                    List<String> prodTypeName = new ArrayList<String>();
                    mServiceTypePO.setproductId(((JSONObject) (ja1.get(i)))
                        .getString("productid"));
                    mServiceTypePO.setproductName(((JSONObject) (ja1.get(i)))
                        .getString("productname"));
                    JSONArray ja2 = new JSONArray();
                    ja2 = (((JSONObject) (ja1.get(i)))
                        .optJSONArray("prodtypes"));

                    CustomLog.d(TAG, "ja2" + ja2.length());

                    if (ja2.length() > 0) {
                      for (int j = 0; j < ja2.length(); j++) {
                        if (((JSONObject) (ja2.get(j))).optString("prodtypeid")
                            .isEmpty()) {

                        } else {
                          prodTypeId.add(j, ((JSONObject) (ja2.get(j)))
                              .optString("prodtypeid"));
                          prodTypeName.add(j, ((JSONObject) (ja2.get(j)))
                              .optString("prodtypename"));
                        }
                      }
                    }
                    CustomLog.d(TAG,
                        "i=" + i + "prodtypeid=" + prodTypeId.toString()
                            + "prodtypename=" + prodTypeName
                            + "mServiceTypePO=" + mServiceTypePO);
                    mServiceTypePO.setProdTypeId(prodTypeId);
                    mServiceTypePO.setProdTypeName(prodTypeName);
                    mServiceTypePOList.add(mServiceTypePO);
                    mServiceTypePO = null;
                    prodTypeId = null;
                    prodTypeName = null;
                    CustomLog.e(TAG, "mServiceTypePOList.size="
                        + mServiceTypePOList.size());

                  }

                  setupViews();
                  if (isEditService) {
                    for (int i = 0; i < mServiceTypePOList.size(); i++) {
                      CustomLog.d(TAG,
                          "productServicePO.getProductName().toString()="
                              + productServicePO.getProductName().toString()
                              + "mServiceTypePOList.get(i).getproductName()="
                              + mServiceTypePOList.get(i).getproductName());
                      if (productServicePO.getProductName().toString()
                          .equals(mServiceTypePOList.get(i).getproductName())) {
                        currentProductId = i;
                      }
                    }
                    CustomLog.d(TAG, "currentProductId=" + currentProductId);
                  }
                }
                init();
                addEvn();
              }
            } catch (JSONException e) {
              // TODO Auto-generated catch block
              // e.printStackTrace();
              setupViews();
              init();
              addEvn();
              CustomLog.e(TAG, e.toString());
            }
          }

          @Override
          public void onFailure(int statusCode, Header[] headers,
              Throwable throwable, JSONObject errorResponse) {
            setupViews();
            init();
            addEvn();
            PublishServiceActivity.this.removeLoadingView();
            CustomLog.e("请求失敗。。。。", errorResponse.toString());
          }

          @Override
          public void onFailure(int statusCode, Header[] headers,
              String errorResponse, Throwable e) {
            setupViews();
            init();
            addEvn();
            PublishServiceActivity.this.removeLoadingView();
            CustomLog.e(TAG, "statusCode=" + statusCode + "errorResponse="
                + errorResponse.toString());
            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
          }

        });

  }

  private void init() {

    btnRet = (Button) findViewById(R.id.btn_ret);
    tvUploadPic = (TextView) findViewById(R.id.tv_upload_pic);

    et_publish_detail = (EditText) findViewById(R.id.et_publish_detail);
    et_publish_info = (EditText) findViewById(R.id.et_publish_info);
    et_publish_name = (EditText) findViewById(R.id.et_publish_name);

    // publishInfoEdit = (Button) findViewById(R.id.bt_publish_add);
    publishInfoEdit = (Button) findViewById(R.id.publish_info_edit);
    // 缺少添加標籤
    btnUpload2 = (Button) findViewById(R.id.btn_upload_pic);
    tvServiceTag1 = (TextView) findViewById(R.id.tv_publish_tag1);
    // tvServiceArea = (TextView) findViewById(R.id.tv_service_area);
    tvServiceTag2 = (TextView) findViewById(R.id.tv_publish_tag2);
    tvServiceTag3 = (TextView) findViewById(R.id.tv_publish_tag4);
    tvServiceTag4 = (TextView) findViewById(R.id.tv_publish_tag5);
    tvServiceTag5 = (TextView) findViewById(R.id.tv_publish_tag6);
    tvServiceType = (TextView) findViewById(R.id.tv_service_type);
    // mBtnServiceArea = (ImageButton) findViewById(R.id.bt_service_area);
    mBtnServiceTag1 = (ImageButton) findViewById(R.id.img_publish_tag1);
    mBtnServiceTag2 = (ImageButton) findViewById(R.id.img_publish_tag2);
    mBtnServiceTag3 = (ImageButton) findViewById(R.id.img_publish_tag4);
    mBtnServiceTag4 = (ImageButton) findViewById(R.id.img_publish_tag5);
    mBtnServiceTag5 = (ImageButton) findViewById(R.id.img_publish_tag6);

    mBtnServiceType = (ImageButton) findViewById(R.id.bt_service_type);
    // mBtnServiceArea.setOnClickListener(btnClickListener);
    mBtnServiceType.setOnClickListener(btnClickListener);
    mBtnServiceTag1.setOnClickListener(btnClickListener);
    mBtnServiceTag2.setOnClickListener(btnClickListener);
    mBtnServiceTag3.setOnClickListener(btnClickListener);
    mBtnServiceTag4.setOnClickListener(btnClickListener);
    mBtnServiceTag5.setOnClickListener(btnClickListener);
    // mButtonAddPublish = (Button) findViewById(R.id.btn_upload_pic);
    if (productServicePO != null) {
      et_publish_detail.setText(productServicePO.getServiceDec());
      et_publish_info.setText(productServicePO.getPrice() + "");
      et_publish_name.setText(productServicePO.getServiceName());

      if (productServicePO.getProductName() != null) {
        tvServiceType.setText(productServicePO.getProductName() + "");
      }
      if (productServicePO.getTypeName() != null) {
        if (productServicePO.getTypeName().size() >= 1) {
          tvServiceTag1.setText(productServicePO.getTypeName().get(0));
        }
        if (productServicePO.getTypeName().size() >= 2) {
          tvServiceTag2.setText(productServicePO.getTypeName().get(1));
        }
        if (productServicePO.getTypeName().size() >= 3) {
          tvServiceTag3.setText(productServicePO.getTypeName().get(2));
        }
        if (productServicePO.getTypeName().size() == 4) {
          tvServiceTag4.setText(productServicePO.getTypeName().get(3));
        }
        if (productServicePO.getTypeName().size() == 5) {
          tvServiceTag5.setText(productServicePO.getTypeName().get(4));
        }
      }
    }
  }

  @Override
  public void onBackPressed() {
    startIntent();
    finish();
  };

  private void addEvn() {
    btnRet.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        startIntent();
        finish();
      }
    });
    // btnUpload1.setOnClickListener(btnClickListener);
    btnUpload2.setOnClickListener(btnClickListener);
    // mButtonAddPublish.setOnClickListener(btnClickListener);
    publishInfoEdit.setOnClickListener(btnClickListener);
    upLoadTest = new OnUploadProcessListener() {

      @Override
      public void onUploadProcess(int uploadSize) {
        // TODO Auto-generated method stub

      }

      @Override
      public void onUploadDone(int responseCode, String message) {
        // TODO Auto-generated method stub
        PublishServiceActivity.this.removeLoadingView();
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
      }

      @Override
      public void initUpload(int fileSize) {
        // TODO Auto-generated method stub

      }
    };
    UploadUtil.getInstance().setOnUploadProcessListener(upLoadTest);

  }

  @SuppressLint("HandlerLeak")
  Handler myHandler = new Handler() {
    public void handleMessage(Message msg) {
      switch (msg.what) {
      case 1:
        startIntent();
        finish();
        break;
      case 2:
        Toast.makeText(PublishServiceActivity.this, (String) msg.obj,
            Toast.LENGTH_SHORT).show();
        break;
      default:
        break;
      }
      super.handleMessage(msg);
    }
  };

  private void setupViews() {
    mTypeAdapterTypeList = new CustemSpinerAdapter(this);
    mTypeAdapterTag1 = new CustemSpinerAdapter(this);
    mTypeAdapterTag2 = new CustemSpinerAdapter(this);
    mTypeAdapterTag3 = new CustemSpinerAdapter(this);
    mTypeAdapterTag4 = new CustemSpinerAdapter(this);
    mTypeAdapterTag5 = new CustemSpinerAdapter(this);

    CustomLog.d(TAG, "mServiceTypePOList.size=" + mServiceTypePOList.size());
    typeList.removeAll(typeList);
    for (int i = 0; i < mServiceTypePOList.size(); i++) {

      CustemObject object = new CustemObject();
      object.data = mServiceTypePOList.get(i).getproductName();
      CustomLog.e(TAG, "mServiceTypePOList.getProdTypeName().get(i)."
          + mServiceTypePOList.get(i).getproductName());
      typeList.add(object);

    }

    CustomLog.e(TAG, "typeList.size=" + typeList.size());
    mTypeAdapterTypeList.refreshData(typeList, 0);

    mSpinerPopWindowTag1 = new SpinerPopWindow(this);
    mSpinerPopWindowTag1.setAdatper(mTypeAdapterTag1);
    mSpinerPopWindowTag1.setItemListener(tag1Listener);

    mSpinerPopWindowTag2 = new SpinerPopWindow(this);
    mSpinerPopWindowTag2.setAdatper(mTypeAdapterTag2);
    mSpinerPopWindowTag2.setItemListener(tag2Listener);

    mSpinerPopWindowTag3 = new SpinerPopWindow(this);
    mSpinerPopWindowTag3.setAdatper(mTypeAdapterTag3);
    mSpinerPopWindowTag3.setItemListener(tag3Listener);

    mSpinerPopWindowTag4 = new SpinerPopWindow(this);
    mSpinerPopWindowTag4.setAdatper(mTypeAdapterTag4);
    mSpinerPopWindowTag4.setItemListener(tag4Listener);

    mSpinerPopWindowTag5 = new SpinerPopWindow(this);
    mSpinerPopWindowTag5.setAdatper(mTypeAdapterTag5);
    mSpinerPopWindowTag5.setItemListener(tag5Listener);

    mTypeSpinerPopWindow = new SpinerPopWindow(this);
    mTypeSpinerPopWindow.setAdatper(mTypeAdapterTypeList);
    mTypeSpinerPopWindow.setItemListener(typeListener);

  }

  private void setTag1Hero(int pos) {
    if (pos >= 0 && pos <= tag1.size()) {
      CustemObject value = tag1.get(pos);

      tvServiceTag1.setText(value.toString());
    }
  }

  private void setTag2Hero(int pos) {
    if (pos >= 0 && pos <= tag2.size()) {
      CustemObject value = tag2.get(pos);

      tvServiceTag2.setText(value.toString());
    }
  }

  private void setTag3Hero(int pos) {
    if (pos >= 0 && pos <= tag3.size()) {
      CustemObject value = tag3.get(pos);

      tvServiceTag3.setText(value.toString());
    }
  }

  private void setTag4Hero(int pos) {
    if (pos >= 0 && pos <= tag4.size()) {
      CustemObject value = tag4.get(pos);

      tvServiceTag4.setText(value.toString());
    }
  }

  private void setTag5Hero(int pos) {
    if (pos >= 0 && pos <= tag5.size()) {
      CustemObject value = tag5.get(pos);

      tvServiceTag5.setText(value.toString());
    }
  }

  private void setTypeHero(int pos) {
    if (pos >= 0 && pos <= typeList.size()) {
      CustemObject value = typeList.get(pos);

      tvServiceType.setText(value.toString());

      // int id = 0;
      for (int i = 0; i < mServiceTypePOList.size(); i++) {
        if (tvServiceType.getText().toString()
            .equals(mServiceTypePOList.get(i).getproductName())) {
          currentProductId = i;
        }
      }
      tag1.removeAll(tag1);
      tag2.removeAll(tag2);
      tag3.removeAll(tag3);
      tag4.removeAll(tag4);
      tag5.removeAll(tag5);
      // TODO
      for (int i = 0; i < mServiceTypePOList.get(currentProductId)
          .getProdTypeName().size(); i++) {
        CustomLog.d(TAG, "id=" + currentProductId);
        CustemObject object = new CustemObject();
        object.data = mServiceTypePOList.get(currentProductId)
            .getProdTypeName().get(i);
        CustomLog
            .e(TAG, "mServiceTypePOList.getTypeProductName().get(i)."
                + mServiceTypePOList.get(currentProductId).getProdTypeName()
                    .get(i));
        tag1.add(object);
        tag2.add(object);
        tag3.add(object);
        tag4.add(object);
        tag5.add(object);
      }
      mTypeAdapterTag1.refreshData(tag1, 0);
      mTypeAdapterTag2.refreshData(tag2, 0);
      mTypeAdapterTag3.refreshData(tag3, 0);
      mTypeAdapterTag4.refreshData(tag4, 0);
      mTypeAdapterTag5.refreshData(tag5, 0);

    }
  }

  private SpinerPopWindow mTypeSpinerPopWindow;
  private SpinerPopWindow mSpinerPopWindowTag1;
  private SpinerPopWindow mSpinerPopWindowTag2;
  private SpinerPopWindow mSpinerPopWindowTag3;
  private SpinerPopWindow mSpinerPopWindowTag4;
  private SpinerPopWindow mSpinerPopWindowTag5;

  private void showSpinWindowTag1() {
    CustomLog.e("", "showSpinWindow");
    mSpinerPopWindowTag1.setWidth(tvServiceTag1.getWidth());

    mSpinerPopWindowTag1.setHeight(300);

    mSpinerPopWindowTag1.showAsDropDown(tvServiceTag1);

  }

  private void showSpinWindowTag2() {
    CustomLog.e("", "showSpinWindow");
    mSpinerPopWindowTag2.setWidth(tvServiceTag2.getWidth());

    mSpinerPopWindowTag2.setHeight(300);

    mSpinerPopWindowTag2.showAsDropDown(tvServiceTag2);

  }

  private void showSpinWindowTag3() {
    CustomLog.e("", "showSpinWindow");
    mSpinerPopWindowTag3.setWidth(tvServiceTag3.getWidth());

    mSpinerPopWindowTag3.setHeight(300);

    mSpinerPopWindowTag3.showAsDropDown(tvServiceTag3);

  }

  private void showSpinWindowTag4() {
    CustomLog.e("", "showSpinWindow");
    mSpinerPopWindowTag4.setWidth(tvServiceTag4.getWidth());

    mSpinerPopWindowTag4.setHeight(300);

    mSpinerPopWindowTag4.showAsDropDown(tvServiceTag4);

  }

  private void showSpinWindowTag5() {
    CustomLog.e("", "showSpinWindow");
    mSpinerPopWindowTag5.setWidth(tvServiceTag5.getWidth());

    mSpinerPopWindowTag5.setHeight(300);

    mSpinerPopWindowTag5.showAsDropDown(tvServiceTag5);

  }

  private void ShowTypeSpinWindow() {
    if (mTypeSpinerPopWindow != null && tvServiceType != null) {
      mTypeSpinerPopWindow.setWidth(tvServiceType.getWidth());
      mTypeSpinerPopWindow.setHeight(500);
      mTypeSpinerPopWindow.showAsDropDown(tvServiceType);
    }
  }

  @SuppressWarnings("deprecation")
  private void showDialog() {

    cid = new CameraImageDialog(PublishServiceActivity.this,
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
  // if (requestCode == IMAGE_CODE) {
  // String filePath = "";
  // Uri originalUri = data.getData(); // 获得图片的uri
  // if (originalUri != null) {
  // filePath = DocumentsHelper.getPath(PublishServiceActivity.this,
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
  // upLoadPath1 = getThumPath(filePath, 400);
  // tvUploadPic.setVisibility(View.VISIBLE);
  // tvUploadPic.setText("已选择");
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
      Drawable drawable = new BitmapDrawable(photo);
      // img_btn.setBackgroundDrawable(drawable);
      SavePicInLocal(photo);
      upLoadPath1 = tempPath;
      CustomLog.d(TAG, "upLoadPath1=" + upLoadPath1);
      // upLoadPath1 = getThumPath(filePath, 400);
      tvUploadPic.setVisibility(View.VISIBLE);
      tvUploadPic.setText("已选择");

    }
  }

  OnClickListener btnClickListener = new OnClickListener() {

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
      case R.id.bt_service_type:
        ShowTypeSpinWindow();
        break;
      case R.id.btn_ret:
        finish();
        break;
      case R.id.publish_info_edit:
        // TODO;
        evaluteParam();
        break;
      case R.id.img_publish_tag1:
        showSpinWindowTag1();
        break;
      case R.id.img_publish_tag2:
        showSpinWindowTag2();
        break;
      case R.id.img_publish_tag4:
        showSpinWindowTag3();
        break;
      case R.id.img_publish_tag5:
        showSpinWindowTag4();
        break;
      case R.id.img_publish_tag6:
        showSpinWindowTag5();
        break;
      case R.id.btn_upload_pic:
        showDialog();
        break;
      default:
        break;
      }
    }
  };

  private void evaluteParam() {

    // 判断几个值存不存在
    // if (et == null || etShopInfo.getText().toString().isEmpty()) {
    // Toast.makeText(getApplicationContext(), "店铺信息未填写", Toast.LENGTH_LONG)
    // .show();
    // return;
    // }
    // if (etShopName == null || etShopName.getText().toString().isEmpty()) {
    // Toast.makeText(getApplicationContext(), "店铺名称未填写", Toast.LENGTH_LONG)
    // .show();
    // return;
    // }
    // if (tvServiceArea == null ||
    // tvServiceArea.getText().toString().isEmpty()) {
    // Toast.makeText(getApplicationContext(), "没有选择地区", Toast.LENGTH_LONG)
    // .show();
    // return;
    // }
    // if (tvServiceType == null ||
    // tvServiceArea.getText().toString().isEmpty()) {
    // Toast.makeText(getApplicationContext(), "没有选择城市", Toast.LENGTH_LONG)
    // .show();
    // return;
    // }

    if (!isEditService && (upLoadPath1 == null || upLoadPath1.isEmpty())) {
      Toast.makeText(getApplicationContext(), "服务图片未上传", Toast.LENGTH_LONG)
          .show();

      return;
    }
    datas.removeAll(datas);
    if (!upLoadPath1.isEmpty()) {
      datas.add(upLoadPath1);
    }
    // if (upLoadPath2 != null && !upLoadPath2.isEmpty()) {
    // Toast.makeText(getApplicationContext(), "税务登记未上传", Toast.LENGTH_LONG)
    // .show();
    // datas.add(upLoadPath2);
    // return;
    // }
    CustomLog.d(TAG,
        "isEditService=" + isEditService + "datas.size()=" + datas.size());
    if (!isEditService && datas.size() < 1) {
      Toast.makeText(getApplicationContext(), "信息不全，请重新上传", Toast.LENGTH_LONG)
          .show();
      return;
    } else {

      Map<String, Object> param = new HashMap<String, Object>();
      param.put(ConstConfig.TOKEN, TokenManager.getInstance().getToken());
      param.put(ConstConfig.USER_ID, TokenManager.getInstance().getUserId());
      param.put("service.servicename", et_publish_name.getText().toString());
      param.put("service.price", et_publish_info.getText().toString());
      param.put("service.servicedec", et_publish_detail.getText().toString());
      // param.put(ConstConfig.SHOP_ID, TokenManager.getInstance().getShopid());

      // for (int i = 0; i < mServiceTypePOList.getTypeProductName().size();
      // i++) {
      // if (tvServiceType.getText().toString()
      // .equals(mServiceTypePOList.getTypeProductName().get(i))) {
      // param.put("proid",
      // Integer.parseInt(mServiceTypePOList.getTypeProductId().get(i)));
      // }
      // }

      CustomLog
          .d(TAG,
              "currentProductId="
                  + currentProductId
                  + "Integer.parseInt(mServiceTypePOList.get(currentProductId).getproductId())="
                  + Integer.parseInt(mServiceTypePOList.get(currentProductId)
                      .getproductId()));

      param.put("proid", Integer.parseInt(mServiceTypePOList.get(
          currentProductId).getproductId()));
      CustomLog.d(
          TAG,
          "Integer.parseInt(mServiceTypePOList.get(currentProductId).getproductId())="
              + Integer.parseInt(mServiceTypePOList.get(currentProductId)
                  .getproductId()));
      Map<String, Object> map1 = new HashMap<String, Object>();
      Map<String, Object> map2 = new HashMap<String, Object>();
      Map<String, Object> map3 = new HashMap<String, Object>();
      Map<String, Object> map4 = new HashMap<String, Object>();
      Map<String, Object> map5 = new HashMap<String, Object>();
      List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

      if (tvServiceTag1.getText() != null
          && !tvServiceTag1.getText().toString().isEmpty()) {

        map1.put("prodtypename", tvServiceTag1.getText().toString());
        // map1.put("name", tvServiceTag1.getText().toString());
        for (int i = 0; i < mServiceTypePOList.get(currentProductId)
            .getProdTypeName().size(); i++) {
          if (tvServiceTag1
              .getText()
              .toString()
              .equals(
                  mServiceTypePOList.get(currentProductId).getProdTypeName()
                      .get(i))) {
            map1.put(
                // "id",
                "prodtypeid",
                Integer.parseInt(mServiceTypePOList.get(currentProductId)
                    .getProdTypeId().get(i)));
          }
        }
      }
      if (tvServiceTag2.getText() != null
          && !tvServiceTag2.getText().toString().isEmpty()) {
        // map2.put("prodtypename", tvServiceTag2.getText().toString());
        map2.put("prodtypename", tvServiceTag2.getText().toString());
        for (int i = 0; i < mServiceTypePOList.get(currentProductId)
            .getProdTypeName().size(); i++) {
          if (tvServiceTag2
              .getText()
              .toString()
              .equals(
                  mServiceTypePOList.get(currentProductId).getProdTypeName()
                      .get(i))) {
            map2.put(
                "prodtypeid",
                Integer.parseInt(mServiceTypePOList.get(currentProductId)
                    .getProdTypeId().get(i)));
          }
        }
      }
      if (tvServiceTag3.getText() != null
          && !tvServiceTag3.getText().toString().isEmpty()) {
        map3.put("prodtypename", tvServiceTag3.getText().toString());
        for (int i = 0; i < mServiceTypePOList.get(currentProductId)
            .getProdTypeName().size(); i++) {
          if (tvServiceTag3
              .getText()
              .toString()
              .equals(
                  mServiceTypePOList.get(currentProductId).getProdTypeName()
                      .get(i))) {
            map3.put(
                "prodtypeid",
                Integer.parseInt(mServiceTypePOList.get(currentProductId)
                    .getProdTypeId().get(i)));
          }
        }
      }
      if (tvServiceTag4.getText() != null
          && !tvServiceTag4.getText().toString().isEmpty()) {
        map4.put("prodtypename", tvServiceTag4.getText().toString());
        for (int i = 0; i < mServiceTypePOList.get(currentProductId)
            .getProdTypeName().size(); i++) {
          if (tvServiceTag4
              .getText()
              .toString()
              .equals(
                  mServiceTypePOList.get(currentProductId).getProdTypeName()
                      .get(i))) {
            map4.put(
                "prodtypeid",
                Integer.parseInt(mServiceTypePOList.get(currentProductId)
                    .getProdTypeId().get(i)));
          }
        }
      }
      if (tvServiceTag5.getText() != null
          && !tvServiceTag5.getText().toString().isEmpty()) {
        map5.put("prodtypename", tvServiceTag5.getText().toString());
        for (int i = 0; i < mServiceTypePOList.get(currentProductId)
            .getProdTypeName().size(); i++) {
          if (tvServiceTag5
              .getText()
              .toString()
              .equals(
                  mServiceTypePOList.get(currentProductId).getProdTypeName()
                      .get(i))) {
            map5.put(
                "prodtypeid",
                Integer.parseInt(mServiceTypePOList.get(currentProductId)
                    .getProdTypeId().get(i)));
          }
        }
      }
      if (map1.size() > 0) {
        list.add(map1);
      }
      if (map2.size() > 0) {
        list.add(map2);
      }
      if (map3.size() > 0) {
        list.add(map3);
      }
      if (map4.size() > 0) {
        list.add(map4);
      }
      if (map5.size() > 0) {
        list.add(map5);
      }
      JSONArray ja = new JSONArray(list);
      CustomLog.d(TAG, ja.toString());
      if (isEditService) {
        param.put("service.serviceid", productServicePO.getServiceId());
        // datas.clear();
        param.put(ConstConfig.SHOP_STATUS, "edit");
      } else {
        param.put(ConstConfig.SHOP_STATUS, "post");
      }
      param.put("types", ja);
      // param.put("service.createDateTime", System.currentTimeMillis());
      // JSONObject jo = new JSONObject();
      // ja =
      CustomLog.d(TAG, "param=" + param.toString());

      if (datas != null && datas.size() == 1) {
        CustomLog.d(TAG, "带图片的服务");
        UploadUtil.getInstance().uploadFile(datas, "files",
            CommUtils.getProduceServiceUrl(), param);
        PublishServiceActivity.this.showLoadingView("上传服务中...",
            new OnCancelListener() {

              @Override
              public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                PublishServiceActivity.this.removeLoadingView();
              }
            });
      } else {
        CustomLog.d(TAG, "不带图片的服务");
        AsyncHttpClient httpClient = new AsyncHttpClient();

        final RequestParams httpParam = new RequestParams();

        httpParam.put(ConstConfig.USER_ID, TokenManager.getInstance()
            .getUserId());
        httpParam.put(ConstConfig.SHOP_STATUS, "edit");
        httpParam.put(ConstConfig.TOKEN, TokenManager.getInstance().getToken());
        httpParam.put("service.servicename", et_publish_name.getText()
            .toString());
        httpParam.put("service.price", et_publish_info.getText().toString());
        httpParam.put("service.servicedec", et_publish_detail.getText()
            .toString());
        httpParam.put(ConstConfig.SHOP_ID, TokenManager.getInstance()
            .getShopid());
        httpParam.put("files", "");

        CustomLog.d(TAG, "currentProductId=" + currentProductId);

        httpParam.put("proid", Integer.parseInt(mServiceTypePOList.get(
            currentProductId).getproductId()));
        if (isEditService) {
          httpParam.put("service.serviceid", productServicePO.getServiceId());
          // datas.clear();
        }
        httpParam.put("types", ja);
        CustomLog.d(TAG, "ja=" + ja);
        // param.put(ConstConfig.USER_ID,
        // TokenManager.getInstance().getUserId());
        PublishServiceActivity.this.showLoadingView("上传服务中...",
            new OnCancelListener() {

              @Override
              public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                PublishServiceActivity.this.removeLoadingView();
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
                PublishServiceActivity.this.removeLoadingView();
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
                PublishServiceActivity.this.removeLoadingView();
                CustomLog.e("请求失敗。。。。", errorResponse.toString());
              }

              @Override
              public void onFailure(int statusCode, Header[] headers,
                  String errorResponse, Throwable e) {
                PublishServiceActivity.this.removeLoadingView();
                CustomLog.e(TAG, "statusCode=" + statusCode + "errorResponse="
                    + errorResponse.toString());
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
              }

            });
      }
    }

  }

  private void startIntent() {
    Intent intent = new Intent();
    intent.setClass(PublishServiceActivity.this, ServiceInfoListActivity.class);
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

  // 使用系统当前日期加以调整作为照片的名称
  private String getPhotoFileName() {
    Date date = new Date(System.currentTimeMillis());
    SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
    return dateFormat.format(date) + ".jpg";
  }

  AbstractSpinerAdapter.IOnItemSelectListener tag1Listener = new AbstractSpinerAdapter.IOnItemSelectListener() {

    @Override
    public void onItemClick(int pos) {
      setTag1Hero(pos);
    }
  };
  AbstractSpinerAdapter.IOnItemSelectListener tag2Listener = new AbstractSpinerAdapter.IOnItemSelectListener() {

    @Override
    public void onItemClick(int pos) {
      setTag2Hero(pos);
    }
  };
  AbstractSpinerAdapter.IOnItemSelectListener tag3Listener = new AbstractSpinerAdapter.IOnItemSelectListener() {

    @Override
    public void onItemClick(int pos) {
      setTag3Hero(pos);
    }
  };
  AbstractSpinerAdapter.IOnItemSelectListener tag4Listener = new AbstractSpinerAdapter.IOnItemSelectListener() {

    @Override
    public void onItemClick(int pos) {
      setTag4Hero(pos);
    }
  };
  AbstractSpinerAdapter.IOnItemSelectListener tag5Listener = new AbstractSpinerAdapter.IOnItemSelectListener() {

    @Override
    public void onItemClick(int pos) {
      setTag5Hero(pos);
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
