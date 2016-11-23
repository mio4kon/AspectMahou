package android.support.v4.animation;

import android.view.View;
import java.util.ArrayList;
import java.util.List;

class GingerbreadAnimatorCompatProvider
  implements AnimatorProvider
{
  GingerbreadAnimatorCompatProvider() {}
  
  public void clearInterpolator(View paramView) {}
  
  public ValueAnimatorCompat emptyValueAnimator()
  {
    return new GingerbreadFloatValueAnimator();
  }
  
  private static class GingerbreadFloatValueAnimator
    implements ValueAnimatorCompat
  {
    private long mDuration = 200L;
    private boolean mEnded = false;
    private float mFraction = 0.0F;
    List<AnimatorListenerCompat> mListeners = new ArrayList();
    private Runnable mLoopRunnable = new Runnable()
    {
      public void run()
      {
        float f = 1.0F * (float)(GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.this.getTime() - mStartTime) / (float)mDuration;
        if ((f > 1.0F) || (mTarget.getParent() == null)) {
          f = 1.0F;
        }
        GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.access$302(GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.this, f);
        GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.this.notifyUpdateListeners();
        if (mFraction >= 1.0F)
        {
          GingerbreadAnimatorCompatProvider.GingerbreadFloatValueAnimator.this.dispatchEnd();
          return;
        }
        mTarget.postDelayed(mLoopRunnable, 16L);
      }
    };
    private long mStartTime;
    private boolean mStarted = false;
    View mTarget;
    List<AnimatorUpdateListenerCompat> mUpdateListeners = new ArrayList();
    
    public GingerbreadFloatValueAnimator() {}
    
    private void dispatchCancel()
    {
      for (int i = -1 + mListeners.size(); i >= 0; i--) {
        ((AnimatorListenerCompat)mListeners.get(i)).onAnimationCancel(this);
      }
    }
    
    private void dispatchEnd()
    {
      for (int i = -1 + mListeners.size(); i >= 0; i--) {
        ((AnimatorListenerCompat)mListeners.get(i)).onAnimationEnd(this);
      }
    }
    
    private void dispatchStart()
    {
      for (int i = -1 + mListeners.size(); i >= 0; i--) {
        ((AnimatorListenerCompat)mListeners.get(i)).onAnimationStart(this);
      }
    }
    
    private long getTime()
    {
      return mTarget.getDrawingTime();
    }
    
    private void notifyUpdateListeners()
    {
      for (int i = -1 + mUpdateListeners.size(); i >= 0; i--) {
        ((AnimatorUpdateListenerCompat)mUpdateListeners.get(i)).onAnimationUpdate(this);
      }
    }
    
    public void addListener(AnimatorListenerCompat paramAnimatorListenerCompat)
    {
      mListeners.add(paramAnimatorListenerCompat);
    }
    
    public void addUpdateListener(AnimatorUpdateListenerCompat paramAnimatorUpdateListenerCompat)
    {
      mUpdateListeners.add(paramAnimatorUpdateListenerCompat);
    }
    
    public void cancel()
    {
      if (mEnded) {
        return;
      }
      mEnded = true;
      if (mStarted) {
        dispatchCancel();
      }
      dispatchEnd();
    }
    
    public float getAnimatedFraction()
    {
      return mFraction;
    }
    
    public void setDuration(long paramLong)
    {
      if (!mStarted) {
        mDuration = paramLong;
      }
    }
    
    public void setTarget(View paramView)
    {
      mTarget = paramView;
    }
    
    public void start()
    {
      if (mStarted) {
        return;
      }
      mStarted = true;
      dispatchStart();
      mFraction = 0.0F;
      mStartTime = getTime();
      mTarget.postDelayed(mLoopRunnable, 16L);
    }
  }
}
