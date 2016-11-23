package android.support.v4.widget;

import android.content.res.Resources;
import android.os.SystemClock;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public abstract class AutoScrollHelper
  implements View.OnTouchListener
{
  private static final int DEFAULT_ACTIVATION_DELAY = ;
  private static final int DEFAULT_EDGE_TYPE = 1;
  private static final float DEFAULT_MAXIMUM_EDGE = Float.MAX_VALUE;
  private static final int DEFAULT_MAXIMUM_VELOCITY_DIPS = 1575;
  private static final int DEFAULT_MINIMUM_VELOCITY_DIPS = 315;
  private static final int DEFAULT_RAMP_DOWN_DURATION = 500;
  private static final int DEFAULT_RAMP_UP_DURATION = 500;
  private static final float DEFAULT_RELATIVE_EDGE = 0.2F;
  private static final float DEFAULT_RELATIVE_VELOCITY = 1.0F;
  public static final int EDGE_TYPE_INSIDE = 0;
  public static final int EDGE_TYPE_INSIDE_EXTEND = 1;
  public static final int EDGE_TYPE_OUTSIDE = 2;
  private static final int HORIZONTAL = 0;
  public static final float NO_MAX = Float.MAX_VALUE;
  public static final float NO_MIN = 0.0F;
  public static final float RELATIVE_UNSPECIFIED = 0.0F;
  private static final int VERTICAL = 1;
  private int mActivationDelay;
  private boolean mAlreadyDelayed;
  boolean mAnimating;
  private final Interpolator mEdgeInterpolator = new AccelerateInterpolator();
  private int mEdgeType;
  private boolean mEnabled;
  private boolean mExclusive;
  private float[] mMaximumEdges = { Float.MAX_VALUE, Float.MAX_VALUE };
  private float[] mMaximumVelocity = { Float.MAX_VALUE, Float.MAX_VALUE };
  private float[] mMinimumVelocity = { 0.0F, 0.0F };
  boolean mNeedsCancel;
  boolean mNeedsReset;
  private float[] mRelativeEdges = { 0.0F, 0.0F };
  private float[] mRelativeVelocity = { 0.0F, 0.0F };
  private Runnable mRunnable;
  final ClampedScroller mScroller = new ClampedScroller();
  final View mTarget;
  
  public AutoScrollHelper(View paramView)
  {
    mTarget = paramView;
    DisplayMetrics localDisplayMetrics = Resources.getSystem().getDisplayMetrics();
    int i = (int)(0.5F + 1575.0F * density);
    int j = (int)(0.5F + 315.0F * density);
    setMaximumVelocity(i, i);
    setMinimumVelocity(j, j);
    setEdgeType(1);
    setMaximumEdges(Float.MAX_VALUE, Float.MAX_VALUE);
    setRelativeEdges(0.2F, 0.2F);
    setRelativeVelocity(1.0F, 1.0F);
    setActivationDelay(DEFAULT_ACTIVATION_DELAY);
    setRampUpDuration(500);
    setRampDownDuration(500);
  }
  
  private float computeTargetVelocity(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    float f1 = getEdgeValue(mRelativeEdges[paramInt], paramFloat2, mMaximumEdges[paramInt], paramFloat1);
    if (f1 == 0.0F) {
      return 0.0F;
    }
    float f2 = mRelativeVelocity[paramInt];
    float f3 = mMinimumVelocity[paramInt];
    float f4 = mMaximumVelocity[paramInt];
    float f5 = f2 * paramFloat3;
    if (f1 > 0.0F) {
      return constrain(f1 * f5, f3, f4);
    }
    return -constrain(f5 * -f1, f3, f4);
  }
  
  static float constrain(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat1 > paramFloat3) {
      return paramFloat3;
    }
    if (paramFloat1 < paramFloat2) {
      return paramFloat2;
    }
    return paramFloat1;
  }
  
  static int constrain(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 > paramInt3) {
      return paramInt3;
    }
    if (paramInt1 < paramInt2) {
      return paramInt2;
    }
    return paramInt1;
  }
  
  private float constrainEdgeValue(float paramFloat1, float paramFloat2)
  {
    if (paramFloat2 == 0.0F) {}
    do
    {
      do
      {
        do
        {
          return 0.0F;
          switch (mEdgeType)
          {
          default: 
            return 0.0F;
          }
        } while (paramFloat1 >= paramFloat2);
        if (paramFloat1 >= 0.0F) {
          return 1.0F - paramFloat1 / paramFloat2;
        }
      } while ((!mAnimating) || (mEdgeType != 1));
      return 1.0F;
    } while (paramFloat1 >= 0.0F);
    return paramFloat1 / -paramFloat2;
  }
  
  private float getEdgeValue(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    float f1 = constrain(paramFloat1 * paramFloat2, 0.0F, paramFloat3);
    float f2 = constrainEdgeValue(paramFloat4, f1);
    float f3 = constrainEdgeValue(paramFloat2 - paramFloat4, f1) - f2;
    if (f3 < 0.0F) {}
    for (float f5 = -mEdgeInterpolator.getInterpolation(-f3);; f5 = mEdgeInterpolator.getInterpolation(f3))
    {
      float f4 = constrain(f5, -1.0F, 1.0F);
      boolean bool;
      do
      {
        return f4;
        bool = f3 < 0.0F;
        f4 = 0.0F;
      } while (!bool);
    }
  }
  
  private void requestStop()
  {
    if (mNeedsReset)
    {
      mAnimating = false;
      return;
    }
    mScroller.requestStop();
  }
  
  private void startAnimating()
  {
    if (mRunnable == null) {
      mRunnable = new ScrollAnimationRunnable();
    }
    mAnimating = true;
    mNeedsReset = true;
    if ((!mAlreadyDelayed) && (mActivationDelay > 0)) {
      ViewCompat.postOnAnimationDelayed(mTarget, mRunnable, mActivationDelay);
    }
    for (;;)
    {
      mAlreadyDelayed = true;
      return;
      mRunnable.run();
    }
  }
  
  public abstract boolean canTargetScrollHorizontally(int paramInt);
  
  public abstract boolean canTargetScrollVertically(int paramInt);
  
  void cancelTargetTouch()
  {
    long l = SystemClock.uptimeMillis();
    MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
    mTarget.onTouchEvent(localMotionEvent);
    localMotionEvent.recycle();
  }
  
  public boolean isEnabled()
  {
    return mEnabled;
  }
  
  public boolean isExclusive()
  {
    return mExclusive;
  }
  
  public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
  {
    int i = 1;
    if (!mEnabled) {
      return false;
    }
    switch (MotionEventCompat.getActionMasked(paramMotionEvent))
    {
    default: 
      if ((!mExclusive) || (!mAnimating)) {
        break;
      }
    }
    for (;;)
    {
      return i;
      mNeedsCancel = i;
      mAlreadyDelayed = false;
      float f1 = computeTargetVelocity(0, paramMotionEvent.getX(), paramView.getWidth(), mTarget.getWidth());
      float f2 = computeTargetVelocity(i, paramMotionEvent.getY(), paramView.getHeight(), mTarget.getHeight());
      mScroller.setTargetVelocity(f1, f2);
      if ((mAnimating) || (!shouldAnimate())) {
        break;
      }
      startAnimating();
      break;
      requestStop();
      break;
      int j = 0;
    }
  }
  
  public abstract void scrollTargetBy(int paramInt1, int paramInt2);
  
  public AutoScrollHelper setActivationDelay(int paramInt)
  {
    mActivationDelay = paramInt;
    return this;
  }
  
  public AutoScrollHelper setEdgeType(int paramInt)
  {
    mEdgeType = paramInt;
    return this;
  }
  
  public AutoScrollHelper setEnabled(boolean paramBoolean)
  {
    if ((mEnabled) && (!paramBoolean)) {
      requestStop();
    }
    mEnabled = paramBoolean;
    return this;
  }
  
  public AutoScrollHelper setExclusive(boolean paramBoolean)
  {
    mExclusive = paramBoolean;
    return this;
  }
  
  public AutoScrollHelper setMaximumEdges(float paramFloat1, float paramFloat2)
  {
    mMaximumEdges[0] = paramFloat1;
    mMaximumEdges[1] = paramFloat2;
    return this;
  }
  
  public AutoScrollHelper setMaximumVelocity(float paramFloat1, float paramFloat2)
  {
    mMaximumVelocity[0] = (paramFloat1 / 1000.0F);
    mMaximumVelocity[1] = (paramFloat2 / 1000.0F);
    return this;
  }
  
  public AutoScrollHelper setMinimumVelocity(float paramFloat1, float paramFloat2)
  {
    mMinimumVelocity[0] = (paramFloat1 / 1000.0F);
    mMinimumVelocity[1] = (paramFloat2 / 1000.0F);
    return this;
  }
  
  public AutoScrollHelper setRampDownDuration(int paramInt)
  {
    mScroller.setRampDownDuration(paramInt);
    return this;
  }
  
  public AutoScrollHelper setRampUpDuration(int paramInt)
  {
    mScroller.setRampUpDuration(paramInt);
    return this;
  }
  
  public AutoScrollHelper setRelativeEdges(float paramFloat1, float paramFloat2)
  {
    mRelativeEdges[0] = paramFloat1;
    mRelativeEdges[1] = paramFloat2;
    return this;
  }
  
  public AutoScrollHelper setRelativeVelocity(float paramFloat1, float paramFloat2)
  {
    mRelativeVelocity[0] = (paramFloat1 / 1000.0F);
    mRelativeVelocity[1] = (paramFloat2 / 1000.0F);
    return this;
  }
  
  boolean shouldAnimate()
  {
    ClampedScroller localClampedScroller = mScroller;
    int i = localClampedScroller.getVerticalDirection();
    int j = localClampedScroller.getHorizontalDirection();
    return ((i != 0) && (canTargetScrollVertically(i))) || ((j != 0) && (canTargetScrollHorizontally(j)));
  }
  
  private static class ClampedScroller
  {
    private long mDeltaTime = 0L;
    private int mDeltaX = 0;
    private int mDeltaY = 0;
    private int mEffectiveRampDown;
    private int mRampDownDuration;
    private int mRampUpDuration;
    private long mStartTime = Long.MIN_VALUE;
    private long mStopTime = -1L;
    private float mStopValue;
    private float mTargetVelocityX;
    private float mTargetVelocityY;
    
    ClampedScroller() {}
    
    private float getValueAt(long paramLong)
    {
      if (paramLong < mStartTime) {
        return 0.0F;
      }
      if ((mStopTime < 0L) || (paramLong < mStopTime)) {
        return 0.5F * AutoScrollHelper.constrain((float)(paramLong - mStartTime) / mRampUpDuration, 0.0F, 1.0F);
      }
      long l = paramLong - mStopTime;
      return 1.0F - mStopValue + mStopValue * AutoScrollHelper.constrain((float)l / mEffectiveRampDown, 0.0F, 1.0F);
    }
    
    private float interpolateValue(float paramFloat)
    {
      return paramFloat * (-4.0F * paramFloat) + 4.0F * paramFloat;
    }
    
    public void computeScrollDelta()
    {
      if (mDeltaTime == 0L) {
        throw new RuntimeException("Cannot compute scroll delta before calling start()");
      }
      long l1 = AnimationUtils.currentAnimationTimeMillis();
      float f = interpolateValue(getValueAt(l1));
      long l2 = l1 - mDeltaTime;
      mDeltaTime = l1;
      mDeltaX = ((int)(f * (float)l2 * mTargetVelocityX));
      mDeltaY = ((int)(f * (float)l2 * mTargetVelocityY));
    }
    
    public int getDeltaX()
    {
      return mDeltaX;
    }
    
    public int getDeltaY()
    {
      return mDeltaY;
    }
    
    public int getHorizontalDirection()
    {
      return (int)(mTargetVelocityX / Math.abs(mTargetVelocityX));
    }
    
    public int getVerticalDirection()
    {
      return (int)(mTargetVelocityY / Math.abs(mTargetVelocityY));
    }
    
    public boolean isFinished()
    {
      return (mStopTime > 0L) && (AnimationUtils.currentAnimationTimeMillis() > mStopTime + mEffectiveRampDown);
    }
    
    public void requestStop()
    {
      long l = AnimationUtils.currentAnimationTimeMillis();
      mEffectiveRampDown = AutoScrollHelper.constrain((int)(l - mStartTime), 0, mRampDownDuration);
      mStopValue = getValueAt(l);
      mStopTime = l;
    }
    
    public void setRampDownDuration(int paramInt)
    {
      mRampDownDuration = paramInt;
    }
    
    public void setRampUpDuration(int paramInt)
    {
      mRampUpDuration = paramInt;
    }
    
    public void setTargetVelocity(float paramFloat1, float paramFloat2)
    {
      mTargetVelocityX = paramFloat1;
      mTargetVelocityY = paramFloat2;
    }
    
    public void start()
    {
      mStartTime = AnimationUtils.currentAnimationTimeMillis();
      mStopTime = -1L;
      mDeltaTime = mStartTime;
      mStopValue = 0.5F;
      mDeltaX = 0;
      mDeltaY = 0;
    }
  }
  
  private class ScrollAnimationRunnable
    implements Runnable
  {
    ScrollAnimationRunnable() {}
    
    public void run()
    {
      if (!mAnimating) {
        return;
      }
      if (mNeedsReset)
      {
        mNeedsReset = false;
        mScroller.start();
      }
      AutoScrollHelper.ClampedScroller localClampedScroller = mScroller;
      if ((localClampedScroller.isFinished()) || (!shouldAnimate()))
      {
        mAnimating = false;
        return;
      }
      if (mNeedsCancel)
      {
        mNeedsCancel = false;
        cancelTargetTouch();
      }
      localClampedScroller.computeScrollDelta();
      int i = localClampedScroller.getDeltaX();
      int j = localClampedScroller.getDeltaY();
      scrollTargetBy(i, j);
      ViewCompat.postOnAnimation(mTarget, this);
    }
  }
}
