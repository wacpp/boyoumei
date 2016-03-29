package com.beautysalon.register;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.redcdn.log.CustomLog;

import com.beautysalon.main.BaseActivity;
import com.beautysalon.util.CommUtils;
import com.beautysalon.util.ConstConfig;
import com.beautysalon.util.TokenManager;
import com.example.testgallary.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RegisterStepFirstActivity extends BaseActivity {
  private EditText etUserName = null;
  private EditText etPassword = null;
  private EditText etConfim = null;
  private Button btnNextStep = null;

  private Button btnRet = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.register_step_first);
    init();
    addEvn();
  }

  private void init() {
    etUserName = (EditText) findViewById(R.id.et_username);
    etPassword = (EditText) findViewById(R.id.et_password);
    etConfim = (EditText) findViewById(R.id.et_confim);
    btnNextStep = (Button) findViewById(R.id.btn_nextstep);
    btnRet = (Button) findViewById(R.id.btn_ret);
  }

  private void addEvn() {
    btnRet.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        finish();
      }
    });
    etUserName.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
          int after) {

      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
    etPassword.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
          int after) {

      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
    etConfim.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
          int after) {

      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });
    btnNextStep.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        if (etUserName.getText() == null
            || etUserName.getText().toString().isEmpty()) {
          Toast.makeText(getApplicationContext(), "用户名不能为空", Toast.LENGTH_LONG)
              .show();
          return;
        } else if (!CommUtils.isEmail(etUserName.getText().toString())) {
          Toast.makeText(getApplicationContext(), "用户名不是有效的邮箱",
              Toast.LENGTH_LONG).show();
          return;
        } else if (etPassword.getText() == null
            || etPassword.getText().toString().isEmpty()
            || etConfim.getText() == null
            || etConfim.getText().toString().isEmpty()) {
          Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_LONG)
              .show();
          return;
        } else if (!etPassword.getText().toString()
            .equals(etConfim.getText().toString())) {
          Toast.makeText(getApplicationContext(), "两次输入的密码不一致",
              Toast.LENGTH_LONG).show();
          return;
        } else if (etPassword.getText().toString().length() < 6) {
          Toast.makeText(getApplicationContext(), "密码长度不能小于6位",
              Toast.LENGTH_LONG).show();
          return;
        }
        AsyncHttpClient httpClient = new AsyncHttpClient();

        final RequestParams param = new RequestParams();
        // userPO.setEmail(etUserName.getText().toString());
        // userPO.setPassWord(etPassword.getText().toString());
        // userPO.setType(0);
        // userPO.setUserName("");
        // userPO.setRealName("");

        RegisterStepFirstActivity.this.showLoadingView("正在请求中...");
        param.put(ConstConfig.EMAIL, etUserName.getText().toString());
        param.put(ConstConfig.TYPE, 0);
        param.put(ConstConfig.REAL_NAME, etUserName.getText().toString());
        param.put(ConstConfig.USER_NAME, etUserName.getText().toString());
        // param.put(ConstConfig.PASSWORD,
        // CommUtils.string2MD5(etPassword.getText().toString()));
        param.put(ConstConfig.PASSWORD, etPassword.getText().toString());
        param.put(ConstConfig.PHONE, "");
        httpClient.post(CommUtils.getRegisterUrl(), param,
            new JsonHttpResponseHandler() {
              @Override
              public void onStart() {
                CustomLog.d(TAG, CommUtils.getRegisterUrl() + param);
                super.onStart();

              }

              @Override
              public void onSuccess(int statusCode, Header[] headers,
                  JSONObject response) {
                RegisterStepFirstActivity.this.removeLoadingView();
                // Handle resulting parsed JSON response here
                CustomLog.e(TAG, response.toString());
                // if (response.getInt(ConstConfig.RETURN_CODE) == 0) {
                if ((response.has("ResulMessage") && response
                    .optInt("ResulMessage") == 0)
                    || (response.has(ConstConfig.RETURN_CODE) && response
                        .optInt(ConstConfig.RETURN_CODE) == 0)) {
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
                  // TokenManager.getInstance().setToken(token);
                  TokenManager.getInstance().setUserId(userId);
                  TokenManager.getInstance().setShopid(userId);
                  TokenManager.getInstance().setToken(token);
                  Intent intent = new Intent();
                  intent.setClass(RegisterStepFirstActivity.this,
                      RegisterStepSecondActivity.class);
                  startActivity(intent);
                  finish();
                } else {
                  if (response.optInt(ConstConfig.RETURN_CODE) == -907) {
                    Toast.makeText(RegisterStepFirstActivity.this,
                        "用户名已注册，请更换用户名", Toast.LENGTH_LONG).show();
                  } else {
                    Toast.makeText(RegisterStepFirstActivity.this,
                        "无网络，请检查网络测试！", Toast.LENGTH_LONG).show();

                  }
                  CustomLog.e(TAG, "response=" + response.toString());
                }
              }

              @Override
              public void onFailure(int statusCode, Header[] headers,
                  String errorResponse, Throwable e) {
                RegisterStepFirstActivity.this.removeLoadingView();
                CustomLog.e(TAG, "statusCode=" + statusCode + "errorResponse="
                    + errorResponse.toString());
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
              }

              @Override
              public void onFailure(int statusCode, Header[] headers,
                  Throwable throwable, JSONObject response) {
                RegisterStepFirstActivity.this.removeLoadingView();
                CustomLog.e(TAG, "statusCode=" + statusCode + "response="
                    );
              }

            });

        // Intent intent = new Intent();
        // Bundle mBundle = new Bundle();

        // mBundle.putSerializable("user", userPO);
        // Intent intent = new Intent();
        // Bundle mBundle = new Bundle();
        //
        // mBundle.putSerializable("user", userPO);
        // intent.putExtras(mBundle);
        // intent.setClass(RegisterStepFirstActivity.this,
        // RegisterStepSecondActivity.class);
        // startActivity(intent);
        // RegisterStepFirstActivity.this.showLoadingView("正在加載中...",
        // new OnCancelListener() {
        //
        // @Override
        // public void onCancel(DialogInterface dialog) {
        // Log.e(TAG, "...");
        // Intent intent = new Intent();
        // Bundle mBundle = new Bundle();
        //
        // mBundle.putSerializable("user", userPO);
        // intent.putExtras(mBundle);
        // intent.setClass(RegisterStepFirstActivity.this,
        // RegisterStepSecondActivity.class);
        // startActivity(intent);
        // // finish();
        // }
        // });
        // Intent intent = new Intent();
        // Bundle mBundle = new Bundle();
        //
        // mBundle.putSerializable("user", userPO);
        // intent.putExtras(mBundle);
        // intent.setClass(RegisterStepFirstActivity.this,
        // RegisterStepSecondActivity.class);
        // startActivity(intent);
        // finish();

      }
    });
  }
}
