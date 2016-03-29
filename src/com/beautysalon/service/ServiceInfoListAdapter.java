package com.beautysalon.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.redcdn.log.CustomLog;

import com.beautysalon.po.ProductServicePO;
import com.beautysalon.util.CommUtils;
import com.beautysalon.util.DisplayImageListener;
import com.beautysalon.util.TokenManager;
import com.example.testgallary.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ServiceInfoListAdapter extends BaseAdapter {
  private List<ProductServicePO> productServiceList = new ArrayList<ProductServicePO>();
  private Context context;
  private String TAG = ServiceInfoListAdapter.class.getName();
  private BtnCallBack bCallback;
  private String serviceId = null;
  private DisplayImageListener mDisplayImageListener = null;

  public ServiceInfoListAdapter(List<ProductServicePO> mProductServiceList,
      Context c, BtnCallBack btnCallBack) {
    productServiceList = mProductServiceList;
    for (int i = 0; i < productServiceList.size(); i++) {
      CustomLog.e(TAG, productServiceList.get(i).toString() + "..."
          + mProductServiceList.get(i).toString());
    }
    context = c;
    bCallback = btnCallBack;
    mDisplayImageListener = new DisplayImageListener();
  }

  @Override
  public int getCount() {
    return productServiceList.size();
  }

  @Override
  public Object getItem(int arg0) {
    // TODO Auto-generated method stub
    return productServiceList.get(arg0);
  }

  @Override
  public long getItemId(int arg0) {

    return arg0;
  }

  @Override
  public View getView(final int position, View view, ViewGroup arg2) {
    ViewHolder holder = null;
    CustomLog.d(TAG,
        "position=" + position + productServiceList.get(position).toString()
            + productServiceList.size());
    ProductServicePO item = (ProductServicePO) getItem(position);

    serviceId = item.getServiceId();
    if (view == null) {
      holder = new ViewHolder();
      view = LayoutInflater.from(context).inflate(R.layout.service_info_item,
          null);
      holder.tvServiceName = (TextView) view.findViewById(R.id.tv_order_number);
      holder.tvServiceImg = (ImageView) view.findViewById(R.id.tv_service_img);
      holder.tvServiceBigTag = (TextView) view
          .findViewById(R.id.tv_service_big);
      holder.tvServiceSmallTag1 = (TextView) view
          .findViewById(R.id.tv_service_small_tag1);
      holder.tvServiceSmallTag2 = (TextView) view
          .findViewById(R.id.tv_service_small_tag2);
      holder.tvServiceSmallTag3 = (TextView) view
          .findViewById(R.id.tv_service_small_tag3);
      holder.btnOrderStatus = (Button) view.findViewById(R.id.btn_order_status);
      holder.tvDetail = (TextView) view.findViewById(R.id.tv_detail);
      holder.tvMoney = (TextView) view.findViewById(R.id.tv_money);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }
    CustomLog.d(TAG, item.toString());
    holder.tvServiceName.setText(item.getServiceName() + "");
    holder.tvServiceBigTag.setText(item.getProductName() + "");
    if (item.getTypeName() != null) {
      if (item.getTypeName().size() == 0) {
        holder.tvServiceSmallTag1.setVisibility(View.INVISIBLE);
        holder.tvServiceSmallTag2.setVisibility(View.INVISIBLE);
        holder.tvServiceSmallTag3.setVisibility(View.INVISIBLE);
      } else if (item.getTypeName().size() == 1) {
        holder.tvServiceSmallTag1.setVisibility(View.VISIBLE);
        holder.tvServiceSmallTag2.setVisibility(View.INVISIBLE);
        holder.tvServiceSmallTag3.setVisibility(View.INVISIBLE);
        holder.tvServiceSmallTag1.setText(item.getTypeName().get(0));
      } else if (item.getTypeName().size() == 2) {
        holder.tvServiceSmallTag1.setVisibility(View.VISIBLE);
        holder.tvServiceSmallTag2.setVisibility(View.VISIBLE);
        holder.tvServiceSmallTag3.setVisibility(View.INVISIBLE);
        holder.tvServiceSmallTag1.setText(item.getTypeName().get(0));
        holder.tvServiceSmallTag2.setText(item.getTypeName().get(1));
      } else {
        holder.tvServiceSmallTag1.setVisibility(View.VISIBLE);
        holder.tvServiceSmallTag2.setVisibility(View.VISIBLE);
        holder.tvServiceSmallTag3.setVisibility(View.VISIBLE);
        holder.tvServiceSmallTag1.setText(item.getTypeName().get(0));
        holder.tvServiceSmallTag2.setText(item.getTypeName().get(1));
        holder.tvServiceSmallTag3.setText("...");
      }
    }
    holder.tvDetail.setText(item.getServiceDec());
    holder.tvMoney.setText("¥" + item.getPrice());

    holder.btnOrderStatus.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        bCallback.btnclick(0, position);
      }
    });
    holder.tvServiceName.setText("服务名称：" + item.getServiceName());
    ImageLoader imageLoader = ImageLoader.getInstance();
    // ImageAware imageAware = new ImageViewAware(viewHolder.headImage, false);

    imageLoader.displayImage(CommUtils.REQUEST_URL_SUFFIX + item.getFile(),
        holder.tvServiceImg, TokenManager.getInstance().options,
        mDisplayImageListener);

    return view;
  }

  final static class ViewHolder {
    public TextView tvServiceName;
    public ImageView tvServiceImg;
    public TextView tvServiceBigTag;
    public TextView tvServiceSmallTag1;
    public TextView tvServiceSmallTag2;
    public TextView tvServiceSmallTag3;
    public Button btnOrderStatus;// 编辑按钮
    public TextView tvMoney;
    public TextView tvDetail;
  }

  public interface BtnCallBack {
    void btnclick(int status, int position);
  }
}