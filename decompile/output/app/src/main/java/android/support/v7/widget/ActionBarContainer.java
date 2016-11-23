package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v7.appcompat.R.id;
import android.support.v7.appcompat.R.styleable;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class ActionBarContainer
  extends FrameLayout
{
  private View mActionBarView;
  Drawable mBackground;
  private View mContextView;
  private int mHeight;
  boolean mIsSplit;
  boolean mIsStacked;
  private boolean mIsTransitioning;
  Drawable mSplitBackground;
  Drawable mStackedBackground;
  private View mTabContainer;
  
  public ActionBarContainer(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ActionBarContainer(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    Object localObject;
    if (Build.VERSION.SDK_INT >= 21)
    {
      localObject = new ActionBarBackgroundDrawableV21(this);
      setBackgroundDrawable((Drawable)localObject);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ActionBar);
      mBackground = localTypedArray.getDrawable(R.styleable.ActionBar_background);
      mStackedBackground = localTypedArray.getDrawable(R.styleable.ActionBar_backgroundStacked);
      mHeight = localTypedArray.getDimensionPixelSize(R.styleable.ActionBar_height, -1);
      if (getId() == R.id.split_action_bar)
      {
        mIsSplit = bool;
        mSplitBackground = localTypedArray.getDrawable(R.styleable.ActionBar_backgroundSplit);
      }
      localTypedArray.recycle();
      if (!mIsSplit) {
        break label149;
      }
      if (mSplitBackground != null) {
        break label144;
      }
    }
    for (;;)
    {
      setWillNotDraw(bool);
      return;
      localObject = new ActionBarBackgroundDrawable(this);
      break;
      label144:
      bool = false;
      continue;
      label149:
      if ((mBackground != null) || (mStackedBackground != null)) {
        bool = false;
      }
    }
  }
  
  private int getMeasuredHeightWithMargins(View paramView)
  {
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)paramView.getLayoutParams();
    return paramView.getMeasuredHeight() + topMargin + bottomMargin;
  }
  
  private boolean isCollapsed(View paramView)
  {
    return (paramView == null) || (paramView.getVisibility() == 8) || (paramView.getMeasuredHeight() == 0);
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    if ((mBackground != null) && (mBackground.isStateful())) {
      mBackground.setState(getDrawableState());
    }
    if ((mStackedBackground != null) && (mStackedBackground.isStateful())) {
      mStackedBackground.setState(getDrawableState());
    }
    if ((mSplitBackground != null) && (mSplitBackground.isStateful())) {
      mSplitBackground.setState(getDrawableState());
    }
  }
  
  public View getTabContainer()
  {
    return mTabContainer;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    if (Build.VERSION.SDK_INT >= 11)
    {
      super.jumpDrawablesToCurrentState();
      if (mBackground != null) {
        mBackground.jumpToCurrentState();
      }
      if (mStackedBackground != null) {
        mStackedBackground.jumpToCurrentState();
      }
      if (mSplitBackground != null) {
        mSplitBackground.jumpToCurrentState();
      }
    }
  }
  
  public void onFinishInflate()
  {
    super.onFinishInflate();
    mActionBarView = findViewById(R.id.action_bar);
    mContextView = findViewById(R.id.action_context_bar);
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    return (mIsTransitioning) || (super.onInterceptTouchEvent(paramMotionEvent));
  }
  
  public void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    View localView = mTabContainer;
    if ((localView != null) && (localView.getVisibility() != 8)) {}
    for (boolean bool = true;; bool = false)
    {
      if ((localView != null) && (localView.getVisibility() != 8))
      {
        int j = getMeasuredHeight();
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
        localView.layout(paramInt1, j - localView.getMeasuredHeight() - bottomMargin, paramInt3, j - bottomMargin);
      }
      if (!mIsSplit) {
        break;
      }
      Drawable localDrawable2 = mSplitBackground;
      i = 0;
      if (localDrawable2 != null)
      {
        mSplitBackground.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        i = 1;
      }
      if (i != 0) {
        invalidate();
      }
      return;
    }
    Drawable localDrawable1 = mBackground;
    int i = 0;
    if (localDrawable1 != null)
    {
      if (mActionBarView.getVisibility() != 0) {
        break label266;
      }
      mBackground.setBounds(mActionBarView.getLeft(), mActionBarView.getTop(), mActionBarView.getRight(), mActionBarView.getBottom());
    }
    for (;;)
    {
      i = 1;
      mIsStacked = bool;
      if ((!bool) || (mStackedBackground == null)) {
        break;
      }
      mStackedBackground.setBounds(localView.getLeft(), localView.getTop(), localView.getRight(), localView.getBottom());
      i = 1;
      break;
      label266:
      if ((mContextView != null) && (mContextView.getVisibility() == 0)) {
        mBackground.setBounds(mContextView.getLeft(), mContextView.getTop(), mContextView.getRight(), mContextView.getBottom());
      } else {
        mBackground.setBounds(0, 0, 0, 0);
      }
    }
  }
  
  public void onMeasure(int paramInt1, int paramInt2)
  {
    if ((mActionBarView == null) && (View.MeasureSpec.getMode(paramInt2) == Integer.MIN_VALUE) && (mHeight >= 0)) {
      paramInt2 = View.MeasureSpec.makeMeasureSpec(Math.min(mHeight, View.MeasureSpec.getSize(paramInt2)), Integer.MIN_VALUE);
    }
    super.onMeasure(paramInt1, paramInt2);
    if (mActionBarView == null) {}
    int i;
    do
    {
      return;
      i = View.MeasureSpec.getMode(paramInt2);
    } while ((mTabContainer == null) || (mTabContainer.getVisibility() == 8) || (i == 1073741824));
    int j;
    if (!isCollapsed(mActionBarView))
    {
      j = getMeasuredHeightWithMargins(mActionBarView);
      if (i != Integer.MIN_VALUE) {
        break label172;
      }
    }
    label172:
    for (int k = View.MeasureSpec.getSize(paramInt2);; k = Integer.MAX_VALUE)
    {
      setMeasuredDimension(getMeasuredWidth(), Math.min(j + getMeasuredHeightWithMargins(mTabContainer), k));
      return;
      if (!isCollapsed(mContextView))
      {
        j = getMeasuredHeightWithMargins(mContextView);
        break;
      }
      j = 0;
      break;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    super.onTouchEvent(paramMotionEvent);
    return true;
  }
  
  public void setPrimaryBackground(Drawable paramDrawable)
  {
    boolean bool = true;
    if (mBackground != null)
    {
      mBackground.setCallback(null);
      unscheduleDrawable(mBackground);
    }
    mBackground = paramDrawable;
    if (paramDrawable != null)
    {
      paramDrawable.setCallback(this);
      if (mActionBarView != null) {
        mBackground.setBounds(mActionBarView.getLeft(), mActionBarView.getTop(), mActionBarView.getRight(), mActionBarView.getBottom());
      }
    }
    if (mIsSplit) {
      if (mSplitBackground != null) {}
    }
    for (;;)
    {
      setWillNotDraw(bool);
      invalidate();
      return;
      bool = false;
      continue;
      if ((mBackground != null) || (mStackedBackground != null)) {
        bool = false;
      }
    }
  }
  
  public void setSplitBackground(Drawable paramDrawable)
  {
    boolean bool = true;
    if (mSplitBackground != null)
    {
      mSplitBackground.setCallback(null);
      unscheduleDrawable(mSplitBackground);
    }
    mSplitBackground = paramDrawable;
    if (paramDrawable != null)
    {
      paramDrawable.setCallback(this);
      if ((mIsSplit) && (mSplitBackground != null)) {
        mSplitBackground.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
      }
    }
    if (mIsSplit) {
      if (mSplitBackground != null) {}
    }
    for (;;)
    {
      setWillNotDraw(bool);
      invalidate();
      return;
      bool = false;
      continue;
      if ((mBackground != null) || (mStackedBackground != null)) {
        bool = false;
      }
    }
  }
  
  public void setStackedBackground(Drawable paramDrawable)
  {
    boolean bool = true;
    if (mStackedBackground != null)
    {
      mStackedBackground.setCallback(null);
      unscheduleDrawable(mStackedBackground);
    }
    mStackedBackground = paramDrawable;
    if (paramDrawable != null)
    {
      paramDrawable.setCallback(this);
      if ((mIsStacked) && (mStackedBackground != null)) {
        mStackedBackground.setBounds(mTabContainer.getLeft(), mTabContainer.getTop(), mTabContainer.getRight(), mTabContainer.getBottom());
      }
    }
    if (mIsSplit) {
      if (mSplitBackground != null) {}
    }
    for (;;)
    {
      setWillNotDraw(bool);
      invalidate();
      return;
      bool = false;
      continue;
      if ((mBackground != null) || (mStackedBackground != null)) {
        bool = false;
      }
    }
  }
  
  public void setTabContainer(ScrollingTabContainerView paramScrollingTabContainerView)
  {
    if (mTabContainer != null) {
      removeView(mTabContainer);
    }
    mTabContainer = paramScrollingTabContainerView;
    if (paramScrollingTabContainerView != null)
    {
      addView(paramScrollingTabContainerView);
      ViewGroup.LayoutParams localLayoutParams = paramScrollingTabContainerView.getLayoutParams();
      width = -1;
      height = -2;
      paramScrollingTabContainerView.setAllowCollapse(false);
    }
  }
  
  public void setTransitioning(boolean paramBoolean)
  {
    mIsTransitioning = paramBoolean;
    if (paramBoolean) {}
    for (int i = 393216;; i = 262144)
    {
      setDescendantFocusability(i);
      return;
    }
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if (paramInt == 0) {}
    for (boolean bool = true;; bool = false)
    {
      if (mBackground != null) {
        mBackground.setVisible(bool, false);
      }
      if (mStackedBackground != null) {
        mStackedBackground.setVisible(bool, false);
      }
      if (mSplitBackground != null) {
        mSplitBackground.setVisible(bool, false);
      }
      return;
    }
  }
  
  public ActionMode startActionModeForChild(View paramView, ActionMode.Callback paramCallback)
  {
    return null;
  }
  
  public ActionMode startActionModeForChild(View paramView, ActionMode.Callback paramCallback, int paramInt)
  {
    if (paramInt != 0) {
      return super.startActionModeForChild(paramView, paramCallback, paramInt);
    }
    return null;
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return ((paramDrawable == mBackground) && (!mIsSplit)) || ((paramDrawable == mStackedBackground) && (mIsStacked)) || ((paramDrawable == mSplitBackground) && (mIsSplit)) || (super.verifyDrawable(paramDrawable));
  }
}
