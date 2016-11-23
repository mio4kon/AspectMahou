package android.support.v4.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewPager
  extends ViewGroup
{
  private static final int CLOSE_ENOUGH = 2;
  private static final Comparator<ItemInfo> COMPARATOR = new Comparator()
  {
    public int compare(ViewPager.ItemInfo paramAnonymousItemInfo1, ViewPager.ItemInfo paramAnonymousItemInfo2)
    {
      return position - position;
    }
  };
  private static final boolean DEBUG = false;
  private static final int DEFAULT_GUTTER_SIZE = 16;
  private static final int DEFAULT_OFFSCREEN_PAGES = 1;
  private static final int DRAW_ORDER_DEFAULT = 0;
  private static final int DRAW_ORDER_FORWARD = 1;
  private static final int DRAW_ORDER_REVERSE = 2;
  private static final int INVALID_POINTER = -1;
  static final int[] LAYOUT_ATTRS = { 16842931 };
  private static final int MAX_SETTLE_DURATION = 600;
  private static final int MIN_DISTANCE_FOR_FLING = 25;
  private static final int MIN_FLING_VELOCITY = 400;
  public static final int SCROLL_STATE_DRAGGING = 1;
  public static final int SCROLL_STATE_IDLE = 0;
  public static final int SCROLL_STATE_SETTLING = 2;
  private static final String TAG = "ViewPager";
  private static final boolean USE_CACHE;
  private static final Interpolator sInterpolator = new Interpolator()
  {
    public float getInterpolation(float paramAnonymousFloat)
    {
      float f = paramAnonymousFloat - 1.0F;
      return 1.0F + f * (f * (f * (f * f)));
    }
  };
  private static final ViewPositionComparator sPositionComparator = new ViewPositionComparator();
  private int mActivePointerId = -1;
  PagerAdapter mAdapter;
  private List<OnAdapterChangeListener> mAdapterChangeListeners;
  private int mBottomPageBounds;
  private boolean mCalledSuper;
  private int mChildHeightMeasureSpec;
  private int mChildWidthMeasureSpec;
  private int mCloseEnough;
  int mCurItem;
  private int mDecorChildCount;
  private int mDefaultGutterSize;
  private int mDrawingOrder;
  private ArrayList<View> mDrawingOrderedChildren;
  private final Runnable mEndScrollRunnable = new Runnable()
  {
    public void run()
    {
      setScrollState(0);
      populate();
    }
  };
  private int mExpectedAdapterCount;
  private long mFakeDragBeginTime;
  private boolean mFakeDragging;
  private boolean mFirstLayout = true;
  private float mFirstOffset = -3.4028235E38F;
  private int mFlingDistance;
  private int mGutterSize;
  private boolean mInLayout;
  private float mInitialMotionX;
  private float mInitialMotionY;
  private OnPageChangeListener mInternalPageChangeListener;
  private boolean mIsBeingDragged;
  private boolean mIsScrollStarted;
  private boolean mIsUnableToDrag;
  private final ArrayList<ItemInfo> mItems = new ArrayList();
  private float mLastMotionX;
  private float mLastMotionY;
  private float mLastOffset = Float.MAX_VALUE;
  private EdgeEffectCompat mLeftEdge;
  private Drawable mMarginDrawable;
  private int mMaximumVelocity;
  private int mMinimumVelocity;
  private boolean mNeedCalculatePageOffsets = false;
  private PagerObserver mObserver;
  private int mOffscreenPageLimit = 1;
  private OnPageChangeListener mOnPageChangeListener;
  private List<OnPageChangeListener> mOnPageChangeListeners;
  private int mPageMargin;
  private PageTransformer mPageTransformer;
  private boolean mPopulatePending;
  private Parcelable mRestoredAdapterState = null;
  private ClassLoader mRestoredClassLoader = null;
  private int mRestoredCurItem = -1;
  private EdgeEffectCompat mRightEdge;
  private int mScrollState = 0;
  private Scroller mScroller;
  private boolean mScrollingCacheEnabled;
  private Method mSetChildrenDrawingOrderEnabled;
  private final ItemInfo mTempItem = new ItemInfo();
  private final Rect mTempRect = new Rect();
  private int mTopPageBounds;
  private int mTouchSlop;
  private VelocityTracker mVelocityTracker;
  
  public ViewPager(Context paramContext)
  {
    super(paramContext);
    initViewPager();
  }
  
  public ViewPager(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViewPager();
  }
  
  private void calculatePageOffsets(ItemInfo paramItemInfo1, int paramInt, ItemInfo paramItemInfo2)
  {
    int i = mAdapter.getCount();
    int j = getClientWidth();
    float f1;
    int i5;
    int i8;
    float f7;
    if (j > 0)
    {
      f1 = mPageMargin / j;
      if (paramItemInfo2 == null) {
        break label371;
      }
      i5 = position;
      if (i5 < position)
      {
        i8 = 0;
        f7 = f1 + (offset + widthFactor);
      }
    }
    else
    {
      for (int i9 = i5 + 1;; i9++)
      {
        if ((i9 > position) || (i8 >= mItems.size())) {
          break label371;
        }
        ItemInfo localItemInfo4 = (ItemInfo)mItems.get(i8);
        for (;;)
        {
          if ((i9 > position) && (i8 < -1 + mItems.size()))
          {
            i8++;
            localItemInfo4 = (ItemInfo)mItems.get(i8);
            continue;
            f1 = 0.0F;
            break;
          }
        }
        while (i9 < position)
        {
          f7 += f1 + mAdapter.getPageWidth(i9);
          i9++;
        }
        offset = f7;
        f7 += f1 + widthFactor;
      }
    }
    if (i5 > position)
    {
      int i6 = -1 + mItems.size();
      float f6 = offset;
      for (int i7 = i5 - 1; (i7 >= position) && (i6 >= 0); i7--)
      {
        for (ItemInfo localItemInfo3 = (ItemInfo)mItems.get(i6); (i7 < position) && (i6 > 0); localItemInfo3 = (ItemInfo)mItems.get(i6)) {
          i6--;
        }
        while (i7 > position)
        {
          f6 -= f1 + mAdapter.getPageWidth(i7);
          i7--;
        }
        f6 -= f1 + widthFactor;
        offset = f6;
      }
    }
    label371:
    int k = mItems.size();
    float f2 = offset;
    int m = -1 + position;
    float f3;
    float f4;
    label437:
    int n;
    if (position == 0)
    {
      f3 = offset;
      mFirstOffset = f3;
      if (position != i - 1) {
        break label518;
      }
      f4 = offset + widthFactor - 1.0F;
      mLastOffset = f4;
      n = paramInt - 1;
    }
    for (;;)
    {
      if (n < 0) {
        break label568;
      }
      ItemInfo localItemInfo2 = (ItemInfo)mItems.get(n);
      for (;;)
      {
        if (m > position)
        {
          PagerAdapter localPagerAdapter2 = mAdapter;
          int i4 = m - 1;
          f2 -= f1 + localPagerAdapter2.getPageWidth(m);
          m = i4;
          continue;
          f3 = -3.4028235E38F;
          break;
          label518:
          f4 = Float.MAX_VALUE;
          break label437;
        }
      }
      f2 -= f1 + widthFactor;
      offset = f2;
      if (position == 0) {
        mFirstOffset = f2;
      }
      n--;
      m--;
    }
    label568:
    float f5 = f1 + (offset + widthFactor);
    int i1 = 1 + position;
    int i2 = paramInt + 1;
    while (i2 < k)
    {
      ItemInfo localItemInfo1 = (ItemInfo)mItems.get(i2);
      while (i1 < position)
      {
        PagerAdapter localPagerAdapter1 = mAdapter;
        int i3 = i1 + 1;
        f5 += f1 + localPagerAdapter1.getPageWidth(i1);
        i1 = i3;
      }
      if (position == i - 1) {
        mLastOffset = (f5 + widthFactor - 1.0F);
      }
      offset = f5;
      f5 += f1 + widthFactor;
      i2++;
      i1++;
    }
    mNeedCalculatePageOffsets = false;
  }
  
  private void completeScroll(boolean paramBoolean)
  {
    int i = 1;
    int j;
    if (mScrollState == 2)
    {
      j = i;
      if (j != 0)
      {
        setScrollingCacheEnabled(false);
        if (mScroller.isFinished()) {
          break label169;
        }
      }
    }
    for (;;)
    {
      if (i != 0)
      {
        mScroller.abortAnimation();
        int m = getScrollX();
        int n = getScrollY();
        int i1 = mScroller.getCurrX();
        int i2 = mScroller.getCurrY();
        if ((m != i1) || (n != i2))
        {
          scrollTo(i1, i2);
          if (i1 != m) {
            pageScrolled(i1);
          }
        }
      }
      mPopulatePending = false;
      for (int k = 0; k < mItems.size(); k++)
      {
        ItemInfo localItemInfo = (ItemInfo)mItems.get(k);
        if (scrolling)
        {
          j = 1;
          scrolling = false;
        }
      }
      j = 0;
      break;
      label169:
      i = 0;
    }
    if (j != 0)
    {
      if (paramBoolean) {
        ViewCompat.postOnAnimation(this, mEndScrollRunnable);
      }
    }
    else {
      return;
    }
    mEndScrollRunnable.run();
  }
  
  private int determineTargetPage(int paramInt1, float paramFloat, int paramInt2, int paramInt3)
  {
    int i;
    if ((Math.abs(paramInt3) > mFlingDistance) && (Math.abs(paramInt2) > mMinimumVelocity))
    {
      if (paramInt2 > 0) {}
      for (i = paramInt1;; i = paramInt1 + 1)
      {
        if (mItems.size() > 0)
        {
          ItemInfo localItemInfo1 = (ItemInfo)mItems.get(0);
          ItemInfo localItemInfo2 = (ItemInfo)mItems.get(-1 + mItems.size());
          i = Math.max(position, Math.min(i, position));
        }
        return i;
      }
    }
    if (paramInt1 >= mCurItem) {}
    for (float f = 0.4F;; f = 0.6F)
    {
      i = paramInt1 + (int)(paramFloat + f);
      break;
    }
  }
  
  private void dispatchOnPageScrolled(int paramInt1, float paramFloat, int paramInt2)
  {
    if (mOnPageChangeListener != null) {
      mOnPageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2);
    }
    if (mOnPageChangeListeners != null)
    {
      int i = 0;
      int j = mOnPageChangeListeners.size();
      while (i < j)
      {
        OnPageChangeListener localOnPageChangeListener = (OnPageChangeListener)mOnPageChangeListeners.get(i);
        if (localOnPageChangeListener != null) {
          localOnPageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2);
        }
        i++;
      }
    }
    if (mInternalPageChangeListener != null) {
      mInternalPageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2);
    }
  }
  
  private void dispatchOnPageSelected(int paramInt)
  {
    if (mOnPageChangeListener != null) {
      mOnPageChangeListener.onPageSelected(paramInt);
    }
    if (mOnPageChangeListeners != null)
    {
      int i = 0;
      int j = mOnPageChangeListeners.size();
      while (i < j)
      {
        OnPageChangeListener localOnPageChangeListener = (OnPageChangeListener)mOnPageChangeListeners.get(i);
        if (localOnPageChangeListener != null) {
          localOnPageChangeListener.onPageSelected(paramInt);
        }
        i++;
      }
    }
    if (mInternalPageChangeListener != null) {
      mInternalPageChangeListener.onPageSelected(paramInt);
    }
  }
  
  private void dispatchOnScrollStateChanged(int paramInt)
  {
    if (mOnPageChangeListener != null) {
      mOnPageChangeListener.onPageScrollStateChanged(paramInt);
    }
    if (mOnPageChangeListeners != null)
    {
      int i = 0;
      int j = mOnPageChangeListeners.size();
      while (i < j)
      {
        OnPageChangeListener localOnPageChangeListener = (OnPageChangeListener)mOnPageChangeListeners.get(i);
        if (localOnPageChangeListener != null) {
          localOnPageChangeListener.onPageScrollStateChanged(paramInt);
        }
        i++;
      }
    }
    if (mInternalPageChangeListener != null) {
      mInternalPageChangeListener.onPageScrollStateChanged(paramInt);
    }
  }
  
  private void enableLayers(boolean paramBoolean)
  {
    int i = getChildCount();
    int j = 0;
    if (j < i)
    {
      if (paramBoolean) {}
      for (int k = 2;; k = 0)
      {
        ViewCompat.setLayerType(getChildAt(j), k, null);
        j++;
        break;
      }
    }
  }
  
  private void endDrag()
  {
    mIsBeingDragged = false;
    mIsUnableToDrag = false;
    if (mVelocityTracker != null)
    {
      mVelocityTracker.recycle();
      mVelocityTracker = null;
    }
  }
  
  private Rect getChildRectInPagerCoordinates(Rect paramRect, View paramView)
  {
    if (paramRect == null) {
      paramRect = new Rect();
    }
    if (paramView == null) {
      paramRect.set(0, 0, 0, 0);
    }
    for (;;)
    {
      return paramRect;
      left = paramView.getLeft();
      right = paramView.getRight();
      top = paramView.getTop();
      bottom = paramView.getBottom();
      ViewGroup localViewGroup;
      for (ViewParent localViewParent = paramView.getParent(); ((localViewParent instanceof ViewGroup)) && (localViewParent != this); localViewParent = localViewGroup.getParent())
      {
        localViewGroup = (ViewGroup)localViewParent;
        left += localViewGroup.getLeft();
        right += localViewGroup.getRight();
        top += localViewGroup.getTop();
        bottom += localViewGroup.getBottom();
      }
    }
  }
  
  private int getClientWidth()
  {
    return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
  }
  
  private ItemInfo infoForCurrentScrollPosition()
  {
    int i = getClientWidth();
    float f1;
    float f2;
    int j;
    float f3;
    float f4;
    int k;
    Object localObject;
    int m;
    if (i > 0)
    {
      f1 = getScrollX() / i;
      f2 = 0.0F;
      if (i > 0) {
        f2 = mPageMargin / i;
      }
      j = -1;
      f3 = 0.0F;
      f4 = 0.0F;
      k = 1;
      localObject = null;
      m = 0;
    }
    for (;;)
    {
      ItemInfo localItemInfo;
      float f5;
      if (m < mItems.size())
      {
        localItemInfo = (ItemInfo)mItems.get(m);
        if ((k == 0) && (position != j + 1))
        {
          localItemInfo = mTempItem;
          offset = (f2 + (f3 + f4));
          position = (j + 1);
          widthFactor = mAdapter.getPageWidth(position);
          m--;
        }
        f5 = offset;
        float f6 = f2 + (f5 + widthFactor);
        if ((k != 0) || (f1 >= f5))
        {
          if ((f1 >= f6) && (m != -1 + mItems.size())) {
            break label205;
          }
          localObject = localItemInfo;
        }
      }
      return localObject;
      f1 = 0.0F;
      break;
      label205:
      j = position;
      f3 = f5;
      f4 = widthFactor;
      localObject = localItemInfo;
      m++;
      k = 0;
    }
  }
  
  private static boolean isDecorView(@NonNull View paramView)
  {
    return paramView.getClass().getAnnotation(DecorView.class) != null;
  }
  
  private boolean isGutterDrag(float paramFloat1, float paramFloat2)
  {
    return ((paramFloat1 < mGutterSize) && (paramFloat2 > 0.0F)) || ((paramFloat1 > getWidth() - mGutterSize) && (paramFloat2 < 0.0F));
  }
  
  private void onSecondaryPointerUp(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionIndex(paramMotionEvent);
    if (paramMotionEvent.getPointerId(i) == mActivePointerId) {
      if (i != 0) {
        break label56;
      }
    }
    label56:
    for (int j = 1;; j = 0)
    {
      mLastMotionX = paramMotionEvent.getX(j);
      mActivePointerId = paramMotionEvent.getPointerId(j);
      if (mVelocityTracker != null) {
        mVelocityTracker.clear();
      }
      return;
    }
  }
  
  private boolean pageScrolled(int paramInt)
  {
    if (mItems.size() == 0)
    {
      if (mFirstLayout) {}
      do
      {
        return false;
        mCalledSuper = false;
        onPageScrolled(0, 0.0F, 0);
      } while (mCalledSuper);
      throw new IllegalStateException("onPageScrolled did not call superclass implementation");
    }
    ItemInfo localItemInfo = infoForCurrentScrollPosition();
    int i = getClientWidth();
    int j = i + mPageMargin;
    float f1 = mPageMargin / i;
    int k = position;
    float f2 = (paramInt / i - offset) / (f1 + widthFactor);
    int m = (int)(f2 * j);
    mCalledSuper = false;
    onPageScrolled(k, f2, m);
    if (!mCalledSuper) {
      throw new IllegalStateException("onPageScrolled did not call superclass implementation");
    }
    return true;
  }
  
  private boolean performDrag(float paramFloat)
  {
    float f1 = mLastMotionX - paramFloat;
    mLastMotionX = paramFloat;
    float f2 = f1 + getScrollX();
    int i = getClientWidth();
    float f3 = i * mFirstOffset;
    float f4 = i * mLastOffset;
    int j = 1;
    int k = 1;
    ItemInfo localItemInfo1 = (ItemInfo)mItems.get(0);
    ItemInfo localItemInfo2 = (ItemInfo)mItems.get(-1 + mItems.size());
    if (position != 0)
    {
      j = 0;
      f3 = offset * i;
    }
    if (position != -1 + mAdapter.getCount())
    {
      k = 0;
      f4 = offset * i;
    }
    boolean bool1;
    if (f2 < f3)
    {
      bool1 = false;
      if (j != 0)
      {
        float f5 = f3 - f2;
        bool1 = mLeftEdge.onPull(Math.abs(f5) / i);
      }
      f2 = f3;
    }
    for (;;)
    {
      mLastMotionX += f2 - (int)f2;
      scrollTo((int)f2, getScrollY());
      pageScrolled((int)f2);
      return bool1;
      boolean bool2 = f2 < f4;
      bool1 = false;
      if (bool2)
      {
        bool1 = false;
        if (k != 0)
        {
          float f6 = f2 - f4;
          bool1 = mRightEdge.onPull(Math.abs(f6) / i);
        }
        f2 = f4;
      }
    }
  }
  
  private void recomputeScrollPosition(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt2 > 0) && (!mItems.isEmpty()))
    {
      if (!mScroller.isFinished())
      {
        mScroller.setFinalX(getCurrentItem() * getClientWidth());
        return;
      }
      int j = paramInt3 + (paramInt1 - getPaddingLeft() - getPaddingRight());
      int k = paramInt4 + (paramInt2 - getPaddingLeft() - getPaddingRight());
      scrollTo((int)(getScrollX() / k * j), getScrollY());
      return;
    }
    ItemInfo localItemInfo = infoForPosition(mCurItem);
    if (localItemInfo != null) {}
    for (float f = Math.min(offset, mLastOffset);; f = 0.0F)
    {
      int i = (int)(f * (paramInt1 - getPaddingLeft() - getPaddingRight()));
      if (i == getScrollX()) {
        break;
      }
      completeScroll(false);
      scrollTo(i, getScrollY());
      return;
    }
  }
  
  private void removeNonDecorViews()
  {
    for (int i = 0; i < getChildCount(); i++) {
      if (!getChildAtgetLayoutParamsisDecor)
      {
        removeViewAt(i);
        i--;
      }
    }
  }
  
  private void requestParentDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    ViewParent localViewParent = getParent();
    if (localViewParent != null) {
      localViewParent.requestDisallowInterceptTouchEvent(paramBoolean);
    }
  }
  
  private boolean resetTouch()
  {
    mActivePointerId = -1;
    endDrag();
    return mLeftEdge.onRelease() | mRightEdge.onRelease();
  }
  
  private void scrollToItem(int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2)
  {
    ItemInfo localItemInfo = infoForPosition(paramInt1);
    int i = 0;
    if (localItemInfo != null) {
      i = (int)(getClientWidth() * Math.max(mFirstOffset, Math.min(offset, mLastOffset)));
    }
    if (paramBoolean1)
    {
      smoothScrollTo(i, 0, paramInt2);
      if (paramBoolean2) {
        dispatchOnPageSelected(paramInt1);
      }
      return;
    }
    if (paramBoolean2) {
      dispatchOnPageSelected(paramInt1);
    }
    completeScroll(false);
    scrollTo(i, 0);
    pageScrolled(i);
  }
  
  private void setScrollingCacheEnabled(boolean paramBoolean)
  {
    if (mScrollingCacheEnabled != paramBoolean) {
      mScrollingCacheEnabled = paramBoolean;
    }
  }
  
  private void sortChildDrawingOrder()
  {
    if (mDrawingOrder != 0)
    {
      if (mDrawingOrderedChildren == null) {
        mDrawingOrderedChildren = new ArrayList();
      }
      for (;;)
      {
        int i = getChildCount();
        for (int j = 0; j < i; j++)
        {
          View localView = getChildAt(j);
          mDrawingOrderedChildren.add(localView);
        }
        mDrawingOrderedChildren.clear();
      }
      Collections.sort(mDrawingOrderedChildren, sPositionComparator);
    }
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    int i = paramArrayList.size();
    int j = getDescendantFocusability();
    if (j != 393216) {
      for (int k = 0; k < getChildCount(); k++)
      {
        View localView = getChildAt(k);
        if (localView.getVisibility() == 0)
        {
          ItemInfo localItemInfo = infoForChild(localView);
          if ((localItemInfo != null) && (position == mCurItem)) {
            localView.addFocusables(paramArrayList, paramInt1, paramInt2);
          }
        }
      }
    }
    if (((j == 262144) && (i != paramArrayList.size())) || (!isFocusable())) {}
    while ((((paramInt2 & 0x1) == 1) && (isInTouchMode()) && (!isFocusableInTouchMode())) || (paramArrayList == null)) {
      return;
    }
    paramArrayList.add(this);
  }
  
  ItemInfo addNewItem(int paramInt1, int paramInt2)
  {
    ItemInfo localItemInfo = new ItemInfo();
    position = paramInt1;
    object = mAdapter.instantiateItem(this, paramInt1);
    widthFactor = mAdapter.getPageWidth(paramInt1);
    if ((paramInt2 < 0) || (paramInt2 >= mItems.size()))
    {
      mItems.add(localItemInfo);
      return localItemInfo;
    }
    mItems.add(paramInt2, localItemInfo);
    return localItemInfo;
  }
  
  public void addOnAdapterChangeListener(@NonNull OnAdapterChangeListener paramOnAdapterChangeListener)
  {
    if (mAdapterChangeListeners == null) {
      mAdapterChangeListeners = new ArrayList();
    }
    mAdapterChangeListeners.add(paramOnAdapterChangeListener);
  }
  
  public void addOnPageChangeListener(OnPageChangeListener paramOnPageChangeListener)
  {
    if (mOnPageChangeListeners == null) {
      mOnPageChangeListeners = new ArrayList();
    }
    mOnPageChangeListeners.add(paramOnPageChangeListener);
  }
  
  public void addTouchables(ArrayList<View> paramArrayList)
  {
    for (int i = 0; i < getChildCount(); i++)
    {
      View localView = getChildAt(i);
      if (localView.getVisibility() == 0)
      {
        ItemInfo localItemInfo = infoForChild(localView);
        if ((localItemInfo != null) && (position == mCurItem)) {
          localView.addTouchables(paramArrayList);
        }
      }
    }
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    if (!checkLayoutParams(paramLayoutParams)) {
      paramLayoutParams = generateLayoutParams(paramLayoutParams);
    }
    LayoutParams localLayoutParams = (LayoutParams)paramLayoutParams;
    isDecor |= isDecorView(paramView);
    if (mInLayout)
    {
      if ((localLayoutParams != null) && (isDecor)) {
        throw new IllegalStateException("Cannot add pager decor view during layout");
      }
      needsMeasure = true;
      addViewInLayout(paramView, paramInt, paramLayoutParams);
      return;
    }
    super.addView(paramView, paramInt, paramLayoutParams);
  }
  
  public boolean arrowScroll(int paramInt)
  {
    View localView1 = findFocus();
    View localView2;
    boolean bool2;
    if (localView1 == this)
    {
      localView1 = null;
      localView2 = FocusFinder.getInstance().findNextFocus(this, localView1, paramInt);
      if ((localView2 == null) || (localView2 == localView1)) {
        break label329;
      }
      if (paramInt != 17) {
        break label261;
      }
      int m = getChildRectInPagerCoordinatesmTempRect, localView2).left;
      int n = getChildRectInPagerCoordinatesmTempRect, localView1).left;
      if ((localView1 == null) || (m < n)) {
        break label251;
      }
      bool2 = pageLeft();
    }
    for (;;)
    {
      if (bool2) {
        playSoundEffect(SoundEffectConstants.getContantForFocusDirection(paramInt));
      }
      return bool2;
      if (localView1 == null) {
        break;
      }
      StringBuilder localStringBuilder;
      for (ViewParent localViewParent1 = localView1.getParent();; localViewParent1 = localViewParent1.getParent())
      {
        boolean bool1 = localViewParent1 instanceof ViewGroup;
        int i = 0;
        if (bool1)
        {
          if (localViewParent1 == this) {
            i = 1;
          }
        }
        else
        {
          if (i != 0) {
            break;
          }
          localStringBuilder = new StringBuilder();
          localStringBuilder.append(localView1.getClass().getSimpleName());
          for (ViewParent localViewParent2 = localView1.getParent(); (localViewParent2 instanceof ViewGroup); localViewParent2 = localViewParent2.getParent()) {
            localStringBuilder.append(" => ").append(localViewParent2.getClass().getSimpleName());
          }
        }
      }
      Log.e("ViewPager", "arrowScroll tried to find focus based on non-child current focused view " + localStringBuilder.toString());
      localView1 = null;
      break;
      label251:
      bool2 = localView2.requestFocus();
      continue;
      label261:
      bool2 = false;
      if (paramInt == 66)
      {
        int j = getChildRectInPagerCoordinatesmTempRect, localView2).left;
        int k = getChildRectInPagerCoordinatesmTempRect, localView1).left;
        if ((localView1 != null) && (j <= k))
        {
          bool2 = pageRight();
        }
        else
        {
          bool2 = localView2.requestFocus();
          continue;
          label329:
          if ((paramInt == 17) || (paramInt == 1))
          {
            bool2 = pageLeft();
          }
          else if (paramInt != 66)
          {
            bool2 = false;
            if (paramInt != 2) {}
          }
          else
          {
            bool2 = pageRight();
          }
        }
      }
    }
  }
  
  public boolean beginFakeDrag()
  {
    if (mIsBeingDragged) {
      return false;
    }
    mFakeDragging = true;
    setScrollState(1);
    mLastMotionX = 0.0F;
    mInitialMotionX = 0.0F;
    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }
    for (;;)
    {
      long l = SystemClock.uptimeMillis();
      MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 0, 0.0F, 0.0F, 0);
      mVelocityTracker.addMovement(localMotionEvent);
      localMotionEvent.recycle();
      mFakeDragBeginTime = l;
      return true;
      mVelocityTracker.clear();
    }
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
        if ((paramInt2 + i >= localView.getLeft()) && (paramInt2 + i < localView.getRight()) && (paramInt3 + j >= localView.getTop()) && (paramInt3 + j < localView.getBottom()) && (canScroll(localView, true, paramInt1, paramInt2 + i - localView.getLeft(), paramInt3 + j - localView.getTop()))) {
          return true;
        }
      }
    }
    return (paramBoolean) && (ViewCompat.canScrollHorizontally(paramView, -paramInt1));
  }
  
  public boolean canScrollHorizontally(int paramInt)
  {
    boolean bool = true;
    if (mAdapter == null) {}
    int i;
    int j;
    do
    {
      return false;
      i = getClientWidth();
      j = getScrollX();
      if (paramInt < 0)
      {
        if (j > (int)(i * mFirstOffset)) {}
        for (;;)
        {
          return bool;
          bool = false;
        }
      }
    } while (paramInt <= 0);
    if (j < (int)(i * mLastOffset)) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return ((paramLayoutParams instanceof LayoutParams)) && (super.checkLayoutParams(paramLayoutParams));
  }
  
  public void clearOnPageChangeListeners()
  {
    if (mOnPageChangeListeners != null) {
      mOnPageChangeListeners.clear();
    }
  }
  
  public void computeScroll()
  {
    mIsScrollStarted = true;
    if ((!mScroller.isFinished()) && (mScroller.computeScrollOffset()))
    {
      int i = getScrollX();
      int j = getScrollY();
      int k = mScroller.getCurrX();
      int m = mScroller.getCurrY();
      if ((i != k) || (j != m))
      {
        scrollTo(k, m);
        if (!pageScrolled(k))
        {
          mScroller.abortAnimation();
          scrollTo(0, m);
        }
      }
      ViewCompat.postInvalidateOnAnimation(this);
      return;
    }
    completeScroll(true);
  }
  
  void dataSetChanged()
  {
    int i = mAdapter.getCount();
    mExpectedAdapterCount = i;
    int j;
    int k;
    int m;
    int n;
    label55:
    ItemInfo localItemInfo;
    int i3;
    if ((mItems.size() < 1 + 2 * mOffscreenPageLimit) && (mItems.size() < i))
    {
      j = 1;
      k = mCurItem;
      m = 0;
      n = 0;
      if (n >= mItems.size()) {
        break label237;
      }
      localItemInfo = (ItemInfo)mItems.get(n);
      i3 = mAdapter.getItemPosition(object);
      if (i3 != -1) {
        break label112;
      }
    }
    for (;;)
    {
      n++;
      break label55;
      j = 0;
      break;
      label112:
      if (i3 == -2)
      {
        mItems.remove(n);
        n--;
        if (m == 0)
        {
          mAdapter.startUpdate(this);
          m = 1;
        }
        mAdapter.destroyItem(this, position, object);
        j = 1;
        if (mCurItem == position)
        {
          k = Math.max(0, Math.min(mCurItem, i - 1));
          j = 1;
        }
      }
      else if (position != i3)
      {
        if (position == mCurItem) {
          k = i3;
        }
        position = i3;
        j = 1;
      }
    }
    label237:
    if (m != 0) {
      mAdapter.finishUpdate(this);
    }
    Collections.sort(mItems, COMPARATOR);
    if (j != 0)
    {
      int i1 = getChildCount();
      for (int i2 = 0; i2 < i1; i2++)
      {
        LayoutParams localLayoutParams = (LayoutParams)getChildAt(i2).getLayoutParams();
        if (!isDecor) {
          widthFactor = 0.0F;
        }
      }
      setCurrentItemInternal(k, false, true);
      requestLayout();
    }
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    return (super.dispatchKeyEvent(paramKeyEvent)) || (executeKeyEvent(paramKeyEvent));
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    if (paramAccessibilityEvent.getEventType() == 4096) {
      return super.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent);
    }
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if (localView.getVisibility() == 0)
      {
        ItemInfo localItemInfo = infoForChild(localView);
        if ((localItemInfo != null) && (position == mCurItem) && (localView.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent))) {
          return true;
        }
      }
    }
    return false;
  }
  
  float distanceInfluenceForSnapDuration(float paramFloat)
  {
    return (float)Math.sin((float)(0.4712389167638204D * (paramFloat - 0.5F)));
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    int i = getOverScrollMode();
    boolean bool2;
    if ((i == 0) || ((i == 1) && (mAdapter != null) && (mAdapter.getCount() > 1)))
    {
      boolean bool1 = mLeftEdge.isFinished();
      bool2 = false;
      if (!bool1)
      {
        int n = paramCanvas.save();
        int i1 = getHeight() - getPaddingTop() - getPaddingBottom();
        int i2 = getWidth();
        paramCanvas.rotate(270.0F);
        paramCanvas.translate(-i1 + getPaddingTop(), mFirstOffset * i2);
        mLeftEdge.setSize(i1, i2);
        bool2 = false | mLeftEdge.draw(paramCanvas);
        paramCanvas.restoreToCount(n);
      }
      if (!mRightEdge.isFinished())
      {
        int j = paramCanvas.save();
        int k = getWidth();
        int m = getHeight() - getPaddingTop() - getPaddingBottom();
        paramCanvas.rotate(90.0F);
        paramCanvas.translate(-getPaddingTop(), -(1.0F + mLastOffset) * k);
        mRightEdge.setSize(m, k);
        bool2 |= mRightEdge.draw(paramCanvas);
        paramCanvas.restoreToCount(j);
      }
    }
    for (;;)
    {
      if (bool2) {
        ViewCompat.postInvalidateOnAnimation(this);
      }
      return;
      mLeftEdge.finish();
      mRightEdge.finish();
      bool2 = false;
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    Drawable localDrawable = mMarginDrawable;
    if ((localDrawable != null) && (localDrawable.isStateful())) {
      localDrawable.setState(getDrawableState());
    }
  }
  
  public void endFakeDrag()
  {
    if (!mFakeDragging) {
      throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
    }
    if (mAdapter != null)
    {
      VelocityTracker localVelocityTracker = mVelocityTracker;
      localVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
      int i = (int)VelocityTrackerCompat.getXVelocity(localVelocityTracker, mActivePointerId);
      mPopulatePending = true;
      int j = getClientWidth();
      int k = getScrollX();
      ItemInfo localItemInfo = infoForCurrentScrollPosition();
      setCurrentItemInternal(determineTargetPage(position, (k / j - offset) / widthFactor, i, (int)(mLastMotionX - mInitialMotionX)), true, true, i);
    }
    endDrag();
    mFakeDragging = false;
  }
  
  public boolean executeKeyEvent(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getAction() == 0) {
      switch (paramKeyEvent.getKeyCode())
      {
      }
    }
    do
    {
      do
      {
        return false;
        return arrowScroll(17);
        return arrowScroll(66);
      } while (Build.VERSION.SDK_INT < 11);
      if (KeyEventCompat.hasNoModifiers(paramKeyEvent)) {
        return arrowScroll(2);
      }
    } while (!KeyEventCompat.hasModifiers(paramKeyEvent, 1));
    return arrowScroll(1);
  }
  
  public void fakeDragBy(float paramFloat)
  {
    if (!mFakeDragging) {
      throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
    }
    if (mAdapter == null) {
      return;
    }
    mLastMotionX = (paramFloat + mLastMotionX);
    float f1 = getScrollX() - paramFloat;
    int i = getClientWidth();
    float f2 = i * mFirstOffset;
    float f3 = i * mLastOffset;
    ItemInfo localItemInfo1 = (ItemInfo)mItems.get(0);
    ItemInfo localItemInfo2 = (ItemInfo)mItems.get(-1 + mItems.size());
    if (position != 0) {
      f2 = offset * i;
    }
    if (position != -1 + mAdapter.getCount()) {
      f3 = offset * i;
    }
    if (f1 < f2) {
      f1 = f2;
    }
    for (;;)
    {
      mLastMotionX += f1 - (int)f1;
      scrollTo((int)f1, getScrollY());
      pageScrolled((int)f1);
      long l = SystemClock.uptimeMillis();
      MotionEvent localMotionEvent = MotionEvent.obtain(mFakeDragBeginTime, l, 2, mLastMotionX, 0.0F, 0);
      mVelocityTracker.addMovement(localMotionEvent);
      localMotionEvent.recycle();
      return;
      if (f1 > f3) {
        f1 = f3;
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
    return generateDefaultLayoutParams();
  }
  
  public PagerAdapter getAdapter()
  {
    return mAdapter;
  }
  
  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    if (mDrawingOrder == 2) {}
    for (int i = paramInt1 - 1 - paramInt2;; i = paramInt2) {
      return mDrawingOrderedChildren.get(i)).getLayoutParams()).childIndex;
    }
  }
  
  public int getCurrentItem()
  {
    return mCurItem;
  }
  
  public int getOffscreenPageLimit()
  {
    return mOffscreenPageLimit;
  }
  
  public int getPageMargin()
  {
    return mPageMargin;
  }
  
  ItemInfo infoForAnyChild(View paramView)
  {
    for (;;)
    {
      ViewParent localViewParent = paramView.getParent();
      if (localViewParent == this) {
        break;
      }
      if ((localViewParent == null) || (!(localViewParent instanceof View))) {
        return null;
      }
      paramView = (View)localViewParent;
    }
    return infoForChild(paramView);
  }
  
  ItemInfo infoForChild(View paramView)
  {
    for (int i = 0; i < mItems.size(); i++)
    {
      ItemInfo localItemInfo = (ItemInfo)mItems.get(i);
      if (mAdapter.isViewFromObject(paramView, object)) {
        return localItemInfo;
      }
    }
    return null;
  }
  
  ItemInfo infoForPosition(int paramInt)
  {
    for (int i = 0; i < mItems.size(); i++)
    {
      ItemInfo localItemInfo = (ItemInfo)mItems.get(i);
      if (position == paramInt) {
        return localItemInfo;
      }
    }
    return null;
  }
  
  void initViewPager()
  {
    setWillNotDraw(false);
    setDescendantFocusability(262144);
    setFocusable(true);
    Context localContext = getContext();
    mScroller = new Scroller(localContext, sInterpolator);
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(localContext);
    float f = getResourcesgetDisplayMetricsdensity;
    mTouchSlop = localViewConfiguration.getScaledPagingTouchSlop();
    mMinimumVelocity = ((int)(400.0F * f));
    mMaximumVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
    mLeftEdge = new EdgeEffectCompat(localContext);
    mRightEdge = new EdgeEffectCompat(localContext);
    mFlingDistance = ((int)(25.0F * f));
    mCloseEnough = ((int)(2.0F * f));
    mDefaultGutterSize = ((int)(16.0F * f));
    ViewCompat.setAccessibilityDelegate(this, new MyAccessibilityDelegate());
    if (ViewCompat.getImportantForAccessibility(this) == 0) {
      ViewCompat.setImportantForAccessibility(this, 1);
    }
    ViewCompat.setOnApplyWindowInsetsListener(this, new OnApplyWindowInsetsListener()
    {
      private final Rect mTempRect = new Rect();
      
      public WindowInsetsCompat onApplyWindowInsets(View paramAnonymousView, WindowInsetsCompat paramAnonymousWindowInsetsCompat)
      {
        WindowInsetsCompat localWindowInsetsCompat1 = ViewCompat.onApplyWindowInsets(paramAnonymousView, paramAnonymousWindowInsetsCompat);
        if (localWindowInsetsCompat1.isConsumed()) {
          return localWindowInsetsCompat1;
        }
        Rect localRect = mTempRect;
        left = localWindowInsetsCompat1.getSystemWindowInsetLeft();
        top = localWindowInsetsCompat1.getSystemWindowInsetTop();
        right = localWindowInsetsCompat1.getSystemWindowInsetRight();
        bottom = localWindowInsetsCompat1.getSystemWindowInsetBottom();
        int i = 0;
        int j = getChildCount();
        while (i < j)
        {
          WindowInsetsCompat localWindowInsetsCompat2 = ViewCompat.dispatchApplyWindowInsets(getChildAt(i), localWindowInsetsCompat1);
          left = Math.min(localWindowInsetsCompat2.getSystemWindowInsetLeft(), left);
          top = Math.min(localWindowInsetsCompat2.getSystemWindowInsetTop(), top);
          right = Math.min(localWindowInsetsCompat2.getSystemWindowInsetRight(), right);
          bottom = Math.min(localWindowInsetsCompat2.getSystemWindowInsetBottom(), bottom);
          i++;
        }
        return localWindowInsetsCompat1.replaceSystemWindowInsets(left, top, right, bottom);
      }
    });
  }
  
  public boolean isFakeDragging()
  {
    return mFakeDragging;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    mFirstLayout = true;
  }
  
  protected void onDetachedFromWindow()
  {
    removeCallbacks(mEndScrollRunnable);
    if ((mScroller != null) && (!mScroller.isFinished())) {
      mScroller.abortAnimation();
    }
    super.onDetachedFromWindow();
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int i;
    int j;
    float f1;
    int k;
    ItemInfo localItemInfo;
    float f2;
    int m;
    int n;
    int i1;
    if ((mPageMargin > 0) && (mMarginDrawable != null) && (mItems.size() > 0) && (mAdapter != null))
    {
      i = getScrollX();
      j = getWidth();
      f1 = mPageMargin / j;
      k = 0;
      localItemInfo = (ItemInfo)mItems.get(0);
      f2 = offset;
      m = mItems.size();
      n = position;
      i1 = mItems.get(m - 1)).position;
    }
    for (int i2 = n;; i2++)
    {
      float f4;
      if (i2 < i1)
      {
        while ((i2 > position) && (k < m))
        {
          ArrayList localArrayList = mItems;
          k++;
          localItemInfo = (ItemInfo)localArrayList.get(k);
        }
        if (i2 != position) {
          break label272;
        }
        f4 = (offset + widthFactor) * j;
      }
      label272:
      float f3;
      for (f2 = f1 + (offset + widthFactor);; f2 += f3 + f1)
      {
        if (f4 + mPageMargin > i)
        {
          mMarginDrawable.setBounds(Math.round(f4), mTopPageBounds, Math.round(f4 + mPageMargin), mBottomPageBounds);
          mMarginDrawable.draw(paramCanvas);
        }
        if (f4 <= i + j) {
          break;
        }
        return;
        f3 = mAdapter.getPageWidth(i2);
        f4 = (f2 + f3) * j;
      }
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = 0xFF & paramMotionEvent.getAction();
    if ((i == 3) || (i == 1))
    {
      resetTouch();
      return false;
    }
    if (i != 0)
    {
      if (mIsBeingDragged) {
        return true;
      }
      if (mIsUnableToDrag) {
        return false;
      }
    }
    switch (i)
    {
    }
    for (;;)
    {
      if (mVelocityTracker == null) {
        mVelocityTracker = VelocityTracker.obtain();
      }
      mVelocityTracker.addMovement(paramMotionEvent);
      return mIsBeingDragged;
      int j = mActivePointerId;
      if (j != -1)
      {
        int k = paramMotionEvent.findPointerIndex(j);
        float f3 = paramMotionEvent.getX(k);
        float f4 = f3 - mLastMotionX;
        float f5 = Math.abs(f4);
        float f6 = paramMotionEvent.getY(k);
        float f7 = Math.abs(f6 - mInitialMotionY);
        if ((f4 != 0.0F) && (!isGutterDrag(mLastMotionX, f4)) && (canScroll(this, false, (int)f4, (int)f3, (int)f6)))
        {
          mLastMotionX = f3;
          mLastMotionY = f6;
          mIsUnableToDrag = true;
          return false;
        }
        float f8;
        if ((f5 > mTouchSlop) && (0.5F * f5 > f7))
        {
          mIsBeingDragged = true;
          requestParentDisallowInterceptTouchEvent(true);
          setScrollState(1);
          if (f4 > 0.0F)
          {
            f8 = mInitialMotionX + mTouchSlop;
            label289:
            mLastMotionX = f8;
            mLastMotionY = f6;
            setScrollingCacheEnabled(true);
          }
        }
        while ((mIsBeingDragged) && (performDrag(f3)))
        {
          ViewCompat.postInvalidateOnAnimation(this);
          break;
          f8 = mInitialMotionX - mTouchSlop;
          break label289;
          if (f7 > mTouchSlop) {
            mIsUnableToDrag = true;
          }
        }
        float f1 = paramMotionEvent.getX();
        mInitialMotionX = f1;
        mLastMotionX = f1;
        float f2 = paramMotionEvent.getY();
        mInitialMotionY = f2;
        mLastMotionY = f2;
        mActivePointerId = paramMotionEvent.getPointerId(0);
        mIsUnableToDrag = false;
        mIsScrollStarted = true;
        mScroller.computeScrollOffset();
        if ((mScrollState == 2) && (Math.abs(mScroller.getFinalX() - mScroller.getCurrX()) > mCloseEnough))
        {
          mScroller.abortAnimation();
          mPopulatePending = false;
          populate();
          mIsBeingDragged = true;
          requestParentDisallowInterceptTouchEvent(true);
          setScrollState(1);
        }
        else
        {
          completeScroll(false);
          mIsBeingDragged = false;
          continue;
          onSecondaryPointerUp(paramMotionEvent);
        }
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = getChildCount();
    int j = paramInt3 - paramInt1;
    int k = paramInt4 - paramInt2;
    int m = getPaddingLeft();
    int n = getPaddingTop();
    int i1 = getPaddingRight();
    int i2 = getPaddingBottom();
    int i3 = getScrollX();
    int i4 = 0;
    int i5 = 0;
    if (i5 < i)
    {
      View localView2 = getChildAt(i5);
      int i12;
      label156:
      int i13;
      if (localView2.getVisibility() != 8)
      {
        LayoutParams localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
        if (isDecor)
        {
          int i10 = 0x7 & gravity;
          int i11 = 0x70 & gravity;
          switch (i10)
          {
          case 2: 
          case 4: 
          default: 
            i12 = m;
            switch (i11)
            {
            default: 
              i13 = n;
            }
            break;
          }
        }
      }
      for (;;)
      {
        int i14 = i12 + i3;
        localView2.layout(i14, i13, i14 + localView2.getMeasuredWidth(), i13 + localView2.getMeasuredHeight());
        i4++;
        i5++;
        break;
        i12 = m;
        m += localView2.getMeasuredWidth();
        break label156;
        i12 = Math.max((j - localView2.getMeasuredWidth()) / 2, m);
        break label156;
        i12 = j - i1 - localView2.getMeasuredWidth();
        i1 += localView2.getMeasuredWidth();
        break label156;
        i13 = n;
        n += localView2.getMeasuredHeight();
        continue;
        i13 = Math.max((k - localView2.getMeasuredHeight()) / 2, n);
        continue;
        i13 = k - i2 - localView2.getMeasuredHeight();
        i2 += localView2.getMeasuredHeight();
      }
    }
    int i6 = j - m - i1;
    for (int i7 = 0; i7 < i; i7++)
    {
      View localView1 = getChildAt(i7);
      if (localView1.getVisibility() != 8)
      {
        LayoutParams localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
        if (!isDecor)
        {
          ItemInfo localItemInfo = infoForChild(localView1);
          if (localItemInfo != null)
          {
            int i8 = m + (int)(i6 * offset);
            int i9 = n;
            if (needsMeasure)
            {
              needsMeasure = false;
              localView1.measure(View.MeasureSpec.makeMeasureSpec((int)(i6 * widthFactor), 1073741824), View.MeasureSpec.makeMeasureSpec(k - n - i2, 1073741824));
            }
            localView1.layout(i8, i9, i8 + localView1.getMeasuredWidth(), i9 + localView1.getMeasuredHeight());
          }
        }
      }
    }
    mTopPageBounds = n;
    mBottomPageBounds = (k - i2);
    mDecorChildCount = i4;
    if (mFirstLayout) {
      scrollToItem(mCurItem, false, 0, false);
    }
    mFirstLayout = false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(getDefaultSize(0, paramInt1), getDefaultSize(0, paramInt2));
    int i = getMeasuredWidth();
    mGutterSize = Math.min(i / 10, mDefaultGutterSize);
    int j = i - getPaddingLeft() - getPaddingRight();
    int k = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
    int m = getChildCount();
    int n = 0;
    if (n < m)
    {
      View localView2 = getChildAt(n);
      int i6;
      int i7;
      label167:
      int i8;
      if (localView2.getVisibility() != 8)
      {
        LayoutParams localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
        if ((localLayoutParams2 != null) && (isDecor))
        {
          int i3 = 0x7 & gravity;
          int i4 = 0x70 & gravity;
          int i5 = Integer.MIN_VALUE;
          i6 = Integer.MIN_VALUE;
          if ((i4 != 48) && (i4 != 80)) {
            break label302;
          }
          i7 = 1;
          if ((i3 != 3) && (i3 != 5)) {
            break label308;
          }
          i8 = 1;
          label182:
          if (i7 == 0) {
            break label314;
          }
          i5 = 1073741824;
          label192:
          int i9 = j;
          int i10 = k;
          if (width != -2)
          {
            i5 = 1073741824;
            if (width != -1) {
              i9 = width;
            }
          }
          if (height != -2)
          {
            i6 = 1073741824;
            if (height != -1) {
              i10 = height;
            }
          }
          localView2.measure(View.MeasureSpec.makeMeasureSpec(i9, i5), View.MeasureSpec.makeMeasureSpec(i10, i6));
          if (i7 == 0) {
            break label327;
          }
          k -= localView2.getMeasuredHeight();
        }
      }
      for (;;)
      {
        n++;
        break;
        label302:
        i7 = 0;
        break label167;
        label308:
        i8 = 0;
        break label182;
        label314:
        if (i8 == 0) {
          break label192;
        }
        i6 = 1073741824;
        break label192;
        label327:
        if (i8 != 0) {
          j -= localView2.getMeasuredWidth();
        }
      }
    }
    mChildWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(j, 1073741824);
    mChildHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(k, 1073741824);
    mInLayout = true;
    populate();
    mInLayout = false;
    int i1 = getChildCount();
    for (int i2 = 0; i2 < i1; i2++)
    {
      View localView1 = getChildAt(i2);
      if (localView1.getVisibility() != 8)
      {
        LayoutParams localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
        if ((localLayoutParams1 == null) || (!isDecor)) {
          localView1.measure(View.MeasureSpec.makeMeasureSpec((int)(j * widthFactor), 1073741824), mChildHeightMeasureSpec);
        }
      }
    }
  }
  
  @CallSuper
  protected void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
  {
    if (mDecorChildCount > 0)
    {
      int m = getScrollX();
      int n = getPaddingLeft();
      int i1 = getPaddingRight();
      int i2 = getWidth();
      int i3 = getChildCount();
      int i4 = 0;
      while (i4 < i3)
      {
        View localView2 = getChildAt(i4);
        LayoutParams localLayoutParams = (LayoutParams)localView2.getLayoutParams();
        if (!isDecor)
        {
          i4++;
        }
        else
        {
          int i5;
          switch (0x7 & gravity)
          {
          case 2: 
          case 4: 
          default: 
            i5 = n;
          }
          for (;;)
          {
            int i6 = i5 + m - localView2.getLeft();
            if (i6 == 0) {
              break;
            }
            localView2.offsetLeftAndRight(i6);
            break;
            i5 = n;
            n += localView2.getWidth();
            continue;
            i5 = Math.max((i2 - localView2.getMeasuredWidth()) / 2, n);
            continue;
            i5 = i2 - i1 - localView2.getMeasuredWidth();
            i1 += localView2.getMeasuredWidth();
          }
        }
      }
    }
    dispatchOnPageScrolled(paramInt1, paramFloat, paramInt2);
    if (mPageTransformer != null)
    {
      int i = getScrollX();
      int j = getChildCount();
      int k = 0;
      if (k < j)
      {
        View localView1 = getChildAt(k);
        if (getLayoutParamsisDecor) {}
        for (;;)
        {
          k++;
          break;
          float f = (localView1.getLeft() - i) / getClientWidth();
          mPageTransformer.transformPage(localView1, f);
        }
      }
    }
    mCalledSuper = true;
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    int i = getChildCount();
    int j;
    int k;
    int m;
    int n;
    if ((paramInt & 0x2) != 0)
    {
      j = 0;
      k = 1;
      m = i;
      n = j;
    }
    for (;;)
    {
      if (n == m) {
        break label108;
      }
      View localView = getChildAt(n);
      if (localView.getVisibility() == 0)
      {
        ItemInfo localItemInfo = infoForChild(localView);
        if ((localItemInfo != null) && (position == mCurItem) && (localView.requestFocus(paramInt, paramRect)))
        {
          return true;
          j = i - 1;
          k = -1;
          m = -1;
          break;
        }
      }
      n += k;
    }
    label108:
    return false;
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    SavedState localSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(localSavedState.getSuperState());
    if (mAdapter != null)
    {
      mAdapter.restoreState(adapterState, loader);
      setCurrentItemInternal(position, false, true);
      return;
    }
    mRestoredCurItem = position;
    mRestoredAdapterState = adapterState;
    mRestoredClassLoader = loader;
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    position = mCurItem;
    if (mAdapter != null) {
      adapterState = mAdapter.saveState();
    }
    return localSavedState;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramInt1 != paramInt3) {
      recomputeScrollPosition(paramInt1, paramInt3, mPageMargin, mPageMargin);
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (mFakeDragging) {
      return true;
    }
    if ((paramMotionEvent.getAction() == 0) && (paramMotionEvent.getEdgeFlags() != 0)) {
      return false;
    }
    if ((mAdapter == null) || (mAdapter.getCount() == 0)) {
      return false;
    }
    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }
    mVelocityTracker.addMovement(paramMotionEvent);
    int i = 0xFF & paramMotionEvent.getAction();
    boolean bool1 = false;
    switch (i)
    {
    }
    for (;;)
    {
      if (bool1) {
        ViewCompat.postInvalidateOnAnimation(this);
      }
      return true;
      mScroller.abortAnimation();
      mPopulatePending = false;
      populate();
      float f7 = paramMotionEvent.getX();
      mInitialMotionX = f7;
      mLastMotionX = f7;
      float f8 = paramMotionEvent.getY();
      mInitialMotionY = f8;
      mLastMotionY = f8;
      mActivePointerId = paramMotionEvent.getPointerId(0);
      bool1 = false;
      continue;
      float f4;
      if (!mIsBeingDragged)
      {
        int i1 = paramMotionEvent.findPointerIndex(mActivePointerId);
        if (i1 == -1)
        {
          bool1 = resetTouch();
          continue;
        }
        float f2 = paramMotionEvent.getX(i1);
        float f3 = Math.abs(f2 - mLastMotionX);
        f4 = paramMotionEvent.getY(i1);
        float f5 = Math.abs(f4 - mLastMotionY);
        if ((f3 > mTouchSlop) && (f3 > f5))
        {
          mIsBeingDragged = true;
          requestParentDisallowInterceptTouchEvent(true);
          if (f2 - mInitialMotionX <= 0.0F) {
            break label396;
          }
        }
      }
      label396:
      for (float f6 = mInitialMotionX + mTouchSlop;; f6 = mInitialMotionX - mTouchSlop)
      {
        mLastMotionX = f6;
        mLastMotionY = f4;
        setScrollState(1);
        setScrollingCacheEnabled(true);
        ViewParent localViewParent = getParent();
        if (localViewParent != null) {
          localViewParent.requestDisallowInterceptTouchEvent(true);
        }
        boolean bool4 = mIsBeingDragged;
        bool1 = false;
        if (!bool4) {
          break;
        }
        bool1 = false | performDrag(paramMotionEvent.getX(paramMotionEvent.findPointerIndex(mActivePointerId)));
        break;
      }
      boolean bool3 = mIsBeingDragged;
      bool1 = false;
      if (bool3)
      {
        VelocityTracker localVelocityTracker = mVelocityTracker;
        localVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        int k = (int)VelocityTrackerCompat.getXVelocity(localVelocityTracker, mActivePointerId);
        mPopulatePending = true;
        int m = getClientWidth();
        int n = getScrollX();
        ItemInfo localItemInfo = infoForCurrentScrollPosition();
        float f1 = mPageMargin / m;
        setCurrentItemInternal(determineTargetPage(position, (n / m - offset) / (f1 + widthFactor), k, (int)(paramMotionEvent.getX(paramMotionEvent.findPointerIndex(mActivePointerId)) - mInitialMotionX)), true, true, k);
        bool1 = resetTouch();
        continue;
        boolean bool2 = mIsBeingDragged;
        bool1 = false;
        if (bool2)
        {
          scrollToItem(mCurItem, true, 0, false);
          bool1 = resetTouch();
          continue;
          int j = MotionEventCompat.getActionIndex(paramMotionEvent);
          mLastMotionX = paramMotionEvent.getX(j);
          mActivePointerId = paramMotionEvent.getPointerId(j);
          bool1 = false;
          continue;
          onSecondaryPointerUp(paramMotionEvent);
          mLastMotionX = paramMotionEvent.getX(paramMotionEvent.findPointerIndex(mActivePointerId));
          bool1 = false;
        }
      }
    }
  }
  
  boolean pageLeft()
  {
    if (mCurItem > 0)
    {
      setCurrentItem(-1 + mCurItem, true);
      return true;
    }
    return false;
  }
  
  boolean pageRight()
  {
    if ((mAdapter != null) && (mCurItem < -1 + mAdapter.getCount()))
    {
      setCurrentItem(1 + mCurItem, true);
      return true;
    }
    return false;
  }
  
  void populate()
  {
    populate(mCurItem);
  }
  
  void populate(int paramInt)
  {
    int i = mCurItem;
    ItemInfo localItemInfo1 = null;
    if (i != paramInt)
    {
      localItemInfo1 = infoForPosition(mCurItem);
      mCurItem = paramInt;
    }
    if (mAdapter == null) {
      sortChildDrawingOrder();
    }
    label361:
    label375:
    label458:
    label466:
    label650:
    label662:
    label684:
    label770:
    label776:
    label889:
    label895:
    label911:
    label1002:
    label1008:
    label1141:
    label1252:
    label1258:
    for (;;)
    {
      return;
      if (mPopulatePending)
      {
        sortChildDrawingOrder();
        return;
      }
      if (getWindowToken() != null)
      {
        mAdapter.startUpdate(this);
        int j = mOffscreenPageLimit;
        int k = Math.max(0, mCurItem - j);
        int m = mAdapter.getCount();
        int n = Math.min(m - 1, j + mCurItem);
        if (m != mExpectedAdapterCount)
        {
          try
          {
            String str2 = getResources().getResourceName(getId());
            str1 = str2;
          }
          catch (Resources.NotFoundException localNotFoundException)
          {
            for (;;)
            {
              String str1 = Integer.toHexString(getId());
            }
          }
          throw new IllegalStateException("The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: " + mExpectedAdapterCount + ", found: " + m + " Pager id: " + str1 + " Pager class: " + getClass() + " Problematic adapter: " + mAdapter.getClass());
        }
        int i1 = 0;
        int i2 = mItems.size();
        Object localObject1 = null;
        if (i1 < i2)
        {
          ItemInfo localItemInfo8 = (ItemInfo)mItems.get(i1);
          if (position < mCurItem) {
            break label650;
          }
          int i21 = position;
          int i22 = mCurItem;
          localObject1 = null;
          if (i21 == i22) {
            localObject1 = localItemInfo8;
          }
        }
        if ((localObject1 == null) && (m > 0)) {
          localObject1 = addNewItem(mCurItem, i1);
        }
        float f1;
        int i7;
        ItemInfo localItemInfo5;
        int i8;
        float f2;
        int i9;
        float f3;
        int i10;
        ItemInfo localItemInfo6;
        float f4;
        int i12;
        PagerAdapter localPagerAdapter1;
        int i3;
        if (localObject1 != null)
        {
          f1 = 0.0F;
          i7 = i1 - 1;
          if (i7 >= 0)
          {
            localItemInfo5 = (ItemInfo)mItems.get(i7);
            i8 = getClientWidth();
            if (i8 > 0) {
              break label662;
            }
            f2 = 0.0F;
            i9 = -1 + mCurItem;
            if (i9 >= 0)
            {
              if ((f1 < f2) || (i9 >= k)) {
                break label776;
              }
              if (localItemInfo5 != null) {
                break label684;
              }
            }
            f3 = widthFactor;
            i10 = i1 + 1;
            if (f3 < 2.0F)
            {
              int i11 = mItems.size();
              if (i10 >= i11) {
                break label889;
              }
              localItemInfo6 = (ItemInfo)mItems.get(i10);
              if (i8 > 0) {
                break label895;
              }
              f4 = 0.0F;
              i12 = 1 + mCurItem;
              if (i12 < m)
              {
                if ((f3 < f4) || (i12 <= n)) {
                  break label1008;
                }
                if (localItemInfo6 != null) {
                  break label911;
                }
              }
            }
            calculatePageOffsets((ItemInfo)localObject1, i1, localItemInfo1);
          }
        }
        else
        {
          localPagerAdapter1 = mAdapter;
          i3 = mCurItem;
          if (localObject1 == null) {
            break label1141;
          }
        }
        for (Object localObject2 = object;; localObject2 = null)
        {
          localPagerAdapter1.setPrimaryItem(this, i3, localObject2);
          mAdapter.finishUpdate(this);
          int i4 = getChildCount();
          for (int i5 = 0; i5 < i4; i5++)
          {
            View localView3 = getChildAt(i5);
            LayoutParams localLayoutParams = (LayoutParams)localView3.getLayoutParams();
            childIndex = i5;
            if ((!isDecor) && (widthFactor == 0.0F))
            {
              ItemInfo localItemInfo4 = infoForChild(localView3);
              if (localItemInfo4 != null)
              {
                widthFactor = widthFactor;
                position = position;
              }
            }
          }
          i1++;
          break;
          localItemInfo5 = null;
          break label361;
          f2 = 2.0F - widthFactor + getPaddingLeft() / i8;
          break label375;
          int i20 = position;
          if ((i9 == i20) && (!scrolling))
          {
            mItems.remove(i7);
            PagerAdapter localPagerAdapter3 = mAdapter;
            Object localObject4 = object;
            localPagerAdapter3.destroyItem(this, i9, localObject4);
            i7--;
            i1--;
            if (i7 < 0) {
              break label770;
            }
          }
          for (localItemInfo5 = (ItemInfo)mItems.get(i7);; localItemInfo5 = null)
          {
            i9--;
            break;
          }
          if (localItemInfo5 != null)
          {
            int i19 = position;
            if (i9 == i19)
            {
              f1 += widthFactor;
              i7--;
              if (i7 >= 0) {}
              for (localItemInfo5 = (ItemInfo)mItems.get(i7);; localItemInfo5 = null) {
                break;
              }
            }
          }
          int i18 = i7 + 1;
          f1 += addNewItemwidthFactor;
          i1++;
          if (i7 >= 0) {}
          for (localItemInfo5 = (ItemInfo)mItems.get(i7);; localItemInfo5 = null) {
            break;
          }
          localItemInfo6 = null;
          break label458;
          f4 = 2.0F + getPaddingRight() / i8;
          break label466;
          int i16 = position;
          if ((i12 == i16) && (!scrolling))
          {
            mItems.remove(i10);
            PagerAdapter localPagerAdapter2 = mAdapter;
            Object localObject3 = object;
            localPagerAdapter2.destroyItem(this, i12, localObject3);
            int i17 = mItems.size();
            if (i10 >= i17) {
              break label1002;
            }
          }
          for (localItemInfo6 = (ItemInfo)mItems.get(i10);; localItemInfo6 = null)
          {
            i12++;
            break;
          }
          if (localItemInfo6 != null)
          {
            int i14 = position;
            if (i12 == i14)
            {
              f3 += widthFactor;
              i10++;
              int i15 = mItems.size();
              if (i10 < i15) {}
              for (localItemInfo6 = (ItemInfo)mItems.get(i10);; localItemInfo6 = null) {
                break;
              }
            }
          }
          ItemInfo localItemInfo7 = addNewItem(i12, i10);
          i10++;
          f3 += widthFactor;
          int i13 = mItems.size();
          if (i10 < i13) {}
          for (localItemInfo6 = (ItemInfo)mItems.get(i10);; localItemInfo6 = null) {
            break;
          }
        }
        sortChildDrawingOrder();
        if (hasFocus())
        {
          View localView1 = findFocus();
          if (localView1 != null) {}
          for (ItemInfo localItemInfo2 = infoForAnyChild(localView1);; localItemInfo2 = null)
          {
            if ((localItemInfo2 != null) && (position == mCurItem)) {
              break label1258;
            }
            for (int i6 = 0;; i6++)
            {
              if (i6 >= getChildCount()) {
                break label1252;
              }
              View localView2 = getChildAt(i6);
              ItemInfo localItemInfo3 = infoForChild(localView2);
              if ((localItemInfo3 != null) && (position == mCurItem) && (localView2.requestFocus(2))) {
                break;
              }
            }
            break;
          }
        }
      }
    }
  }
  
  public void removeOnAdapterChangeListener(@NonNull OnAdapterChangeListener paramOnAdapterChangeListener)
  {
    if (mAdapterChangeListeners != null) {
      mAdapterChangeListeners.remove(paramOnAdapterChangeListener);
    }
  }
  
  public void removeOnPageChangeListener(OnPageChangeListener paramOnPageChangeListener)
  {
    if (mOnPageChangeListeners != null) {
      mOnPageChangeListeners.remove(paramOnPageChangeListener);
    }
  }
  
  public void removeView(View paramView)
  {
    if (mInLayout)
    {
      removeViewInLayout(paramView);
      return;
    }
    super.removeView(paramView);
  }
  
  public void setAdapter(PagerAdapter paramPagerAdapter)
  {
    if (mAdapter != null)
    {
      mAdapter.setViewPagerObserver(null);
      mAdapter.startUpdate(this);
      for (int k = 0; k < mItems.size(); k++)
      {
        ItemInfo localItemInfo = (ItemInfo)mItems.get(k);
        mAdapter.destroyItem(this, position, object);
      }
      mAdapter.finishUpdate(this);
      mItems.clear();
      removeNonDecorViews();
      mCurItem = 0;
      scrollTo(0, 0);
    }
    PagerAdapter localPagerAdapter = mAdapter;
    mAdapter = paramPagerAdapter;
    mExpectedAdapterCount = 0;
    boolean bool;
    if (mAdapter != null)
    {
      if (mObserver == null) {
        mObserver = new PagerObserver();
      }
      mAdapter.setViewPagerObserver(mObserver);
      mPopulatePending = false;
      bool = mFirstLayout;
      mFirstLayout = true;
      mExpectedAdapterCount = mAdapter.getCount();
      if (mRestoredCurItem < 0) {
        break label297;
      }
      mAdapter.restoreState(mRestoredAdapterState, mRestoredClassLoader);
      setCurrentItemInternal(mRestoredCurItem, false, true);
      mRestoredCurItem = -1;
      mRestoredAdapterState = null;
      mRestoredClassLoader = null;
    }
    while ((mAdapterChangeListeners != null) && (!mAdapterChangeListeners.isEmpty()))
    {
      int i = 0;
      int j = mAdapterChangeListeners.size();
      while (i < j)
      {
        ((OnAdapterChangeListener)mAdapterChangeListeners.get(i)).onAdapterChanged(this, localPagerAdapter, paramPagerAdapter);
        i++;
      }
      label297:
      if (!bool) {
        populate();
      } else {
        requestLayout();
      }
    }
  }
  
  void setChildrenDrawingOrderEnabledCompat(boolean paramBoolean)
  {
    if ((Build.VERSION.SDK_INT < 7) || (mSetChildrenDrawingOrderEnabled == null)) {}
    try
    {
      Class[] arrayOfClass = new Class[1];
      arrayOfClass[0] = Boolean.TYPE;
      mSetChildrenDrawingOrderEnabled = ViewGroup.class.getDeclaredMethod("setChildrenDrawingOrderEnabled", arrayOfClass);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        try
        {
          Method localMethod = mSetChildrenDrawingOrderEnabled;
          Object[] arrayOfObject = new Object[1];
          arrayOfObject[0] = Boolean.valueOf(paramBoolean);
          localMethod.invoke(this, arrayOfObject);
          return;
        }
        catch (Exception localException)
        {
          Log.e("ViewPager", "Error changing children drawing order", localException);
        }
        localNoSuchMethodException = localNoSuchMethodException;
        Log.e("ViewPager", "Can't find setChildrenDrawingOrderEnabled", localNoSuchMethodException);
      }
    }
  }
  
  public void setCurrentItem(int paramInt)
  {
    mPopulatePending = false;
    if (!mFirstLayout) {}
    for (boolean bool = true;; bool = false)
    {
      setCurrentItemInternal(paramInt, bool, false);
      return;
    }
  }
  
  public void setCurrentItem(int paramInt, boolean paramBoolean)
  {
    mPopulatePending = false;
    setCurrentItemInternal(paramInt, paramBoolean, false);
  }
  
  void setCurrentItemInternal(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    setCurrentItemInternal(paramInt, paramBoolean1, paramBoolean2, 0);
  }
  
  void setCurrentItemInternal(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
  {
    boolean bool = true;
    if ((mAdapter == null) || (mAdapter.getCount() <= 0))
    {
      setScrollingCacheEnabled(false);
      return;
    }
    if ((!paramBoolean2) && (mCurItem == paramInt1) && (mItems.size() != 0))
    {
      setScrollingCacheEnabled(false);
      return;
    }
    if (paramInt1 < 0) {
      paramInt1 = 0;
    }
    for (;;)
    {
      int i = mOffscreenPageLimit;
      if ((paramInt1 <= i + mCurItem) && (paramInt1 >= mCurItem - i)) {
        break;
      }
      for (int j = 0; j < mItems.size(); j++) {
        mItems.get(j)).scrolling = bool;
      }
      if (paramInt1 >= mAdapter.getCount()) {
        paramInt1 = -1 + mAdapter.getCount();
      }
    }
    if (mCurItem != paramInt1) {}
    while (mFirstLayout)
    {
      mCurItem = paramInt1;
      if (bool) {
        dispatchOnPageSelected(paramInt1);
      }
      requestLayout();
      return;
      bool = false;
    }
    populate(paramInt1);
    scrollToItem(paramInt1, paramBoolean1, paramInt2, bool);
  }
  
  OnPageChangeListener setInternalPageChangeListener(OnPageChangeListener paramOnPageChangeListener)
  {
    OnPageChangeListener localOnPageChangeListener = mInternalPageChangeListener;
    mInternalPageChangeListener = paramOnPageChangeListener;
    return localOnPageChangeListener;
  }
  
  public void setOffscreenPageLimit(int paramInt)
  {
    if (paramInt < 1)
    {
      Log.w("ViewPager", "Requested offscreen page limit " + paramInt + " too small; defaulting to " + 1);
      paramInt = 1;
    }
    if (paramInt != mOffscreenPageLimit)
    {
      mOffscreenPageLimit = paramInt;
      populate();
    }
  }
  
  @Deprecated
  public void setOnPageChangeListener(OnPageChangeListener paramOnPageChangeListener)
  {
    mOnPageChangeListener = paramOnPageChangeListener;
  }
  
  public void setPageMargin(int paramInt)
  {
    int i = mPageMargin;
    mPageMargin = paramInt;
    int j = getWidth();
    recomputeScrollPosition(j, j, paramInt, i);
    requestLayout();
  }
  
  public void setPageMarginDrawable(@DrawableRes int paramInt)
  {
    setPageMarginDrawable(getContext().getResources().getDrawable(paramInt));
  }
  
  public void setPageMarginDrawable(Drawable paramDrawable)
  {
    mMarginDrawable = paramDrawable;
    if (paramDrawable != null) {
      refreshDrawableState();
    }
    if (paramDrawable == null) {}
    for (boolean bool = true;; bool = false)
    {
      setWillNotDraw(bool);
      invalidate();
      return;
    }
  }
  
  public void setPageTransformer(boolean paramBoolean, PageTransformer paramPageTransformer)
  {
    int i = 1;
    label27:
    int i1;
    if (Build.VERSION.SDK_INT >= 11)
    {
      if (paramPageTransformer == null) {
        break label74;
      }
      int j = i;
      if (mPageTransformer == null) {
        break label80;
      }
      int m = i;
      if (j == m) {
        break label86;
      }
      i1 = i;
      label37:
      mPageTransformer = paramPageTransformer;
      setChildrenDrawingOrderEnabledCompat(j);
      if (j == 0) {
        break label92;
      }
      if (paramBoolean) {
        i = 2;
      }
    }
    label74:
    label80:
    label86:
    label92:
    for (mDrawingOrder = i;; mDrawingOrder = 0)
    {
      if (i1 != 0) {
        populate();
      }
      return;
      int k = 0;
      break;
      int n = 0;
      break label27;
      i1 = 0;
      break label37;
    }
  }
  
  void setScrollState(int paramInt)
  {
    if (mScrollState == paramInt) {
      return;
    }
    mScrollState = paramInt;
    if (mPageTransformer != null) {
      if (paramInt == 0) {
        break label38;
      }
    }
    label38:
    for (boolean bool = true;; bool = false)
    {
      enableLayers(bool);
      dispatchOnScrollStateChanged(paramInt);
      return;
    }
  }
  
  void smoothScrollTo(int paramInt1, int paramInt2)
  {
    smoothScrollTo(paramInt1, paramInt2, 0);
  }
  
  void smoothScrollTo(int paramInt1, int paramInt2, int paramInt3)
  {
    if (getChildCount() == 0)
    {
      setScrollingCacheEnabled(false);
      return;
    }
    int i;
    int j;
    if ((mScroller != null) && (!mScroller.isFinished()))
    {
      i = 1;
      if (i == 0) {
        break label127;
      }
      if (!mIsScrollStarted) {
        break label115;
      }
      j = mScroller.getCurrX();
      label54:
      mScroller.abortAnimation();
      setScrollingCacheEnabled(false);
    }
    int k;
    int m;
    int n;
    for (;;)
    {
      k = getScrollY();
      m = paramInt1 - j;
      n = paramInt2 - k;
      if ((m != 0) || (n != 0)) {
        break label136;
      }
      completeScroll(false);
      populate();
      setScrollState(0);
      return;
      i = 0;
      break;
      label115:
      j = mScroller.getStartX();
      break label54;
      label127:
      j = getScrollX();
    }
    label136:
    setScrollingCacheEnabled(true);
    setScrollState(2);
    int i1 = getClientWidth();
    int i2 = i1 / 2;
    float f1 = Math.min(1.0F, 1.0F * Math.abs(m) / i1);
    float f2 = i2 + i2 * distanceInfluenceForSnapDuration(f1);
    int i3 = Math.abs(paramInt3);
    if (i3 > 0) {}
    float f3;
    for (int i4 = 4 * Math.round(1000.0F * Math.abs(f2 / i3));; i4 = (int)(100.0F * (1.0F + Math.abs(m) / (f3 + mPageMargin))))
    {
      int i5 = Math.min(i4, 600);
      mIsScrollStarted = false;
      mScroller.startScroll(j, k, m, n, i5);
      ViewCompat.postInvalidateOnAnimation(this);
      return;
      f3 = i1 * mAdapter.getPageWidth(mCurItem);
    }
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (super.verifyDrawable(paramDrawable)) || (paramDrawable == mMarginDrawable);
  }
  
  @Inherited
  @Retention(RetentionPolicy.RUNTIME)
  @Target({java.lang.annotation.ElementType.TYPE})
  public static @interface DecorView {}
  
  static class ItemInfo
  {
    Object object;
    float offset;
    int position;
    boolean scrolling;
    float widthFactor;
    
    ItemInfo() {}
  }
  
  public static class LayoutParams
    extends ViewGroup.LayoutParams
  {
    int childIndex;
    public int gravity;
    public boolean isDecor;
    boolean needsMeasure;
    int position;
    float widthFactor = 0.0F;
    
    public LayoutParams()
    {
      super(-1);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, ViewPager.LAYOUT_ATTRS);
      gravity = localTypedArray.getInteger(0, 48);
      localTypedArray.recycle();
    }
  }
  
  class MyAccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    MyAccessibilityDelegate() {}
    
    private boolean canScroll()
    {
      return (mAdapter != null) && (mAdapter.getCount() > 1);
    }
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(ViewPager.class.getName());
      AccessibilityRecordCompat localAccessibilityRecordCompat = AccessibilityEventCompat.asRecord(paramAccessibilityEvent);
      localAccessibilityRecordCompat.setScrollable(canScroll());
      if ((paramAccessibilityEvent.getEventType() == 4096) && (mAdapter != null))
      {
        localAccessibilityRecordCompat.setItemCount(mAdapter.getCount());
        localAccessibilityRecordCompat.setFromIndex(mCurItem);
        localAccessibilityRecordCompat.setToIndex(mCurItem);
      }
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
      paramAccessibilityNodeInfoCompat.setClassName(ViewPager.class.getName());
      paramAccessibilityNodeInfoCompat.setScrollable(canScroll());
      if (canScrollHorizontally(1)) {
        paramAccessibilityNodeInfoCompat.addAction(4096);
      }
      if (canScrollHorizontally(-1)) {
        paramAccessibilityNodeInfoCompat.addAction(8192);
      }
    }
    
    public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
    {
      if (super.performAccessibilityAction(paramView, paramInt, paramBundle)) {
        return true;
      }
      switch (paramInt)
      {
      default: 
        return false;
      case 4096: 
        if (canScrollHorizontally(1))
        {
          setCurrentItem(1 + mCurItem);
          return true;
        }
        return false;
      }
      if (canScrollHorizontally(-1))
      {
        setCurrentItem(-1 + mCurItem);
        return true;
      }
      return false;
    }
  }
  
  public static abstract interface OnAdapterChangeListener
  {
    public abstract void onAdapterChanged(@NonNull ViewPager paramViewPager, @Nullable PagerAdapter paramPagerAdapter1, @Nullable PagerAdapter paramPagerAdapter2);
  }
  
  public static abstract interface OnPageChangeListener
  {
    public abstract void onPageScrollStateChanged(int paramInt);
    
    public abstract void onPageScrolled(int paramInt1, float paramFloat, int paramInt2);
    
    public abstract void onPageSelected(int paramInt);
  }
  
  public static abstract interface PageTransformer
  {
    public abstract void transformPage(View paramView, float paramFloat);
  }
  
  private class PagerObserver
    extends DataSetObserver
  {
    PagerObserver() {}
    
    public void onChanged()
    {
      dataSetChanged();
    }
    
    public void onInvalidated()
    {
      dataSetChanged();
    }
  }
  
  public static class SavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
    {
      public ViewPager.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new ViewPager.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public ViewPager.SavedState[] newArray(int paramAnonymousInt)
      {
        return new ViewPager.SavedState[paramAnonymousInt];
      }
    });
    Parcelable adapterState;
    ClassLoader loader;
    int position;
    
    SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      if (paramClassLoader == null) {
        paramClassLoader = getClass().getClassLoader();
      }
      position = paramParcel.readInt();
      adapterState = paramParcel.readParcelable(paramClassLoader);
      loader = paramClassLoader;
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + position + "}";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(position);
      paramParcel.writeParcelable(adapterState, paramInt);
    }
  }
  
  public static class SimpleOnPageChangeListener
    implements ViewPager.OnPageChangeListener
  {
    public SimpleOnPageChangeListener() {}
    
    public void onPageScrollStateChanged(int paramInt) {}
    
    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {}
    
    public void onPageSelected(int paramInt) {}
  }
  
  static class ViewPositionComparator
    implements Comparator<View>
  {
    ViewPositionComparator() {}
    
    public int compare(View paramView1, View paramView2)
    {
      ViewPager.LayoutParams localLayoutParams1 = (ViewPager.LayoutParams)paramView1.getLayoutParams();
      ViewPager.LayoutParams localLayoutParams2 = (ViewPager.LayoutParams)paramView2.getLayoutParams();
      if (isDecor != isDecor)
      {
        if (isDecor) {
          return 1;
        }
        return -1;
      }
      return position - position;
    }
  }
}
