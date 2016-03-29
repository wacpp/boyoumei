package com.beautysalon.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import cn.redcdn.log.CustomLog;

import com.beautysalon.util.CommUtils;

public class BaseActivity extends Activity {
  protected final String TAG = getClass().getName();
  private Dialog mLoadingDialog = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    CustomLog.d(TAG, "onCreate:" + this.toString());
    super.onCreate(savedInstanceState);

  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (ev.getPointerCount() > 1) {
      if (ev.getAction() != MotionEvent.ACTION_POINTER_1_DOWN
          && ev.getAction() != MotionEvent.ACTION_POINTER_1_UP
          && ev.getAction() != MotionEvent.ACTION_DOWN
          && ev.getAction() != MotionEvent.ACTION_UP
          && ev.getAction() != MotionEvent.ACTION_MOVE
          && ev.getAction() != MotionEvent.ACTION_CANCEL) {
        return true;
      }
    }
    return super.dispatchTouchEvent(ev);
  }

  @Override
  protected void onPause() {
    CustomLog.d(TAG, "onPause:" + this.toString());
    super.onPause();
  }

//  @Override
//  protected void onResume() {
//    Log.d(TAG, "onResume:" + this.toString());
//    super.onResume();
//  }

  @Override
  protected void onStop() {
    CustomLog.d(TAG, "onStop:" + this.toString());
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    CustomLog.d(TAG, "onDestroy:" + this.toString());
    super.onDestroy();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    CustomLog.d(TAG, "onNewIntent:" + this.toString());
    super.onNewIntent(intent);
  }

  protected void showLoadingView(String message) {
    CustomLog.i(TAG, "MeetingActivity::showLoadingDialog() msg: " + message);
    if (mLoadingDialog != null) {
      mLoadingDialog.dismiss();
    }
    mLoadingDialog = CommUtils.createLoadingDialog(this, message);
    try {
      mLoadingDialog.show();
    } catch (Exception ex) {
      CustomLog.d(TAG, "BaseActivity::showLoadingView()" + ex.toString());
    }
  }

  protected void showLoadingView(String message,
      DialogInterface.OnCancelListener listener) {
    CustomLog.i(TAG, "MeetingActivity::showLoadingDialog() msg: " + message);
    if (mLoadingDialog != null) {
      mLoadingDialog.dismiss();
    }
    mLoadingDialog = CommUtils.createLoadingDialog(this, message, listener);
    try {
      mLoadingDialog.show();
    } catch (Exception ex) {
      CustomLog.d(TAG, "BaseActivity::showLoadingView()" + ex.toString());
    }
  }

  protected void showLoadingView(String message,
      final DialogInterface.OnCancelListener listener, boolean cancelAble) {
    CustomLog.i(TAG, "MeetingActivity::showLoadingDialog() msg: " + message);
    if (mLoadingDialog != null) {
      mLoadingDialog.dismiss();
    }
    mLoadingDialog = CommUtils.createLoadingDialog(this, message, listener);
    mLoadingDialog.setCancelable(cancelAble);
    mLoadingDialog.setOnKeyListener(new OnKeyListener() {

      @Override
      public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          listener.onCancel(dialog);
        }
        return false;
      }
    });
    try {
      mLoadingDialog.show();
    } catch (Exception ex) {
      CustomLog.d(TAG, "BaseActivity::showLoadingView()" + ex.toString());
    }
  }

  protected void removeLoadingView() {
    CustomLog.i(TAG, "MeetingActivity::removeLoadingView()");
    if (mLoadingDialog != null) {
      mLoadingDialog.dismiss();
      mLoadingDialog = null;
    }
  }

}
