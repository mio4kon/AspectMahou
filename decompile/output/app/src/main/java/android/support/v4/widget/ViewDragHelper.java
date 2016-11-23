package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import java.util.Arrays;

public class ViewDragHelper
{
  private static final int BASE_SETTLE_DURATION = 256;
  public static final int DIRECTION_ALL = 3;
  public static final int DIRECTION_HORIZONTAL = 1;
  public static final int DIRECTION_VERTICAL = 2;
  public static final int EDGE_ALL = 15;
  public static final int EDGE_BOTTOM = 8;
  public static final int EDGE_LEFT = 1;
  public static final int EDGE_RIGHT = 2;
  private static final int EDGE_SIZE = 20;
  public static final int EDGE_TOP = 4;
  public static final int INVALID_POINTER = -1;
  private static final int MAX_SETTLE_DURATION = 600;
  public static final int STATE_DRAGGING = 1;
  public static final int STATE_IDLE = 0;
  public static final int STATE_SETTLING = 2;
  private static final String TAG = "ViewDragHelper";
  private static final Interpolator sInterpolator = new Interpolator()
  {
    public float getInterpolation(float paramAnonymousFloat)
    {
      float f = paramAnonymousFloat - 1.0F;
      return 1.0F + f * (f * (f * (f * f)));
    }
  };
  private int mActivePointerId = -1;
  private final Callback mCallback;
  private View mCapturedView;
  private int mDragState;
  private int[] mEdgeDragsInProgress;
  private int[] mEdgeDragsLocked;
  private int mEdgeSize;
  private int[] mInitialEdgesTouched;
  private float[] mInitialMotionX;
  private float[] mInitialMotionY;
  private float[] mLastMotionX;
  private float[] mLastMotionY;
  private float mMaxVelocity;
  private float mMinVelocity;
  private final ViewGroup mParentView;
  private int mPointersDown;
  private boolean mReleaseInProgress;
  private ScrollerCompat mScroller;
  private final Runnable mSetIdleRunnable = new Runnable()
  {
    public void run()
    {
      setDragState(0);
    }
  };
  private int mTouchSlop;
  private int mTrackingEdges;
  private VelocityTracker mVelocityTracker;
  
  private ViewDragHelper(Context paramContext, ViewGroup paramViewGroup, Callback paramCallback)
  {
    if (paramViewGroup == null) {
      throw new IllegalArgumentException("Parent view may not be null");
    }
    if (paramCallback == null) {
      throw new IllegalArgumentException("Callback may not be null");
    }
    mParentView = paramViewGroup;
    mCallback = paramCallback;
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(paramContext);
    mEdgeSize = ((int)(0.5F + 20.0F * getResourcesgetDisplayMetricsdensity));
    mTouchSlop = localViewConfiguration.getScaledTouchSlop();
    mMaxVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
    mMinVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
    mScroller = ScrollerCompat.create(paramContext, sInterpolator);
  }
  
  private boolean checkNewEdgeDrag(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2)
  {
    float f1 = Math.abs(paramFloat1);
    float f2 = Math.abs(paramFloat2);
    if (((paramInt2 & mInitialEdgesTouched[paramInt1]) != paramInt2) || ((paramInt2 & mTrackingEdges) == 0) || ((paramInt2 & mEdgeDragsLocked[paramInt1]) == paramInt2) || ((paramInt2 & mEdgeDragsInProgress[paramInt1]) == paramInt2) || ((f1 <= mTouchSlop) && (f2 <= mTouchSlop))) {}
    do
    {
      return false;
      if ((f1 < 0.5F * f2) && (mCallback.onEdgeLock(paramInt2)))
      {
        int[] arrayOfInt = mEdgeDragsLocked;
        arrayOfInt[paramInt1] = (paramInt2 | arrayOfInt[paramInt1]);
        return false;
      }
    } while (((paramInt2 & mEdgeDragsInProgress[paramInt1]) != 0) || (f1 <= mTouchSlop));
    return true;
  }
  
