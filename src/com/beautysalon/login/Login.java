package com.beautysalon.login;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.redcdn.log.CustomLog;

import com.beautysalon.main.BaseActivity;
import com.beautysalon.register.RegisterStepFinishedActivity;
import com.beautysalon.register.RegisterStepFirstActivity;
import com.beautysalon.util.CommUtils;
import com.beautysalon.util.ConstConfig;
import com.beautysalon.util.TokenManager;
import com.example.testgallary.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Login extends BaseActivity {

  private Button btnRet = null;
  private EditText etUserName = null;
  private EditText etPassword = null;
  private Button btnLogin = null;
  private Button btnRegister = null;

  protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login);
    init();
    addEvn();
    SharedPreferences sharedPreferencesStatus = getSharedPreferences(
        "userInfo", Context.MODE_PRIVATE);
    etUserName.setText(sharedPreferencesStatus.getString("userName", ""));

  }

  private void init() {
    btnRet = (Button) findViewById(R.id.btn_ret);
    etUserName = (EditText) findViewById(R.id.et_username);
    etPassword = (EditText) findViewById(R.id.et_password);
    btnLogin = (Button) findViewById(R.id.btn_login);
    btnRegister = (Button) findViewById(R.id.btn_register);
  }

  private void addEvn() {
    btnRet.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        finish();
      }
    });
    btnLogin.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub

        if (etUserName.getText().toString() == null
            || etUserName.getText().toString().isEmpty()) {
          Toast.makeText(Login.this, "用户名不能为空", Toast.LENGTH_LONG).show();
          // toast.setGravity(Gravity.CENTER, 0, 0);
          // toast.show();
          return;
        } else if (!CommUtils.isEmail(etUserName.getText().toString())) {
          CustomLog.e("onclick", etUserName.getText().toString());
          Toast.makeText(getApplicationContext(), "请输入正确的邮箱格式",
              Toast.LENGTH_LONG).show();
          return;
        }
        if (etPassword.getText().toString() == null
            || etPassword.getText().toString().isEmpty()) {
          Toast.makeText(Login.this, "密码不能为空", Toast.LENGTH_LONG).show();
          return;
        }
        Login.this.showLoadingView("登录中...");
        AsyncHttpClient httpClient = new AsyncHttpClient();

        RequestParams param = new RequestParams();
        param.put(ConstConfig.USER_NAME, etUserName.getText().toString());
        // param.put("password",
        // CommUtils.string2MD5(etPassword.getText().toString()));
        param.put(ConstConfig.PASSWORD, etPassword.getText().toString());

        httpClient.post(CommUtils.getLoginUrl(), param,
            new JsonHttpResponseHandler() {
              @Override
              public void onStart() {
                super.onStart();

              }

              @Override
              public void onSuccess(int statusCode, Header[] headers,
                  JSONObject response) {
                // Handle resulting parsed JSON response here

                Login.this.removeLoadingView();
                CustomLog.e("请求成功。。。。", response.toString());
                try {
                  if (response.getInt(ConstConfig.RETURN_CODE) == 0) {
                    SharedPreferences sharePreferencesUser = getSharedPreferences(
                        "userInfo", Context.MODE_PRIVATE);
                    Editor editorUser = sharePreferencesUser.edit();
                    editorUser.putString("userName", etUserName.getText()
                        .toString());
                    editorUser.commit();
                    String userId = "";
                    String token = "";
                    try {
                      userId = response.getString("userid");
                      token = response.getString("token");
                    } catch (JSONException e) {
                      // TODO Auto-generated catch block
                      // e.printStackTrace();
                      CustomLog.e("請求錯誤。。。", e.toString());
                    }

                    CustomLog.e("请求成功。。。。", response.toString());
                    TokenManager.getInstance().setToken(token);
                    TokenManager.getInstance().setUserId(userId);
                    TokenManager.getInstance().setShopid(userId);

                    // sharePreference
                    SharedPreferences sharePreferences = getSharedPreferences(
                        "user", Context.MODE_PRIVATE);
                    Editor editor = sharePreferences.edit();
                    editor.putString(ConstConfig.SHARE_TOKEN, token);
                    editor.putString(ConstConfig.SHARE_USERID, userId);
                    editor.commit();
                    Intent intent = new Intent();
                    // intent.setClass(Login.this,
                    // StoreManagementActivity.class);
                    intent.setClass(Login.this,
                        RegisterStepFinishedActivity.class);
                    startActivity(intent);
                    finish();
                  } else {
                    if (response.getInt(ConstConfig.RETURN_CODE) == -907) {
                      Toast.makeText(Login.this, "用户名已注册，请重新换个用户名！",
                          Toast.LENGTH_LONG).show();
                    } else if (response.getInt(ConstConfig.RETURN_CODE) == -905) {
                      Toast.makeText(Login.this, "用户名或者密码错误，请重新输入",
                          Toast.LENGTH_LONG).show();
                    } else {
                      Toast
                          .makeText(Login.this, "网络错误，请联网！", Toast.LENGTH_LONG)
                          .show();
                    }
                  }
                } catch (JSONException e) {
                  CustomLog.e(TAG, e.toString());
                }
              }

              // @Override
              // public void onSuccess(int statusCode, Header[] headers,
              // JSONArray response) {
              //
              // }
              //
              // @Override
              // public void onFailure(int statusCode, Header[] headers,
              // Throwable throwable, JSONArray errorResponse) {
              //
              // }
              @Override
              public void onFailure(int statusCode, Header[] headers,
                  Throwable throwable, JSONObject errorResponse) {
                Login.this.removeLoadingView();
                CustomLog.e("请求失敗。。。。", "statusCode=" + statusCode);
                if (errorResponse != null) {
                  CustomLog.e("请求失敗。。。。", errorResponse.toString());
                }
              }

              @Override
              public void onFailure(int statusCode, Header[] headers,
                  String errorResponse, Throwable e) {
                Login.this.removeLoadingView();
                CustomLog.e(TAG, "statusCode=" + statusCode + "errorResponse="
                    + errorResponse.toString());
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
              }
            });
        // Intent intent = new Intent();
        // intent.setClass(Login.this, StoreManagementActivity.class);
        // startActivity(intent);
      }
    });
    btnRegister.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(Login.this, RegisterStepFirstActivity.class);
        startActivity(intent);
        finish();
      }
    });
  }
}
