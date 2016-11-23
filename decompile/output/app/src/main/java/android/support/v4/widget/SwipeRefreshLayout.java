package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

public class SwipeRefreshLayout
  extends ViewGroup
  implements NestedScrollingParent, NestedScrollingChild
{
  private static final int ALPHA_ANIMATION_DURATION = 300;
  private static final int ANIMATE_TO_START_DURATION = 200;
  private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
  private static final int CIRCLE_BG_LIGHT = -328966;
  @VisibleForTesting
  static final int CIRCLE_DIAMETER = 40;
  @VisibleForTesting
  static final int CIRCLE_DIAMETER_LARGE = 56;
  private static final float DECELERATE_INTERPOLATION_FACTOR = 2.0F;
  public static final int DEFAULT = 1;
  private static final int DEFAULT_CIRCLE_TARGET = 64;
  private static final float DRAG_RATE = 0.5F;
  private static final int INVALID_POINTER = -1;
  public static final int LARGE = 0;
  private static final int[] LAYOUT_ATTRS = { 16842766 };
  private static final String LOG_TAG = SwipeRefreshLayout.class.getSimpleName();
  private static final int MAX_ALPHA = 255;
  private static final float MAX_PROGRESS_ANGLE = 0.8F;
  private static final int SCALE_DOWN_DURATION = 150;
  private static final int STARTING_PROGRESS_ALPHA = 76;
  private int mActivePointerId = -1;
  private Animation mAlphaMaxAnimation;
  private Animation mAlphaStartAnimation;
  private final Animation mAnimateToCorrectPosition = new Animation()
  {
    public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
    {
      if (!mUsingCustomStart) {}
      for (int i = (int)(mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop));; i = (int)mSpinnerFinalOffset)
      {
        int j = mFrom + (int)(paramAnonymousFloat * (i - mFrom)) - mCircleView.getTop();
        setTargetOffsetTopAndBottom(j, false);
        mProgress.setArrowScale(1.0F - paramAnonymousFloat);
        return;
      }
    }
  };
  private final Animation mAnimateToStartPosition = new Animation()
  {
    public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
    {
      moveToStart(paramAnonymousFloat);
    }
  };
  private OnChildScrollUpCallback mChildScrollUpCallback;
  private int mCircleDiameter;
  CircleImageView mCircleView;
  private int mCircleViewIndex = -1;
  int mCurrentTargetOffsetTop;
  private final DecelerateInterpolator mDecelerateInterpolator;
  protected int mFrom;
  private float mInitialDownY;
  private float mInitialMotionY;
  private boolean mIsBeingDragged;
  OnRefreshListener mListener;
  private int mMediumAnimationDuration;
  private boolean mNestedScrollInProgress;
  private final NestedScrollingChildHelper mNestedScrollingChildHelper;
  private final NestedScrollingParentHelper mNestedScrollingParentHelper;
  boolean mNotify;
  protected int mOriginalOffsetTop;
  private final int[] mParentOffsetInWindow = new int[2];
  private final int[] mParentScrollConsumed = new int[2];
  MaterialProgressDrawable mProgress;
  private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener()
  {
    public void onAnimationEnd(Animation paramAnonymousAnimation)
    {
      if (mRefreshing)
      {
        mProgress.setAlpha(255);
        mProgress.start();
        if ((mNotify) && (mListener != null)) {
          mListener.onRefresh();
        }
        mCurrentTargetOffsetTop = mCircleView.getTop();
        return;
      }
      reset();
    }
    
    public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
    
    public void onAnimationStart(Animation paramAnonymousAnimation) {}
  };
  boolean mRefreshing = false;
  private boolean mReturningToStart;
  boolean mScale;
  private Animation mScaleAnimation;
  private Animation mScaleDownAnimation;
  private Animation mScaleDownToStartAnimation;
  float mSpinnerFinalOffset;
  float mStartingScale;
  private View mTarget;
  private float mTotalDragDistance = -1.0F;
  private float mTotalUnconsumed;
  private int mTouchSlop;
  boolean mUsingCustomStart;
  
  public SwipeRefreshLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SwipeRefreshLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    mTouchSlop = ViewConfiguration.get(paramContext).getScaledTouchSlop();
    mMediumAnimationDuration = getResources().getInteger(17694721);
    setWillNotDraw(false);
    mDecelerateInterpolator = new DecelerateInterpolator(2.0F);
    DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
    mCircleDiameter = ((int)(40.0F * density));
    createProgressView();
    ViewCompat.setChildrenDrawingOrderEnabled(this, true);
    mSpinnerFinalOffset = (64.0F * density);
    mTotalDragDistance = mSpinnerFinalOffset;
    mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
    setNestedScrollingEnabled(true);
    int i = -mCircleDiameter;
    mCurrentTargetOffsetTop = i;
    mOriginalOffsetTop = i;
    moveToStart(1.0F);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, LAYOUT_ATTRS);
    setEnabled(localTypedArray.getBoolean(0, true));
    localTypedArray.recycle();
  }
  
  private void animateOffsetToCorrectPosition(int paramInt, Animation.AnimationListener paramAnimationListener)
  {
    mFrom = paramInt;
    mAnimateToCorrectPosition.reset();
    mAnimateToCorrectPosition.setDuration(200L);
    mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
    if (paramAnimationListener != null) {
      mCircleView.setAnimationListener(paramAnimationListener);
    }
    mCircleView.clearAnimation();
    mCircleView.startAnimation(mAnimateToCorrectPosition);
  }
  
  private void animateOffsetToStartPosition(int paramInt, Animation.AnimationListener paramAnimationListener)
  {
    if (mScale)
    {
      startScaleDownReturnToStartAnimation(paramInt, paramAnimationListener);
      return;
    }
    mFrom = paramInt;
    mAnimateToStartPosition.reset();
    mAnimateToStartPosition.setDuration(200L);
    mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
    if (paramAnimationListener != null) {
      mCircleView.setAnimationListener(paramAnimationListener);
    }
    mCircleView.clearAnimation();
    mCircleView.startAnimation(mAnimateToStartPosition);
  }
  
  private void createProgressView()
  {
    mCircleView = new CircleImageView(getContext(), -328966);
    mProgress = new MaterialProgressDrawable(getContext(), this);
    mProgress.setBackgroundColor(-328966);
    mCircleView.setImageDrawable(mProgress);
    mCircleView.setVisibility(8);
    addView(mCircleView);
  }
  
  private void ensureTarget()
  {
    if (mTarget == null) {}
    for (int i = 0;; i++) {
      if (i < getChildCount())
      {
        View localView = getChildAt(i);
        if (!localView.equals(mCircleView)) {
          mTarget = localView;
        }
      }
      else
      {
        return;
      }
    }
  }
  
  private void finishSpinner(float paramFloat)
  {
    if (paramFloat > mTotalDragDistance)
    {
      setRefreshing(true, true);
      return;
    }
    mRefreshing = false;
    mProgress.setStartEndTrim(0.0F, 0.0F);
    boolean bool = mScale;
    Animation.AnimationListener local5 = null;
    if (!bool) {
      local5 = new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnonymousAnimation)
        {
          if (!mScale) {
            startScaleDownAnimation(null);
          }
        }
        
        public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
        
        public void onAnimationStart(Animation paramAnonymousAnimation) {}
      };
    }
    animateOffsetToStartPosition(mCurrentTargetOffsetTop, local5);
    mProgress.showArrow(false);
  }
  
  private boolean isAlphaUsedForScale()
  {
    return Build.VERSION.SDK_INT < 11;
  }
  
  private boolean isAnimationRunning(Animation paramAnimation)
  {
    return (paramAnimation != null) && (paramAnimation.hasStarted()) && (!paramAnimation.hasEnded());
  }
  
  private void moveSpinner(float paramFloat)
  {
    mProgress.showArrow(true);
    float f1 = Math.min(1.0F, Math.abs(paramFloat / mTotalDragDistance));
    float f2 = 5.0F * (float)Math.max(f1 - 0.4D, 0.0D) / 3.0F;
    float f3 = Math.abs(paramFloat) - mTotalDragDistance;
    float f4;
    float f6;
    int i;
    if (mUsingCustomStart)
    {
      f4 = mSpinnerFinalOffset - mOriginalOffsetTop;
      float f5 = Math.max(0.0F, Math.min(f3, 2.0F * f4) / f4);
      f6 = 2.0F * (float)(f5 / 4.0F - Math.pow(f5 / 4.0F, 2.0D));
      float f7 = 2.0F * (f4 * f6);
      i = mOriginalOffsetTop + (int)(f7 + f4 * f1);
      if (mCircleView.getVisibility() != 0) {
        mCircleView.setVisibility(0);
      }
      if (!mScale)
      {
        ViewCompat.setScaleX(mCircleView, 1.0F);
        ViewCompat.setScaleY(mCircleView, 1.0F);
      }
      if (mScale) {
        setAnimationProgress(Math.min(1.0F, paramFloat / mTotalDragDistance));
      }
      if (paramFloat >= mTotalDragDistance) {
        break label321;
      }
      if ((mProgress.getAlpha() > 76) && (!isAnimationRunning(mAlphaStartAnimation))) {
        startProgressAlphaStartAnimation();
      }
    }
    for (;;)
    {
      float f8 = f2 * 0.8F;
      mProgress.setStartEndTrim(0.0F, Math.min(0.8F, f8));
      mProgress.setArrowScale(Math.min(1.0F, f2));
      float f9 = 0.5F * (-0.25F + 0.4F * f2 + 2.0F * f6);
      mProgress.setProgressRotation(f9);
      setTargetOffsetTopAndBottom(i - mCurrentTargetOffsetTop, true);
      return;
      f4 = mSpinnerFinalOffset;
      break;
      label321:
      if ((mProgress.getAlpha() < 255) && (!isAnimationRunning(mAlphaMaxAnimation))) {
        startProgressAlphaMaxAnimation();
      }
    }
  }
  
  private void onSecondaryPointerUp(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionIndex(paramMotionEvent);
    if (paramMotionEvent.getPointerId(i) == mActivePointerId) {
      if (i != 0) {
        break label33;
      }
    }
    label33:
    for (int j = 1;; j = 0)
    {
      mActivePointerId = paramMotionEvent.getPointerId(j);
      return;
    }
  }
  
  private void setColorViewAlpha(int paramInt)
  {
    mCircleView.getBackground().setAlpha(paramInt);
    mProgress.setAlpha(paramInt);
  }
  
  private void setRefreshing(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (mRefreshing != paramBoolean1)
    {
      mNotify = paramBoolean2;
      ensureTarget();
      mRefreshing = paramBoolean1;
      if (mRefreshing) {
        animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener);
      }
    }
    else
    {
      return;
    }
    startScaleDownAnimation(mRefreshListener);
  }
  
  private Animation startAlphaAnimation(final int paramInt1, final int paramInt2)
  {
    if ((mScale) && (isAlphaUsedForScale())) {
      return null;
    }
    Animation local4 = new Animation()
    {
      public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
      {
        mProgress.setAlpha((int)(paramInt1 + paramAnonymousFloat * (paramInt2 - paramInt1)));
      }
    };
    local4.setDuration(300L);
    mCircleView.setAnimationListener(null);
    mCircleView.clearAnimation();
    mCircleView.startAnimation(local4);
    return local4;
  }
  
  private void startDragging(float paramFloat)
  {
    if ((paramFloat - mInitialDownY > mTouchSlop) && (!mIsBeingDragged))
    {
      mInitialMotionY = (mInitialDownY + mTouchSlop);
      mIsBeingDragged = true;
      mProgress.setAlpha(76);
    }
  }
  
  private void startProgressAlphaMaxAnimation()
  {
    mAlphaMaxAnimation = startAlphaAnimation(mProgress.getAlpha(), 255);
  }
  
  private void startProgressAlphaStartAnimation()
  {
    mAlphaStartAnimation = startAlphaAnimation(mProgress.getAlpha(), 76);
  }
  
  private void startScaleDownReturnToStartAnimation(int paramInt, Animation.AnimationListener paramAnimationListener)
  {
    mFrom = paramInt;
    if (isAlphaUsedForScale()) {}
    for (mStartingScale = mProgress.getAlpha();; mStartingScale = ViewCompat.getScaleX(mCircleView))
    {
      mScaleDownToStartAnimation = new Animation()
      {
        public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
        {
          float f = mStartingScale + paramAnonymousFloat * -mStartingScale;
          setAnimationProgress(f);
          moveToStart(paramAnonymousFloat);
        }
      };
      mScaleDownToStartAnimation.setDuration(150L);
      if (paramAnimationListener != null) {
        mCircleView.setAnimationListener(paramAnimationListener);
      }
      mCircleView.clearAnimation();
      mCircleView.startAnimation(mScaleDownToStartAnimation);
      return;
    }
  }
  
  private void startScaleUpAnimation(Animation.AnimationListener paramAnimationListener)
  {
    mCircleView.setVisibility(0);
    if (Build.VERSION.SDK_INT >= 11) {
      mProgress.setAlpha(255);
    }
    mScaleAnimation = new Animation()
    {
      public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
      {
        setAnimationProgress(paramAnonymousFloat);
      }
    };
    mScaleAnimation.setDuration(mMediumAnimationDuration);
    if (paramAnimationListener != null) {
      mCircleView.setAnimationListener(paramAnimationListener);
    }
    mCircleView.clearAnimation();
    mCircleView.startAnimation(mScaleAnimation);
  }
  
  public boolean canChildScrollUp()
  {
    boolean bool1 = true;
    if (mChildScrollUpCallback != null) {
      bool1 = mChildScrollUpCallback.canChildScrollUp(this, mTarget);
    }
    AbsListView localAbsListView;
    do
    {
      return bool1;
      if (Build.VERSION.SDK_INT >= 14) {
        break label117;
      }
      if (!(mTarget instanceof AbsListView)) {
        break;
      }
      localAbsListView = (AbsListView)mTarget;
    } while ((localAbsListView.getChildCount() > 0) && ((localAbsListView.getFirstVisiblePosition() > 0) || (localAbsListView.getChildAt(0).getTop() < localAbsListView.getPaddingTop())));
    return false;
    boolean bool2;
    if (!ViewCompat.canScrollVertically(mTarget, -1))
    {
      int i = mTarget.getScrollY();
      bool2 = false;
      if (i <= 0) {}
    }
    else
    {
      bool2 = bool1;
    }
    return bool2;
    label117:
    return ViewCompat.canScrollVertically(mTarget, -1);
  }
  
  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    return mNestedScrollingChildHelper.dispatchNestedFling(paramFloat1, paramFloat2, paramBoolean);
  }
  
  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2)
  {
    return mNestedScrollingChildHelper.dispatchNestedPreFling(paramFloat1, paramFloat2);
  }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    return mNestedScrollingChildHelper.dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    return mNestedScrollingChildHelper.dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
  }
  
  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    if (mCircleViewIndex < 0) {}
    do
    {
      return paramInt2;
      if (paramInt2 == paramInt1 - 1) {
        return mCircleViewIndex;
      }
    } while (paramInt2 < mCircleViewIndex);
    return paramInt2 + 1;
  }
  
  public int getNestedScrollAxes()
  {
    return mNestedScrollingParentHelper.getNestedScrollAxes();
  }
  
  public int getProgressCircleDiameter()
  {
    return mCircleDiameter;
  }
  
  public boolean hasNestedScrollingParent()
  {
    return mNestedScrollingChildHelper.hasNestedScrollingParent();
  }
  
  public boolean isNestedScrollingEnabled()
  {
    return mNestedScrollingChildHelper.isNestedScrollingEnabled();
  }
  
  public boolean isRefreshing()
  {
    return mRefreshing;
  }
  
  void moveToStart(float paramFloat)
  {
    setTargetOffsetTopAndBottom(mFrom + (int)(paramFloat * (mOriginalOffsetTop - mFrom)) - mCircleView.getTop(), false);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    reset();
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    ensureTarget();
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if ((mReturningToStart) && (i == 0)) {
      mReturningToStart = false;
    }
    if ((!isEnabled()) || (mReturningToStart) || (canChildScrollUp()) || (mRefreshing) || (mNestedScrollInProgress)) {
      return false;
    }
    switch (i)
    {
    }
    for (;;)
    {
      return mIsBeingDragged;
      setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCircleView.getTop(), true);
      mActivePointerId = paramMotionEvent.getPointerId(0);
      mIsBeingDragged = false;
      int k = paramMotionEvent.findPointerIndex(mActivePointerId);
      if (k < 0) {
        break;
      }
      mInitialDownY = paramMotionEvent.getY(k);
      continue;
      if (mActivePointerId == -1)
      {
        Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
        return false;
      }
      int j = paramMotionEvent.findPointerIndex(mActivePointerId);
      if (j < 0) {
        break;
      }
      startDragging(paramMotionEvent.getY(j));
      continue;
      onSecondaryPointerUp(paramMotionEvent);
      continue;
      mIsBeingDragged = false;
      mActivePointerId = -1;
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = getMeasuredWidth();
    int j = getMeasuredHeight();
    if (getChildCount() == 0) {}
    do
    {
      return;
      if (mTarget == null) {
        ensureTarget();
      }
    } while (mTarget == null);
    View localView = mTarget;
    int k = getPaddingLeft();
    int m = getPaddingTop();
    int n = i - getPaddingLeft() - getPaddingRight();
    int i1 = j - getPaddingTop() - getPaddingBottom();
    localView.layout(k, m, k + n, m + i1);
    int i2 = mCircleView.getMeasuredWidth();
    int i3 = mCircleView.getMeasuredHeight();
    mCircleView.layout(i / 2 - i2 / 2, mCurrentTargetOffsetTop, i / 2 + i2 / 2, i3 + mCurrentTargetOffsetTop);
  }
  
  public void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if (mTarget == null) {
      ensureTarget();
    }
    if (mTarget == null) {}
    for (;;)
    {
      return;
      mTarget.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), 1073741824));
      mCircleView.measure(View.MeasureSpec.makeMeasureSpec(mCircleDiameter, 1073741824), View.MeasureSpec.makeMeasureSpec(mCircleDiameter, 1073741824));
      mCircleViewIndex = -1;
      for (int i = 0; i < getChildCount(); i++) {
        if (getChildAt(i) == mCircleView)
        {
          mCircleViewIndex = i;
          return;
        }
      }
    }
  }
  
  public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    return dispatchNestedFling(paramFloat1, paramFloat2, paramBoolean);
  }
  
  public boolean onNestedPreFling(View paramView, float paramFloat1, float paramFloat2)
  {
    return dispatchNestedPreFling(paramFloat1, paramFloat2);
  }
  
  public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    if ((paramInt2 > 0) && (mTotalUnconsumed > 0.0F))
    {
      if (paramInt2 <= mTotalUnconsumed) {
        break label143;
      }
      paramArrayOfInt[1] = (paramInt2 - (int)mTotalUnconsumed);
      mTotalUnconsumed = 0.0F;
    }
    for (;;)
    {
      moveSpinner(mTotalUnconsumed);
      if ((mUsingCustomStart) && (paramInt2 > 0) && (mTotalUnconsumed == 0.0F) && (Math.abs(paramInt2 - paramArrayOfInt[1]) > 0)) {
        mCircleView.setVisibility(8);
      }
      int[] arrayOfInt = mParentScrollConsumed;
      if (dispatchNestedPreScroll(paramInt1 - paramArrayOfInt[0], paramInt2 - paramArrayOfInt[1], arrayOfInt, null))
      {
        paramArrayOfInt[0] += arrayOfInt[0];
        paramArrayOfInt[1] += arrayOfInt[1];
      }
      return;
      label143:
      mTotalUnconsumed -= paramInt2;
      paramArrayOfInt[1] = paramInt2;
    }
  }
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, mParentOffsetInWindow);
    int i = paramInt4 + mParentOffsetInWindow[1];
    if ((i < 0) && (!canChildScrollUp()))
    {
      mTotalUnconsumed += Math.abs(i);
      moveSpinner(mTotalUnconsumed);
    }
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt)
  {
    mNestedScrollingParentHelper.onNestedScrollAccepted(paramView1, paramView2, paramInt);
    startNestedScroll(paramInt & 0x2);
    mTotalUnconsumed = 0.0F;
    mNestedScrollInProgress = true;
  }
  
  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt)
  {
    return (isEnabled()) && (!mReturningToStart) && (!mRefreshing) && ((paramInt & 0x2) != 0);
  }
  
  public void onStopNestedScroll(View paramView)
  {
    mNestedScrollingParentHelper.onStopNestedScroll(paramView);
    mNestedScrollInProgress = false;
    if (mTotalUnconsumed > 0.0F)
    {
      finishSpinner(mTotalUnconsumed);
      mTotalUnconsumed = 0.0F;
    }
    stopNestedScroll();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if ((mReturningToStart) && (i == 0)) {
      mReturningToStart = false;
    }
    if ((!isEnabled()) || (mReturningToStart) || (canChildScrollUp()) || (mRefreshing) || (mNestedScrollInProgress)) {
      return false;
    }
    switch (i)
    {
    case 3: 
    case 4: 
    default: 
    case 0: 
    case 2: 
    case 5: 
    case 6: 
      for (;;)
      {
        return true;
        mActivePointerId = paramMotionEvent.getPointerId(0);
        mIsBeingDragged = false;
        continue;
        int m = paramMotionEvent.findPointerIndex(mActivePointerId);
        if (m < 0)
        {
          Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
          return false;
        }
        float f2 = paramMotionEvent.getY(m);
        startDragging(f2);
        if (mIsBeingDragged)
        {
          float f3 = 0.5F * (f2 - mInitialMotionY);
          if (f3 <= 0.0F) {
            break;
          }
          moveSpinner(f3);
          continue;
          int k = MotionEventCompat.getActionIndex(paramMotionEvent);
          if (k < 0)
          {
            Log.e(LOG_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
            return false;
          }
          mActivePointerId = paramMotionEvent.getPointerId(k);
          continue;
          onSecondaryPointerUp(paramMotionEvent);
        }
      }
    }
    int j = paramMotionEvent.findPointerIndex(mActivePointerId);
    if (j < 0)
    {
      Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
      return false;
    }
    if (mIsBeingDragged)
    {
      float f1 = 0.5F * (paramMotionEvent.getY(j) - mInitialMotionY);
      mIsBeingDragged = false;
      finishSpinner(f1);
    }
    mActivePointerId = -1;
    return false;
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    if (((Build.VERSION.SDK_INT < 21) && ((mTarget instanceof AbsListView))) || ((mTarget != null) && (!ViewCompat.isNestedScrollingEnabled(mTarget)))) {
      return;
    }
    super.requestDisallowInterceptTouchEvent(paramBoolean);
  }
  
  void reset()
  {
    mCircleView.clearAnimation();
    mProgress.stop();
    mCircleView.setVisibility(8);
    setColorViewAlpha(255);
    if (mScale) {
      setAnimationProgress(0.0F);
    }
    for (;;)
    {
      mCurrentTargetOffsetTop = mCircleView.getTop();
      return;
      setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop, true);
    }
  }
  
  void setAnimationProgress(float paramFloat)
  {
    if (isAlphaUsedForScale())
    {
      setColorViewAlpha((int)(255.0F * paramFloat));
      return;
    }
    ViewCompat.setScaleX(mCircleView, paramFloat);
    ViewCompat.setScaleY(mCircleView, paramFloat);
  }
  
  @Deprecated
  public void setColorScheme(@ColorInt int... paramVarArgs)
  {
    setColorSchemeResources(paramVarArgs);
  }
  
  public void setColorSchemeColors(@ColorInt int... paramVarArgs)
  {
    ensureTarget();
    mProgress.setColorSchemeColors(paramVarArgs);
  }
  
  public void setColorSchemeResources(@ColorRes int... paramVarArgs)
  {
    Resources localResources = getResources();
    int[] arrayOfInt = new int[paramVarArgs.length];
    for (int i = 0; i < paramVarArgs.length; i++) {
      arrayOfInt[i] = localResources.getColor(paramVarArgs[i]);
    }
    setColorSchemeColors(arrayOfInt);
  }
  
  public void setDistanceToTriggerSync(int paramInt)
  {
    mTotalDragDistance = paramInt;
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    if (!paramBoolean) {
      reset();
    }
  }
  
  public void setNestedScrollingEnabled(boolean paramBoolean)
  {
    mNestedScrollingChildHelper.setNestedScrollingEnabled(paramBoolean);
  }
  
  public void setOnChildScrollUpCallback(@Nullable OnChildScrollUpCallback paramOnChildScrollUpCallback)
  {
    mChildScrollUpCallback = paramOnChildScrollUpCallback;
  }
  
  public void setOnRefreshListener(OnRefreshListener paramOnRefreshListener)
  {
    mListener = paramOnRefreshListener;
  }
  
  @Deprecated
  public void setProgressBackgroundColor(int paramInt)
  {
    setProgressBackgroundColorSchemeResource(paramInt);
  }
  
  public void setProgressBackgroundColorSchemeColor(@ColorInt int paramInt)
  {
    mCircleView.setBackgroundColor(paramInt);
    mProgress.setBackgroundColor(paramInt);
  }
  
  public void setProgressBackgroundColorSchemeResource(@ColorRes int paramInt)
  {
    setProgressBackgroundColorSchemeColor(getResources().getColor(paramInt));
  }
  
  public void setProgressViewEndTarget(boolean paramBoolean, int paramInt)
  {
    mSpinnerFinalOffset = paramInt;
    mScale = paramBoolean;
    mCircleView.invalidate();
  }
  
  public void setProgressViewOffset(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    mScale = paramBoolean;
    mOriginalOffsetTop = paramInt1;
    mSpinnerFinalOffset = paramInt2;
    mUsingCustomStart = true;
    reset();
    mRefreshing = false;
  }
  
  public void setRefreshing(boolean paramBoolean)
  {
    if ((paramBoolean) && (mRefreshing != paramBoolean))
    {
      mRefreshing = paramBoolean;
      if (!mUsingCustomStart) {}
      for (int i = (int)(mSpinnerFinalOffset + mOriginalOffsetTop);; i = (int)mSpinnerFinalOffset)
      {
        setTargetOffsetTopAndBottom(i - mCurrentTargetOffsetTop, true);
        mNotify = false;
        startScaleUpAnimation(mRefreshListener);
        return;
      }
    }
    setRefreshing(paramBoolean, false);
  }
  
  public void setSize(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      return;
    }
    DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
    if (paramInt == 0) {}
    for (mCircleDiameter = ((int)(56.0F * density));; mCircleDiameter = ((int)(40.0F * density)))
    {
      mCircleView.setImageDrawable(null);
      mProgress.updateSizes(paramInt);
      mCircleView.setImageDrawable(mProgress);
      return;
    }
  }
  
  void setTargetOffsetTopAndBottom(int paramInt, boolean paramBoolean)
  {
    mCircleView.bringToFront();
    ViewCompat.offsetTopAndBottom(mCircleView, paramInt);
    mCurrentTargetOffsetTop = mCircleView.getTop();
    if ((paramBoolean) && (Build.VERSION.SDK_INT < 11)) {
      invalidate();
    }
  }
  
  public boolean startNestedScroll(int paramInt)
  {
    return mNestedScrollingChildHelper.startNestedScroll(paramInt);
  }
  
  void startScaleDownAnimation(Animation.AnimationListener paramAnimationListener)
  {
    mScaleDownAnimation = new Animation()
    {
      public void applyTransformation(float paramAnonymousFloat, Transformation paramAnonymousTransformation)
      {
        setAnimationProgress(1.0F - paramAnonymousFloat);
      }
    };
    mScaleDownAnimation.setDuration(150L);
    mCircleView.setAnimationListener(paramAnimationListener);
    mCircleView.clearAnimation();
    mCircleView.startAnimation(mScaleDownAnimation);
  }
  
  public void stopNestedScroll()
  {
    mNestedScrollingChildHelper.stopNestedScroll();
  }
  
  public static abstract interface OnChildScrollUpCallback
  {
    public abstract boolean canChildScrollUp(SwipeRefreshLayout paramSwipeRefreshLayout, @Nullable View paramView);
  }
  
  public static abstract interface OnRefreshListener
  {
    public abstract void onRefresh();
  }
}
