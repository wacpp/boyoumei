package com.beautysalon.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.redcdn.log.CustomLog;

/**
 * 
 * 上传工具�?
 * 
 * @author spring sky<br>
 *         Email :vipa1888@163.com<br>
 *         QQ: 840950105<br>
 *         支持上传文件和参�?
 */
public class UploadUtil {
  private static UploadUtil uploadUtil;
  private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识
                                                                       // 随机生成
  private static final String PREFIX = "--";
  private static final String LINE_END = "\r\n";
  private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型

  private UploadUtil() {

  }

  /**
   * 单例模式获取上传工具�?
   * 
   * @return
   */
  public static UploadUtil getInstance() {
    if (null == uploadUtil) {
      uploadUtil = new UploadUtil();
    }
    return uploadUtil;
  }

  private static final String TAG = "BEAUTYAPP";
  private int readTimeOut = 10 * 1000; // 读取超时
  private int connectTimeout = 10 * 1000; // 超时时间
  /***
   * 请求使用多长时间
   */
  private static int requestTime = 0;

  private static final String CHARSET = "utf-8"; // 设置编码

  /***
   * 上传成功
   */
  public static final int UPLOAD_SUCCESS_CODE = 1;
  /**
   * 文件不存�?
   */
  public static final int UPLOAD_FILE_NOT_EXISTS_CODE = 2;
  /**
   * 服务器出�?
   */
  public static final int UPLOAD_SERVER_ERROR_CODE = 3;
  protected static final int WHAT_TO_UPLOAD = 1;
  protected static final int WHAT_UPLOAD_DONE = 2;

