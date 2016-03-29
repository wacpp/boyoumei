package com.beautysalon.util;

import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.testgallary.R;

import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CommUtils {

  // public static final String REQUEST_URL_SUFFIX =
  // "http://121.41.35.130:8080/BeautyCentre/";
  public static final String REQUEST_URL_SUFFIX = "http://umeibar.com/BeautyCentre/";
  private static final String REQUEST_LOGIN = "login";
  private static final String REQUEST_REGISTER = "businessRegiste";
  private static final String REQUEST_UNLOGIN = "unlogin";
  private static final String REQUEST_GETSHOPINFO = "getShopInfo";// 获取店铺信息
  private static final String REQUEST_UPDATESHOPINFO = "updateShopInfo";// 上传店铺信息
  private static final String REQUEST_GETSERVICETYPE = "getServtype";// 获取服务标签
  private static final String REQUEST_GETCITYINFO = "getCityInfo";// 获取服务标签

  private static final String REQUEST_PRODUCESERVICE = "produceService";// 发布、编辑服务
  private static final String REQUEST_GETSERVSINFO = "getServsInfo";// 获取服务信息列表
  private static final String REQUEST_DELETESERVICE = "deleteService";// 删除服务
  private static final String REQUEST_GETORDERSINFO = "getOrdersInfo";// 获取订单信息列表

  private static final String REQUEST_SHOP_ORDER_INFO = "getShopOrdInfo";

  private static final String REQUEST_EDITORDER = "editOrder";// 编辑订单
  private static final int REQUEST_RESP_STATUS_SUC = 0;// 成功
  private static final int REQUEST_RESP_STATUS_SYSTEM_ERROR = -999;// 系统错误
  private static final int REQUEST_RESP_STATUS_JSON_ERROR = -900;// 传入json数据错误
  private static final int REQUEST_RESP_STATUS_NOPARAM_ERROR = -901;// 传入参数为空
  private static final int REQUEST_RESP_STATUS_TOKEN_ERROR = -902;// TOKEN失效

  /**
   * 验证邮箱输入是否合法
   * 
   * @param strEmail
   * @return
   */
  public static boolean isEmail(String strEmail) {
    // String strPattern =
    // "^([a-z0-9A-Z]+[-|//.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?//.)+[a-zA-Z]{2,}$";
    // String strPattern =
    // "^//s*//w+(?://.{0,1}[//w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*//.[a-zA-Z]+//s*$";
    // Pattern p = Pattern.compile(strPattern);
    // Matcher m = p.matcher(strEmail);
    // return m.matches();
    Pattern pattern = Pattern
        .compile("^[A-Za-z0-9][\\w\\._]*[a-zA-Z0-9]+@[A-Za-z0-9-_]+\\.([A-Za-z]{2,4})");
    Matcher mc = pattern.matcher(strEmail);
    return mc.matches();
  }

  /***
   * MD5加码 生成32位md5码
   */
  public static String string2MD5(String inStr) {
    MessageDigest md5 = null;
    try {
      md5 = MessageDigest.getInstance("MD5");
    } catch (Exception e) {
      System.out.println(e.toString());
      e.printStackTrace();
      return "";
    }
    char[] charArray = inStr.toCharArray();
    byte[] byteArray = new byte[charArray.length];

    for (int i = 0; i < charArray.length; i++)
      byteArray[i] = (byte) charArray[i];
    byte[] md5Bytes = md5.digest(byteArray);
    StringBuffer hexValue = new StringBuffer();
    for (int i = 0; i < md5Bytes.length; i++) {
      int val = ((int) md5Bytes[i]) & 0xff;
      if (val < 16)
        hexValue.append("0");
      hexValue.append(Integer.toHexString(val));
    }
    return hexValue.toString();

  }

  public static String getLoginUrl() {
    if (REQUEST_URL_SUFFIX != null && REQUEST_LOGIN != null) {
      return REQUEST_URL_SUFFIX + REQUEST_LOGIN;
    }
    return "127.0.0.1/" + REQUEST_LOGIN;
  }

  public static String getRegisterUrl() {
    if (REQUEST_URL_SUFFIX != null && REQUEST_REGISTER != null) {
      return REQUEST_URL_SUFFIX + REQUEST_REGISTER;
    }
    return "127.0.0.1/" + REQUEST_REGISTER;
  }

  public static String getUnLoginUrl() {
    if (REQUEST_URL_SUFFIX != null && REQUEST_UNLOGIN != null) {
      return REQUEST_URL_SUFFIX + REQUEST_UNLOGIN;
    }
    return "127.0.0.1/" + REQUEST_UNLOGIN;
  }

  public static String getShopInfoUrl() {
    if (REQUEST_URL_SUFFIX != null && REQUEST_GETSHOPINFO != null) {
      return REQUEST_URL_SUFFIX + REQUEST_GETSHOPINFO;
    }
    return "127.0.0.1/" + REQUEST_GETSHOPINFO;
  }

  public static String getUpdateShopInfoUrl() {
    if (REQUEST_URL_SUFFIX != null && REQUEST_UPDATESHOPINFO != null) {
      return REQUEST_URL_SUFFIX + REQUEST_UPDATESHOPINFO;
    }
    return "127.0.0.1/" + REQUEST_UPDATESHOPINFO;
  }

  public static String getServTypeUrl() {
    if (REQUEST_URL_SUFFIX != null && REQUEST_GETSERVICETYPE != null) {
      return REQUEST_URL_SUFFIX + REQUEST_GETSERVICETYPE;
    }
    return "127.0.0.1/" + REQUEST_GETSERVICETYPE;
  }

  public static String getCityInfoUrl() {
    if (REQUEST_URL_SUFFIX != null && REQUEST_GETCITYINFO != null) {
      return REQUEST_URL_SUFFIX + REQUEST_GETCITYINFO;
    }
    return "127.0.0.1/" + REQUEST_GETCITYINFO;
  }

  public static String getProduceServiceUrl() {
    if (REQUEST_URL_SUFFIX != null && REQUEST_PRODUCESERVICE != null) {
      return REQUEST_URL_SUFFIX + REQUEST_PRODUCESERVICE;
    }
    return "127.0.0.1/" + REQUEST_PRODUCESERVICE;
  }

  public static String getServsInfoUrl() {
    if (REQUEST_URL_SUFFIX != null && REQUEST_GETSERVSINFO != null) {
      return REQUEST_URL_SUFFIX + REQUEST_GETSERVSINFO;
    }
    return "127.0.0.1/" + REQUEST_GETSERVSINFO;
  }

  public static String getDeleteServiceUrl() {
    if (REQUEST_URL_SUFFIX != null && REQUEST_DELETESERVICE != null) {
      return REQUEST_URL_SUFFIX + REQUEST_DELETESERVICE;
    }
    return "127.0.0.1/" + REQUEST_DELETESERVICE;
  }

  public static String getOrdersInfoUrl() {
    if (REQUEST_URL_SUFFIX != null && REQUEST_GETORDERSINFO != null) {
      return REQUEST_URL_SUFFIX + REQUEST_GETORDERSINFO;
    }
    return "127.0.0.1/" + REQUEST_GETORDERSINFO;
  }

  public static String getShopOrdInfoUrl() {
    if (REQUEST_URL_SUFFIX != null && REQUEST_SHOP_ORDER_INFO != null) {
      return REQUEST_URL_SUFFIX + REQUEST_SHOP_ORDER_INFO;
    }
    return "127.0.0.1/" + REQUEST_SHOP_ORDER_INFO;
  }

  public static String getEditOrderUrl() {
    if (REQUEST_URL_SUFFIX != null && REQUEST_EDITORDER != null) {
      return REQUEST_URL_SUFFIX + REQUEST_EDITORDER;
    }
    return "127.0.0.1/" + REQUEST_EDITORDER;
  }

  /**
   * 
   * 得到自定义的progressDialog
   * 
   * @param context
   * @param msg
   * @return
   */
  public static Dialog createLoadingDialog(Context context, String msg) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View v = inflater.inflate(R.layout.loading_dialog, null);
    RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.dialog_view);
    ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
    TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);

    Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context,
        R.drawable.loading_animation);

    spaceshipImage.startAnimation(hyperspaceJumpAnimation);

    if (msg == null || msg.equals("")) {
      RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
          LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      params.addRule(RelativeLayout.CENTER_IN_PARENT);
      spaceshipImage.setLayoutParams(params);
    }

    tipTextView.setText(msg);

    Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
    loadingDialog.setCanceledOnTouchOutside(false);
    loadingDialog.setCancelable(false);
    loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT));
    return loadingDialog;

  }

  public static Dialog createLoadingDialog(Context context, String msg,
      DialogInterface.OnCancelListener listener) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View v = inflater.inflate(R.layout.loading_dialog, null);
    RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.dialog_view);

    ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
    TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);

    Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context,
        R.drawable.loading_animation);

    if (msg == null || msg.equals("")) {
      RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
          LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      params.addRule(RelativeLayout.CENTER_IN_PARENT);
      spaceshipImage.setLayoutParams(params);
    }

    spaceshipImage.startAnimation(hyperspaceJumpAnimation);
    tipTextView.setText(msg);

    Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);

    loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT));

    loadingDialog.setCanceledOnTouchOutside(false);

    if (listener != null) {
      loadingDialog.setOnCancelListener(listener);
    }

    return loadingDialog;

  }

  /** 没有网络 */
  public static final int NETWORKTYPE_INVALID = -1;
  /** wifi网络 */
  public static final int NETWORKTYPE_WIFI = 1;
  /** 2G网络 */
  public static final int NETWORKTYPE_2G = 2;
  /** 3G和3G以上网络，或统称为快速网络 */
  public static final int NETWORKTYPE_3G = 3;
  /** wap网络 */
  public static final int NETWORKTYPE_WAP = 4;

  /**
   * 获取网络状态: wifi,wap,2g,3g.
   * 
   * @param appcontext
   *          上下文
   * @return int 网络状态
   * 
   *         -1: 没有网络；
   * 
   *         1：wifi网络；
   * 
   *         2: 2G网络；
   * 
   *         3: 3G及以上网络；
   * 
   *         4：wap网络
   */

  public static int getNetWorkType(Context appcontext) {

    ConnectivityManager manager = (ConnectivityManager) appcontext
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = manager.getActiveNetworkInfo();

    int mNetWorkType = NETWORKTYPE_INVALID;

    if (networkInfo != null && networkInfo.isConnected()) {
      String type = networkInfo.getTypeName();

      if (type.equalsIgnoreCase("WIFI")) {
        mNetWorkType = NETWORKTYPE_WIFI;
      } else if (type.equalsIgnoreCase("MOBILE")) {
        String proxyHost = android.net.Proxy.getDefaultHost();

        mNetWorkType = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(appcontext) ? NETWORKTYPE_3G
            : NETWORKTYPE_2G)
            : NETWORKTYPE_WAP;
      }
    }

    return mNetWorkType;
  }

  private static boolean isFastMobileNetwork(Context context) {
    TelephonyManager telephonyManager = (TelephonyManager) context
        .getSystemService(Context.TELEPHONY_SERVICE);
    switch (telephonyManager.getNetworkType()) {
    case TelephonyManager.NETWORK_TYPE_1xRTT:
      return false; // ~ 50-100 kbps
    case TelephonyManager.NETWORK_TYPE_CDMA:
      return false; // ~ 14-64 kbps
    case TelephonyManager.NETWORK_TYPE_EDGE:
      return false; // ~ 50-100 kbps
    case TelephonyManager.NETWORK_TYPE_EVDO_0:
      return true; // ~ 400-1000 kbps
    case TelephonyManager.NETWORK_TYPE_EVDO_A:
      return true; // ~ 600-1400 kbps
    case TelephonyManager.NETWORK_TYPE_GPRS:
      return false; // ~ 100 kbps
    case TelephonyManager.NETWORK_TYPE_HSDPA:
      return true; // ~ 2-14 Mbps
    case TelephonyManager.NETWORK_TYPE_HSPA:
      return true; // ~ 700-1700 kbps
    case TelephonyManager.NETWORK_TYPE_HSUPA:
      return true; // ~ 1-23 Mbps
    case TelephonyManager.NETWORK_TYPE_UMTS:
      return true; // ~ 400-7000 kbps
    case TelephonyManager.NETWORK_TYPE_EHRPD:
      return true; // ~ 1-2 Mbps
    case TelephonyManager.NETWORK_TYPE_EVDO_B:
      return true; // ~ 5 Mbps
    case TelephonyManager.NETWORK_TYPE_HSPAP:
      return true; // ~ 10-20 Mbps
    case TelephonyManager.NETWORK_TYPE_IDEN:
      return false; // ~25 kbps
    case TelephonyManager.NETWORK_TYPE_LTE:
      return true; // ~ 10+ Mbps
    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
      return false;
    default:
      return false;
    }
  }

  /**
   * * 验证号码 手机号 固话均可
   * */
  public static boolean isPhoneNumberValid(String phoneNumber) {
    boolean isValid = false;

    String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
    CharSequence inputStr = phoneNumber;

    Pattern pattern = Pattern.compile(expression);

    Matcher matcher = pattern.matcher(inputStr);

    if (matcher.matches()) {
      isValid = true;
    }

    return isValid;

  }
}
