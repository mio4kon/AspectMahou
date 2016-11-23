package android.support.v4.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.TextView;

public class PagerTabStrip
  extends PagerTitleStrip
{
  private static final int FULL_UNDERLINE_HEIGHT = 1;
  private static final int INDICATOR_HEIGHT = 3;
  private static final int MIN_PADDING_BOTTOM = 6;
  private static final int MIN_STRIP_HEIGHT = 32;
  private static final int MIN_TEXT_SPACING = 64;
  private static final int TAB_PADDING = 16;
  private static final int TAB_SPACING = 32;
  private static final String TAG = "PagerTabStrip";
  private boolean mDrawFullUnderline = false;
  private boolean mDrawFullUnderlineSet = false;
  private int mFullUnderlineHeight;
  private boolean mIgnoreTap;
  private int mIndicatorColor = mTextColor;
  private int mIndicatorHeight;
  private float mInitialMotionX;
  private float mInitialMotionY;
  private int mMinPaddingBottom;
  private int mMinStripHeight;
  private int mMinTextSpacing;
  private int mTabAlpha = 255;
  private int mTabPadding;
  private final Paint mTabPaint = new Paint();
  private final Rect mTempRect = new Rect();
  private int mTouchSlop;
  
  public PagerTabStrip(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public PagerTabStrip(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    mTabPaint.setColor(mIndicatorColor);
    float f = getResourcesgetDisplayMetricsdensity;
    mIndicatorHeight = ((int)(0.5F + 3.0F * f));
    mMinPaddingBottom = ((int)(0.5F + 6.0F * f));
    mMinTextSpacing = ((int)(64.0F * f));
    mTabPadding = ((int)(0.5F + 16.0F * f));
    mFullUnderlineHeight = ((int)(0.5F + 1.0F * f));
    mMinStripHeight = ((int)(0.5F + 32.0F * f));
    mTouchSlop = ViewConfiguration.get(paramContext).getScaledTouchSlop();
    setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());
    setTextSpacing(getTextSpacing());
    setWillNotDraw(false);
    mPrevText.setFocusable(true);
    mPrevText.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        mPager.setCurrentItem(-1 + mPager.getCurrentItem());
      }
    });
    mNextText.setFocusable(true);
    mNextText.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        mPager.setCurrentItem(1 + mPager.getCurrentItem());
      }
    });
    if (getBackground() == null) {
      mDrawFullUnderline = true;
    }
  }
  
  public boolean getDrawFullUnderline()
  {
    return mDrawFullUnderline;
  }
  
  int getMinHeight()
  {
    return Math.max(super.getMinHeight(), mMinStripHeight);
  }
  
  @ColorInt
  public int getTabIndicatorColor()
  {
    return mIndicatorColor;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int i = getHeight();
    int j = mCurrText.getLeft() - mTabPadding;
    int k = mCurrText.getRight() + mTabPadding;
    int m = i - mIndicatorHeight;
    mTabPaint.setColor(mTabAlpha << 24 | 0xFFFFFF & mIndicatorColor);
    paramCanvas.drawRect(j, m, k, i, mTabPaint);
    if (mDrawFullUnderline)
    {
      mTabPaint.setColor(0xFF000000 | 0xFFFFFF & mIndicatorColor);
      paramCanvas.drawRect(getPaddingLeft(), i - mFullUnderlineHeight, getWidth() - getPaddingRight(), i, mTabPaint);
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    if ((i != 0) && (mIgnoreTap)) {
      return false;
    }
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    switch (i)
    {
    }
    for (;;)
    {
      return true;
      mInitialMotionX = f1;
      mInitialMotionY = f2;
      mIgnoreTap = false;
      continue;
      if ((Math.abs(f1 - mInitialMotionX) > mTouchSlop) || (Math.abs(f2 - mInitialMotionY) > mTouchSlop))
      {
        mIgnoreTap = true;
        continue;
        if (f1 < mCurrText.getLeft() - mTabPadding) {
          mPager.setCurrentItem(-1 + mPager.getCurrentItem());
        } else if (f1 > mCurrText.getRight() + mTabPadding) {
          mPager.setCurrentItem(1 + mPager.getCurrentItem());
        }
      }
    }
  }
  
  public void setBackgroundColor(@ColorInt int paramInt)
  {
    super.setBackgroundColor(paramInt);
    if (!mDrawFullUnderlineSet) {
      if ((0xFF000000 & paramInt) != 0) {
        break label27;
      }
    }
    label27:
    for (boolean bool = true;; bool = false)
    {
      mDrawFullUnderline = bool;
      return;
    }
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    super.setBackgroundDrawable(paramDrawable);
    if (!mDrawFullUnderlineSet) {
      if (paramDrawable != null) {
        break label24;
      }
    }
    label24:
    for (boolean bool = true;; bool = false)
    {
      mDrawFullUnderline = bool;
      return;
    }
  }
  
  public void setBackgroundResource(@DrawableRes int paramInt)
  {
    super.setBackgroundResource(paramInt);
    if (!mDrawFullUnderlineSet) {
      if (paramInt != 0) {
        break label24;
      }
    }
    label24:
    for (boolean bool = true;; bool = false)
    {
      mDrawFullUnderline = bool;
      return;
    }
  }
  
  public void setDrawFullUnderline(boolean paramBoolean)
  {
    mDrawFullUnderline = paramBoolean;
    mDrawFullUnderlineSet = true;
    invalidate();
  }
  
  public void setPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramInt4 < mMinPaddingBottom) {
      paramInt4 = mMinPaddingBottom;
    }
    super.setPadding(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void setTabIndicatorColor(@ColorInt int paramInt)
  {
    mIndicatorColor = paramInt;
    mTabPaint.setColor(mIndicatorColor);
    invalidate();
  }
  
  public void setTabIndicatorColorResource(@ColorRes int paramInt)
  {
    setTabIndicatorColor(getContext().getResources().getColor(paramInt));
  }
  
  public void setTextSpacing(int paramInt)
  {
    if (paramInt < mMinTextSpacing) {
      paramInt = mMinTextSpacing;
    }
    super.setTextSpacing(paramInt);
  }
  
  void updateTextPositions(int paramInt, float paramFloat, boolean paramBoolean)
  {
    Rect localRect = mTempRect;
    int i = getHeight();
    int j = mCurrText.getLeft() - mTabPadding;
    int k = mCurrText.getRight() + mTabPadding;
    int m = i - mIndicatorHeight;
    localRect.set(j, m, k, i);
    super.updateTextPositions(paramInt, paramFloat, paramBoolean);
    mTabAlpha = ((int)(255.0F * (2.0F * Math.abs(paramFloat - 0.5F))));
    localRect.union(mCurrText.getLeft() - mTabPadding, m, mCurrText.getRight() + mTabPadding, i);
    invalidate(localRect);
  }
}
