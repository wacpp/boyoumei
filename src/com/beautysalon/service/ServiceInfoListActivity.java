package com.beautysalon.service;

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
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.redcdn.log.CustomLog;

import com.beautysalon.main.BaseActivity;
import com.beautysalon.po.ProductServicePO;
import com.beautysalon.service.ServiceInfoListAdapter.BtnCallBack;
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

public class ServiceInfoListActivity extends BaseActivity {

  private ListView serviceInfoListView;
  private List<ProductServicePO> mProductServiceList = null;
  private ServiceInfoListAdapter serviceInfoListAdapter;

  private Button btnRet = null;
  private Button btnServiceInfo = null;

  private TextView tvServiceZero = null;

  // private int sumPrice = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.service_info);

    ServiceInfoListActivity.this.showLoadingView("上传服务中...",
        new OnCancelListener() {

          @Override
          public void onCancel(DialogInterface dialog) {
            // TODO Auto-generated method stub
            ServiceInfoListActivity.this.removeLoadingView();
          }
        });
    // 接口步好后需要去服务器获取信息，根据userId和token
    mProductServiceList = new ArrayList<ProductServicePO>();
    initWidget();
    initAdapter();
    initData();

  }

  private Handler mHandler = new Handler() {
    public void handleMessage(Message msg) {
      CustomLog.d(TAG, "刷新UI");
      for (int i = 0; i < mProductServiceList.size(); i++) {
        CustomLog.e(TAG, mProductServiceList.get(i).toString());
      }
      serviceInfoListAdapter.notifyDataSetChanged();
      //
      // mProductServiceList.removeAll(mProductServiceList)
      if (mProductServiceList.size() == 0) {
        CustomLog.d(TAG, "mProductServiceList visable...size="
            + mProductServiceList.size());
        tvServiceZero.setVisibility(View.VISIBLE);
        serviceInfoListView.setVisibility(View.INVISIBLE);
      } else {
        CustomLog.d(TAG, "mProductServiceList invible...size="
            + mProductServiceList.size());
        tvServiceZero.setVisibility(View.INVISIBLE);
        serviceInfoListView.setVisibility(View.VISIBLE);
      }
    }

  };

  private void initWidget() {
    tvServiceZero = (TextView) findViewById(R.id.tv_service_zero);
    btnRet = (Button) findViewById(R.id.btn_ret);
    btnServiceInfo = (Button) findViewById(R.id.btn_service_info);
    serviceInfoListView = (ListView) findViewById(R.id.service_lv_info);
    btnServiceInfo.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(ServiceInfoListActivity.this,
            PublishServiceActivity.class);
        startActivity(intent);
        finish();
      }
    });
    btnRet.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        finish();
      }
    });
  }

  private void initData() {
    // orderList = new ArrayList<OrderPO>();
    // for (int i = 0; i < 10; i++) {
    // OrderPO orderPo = new OrderPO();
    // orderPo.setOrderId("201500000" + i);
    // orderPo.setServiceName("helll" + i);
    // orderPo.setUserName("hello" + i);
    // orderPo.setUserContact("1233333335" + i);
    // orderPo.setSumPrice(90 + i);
    // if (i % 2 == 0) {
    // orderPo.setState(0);
    // } else {
    // orderPo.setState(1);
    // }
    // orderList.add(orderPo);
    // }
    // 获取数据

    AsyncHttpClient httpClient = new AsyncHttpClient();

    RequestParams param = new RequestParams();
    param.put(ConstConfig.TOKEN, TokenManager.getInstance().getToken());
    param.put(ConstConfig.USER_ID, TokenManager.getInstance().getUserId());
    // param.put("shop.shopid", 54);
    param.put("number", 0);
    CustomLog.d(TAG, param.toString());
    httpClient.post(CommUtils.getServsInfoUrl(), param,
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
                    ServiceInfoListActivity.this.removeLoadingView();
                    // if()
                    if (!response.optString("IfExist").equals("yes")) {
                      mHandler.sendEmptyMessage(0);
                      return;
                    }
                    JSONArray ja1 = new JSONArray();
                    ja1 = response.optJSONArray("services");
                    CustomLog.d(TAG, "ja1=" + ja1.length());
                    if (ja1.length() == 0) {
                      mHandler.sendEmptyMessage(0);
                    } else {
                      JSONArray ja2 = new JSONArray();
                      JSONArray jaType = new JSONArray();
                      for (int i = 0; i < ja1.length(); i++) {
                        ProductServicePO mProductServicePO = new ProductServicePO();
                        mProductServicePO.setServiceId(((JSONObject) (ja1
                            .get(i))).optString("serviceid"));
                        mProductServicePO.setServiceName(((JSONObject) (ja1
                            .get(i))).optString("servicename"));
                        mProductServicePO.setPrice(Integer
                            .parseInt(((JSONObject) (ja1.get(i)))
                                .optString("price")));
                        mProductServicePO.setServiceDec(((JSONObject) (ja1
                            .get(i))).optString("servicedec"));
                        mProductServicePO.setCreateDateTime(((JSONObject) (ja1
                            .get(i))).optString("createtime"));
                        mProductServicePO.setProductId(((JSONObject) (ja1
                            .get(i))).optString("productid"));

                        mProductServicePO.setProductName(((JSONObject) (ja1
                            .get(i))).optString("productname"));

                        if (((JSONObject) (ja1.get(i))).has("types")) {
                          jaType = ((JSONObject) (ja1.get(i)))
                              .optJSONArray("types");
                        }
                        ja2 = ((JSONObject) (ja1.get(i)))
                            .optJSONArray("servimgs");
                        mProductServicePO.setFile(((JSONObject) (ja2.get(0)))
                            .optString("url"));
                        List<String> typeId = new ArrayList<String>();
                        List<String> typeName = new ArrayList<String>();
                        if (jaType == null || jaType.length() == 0) {

                        } else {
                          for (int j = 0; j < jaType.length(); j++) {
                            String idProdType = ((JSONObject) (jaType.get(j)))
                                .optString("prodtypeid");
                            String idProdName = ((JSONObject) (jaType.get(j)))
                                .optString("prodtypename");
                            CustomLog.d(TAG, "idProdType..." + idProdType
                                + "idProdName=" + idProdName);
                            typeId.add(idProdType);
                            typeName.add(idProdName);
                            idProdType = null;
                            idProdName = null;

                          }

                          mProductServicePO.setTypeId(typeId);
                          mProductServicePO.setTypeName(typeName);
                          mProductServiceList.add(mProductServicePO);
                        }

                        CustomLog.d(TAG, "mProductServiceList.get(i)="
                            + mProductServiceList.get(i).toString()
                            + "mProductServicePO=" + mProductServicePO);
                        mProductServicePO = null;

                      }
                      ServiceInfoListActivity.this.removeLoadingView();
                      mHandler.sendEmptyMessage(0);
                    }
                  }
                } catch (JSONException e) {
                  ServiceInfoListActivity.this.removeLoadingView();
                  // TODO Auto-generated catch block
                  CustomLog.e(TAG, e.toString());
                }
              }
            } catch (JSONException e) {
              ServiceInfoListActivity.this.removeLoadingView();
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
            ServiceInfoListActivity.this.removeLoadingView();
            if (errorResponse != null) {
              CustomLog.e("请求失敗。。。。", errorResponse.toString());
            }
            CustomLog.e("请求失敗。。。。", "statusCode=" + statusCode);
          }

          @Override
          public void onFailure(int statusCode, Header[] headers,
              String errorResponse, Throwable e) {
            ServiceInfoListActivity.this.removeLoadingView();
            CustomLog.e(TAG, "statusCode=" + statusCode + "errorResponse="
                + errorResponse.toString());
            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
          }

        });

  }

  private void initAdapter() {
    serviceInfoListAdapter = new ServiceInfoListAdapter(mProductServiceList,
        ServiceInfoListActivity.this, bCallback);
    serviceInfoListView.setAdapter(serviceInfoListAdapter);

    serviceInfoListView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view,
          final int position, long id) {
        CustomLog.d(TAG, "itemclick....");

        final CustomDialog cd = new CustomDialog(ServiceInfoListActivity.this);
        cd.setTip("是否确认删除！");
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
            ServiceInfoListActivity.this.showLoadingView("删除服务中...",
                new OnCancelListener() {

                  @Override
                  public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    ServiceInfoListActivity.this.removeLoadingView();
                  }
                });
            CustomLog.e("orderListView", "position=" + position);
            AsyncHttpClient httpClient = new AsyncHttpClient();
            RequestParams param = new RequestParams();
            param.put(ConstConfig.TOKEN, TokenManager.getInstance().getToken());
            param.put("service.serviceid", mProductServiceList.get(position)
                .getServiceId());
            // param.put("shop.shopid", 54);
            // param.put("number", 0);
            CustomLog.d(TAG, param.toString());
            httpClient.post(CommUtils.getDeleteServiceUrl(), param,
                new JsonHttpResponseHandler() {
                  @Override
                  public void onStart() {
                    super.onStart();

                  }

                  @Override
                  public void onSuccess(int statusCode, Header[] headers,
                      JSONObject response) {
                    ServiceInfoListActivity.this.removeLoadingView();
                    CustomLog.d(TAG, "response=" + response.toString());

                    // Handle resulting parsed JSON response here
                    try {
                      if (response.getInt(ConstConfig.RETURN_CODE) == 0) {
                        mProductServiceList.remove(position);
                        serviceInfoListAdapter.notifyDataSetChanged();
                      } else {
                        if (response.getInt(ConstConfig.RETURN_CODE) == -904) {
                          Toast.makeText(ServiceInfoListActivity.this,
                              "有订单订购此服务不能删除", Toast.LENGTH_LONG).show();
                        } else {
                          Toast.makeText(ServiceInfoListActivity.this,
                              "网络异常，删除失败，请重试！", Toast.LENGTH_LONG).show();
                        }
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
                    ServiceInfoListActivity.this.removeLoadingView();
                    CustomLog.e("请求失敗。。。。", errorResponse.toString());
                  }

                  @Override
                  public void onFailure(int statusCode, Header[] headers,
                      String errorResponse, Throwable e) {
                    ServiceInfoListActivity.this.removeLoadingView();
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

        // TODO Auto-generated method stub
      }
    });

  }

  private BtnCallBack bCallback = new BtnCallBack() {

    @Override
    public void btnclick(int status, int id) {
      // TODO Auto-generated method stub
      CustomLog.e("BtnCallBack", "status=" + status + "id=" + id);
      // if (status == 1) {
      // // addContact(id);
      // }
      // TODO跳转到编辑页面
      Intent intent = new Intent();
      Bundle mbundle = new Bundle();
      CustomLog.d(TAG, "mProductServiceList.get(id)="
          + mProductServiceList.get(id).toString());
      mbundle.putSerializable("service", mProductServiceList.get(id));
      // intent.putExtra("serviceId", id);
      intent.setClass(ServiceInfoListActivity.this,
          PublishServiceActivity.class);
      intent.putExtras(mbundle);
      startActivity(intent);
      finish();
    }
  };

}