  /**
   * android上传文件到服务器
   * 
   * @param filePath
   *          �?��上传的文件的路径
   * @param fileKey
   *          在网页上<input type=file name=xxx/> xxx就是这里的fileKey
   * @param RequestURL
   *          请求的URL
   */
  public void uploadFile(final List<String> filePathList, final String fileKey,
      final String RequestURL, final Map<String, Object> param) {
    CustomLog.e("uploadFile", "filePathList=" + filePathList.size()
        + "RequestURL=" + RequestURL);
    if (filePathList.size() == 0) {
      sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE, "File not exist");
      return;
    }
    new Thread(new Runnable() { // �?��线程上传文件
          @Override
          public void run() {
            toUploadFile(filePathList, fileKey, RequestURL, param);
          }
        }).start();
    return;

  }

  /**
   * android上传文件到服务器
   * 
   * @param file
   *          �?��上传的文�?
   * @param fileKey
   *          在网页上<input type=file name=xxx/> xxx就是这里的fileKey
   * @param RequestURL
   *          请求的URL
   */

  private void toUploadFile(List<String> filePathList, String fileKey,
      String RequestURL, Map<String, Object> param) {
    String result = null;
    requestTime = 0;

    long requestTime = System.currentTimeMillis();
    long responseTime = 0;

    try {
      URL url = new URL(RequestURL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(readTimeOut);
      conn.setConnectTimeout(connectTimeout);
      conn.setDoInput(true); // 允许输入�?
      conn.setDoOutput(true); // 允许输出�?
      conn.setUseCaches(false); // 不允许使用缓�?
      conn.setRequestMethod("POST"); // 请求方式
      conn.setRequestProperty("Charset", CHARSET); // 设置编码
      conn.setRequestProperty("connection", "keep-alive");
      conn.setRequestProperty("user-agent",
          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
      conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
          + BOUNDARY);
      // conn.setRequestProperty("Content-Type",
      // "application/x-www-form-urlencoded");

      /**
       * 当文件不为空，把文件包装并且上传
       */
      DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
      StringBuffer sb = null;
      String params = "";

      /***
       * 以下是用于上传参�?
       */
      if (param != null && param.size() > 0) {
        Iterator<String> it = param.keySet().iterator();
        while (it.hasNext()) {
          sb = null;
          sb = new StringBuffer();
          String key = it.next();
          Object value = param.get(key);
          sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
          sb.append("Content-Disposition: form-data; name=\"").append(key)
              .append("\"").append(LINE_END).append(LINE_END);
          sb.append(value).append(LINE_END);
          params = sb.toString();
          CustomLog.i(TAG, key + "=" + params + "##");
          dos.write(params.getBytes());
          // dos.flush();
        }
      }

      sb = null;
      params = null;
      sb = new StringBuffer();

      CustomLog.i(TAG, "File Num =" + filePathList.size());

      if (filePathList != null && filePathList.size() > 0) {
        Iterator<String> it_file = filePathList.iterator();
        while (it_file.hasNext()) {

          sb = null;
          sb = new StringBuffer();
          String filepath = it_file.next();
          File file = new File(filepath);
          CustomLog.d(TAG, "filepath=" + filepath);
          sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
          sb.append("Content-Disposition:form-data; name=\"" + fileKey
              + "\"; filename=\"" + file.getName() + "\"" + LINE_END);
          sb.append("Content-Type:image/pjpeg" + LINE_END); // 这里配置的Content-type很重要的
          // image/jpg,image/bmp,image/png,image/gif,image/jpeg,image/x-png,image/pjpeg
          // // ，用于服务器端辨别文件的类型�?
          sb.append(LINE_END);
          params = sb.toString();
          dos.write(params.getBytes());
          InputStream is = new FileInputStream(file);
          byte[] bytes = new byte[1024];
          int len = 0;
          int curLen = 0;
          while ((len = is.read(bytes)) != -1) {
            curLen += len;
            dos.write(bytes, 0, len);
          }
          dos.write(LINE_END.getBytes());
          is.close();
          dos.write(LINE_END.getBytes());
          dos.write(LINE_END.getBytes());
        }
      }

      byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
      dos.write(end_data);
      dos.flush();
      //
      // dos.write(tempOutputStream.toByteArray());
      /**
       * 获取响应�?200=成功 当响应成功，获取响应的流
       */
      int res = conn.getResponseCode();
      responseTime = System.currentTimeMillis();
      this.requestTime = (int) ((responseTime - requestTime) / 1000);
      CustomLog.e(TAG, "response code:" + res);
      if (res == 200) {
        CustomLog.e(TAG, "request success");
        InputStream input = conn.getInputStream();
        StringBuffer sb1 = new StringBuffer();
        int ss;
        while ((ss = input.read()) != -1) {
          sb1.append((char) ss);
        }
        result = sb1.toString();
        CustomLog.e(TAG, "result : " + result);
        sendMessage(UPLOAD_SUCCESS_CODE, "Upload result" + result);
        return;
      } else {
        CustomLog.e(TAG, "request error");
        sendMessage(UPLOAD_SERVER_ERROR_CODE, "上传失败：code=" + res);
        return;
      }
    } catch (MalformedURLException e) {
      sendMessage(UPLOAD_SERVER_ERROR_CODE, "上传失败：error=" + e.getMessage());
      e.printStackTrace();
      return;
    } catch (IOException e) {
      sendMessage(UPLOAD_SERVER_ERROR_CODE, "上传失败：error=" + e.getMessage());
      e.printStackTrace();
      return;
    }
  }

  /**
   * 发�?上传结果
   * 
   * @param responseCode
   * @param responseMessage
   */
  private void sendMessage(int responseCode, String responseMessage) {
    onUploadProcessListener.onUploadDone(responseCode, responseMessage);
  }

  /**
   * 下面是一个自定义的回调函数，用到回调上传文件是否完成
   * 
   * @author shimingzheng
   * 
   */
  public static interface OnUploadProcessListener {
    /**
     * 上传响应
     * 
     * @param responseCode
     * @param message
     */
    void onUploadDone(int responseCode, String message);

    /**
     * 上传�?
     * 
     * @param uploadSize
     */
    void onUploadProcess(int uploadSize);

    /**
     * 准备上传
     * 
     * @param fileSize
     */
    void initUpload(int fileSize);
  }

  private OnUploadProcessListener onUploadProcessListener;

  public void setOnUploadProcessListener(
      OnUploadProcessListener onUploadProcessListener) {
    this.onUploadProcessListener = onUploadProcessListener;
  }

  public int getReadTimeOut() {
    return readTimeOut;
  }

  public void setReadTimeOut(int readTimeOut) {
    this.readTimeOut = readTimeOut;
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  /**
   * 获取上传使用的时�?
   * 
   * @return
   */
  public static int getRequestTime() {
    return requestTime;
  }

  public static interface uploadProcessListener {

  }

}
