package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SlidingPaneLayout
  extends ViewGroup
{
  private static final int DEFAULT_FADE_COLOR = -858993460;
  private static final int DEFAULT_OVERHANG_SIZE = 32;
  static final SlidingPanelLayoutImpl IMPL = new SlidingPanelLayoutImplBase();
  private static final int MIN_FLING_VELOCITY = 400;
  private static final String TAG = "SlidingPaneLayout";
  private boolean mCanSlide;
  private int mCoveredFadeColor;
  final ViewDragHelper mDragHelper;
  private boolean mFirstLayout = true;
  private float mInitialMotionX;
  private float mInitialMotionY;
  boolean mIsUnableToDrag;
  private final int mOverhangSize;
  private PanelSlideListener mPanelSlideListener;
  private int mParallaxBy;
  private float mParallaxOffset;
  final ArrayList<DisableLayerRunnable> mPostedRunnables = new ArrayList();
  boolean mPreservedOpenState;
  private Drawable mShadowDrawableLeft;
  private Drawable mShadowDrawableRight;
  float mSlideOffset;
  int mSlideRange;
  View mSlideableView;
  private int mSliderFadeColor = -858993460;
  private final Rect mTmpRect = new Rect();
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 17)
    {
      IMPL = new SlidingPanelLayoutImplJBMR1();
      return;
    }
    if (i >= 16)
    {
      IMPL = new SlidingPanelLayoutImplJB();
      return;
    }
  }
  
  public SlidingPaneLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SlidingPaneLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public SlidingPaneLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    float f = getResourcesgetDisplayMetricsdensity;
    mOverhangSize = ((int)(0.5F + 32.0F * f));
    ViewConfiguration.get(paramContext);
    setWillNotDraw(false);
    ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
    ViewCompat.setImportantForAccessibility(this, 1);
    mDragHelper = ViewDragHelper.create(this, 0.5F, new DragHelperCallback());
    mDragHelper.setMinVelocity(400.0F * f);
  }
  
  private boolean closePane(View paramView, int paramInt)
  {
    boolean bool1;
    if (!mFirstLayout)
    {
      boolean bool2 = smoothSlideTo(0.0F, paramInt);
      bool1 = false;
      if (!bool2) {}
    }
    else
    {
      mPreservedOpenState = false;
      bool1 = true;
    }
    return bool1;
  }
  
  private void dimChildView(View paramView, float paramFloat, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if ((paramFloat > 0.0F) && (paramInt != 0))
    {
      i = (int)(paramFloat * ((0xFF000000 & paramInt) >>> 24)) << 24 | 0xFFFFFF & paramInt;
      if (dimPaint == null) {
        dimPaint = new Paint();
      }
      dimPaint.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_OVER));
      if (ViewCompat.getLayerType(paramView) != 2) {
        ViewCompat.setLayerType(paramView, 2, dimPaint);
      }
      invalidateChildRegion(paramView);
    }
    while (ViewCompat.getLayerType(paramView) == 0)
    {
      int i;
      return;
    }
    if (dimPaint != null) {
      dimPaint.setColorFilter(null);
    }
    DisableLayerRunnable localDisableLayerRunnable = new DisableLayerRunnable(paramView);
    mPostedRunnables.add(localDisableLayerRunnable);
    ViewCompat.postOnAnimation(this, localDisableLayerRunnable);
  }
  
  private boolean openPane(View paramView, int paramInt)
  {
    if ((mFirstLayout) || (smoothSlideTo(1.0F, paramInt)))
    {
      mPreservedOpenState = true;
      return true;
    }
    return false;
  }
  
  private void parallaxOtherViews(float paramFloat)
  {
    boolean bool = isLayoutRtlSupport();
    LayoutParams localLayoutParams = (LayoutParams)mSlideableView.getLayoutParams();
    int i1;
    int i;
    label41:
    int k;
    label50:
    View localView;
    if (dimWhenOffset) {
      if (bool)
      {
        i1 = rightMargin;
        if (i1 > 0) {
          break label89;
        }
        i = 1;
        int j = getChildCount();
        k = 0;
        if (k >= j) {
          return;
        }
        localView = getChildAt(k);
        if (localView != mSlideableView) {
          break label95;
        }
      }
    }
    label89:
    label95:
    do
    {
      k++;
      break label50;
      i1 = leftMargin;
      break;
      i = 0;
      break label41;
      int m = (int)((1.0F - mParallaxOffset) * mParallaxBy);
      mParallaxOffset = paramFloat;
      int n = m - (int)((1.0F - paramFloat) * mParallaxBy);
      if (bool) {
        n = -n;
      }
      localView.offsetLeftAndRight(n);
    } while (i == 0);
    if (bool) {}
    for (float f = mParallaxOffset - 1.0F;; f = 1.0F - mParallaxOffset)
    {
      dimChildView(localView, f, mCoveredFadeColor);
      break;
    }
  }
  
  private static boolean viewIsOpaque(View paramView)
  {
    if (paramView.isOpaque()) {}
    Drawable localDrawable;
    do
    {
      return true;
      if (Build.VERSION.SDK_INT >= 18) {
        return false;
      }
      localDrawable = paramView.getBackground();
      if (localDrawable == null) {
        break;
      }
    } while (localDrawable.getOpacity() == -1);
    return false;
    return false;
  }
  
  protected boolean canScroll(View paramView, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramView instanceof ViewGroup))
    {
      ViewGroup localViewGroup = (ViewGroup)paramView;
      int i = paramView.getScrollX();
      int j = paramView.getScrollY();
      for (int k = -1 + localViewGroup.getChildCount(); k >= 0; k--)
      {
        View localView = localViewGroup.getChildAt(k);
        if ((paramInt2 + i >= localView.getLeft()) && (paramInt2 + i < localView.getRight()) && (paramInt3 + j >= localView.getTop()) && (paramInt3 + j < localView.getBottom()))
        {
          int m = paramInt2 + i - localView.getLeft();
          int n = paramInt3 + j - localView.getTop();
          if (canScroll(localView, true, paramInt1, m, n)) {
            return true;
          }
        }
      }
    }
    if (paramBoolean)
    {
      if (isLayoutRtlSupport()) {}
      while (ViewCompat.canScrollHorizontally(paramView, paramInt1))
      {
        return true;
        paramInt1 = -paramInt1;
      }
    }
    return false;
  }
  
  @Deprecated
  public boolean canSlide()
  {
    return mCanSlide;
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return ((paramLayoutParams instanceof LayoutParams)) && (super.checkLayoutParams(paramLayoutParams));
  }
  
  public boolean closePane()
  {
    return closePane(mSlideableView, 0);
  }
  
  public void computeScroll()
  {
    if (mDragHelper.continueSettling(true))
    {
      if (!mCanSlide) {
        mDragHelper.abort();
      }
    }
    else {
      return;
    }
    ViewCompat.postInvalidateOnAnimation(this);
  }
  
  void dispatchOnPanelClosed(View paramView)
  {
    if (mPanelSlideListener != null) {
      mPanelSlideListener.onPanelClosed(paramView);
    }
    sendAccessibilityEvent(32);
  }
  
  void dispatchOnPanelOpened(View paramView)
  {
    if (mPanelSlideListener != null) {
      mPanelSlideListener.onPanelOpened(paramView);
    }
    sendAccessibilityEvent(32);
  }
  
  void dispatchOnPanelSlide(View paramView)
  {
    if (mPanelSlideListener != null) {
      mPanelSlideListener.onPanelSlide(paramView, mSlideOffset);
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    Drawable localDrawable;
    if (isLayoutRtlSupport())
    {
      localDrawable = mShadowDrawableRight;
      if (getChildCount() <= 1) {
        break label48;
      }
    }
    label48:
    for (View localView = getChildAt(1);; localView = null)
    {
      if ((localView != null) && (localDrawable != null)) {
        break label53;
      }
      return;
      localDrawable = mShadowDrawableLeft;
      break;
    }
    label53:
    int i = localView.getTop();
    int j = localView.getBottom();
    int k = localDrawable.getIntrinsicWidth();
    int n;
    int m;
    if (isLayoutRtlSupport())
    {
      n = localView.getRight();
      m = n + k;
    }
    for (;;)
    {
      localDrawable.setBounds(n, i, m, j);
      localDrawable.draw(paramCanvas);
      return;
      m = localView.getLeft();
      n = m - k;
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = paramCanvas.save(2);
    boolean bool;
    if ((mCanSlide) && (!slideable) && (mSlideableView != null))
    {
      paramCanvas.getClipBounds(mTmpRect);
      if (isLayoutRtlSupport())
      {
        mTmpRect.left = Math.max(mTmpRect.left, mSlideableView.getRight());
        paramCanvas.clipRect(mTmpRect);
      }
    }
    else
    {
      if (Build.VERSION.SDK_INT < 11) {
        break label140;
      }
      bool = super.drawChild(paramCanvas, paramView, paramLong);
    }
    for (;;)
    {
      paramCanvas.restoreToCount(i);
      return bool;
      mTmpRect.right = Math.min(mTmpRect.right, mSlideableView.getLeft());
      break;
      label140:
      if ((dimWhenOffset) && (mSlideOffset > 0.0F))
      {
        if (!paramView.isDrawingCacheEnabled()) {
          paramView.setDrawingCacheEnabled(true);
        }
        Bitmap localBitmap = paramView.getDrawingCache();
        if (localBitmap != null)
        {
          paramCanvas.drawBitmap(localBitmap, paramView.getLeft(), paramView.getTop(), dimPaint);
          bool = false;
        }
        else
        {
          Log.e("SlidingPaneLayout", "drawChild: child view " + paramView + " returned null drawing cache");
          bool = super.drawChild(paramCanvas, paramView, paramLong);
        }
      }
      else
      {
        if (paramView.isDrawingCacheEnabled()) {
          paramView.setDrawingCacheEnabled(false);
        }
        bool = super.drawChild(paramCanvas, paramView, paramLong);
      }
    }
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams();
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
      return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  @ColorInt
  public int getCoveredFadeColor()
  {
    return mCoveredFadeColor;
  }
  
  public int getParallaxDistance()
  {
    return mParallaxBy;
  }
  
  @ColorInt
  public int getSliderFadeColor()
  {
    return mSliderFadeColor;
  }
  
  void invalidateChildRegion(View paramView)
  {
    IMPL.invalidateChildRegion(this, paramView);
  }
  
  boolean isDimmed(View paramView)
  {
    if (paramView == null) {}
    LayoutParams localLayoutParams;
    do
    {
      return false;
      localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    } while ((!mCanSlide) || (!dimWhenOffset) || (mSlideOffset <= 0.0F));
    return true;
  }
  
  boolean isLayoutRtlSupport()
  {
    return ViewCompat.getLayoutDirection(this) == 1;
  }
  
  public boolean isOpen()
  {
    return (!mCanSlide) || (mSlideOffset == 1.0F);
  }
  
  public boolean isSlideable()
  {
    return mCanSlide;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    mFirstLayout = true;
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    mFirstLayout = true;
    int i = 0;
    int j = mPostedRunnables.size();
    while (i < j)
    {
      ((DisableLayerRunnable)mPostedRunnables.get(i)).run();
      i++;
    }
    mPostedRunnables.clear();
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if ((!mCanSlide) && (i == 0) && (getChildCount() > 1))
    {
      View localView = getChildAt(1);
      if (localView != null) {
        if (mDragHelper.isViewUnder(localView, (int)paramMotionEvent.getX(), (int)paramMotionEvent.getY())) {
          break label98;
        }
      }
    }
    label98:
    for (boolean bool5 = true;; bool5 = false)
    {
      mPreservedOpenState = bool5;
      if ((mCanSlide) && ((!mIsUnableToDrag) || (i == 0))) {
        break;
      }
      mDragHelper.cancel();
      return super.onInterceptTouchEvent(paramMotionEvent);
    }
    if ((i == 3) || (i == 1))
    {
      mDragHelper.cancel();
      return false;
    }
    int j = 0;
    switch (i)
    {
    }
    while ((mDragHelper.shouldInterceptTouchEvent(paramMotionEvent)) || (j != 0))
    {
      return true;
      mIsUnableToDrag = false;
      float f5 = paramMotionEvent.getX();
      float f6 = paramMotionEvent.getY();
      mInitialMotionX = f5;
      mInitialMotionY = f6;
      boolean bool3 = mDragHelper.isViewUnder(mSlideableView, (int)f5, (int)f6);
      j = 0;
      if (bool3)
      {
        boolean bool4 = isDimmed(mSlideableView);
        j = 0;
        if (bool4)
        {
          j = 1;
          continue;
          float f1 = paramMotionEvent.getX();
          float f2 = paramMotionEvent.getY();
          float f3 = Math.abs(f1 - mInitialMotionX);
          float f4 = Math.abs(f2 - mInitialMotionY);
          boolean bool1 = f3 < mDragHelper.getTouchSlop();
          j = 0;
          if (bool1)
          {
            boolean bool2 = f4 < f3;
            j = 0;
            if (bool2)
            {
              mDragHelper.cancel();
              mIsUnableToDrag = true;
              return false;
            }
          }
        }
      }
    }
    return false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    boolean bool1 = isLayoutRtlSupport();
    int i;
    int j;
    label36:
    int k;
    label47:
    int m;
    int n;
    int i1;
    int i2;
    if (bool1)
    {
      mDragHelper.setEdgeTrackingEnabled(2);
      i = paramInt3 - paramInt1;
      if (!bool1) {
        break label142;
      }
      j = getPaddingRight();
      if (!bool1) {
        break label151;
      }
      k = getPaddingLeft();
      m = getPaddingTop();
      n = getChildCount();
      i1 = j;
      i2 = i1;
      if (mFirstLayout) {
        if ((!mCanSlide) || (!mPreservedOpenState)) {
          break label160;
        }
      }
    }
    View localView;
    label142:
    label151:
    label160:
    for (float f = 1.0F;; f = 0.0F)
    {
      mSlideOffset = f;
      for (int i3 = 0;; i3++)
      {
        if (i3 >= n) {
          break label450;
        }
        localView = getChildAt(i3);
        if (localView.getVisibility() != 8) {
          break;
        }
      }
      mDragHelper.setEdgeTrackingEnabled(1);
      break;
      j = getPaddingLeft();
      break label36;
      k = getPaddingRight();
      break label47;
    }
    LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
    int i5 = localView.getMeasuredWidth();
    int i6 = 0;
    int i12;
    label252:
    boolean bool2;
    label276:
    label317:
    int i8;
    int i7;
    if (slideable)
    {
      int i9 = leftMargin + rightMargin;
      int i10 = i - k - mOverhangSize;
      int i11 = Math.min(i2, i10) - i1 - i9;
      mSlideRange = i11;
      if (bool1)
      {
        i12 = rightMargin;
        if (i11 + (i1 + i12) + i5 / 2 <= i - k) {
          break label381;
        }
        bool2 = true;
        dimWhenOffset = bool2;
        int i13 = (int)(i11 * mSlideOffset);
        i1 += i13 + i12;
        mSlideOffset = (i13 / mSlideRange);
        if (!bool1) {
          break label433;
        }
        i8 = i6 + (i - i1);
        i7 = i8 - i5;
      }
    }
    for (;;)
    {
      localView.layout(i7, m, i8, m + localView.getMeasuredHeight());
      i2 += localView.getWidth();
      break;
      i12 = leftMargin;
      break label252;
      label381:
      bool2 = false;
      break label276;
      if ((mCanSlide) && (mParallaxBy != 0))
      {
        i6 = (int)((1.0F - mSlideOffset) * mParallaxBy);
        i1 = i2;
        break label317;
      }
      i1 = i2;
      i6 = 0;
      break label317;
      label433:
      i7 = i1 - i6;
      i8 = i7 + i5;
    }
    label450:
    if (mFirstLayout)
    {
      if (!mCanSlide) {
        break label525;
      }
      if (mParallaxBy != 0) {
        parallaxOtherViews(mSlideOffset);
      }
      if (mSlideableView.getLayoutParams()).dimWhenOffset) {
        dimChildView(mSlideableView, mSlideOffset, mSliderFadeColor);
      }
    }
    for (;;)
    {
      updateObscuredViewsVisibility(mSlideableView);
      mFirstLayout = false;
      return;
      label525:
      for (int i4 = 0; i4 < n; i4++) {
        dimChildView(getChildAt(i4), 0.0F, mSliderFadeColor);
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt1);
    int k = View.MeasureSpec.getMode(paramInt2);
    int m = View.MeasureSpec.getSize(paramInt2);
    int n;
    int i1;
    label80:
    float f;
    boolean bool1;
    int i2;
    int i3;
    int i4;
    int i5;
    label133:
    View localView2;
    LayoutParams localLayoutParams2;
    if (i != 1073741824) {
      if (isInEditMode()) {
        if (i == Integer.MIN_VALUE)
        {
          n = -1;
          i1 = 0;
          switch (k)
          {
          default: 
            f = 0.0F;
            bool1 = false;
            i2 = j - getPaddingLeft() - getPaddingRight();
            i3 = i2;
            i4 = getChildCount();
            if (i4 > 2) {
              Log.e("SlidingPaneLayout", "onMeasure: More than two child views are not supported.");
            }
            mSlideableView = null;
            i5 = 0;
            if (i5 >= i4) {
              break label547;
            }
            localView2 = getChildAt(i5);
            localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
            if (localView2.getVisibility() == 8) {
              dimWhenOffset = false;
            }
            break;
          }
        }
      }
    }
    do
    {
      i5++;
      break label133;
      if (i != 0) {
        break;
      }
      j = 300;
      break;
      throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
      if (k != 0) {
        break;
      }
      if (isInEditMode())
      {
        if (k != 0) {
          break;
        }
        k = Integer.MIN_VALUE;
        m = 300;
        break;
      }
      throw new IllegalStateException("Height must not be UNSPECIFIED");
      n = m - getPaddingTop() - getPaddingBottom();
      i1 = n;
      break label80;
      n = m - getPaddingTop() - getPaddingBottom();
      i1 = 0;
      break label80;
      if (weight <= 0.0F) {
        break label313;
      }
      f += weight;
    } while (width == 0);
    label313:
    int i15 = leftMargin + rightMargin;
    int i16;
    label349:
    int i17;
    if (width == -2)
    {
      i16 = View.MeasureSpec.makeMeasureSpec(i2 - i15, Integer.MIN_VALUE);
      if (height != -2) {
        break label503;
      }
      i17 = View.MeasureSpec.makeMeasureSpec(n, Integer.MIN_VALUE);
      label369:
      localView2.measure(i16, i17);
      int i18 = localView2.getMeasuredWidth();
      int i19 = localView2.getMeasuredHeight();
      if ((k == Integer.MIN_VALUE) && (i19 > i1)) {
        i1 = Math.min(i19, n);
      }
      i3 -= i18;
      if (i3 >= 0) {
        break label541;
      }
    }
    label503:
    label541:
    for (boolean bool2 = true;; bool2 = false)
    {
      slideable = bool2;
      bool1 |= bool2;
      if (!slideable) {
        break;
      }
      mSlideableView = localView2;
      break;
      if (width == -1)
      {
        i16 = View.MeasureSpec.makeMeasureSpec(i2 - i15, 1073741824);
        break label349;
      }
      i16 = View.MeasureSpec.makeMeasureSpec(width, 1073741824);
      break label349;
      if (height == -1)
      {
        i17 = View.MeasureSpec.makeMeasureSpec(n, 1073741824);
        break label369;
      }
      i17 = View.MeasureSpec.makeMeasureSpec(height, 1073741824);
      break label369;
    }
    label547:
    if ((bool1) || (f > 0.0F))
    {
      int i6 = i2 - mOverhangSize;
      int i7 = 0;
      if (i7 < i4)
      {
        View localView1 = getChildAt(i7);
        if (localView1.getVisibility() == 8) {}
        for (;;)
        {
          i7++;
          break;
          LayoutParams localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
          if (localView1.getVisibility() != 8)
          {
            int i8;
            label643:
            int i9;
            label651:
            int i14;
            if ((width == 0) && (weight > 0.0F))
            {
              i8 = 1;
              if (i8 == 0) {
                break label739;
              }
              i9 = 0;
              if ((!bool1) || (localView1 == mSlideableView)) {
                break label803;
              }
              if ((width >= 0) || ((i9 <= i6) && (weight <= 0.0F))) {
                continue;
              }
              if (i8 == 0) {
                break label787;
              }
              if (height != -2) {
                break label749;
              }
              i14 = View.MeasureSpec.makeMeasureSpec(n, Integer.MIN_VALUE);
            }
            for (;;)
            {
              localView1.measure(View.MeasureSpec.makeMeasureSpec(i6, 1073741824), i14);
              break;
              i8 = 0;
              break label643;
              label739:
              i9 = localView1.getMeasuredWidth();
              break label651;
              label749:
              if (height == -1)
              {
                i14 = View.MeasureSpec.makeMeasureSpec(n, 1073741824);
              }
              else
              {
                i14 = View.MeasureSpec.makeMeasureSpec(height, 1073741824);
                continue;
                label787:
                i14 = View.MeasureSpec.makeMeasureSpec(localView1.getMeasuredHeight(), 1073741824);
              }
            }
            label803:
            if (weight > 0.0F)
            {
              int i10;
              if (width == 0) {
                if (height == -2) {
                  i10 = View.MeasureSpec.makeMeasureSpec(n, Integer.MIN_VALUE);
                }
              }
              for (;;)
              {
                if (!bool1) {
                  break label945;
                }
                int i12 = i2 - (leftMargin + rightMargin);
                int i13 = View.MeasureSpec.makeMeasureSpec(i12, 1073741824);
                if (i9 == i12) {
                  break;
                }
                localView1.measure(i13, i10);
                break;
                if (height == -1)
                {
                  i10 = View.MeasureSpec.makeMeasureSpec(n, 1073741824);
                }
                else
                {
                  i10 = View.MeasureSpec.makeMeasureSpec(height, 1073741824);
                  continue;
                  i10 = View.MeasureSpec.makeMeasureSpec(localView1.getMeasuredHeight(), 1073741824);
                }
              }
              label945:
              int i11 = Math.max(0, i3);
              localView1.measure(View.MeasureSpec.makeMeasureSpec(i9 + (int)(weight * i11 / f), 1073741824), i10);
            }
          }
        }
      }
    }
    setMeasuredDimension(j, i1 + getPaddingTop() + getPaddingBottom());
    mCanSlide = bool1;
    if ((mDragHelper.getViewDragState() != 0) && (!bool1)) {
      mDragHelper.abort();
    }
  }
  
  void onPanelDragged(int paramInt)
  {
    if (mSlideableView == null)
    {
      mSlideOffset = 0.0F;
      return;
    }
    boolean bool = isLayoutRtlSupport();
    LayoutParams localLayoutParams = (LayoutParams)mSlideableView.getLayoutParams();
    int i = mSlideableView.getWidth();
    int j;
    int k;
    if (bool)
    {
      j = getWidth() - paramInt - i;
      if (!bool) {
        break label145;
      }
      k = getPaddingRight();
      label63:
      if (!bool) {
        break label154;
      }
    }
    label145:
    label154:
    for (int m = rightMargin;; m = leftMargin)
    {
      mSlideOffset = ((j - (k + m)) / mSlideRange);
      if (mParallaxBy != 0) {
        parallaxOtherViews(mSlideOffset);
      }
      if (dimWhenOffset) {
        dimChildView(mSlideableView, mSlideOffset, mSliderFadeColor);
      }
      dispatchOnPanelSlide(mSlideableView);
      return;
      j = paramInt;
      break;
      k = getPaddingLeft();
      break label63;
    }
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    SavedState localSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(localSavedState.getSuperState());
    if (isOpen) {
      openPane();
    }
    for (;;)
    {
      mPreservedOpenState = isOpen;
      return;
      closePane();
    }
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    if (isSlideable()) {}
    for (boolean bool = isOpen();; bool = mPreservedOpenState)
    {
      isOpen = bool;
      return localSavedState;
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramInt1 != paramInt3) {
      mFirstLayout = true;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool;
    if (!mCanSlide) {
      bool = super.onTouchEvent(paramMotionEvent);
    }
    float f1;
    float f2;
    float f3;
    float f4;
    int j;
    do
    {
      do
      {
        return bool;
        mDragHelper.processTouchEvent(paramMotionEvent);
        int i = paramMotionEvent.getAction();
        bool = true;
        switch (i & 0xFF)
        {
        default: 
          return bool;
        case 0: 
          float f5 = paramMotionEvent.getX();
          float f6 = paramMotionEvent.getY();
          mInitialMotionX = f5;
          mInitialMotionY = f6;
          return bool;
        }
      } while (!isDimmed(mSlideableView));
      f1 = paramMotionEvent.getX();
      f2 = paramMotionEvent.getY();
      f3 = f1 - mInitialMotionX;
      f4 = f2 - mInitialMotionY;
      j = mDragHelper.getTouchSlop();
    } while ((f3 * f3 + f4 * f4 >= j * j) || (!mDragHelper.isViewUnder(mSlideableView, (int)f1, (int)f2)));
    closePane(mSlideableView, 0);
    return bool;
  }
  
  public boolean openPane()
  {
    return openPane(mSlideableView, 0);
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    super.requestChildFocus(paramView1, paramView2);
    if ((!isInTouchMode()) && (!mCanSlide)) {
      if (paramView1 != mSlideableView) {
        break label36;
      }
    }
    label36:
    for (boolean bool = true;; bool = false)
    {
      mPreservedOpenState = bool;
      return;
    }
  }
  
  void setAllChildrenVisible()
  {
    int i = 0;
    int j = getChildCount();
    while (i < j)
    {
      View localView = getChildAt(i);
      if (localView.getVisibility() == 4) {
        localView.setVisibility(0);
      }
      i++;
    }
  }
  
  public void setCoveredFadeColor(@ColorInt int paramInt)
  {
    mCoveredFadeColor = paramInt;
  }
  
  public void setPanelSlideListener(PanelSlideListener paramPanelSlideListener)
  {
    mPanelSlideListener = paramPanelSlideListener;
  }
  
  public void setParallaxDistance(int paramInt)
  {
    mParallaxBy = paramInt;
    requestLayout();
  }
  
  @Deprecated
  public void setShadowDrawable(Drawable paramDrawable)
  {
    setShadowDrawableLeft(paramDrawable);
  }
  
  public void setShadowDrawableLeft(Drawable paramDrawable)
  {
    mShadowDrawableLeft = paramDrawable;
  }
  
  public void setShadowDrawableRight(Drawable paramDrawable)
  {
    mShadowDrawableRight = paramDrawable;
  }
  
  @Deprecated
  public void setShadowResource(@DrawableRes int paramInt)
  {
    setShadowDrawable(getResources().getDrawable(paramInt));
  }
  
  public void setShadowResourceLeft(int paramInt)
  {
    setShadowDrawableLeft(getResources().getDrawable(paramInt));
  }
  
  public void setShadowResourceRight(int paramInt)
  {
    setShadowDrawableRight(getResources().getDrawable(paramInt));
  }
  
  public void setSliderFadeColor(@ColorInt int paramInt)
  {
    mSliderFadeColor = paramInt;
  }
  
  @Deprecated
  public void smoothSlideClosed()
  {
    closePane();
  }
  
  @Deprecated
  public void smoothSlideOpen()
  {
    openPane();
  }
  
  boolean smoothSlideTo(float paramFloat, int paramInt)
  {
    if (!mCanSlide) {}
    for (;;)
    {
      return false;
      boolean bool = isLayoutRtlSupport();
      LayoutParams localLayoutParams = (LayoutParams)mSlideableView.getLayoutParams();
      int j;
      int k;
      if (bool)
      {
        j = getPaddingRight() + rightMargin;
        k = mSlideableView.getWidth();
      }
      for (int i = (int)(getWidth() - (j + paramFloat * mSlideRange + k)); mDragHelper.smoothSlideViewTo(mSlideableView, i, mSlideableView.getTop()); i = (int)(getPaddingLeft() + leftMargin + paramFloat * mSlideRange))
      {
        setAllChildrenVisible();
        ViewCompat.postInvalidateOnAnimation(this);
        return true;
      }
    }
  }
  
  void updateObscuredViewsVisibility(View paramView)
  {
    boolean bool = isLayoutRtlSupport();
    int i;
    int j;
    label29:
    int k;
    int m;
    int i1;
    int i2;
    int i3;
    int n;
    label81:
    int i4;
    int i5;
    if (bool)
    {
      i = getWidth() - getPaddingRight();
      if (!bool) {
        break label120;
      }
      j = getPaddingLeft();
      k = getPaddingTop();
      m = getHeight() - getPaddingBottom();
      if ((paramView == null) || (!viewIsOpaque(paramView))) {
        break label134;
      }
      i1 = paramView.getLeft();
      i2 = paramView.getRight();
      i3 = paramView.getTop();
      n = paramView.getBottom();
      i4 = 0;
      i5 = getChildCount();
    }
    View localView;
    for (;;)
    {
      if (i4 < i5)
      {
        localView = getChildAt(i4);
        if (localView != paramView) {}
      }
      else
      {
        return;
        i = getPaddingLeft();
        break;
        label120:
        j = getWidth() - getPaddingRight();
        break label29;
        label134:
        n = 0;
        i1 = 0;
        i2 = 0;
        i3 = 0;
        break label81;
      }
      if (localView.getVisibility() != 8) {
        break label165;
      }
      i4++;
    }
    label165:
    int i6;
    label173:
    int i9;
    if (bool)
    {
      i6 = j;
      int i7 = Math.max(i6, localView.getLeft());
      int i8 = Math.max(k, localView.getTop());
      if (!bool) {
        break label275;
      }
      i9 = i;
      label204:
      int i10 = Math.min(i9, localView.getRight());
      int i11 = Math.min(m, localView.getBottom());
      if ((i7 < i1) || (i8 < i3) || (i10 > i2) || (i11 > n)) {
        break label282;
      }
    }
    label275:
    label282:
    for (int i12 = 4;; i12 = 0)
    {
      localView.setVisibility(i12);
      break;
      i6 = i;
      break label173;
      i9 = j;
      break label204;
    }
  }
  
  class AccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    private final Rect mTmpRect = new Rect();
    
    AccessibilityDelegate() {}
    
    private void copyNodeInfoNoChildren(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat1, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat2)
    {
      Rect localRect = mTmpRect;
      paramAccessibilityNodeInfoCompat2.getBoundsInParent(localRect);
      paramAccessibilityNodeInfoCompat1.setBoundsInParent(localRect);
      paramAccessibilityNodeInfoCompat2.getBoundsInScreen(localRect);
      paramAccessibilityNodeInfoCompat1.setBoundsInScreen(localRect);
      paramAccessibilityNodeInfoCompat1.setVisibleToUser(paramAccessibilityNodeInfoCompat2.isVisibleToUser());
      paramAccessibilityNodeInfoCompat1.setPackageName(paramAccessibilityNodeInfoCompat2.getPackageName());
      paramAccessibilityNodeInfoCompat1.setClassName(paramAccessibilityNodeInfoCompat2.getClassName());
      paramAccessibilityNodeInfoCompat1.setContentDescription(paramAccessibilityNodeInfoCompat2.getContentDescription());
      paramAccessibilityNodeInfoCompat1.setEnabled(paramAccessibilityNodeInfoCompat2.isEnabled());
      paramAccessibilityNodeInfoCompat1.setClickable(paramAccessibilityNodeInfoCompat2.isClickable());
      paramAccessibilityNodeInfoCompat1.setFocusable(paramAccessibilityNodeInfoCompat2.isFocusable());
      paramAccessibilityNodeInfoCompat1.setFocused(paramAccessibilityNodeInfoCompat2.isFocused());
      paramAccessibilityNodeInfoCompat1.setAccessibilityFocused(paramAccessibilityNodeInfoCompat2.isAccessibilityFocused());
      paramAccessibilityNodeInfoCompat1.setSelected(paramAccessibilityNodeInfoCompat2.isSelected());
      paramAccessibilityNodeInfoCompat1.setLongClickable(paramAccessibilityNodeInfoCompat2.isLongClickable());
      paramAccessibilityNodeInfoCompat1.addAction(paramAccessibilityNodeInfoCompat2.getActions());
      paramAccessibilityNodeInfoCompat1.setMovementGranularities(paramAccessibilityNodeInfoCompat2.getMovementGranularities());
    }
    
    public boolean filter(View paramView)
    {
      return isDimmed(paramView);
    }
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(SlidingPaneLayout.class.getName());
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.obtain(paramAccessibilityNodeInfoCompat);
      super.onInitializeAccessibilityNodeInfo(paramView, localAccessibilityNodeInfoCompat);
      copyNodeInfoNoChildren(paramAccessibilityNodeInfoCompat, localAccessibilityNodeInfoCompat);
      localAccessibilityNodeInfoCompat.recycle();
      paramAccessibilityNodeInfoCompat.setClassName(SlidingPaneLayout.class.getName());
      paramAccessibilityNodeInfoCompat.setSource(paramView);
      ViewParent localViewParent = ViewCompat.getParentForAccessibility(paramView);
      if ((localViewParent instanceof View)) {
        paramAccessibilityNodeInfoCompat.setParent((View)localViewParent);
      }
      int i = getChildCount();
      for (int j = 0; j < i; j++)
      {
        View localView = getChildAt(j);
        if ((!filter(localView)) && (localView.getVisibility() == 0))
        {
          ViewCompat.setImportantForAccessibility(localView, 1);
          paramAccessibilityNodeInfoCompat.addChild(localView);
        }
      }
    }
    
    public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      if (!filter(paramView)) {
        return super.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
      }
      return false;
    }
  }
  
  private class DisableLayerRunnable
    implements Runnable
  {
    final View mChildView;
    
    DisableLayerRunnable(View paramView)
    {
      mChildView = paramView;
    }
    
    public void run()
    {
      if (mChildView.getParent() == SlidingPaneLayout.this)
      {
        ViewCompat.setLayerType(mChildView, 0, null);
        invalidateChildRegion(mChildView);
      }
      mPostedRunnables.remove(this);
    }
  }
  
  private class DragHelperCallback
    extends ViewDragHelper.Callback
  {
    DragHelperCallback() {}
    
    public int clampViewPositionHorizontal(View paramView, int paramInt1, int paramInt2)
    {
      SlidingPaneLayout.LayoutParams localLayoutParams = (SlidingPaneLayout.LayoutParams)mSlideableView.getLayoutParams();
      if (isLayoutRtlSupport())
      {
        int k = getWidth() - (getPaddingRight() + rightMargin + mSlideableView.getWidth());
        int m = k - mSlideRange;
        return Math.max(Math.min(paramInt1, k), m);
      }
      int i = getPaddingLeft() + leftMargin;
      int j = i + mSlideRange;
      return Math.min(Math.max(paramInt1, i), j);
    }
    
    public int clampViewPositionVertical(View paramView, int paramInt1, int paramInt2)
    {
      return paramView.getTop();
    }
    
    public int getViewHorizontalDragRange(View paramView)
    {
      return mSlideRange;
    }
    
    public void onEdgeDragStarted(int paramInt1, int paramInt2)
    {
      mDragHelper.captureChildView(mSlideableView, paramInt2);
    }
    
    public void onViewCaptured(View paramView, int paramInt)
    {
      setAllChildrenVisible();
    }
    
    public void onViewDragStateChanged(int paramInt)
    {
      if (mDragHelper.getViewDragState() == 0)
      {
        if (mSlideOffset == 0.0F)
        {
          updateObscuredViewsVisibility(mSlideableView);
          dispatchOnPanelClosed(mSlideableView);
          mPreservedOpenState = false;
        }
      }
      else {
        return;
      }
      dispatchOnPanelOpened(mSlideableView);
      mPreservedOpenState = true;
    }
    
    public void onViewPositionChanged(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      onPanelDragged(paramInt1);
      invalidate();
    }
    
    public void onViewReleased(View paramView, float paramFloat1, float paramFloat2)
    {
      SlidingPaneLayout.LayoutParams localLayoutParams = (SlidingPaneLayout.LayoutParams)paramView.getLayoutParams();
      int i;
      if (isLayoutRtlSupport())
      {
        int j = getPaddingRight() + rightMargin;
        if ((paramFloat1 < 0.0F) || ((paramFloat1 == 0.0F) && (mSlideOffset > 0.5F))) {
          j += mSlideRange;
        }
        int k = mSlideableView.getWidth();
        i = getWidth() - j - k;
      }
      for (;;)
      {
        mDragHelper.settleCapturedViewAt(i, paramView.getTop());
        invalidate();
        return;
        i = getPaddingLeft() + leftMargin;
        if ((paramFloat1 > 0.0F) || ((paramFloat1 == 0.0F) && (mSlideOffset > 0.5F))) {
          i += mSlideRange;
        }
      }
    }
    
    public boolean tryCaptureView(View paramView, int paramInt)
    {
      if (mIsUnableToDrag) {
        return false;
      }
      return getLayoutParamsslideable;
    }
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    private static final int[] ATTRS = { 16843137 };
    Paint dimPaint;
    boolean dimWhenOffset;
    boolean slideable;
    public float weight = 0.0F;
    
    public LayoutParams()
    {
      super(-1);
    }
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, ATTRS);
      weight = localTypedArray.getFloat(0, 0.0F);
      localTypedArray.recycle();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      weight = weight;
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
  }
  
  public static abstract interface PanelSlideListener
  {
    public abstract void onPanelClosed(View paramView);
    
    public abstract void onPanelOpened(View paramView);
    
    public abstract void onPanelSlide(View paramView, float paramFloat);
  }
  
  static class SavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
    {
      public SlidingPaneLayout.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new SlidingPaneLayout.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public SlidingPaneLayout.SavedState[] newArray(int paramAnonymousInt)
      {
        return new SlidingPaneLayout.SavedState[paramAnonymousInt];
      }
    });
    boolean isOpen;
    
    SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      if (paramParcel.readInt() != 0) {}
      for (boolean bool = true;; bool = false)
      {
        isOpen = bool;
        return;
      }
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      if (isOpen) {}
      for (int i = 1;; i = 0)
      {
        paramParcel.writeInt(i);
        return;
      }
    }
  }
  
  public static class SimplePanelSlideListener
    implements SlidingPaneLayout.PanelSlideListener
  {
    public SimplePanelSlideListener() {}
    
    public void onPanelClosed(View paramView) {}
    
    public void onPanelOpened(View paramView) {}
    
    public void onPanelSlide(View paramView, float paramFloat) {}
  }
  
  static abstract interface SlidingPanelLayoutImpl
  {
    public abstract void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView);
  }
  
  static class SlidingPanelLayoutImplBase
    implements SlidingPaneLayout.SlidingPanelLayoutImpl
  {
    SlidingPanelLayoutImplBase() {}
    
    public void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView)
    {
      ViewCompat.postInvalidateOnAnimation(paramSlidingPaneLayout, paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
    }
  }
  
  static class SlidingPanelLayoutImplJB
    extends SlidingPaneLayout.SlidingPanelLayoutImplBase
  {
    private Method mGetDisplayList;
    private Field mRecreateDisplayList;
    
    SlidingPanelLayoutImplJB()
    {
      try
      {
        mGetDisplayList = View.class.getDeclaredMethod("getDisplayList", (Class[])null);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        for (;;)
        {
          try
          {
            mRecreateDisplayList = View.class.getDeclaredField("mRecreateDisplayList");
            mRecreateDisplayList.setAccessible(true);
            return;
          }
          catch (NoSuchFieldException localNoSuchFieldException)
          {
            Log.e("SlidingPaneLayout", "Couldn't fetch mRecreateDisplayList field; dimming will be slow.", localNoSuchFieldException);
          }
          localNoSuchMethodException = localNoSuchMethodException;
          Log.e("SlidingPaneLayout", "Couldn't fetch getDisplayList method; dimming won't work right.", localNoSuchMethodException);
        }
      }
    }
    
    public void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView)
    {
      if ((mGetDisplayList != null) && (mRecreateDisplayList != null)) {
        try
        {
          mRecreateDisplayList.setBoolean(paramView, true);
          mGetDisplayList.invoke(paramView, (Object[])null);
          super.invalidateChildRegion(paramSlidingPaneLayout, paramView);
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Log.e("SlidingPaneLayout", "Error refreshing display list state", localException);
          }
        }
      }
      paramView.invalidate();
    }
  }
  
  static class SlidingPanelLayoutImplJBMR1
    extends SlidingPaneLayout.SlidingPanelLayoutImplBase
  {
    SlidingPanelLayoutImplJBMR1() {}
    
    public void invalidateChildRegion(SlidingPaneLayout paramSlidingPaneLayout, View paramView)
    {
      ViewCompat.setLayerPaint(paramView, getLayoutParamsdimPaint);
    }
  }
}
