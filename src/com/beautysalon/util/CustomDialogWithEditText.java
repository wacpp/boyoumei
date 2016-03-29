package com.beautysalon.util;

import com.example.testgallary.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomDialogWithEditText extends Dialog {
  private Button okBtn;
  private Button cancelBtn;
  private boolean isWithCancelBtn = true;
  private String tip;
  private String showStatus;
  private int bShowStatus = View.INVISIBLE;
  private String okBtnText = "添加";
  private String cancelBtnText = "取消";
  private OKBtnOnClickListener okBtnOnClickListener;
  private EditText et = null;
  private TextView tvShowStatus = null;
  private CancelBtnOnClickListener cancelBtnOnClickListener;
  private InputOnClickListener inputOnClickListener;
  private InputTextChangedListenerListener inputOnFocusListener;
  private View titleUnderLine = null;
  RelativeLayout twoBtnLay = null;
  RelativeLayout oneBtnLay = null;
  private TextView inputName = null;
  private String input;

  public interface OKBtnOnClickListener {
    public void onClick(CustomDialogWithEditText customDialog, TextView tv);
  }

  public interface CancelBtnOnClickListener {
    public void onClick(CustomDialogWithEditText customDialog);
  }

  public interface InputOnClickListener {
    public void onClick(CustomDialogWithEditText customDialog);
  }

  public interface InputTextChangedListenerListener {
    public void onTextChangedListener(CustomDialogWithEditText customDialog,
        EditText et, TextView tv);
  }

  /**
   * 构造方法，传入activity的上下文
   * 
   * @param context
   */
  public CustomDialogWithEditText(Context context) {
    this(context, R.style.qn_custom_dialog);
  }

  public void setInputName(String name) {
    input = name;
  }

  /**
   * 构造方法，传入activity的上下文，以及对应的主题
   * 
   * @param context
   * @param theme
   */
  public CustomDialogWithEditText(Context context, int theme) {
    super(context, theme);
  }

  /**
   * 设置dialog的具体提示内容
   * 
   * @param tipStr
   */
  public void setTip(String tipStr) {
    tip = tipStr;
  }

  /**
   * 设置dialog的具体提示内容
   * 
   * @param tipStr
   */
  public void setShowStatus(String showStatus) {
    this.showStatus = showStatus;
  }

  /**
   * 设置dialog的具体提示内容
   * 
   * @param tipStr
   */
  public void setBShowStatus(int bShowStatus) {
    this.bShowStatus = bShowStatus;
  }

  /**
   * 设置确认按钮的文字
   * 
   * @param okBtnText
   */
  public void setOkBtnText(String okBtnText) {
    this.okBtnText = okBtnText;
  }

  /**
   * 设置取消按钮的文字
   * 
   * @param cancelBtnText
   */
  public void setCancelBtnText(String cancelBtnText) {
    this.cancelBtnText = cancelBtnText;
  }

  public void removeCancelBtn() {
    isWithCancelBtn = false;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
    getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION);
    this.setContentView(R.layout.addcontactdialog);
    setCanceledOnTouchOutside(false);
    RelativeLayout layout = (RelativeLayout) findViewById(R.id.add_contact_dialog);
    twoBtnLay = (RelativeLayout) findViewById(R.id.two_button_layout);
    oneBtnLay = (RelativeLayout) findViewById(R.id.one_button_layout);
    inputName = (TextView) findViewById(R.id.input_name);
    titleUnderLine = findViewById(R.id.qn_operate_dialog_title_unline);

    oneBtnLay.setVisibility(View.INVISIBLE);
    twoBtnLay.setVisibility(View.VISIBLE);
    okBtn = (Button) findViewById(R.id.dialog_button_ok);
    cancelBtn = (Button) findViewById(R.id.dialog_button_cancel);
    et = (EditText) findViewById(R.id.input_name_et);
    tvShowStatus = (TextView) findViewById(R.id.show_status);
    et.setOnEditorActionListener(new EditText.OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          InputMethodManager imm = (InputMethodManager) v.getContext()
              .getSystemService(Context.INPUT_METHOD_SERVICE);
          imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

          return true;
        }
        return false;
      }

    });
    okBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (okBtnOnClickListener != null && tvShowStatus != null) {
          okBtnOnClickListener.onClick(CustomDialogWithEditText.this,
              tvShowStatus);
        }
      }
    });
    cancelBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (cancelBtnOnClickListener != null) {
          cancelBtnOnClickListener.onClick(CustomDialogWithEditText.this);
        }
      }
    });
    et.addTextChangedListener(new TextWatcher() {

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (inputOnFocusListener != null) {
          if (et != null) {
            inputOnFocusListener.onTextChangedListener(
                CustomDialogWithEditText.this, et, tvShowStatus);
          }
        }
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
          int after) {

      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
    et.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        if (inputOnClickListener != null) {
          inputOnClickListener.onClick(CustomDialogWithEditText.this);
        }
      }
    });

    ((TextView) findViewById(R.id.tip)).setText(tip);
    ((TextView) findViewById(R.id.show_status)).setText(showStatus);
    ((TextView) findViewById(R.id.show_status)).setVisibility(bShowStatus);
    inputName.setText(input);
    okBtn.setText(okBtnText);
    cancelBtn.setText(cancelBtnText);
    titleUnderLine.setVisibility(View.VISIBLE);
    if (!isWithCancelBtn) {
      layout.setVisibility(View.GONE);
    } else {
      okBtn.requestFocus();
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    Log.d("AddDialog", "Dialog 点击，触控数： " + ev.getPointerCount());
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

  public void setOkBtnOnClickListener(OKBtnOnClickListener okBtnOnClickListener) {
    this.okBtnOnClickListener = okBtnOnClickListener;
  }

  public void setCancelBtnOnClickListener(
      CancelBtnOnClickListener cancelBtnOnClickListener) {
    this.cancelBtnOnClickListener = cancelBtnOnClickListener;
  }

  public void setInputOnClickListener(InputOnClickListener inputOnClickListener) {
    this.inputOnClickListener = inputOnClickListener;
  }

  public void setInputTextChangedListenerListener(
      InputTextChangedListenerListener inputOnFocusListener) {
    this.inputOnFocusListener = inputOnFocusListener;
  }

}
