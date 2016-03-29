package com.beautysalon.store;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import cn.redcdn.log.CustomLog;

import com.beautysalon.login.ImgSwitchActivity;
import com.beautysalon.main.BaseActivity;
import com.beautysalon.order.OrderManagerActivity;
import com.beautysalon.service.ServiceInfoListActivity;
import com.beautysalon.util.CommUtils;
import com.beautysalon.util.ConstConfig;
import com.beautysalon.util.CustomDialog;
import com.beautysalon.util.CustomDialog.CancelBtnOnClickListener;
import com.beautysalon.util.CustomDialog.OKBtnOnClickListener;
import com.beautysalon.util.TokenManager;
import com.example.testgallary.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class StoreManagementActivity extends BaseActivity {

  private Button btnStoreInfo = null;
  private Button btnOrderManager = null;
  private Button btnServiceInfo = null;
  private Button btnSetting = null;

  private boolean mTwiceClickReturn = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.store_management);
    init();
    addEvn();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {

      if (mTwiceClickReturn) {
        finish();
      } else {
        // mHandler.removeMessages(0);
        Toast.makeText(StoreManagementActivity.this, "再点击一次退出程序",
            Toast.LENGTH_LONG).show();
        mTwiceClickReturn = true;
        mHandler.sendEmptyMessageDelayed(0, 10000);
      }
    }
    return true;
  }

  @Override
  protected void onStop() {
    super.onStop();
    CustomLog.d(TAG, "onStop");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    CustomLog.d(TAG, "onDestroy");
    finish();

  }

  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(android.os.Message msg) {
      switch (msg.what) {
      case 0:
        mTwiceClickReturn = false;
        break;

      default:
        break;
      }
    };

  };

  private void init() {
    btnStoreInfo = (Button) findViewById(R.id.btn_store_info);
    btnOrderManager = (Button) findViewById(R.id.btn_order_mana);
    btnServiceInfo = (Button) findViewById(R.id.btn_serve_info);
    btnSetting = (Button) findViewById(R.id.btn_setting);
  }

  private void addEvn() {
    btnStoreInfo.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(StoreManagementActivity.this, StoreInfoActivity.class);
        startActivity(intent);
      }
    });
    btnOrderManager.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        CustomLog.d("StoreManager", "点击按钮了。。。。");
        Intent intent = new Intent();
        intent.setClass(StoreManagementActivity.this,
            OrderManagerActivity.class);
        startActivity(intent);
      }
    });
    btnServiceInfo.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        CustomLog.d("StoreManager", "点击按钮了。。。。");
        Intent intent = new Intent();
        intent.setClass(StoreManagementActivity.this,
            ServiceInfoListActivity.class);
        startActivity(intent);
      }
    });
    btnSetting.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        CustomLog.d("StoreManager", "点击按钮了。。。。btnSetting");
        // Intent intent = new Intent();
        // intent.setClass(StoreManagementActivity.this,
        // SettingPageActivity.class);
        // startActivity(intent);
        final CustomDialog cd = new CustomDialog(StoreManagementActivity.this);
        cd.setTip("是否确认退出！");
        cd.setCancelBtnOnClickListener(new CancelBtnOnClickListener() {

          @Override
          public void onClick(CustomDialog customDialog) {
            // TODO Auto-generated method stub
            CustomLog.d(TAG, "btn.....");
            cd.dismiss();
            return;
          }
        });
        cd.setOkBtnOnClickListener(new OKBtnOnClickListener() {

          @Override
          public void onClick(CustomDialog customDialog) {
            // TODO Auto-generated method stub
            cd.dismiss();
            StoreManagementActivity.this.showLoadingView("正在申请注销...",
                new OnCancelListener() {

                  @Override
                  public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    StoreManagementActivity.this.removeLoadingView();

                  }
                });
            CustomLog.d(TAG, "ok.....");
            AsyncHttpClient httpClient = new AsyncHttpClient();

            RequestParams param = new RequestParams();
            param.put(ConstConfig.TOKEN, TokenManager.getInstance().getToken());
            // param.put(ConstConfig.USER_ID,
            // TokenManager.getInstance().getUserId());

            httpClient.post(CommUtils.getUnLoginUrl(), param,
                new JsonHttpResponseHandler() {
                  @Override
                  public void onStart() {
                    super.onStart();

                  }

                  @Override
                  public void onSuccess(int statusCode, Header[] headers,
                      JSONObject response) {
                    SharedPreferences sharePreferences = getSharedPreferences(
                        "user", Context.MODE_PRIVATE);
                    Editor editor = sharePreferences.edit();
                    // editor.putString(ConstConfig.SHARE_TOKEN, "");
                    // editor.putString(ConstConfig.SHARE_USERID, "");
                    editor.clear();
                    editor.commit();
                    CustomLog.d(TAG, "response=" + response.toString());

                    // Handle resulting parsed JSON response here
                    try {
                      if (response.getInt(ConstConfig.RETURN_CODE) == 0) {
                        CustomLog.e("请求成功。。。。", response.toString());
                        StoreManagementActivity.this.removeLoadingView();
                        finish();
                        Intent intent = new Intent();
                        intent.setClass(StoreManagementActivity.this,
                            ImgSwitchActivity.class);
                        startActivity(intent);

                      } else {
                        finish();
                        Intent intent = new Intent();
                        intent.setClass(StoreManagementActivity.this,
                            ImgSwitchActivity.class);
                        startActivity(intent);
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
                    StoreManagementActivity.this.removeLoadingView();
                    CustomLog.e("请求失敗。。。。", errorResponse.toString());
                  }

                  @Override
                  public void onFailure(int statusCode, Header[] headers,
                      String errorResponse, Throwable e) {
                    StoreManagementActivity.this.removeLoadingView();
                    CustomLog.e(TAG, "statusCode=" + statusCode
                        + "errorResponse=" + errorResponse.toString());
                    // called when response HTTP status is "4XX" (eg. 401, 403,
                    // 404)
                  }

                });

          }
        });
        // cd .show();
        cd.setCancelable(true);
        cd.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        cd.show();

      }
    });
  }
}
