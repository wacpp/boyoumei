package com.beautysalon.order;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.redcdn.log.CustomLog;

import com.beautysalon.main.BaseActivity;
import com.beautysalon.order.OrderListAdapter.BtnCallBack;
import com.beautysalon.po.OrderPO;
import com.beautysalon.spiner.widget.AbstractSpinerAdapter;
import com.beautysalon.spiner.widget.CustemObject;
import com.beautysalon.spiner.widget.CustemSpinerAdapter;
import com.beautysalon.spiner.widget.SpinerPopWindow;
import com.beautysalon.util.CommUtils;
import com.beautysalon.util.ConstConfig;
import com.beautysalon.util.CustomDialogWithEditText;
import com.beautysalon.util.TokenManager;
import com.example.testgallary.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class OrderManagerActivity extends BaseActivity {

  private ListView orderListView;
  private List<OrderPO> orderList = new ArrayList<OrderPO>();

  private List<OrderPO> shopOrderList = new ArrayList<OrderPO>();

  private OrderListAdapter orderListAdapter;

  private Button btnRet = null;

  private List<OrderPO> initOrderList = new ArrayList<OrderPO>();

  private List<OrderPO> initServerOrderList = new ArrayList<OrderPO>();;

  private int sumPrice = 0;

  // 当前选择状态
  private boolean currentOrderStatus = false;

  // private int curStatus = 0;// 筛选所有

  private TextView tvServiceArea;
  private ImageButton mBtnServiceArea;
  private List<CustemObject> areaList = new ArrayList<CustemObject>();
  private AbstractSpinerAdapter<CustemObject> mAreaAdapter;

  private TextView tvOrderZero = null;

  private Button btnServerOrder = null;
  private Button btnOrder = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.order_manager);

    // 接口步好后需要去服务器获取信息，根据userId和token
    initOrderList = new ArrayList<OrderPO>();
    CustomLog.d(TAG, "11111111111111111111");
    initWidget();
    initShopOrder();
    OrderManagerActivity.this.showLoadingView("获取订单信息中...",
        new OnCancelListener() {

          @Override
          public void onCancel(DialogInterface dialog) {
            // TODO Auto-generated method stub
            OrderManagerActivity.this.removeLoadingView();
          }
        });
  }

  private void initShopOrder() {

    // OrderManagerActivity.this.showLoadingView(TAG, new OnCancelListener() {
    //
    // @Override
    // public void onCancel(DialogInterface dialog) {
    // // TODO Auto-generated method stub
    //
    // }
    // });
    CustomLog.d(TAG, "ok.....1");

    AsyncHttpClient httpClient = new AsyncHttpClient();

    RequestParams param = new RequestParams();
    param.put(ConstConfig.TOKEN, TokenManager.getInstance().getToken());
    // param.put("shop.shopid", TokenManager.getInstance().getUserId());
    param.put("userid", TokenManager.getInstance().getUserId());

    // param.put("number", 0);
    // param.put(ConstConfig.USER_ID,
    // TokenManager.getInstance().getUserId());

    httpClient.post(CommUtils.getShopOrdInfoUrl(), param,
        new JsonHttpResponseHandler() {
          @Override
          public void onStart() {
            super.onStart();

          }

          @Override
          public void onSuccess(int statusCode, Header[] headers,
              JSONObject response) {
            // OrderManagerActivity.this.removeLoadingView();
            CustomLog.d(TAG, "response=" + response.toString());

            // Handle resulting parsed JSON response here
            try {
              if (response.getInt(ConstConfig.RETURN_CODE) == 0) {
                CustomLog.e("请求成功。。。。", response.toString());

                JSONArray ja = new JSONArray();

                if (response.optString("IfExist").equals("yes")
                    && response.has("ordinfo")) {

                  ja = response.optJSONArray("ordinfo");

                  if (ja.length() > 0) {
                    for (int i = 0; i < ja.length(); i++) {
                      CustomLog.d(TAG, "ja.length" + ja.length());
                      OrderPO orderPOTemp = new OrderPO();
                      orderPOTemp.setCreateTime(((JSONObject) ja.get(i))
                          .optString("createtime"));
                      orderPOTemp.setOrderId(((JSONObject) ja.get(i))
                          .optString("orderid"));
                      // State 0 yiguanbi 1yiwancheng2yuyuezhong 3yituiding
                      // 1 预约中, 2 已完成, 3 已关闭, 4 已退订
                      int state = ((JSONObject) ja.get(i)).optInt("state");
                      if (state == 0) {
                        orderPOTemp.setState(3);
                      } else if (state == 1) {
                        orderPOTemp.setState(2);
                      } else if (state == 2) {
                        orderPOTemp.setState(1);
                      } else {
                        orderPOTemp.setState(4);
                      }
                      orderPOTemp.setSumPrice(((JSONObject) ja.get(i))
                          .optInt("sumprice"));

                      if (((JSONObject) ja.get(i)).has("services")) {

                        orderPOTemp
                            .setServiceName(((JSONObject) ((JSONArray) ((JSONObject) ja
                                .get(i)).optJSONArray("services")).get(0))
                                .optString("servicename"));
                        orderPOTemp
                            .setServiceId(((JSONObject) ((JSONArray) ((JSONObject) ja
                                .get(0)).optJSONArray("services")).get(0))
                                .optString("serviceid "));
                      }
                      orderPOTemp.setUserContact(((JSONObject) ja.get(i))
                          .optString("ordphone"));
                      orderPOTemp.setUserName(((JSONObject) ja.get(i))
                          .optString("username"));

                      shopOrderList.add(orderPOTemp);
                      initServerOrderList.add(orderPOTemp);
                      // orderPOTem
                      CustomLog.d(TAG, "193................");

                    }
                    initData();
                  }
                } else {
                  CustomLog.d(TAG, "198................");
                  initData();
                }
              } else {
                // OrderManagerActivity.this.removeLoadingView();
              }
            } catch (JSONException e) {
              // TODO Auto-generated catch block
              // e.printStackTrace();
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
            // OrderManagerActivity.this.removeLoadingView();
            CustomLog.d(TAG, "221................");
            initData();
            CustomLog.e("请求失敗。。。。", errorResponse.toString());
          }

          @Override
          public void onFailure(int statusCode, Header[] headers,
              String errorResponse, Throwable e) {
            CustomLog.d(TAG, "229................");
            initData();
            // OrderManagerActivity.this.removeLoadingView();
            CustomLog.e(TAG, "statusCode=" + statusCode + "errorResponse="
                + errorResponse.toString());
            // called when response HTTP status is "4XX" (eg. 401, 403,
            // 404)
          }

        });

  }

  private Handler mHandler = new Handler() {
    public void handleMessage(Message msg) {

      if (orderList.size() > 0) {
        initAdapter(orderList);
      }

      setupViews();
      // orderList
      // orderList.removeAll(orderList);
      if (orderList.size() == 0) {
        tvOrderZero.setVisibility(View.VISIBLE);
        orderListView.setVisibility(View.INVISIBLE);
      } else {
        tvOrderZero.setVisibility(View.INVISIBLE);
        orderListView.setVisibility(View.VISIBLE);
      }
    }
  };

  OnClickListener btnClickListener = new OnClickListener() {

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
      case R.id.bt_service_type:
        showAreaSpinWindow();
        break;
      case R.id.btn_ret:
        finish();
        break;
      default:
        break;
      }
    }

  };

  private void setupViews() {

    tvServiceArea = (TextView) findViewById(R.id.tv_service_type);
    mBtnServiceArea = (ImageButton) findViewById(R.id.bt_service_type);
    mBtnServiceArea.setOnClickListener(btnClickListener);
    areaList.removeAll(areaList);
    String[] areas = { "全部", "预约中", "已完成", "已关闭" };
    for (int i = 0; i < areas.length; i++) {
      CustemObject object = new CustemObject();
      object.data = areas[i];
      areaList.add(object);

    }

    mAreaAdapter = new CustemSpinerAdapter(this);
    mAreaAdapter.refreshData(areaList, 0);

    mAreaSpinerPopWindow = new SpinerPopWindow(this);
    mAreaSpinerPopWindow.setAdatper(mAreaAdapter);
    mAreaSpinerPopWindow.setItemListener(areaListener);

  }

  AbstractSpinerAdapter.IOnItemSelectListener areaListener = new AbstractSpinerAdapter.IOnItemSelectListener() {

    @Override
    public void onItemClick(int pos) {
      setAreaHero(pos);
      // curStatus = pos;

      if (currentOrderStatus) {
        CustomLog.d(TAG, "pos=" + pos + "orderList.size=" + orderList.size()
            + "initOrderList.size=" + initOrderList.size());
        orderList.removeAll(orderList);

        for (int i = 0; i < initOrderList.size(); i++) {
          CustomLog.d(TAG, "initOrderList.get(i).getState()="
              + initOrderList.get(i).getState() + "pos=" + pos);
          if (initOrderList.get(i).getState() == pos || pos == 0) {

            orderList.add(initOrderList.get(i));
          }
        }
        // orderListAdapter.setCurStatus(pos);
        CustomLog.d(TAG, "pos=" + pos + "orderList.size=" + orderList.size()
            + "initOrderList.size=" + initOrderList.size());
        // orderListAdapter.notifyDataSetChanged();
        // orderListAdapter.refresh(orderList);

        orderListAdapter = null;
        orderListAdapter = new OrderListAdapter(orderList,
            OrderManagerActivity.this, bCallback);
        orderListView.setAdapter(orderListAdapter);

      } else {
        CustomLog.d(TAG, "pos=" + pos + "orderList.size=" + orderList.size()
            + "initServerOrderList.size=" + initServerOrderList.size());
        shopOrderList.removeAll(shopOrderList);

        for (int i = 0; i < initServerOrderList.size(); i++) {
          CustomLog.d(TAG, "initServerOrderList.get(i).getState()="
              + initServerOrderList.get(i).getState() + "pos=" + pos);
          if (initServerOrderList.get(i).getState() == pos || pos == 0) {

            shopOrderList.add(initServerOrderList.get(i));
          }
        }
        // orderListAdapter.setCurStatus(pos);
        CustomLog.d(TAG,
            "pos=" + pos + "shopOrderList.size=" + shopOrderList.size()
                + "shopOrderList.size=" + shopOrderList.size());
        // orderListAdapter.notifyDataSetChanged();
        // orderListAdapter.refresh(orderList);

        orderListAdapter = null;
        orderListAdapter = new OrderListAdapter(shopOrderList,
            OrderManagerActivity.this, bCallback);
        orderListView.setAdapter(orderListAdapter);

      }

    }
  };

  private void setAreaHero(int pos) {
    if (pos >= 0 && pos <= areaList.size()) {
      CustemObject value = areaList.get(pos);

      tvServiceArea.setText(value.toString());
    }
  }

  private SpinerPopWindow mAreaSpinerPopWindow;

  private void showAreaSpinWindow() {
    CustomLog.e("", "showSpinWindow");
    mAreaSpinerPopWindow.setWidth(tvServiceArea.getWidth());
    mAreaSpinerPopWindow.showAsDropDown(tvServiceArea);

  }

  private void initWidget() {
    btnRet = (Button) findViewById(R.id.btn_ret);
    btnRet.setOnClickListener(btnClickListener);
    orderListView = (ListView) findViewById(R.id.lv_info);
    tvOrderZero = (TextView) findViewById(R.id.tv_order_zero);

    btnServerOrder = (Button) findViewById(R.id.btn_server_order);
    btnOrder = (Button) findViewById(R.id.btn_order);

    // btnServerOrder.setFocusable(true);
    // btnServerOrder.setFocusableInTouchMode(true);
    btnOrder.setTextColor(getResources().getColorStateList(R.color.white));
    btnOrder.setBackgroundColor(getResources().getColor(R.color.button_select));
    btnServerOrder.setTextColor(getResources().getColorStateList(
        R.color.button_select));
    btnServerOrder.setBackgroundColor(getResources().getColor(R.color.white));
    btnServerOrder.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub

        if (orderListAdapter != null) {
          orderListAdapter.setOrderMode(false);
        }
        btnServerOrder.setTextColor(getResources().getColorStateList(
            R.color.white));
        btnServerOrder.setBackgroundColor(getResources().getColor(
            R.color.button_select));
        btnOrder.setTextColor(getResources().getColorStateList(
            R.color.button_select));
        btnOrder.setBackgroundColor(getResources().getColor(R.color.white));
        CustomLog.d(TAG, "btnServerOrder ....");
        currentOrderStatus = false;
        orderListAdapter = null;
        // orderListAdapter = new OrderListAdapter(shopOrderList,
        // OrderManagerActivity.this, bCallback);
        // orderListView.setAdapter(orderListAdapter);
        if (shopOrderList.size() > 0) {
          initAdapter(shopOrderList);
        } else {
          if (orderList.size() == 0) {
            tvOrderZero.setVisibility(View.VISIBLE);
            orderListView.setVisibility(View.INVISIBLE);
          } else {
            tvOrderZero.setVisibility(View.INVISIBLE);
            orderListView.setVisibility(View.VISIBLE);
          }
        }
      }
    });

    btnOrder.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub

        if (orderListAdapter != null) {
          orderListAdapter.setOrderMode(true);
        }
        btnOrder.setTextColor(getResources().getColorStateList(R.color.white));
        btnOrder.setBackgroundColor(getResources().getColor(
            R.color.button_select));
        btnServerOrder.setTextColor(getResources().getColorStateList(
            R.color.button_select));
        btnServerOrder.setBackgroundColor(getResources()
            .getColor(R.color.white));

        CustomLog.d(TAG, "btnOrder ....");
        currentOrderStatus = true;
        orderListAdapter = null;
        // orderListAdapter = new OrderListAdapter(orderList,
        // OrderManagerActivity.this, bCallback);
        // orderListView.setAdapter(orderListAdapter);
        if (orderList.size() > 0) {
          initAdapter(orderList);
        } else {
          if (orderList.size() == 0) {
            tvOrderZero.setVisibility(View.VISIBLE);
            orderListView.setVisibility(View.INVISIBLE);
          } else {
            tvOrderZero.setVisibility(View.INVISIBLE);
            orderListView.setVisibility(View.VISIBLE);
          }
        }
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    // initData();
  }

  private void initData() {
    OrderManagerActivity.this.showLoadingView(TAG, new OnCancelListener() {

      @Override
      public void onCancel(DialogInterface dialog) {
        // TODO Auto-generated method stub

      }
    });
    CustomLog.d(TAG, "ok.....initData");

    AsyncHttpClient httpClient = new AsyncHttpClient();

    final RequestParams param = new RequestParams();
    param.put(ConstConfig.TOKEN, TokenManager.getInstance().getToken());
    // param.put("shop.shopid", TokenManager.getInstance().getUserId());
    param.put("userid", TokenManager.getInstance().getUserId());

    param.put("number", 0);
    // param.put(ConstConfig.USER_ID,
    // TokenManager.getInstance().getUserId());

    httpClient.post(CommUtils.getOrdersInfoUrl(), param,
        new JsonHttpResponseHandler() {
          @Override
          public void onStart() {
            super.onStart();

          }

          @Override
          public void onSuccess(int statusCode, Header[] headers,
              JSONObject response) {
            OrderManagerActivity.this.removeLoadingView();
            CustomLog.d(TAG,
                "response=111111111111111111111111111" + response.toString());

            // Handle resulting parsed JSON response here
            try {
              if (response.getInt(ConstConfig.RETURN_CODE) == 0) {
                CustomLog.e("请求成功。。。。", response.toString());

                JSONArray ja = new JSONArray();

                if (response.has("orders")) {

                  ja = response.optJSONArray("orders");

                  if (ja.length() > 0) {
                    for (int i = 0; i < ja.length(); i++) {
                      OrderPO orderPOTemp = new OrderPO();
                      orderPOTemp.setCreateTime(((JSONObject) ja.get(i))
                          .optString("createtime"));
                      orderPOTemp.setOrderId(((JSONObject) ja.get(i))
                          .optString("orderid"));
                      // State 0 yiguanbi 1yiwancheng2yuyuezhong 3yituiding
                      // 1 预约中, 2 已完成, 3 已关闭, 4 已退订
                      int state = ((JSONObject) ja.get(i)).optInt("state");
                      if (state == 0) {
                        orderPOTemp.setState(3);
                      } else if (state == 1) {
                        orderPOTemp.setState(2);
                      } else if (state == 2) {
                        orderPOTemp.setState(1);
                      } else {
                        orderPOTemp.setState(4);
                      }
                      orderPOTemp.setSumPrice(((JSONObject) ja.get(i))
                          .optInt("sumprice"));
                      orderPOTemp
                          .setServiceName(((JSONObject) ((JSONArray) ((JSONObject) ja
                              .get(i)).optJSONArray("services")).get(0))
                              .optString("servicename"));
                      orderPOTemp.setUserContact(((JSONObject) ja.get(i))
                          .optString("ordphone"));
                      orderPOTemp.setUserName(((JSONObject) ja.get(i))
                          .optString("username"));
                      orderPOTemp
                          .setServiceId(((JSONObject) ((JSONArray) ((JSONObject) ja
                              .get(0)).optJSONArray("services")).get(0))
                              .optString("serviceid "));
                      orderList.add(orderPOTemp);
                      initOrderList.add(orderPOTemp);
                      CustomLog.d(TAG, "orderList.." + orderList.size());
                      // orderPOTem
                      mHandler.sendEmptyMessage(0);
                    }
                  }
                } else {
                  mHandler.sendEmptyMessage(0);
                }
              } else {
                OrderManagerActivity.this.removeLoadingView();
              }
            } catch (JSONException e) {
              // TODO Auto-generated catch block
              // e.printStackTrace();
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
            OrderManagerActivity.this.removeLoadingView();
            mHandler.sendEmptyMessage(0);
            CustomLog.e("请求失敗。。。。", errorResponse.toString());
          }

          @Override
          public void onFailure(int statusCode, Header[] headers,
              String errorResponse, Throwable e) {
            mHandler.sendEmptyMessage(0);
            OrderManagerActivity.this.removeLoadingView();
            CustomLog.e(TAG, "statusCode=" + statusCode + "errorResponse="
                + errorResponse.toString());
            // called when response HTTP status is "4XX" (eg. 401, 403,
            // 404)
          }

        });
    // if (orderList.size() == 0) {
    // for (int i = 0; i < 10; i++) {
    // OrderPO orderPo = new OrderPO();
    // orderPo.setOrderId("201500000" + i);
    // orderPo.setServiceName("helll" + i);
    // orderPo.setUserName("hello" + i);
    // orderPo.setUserContact("1233333335" + i);
    // orderPo.setSumPrice(90 + i);
    // if (i % 2 == 0) {
    // orderPo.setState(1);
    // } else {
    // orderPo.setState(2);
    // }
    // orderList.add(orderPo);
    // initOrderList.add(orderPo);
    // }
    // }
    // initOrderList= orderList;
  }

  private void initAdapter(List<OrderPO> orderList) {
    orderListAdapter = new OrderListAdapter(orderList,
        OrderManagerActivity.this, bCallback);
    orderListView.setAdapter(orderListAdapter);

    orderListView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
        // TODO Auto-generated method stub
        CustomLog.e("orderListView", "position=" + position);
      }
    });
    orderListView.setOnItemLongClickListener(new OnItemLongClickListener() {

      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view,
          int position, long id) {
        // TODO Auto-generated method stub

        CustomLog.e("orderListView longclick", "position=" + position);

        return false;
      }
    });
  }

  private BtnCallBack bCallback = new BtnCallBack() {

    @Override
    public void btnclick(int status, final String id) {
      // TODO Auto-generated method stub
      CustomLog.e("BtnCallBack", "status=" + status + "id=" + id);
      if (status == 1) {
        addContact(id);
      } else {
        // 关闭订单
        AsyncHttpClient httpClient = new AsyncHttpClient();

        final RequestParams param = new RequestParams();
        param.put(ConstConfig.TOKEN, TokenManager.getInstance().getToken());
        param.put("order.orderid", id);
        param.put("optype", 0);// opttype1完成0關閉
        param.put("realprice", sumPrice);

        // param.put(ConstConfig.USER_ID,
        // TokenManager.getInstance().getUserId());

        httpClient.post(CommUtils.getEditOrderUrl(), param,
            new JsonHttpResponseHandler() {
              @Override
              public void onStart() {
                CustomLog.d(TAG, "response=" + param.toString());
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
                    OrderManagerActivity.this.removeLoadingView();
                    CustomLog.e("addDialog", sumPrice + "...");
                    for (int i = 0; i < orderList.size(); i++) {
                      if (orderList.get(i).getOrderId().equals(id)) {
                        orderList.get(i).setState(3);
                        // orderList.get(i).setSumPrice(sumPrice);

                        // addDi alog.dismiss();

                        break;
                      }
                    }
                    // addDialog.dismiss();
                    // orderListAdapter.notifyDataSetChanged();
                    for (int i = 0; i < orderList.size(); i++) {
                      CustomLog.d(TAG, "orderList.get(i)="
                          + orderList.get(i).toString());
                    }
                    orderListAdapter.refresh(orderList);
                  } else {
                    OrderManagerActivity.this.removeLoadingView();
                  }
                } catch (JSONException e) {
                  // TODO Auto-generated catch block
                  // e.printStackTrace();
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
                OrderManagerActivity.this.removeLoadingView();
                CustomLog.e("请求失敗。。。。", errorResponse.toString());
              }

              @Override
              public void onFailure(int statusCode, Header[] headers,
                  String errorResponse, Throwable e) {
                OrderManagerActivity.this.removeLoadingView();
                CustomLog.e(TAG, "statusCode=" + statusCode + "errorResponse="
                    + errorResponse.toString());
                // called when response HTTP status is "4XX" (eg. 401, 403,
                // 404)
              }

            });

      }
    }
  };

  private void addContact(final String id) {
    final CustomDialogWithEditText addDialog = new CustomDialogWithEditText(
        OrderManagerActivity.this);
    addDialog.setTip("完成订单");
    addDialog.setCancelBtnText("取消");
    addDialog.setOkBtnText("完成");
    addDialog.setInputName("请输入实际金额 ");
    addDialog
        .setInputOnClickListener(new CustomDialogWithEditText.InputOnClickListener() {

          @Override
          public void onClick(CustomDialogWithEditText customDialog) {
          }
        });
    addDialog
        .setInputTextChangedListenerListener(new CustomDialogWithEditText.InputTextChangedListenerListener() {

          @Override
          public void onTextChangedListener(
              CustomDialogWithEditText customDialog, EditText et, TextView tv) {
            if (et != null && !et.getText().toString().isEmpty()) {
              sumPrice = Integer.parseInt(et.getText().toString());
            }
          }
        });
    addDialog
        .setCancelBtnOnClickListener(new CustomDialogWithEditText.CancelBtnOnClickListener() {

          @Override
          public void onClick(CustomDialogWithEditText customDialog) {
            addDialog.dismiss();
            return;
          }

        });
    addDialog
        .setOkBtnOnClickListener(new CustomDialogWithEditText.OKBtnOnClickListener() {
          @Override
          public void onClick(CustomDialogWithEditText customDialog, TextView tv) {
            // if (tv == null || tv.getText().toString().isEmpty()) {
            // return;
            // }
            AsyncHttpClient httpClient = new AsyncHttpClient();

            final RequestParams param = new RequestParams();
            param.put(ConstConfig.TOKEN, TokenManager.getInstance().getToken());
            param.put("order.orderid", id);
            param.put("optype", 1);// opttype1完成0關閉
            param.put("realprice", sumPrice * 100);

            // param.put(ConstConfig.USER_ID,
            // TokenManager.getInstance().getUserId());

            httpClient.post(CommUtils.getEditOrderUrl(), param,
                new JsonHttpResponseHandler() {
                  @Override
                  public void onStart() {
                    CustomLog.d(TAG, "response=" + param.toString());
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
                        OrderManagerActivity.this.removeLoadingView();
                        CustomLog.e("addDialog", sumPrice + "...");
                        for (int i = 0; i < orderList.size(); i++) {
                          if (orderList.get(i).getOrderId().equals(id)) {
                            orderList.get(i).setState(2);
                            orderList.get(i).setSumPrice(sumPrice);

                            addDialog.dismiss();

                            break;
                          }
                        }
                        addDialog.dismiss();
                        // orderListAdapter.notifyDataSetChanged();
                        for (int i = 0; i < orderList.size(); i++) {
                          CustomLog
                              .d(TAG, "orderList.get(i)="
                                  + orderList.get(i).toString());
                        }
                        orderListAdapter.refresh(orderList);
                      } else {
                        OrderManagerActivity.this.removeLoadingView();
                      }
                    } catch (JSONException e) {
                      // TODO Auto-generated catch block
                      // e.printStackTrace();
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
                    OrderManagerActivity.this.removeLoadingView();
                    CustomLog.e("请求失敗。。。。", errorResponse.toString());
                  }

                  @Override
                  public void onFailure(int statusCode, Header[] headers,
                      String errorResponse, Throwable e) {
                    OrderManagerActivity.this.removeLoadingView();
                    CustomLog.e(TAG, "statusCode=" + statusCode
                        + "errorResponse=" + errorResponse.toString());
                    // called when response HTTP status is "4XX" (eg. 401, 403,
                    // 404)
                  }

                });

            // CustomLog.e("addDialog", sumPrice + "...");
            // for (int i = 0; i < orderList.size(); i++) {
            // if (orderList.get(i).getOrderId().equals(id)) {
            // orderList.get(i).setState(2);
            // orderList.get(i).setSumPrice(sumPrice);
            //
            // addDialog.dismiss();
            //
            // break;
            // }
            // }
            // addDialog.dismiss();
            // // orderListAdapter.notifyDataSetChanged();
            // for (int i = 0; i < orderList.size(); i++) {
            // CustomLog.d(TAG, "orderList.get(i)="
            // + orderList.get(i).toString());
            // }
            // orderListAdapter.refresh(orderList);
          }
        });
    addDialog.setCancelable(true);
    addDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
    addDialog.show();

  }

}
