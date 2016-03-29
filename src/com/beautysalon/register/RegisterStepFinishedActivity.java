package com.beautysalon.register;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.redcdn.log.CustomLog;

import com.beautysalon.main.BaseActivity;
import com.beautysalon.store.StoreManagementActivity;
import com.beautysalon.util.CommUtils;
import com.beautysalon.util.ConstConfig;
import com.beautysalon.util.TokenManager;
import com.example.testgallary.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RegisterStepFinishedActivity extends BaseActivity {

  private Button btnRet = null;

  // private Button btnHome = null;

  private TextView tvTitle = null;

  private Button btnSetShopInfo = null;

  private RelativeLayout rl1 = null;
  private RelativeLayout rl2 = null;
  private RelativeLayout rl3 = null;

  private int currentState = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.register_finish);

    tvTitle = (TextView) findViewById(R.id.tv_finished_title);

    rl1 = (RelativeLayout) findViewById(R.id.rl1);

    rl2 = (RelativeLayout) findViewById(R.id.rl2);
    rl3 = (RelativeLayout) findViewById(R.id.rl3);
    rl1.setVisibility(View.GONE);
    rl2.setVisibility(View.GONE);
    rl3.setVisibility(View.GONE);
    // btnHome = (Button) findViewById(R.id.btn_home);
    btnSetShopInfo = (Button) findViewById(R.id.btn_setshop_info);
    // btnHome.setOnClickListener(new View.OnClickListener() {
    //
    // @Override
    // public void onClick(View v) {
    // CustomLog.d(TAG, "btnHomeClick");
    // Intent intent = new Intent();
    // intent.setClass(RegisterStepFinishedActivity.this,
    // StoreManagementActivity.class);
    // startActivity(intent);
    // }
    // });
    btnSetShopInfo.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        CustomLog.d(TAG, "btnSetShopInfo");
        if (currentState == 3) {
          Intent intent = new Intent();
          intent.setClass(RegisterStepFinishedActivity.this,
              StoreManagementActivity.class);
          startActivity(intent);
        } else {

          Intent intent = new Intent();
          intent.setClass(RegisterStepFinishedActivity.this,
              RegisterStepSecondActivity.class);
          startActivity(intent);
        }
        finish();
      }
    });
    btnSetShopInfo = (Button) findViewById(R.id.btn_setshop_info);
    btnRet = (Button) findViewById(R.id.btn_ret);
    btnRet.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        // if (currentState == 1 || currentState == 2
        // && TokenManager.getInstance().getCurrentShopState() != 2) {
        // return;
        // }

        finish();
      }
    });
    RegisterStepFinishedActivity.this.showLoadingView("获取店铺状态中...",
        new OnCancelListener() {

          @Override
          public void onCancel(DialogInterface dialog) {
            CustomLog.e(TAG, "cancle。。。。");
            RegisterStepFinishedActivity.this.removeLoadingView();
          }
        });
    initWebData();
    // if (TokenManager.getInstance().getCurrentShopState() == 2) {
    //
    // } else {
    //
    // }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {

    if (keyCode == KeyEvent.KEYCODE_BACK) {
      // if (currentState == 1 || currentState == 2) {
      // return true;
      // }
      finish();

    }
    return true;
  }

  private void setUi() {
    CustomLog.d(TAG, "currentState=" + currentState);
    if (currentState == 1 || currentState == 2) {
      SharedPreferences sharePreferences = getSharedPreferences(
          "shopStatus", Context.MODE_PRIVATE);
      Editor editor = sharePreferences.edit();
      editor.putInt("status", 2);
      editor.commit();

      TokenManager.getInstance().setCurrentShopState(2);
      tvTitle.setText("注册成功");
      btnSetShopInfo.setVisibility(View.GONE);
      rl1.setVisibility(View.VISIBLE);
      rl2.setVisibility(View.INVISIBLE);
      rl3.setVisibility(View.INVISIBLE);
    } else if (currentState == 3) {
      SharedPreferences sharePreferences = getSharedPreferences(
          "shopStatus", Context.MODE_PRIVATE);
      Editor editor = sharePreferences.edit();
      editor.putInt("status", 3);
      editor.commit();

      TokenManager.getInstance().setCurrentShopState(3);
      tvTitle.setText("注册成功");
      btnSetShopInfo.setText("管理店铺");
      btnSetShopInfo.setVisibility(View.VISIBLE);
      rl2.setVisibility(View.VISIBLE);
      rl3.setVisibility(View.INVISIBLE);
      rl1.setVisibility(View.INVISIBLE);

      // Intent intent = new Intent();
      // intent.setClass(RegisterStepFinishedActivity.this,
      // StoreManagementActivity.class);
      // startActivity(intent);
    } else if (currentState == 4) {
      SharedPreferences sharePreferencesStatus = getSharedPreferences(
          "shopStatus", Context.MODE_PRIVATE);
      Editor editorStatus = sharePreferencesStatus.edit();
      editorStatus.putInt("status", 4);
      editorStatus.commit();

      
      TokenManager.getInstance().setCurrentShopState(4);
      SharedPreferences sharePreferences = getSharedPreferences("user",
          Context.MODE_PRIVATE);
      Editor editor = sharePreferences.edit();
      // editor.putString(ConstConfig.SHARE_TOKEN, "");
      // editor.putString(ConstConfig.SHARE_USERID, "");
      editor.clear();
      editor.commit();
      tvTitle.setText("注册失败");
      btnSetShopInfo.setVisibility(View.VISIBLE);
      btnSetShopInfo.setText("编辑注册信息");
      rl2.setVisibility(View.INVISIBLE);
      rl1.setVisibility(View.INVISIBLE);
      rl3.setVisibility(View.VISIBLE);
    }
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
            RegisterStepFinishedActivity.this.removeLoadingView();
            CustomLog.e("请求成功。。。。cccccc", response.toString());
            try {
              if (response.getInt(ConstConfig.RETURN_CODE) == 0) {
                
                
                JSONArray jaService = new JSONArray();
                
                if(!response.has("shop")){
//                  setUi();
                  Intent intent = new Intent();
                  intent.setClass(RegisterStepFinishedActivity.this,
                      RegisterStepSecondActivity.class);
                  startActivity(intent);
                  finish();
                  return ;
                }
                
                jaService = (JSONArray) response.optJSONArray("shop");
                JSONObject jsShopInfo = new JSONObject();
                jsShopInfo = (JSONObject) jaService.optJSONObject(0);
                CustomLog.e("请求成功。。。。", response.toString());
                currentState = jsShopInfo.optInt("state");
                setUi();
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
            RegisterStepFinishedActivity.this.removeLoadingView();
            CustomLog.e("请求失敗。。。。", "statusCode" + statusCode);
          }

          @Override
          public void onFailure(int statusCode, Header[] headers,
              String errorResponse, Throwable e) {
            RegisterStepFinishedActivity.this.removeLoadingView();
            CustomLog.e(TAG, "statusCode=" + statusCode + "errorResponse="
                + errorResponse.toString());
            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
          }

        });

  }

}
