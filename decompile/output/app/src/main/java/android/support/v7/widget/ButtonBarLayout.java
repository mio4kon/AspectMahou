package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.v4.content.res.ConfigurationHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.styleable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;

public class ButtonBarLayout
  extends LinearLayout
{
  private static final int ALLOW_STACKING_MIN_HEIGHT_DP = 320;
  private boolean mAllowStacking;
  private int mLastWidthSize = -1;
  
  public ButtonBarLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    if (ConfigurationHelper.getScreenHeightDp(getResources()) >= 320) {}
    for (boolean bool = true;; bool = false)
    {
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ButtonBarLayout);
      mAllowStacking = localTypedArray.getBoolean(R.styleable.ButtonBarLayout_allowStacking, bool);
      localTypedArray.recycle();
      return;
    }
  }
  
  private boolean isStacked()
  {
    return getOrientation() == 1;
  }
  
  private void setStacked(boolean paramBoolean)
  {
    int i;
    int j;
    label17:
    View localView;
    if (paramBoolean)
    {
      i = 1;
      setOrientation(i);
      if (!paramBoolean) {
        break label86;
      }
      j = 5;
      setGravity(j);
      localView = findViewById(R.id.spacer);
      if (localView != null) {
        if (!paramBoolean) {
          break label92;
        }
      }
    }
    label86:
    label92:
    for (int m = 8;; m = 4)
    {
      localView.setVisibility(m);
      for (int k = -2 + getChildCount(); k >= 0; k--) {
        bringChildToFront(getChildAt(k));
      }
      i = 0;
      break;
      j = 80;
      break label17;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt1);
    if (mAllowStacking)
    {
      if ((i > mLastWidthSize) && (isStacked())) {
        setStacked(false);
      }
      mLastWidthSize = i;
    }
    int j;
    int k;
    int i3;
    if ((!isStacked()) && (View.MeasureSpec.getMode(paramInt1) == 1073741824))
    {
      j = View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE);
      k = 1;
      super.onMeasure(j, paramInt2);
      if ((mAllowStacking) && (!isStacked()))
      {
        if (Build.VERSION.SDK_INT < 11) {
          break label149;
        }
        int i4 = 0xFF000000 & ViewCompat.getMeasuredWidthAndState(this);
        i3 = 0;
        if (i4 == 16777216) {
          i3 = 1;
        }
      }
    }
    for (;;)
    {
      if (i3 != 0)
      {
        setStacked(true);
        k = 1;
      }
      if (k != 0) {
        super.onMeasure(paramInt1, paramInt2);
      }
      return;
      j = paramInt1;
      k = 0;
      break;
      label149:
      int m = 0;
      int n = 0;
      int i1 = getChildCount();
      while (n < i1)
      {
        m += getChildAt(n).getMeasuredWidth();
        n++;
      }
      int i2 = m + getPaddingLeft() + getPaddingRight();
      i3 = 0;
      if (i2 > i) {
        i3 = 1;
      }
    }
  }
  
  public void setAllowStacking(boolean paramBoolean)
  {
    if (mAllowStacking != paramBoolean)
    {
      mAllowStacking = paramBoolean;
      if ((!mAllowStacking) && (getOrientation() == 1)) {
        setStacked(false);
      }
      requestLayout();
    }
  }
}
