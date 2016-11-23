package android.support.v7.widget;

import android.os.SystemClock;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.view.menu.ShowableListMenu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewParent;

public abstract class ForwardingListener
  implements View.OnTouchListener
{
  private int mActivePointerId;
  private Runnable mDisallowIntercept;
  private boolean mForwarding;
  private final int mLongPressTimeout;
  private final float mScaledTouchSlop;
  final View mSrc;
  private final int mTapTimeout;
  private final int[] mTmpLocation = new int[2];
  private Runnable mTriggerLongPress;
  
  public ForwardingListener(View paramView)
  {
    mSrc = paramView;
    mScaledTouchSlop = ViewConfiguration.get(paramView.getContext()).getScaledTouchSlop();
    mTapTimeout = ViewConfiguration.getTapTimeout();
    mLongPressTimeout = ((mTapTimeout + ViewConfiguration.getLongPressTimeout()) / 2);
  }
  
  private void clearCallbacks()
  {
    if (mTriggerLongPress != null) {
      mSrc.removeCallbacks(mTriggerLongPress);
    }
    if (mDisallowIntercept != null) {
      mSrc.removeCallbacks(mDisallowIntercept);
    }
  }
  
  private boolean onTouchForwarded(MotionEvent paramMotionEvent)
  {
    int i = 1;
    View localView = mSrc;
    ShowableListMenu localShowableListMenu = getPopup();
    if ((localShowableListMenu == null) || (!localShowableListMenu.isShowing())) {}
    DropDownListView localDropDownListView;
    do
    {
      return false;
      localDropDownListView = (DropDownListView)localShowableListMenu.getListView();
    } while ((localDropDownListView == null) || (!localDropDownListView.isShown()));
    MotionEvent localMotionEvent = MotionEvent.obtainNoHistory(paramMotionEvent);
    toGlobalMotionEvent(localView, localMotionEvent);
    toLocalMotionEvent(localDropDownListView, localMotionEvent);
    boolean bool = localDropDownListView.onForwardedEvent(localMotionEvent, mActivePointerId);
    localMotionEvent.recycle();
    int j = MotionEventCompat.getActionMasked(paramMotionEvent);
    if ((j != i) && (j != 3))
    {
      int k = i;
      if ((!bool) || (k == 0)) {
        break label135;
      }
    }
    for (;;)
    {
      return i;
      int m = 0;
      break;
      label135:
      i = 0;
    }
  }
  
  private boolean onTouchObserved(MotionEvent paramMotionEvent)
  {
    View localView = mSrc;
    if (!localView.isEnabled()) {}
    int i;
    do
    {
      return false;
      switch (MotionEventCompat.getActionMasked(paramMotionEvent))
      {
      default: 
        return false;
      case 0: 
        mActivePointerId = paramMotionEvent.getPointerId(0);
        if (mDisallowIntercept == null) {
          mDisallowIntercept = new DisallowIntercept();
        }
        localView.postDelayed(mDisallowIntercept, mTapTimeout);
        if (mTriggerLongPress == null) {
          mTriggerLongPress = new TriggerLongPress();
        }
        localView.postDelayed(mTriggerLongPress, mLongPressTimeout);
        return false;
      case 2: 
        i = paramMotionEvent.findPointerIndex(mActivePointerId);
      }
    } while ((i < 0) || (pointInView(localView, paramMotionEvent.getX(i), paramMotionEvent.getY(i), mScaledTouchSlop)));
    clearCallbacks();
    localView.getParent().requestDisallowInterceptTouchEvent(true);
    return true;
    clearCallbacks();
    return false;
  }
  
  private static boolean pointInView(View paramView, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return (paramFloat1 >= -paramFloat3) && (paramFloat2 >= -paramFloat3) && (paramFloat1 < paramFloat3 + (paramView.getRight() - paramView.getLeft())) && (paramFloat2 < paramFloat3 + (paramView.getBottom() - paramView.getTop()));
  }
  
  private boolean toGlobalMotionEvent(View paramView, MotionEvent paramMotionEvent)
  {
    int[] arrayOfInt = mTmpLocation;
    paramView.getLocationOnScreen(arrayOfInt);
    paramMotionEvent.offsetLocation(arrayOfInt[0], arrayOfInt[1]);
    return true;
  }
  
  private boolean toLocalMotionEvent(View paramView, MotionEvent paramMotionEvent)
  {
    int[] arrayOfInt = mTmpLocation;
    paramView.getLocationOnScreen(arrayOfInt);
    paramMotionEvent.offsetLocation(-arrayOfInt[0], -arrayOfInt[1]);
    return true;
  }
  
  public abstract ShowableListMenu getPopup();
  
  protected boolean onForwardingStarted()
  {
    ShowableListMenu localShowableListMenu = getPopup();
    if ((localShowableListMenu != null) && (!localShowableListMenu.isShowing())) {
      localShowableListMenu.show();
    }
    return true;
  }
  
  protected boolean onForwardingStopped()
  {
    ShowableListMenu localShowableListMenu = getPopup();
    if ((localShowableListMenu != null) && (localShowableListMenu.isShowing())) {
      localShowableListMenu.dismiss();
    }
    return true;
  }
  
  void onLongPress()
  {
    clearCallbacks();
    View localView = mSrc;
    if ((!localView.isEnabled()) || (localView.isLongClickable())) {}
    while (!onForwardingStarted()) {
      return;
    }
    localView.getParent().requestDisallowInterceptTouchEvent(true);
    long l = SystemClock.uptimeMillis();
    MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
    localView.onTouchEvent(localMotionEvent);
    localMotionEvent.recycle();
    mForwarding = true;
  }
  
  public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
  {
    boolean bool1 = mForwarding;
    boolean bool2;
    if (bool1) {
      if ((onTouchForwarded(paramMotionEvent)) || (!onForwardingStopped())) {
        bool2 = true;
      }
    }
    label120:
    for (;;)
    {
      mForwarding = bool2;
      boolean bool3;
      if (!bool2)
      {
        bool3 = false;
        if (!bool1) {}
      }
      else
      {
        bool3 = true;
      }
      return bool3;
      bool2 = false;
      continue;
      if ((onTouchObserved(paramMotionEvent)) && (onForwardingStarted())) {}
      for (bool2 = true;; bool2 = false)
      {
        if (!bool2) {
          break label120;
        }
        long l = SystemClock.uptimeMillis();
        MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
        mSrc.onTouchEvent(localMotionEvent);
        localMotionEvent.recycle();
        break;
      }
    }
  }
  
  private class DisallowIntercept
    implements Runnable
  {
    DisallowIntercept() {}
    
    public void run()
    {
      ViewParent localViewParent = mSrc.getParent();
      if (localViewParent != null) {
        localViewParent.requestDisallowInterceptTouchEvent(true);
      }
    }
  }
  
  private class TriggerLongPress
    implements Runnable
  {
    TriggerLongPress() {}
    
    public void run()
    {
      onLongPress();
    }
  }
}
