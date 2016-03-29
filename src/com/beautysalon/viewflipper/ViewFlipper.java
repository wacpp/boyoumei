package com.beautysalon.viewflipper;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

public class ViewFlipper extends android.widget.ViewFlipper {
  private int postiont = 0;

  public ViewFlipper(Context context) {
    this(context, null);
    // TODO Auto-generated constructor stub
  }

  public ViewFlipper(Context context, AttributeSet attrs) {
    super(context, attrs);
    // TODO Auto-generated constructor stub

  }

  public void setPositon(int positon) {
    this.postiont = positon;
  }

  @Override
  public void showPrevious() {
    Log.e("hhhhhhhhhh", "showPrevious");
   
    super.showPrevious();
  }

  @Override
  public void showNext() {
    Log.e("hhhhhhhhhh", "showNext");
    
    super.showNext();
  }

}
