package android.support.v7.widget;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.appcompat.R.attr;
import android.view.MotionEvent;
import android.view.View;

class DropDownListView
  extends ListViewCompat
{
  private ViewPropertyAnimatorCompat mClickAnimation;
  private boolean mDrawsInPressedState;
  private boolean mHijackFocus;
  private boolean mListSelectionHidden;
  private ListViewAutoScrollHelper mScrollHelper;
  
  public DropDownListView(Context paramContext, boolean paramBoolean)
  {
    super(paramContext, null, R.attr.dropDownListViewStyle);
    mHijackFocus = paramBoolean;
    setCacheColorHint(0);
  }
  
  private void clearPressedItem()
  {
    mDrawsInPressedState = false;
    setPressed(false);
    drawableStateChanged();
    View localView = getChildAt(mMotionPosition - getFirstVisiblePosition());
    if (localView != null) {
      localView.setPressed(false);
    }
    if (mClickAnimation != null)
    {
      mClickAnimation.cancel();
      mClickAnimation = null;
    }
  }
  
  private void clickPressedItem(View paramView, int paramInt)
  {
    performItemClick(paramView, paramInt, getItemIdAtPosition(paramInt));
  }
  
  private void setPressedItem(View paramView, int paramInt, float paramFloat1, float paramFloat2)
  {
    mDrawsInPressedState = true;
    if (Build.VERSION.SDK_INT >= 21) {
      drawableHotspotChanged(paramFloat1, paramFloat2);
    }
    if (!isPressed()) {
      setPressed(true);
    }
    layoutChildren();
    if (mMotionPosition != -1)
    {
      View localView = getChildAt(mMotionPosition - getFirstVisiblePosition());
      if ((localView != null) && (localView != paramView) && (localView.isPressed())) {
        localView.setPressed(false);
      }
    }
    mMotionPosition = paramInt;
    float f1 = paramFloat1 - paramView.getLeft();
    float f2 = paramFloat2 - paramView.getTop();
    if (Build.VERSION.SDK_INT >= 21) {
      paramView.drawableHotspotChanged(f1, f2);
    }
    if (!paramView.isPressed()) {
      paramView.setPressed(true);
    }
    positionSelectorLikeTouchCompat(paramInt, paramView, paramFloat1, paramFloat2);
    setSelectorEnabled(false);
    refreshDrawableState();
  }
  
  public boolean hasFocus()
  {
    return (mHijackFocus) || (super.hasFocus());
  }
  
  public boolean hasWindowFocus()
  {
    return (mHijackFocus) || (super.hasWindowFocus());
  }
  
  public boolean isFocused()
  {
    return (mHijackFocus) || (super.isFocused());
  }
  
  public boolean isInTouchMode()
  {
    return ((mHijackFocus) && (mListSelectionHidden)) || (super.isInTouchMode());
  }
  
  public boolean onForwardedEvent(MotionEvent paramMotionEvent, int paramInt)
  {
    boolean bool = true;
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    int j = 0;
    switch (i)
    {
    default: 
      if ((!bool) || (j != 0)) {
        clearPressedItem();
      }
      if (bool)
      {
        if (mScrollHelper == null) {
          mScrollHelper = new ListViewAutoScrollHelper(this);
        }
        mScrollHelper.setEnabled(true);
        mScrollHelper.onTouch(this, paramMotionEvent);
      }
      break;
    }
    while (mScrollHelper == null)
    {
      return bool;
      j = 0;
      bool = false;
      break;
      bool = false;
      int k = paramMotionEvent.findPointerIndex(paramInt);
      if (k < 0)
      {
        j = 0;
        bool = false;
        break;
      }
      int m = (int)paramMotionEvent.getX(k);
      int n = (int)paramMotionEvent.getY(k);
      int i1 = pointToPosition(m, n);
      if (i1 == -1)
      {
        j = 1;
        break;
      }
      View localView = getChildAt(i1 - getFirstVisiblePosition());
      setPressedItem(localView, i1, m, n);
      bool = true;
      j = 0;
      if (i != 1) {
        break;
      }
      clickPressedItem(localView, i1);
      j = 0;
      break;
    }
    mScrollHelper.setEnabled(false);
    return bool;
  }
  
  void setListSelectionHidden(boolean paramBoolean)
  {
    mListSelectionHidden = paramBoolean;
  }
  
  protected boolean touchModeDrawsInPressedStateCompat()
  {
    return (mDrawsInPressedState) || (super.touchModeDrawsInPressedStateCompat());
  }
}