  private boolean checkTouchSlop(View paramView, float paramFloat1, float paramFloat2)
  {
    boolean bool1 = true;
    if (paramView == null) {
      bool1 = false;
    }
    label28:
    label82:
    label88:
    do
    {
      boolean bool3;
      do
      {
        return bool1;
        boolean bool2;
        if (mCallback.getViewHorizontalDragRange(paramView) > 0)
        {
          bool2 = bool1;
          if (mCallback.getViewVerticalDragRange(paramView) <= 0) {
            break label82;
          }
        }
        for (bool3 = bool1;; bool3 = false)
        {
          if ((!bool2) || (!bool3)) {
            break label88;
          }
          if (paramFloat1 * paramFloat1 + paramFloat2 * paramFloat2 > mTouchSlop * mTouchSlop) {
            break;
          }
          return false;
          bool2 = false;
          break label28;
        }
        if (!bool2) {
          break;
        }
      } while (Math.abs(paramFloat1) > mTouchSlop);
      return false;
      if (!bool3) {
        break;
      }
    } while (Math.abs(paramFloat2) > mTouchSlop);
    return false;
    return false;
  }
  
  private float clampMag(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    float f = Math.abs(paramFloat1);
    if (f < paramFloat2) {
      paramFloat3 = 0.0F;
    }
    do
    {
      return paramFloat3;
      if (f <= paramFloat3) {
        break;
      }
    } while (paramFloat1 > 0.0F);
    return -paramFloat3;
    return paramFloat1;
  }
  
  private int clampMag(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = Math.abs(paramInt1);
    if (i < paramInt2) {
      paramInt3 = 0;
    }
    do
    {
      return paramInt3;
      if (i <= paramInt3) {
        break;
      }
    } while (paramInt1 > 0);
    return -paramInt3;
    return paramInt1;
  }
  
  private void clearMotionHistory()
  {
    if (mInitialMotionX == null) {
      return;
    }
    Arrays.fill(mInitialMotionX, 0.0F);
    Arrays.fill(mInitialMotionY, 0.0F);
    Arrays.fill(mLastMotionX, 0.0F);
    Arrays.fill(mLastMotionY, 0.0F);
    Arrays.fill(mInitialEdgesTouched, 0);
    Arrays.fill(mEdgeDragsInProgress, 0);
    Arrays.fill(mEdgeDragsLocked, 0);
    mPointersDown = 0;
  }
  
  private void clearMotionHistory(int paramInt)
  {
    if ((mInitialMotionX == null) || (!isPointerDown(paramInt))) {
      return;
    }
    mInitialMotionX[paramInt] = 0.0F;
    mInitialMotionY[paramInt] = 0.0F;
    mLastMotionX[paramInt] = 0.0F;
    mLastMotionY[paramInt] = 0.0F;
    mInitialEdgesTouched[paramInt] = 0;
    mEdgeDragsInProgress[paramInt] = 0;
    mEdgeDragsLocked[paramInt] = 0;
    mPointersDown &= (0xFFFFFFFF ^ 1 << paramInt);
  }
  
