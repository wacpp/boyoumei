package com.beautysalon.order;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.redcdn.log.CustomLog;

import com.beautysalon.po.OrderPO;
import com.example.testgallary.R;

public class OrderListAdapter extends BaseAdapter {
  private List<OrderPO> orderList = null;
  private Context context;
  private String TAG = OrderListAdapter.class.getName();
  private BtnCallBack bCallback;

  private boolean isOrderMode = true;

  public boolean isOrderMode() {
    return isOrderMode;
  }

  public void setOrderMode(boolean isOrderMode) {
    this.isOrderMode = isOrderMode;
  }

  private int curStatus = 0;// 当前显示是全部还是只显示预约中等

  public OrderListAdapter(List<OrderPO> OrderList, Context c,
      BtnCallBack btnCallBack) {
    orderList = OrderList;
    context = c;
    bCallback = btnCallBack;
  }

  @Override
  public int getCount() {

    return orderList.size();
  }

  @Override
  public Object getItem(int arg0) {
    // TODO Auto-generated method stub
    return orderList.get(arg0);
  }

  @Override
  public long getItemId(int arg0) {

    return arg0;
  }

  public void refresh(List<OrderPO> list) {
    orderList = list;
    notifyDataSetChanged();
  }

  @Override
  public View getView(final int position, View view, ViewGroup arg2) {
    ViewHolder holder = null;

    final OrderPO item = (OrderPO) getItem(position);
    CustomLog.e(TAG, "getView...." + item.getState());
    if (view == null) {
      holder = new ViewHolder();
      view = LayoutInflater.from(context).inflate(R.layout.order_manager_item,
          null);
      holder.llOrderItem = (LinearLayout) view.findViewById(R.id.ll_order_item);
      holder.tvOrderNumber = (TextView) view.findViewById(R.id.tv_order_number);
      holder.tvOrderStatus = (TextView) view.findViewById(R.id.tv_order_status);
      holder.tvServiceName = (TextView) view.findViewById(R.id.tv_service_name);
      holder.tvPersonName = (TextView) view.findViewById(R.id.tv_person_name);
      holder.tvTelephone = (TextView) view.findViewById(R.id.tv_telephone);
      holder.btnOrderShutDown = (Button) view
          .findViewById(R.id.btn_order_shut_down);
      holder.btnOrderShutFinish = (Button) view
          .findViewById(R.id.btn_order_shut_finish);
      holder.tvMoney = (TextView) view.findViewById(R.id.tv_money);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }

    holder.tvOrderNumber.setText("订单号：" + item.getOrderId());
    if (item.getState() == 1) {
      holder.tvOrderStatus.setText("预约中");
      holder.btnOrderShutDown.setVisibility(View.VISIBLE);
      holder.btnOrderShutFinish.setVisibility(View.VISIBLE);
      holder.tvMoney.setVisibility(View.INVISIBLE);
    } else if (item.getState() == 2) {
      holder.btnOrderShutDown.setVisibility(View.INVISIBLE);
      holder.btnOrderShutFinish.setVisibility(View.INVISIBLE);
      holder.tvOrderStatus.setText("已完成");
      holder.tvMoney.setText(item.getSumPrice() + "");
      holder.tvMoney.setVisibility(View.VISIBLE);
    } else if (item.getState() == 3) {
      holder.tvOrderStatus.setText("已关闭");
      holder.btnOrderShutDown.setVisibility(View.INVISIBLE);
      holder.btnOrderShutFinish.setVisibility(View.INVISIBLE);
      holder.tvMoney.setVisibility(View.INVISIBLE);
    } else {
      holder.tvOrderStatus.setText("已退订");
      holder.btnOrderShutDown.setVisibility(View.INVISIBLE);
      holder.btnOrderShutFinish.setVisibility(View.INVISIBLE);
      holder.tvMoney.setVisibility(View.INVISIBLE);
    }
    holder.btnOrderShutDown.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        bCallback.btnclick(0, item.getOrderId());
      }
    });
    holder.btnOrderShutFinish.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        bCallback.btnclick(1, item.getOrderId());
      }
    });
    holder.tvServiceName.setText("服务名称：" + item.getServiceName());
    holder.tvPersonName.setText("联系人：" + item.getUserName());
    holder.tvTelephone.setText("联系方式：" + item.getUserContact());

    // if (getCurStatus() != 0 && getCurStatus() != item.getState()) {
    // holder.llOrderItem.setVisibility(View.GONE);
    // }
    // else if()
    return view;
  }

  public int getCurStatus() {
    return curStatus;
  }

  public void setCurStatus(int curStatus) {
    this.curStatus = curStatus;
  }

  final static class ViewHolder {
    public TextView tvOrderNumber;
    public TextView tvOrderStatus;
    public TextView tvServiceName;
    public TextView tvPersonName;
    public TextView tvTelephone;
    public Button btnOrderShutDown;
    public Button btnOrderShutFinish;
    public TextView tvMoney;
    public LinearLayout llOrderItem;
  }

  public interface BtnCallBack {
    void btnclick(int status, String id);
  }
}