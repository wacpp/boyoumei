package com.beautysalon.viewflipper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GridView extends android.widget.GridView {

  public GridView(Context context) {
    super(context);
    // TODO Auto-generated constructor stub
  }

  public GridView(Context context, AttributeSet attrs) {
    super(context, attrs);
    // TODO Auto-generated constructor stub

  }

  public int positon = -1;

  public int getPositon() {
    return positon;
  }

  public void setPositon(int positon) {
    Log.e("setPositon positon  ", "" + positon);
    this.positon = positon;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int heightSpec;

//    if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
//      // The <span id="4_nwp"
//      // id="4_nwl"
//      //
//      // target="_blank" mpid="4" style="text-decoration: none;"><span
//      // spec have
//      // a special meaning, hence we can't use them to describe height.
//      heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//          MeasureSpec.AT_MOST);
//    } else {
//      // Any other height should be <span id="6_nwp"
//      // style="width: auto; height: auto; float: none;"><a id="6_nwl"
//      //
//      heightSpec = heightMeasureSpec;
//    }
//
//    super.onMeasure(widthMeasureSpec, heightSpec);
    int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
        MeasureSpec.AT_MOST);
    super.onMeasure(widthMeasureSpec, expandSpec);
  }

  @Override
  protected void onFocusChanged(boolean gainFocus, int direction,
      Rect previouslyFocusedRect) {
    // Log.e("onFocusChanged positon  ", "" + positon + " " + gainFocus);
    // int lastSelectItem = getSelectedItemPosition();
    if (gainFocus) {
      Log.e("onFocusChanged gainFocus  ", "" + positon + " " + gainFocus
          + "this=" + this);
      // TextView v = (TextView) this.findViewById(R.id.itemText);
      // v.setTextColor(Color.parseColor("#eaf0f3"));
      // ViewGroup.LayoutParams lp = v.getLayoutParams();
      // lp.width = 80;
      // lp.height = 80;
      // v.setLayoutParams(lp);
      setSelection(positon);
      /*
       * Handler h = new Handler(); Runnable r = new Runnable() { public void
       * run() { setSelection(positon); } }; h.postDelayed(r, 5);
       */
    }
    super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
  }

}
