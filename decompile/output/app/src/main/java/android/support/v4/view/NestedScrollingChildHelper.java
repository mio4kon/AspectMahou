package android.support.v4.view;

import android.view.View;
import android.view.ViewParent;

public class NestedScrollingChildHelper
{
  private boolean mIsNestedScrollingEnabled;
  private ViewParent mNestedScrollingParent;
  private int[] mTempNestedScrollConsumed;
  private final View mView;
  
  public NestedScrollingChildHelper(View paramView)
  {
    mView = paramView;
  }
  
  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    if ((isNestedScrollingEnabled()) && (mNestedScrollingParent != null)) {
      return ViewParentCompat.onNestedFling(mNestedScrollingParent, mView, paramFloat1, paramFloat2, paramBoolean);
    }
    return false;
  }
  
  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2)
  {
    if ((isNestedScrollingEnabled()) && (mNestedScrollingParent != null)) {
      return ViewParentCompat.onNestedPreFling(mNestedScrollingParent, mView, paramFloat1, paramFloat2);
    }
    return false;
  }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    boolean bool1 = isNestedScrollingEnabled();
    boolean bool2 = false;
    if (bool1)
    {
      ViewParent localViewParent = mNestedScrollingParent;
      bool2 = false;
      if (localViewParent != null)
      {
        if ((paramInt1 == 0) && (paramInt2 == 0)) {
          break label174;
        }
        int i = 0;
        int j = 0;
        if (paramArrayOfInt2 != null)
        {
          mView.getLocationInWindow(paramArrayOfInt2);
          i = paramArrayOfInt2[0];
          j = paramArrayOfInt2[1];
        }
        if (paramArrayOfInt1 == null)
        {
          if (mTempNestedScrollConsumed == null) {
            mTempNestedScrollConsumed = new int[2];
          }
          paramArrayOfInt1 = mTempNestedScrollConsumed;
        }
        paramArrayOfInt1[0] = 0;
        paramArrayOfInt1[1] = 0;
        ViewParentCompat.onNestedPreScroll(mNestedScrollingParent, mView, paramInt1, paramInt2, paramArrayOfInt1);
        if (paramArrayOfInt2 != null)
        {
          mView.getLocationInWindow(paramArrayOfInt2);
          paramArrayOfInt2[0] -= i;
          paramArrayOfInt2[1] -= j;
        }
        if (paramArrayOfInt1[0] == 0)
        {
          int k = paramArrayOfInt1[1];
          bool2 = false;
          if (k == 0) {}
        }
        else
        {
          bool2 = true;
        }
      }
    }
    label174:
    do
    {
      return bool2;
      bool2 = false;
    } while (paramArrayOfInt2 == null);
    paramArrayOfInt2[0] = 0;
    paramArrayOfInt2[1] = 0;
    return false;
  }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    if ((isNestedScrollingEnabled()) && (mNestedScrollingParent != null))
    {
      if ((paramInt1 != 0) || (paramInt2 != 0) || (paramInt3 != 0) || (paramInt4 != 0))
      {
        int i = 0;
        int j = 0;
        if (paramArrayOfInt != null)
        {
          mView.getLocationInWindow(paramArrayOfInt);
          i = paramArrayOfInt[0];
          j = paramArrayOfInt[1];
        }
        ViewParentCompat.onNestedScroll(mNestedScrollingParent, mView, paramInt1, paramInt2, paramInt3, paramInt4);
        if (paramArrayOfInt != null)
        {
          mView.getLocationInWindow(paramArrayOfInt);
          paramArrayOfInt[0] -= i;
          paramArrayOfInt[1] -= j;
        }
        return true;
      }
      if (paramArrayOfInt != null)
      {
        paramArrayOfInt[0] = 0;
        paramArrayOfInt[1] = 0;
      }
    }
    return false;
  }
  
  public boolean hasNestedScrollingParent()
  {
    return mNestedScrollingParent != null;
  }
  
  public boolean isNestedScrollingEnabled()
  {
    return mIsNestedScrollingEnabled;
  }
  
  public void onDetachedFromWindow()
  {
    ViewCompat.stopNestedScroll(mView);
  }
  
  public void onStopNestedScroll(View paramView)
  {
    ViewCompat.stopNestedScroll(mView);
  }
  
  public void setNestedScrollingEnabled(boolean paramBoolean)
  {
    if (mIsNestedScrollingEnabled) {
      ViewCompat.stopNestedScroll(mView);
    }
    mIsNestedScrollingEnabled = paramBoolean;
  }
  
  public boolean startNestedScroll(int paramInt)
  {
    if (hasNestedScrollingParent()) {
      return true;
    }
    if (isNestedScrollingEnabled())
    {
      ViewParent localViewParent = mView.getParent();
      View localView = mView;
      while (localViewParent != null)
      {
        if (ViewParentCompat.onStartNestedScroll(localViewParent, localView, mView, paramInt))
        {
          mNestedScrollingParent = localViewParent;
          ViewParentCompat.onNestedScrollAccepted(localViewParent, localView, mView, paramInt);
          return true;
        }
        if ((localViewParent instanceof View)) {
          localView = (View)localViewParent;
        }
        localViewParent = localViewParent.getParent();
      }
    }
    return false;
  }
  
  public void stopNestedScroll()
  {
    if (mNestedScrollingParent != null)
    {
      ViewParentCompat.onStopNestedScroll(mNestedScrollingParent, mView);
      mNestedScrollingParent = null;
    }
  }
}