  private int computeAxisDuration(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 == 0) {
      return 0;
    }
    int i = mParentView.getWidth();
    int j = i / 2;
    float f1 = Math.min(1.0F, Math.abs(paramInt1) / i);
    float f2 = j + j * distanceInfluenceForSnapDuration(f1);
    int k = Math.abs(paramInt2);
    if (k > 0) {}
    for (int m = 4 * Math.round(1000.0F * Math.abs(f2 / k));; m = (int)(256.0F * (1.0F + Math.abs(paramInt1) / paramInt3))) {
      return Math.min(m, 600);
    }
  }
  
  private int computeSettleDuration(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = clampMag(paramInt3, (int)mMinVelocity, (int)mMaxVelocity);
    int j = clampMag(paramInt4, (int)mMinVelocity, (int)mMaxVelocity);
    int k = Math.abs(paramInt1);
    int m = Math.abs(paramInt2);
    int n = Math.abs(i);
    int i1 = Math.abs(j);
    int i2 = n + i1;
    int i3 = k + m;
    float f1;
    if (i != 0)
    {
      f1 = n / i2;
      if (j == 0) {
        break label165;
      }
    }
    label165:
    for (float f2 = i1 / i2;; f2 = m / i3)
    {
      int i4 = computeAxisDuration(paramInt1, i, mCallback.getViewHorizontalDragRange(paramView));
      int i5 = computeAxisDuration(paramInt2, j, mCallback.getViewVerticalDragRange(paramView));
      return (int)(f1 * i4 + f2 * i5);
      f1 = k / i3;
      break;
    }
  }
  
  public static ViewDragHelper create(ViewGroup paramViewGroup, float paramFloat, Callback paramCallback)
  {
    ViewDragHelper localViewDragHelper = create(paramViewGroup, paramCallback);
    mTouchSlop = ((int)(mTouchSlop * (1.0F / paramFloat)));
    return localViewDragHelper;
  }
  
  public static ViewDragHelper create(ViewGroup paramViewGroup, Callback paramCallback)
  {
    return new ViewDragHelper(paramViewGroup.getContext(), paramViewGroup, paramCallback);
  }
  
  private void dispatchViewReleased(float paramFloat1, float paramFloat2)
  {
    mReleaseInProgress = true;
    mCallback.onViewReleased(mCapturedView, paramFloat1, paramFloat2);
    mReleaseInProgress = false;
    if (mDragState == 1) {
      setDragState(0);
    }
  }
  
  private float distanceInfluenceForSnapDuration(float paramFloat)
  {
    return (float)Math.sin((float)(0.4712389167638204D * (paramFloat - 0.5F)));
  }
  
  private void dragTo(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = paramInt1;
    int j = paramInt2;
    int k = mCapturedView.getLeft();
    int m = mCapturedView.getTop();
    if (paramInt3 != 0)
    {
      i = mCallback.clampViewPositionHorizontal(mCapturedView, paramInt1, paramInt3);
      ViewCompat.offsetLeftAndRight(mCapturedView, i - k);
    }
    if (paramInt4 != 0)
    {
      j = mCallback.clampViewPositionVertical(mCapturedView, paramInt2, paramInt4);
      ViewCompat.offsetTopAndBottom(mCapturedView, j - m);
    }
    if ((paramInt3 != 0) || (paramInt4 != 0))
    {
      int n = i - k;
      int i1 = j - m;
      mCallback.onViewPositionChanged(mCapturedView, i, j, n, i1);
    }
  }
  
  private void ensureMotionHistorySizeForId(int paramInt)
  {
    if ((mInitialMotionX == null) || (mInitialMotionX.length <= paramInt))
    {
      float[] arrayOfFloat1 = new float[paramInt + 1];
      float[] arrayOfFloat2 = new float[paramInt + 1];
      float[] arrayOfFloat3 = new float[paramInt + 1];
      float[] arrayOfFloat4 = new float[paramInt + 1];
      int[] arrayOfInt1 = new int[paramInt + 1];
      int[] arrayOfInt2 = new int[paramInt + 1];
      int[] arrayOfInt3 = new int[paramInt + 1];
      if (mInitialMotionX != null)
      {
        System.arraycopy(mInitialMotionX, 0, arrayOfFloat1, 0, mInitialMotionX.length);
        System.arraycopy(mInitialMotionY, 0, arrayOfFloat2, 0, mInitialMotionY.length);
        System.arraycopy(mLastMotionX, 0, arrayOfFloat3, 0, mLastMotionX.length);
        System.arraycopy(mLastMotionY, 0, arrayOfFloat4, 0, mLastMotionY.length);
        System.arraycopy(mInitialEdgesTouched, 0, arrayOfInt1, 0, mInitialEdgesTouched.length);
        System.arraycopy(mEdgeDragsInProgress, 0, arrayOfInt2, 0, mEdgeDragsInProgress.length);
        System.arraycopy(mEdgeDragsLocked, 0, arrayOfInt3, 0, mEdgeDragsLocked.length);
      }
      mInitialMotionX = arrayOfFloat1;
      mInitialMotionY = arrayOfFloat2;
      mLastMotionX = arrayOfFloat3;
      mLastMotionY = arrayOfFloat4;
      mInitialEdgesTouched = arrayOfInt1;
      mEdgeDragsInProgress = arrayOfInt2;
      mEdgeDragsLocked = arrayOfInt3;
    }
  }
  
  private boolean forceSettleCapturedViewAt(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = mCapturedView.getLeft();
    int j = mCapturedView.getTop();
    int k = paramInt1 - i;
    int m = paramInt2 - j;
    if ((k == 0) && (m == 0))
    {
      mScroller.abortAnimation();
      setDragState(0);
      return false;
    }
    int n = computeSettleDuration(mCapturedView, k, m, paramInt3, paramInt4);
    mScroller.startScroll(i, j, k, m, n);
    setDragState(2);
    return true;
  }
  
  private int getEdgesTouched(int paramInt1, int paramInt2)
  {
    int i = mParentView.getLeft() + mEdgeSize;
    int j = 0;
    if (paramInt1 < i) {
      j = 0x0 | 0x1;
    }
    if (paramInt2 < mParentView.getTop() + mEdgeSize) {
      j |= 0x4;
    }
    if (paramInt1 > mParentView.getRight() - mEdgeSize) {
      j |= 0x2;
    }
    if (paramInt2 > mParentView.getBottom() - mEdgeSize) {
      j |= 0x8;
    }
    return j;
  }
  
  private boolean isValidPointerForActionMove(int paramInt)
  {
    if (!isPointerDown(paramInt))
    {
      Log.e("ViewDragHelper", "Ignoring pointerId=" + paramInt + " because ACTION_DOWN was not received " + "for this pointer before ACTION_MOVE. It likely happened because " + " ViewDragHelper did not receive all the events in the event stream.");
      return false;
    }
    return true;
  }
  
  private void releaseViewForPointerUp()
  {
    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
    dispatchViewReleased(clampMag(VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId), mMinVelocity, mMaxVelocity), clampMag(VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId), mMinVelocity, mMaxVelocity));
  }
  
  private void reportNewEdgeDrags(float paramFloat1, float paramFloat2, int paramInt)
  {
    boolean bool = checkNewEdgeDrag(paramFloat1, paramFloat2, paramInt, 1);
    int i = 0;
    if (bool) {
      i = 0x0 | 0x1;
    }
    if (checkNewEdgeDrag(paramFloat2, paramFloat1, paramInt, 4)) {
      i |= 0x4;
    }
    if (checkNewEdgeDrag(paramFloat1, paramFloat2, paramInt, 2)) {
      i |= 0x2;
    }
    if (checkNewEdgeDrag(paramFloat2, paramFloat1, paramInt, 8)) {
      i |= 0x8;
    }
    if (i != 0)
    {
      int[] arrayOfInt = mEdgeDragsInProgress;
      arrayOfInt[paramInt] = (i | arrayOfInt[paramInt]);
      mCallback.onEdgeDragStarted(i, paramInt);
    }
  }
  
  private void saveInitialMotion(float paramFloat1, float paramFloat2, int paramInt)
  {
    ensureMotionHistorySizeForId(paramInt);
    float[] arrayOfFloat1 = mInitialMotionX;
    mLastMotionX[paramInt] = paramFloat1;
    arrayOfFloat1[paramInt] = paramFloat1;
    float[] arrayOfFloat2 = mInitialMotionY;
    mLastMotionY[paramInt] = paramFloat2;
    arrayOfFloat2[paramInt] = paramFloat2;
    mInitialEdgesTouched[paramInt] = getEdgesTouched((int)paramFloat1, (int)paramFloat2);
    mPointersDown |= 1 << paramInt;
  }
  
  private void saveLastMotion(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getPointerCount();
    int j = 0;
    if (j < i)
    {
      int k = paramMotionEvent.getPointerId(j);
      if (!isValidPointerForActionMove(k)) {}
      for (;;)
      {
        j++;
        break;
        float f1 = paramMotionEvent.getX(j);
        float f2 = paramMotionEvent.getY(j);
        mLastMotionX[k] = f1;
        mLastMotionY[k] = f2;
      }
    }
  }
  
  public void abort()
  {
    cancel();
    if (mDragState == 2)
    {
      int i = mScroller.getCurrX();
      int j = mScroller.getCurrY();
      mScroller.abortAnimation();
      int k = mScroller.getCurrX();
      int m = mScroller.getCurrY();
      mCallback.onViewPositionChanged(mCapturedView, k, m, k - i, m - j);
    }
    setDragState(0);
  }
  
  protected boolean canScroll(View paramView, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramView instanceof ViewGroup))
    {
      ViewGroup localViewGroup = (ViewGroup)paramView;
      int i = paramView.getScrollX();
      int j = paramView.getScrollY();
      for (int k = -1 + localViewGroup.getChildCount(); k >= 0; k--)
      {
        View localView = localViewGroup.getChildAt(k);
        if ((paramInt3 + i >= localView.getLeft()) && (paramInt3 + i < localView.getRight()) && (paramInt4 + j >= localView.getTop()) && (paramInt4 + j < localView.getBottom()) && (canScroll(localView, true, paramInt1, paramInt2, paramInt3 + i - localView.getLeft(), paramInt4 + j - localView.getTop()))) {
          return true;
        }
      }
    }
    return (paramBoolean) && ((ViewCompat.canScrollHorizontally(paramView, -paramInt1)) || (ViewCompat.canScrollVertically(paramView, -paramInt2)));
  }
  
  public void cancel()
  {
    mActivePointerId = -1;
    clearMotionHistory();
    if (mVelocityTracker != null)
    {
      mVelocityTracker.recycle();
      mVelocityTracker = null;
    }
  }
  
  public void captureChildView(View paramView, int paramInt)
  {
    if (paramView.getParent() != mParentView) {
      throw new IllegalArgumentException("captureChildView: parameter must be a descendant of the ViewDragHelper's tracked parent view (" + mParentView + ")");
    }
    mCapturedView = paramView;
    mActivePointerId = paramInt;
    mCallback.onViewCaptured(paramView, paramInt);
    setDragState(1);
  }
  
  public boolean checkTouchSlop(int paramInt)
  {
    int i = mInitialMotionX.length;
    for (int j = 0; j < i; j++) {
      if (checkTouchSlop(paramInt, j)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean checkTouchSlop(int paramInt1, int paramInt2)
  {
    int i = 1;
    if (!isPointerDown(paramInt2)) {
      i = 0;
    }
    label24:
    float f2;
    label107:
    label113:
    do
    {
      float f1;
      int n;
      do
      {
        return i;
        int j;
        if ((paramInt1 & 0x1) == i)
        {
          j = i;
          if ((paramInt1 & 0x2) != 2) {
            break label107;
          }
        }
        int k;
        for (int m = i;; n = 0)
        {
          f1 = mLastMotionX[paramInt2] - mInitialMotionX[paramInt2];
          f2 = mLastMotionY[paramInt2] - mInitialMotionY[paramInt2];
          if ((j == 0) || (m == 0)) {
            break label113;
          }
          if (f1 * f1 + f2 * f2 > mTouchSlop * mTouchSlop) {
            break;
          }
          return false;
          k = 0;
          break label24;
        }
        if (k == 0) {
          break;
        }
      } while (Math.abs(f1) > mTouchSlop);
      return false;
      if (n == 0) {
        break;
      }
    } while (Math.abs(f2) > mTouchSlop);
    return false;
    return false;
  }
  
  public boolean continueSettling(boolean paramBoolean)
  {
    if (mDragState == 2)
    {
      boolean bool = mScroller.computeScrollOffset();
      int i = mScroller.getCurrX();
      int j = mScroller.getCurrY();
      int k = i - mCapturedView.getLeft();
      int m = j - mCapturedView.getTop();
      if (k != 0) {
        ViewCompat.offsetLeftAndRight(mCapturedView, k);
      }
      if (m != 0) {
        ViewCompat.offsetTopAndBottom(mCapturedView, m);
      }
      if ((k != 0) || (m != 0)) {
        mCallback.onViewPositionChanged(mCapturedView, i, j, k, m);
      }
      if ((bool) && (i == mScroller.getFinalX()) && (j == mScroller.getFinalY()))
      {
        mScroller.abortAnimation();
        bool = false;
      }
      if (!bool)
      {
        if (!paramBoolean) {
          break label178;
        }
        mParentView.post(mSetIdleRunnable);
      }
    }
    while (mDragState == 2)
    {
      return true;
      label178:
      setDragState(0);
    }
    return false;
  }
  
  public View findTopChildUnder(int paramInt1, int paramInt2)
  {
    for (int i = -1 + mParentView.getChildCount(); i >= 0; i--)
    {
      View localView = mParentView.getChildAt(mCallback.getOrderedChildIndex(i));
      if ((paramInt1 >= localView.getLeft()) && (paramInt1 < localView.getRight()) && (paramInt2 >= localView.getTop()) && (paramInt2 < localView.getBottom())) {
        return localView;
      }
    }
    return null;
  }
  
  public void flingCapturedView(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!mReleaseInProgress) {
      throw new IllegalStateException("Cannot flingCapturedView outside of a call to Callback#onViewReleased");
    }
    mScroller.fling(mCapturedView.getLeft(), mCapturedView.getTop(), (int)VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId), (int)VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId), paramInt1, paramInt3, paramInt2, paramInt4);
    setDragState(2);
  }
  
  public int getActivePointerId()
  {
    return mActivePointerId;
  }
  
  public View getCapturedView()
  {
    return mCapturedView;
  }
  
  public int getEdgeSize()
  {
    return mEdgeSize;
  }
  
  public float getMinVelocity()
  {
    return mMinVelocity;
  }
  
  public int getTouchSlop()
  {
    return mTouchSlop;
  }
  
  public int getViewDragState()
  {
    return mDragState;
  }
  
  public boolean isCapturedViewUnder(int paramInt1, int paramInt2)
  {
    return isViewUnder(mCapturedView, paramInt1, paramInt2);
  }
  
  public boolean isEdgeTouched(int paramInt)
  {
    int i = mInitialEdgesTouched.length;
    for (int j = 0; j < i; j++) {
      if (isEdgeTouched(paramInt, j)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isEdgeTouched(int paramInt1, int paramInt2)
  {
    return (isPointerDown(paramInt2)) && ((paramInt1 & mInitialEdgesTouched[paramInt2]) != 0);
  }
  
  public boolean isPointerDown(int paramInt)
  {
    return (mPointersDown & 1 << paramInt) != 0;
  }
  
  public boolean isViewUnder(View paramView, int paramInt1, int paramInt2)
  {
    if (paramView == null) {}
    while ((paramInt1 < paramView.getLeft()) || (paramInt1 >= paramView.getRight()) || (paramInt2 < paramView.getTop()) || (paramInt2 >= paramView.getBottom())) {
      return false;
    }
    return true;
  }
  
  public void processTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    int j = MotionEventCompat.getActionIndex(paramMotionEvent);
    if (i == 0) {
      cancel();
    }
    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }
    mVelocityTracker.addMovement(paramMotionEvent);
    switch (i)
    {
    case 4: 
    default: 
    case 0: 
    case 5: 
    case 2: 
      do
      {
        int i9;
        float f9;
        float f10;
        do
        {
          int i10;
          do
          {
            int i11;
            int i12;
            do
            {
              return;
              float f11 = paramMotionEvent.getX();
              float f12 = paramMotionEvent.getY();
              i11 = paramMotionEvent.getPointerId(0);
              View localView2 = findTopChildUnder((int)f11, (int)f12);
              saveInitialMotion(f11, f12, i11);
              tryCaptureViewForDrag(localView2, i11);
              i12 = mInitialEdgesTouched[i11];
            } while ((i12 & mTrackingEdges) == 0);
            mCallback.onEdgeTouched(i12 & mTrackingEdges, i11);
            return;
            i9 = paramMotionEvent.getPointerId(j);
            f9 = paramMotionEvent.getX(j);
            f10 = paramMotionEvent.getY(j);
            saveInitialMotion(f9, f10, i9);
            if (mDragState != 0) {
              break;
            }
            tryCaptureViewForDrag(findTopChildUnder((int)f9, (int)f10), i9);
            i10 = mInitialEdgesTouched[i9];
          } while ((i10 & mTrackingEdges) == 0);
          mCallback.onEdgeTouched(i10 & mTrackingEdges, i9);
          return;
        } while (!isCapturedViewUnder((int)f9, (int)f10));
        tryCaptureViewForDrag(mCapturedView, i9);
        return;
        if (mDragState != 1) {
          break;
        }
      } while (!isValidPointerForActionMove(mActivePointerId));
      int i6 = paramMotionEvent.findPointerIndex(mActivePointerId);
      float f7 = paramMotionEvent.getX(i6);
      float f8 = paramMotionEvent.getY(i6);
      int i7 = (int)(f7 - mLastMotionX[mActivePointerId]);
      int i8 = (int)(f8 - mLastMotionY[mActivePointerId]);
      dragTo(i7 + mCapturedView.getLeft(), i8 + mCapturedView.getTop(), i7, i8);
      saveLastMotion(paramMotionEvent);
      return;
      int i3 = paramMotionEvent.getPointerCount();
      int i4 = 0;
      int i5;
      float f3;
      float f4;
      float f5;
      float f6;
      while (i4 < i3)
      {
        i5 = paramMotionEvent.getPointerId(i4);
        if (!isValidPointerForActionMove(i5))
        {
          i4++;
        }
        else
        {
          f3 = paramMotionEvent.getX(i4);
          f4 = paramMotionEvent.getY(i4);
          f5 = f3 - mInitialMotionX[i5];
          f6 = f4 - mInitialMotionY[i5];
          reportNewEdgeDrags(f5, f6, i5);
          if (mDragState != 1) {
            break label499;
          }
        }
      }
      for (;;)
      {
        saveLastMotion(paramMotionEvent);
        return;
        View localView1 = findTopChildUnder((int)f3, (int)f4);
        if ((!checkTouchSlop(localView1, f5, f6)) || (!tryCaptureViewForDrag(localView1, i5))) {
          break;
        }
      }
    case 6: 
      int k = paramMotionEvent.getPointerId(j);
      if ((mDragState == 1) && (k == mActivePointerId))
      {
        int m = -1;
        int n = paramMotionEvent.getPointerCount();
        int i1 = 0;
        if (i1 < n)
        {
          int i2 = paramMotionEvent.getPointerId(i1);
          if (i2 == mActivePointerId) {}
          float f1;
          float f2;
          do
          {
            i1++;
            break;
            f1 = paramMotionEvent.getX(i1);
            f2 = paramMotionEvent.getY(i1);
          } while ((findTopChildUnder((int)f1, (int)f2) != mCapturedView) || (!tryCaptureViewForDrag(mCapturedView, i2)));
          m = mActivePointerId;
        }
        if (m == -1) {
          releaseViewForPointerUp();
        }
      }
      clearMotionHistory(k);
      return;
    case 1: 
      label499:
      if (mDragState == 1) {
        releaseViewForPointerUp();
      }
      cancel();
      return;
    }
    if (mDragState == 1) {
      dispatchViewReleased(0.0F, 0.0F);
    }
    cancel();
  }
  
  void setDragState(int paramInt)
  {
    mParentView.removeCallbacks(mSetIdleRunnable);
    if (mDragState != paramInt)
    {
      mDragState = paramInt;
      mCallback.onViewDragStateChanged(paramInt);
      if (mDragState == 0) {
        mCapturedView = null;
      }
    }
  }
  
  public void setEdgeTrackingEnabled(int paramInt)
  {
    mTrackingEdges = paramInt;
  }
  
  public void setMinVelocity(float paramFloat)
  {
    mMinVelocity = paramFloat;
  }
  
  public boolean settleCapturedViewAt(int paramInt1, int paramInt2)
  {
    if (!mReleaseInProgress) {
      throw new IllegalStateException("Cannot settleCapturedViewAt outside of a call to Callback#onViewReleased");
    }
    return forceSettleCapturedViewAt(paramInt1, paramInt2, (int)VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId), (int)VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId));
  }
  
  public boolean shouldInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    int j = MotionEventCompat.getActionIndex(paramMotionEvent);
    if (i == 0) {
      cancel();
    }
    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }
    mVelocityTracker.addMovement(paramMotionEvent);
    switch (i)
    {
    }
    while (mDragState == 1)
    {
      return true;
      float f7 = paramMotionEvent.getX();
      float f8 = paramMotionEvent.getY();
      int i12 = paramMotionEvent.getPointerId(0);
      saveInitialMotion(f7, f8, i12);
      View localView3 = findTopChildUnder((int)f7, (int)f8);
      if ((localView3 == mCapturedView) && (mDragState == 2)) {
        tryCaptureViewForDrag(localView3, i12);
      }
      int i13 = mInitialEdgesTouched[i12];
      if ((i13 & mTrackingEdges) != 0)
      {
        mCallback.onEdgeTouched(i13 & mTrackingEdges, i12);
        continue;
        int i10 = paramMotionEvent.getPointerId(j);
        float f5 = paramMotionEvent.getX(j);
        float f6 = paramMotionEvent.getY(j);
        saveInitialMotion(f5, f6, i10);
        if (mDragState == 0)
        {
          int i11 = mInitialEdgesTouched[i10];
          if ((i11 & mTrackingEdges) != 0) {
            mCallback.onEdgeTouched(i11 & mTrackingEdges, i10);
          }
        }
        else if (mDragState == 2)
        {
          View localView2 = findTopChildUnder((int)f5, (int)f6);
          if (localView2 == mCapturedView)
          {
            tryCaptureViewForDrag(localView2, i10);
            continue;
            if ((mInitialMotionX != null) && (mInitialMotionY != null))
            {
              int k = paramMotionEvent.getPointerCount();
              int m = 0;
              int n;
              label363:
              float f3;
              float f4;
              View localView1;
              int i1;
              while (m < k)
              {
                n = paramMotionEvent.getPointerId(m);
                if (!isValidPointerForActionMove(n))
                {
                  m++;
                }
                else
                {
                  float f1 = paramMotionEvent.getX(m);
                  float f2 = paramMotionEvent.getY(m);
                  f3 = f1 - mInitialMotionX[n];
                  f4 = f2 - mInitialMotionY[n];
                  localView1 = findTopChildUnder((int)f1, (int)f2);
                  if ((localView1 == null) || (!checkTouchSlop(localView1, f3, f4))) {
                    break label573;
                  }
                  i1 = 1;
                  label442:
                  if (i1 == 0) {
                    break label579;
                  }
                  int i2 = localView1.getLeft();
                  int i3 = i2 + (int)f3;
                  int i4 = mCallback.clampViewPositionHorizontal(localView1, i3, (int)f3);
                  int i5 = localView1.getTop();
                  int i6 = i5 + (int)f4;
                  int i7 = mCallback.clampViewPositionVertical(localView1, i6, (int)f4);
                  int i8 = mCallback.getViewHorizontalDragRange(localView1);
                  int i9 = mCallback.getViewVerticalDragRange(localView1);
                  if (((i8 != 0) && ((i8 <= 0) || (i4 != i2))) || ((i9 != 0) && ((i9 <= 0) || (i7 != i5)))) {
                    break label579;
                  }
                }
              }
              for (;;)
              {
                saveLastMotion(paramMotionEvent);
                break;
                label573:
                i1 = 0;
                break label442;
                label579:
                reportNewEdgeDrags(f3, f4, n);
                if (mDragState != 1) {
                  if ((i1 == 0) || (!tryCaptureViewForDrag(localView1, n))) {
                    break label363;
                  }
                }
              }
              clearMotionHistory(paramMotionEvent.getPointerId(j));
              continue;
              cancel();
            }
          }
        }
      }
    }
    return false;
  }
  
  public boolean smoothSlideViewTo(View paramView, int paramInt1, int paramInt2)
  {
    mCapturedView = paramView;
    mActivePointerId = -1;
    boolean bool = forceSettleCapturedViewAt(paramInt1, paramInt2, 0, 0);
    if ((!bool) && (mDragState == 0) && (mCapturedView != null)) {
      mCapturedView = null;
    }
    return bool;
  }
  
  boolean tryCaptureViewForDrag(View paramView, int paramInt)
  {
    if ((paramView == mCapturedView) && (mActivePointerId == paramInt)) {
      return true;
    }
    if ((paramView != null) && (mCallback.tryCaptureView(paramView, paramInt)))
    {
      mActivePointerId = paramInt;
      captureChildView(paramView, paramInt);
      return true;
    }
    return false;
  }
  
  public static abstract class Callback
  {
    public Callback() {}
    
    public int clampViewPositionHorizontal(View paramView, int paramInt1, int paramInt2)
    {
      return 0;
    }
    
    public int clampViewPositionVertical(View paramView, int paramInt1, int paramInt2)
    {
      return 0;
    }
    
    public int getOrderedChildIndex(int paramInt)
    {
      return paramInt;
    }
    
    public int getViewHorizontalDragRange(View paramView)
    {
      return 0;
    }
    
    public int getViewVerticalDragRange(View paramView)
    {
      return 0;
    }
    
    public void onEdgeDragStarted(int paramInt1, int paramInt2) {}
    
    public boolean onEdgeLock(int paramInt)
    {
      return false;
    }
    
    public void onEdgeTouched(int paramInt1, int paramInt2) {}
    
    public void onViewCaptured(View paramView, int paramInt) {}
    
    public void onViewDragStateChanged(int paramInt) {}
    
    public void onViewPositionChanged(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
    
    public void onViewReleased(View paramView, float paramFloat1, float paramFloat2) {}
    
    public abstract boolean tryCaptureView(View paramView, int paramInt);
  }
}
