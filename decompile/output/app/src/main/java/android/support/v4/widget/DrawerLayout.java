package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewGroupCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import java.util.ArrayList;
import java.util.List;

public class DrawerLayout
  extends ViewGroup
  implements DrawerLayoutImpl
{
  private static final boolean ALLOW_EDGE_LOCK = false;
  static final boolean CAN_HIDE_DESCENDANTS = false;
  private static final boolean CHILDREN_DISALLOW_INTERCEPT = true;
  private static final int DEFAULT_SCRIM_COLOR = -1728053248;
  private static final int DRAWER_ELEVATION = 10;
  static final DrawerLayoutCompatImpl IMPL;
  static final int[] LAYOUT_ATTRS;
  public static final int LOCK_MODE_LOCKED_CLOSED = 1;
  public static final int LOCK_MODE_LOCKED_OPEN = 2;
  public static final int LOCK_MODE_UNDEFINED = 3;
  public static final int LOCK_MODE_UNLOCKED = 0;
  private static final int MIN_DRAWER_MARGIN = 64;
  private static final int MIN_FLING_VELOCITY = 400;
  private static final int PEEK_DELAY = 160;
  private static final boolean SET_DRAWER_SHADOW_FROM_ELEVATION = false;
  public static final int STATE_DRAGGING = 1;
  public static final int STATE_IDLE = 0;
  public static final int STATE_SETTLING = 2;
  private static final String TAG = "DrawerLayout";
  private static final float TOUCH_SLOP_SENSITIVITY = 1.0F;
  private final ChildAccessibilityDelegate mChildAccessibilityDelegate = new ChildAccessibilityDelegate();
  private boolean mChildrenCanceledTouch;
  private boolean mDisallowInterceptRequested;
  private boolean mDrawStatusBarBackground;
  private float mDrawerElevation;
  private int mDrawerState;
  private boolean mFirstLayout = true;
  private boolean mInLayout;
  private float mInitialMotionX;
  private float mInitialMotionY;
  private Object mLastInsets;
  private final ViewDragCallback mLeftCallback;
  private final ViewDragHelper mLeftDragger;
  @Nullable
  private DrawerListener mListener;
  private List<DrawerListener> mListeners;
  private int mLockModeEnd = 3;
  private int mLockModeLeft = 3;
  private int mLockModeRight = 3;
  private int mLockModeStart = 3;
  private int mMinDrawerMargin;
  private final ArrayList<View> mNonDrawerViews;
  private final ViewDragCallback mRightCallback;
  private final ViewDragHelper mRightDragger;
  private int mScrimColor = -1728053248;
  private float mScrimOpacity;
  private Paint mScrimPaint = new Paint();
  private Drawable mShadowEnd = null;
  private Drawable mShadowLeft = null;
  private Drawable mShadowLeftResolved;
  private Drawable mShadowRight = null;
  private Drawable mShadowRightResolved;
  private Drawable mShadowStart = null;
  private Drawable mStatusBarBackground;
  private CharSequence mTitleLeft;
  private CharSequence mTitleRight;
  
  static
  {
    boolean bool1 = true;
    int[] arrayOfInt = new int[bool1];
    arrayOfInt[0] = 16842931;
    LAYOUT_ATTRS = arrayOfInt;
    boolean bool2;
    if (Build.VERSION.SDK_INT >= 19)
    {
      bool2 = bool1;
      CAN_HIDE_DESCENDANTS = bool2;
      if (Build.VERSION.SDK_INT < 21) {
        break label65;
      }
    }
    for (;;)
    {
      SET_DRAWER_SHADOW_FROM_ELEVATION = bool1;
      if (Build.VERSION.SDK_INT < 21) {
        break label70;
      }
      IMPL = new DrawerLayoutCompatImplApi21();
      return;
      bool2 = false;
      break;
      label65:
      bool1 = false;
    }
    label70:
    IMPL = new DrawerLayoutCompatImplBase();
  }
  
  public DrawerLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public DrawerLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public DrawerLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setDescendantFocusability(262144);
    float f1 = getResourcesgetDisplayMetricsdensity;
    mMinDrawerMargin = ((int)(0.5F + 64.0F * f1));
    float f2 = 400.0F * f1;
    mLeftCallback = new ViewDragCallback(3);
    mRightCallback = new ViewDragCallback(5);
    mLeftDragger = ViewDragHelper.create(this, 1.0F, mLeftCallback);
    mLeftDragger.setEdgeTrackingEnabled(1);
    mLeftDragger.setMinVelocity(f2);
    mLeftCallback.setDragger(mLeftDragger);
    mRightDragger = ViewDragHelper.create(this, 1.0F, mRightCallback);
    mRightDragger.setEdgeTrackingEnabled(2);
    mRightDragger.setMinVelocity(f2);
    mRightCallback.setDragger(mRightDragger);
    setFocusableInTouchMode(true);
    ViewCompat.setImportantForAccessibility(this, 1);
    ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
    ViewGroupCompat.setMotionEventSplittingEnabled(this, false);
    if (ViewCompat.getFitsSystemWindows(this))
    {
      IMPL.configureApplyInsets(this);
      mStatusBarBackground = IMPL.getDefaultStatusBarBackground(paramContext);
    }
    mDrawerElevation = (10.0F * f1);
    mNonDrawerViews = new ArrayList();
  }
  
  static String gravityToString(int paramInt)
  {
    if ((paramInt & 0x3) == 3) {
      return "LEFT";
    }
    if ((paramInt & 0x5) == 5) {
      return "RIGHT";
    }
    return Integer.toHexString(paramInt);
  }
  
  private static boolean hasOpaqueBackground(View paramView)
  {
    Drawable localDrawable = paramView.getBackground();
    boolean bool = false;
    if (localDrawable != null)
    {
      int i = localDrawable.getOpacity();
      bool = false;
      if (i == -1) {
        bool = true;
      }
    }
    return bool;
  }
  
  private boolean hasPeekingDrawer()
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++) {
      if (getChildAtgetLayoutParamsisPeeking) {
        return true;
      }
    }
    return false;
  }
  
  private boolean hasVisibleDrawer()
  {
    return findVisibleDrawer() != null;
  }
  
  static boolean includeChildForAccessibility(View paramView)
  {
    return (ViewCompat.getImportantForAccessibility(paramView) != 4) && (ViewCompat.getImportantForAccessibility(paramView) != 2);
  }
  
  private boolean mirror(Drawable paramDrawable, int paramInt)
  {
    if ((paramDrawable == null) || (!DrawableCompat.isAutoMirrored(paramDrawable))) {
      return false;
    }
    DrawableCompat.setLayoutDirection(paramDrawable, paramInt);
    return true;
  }
  
  private Drawable resolveLeftShadow()
  {
    int i = ViewCompat.getLayoutDirection(this);
    if (i == 0)
    {
      if (mShadowStart != null)
      {
        mirror(mShadowStart, i);
        return mShadowStart;
      }
    }
    else if (mShadowEnd != null)
    {
      mirror(mShadowEnd, i);
      return mShadowEnd;
    }
    return mShadowLeft;
  }
  
  private Drawable resolveRightShadow()
  {
    int i = ViewCompat.getLayoutDirection(this);
    if (i == 0)
    {
      if (mShadowEnd != null)
      {
        mirror(mShadowEnd, i);
        return mShadowEnd;
      }
    }
    else if (mShadowStart != null)
    {
      mirror(mShadowStart, i);
      return mShadowStart;
    }
    return mShadowRight;
  }
  
  private void resolveShadowDrawables()
  {
    if (SET_DRAWER_SHADOW_FROM_ELEVATION) {
      return;
    }
    mShadowLeftResolved = resolveLeftShadow();
    mShadowRightResolved = resolveRightShadow();
  }
  
  private void updateChildrenImportantForAccessibility(View paramView, boolean paramBoolean)
  {
    int i = getChildCount();
    int j = 0;
    if (j < i)
    {
      View localView = getChildAt(j);
      if (((!paramBoolean) && (!isDrawerView(localView))) || ((paramBoolean) && (localView == paramView))) {
        ViewCompat.setImportantForAccessibility(localView, 1);
      }
      for (;;)
      {
        j++;
        break;
        ViewCompat.setImportantForAccessibility(localView, 4);
      }
    }
  }
  
  public void addDrawerListener(@NonNull DrawerListener paramDrawerListener)
  {
    if (paramDrawerListener == null) {
      return;
    }
    if (mListeners == null) {
      mListeners = new ArrayList();
    }
    mListeners.add(paramDrawerListener);
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    if (getDescendantFocusability() == 393216) {
      return;
    }
    int i = getChildCount();
    int j = 0;
    int k = 0;
    if (k < i)
    {
      View localView2 = getChildAt(k);
      if (isDrawerView(localView2)) {
        if (isDrawerOpen(localView2))
        {
          j = 1;
          localView2.addFocusables(paramArrayList, paramInt1, paramInt2);
        }
      }
      for (;;)
      {
        k++;
        break;
        mNonDrawerViews.add(localView2);
      }
    }
    if (j == 0)
    {
      int m = mNonDrawerViews.size();
      for (int n = 0; n < m; n++)
      {
        View localView1 = (View)mNonDrawerViews.get(n);
        if (localView1.getVisibility() == 0) {
          localView1.addFocusables(paramArrayList, paramInt1, paramInt2);
        }
      }
    }
    mNonDrawerViews.clear();
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    super.addView(paramView, paramInt, paramLayoutParams);
    if ((findOpenDrawer() != null) || (isDrawerView(paramView))) {
      ViewCompat.setImportantForAccessibility(paramView, 4);
    }
    for (;;)
    {
      if (!CAN_HIDE_DESCENDANTS) {
        ViewCompat.setAccessibilityDelegate(paramView, mChildAccessibilityDelegate);
      }
      return;
      ViewCompat.setImportantForAccessibility(paramView, 1);
    }
  }
  
  void cancelChildViewTouch()
  {
    if (!mChildrenCanceledTouch)
    {
      long l = SystemClock.uptimeMillis();
      MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
      int i = getChildCount();
      for (int j = 0; j < i; j++) {
        getChildAt(j).dispatchTouchEvent(localMotionEvent);
      }
      localMotionEvent.recycle();
      mChildrenCanceledTouch = true;
    }
  }
  
  boolean checkDrawerViewAbsoluteGravity(View paramView, int paramInt)
  {
    return (paramInt & getDrawerViewAbsoluteGravity(paramView)) == paramInt;
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return ((paramLayoutParams instanceof LayoutParams)) && (super.checkLayoutParams(paramLayoutParams));
  }
  
  public void closeDrawer(int paramInt)
  {
    closeDrawer(paramInt, true);
  }
  
  public void closeDrawer(int paramInt, boolean paramBoolean)
  {
    View localView = findDrawerWithGravity(paramInt);
    if (localView == null) {
      throw new IllegalArgumentException("No drawer view found with gravity " + gravityToString(paramInt));
    }
    closeDrawer(localView, paramBoolean);
  }
  
  public void closeDrawer(View paramView)
  {
    closeDrawer(paramView, true);
  }
  
  public void closeDrawer(View paramView, boolean paramBoolean)
  {
    if (!isDrawerView(paramView)) {
      throw new IllegalArgumentException("View " + paramView + " is not a sliding drawer");
    }
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (mFirstLayout)
    {
      onScreen = 0.0F;
      openState = 0;
    }
    for (;;)
    {
      invalidate();
      return;
      if (paramBoolean)
      {
        openState = (0x4 | openState);
        if (checkDrawerViewAbsoluteGravity(paramView, 3)) {
          mLeftDragger.smoothSlideViewTo(paramView, -paramView.getWidth(), paramView.getTop());
        } else {
          mRightDragger.smoothSlideViewTo(paramView, getWidth(), paramView.getTop());
        }
      }
      else
      {
        moveDrawerToOffset(paramView, 0.0F);
        updateDrawerState(gravity, 0, paramView);
        paramView.setVisibility(4);
      }
    }
  }
  
  public void closeDrawers()
  {
    closeDrawers(false);
  }
  
  void closeDrawers(boolean paramBoolean)
  {
    boolean bool = false;
    int i = getChildCount();
    int j = 0;
    while (j < i)
    {
      View localView = getChildAt(j);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if ((!isDrawerView(localView)) || ((paramBoolean) && (!isPeeking)))
      {
        j++;
      }
      else
      {
        int k = localView.getWidth();
        if (checkDrawerViewAbsoluteGravity(localView, 3)) {
          bool |= mLeftDragger.smoothSlideViewTo(localView, -k, localView.getTop());
        }
        for (;;)
        {
          isPeeking = false;
          break;
          bool |= mRightDragger.smoothSlideViewTo(localView, getWidth(), localView.getTop());
        }
      }
    }
    mLeftCallback.removeCallbacks();
    mRightCallback.removeCallbacks();
    if (bool) {
      invalidate();
    }
  }
  
  public void computeScroll()
  {
    int i = getChildCount();
    float f = 0.0F;
    for (int j = 0; j < i; j++) {
      f = Math.max(f, getChildAtgetLayoutParamsonScreen);
    }
    mScrimOpacity = f;
    if ((mLeftDragger.continueSettling(true) | mRightDragger.continueSettling(true))) {
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }
  
  void dispatchOnDrawerClosed(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if ((0x1 & openState) == 1)
    {
      openState = 0;
      if (mListeners != null) {
        for (int i = -1 + mListeners.size(); i >= 0; i--) {
          ((DrawerListener)mListeners.get(i)).onDrawerClosed(paramView);
        }
      }
      updateChildrenImportantForAccessibility(paramView, false);
      if (hasWindowFocus())
      {
        View localView = getRootView();
        if (localView != null) {
          localView.sendAccessibilityEvent(32);
        }
      }
    }
  }
  
  void dispatchOnDrawerOpened(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if ((0x1 & openState) == 0)
    {
      openState = 1;
      if (mListeners != null) {
        for (int i = -1 + mListeners.size(); i >= 0; i--) {
          ((DrawerListener)mListeners.get(i)).onDrawerOpened(paramView);
        }
      }
      updateChildrenImportantForAccessibility(paramView, true);
      if (hasWindowFocus()) {
        sendAccessibilityEvent(32);
      }
      paramView.requestFocus();
    }
  }
  
  void dispatchOnDrawerSlide(View paramView, float paramFloat)
  {
    if (mListeners != null) {
      for (int i = -1 + mListeners.size(); i >= 0; i--) {
        ((DrawerListener)mListeners.get(i)).onDrawerSlide(paramView, paramFloat);
      }
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    int i = getHeight();
    boolean bool1 = isContentView(paramView);
    int j = getWidth();
    int k = paramCanvas.save();
    int m = 0;
    if (bool1)
    {
      int i8 = getChildCount();
      int i9 = 0;
      if (i9 < i8)
      {
        View localView = getChildAt(i9);
        if ((localView == paramView) || (localView.getVisibility() != 0) || (!hasOpaqueBackground(localView)) || (!isDrawerView(localView)) || (localView.getHeight() < i)) {}
        for (;;)
        {
          i9++;
          break;
          if (checkDrawerViewAbsoluteGravity(localView, 3))
          {
            int i11 = localView.getRight();
            if (i11 > m) {
              m = i11;
            }
          }
          else
          {
            int i10 = localView.getLeft();
            if (i10 < j) {
              j = i10;
            }
          }
        }
      }
      paramCanvas.clipRect(m, 0, j, getHeight());
    }
    boolean bool2 = super.drawChild(paramCanvas, paramView, paramLong);
    paramCanvas.restoreToCount(k);
    if ((mScrimOpacity > 0.0F) && (bool1))
    {
      int i7 = (int)(((0xFF000000 & mScrimColor) >>> 24) * mScrimOpacity) << 24 | 0xFFFFFF & mScrimColor;
      mScrimPaint.setColor(i7);
      paramCanvas.drawRect(m, 0.0F, j, getHeight(), mScrimPaint);
    }
    do
    {
      return bool2;
      if ((mShadowLeftResolved != null) && (checkDrawerViewAbsoluteGravity(paramView, 3)))
      {
        int i4 = mShadowLeftResolved.getIntrinsicWidth();
        int i5 = paramView.getRight();
        int i6 = mLeftDragger.getEdgeSize();
        float f2 = Math.max(0.0F, Math.min(i5 / i6, 1.0F));
        mShadowLeftResolved.setBounds(i5, paramView.getTop(), i5 + i4, paramView.getBottom());
        mShadowLeftResolved.setAlpha((int)(255.0F * f2));
        mShadowLeftResolved.draw(paramCanvas);
        return bool2;
      }
    } while ((mShadowRightResolved == null) || (!checkDrawerViewAbsoluteGravity(paramView, 5)));
    int n = mShadowRightResolved.getIntrinsicWidth();
    int i1 = paramView.getLeft();
    int i2 = getWidth() - i1;
    int i3 = mRightDragger.getEdgeSize();
    float f1 = Math.max(0.0F, Math.min(i2 / i3, 1.0F));
    mShadowRightResolved.setBounds(i1 - n, paramView.getTop(), i1, paramView.getBottom());
    mShadowRightResolved.setAlpha((int)(255.0F * f1));
    mShadowRightResolved.draw(paramCanvas);
    return bool2;
  }
  
  View findDrawerWithGravity(int paramInt)
  {
    int i = 0x7 & GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    int j = getChildCount();
    for (int k = 0; k < j; k++)
    {
      View localView = getChildAt(k);
      if ((0x7 & getDrawerViewAbsoluteGravity(localView)) == i) {
        return localView;
      }
    }
    return null;
  }
  
  View findOpenDrawer()
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if ((0x1 & getLayoutParamsopenState) == 1) {
        return localView;
      }
    }
    return null;
  }
  
  View findVisibleDrawer()
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if ((isDrawerView(localView)) && (isDrawerVisible(localView))) {
        return localView;
      }
    }
    return null;
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-1, -1);
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof LayoutParams)) {
      return new LayoutParams((LayoutParams)paramLayoutParams);
    }
    if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
      return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  public float getDrawerElevation()
  {
    if (SET_DRAWER_SHADOW_FROM_ELEVATION) {
      return mDrawerElevation;
    }
    return 0.0F;
  }
  
  public int getDrawerLockMode(int paramInt)
  {
    int i = ViewCompat.getLayoutDirection(this);
    switch (paramInt)
    {
    }
    for (;;)
    {
      return 0;
      if (mLockModeLeft != 3) {
        return mLockModeLeft;
      }
      if (i == 0) {}
      for (int n = mLockModeStart; n != 3; n = mLockModeEnd) {
        return n;
      }
      if (mLockModeRight != 3) {
        return mLockModeRight;
      }
      if (i == 0) {}
      for (int m = mLockModeEnd; m != 3; m = mLockModeStart) {
        return m;
      }
      if (mLockModeStart != 3) {
        return mLockModeStart;
      }
      if (i == 0) {}
      for (int k = mLockModeLeft; k != 3; k = mLockModeRight) {
        return k;
      }
      if (mLockModeEnd != 3) {
        return mLockModeEnd;
      }
      if (i == 0) {}
      for (int j = mLockModeRight; j != 3; j = mLockModeLeft) {
        return j;
      }
    }
  }
  
  public int getDrawerLockMode(View paramView)
  {
    if (!isDrawerView(paramView)) {
      throw new IllegalArgumentException("View " + paramView + " is not a drawer");
    }
    return getDrawerLockMode(getLayoutParamsgravity);
  }
  
  @Nullable
  public CharSequence getDrawerTitle(int paramInt)
  {
    int i = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    if (i == 3) {
      return mTitleLeft;
    }
    if (i == 5) {
      return mTitleRight;
    }
    return null;
  }
  
  int getDrawerViewAbsoluteGravity(View paramView)
  {
    return GravityCompat.getAbsoluteGravity(getLayoutParamsgravity, ViewCompat.getLayoutDirection(this));
  }
  
  float getDrawerViewOffset(View paramView)
  {
    return getLayoutParamsonScreen;
  }
  
  public Drawable getStatusBarBackgroundDrawable()
  {
    return mStatusBarBackground;
  }
  
  boolean isContentView(View paramView)
  {
    return getLayoutParamsgravity == 0;
  }
  
  public boolean isDrawerOpen(int paramInt)
  {
    View localView = findDrawerWithGravity(paramInt);
    if (localView != null) {
      return isDrawerOpen(localView);
    }
    return false;
  }
  
  public boolean isDrawerOpen(View paramView)
  {
    if (!isDrawerView(paramView)) {
      throw new IllegalArgumentException("View " + paramView + " is not a drawer");
    }
    return (0x1 & getLayoutParamsopenState) == 1;
  }
  
  boolean isDrawerView(View paramView)
  {
    int i = GravityCompat.getAbsoluteGravity(getLayoutParamsgravity, ViewCompat.getLayoutDirection(paramView));
    if ((i & 0x3) != 0) {
      return true;
    }
    return (i & 0x5) != 0;
  }
  
  public boolean isDrawerVisible(int paramInt)
  {
    View localView = findDrawerWithGravity(paramInt);
    if (localView != null) {
      return isDrawerVisible(localView);
    }
    return false;
  }
  
  public boolean isDrawerVisible(View paramView)
  {
    if (!isDrawerView(paramView)) {
      throw new IllegalArgumentException("View " + paramView + " is not a drawer");
    }
    return getLayoutParamsonScreen > 0.0F;
  }
  
  void moveDrawerToOffset(View paramView, float paramFloat)
  {
    float f = getDrawerViewOffset(paramView);
    int i = paramView.getWidth();
    int j = (int)(f * i);
    int k = (int)(paramFloat * i) - j;
    if (checkDrawerViewAbsoluteGravity(paramView, 3)) {}
    for (;;)
    {
      paramView.offsetLeftAndRight(k);
      setDrawerViewOffset(paramView, paramFloat);
      return;
      k = -k;
    }
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
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if ((mDrawStatusBarBackground) && (mStatusBarBackground != null))
    {
      int i = IMPL.getTopInset(mLastInsets);
      if (i > 0)
      {
        mStatusBarBackground.setBounds(0, 0, getWidth(), i);
        mStatusBarBackground.draw(paramCanvas);
      }
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    boolean bool1 = mLeftDragger.shouldInterceptTouchEvent(paramMotionEvent) | mRightDragger.shouldInterceptTouchEvent(paramMotionEvent);
    int j = 0;
    switch (i)
    {
    }
    for (;;)
    {
      boolean bool2;
      if ((!bool1) && (j == 0) && (!hasPeekingDrawer()))
      {
        boolean bool3 = mChildrenCanceledTouch;
        bool2 = false;
        if (!bool3) {}
      }
      else
      {
        bool2 = true;
      }
      return bool2;
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      mInitialMotionX = f1;
      mInitialMotionY = f2;
      boolean bool5 = mScrimOpacity < 0.0F;
      j = 0;
      if (bool5)
      {
        View localView = mLeftDragger.findTopChildUnder((int)f1, (int)f2);
        j = 0;
        if (localView != null)
        {
          boolean bool6 = isContentView(localView);
          j = 0;
          if (bool6) {
            j = 1;
          }
        }
      }
      mDisallowInterceptRequested = false;
      mChildrenCanceledTouch = false;
      continue;
      boolean bool4 = mLeftDragger.checkTouchSlop(3);
      j = 0;
      if (bool4)
      {
        mLeftCallback.removeCallbacks();
        mRightCallback.removeCallbacks();
        j = 0;
        continue;
        closeDrawers(true);
        mDisallowInterceptRequested = false;
        mChildrenCanceledTouch = false;
        j = 0;
      }
    }
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt == 4) && (hasVisibleDrawer()))
    {
      paramKeyEvent.startTracking();
      return true;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramInt == 4)
    {
      View localView = findVisibleDrawer();
      if ((localView != null) && (getDrawerLockMode(localView) == 0)) {
        closeDrawers();
      }
      return localView != null;
    }
    return super.onKeyUp(paramInt, paramKeyEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    mInLayout = true;
    int i = paramInt3 - paramInt1;
    int j = getChildCount();
    int k = 0;
    if (k < j)
    {
      View localView = getChildAt(k);
      if (localView.getVisibility() == 8) {}
      LayoutParams localLayoutParams;
      for (;;)
      {
        k++;
        break;
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (!isContentView(localView)) {
          break label110;
        }
        localView.layout(leftMargin, topMargin, leftMargin + localView.getMeasuredWidth(), topMargin + localView.getMeasuredHeight());
      }
      label110:
      int m = localView.getMeasuredWidth();
      int n = localView.getMeasuredHeight();
      int i1;
      float f;
      label162:
      int i2;
      if (checkDrawerViewAbsoluteGravity(localView, 3))
      {
        i1 = -m + (int)(m * onScreen);
        f = (m + i1) / m;
        if (f == onScreen) {
          break label313;
        }
        i2 = 1;
        label176:
        switch (0x70 & gravity)
        {
        default: 
          localView.layout(i1, topMargin, i1 + m, n + topMargin);
          label237:
          if (i2 != 0) {
            setDrawerViewOffset(localView, f);
          }
          if (onScreen <= 0.0F) {
            break;
          }
        }
      }
      for (int i5 = 0; localView.getVisibility() != i5; i5 = 4)
      {
        localView.setVisibility(i5);
        break;
        i1 = i - (int)(m * onScreen);
        f = (i - i1) / m;
        break label162;
        label313:
        i2 = 0;
        break label176;
        int i6 = paramInt4 - paramInt2;
        localView.layout(i1, i6 - bottomMargin - localView.getMeasuredHeight(), i1 + m, i6 - bottomMargin);
        break label237;
        int i3 = paramInt4 - paramInt2;
        int i4 = (i3 - n) / 2;
        if (i4 < topMargin) {
          i4 = topMargin;
        }
        for (;;)
        {
          localView.layout(i1, i4, i1 + m, i4 + n);
          break;
          if (i4 + n > i3 - bottomMargin) {
            i4 = i3 - bottomMargin - n;
          }
        }
      }
    }
    mInLayout = false;
    mFirstLayout = false;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt1);
    int j = View.MeasureSpec.getMode(paramInt2);
    int k = View.MeasureSpec.getSize(paramInt1);
    int m = View.MeasureSpec.getSize(paramInt2);
    if ((i != 1073741824) || (j != 1073741824))
    {
      if (!isInEditMode()) {
        break label162;
      }
      if (i == Integer.MIN_VALUE) {
        if (j != Integer.MIN_VALUE) {
          break label149;
        }
      }
    }
    else
    {
      label60:
      setMeasuredDimension(k, m);
      if ((mLastInsets == null) || (!ViewCompat.getFitsSystemWindows(this))) {
        break label173;
      }
    }
    int i1;
    int i2;
    int i3;
    int i5;
    View localView;
    label149:
    label162:
    label173:
    for (int n = 1;; n = 0)
    {
      i1 = ViewCompat.getLayoutDirection(this);
      i2 = 0;
      i3 = 0;
      int i4 = getChildCount();
      for (i5 = 0;; i5++)
      {
        if (i5 >= i4) {
          return;
        }
        localView = getChildAt(i5);
        if (localView.getVisibility() != 8) {
          break;
        }
      }
      if (i != 0) {
        break;
      }
      k = 300;
      break;
      if (j != 0) {
        break label60;
      }
      m = 300;
      break label60;
      throw new IllegalArgumentException("DrawerLayout must be measured with MeasureSpec.EXACTLY.");
    }
    LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
    int i8;
    if (n != 0)
    {
      i8 = GravityCompat.getAbsoluteGravity(gravity, i1);
      if (!ViewCompat.getFitsSystemWindows(localView)) {
        break label287;
      }
      IMPL.dispatchChildInsets(localView, mLastInsets, i8);
    }
    for (;;)
    {
      if (!isContentView(localView)) {
        break label306;
      }
      localView.measure(View.MeasureSpec.makeMeasureSpec(k - leftMargin - rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(m - topMargin - bottomMargin, 1073741824));
      break;
      label287:
      IMPL.applyMarginInsets(localLayoutParams, mLastInsets, i8);
    }
    label306:
    if (isDrawerView(localView))
    {
      if ((SET_DRAWER_SHADOW_FROM_ELEVATION) && (ViewCompat.getElevation(localView) != mDrawerElevation)) {
        ViewCompat.setElevation(localView, mDrawerElevation);
      }
      int i6 = 0x7 & getDrawerViewAbsoluteGravity(localView);
      if (i6 == 3) {}
      for (int i7 = 1; ((i7 != 0) && (i2 != 0)) || ((i7 == 0) && (i3 != 0)); i7 = 0) {
        throw new IllegalStateException("Child drawer has absolute gravity " + gravityToString(i6) + " but this " + "DrawerLayout" + " already has a " + "drawer view along that edge");
      }
      if (i7 != 0) {
        i2 = 1;
      }
      for (;;)
      {
        localView.measure(getChildMeasureSpec(paramInt1, mMinDrawerMargin + leftMargin + rightMargin, width), getChildMeasureSpec(paramInt2, topMargin + bottomMargin, height));
        break;
        i3 = 1;
      }
    }
    throw new IllegalStateException("Child " + localView + " at index " + i5 + " does not have a valid layout_gravity - must be Gravity.LEFT, " + "Gravity.RIGHT or Gravity.NO_GRAVITY");
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState)) {
      super.onRestoreInstanceState(paramParcelable);
    }
    SavedState localSavedState;
    do
    {
      return;
      localSavedState = (SavedState)paramParcelable;
      super.onRestoreInstanceState(localSavedState.getSuperState());
      if (openDrawerGravity != 0)
      {
        View localView = findDrawerWithGravity(openDrawerGravity);
        if (localView != null) {
          openDrawer(localView);
        }
      }
      if (lockModeLeft != 3) {
        setDrawerLockMode(lockModeLeft, 3);
      }
      if (lockModeRight != 3) {
        setDrawerLockMode(lockModeRight, 5);
      }
      if (lockModeStart != 3) {
        setDrawerLockMode(lockModeStart, 8388611);
      }
    } while (lockModeEnd == 3);
    setDrawerLockMode(lockModeEnd, 8388613);
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    resolveShadowDrawables();
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    int i = getChildCount();
    label114:
    label120:
    label126:
    for (int j = 0;; j++)
    {
      LayoutParams localLayoutParams;
      int k;
      if (j < i)
      {
        localLayoutParams = (LayoutParams)getChildAt(j).getLayoutParams();
        if (openState != 1) {
          break label114;
        }
        k = 1;
        if (openState != 2) {
          break label120;
        }
      }
      for (int m = 1;; m = 0)
      {
        if ((k == 0) && (m == 0)) {
          break label126;
        }
        openDrawerGravity = gravity;
        lockModeLeft = mLockModeLeft;
        lockModeRight = mLockModeRight;
        lockModeStart = mLockModeStart;
        lockModeEnd = mLockModeEnd;
        return localSavedState;
        k = 0;
        break;
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    mLeftDragger.processTouchEvent(paramMotionEvent);
    mRightDragger.processTouchEvent(paramMotionEvent);
    switch (0xFF & paramMotionEvent.getAction())
    {
    case 2: 
    default: 
      return true;
    case 0: 
      float f5 = paramMotionEvent.getX();
      float f6 = paramMotionEvent.getY();
      mInitialMotionX = f5;
      mInitialMotionY = f6;
      mDisallowInterceptRequested = false;
      mChildrenCanceledTouch = false;
      return true;
    case 1: 
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      boolean bool = true;
      View localView1 = mLeftDragger.findTopChildUnder((int)f1, (int)f2);
      if ((localView1 != null) && (isContentView(localView1)))
      {
        float f3 = f1 - mInitialMotionX;
        float f4 = f2 - mInitialMotionY;
        int i = mLeftDragger.getTouchSlop();
        if (f3 * f3 + f4 * f4 < i * i)
        {
          View localView2 = findOpenDrawer();
          if (localView2 != null) {
            if (getDrawerLockMode(localView2) != 2) {
              break label217;
            }
          }
        }
      }
      label217:
      for (bool = true;; bool = false)
      {
        closeDrawers(bool);
        mDisallowInterceptRequested = false;
        return true;
      }
    }
    closeDrawers(true);
    mDisallowInterceptRequested = false;
    mChildrenCanceledTouch = false;
    return true;
  }
  
  public void openDrawer(int paramInt)
  {
    openDrawer(paramInt, true);
  }
  
  public void openDrawer(int paramInt, boolean paramBoolean)
  {
    View localView = findDrawerWithGravity(paramInt);
    if (localView == null) {
      throw new IllegalArgumentException("No drawer view found with gravity " + gravityToString(paramInt));
    }
    openDrawer(localView, paramBoolean);
  }
  
  public void openDrawer(View paramView)
  {
    openDrawer(paramView, true);
  }
  
  public void openDrawer(View paramView, boolean paramBoolean)
  {
    if (!isDrawerView(paramView)) {
      throw new IllegalArgumentException("View " + paramView + " is not a sliding drawer");
    }
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (mFirstLayout)
    {
      onScreen = 1.0F;
      openState = 1;
      updateChildrenImportantForAccessibility(paramView, true);
    }
    for (;;)
    {
      invalidate();
      return;
      if (paramBoolean)
      {
        openState = (0x2 | openState);
        if (checkDrawerViewAbsoluteGravity(paramView, 3)) {
          mLeftDragger.smoothSlideViewTo(paramView, 0, paramView.getTop());
        } else {
          mRightDragger.smoothSlideViewTo(paramView, getWidth() - paramView.getWidth(), paramView.getTop());
        }
      }
      else
      {
        moveDrawerToOffset(paramView, 1.0F);
        updateDrawerState(gravity, 0, paramView);
        paramView.setVisibility(0);
      }
    }
  }
  
  public void removeDrawerListener(@NonNull DrawerListener paramDrawerListener)
  {
    if (paramDrawerListener == null) {}
    while (mListeners == null) {
      return;
    }
    mListeners.remove(paramDrawerListener);
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    super.requestDisallowInterceptTouchEvent(paramBoolean);
    mDisallowInterceptRequested = paramBoolean;
    if (paramBoolean) {
      closeDrawers(true);
    }
  }
  
  public void requestLayout()
  {
    if (!mInLayout) {
      super.requestLayout();
    }
  }
  
  public void setChildInsets(Object paramObject, boolean paramBoolean)
  {
    mLastInsets = paramObject;
    mDrawStatusBarBackground = paramBoolean;
    if ((!paramBoolean) && (getBackground() == null)) {}
    for (boolean bool = true;; bool = false)
    {
      setWillNotDraw(bool);
      requestLayout();
      return;
    }
  }
  
  public void setDrawerElevation(float paramFloat)
  {
    mDrawerElevation = paramFloat;
    for (int i = 0; i < getChildCount(); i++)
    {
      View localView = getChildAt(i);
      if (isDrawerView(localView)) {
        ViewCompat.setElevation(localView, mDrawerElevation);
      }
    }
  }
  
  @Deprecated
  public void setDrawerListener(DrawerListener paramDrawerListener)
  {
    if (mListener != null) {
      removeDrawerListener(mListener);
    }
    if (paramDrawerListener != null) {
      addDrawerListener(paramDrawerListener);
    }
    mListener = paramDrawerListener;
  }
  
  public void setDrawerLockMode(int paramInt)
  {
    setDrawerLockMode(paramInt, 3);
    setDrawerLockMode(paramInt, 5);
  }
  
  public void setDrawerLockMode(int paramInt1, int paramInt2)
  {
    int i = GravityCompat.getAbsoluteGravity(paramInt2, ViewCompat.getLayoutDirection(this));
    ViewDragHelper localViewDragHelper;
    switch (paramInt2)
    {
    default: 
      if (paramInt1 != 0)
      {
        if (i == 3)
        {
          localViewDragHelper = mLeftDragger;
          label67:
          localViewDragHelper.cancel();
        }
      }
      else {
        switch (paramInt1)
        {
        }
      }
      break;
    }
    View localView1;
    do
    {
      View localView2;
      do
      {
        return;
        mLockModeLeft = paramInt1;
        break;
        mLockModeRight = paramInt1;
        break;
        mLockModeStart = paramInt1;
        break;
        mLockModeEnd = paramInt1;
        break;
        localViewDragHelper = mRightDragger;
        break label67;
        localView2 = findDrawerWithGravity(i);
      } while (localView2 == null);
      openDrawer(localView2);
      return;
      localView1 = findDrawerWithGravity(i);
    } while (localView1 == null);
    closeDrawer(localView1);
  }
  
  public void setDrawerLockMode(int paramInt, View paramView)
  {
    if (!isDrawerView(paramView)) {
      throw new IllegalArgumentException("View " + paramView + " is not a " + "drawer with appropriate layout_gravity");
    }
    setDrawerLockMode(paramInt, getLayoutParamsgravity);
  }
  
  public void setDrawerShadow(@DrawableRes int paramInt1, int paramInt2)
  {
    setDrawerShadow(getResources().getDrawable(paramInt1), paramInt2);
  }
  
  public void setDrawerShadow(Drawable paramDrawable, int paramInt)
  {
    if (SET_DRAWER_SHADOW_FROM_ELEVATION) {
      return;
    }
    if ((paramInt & 0x800003) == 8388611) {
      mShadowStart = paramDrawable;
    }
    for (;;)
    {
      resolveShadowDrawables();
      invalidate();
      return;
      if ((paramInt & 0x800005) == 8388613)
      {
        mShadowEnd = paramDrawable;
      }
      else if ((paramInt & 0x3) == 3)
      {
        mShadowLeft = paramDrawable;
      }
      else
      {
        if ((paramInt & 0x5) != 5) {
          break;
        }
        mShadowRight = paramDrawable;
      }
    }
  }
  
  public void setDrawerTitle(int paramInt, CharSequence paramCharSequence)
  {
    int i = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this));
    if (i == 3) {
      mTitleLeft = paramCharSequence;
    }
    while (i != 5) {
      return;
    }
    mTitleRight = paramCharSequence;
  }
  
  void setDrawerViewOffset(View paramView, float paramFloat)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (paramFloat == onScreen) {
      return;
    }
    onScreen = paramFloat;
    dispatchOnDrawerSlide(paramView, paramFloat);
  }
  
  public void setScrimColor(@ColorInt int paramInt)
  {
    mScrimColor = paramInt;
    invalidate();
  }
  
  public void setStatusBarBackground(int paramInt)
  {
    if (paramInt != 0) {}
    for (Drawable localDrawable = ContextCompat.getDrawable(getContext(), paramInt);; localDrawable = null)
    {
      mStatusBarBackground = localDrawable;
      invalidate();
      return;
    }
  }
  
  public void setStatusBarBackground(Drawable paramDrawable)
  {
    mStatusBarBackground = paramDrawable;
    invalidate();
  }
  
  public void setStatusBarBackgroundColor(@ColorInt int paramInt)
  {
    mStatusBarBackground = new ColorDrawable(paramInt);
    invalidate();
  }
  
  void updateDrawerState(int paramInt1, int paramInt2, View paramView)
  {
    int i = mLeftDragger.getViewDragState();
    int j = mRightDragger.getViewDragState();
    int k;
    LayoutParams localLayoutParams;
    if ((i == 1) || (j == 1))
    {
      k = 1;
      if ((paramView != null) && (paramInt2 == 0))
      {
        localLayoutParams = (LayoutParams)paramView.getLayoutParams();
        if (onScreen != 0.0F) {
          break label156;
        }
        dispatchOnDrawerClosed(paramView);
      }
    }
    for (;;)
    {
      if (k == mDrawerState) {
        return;
      }
      mDrawerState = k;
      if (mListeners == null) {
        return;
      }
      for (int m = -1 + mListeners.size(); m >= 0; m--) {
        ((DrawerListener)mListeners.get(m)).onDrawerStateChanged(k);
      }
      if ((i == 2) || (j == 2))
      {
        k = 2;
        break;
      }
      k = 0;
      break;
      label156:
      if (onScreen == 1.0F) {
        dispatchOnDrawerOpened(paramView);
      }
    }
  }
  
  class AccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    private final Rect mTmpRect = new Rect();
    
    AccessibilityDelegate() {}
    
    private void addChildrenForAccessibility(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat, ViewGroup paramViewGroup)
    {
      int i = paramViewGroup.getChildCount();
      for (int j = 0; j < i; j++)
      {
        View localView = paramViewGroup.getChildAt(j);
        if (DrawerLayout.includeChildForAccessibility(localView)) {
          paramAccessibilityNodeInfoCompat.addChild(localView);
        }
      }
    }
    
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
    }
    
    public boolean dispatchPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      if (paramAccessibilityEvent.getEventType() == 32)
      {
        List localList = paramAccessibilityEvent.getText();
        View localView = findVisibleDrawer();
        if (localView != null)
        {
          int i = getDrawerViewAbsoluteGravity(localView);
          CharSequence localCharSequence = getDrawerTitle(i);
          if (localCharSequence != null) {
            localList.add(localCharSequence);
          }
        }
        return true;
      }
      return super.dispatchPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
    }
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(DrawerLayout.class.getName());
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      if (DrawerLayout.CAN_HIDE_DESCENDANTS) {
        super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
      }
      for (;;)
      {
        paramAccessibilityNodeInfoCompat.setClassName(DrawerLayout.class.getName());
        paramAccessibilityNodeInfoCompat.setFocusable(false);
        paramAccessibilityNodeInfoCompat.setFocused(false);
        paramAccessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_FOCUS);
        paramAccessibilityNodeInfoCompat.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLEAR_FOCUS);
        return;
        AccessibilityNodeInfoCompat localAccessibilityNodeInfoCompat = AccessibilityNodeInfoCompat.obtain(paramAccessibilityNodeInfoCompat);
        super.onInitializeAccessibilityNodeInfo(paramView, localAccessibilityNodeInfoCompat);
        paramAccessibilityNodeInfoCompat.setSource(paramView);
        ViewParent localViewParent = ViewCompat.getParentForAccessibility(paramView);
        if ((localViewParent instanceof View)) {
          paramAccessibilityNodeInfoCompat.setParent((View)localViewParent);
        }
        copyNodeInfoNoChildren(paramAccessibilityNodeInfoCompat, localAccessibilityNodeInfoCompat);
        localAccessibilityNodeInfoCompat.recycle();
        addChildrenForAccessibility(paramAccessibilityNodeInfoCompat, (ViewGroup)paramView);
      }
    }
    
    public boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      if ((DrawerLayout.CAN_HIDE_DESCENDANTS) || (DrawerLayout.includeChildForAccessibility(paramView))) {
        return super.onRequestSendAccessibilityEvent(paramViewGroup, paramView, paramAccessibilityEvent);
      }
      return false;
    }
  }
  
  final class ChildAccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    ChildAccessibilityDelegate() {}
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
      if (!DrawerLayout.includeChildForAccessibility(paramView)) {
        paramAccessibilityNodeInfoCompat.setParent(null);
      }
    }
  }
  
  static abstract interface DrawerLayoutCompatImpl
  {
    public abstract void applyMarginInsets(ViewGroup.MarginLayoutParams paramMarginLayoutParams, Object paramObject, int paramInt);
    
    public abstract void configureApplyInsets(View paramView);
    
    public abstract void dispatchChildInsets(View paramView, Object paramObject, int paramInt);
    
    public abstract Drawable getDefaultStatusBarBackground(Context paramContext);
    
    public abstract int getTopInset(Object paramObject);
  }
  
  static class DrawerLayoutCompatImplApi21
    implements DrawerLayout.DrawerLayoutCompatImpl
  {
    DrawerLayoutCompatImplApi21() {}
    
    public void applyMarginInsets(ViewGroup.MarginLayoutParams paramMarginLayoutParams, Object paramObject, int paramInt)
    {
      DrawerLayoutCompatApi21.applyMarginInsets(paramMarginLayoutParams, paramObject, paramInt);
    }
    
    public void configureApplyInsets(View paramView)
    {
      DrawerLayoutCompatApi21.configureApplyInsets(paramView);
    }
    
    public void dispatchChildInsets(View paramView, Object paramObject, int paramInt)
    {
      DrawerLayoutCompatApi21.dispatchChildInsets(paramView, paramObject, paramInt);
    }
    
    public Drawable getDefaultStatusBarBackground(Context paramContext)
    {
      return DrawerLayoutCompatApi21.getDefaultStatusBarBackground(paramContext);
    }
    
    public int getTopInset(Object paramObject)
    {
      return DrawerLayoutCompatApi21.getTopInset(paramObject);
    }
  }
  
  static class DrawerLayoutCompatImplBase
    implements DrawerLayout.DrawerLayoutCompatImpl
  {
    DrawerLayoutCompatImplBase() {}
    
    public void applyMarginInsets(ViewGroup.MarginLayoutParams paramMarginLayoutParams, Object paramObject, int paramInt) {}
    
    public void configureApplyInsets(View paramView) {}
    
    public void dispatchChildInsets(View paramView, Object paramObject, int paramInt) {}
    
    public Drawable getDefaultStatusBarBackground(Context paramContext)
    {
      return null;
    }
    
    public int getTopInset(Object paramObject)
    {
      return 0;
    }
  }
  
  public static abstract interface DrawerListener
  {
    public abstract void onDrawerClosed(View paramView);
    
    public abstract void onDrawerOpened(View paramView);
    
    public abstract void onDrawerSlide(View paramView, float paramFloat);
    
    public abstract void onDrawerStateChanged(int paramInt);
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    private static final int FLAG_IS_CLOSING = 4;
    private static final int FLAG_IS_OPENED = 1;
    private static final int FLAG_IS_OPENING = 2;
    public int gravity = 0;
    boolean isPeeking;
    float onScreen;
    int openState;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(int paramInt1, int paramInt2, int paramInt3)
    {
      this(paramInt1, paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, DrawerLayout.LAYOUT_ATTRS);
      gravity = localTypedArray.getInt(0, 0);
      localTypedArray.recycle();
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      gravity = gravity;
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
  
  protected static class SavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
    {
      public DrawerLayout.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new DrawerLayout.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public DrawerLayout.SavedState[] newArray(int paramAnonymousInt)
      {
        return new DrawerLayout.SavedState[paramAnonymousInt];
      }
    });
    int lockModeEnd;
    int lockModeLeft;
    int lockModeRight;
    int lockModeStart;
    int openDrawerGravity = 0;
    
    public SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      openDrawerGravity = paramParcel.readInt();
      lockModeLeft = paramParcel.readInt();
      lockModeRight = paramParcel.readInt();
      lockModeStart = paramParcel.readInt();
      lockModeEnd = paramParcel.readInt();
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(openDrawerGravity);
      paramParcel.writeInt(lockModeLeft);
      paramParcel.writeInt(lockModeRight);
      paramParcel.writeInt(lockModeStart);
      paramParcel.writeInt(lockModeEnd);
    }
  }
  
  public static abstract class SimpleDrawerListener
    implements DrawerLayout.DrawerListener
  {
    public SimpleDrawerListener() {}
    
    public void onDrawerClosed(View paramView) {}
    
    public void onDrawerOpened(View paramView) {}
    
    public void onDrawerSlide(View paramView, float paramFloat) {}
    
    public void onDrawerStateChanged(int paramInt) {}
  }
  
  private class ViewDragCallback
    extends ViewDragHelper.Callback
  {
    private final int mAbsGravity;
    private ViewDragHelper mDragger;
    private final Runnable mPeekRunnable = new Runnable()
    {
      public void run()
      {
        peekDrawer();
      }
    };
    
    ViewDragCallback(int paramInt)
    {
      mAbsGravity = paramInt;
    }
    
    private void closeOtherDrawer()
    {
      int i = 3;
      if (mAbsGravity == i) {
        i = 5;
      }
      View localView = findDrawerWithGravity(i);
      if (localView != null) {
        closeDrawer(localView);
      }
    }
    
    public int clampViewPositionHorizontal(View paramView, int paramInt1, int paramInt2)
    {
      if (checkDrawerViewAbsoluteGravity(paramView, 3)) {
        return Math.max(-paramView.getWidth(), Math.min(paramInt1, 0));
      }
      int i = getWidth();
      return Math.max(i - paramView.getWidth(), Math.min(paramInt1, i));
    }
    
    public int clampViewPositionVertical(View paramView, int paramInt1, int paramInt2)
    {
      return paramView.getTop();
    }
    
    public int getViewHorizontalDragRange(View paramView)
    {
      if (isDrawerView(paramView)) {
        return paramView.getWidth();
      }
      return 0;
    }
    
    public void onEdgeDragStarted(int paramInt1, int paramInt2)
    {
      if ((paramInt1 & 0x1) == 1) {}
      for (View localView = findDrawerWithGravity(3);; localView = findDrawerWithGravity(5))
      {
        if ((localView != null) && (getDrawerLockMode(localView) == 0)) {
          mDragger.captureChildView(localView, paramInt2);
        }
        return;
      }
    }
    
    public boolean onEdgeLock(int paramInt)
    {
      return false;
    }
    
    public void onEdgeTouched(int paramInt1, int paramInt2)
    {
      postDelayed(mPeekRunnable, 160L);
    }
    
    public void onViewCaptured(View paramView, int paramInt)
    {
      getLayoutParamsisPeeking = false;
      closeOtherDrawer();
    }
    
    public void onViewDragStateChanged(int paramInt)
    {
      updateDrawerState(mAbsGravity, paramInt, mDragger.getCapturedView());
    }
    
    public void onViewPositionChanged(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int i = paramView.getWidth();
      float f;
      if (checkDrawerViewAbsoluteGravity(paramView, 3))
      {
        f = (i + paramInt1) / i;
        setDrawerViewOffset(paramView, f);
        if (f != 0.0F) {
          break label82;
        }
      }
      label82:
      for (int j = 4;; j = 0)
      {
        paramView.setVisibility(j);
        invalidate();
        return;
        f = (getWidth() - paramInt1) / i;
        break;
      }
    }
    
    public void onViewReleased(View paramView, float paramFloat1, float paramFloat2)
    {
      float f = getDrawerViewOffset(paramView);
      int i = paramView.getWidth();
      if (checkDrawerViewAbsoluteGravity(paramView, 3))
      {
        if ((paramFloat1 > 0.0F) || ((paramFloat1 == 0.0F) && (f > 0.5F))) {}
        for (k = 0;; k = -i)
        {
          mDragger.settleCapturedViewAt(k, paramView.getTop());
          invalidate();
          return;
        }
      }
      int j = getWidth();
      if ((paramFloat1 < 0.0F) || ((paramFloat1 == 0.0F) && (f > 0.5F))) {}
      for (int k = j - i;; k = j) {
        break;
      }
    }
    
    void peekDrawer()
    {
      int i = mDragger.getEdgeSize();
      int j;
      View localView;
      int m;
      if (mAbsGravity == 3)
      {
        j = 1;
        if (j == 0) {
          break label146;
        }
        localView = findDrawerWithGravity(3);
        m = 0;
        if (localView != null) {
          m = -localView.getWidth();
        }
      }
      for (int k = m + i;; k = getWidth() - i)
      {
        if ((localView != null) && (((j != 0) && (localView.getLeft() < k)) || ((j == 0) && (localView.getLeft() > k) && (getDrawerLockMode(localView) == 0))))
        {
          DrawerLayout.LayoutParams localLayoutParams = (DrawerLayout.LayoutParams)localView.getLayoutParams();
          mDragger.smoothSlideViewTo(localView, k, localView.getTop());
          isPeeking = true;
          invalidate();
          closeOtherDrawer();
          cancelChildViewTouch();
        }
        return;
        j = 0;
        break;
        label146:
        localView = findDrawerWithGravity(5);
      }
    }
    
    public void removeCallbacks()
    {
      removeCallbacks(mPeekRunnable);
    }
    
    public void setDragger(ViewDragHelper paramViewDragHelper)
    {
      mDragger = paramViewDragHelper;
    }
    
    public boolean tryCaptureView(View paramView, int paramInt)
    {
      return (isDrawerView(paramView)) && (checkDrawerViewAbsoluteGravity(paramView, mAbsGravity)) && (getDrawerLockMode(paramView) == 0);
    }
  }
}
