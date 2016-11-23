package android.support.v4.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

final class SwipeProgressBar
{
  private static final int ANIMATION_DURATION_MS = 2000;
  private static final int COLOR1 = -1291845632;
  private static final int COLOR2 = Integer.MIN_VALUE;
  private static final int COLOR3 = 1291845632;
  private static final int COLOR4 = 436207616;
  private static final int FINISH_ANIMATION_DURATION_MS = 1000;
  private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
  private Rect mBounds = new Rect();
  private final RectF mClipRect = new RectF();
  private int mColor1;
  private int mColor2;
  private int mColor3;
  private int mColor4;
  private long mFinishTime;
  private final Paint mPaint = new Paint();
  private View mParent;
  private boolean mRunning;
  private long mStartTime;
  private float mTriggerPercentage;
  
  SwipeProgressBar(View paramView)
  {
    mParent = paramView;
    mColor1 = -1291845632;
    mColor2 = Integer.MIN_VALUE;
    mColor3 = 1291845632;
    mColor4 = 436207616;
  }
  
  private void drawCircle(Canvas paramCanvas, float paramFloat1, float paramFloat2, int paramInt, float paramFloat3)
  {
    mPaint.setColor(paramInt);
    paramCanvas.save();
    paramCanvas.translate(paramFloat1, paramFloat2);
    float f = INTERPOLATOR.getInterpolation(paramFloat3);
    paramCanvas.scale(f, f);
    paramCanvas.drawCircle(0.0F, 0.0F, paramFloat1, mPaint);
    paramCanvas.restore();
  }
  
  private void drawTrigger(Canvas paramCanvas, int paramInt1, int paramInt2)
  {
    mPaint.setColor(mColor1);
    paramCanvas.drawCircle(paramInt1, paramInt2, paramInt1 * mTriggerPercentage, mPaint);
  }
  
  void draw(Canvas paramCanvas)
  {
    int i = mBounds.width();
    int j = mBounds.height();
    int k = i / 2;
    int m = j / 2;
    int n = paramCanvas.save();
    paramCanvas.clipRect(mBounds);
    float f1;
    if ((mRunning) || (mFinishTime > 0L))
    {
      long l1 = AnimationUtils.currentAnimationTimeMillis();
      long l2 = (l1 - mStartTime) % 2000L;
      long l3 = (l1 - mStartTime) / 2000L;
      f1 = (float)l2 / 20.0F;
      boolean bool = mRunning;
      int i1 = 0;
      if (!bool)
      {
        if (l1 - mFinishTime >= 1000L)
        {
          mFinishTime = 0L;
          return;
        }
        float f7 = (float)((l1 - mFinishTime) % 1000L) / 10.0F / 100.0F;
        float f8 = i / 2 * INTERPOLATOR.getInterpolation(f7);
        mClipRect.set(k - f8, 0.0F, f8 + k, j);
        paramCanvas.saveLayerAlpha(mClipRect, 0, 0);
        i1 = 1;
      }
      if (l3 == 0L)
      {
        paramCanvas.drawColor(mColor1);
        if ((f1 >= 0.0F) && (f1 <= 25.0F))
        {
          float f6 = 2.0F * (25.0F + f1) / 100.0F;
          drawCircle(paramCanvas, k, m, mColor1, f6);
        }
        if ((f1 >= 0.0F) && (f1 <= 50.0F))
        {
          float f5 = 2.0F * f1 / 100.0F;
          drawCircle(paramCanvas, k, m, mColor2, f5);
        }
        if ((f1 >= 25.0F) && (f1 <= 75.0F))
        {
          float f4 = 2.0F * (f1 - 25.0F) / 100.0F;
          drawCircle(paramCanvas, k, m, mColor3, f4);
        }
        if ((f1 >= 50.0F) && (f1 <= 100.0F))
        {
          float f3 = 2.0F * (f1 - 50.0F) / 100.0F;
          drawCircle(paramCanvas, k, m, mColor4, f3);
        }
        if ((f1 >= 75.0F) && (f1 <= 100.0F))
        {
          float f2 = 2.0F * (f1 - 75.0F) / 100.0F;
          drawCircle(paramCanvas, k, m, mColor1, f2);
        }
        if ((mTriggerPercentage > 0.0F) && (i1 != 0))
        {
          paramCanvas.restoreToCount(n);
          n = paramCanvas.save();
          paramCanvas.clipRect(mBounds);
          drawTrigger(paramCanvas, k, m);
        }
        ViewCompat.postInvalidateOnAnimation(mParent, mBounds.left, mBounds.top, mBounds.right, mBounds.bottom);
      }
    }
    for (;;)
    {
      paramCanvas.restoreToCount(n);
      return;
      if ((f1 >= 0.0F) && (f1 < 25.0F))
      {
        paramCanvas.drawColor(mColor4);
        break;
      }
      if ((f1 >= 25.0F) && (f1 < 50.0F))
      {
        paramCanvas.drawColor(mColor1);
        break;
      }
      if ((f1 >= 50.0F) && (f1 < 75.0F))
      {
        paramCanvas.drawColor(mColor2);
        break;
      }
      paramCanvas.drawColor(mColor3);
      break;
      if ((mTriggerPercentage > 0.0F) && (mTriggerPercentage <= 1.0D)) {
        drawTrigger(paramCanvas, k, m);
      }
    }
  }
  
  boolean isRunning()
  {
    return (mRunning) || (mFinishTime > 0L);
  }
  
  void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    mBounds.left = paramInt1;
    mBounds.top = paramInt2;
    mBounds.right = paramInt3;
    mBounds.bottom = paramInt4;
  }
  
  void setColorScheme(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    mColor1 = paramInt1;
    mColor2 = paramInt2;
    mColor3 = paramInt3;
    mColor4 = paramInt4;
  }
  
  void setTriggerPercentage(float paramFloat)
  {
    mTriggerPercentage = paramFloat;
    mStartTime = 0L;
    ViewCompat.postInvalidateOnAnimation(mParent, mBounds.left, mBounds.top, mBounds.right, mBounds.bottom);
  }
  
  void start()
  {
    if (!mRunning)
    {
      mTriggerPercentage = 0.0F;
      mStartTime = AnimationUtils.currentAnimationTimeMillis();
      mRunning = true;
      mParent.postInvalidate();
    }
  }
  
  void stop()
  {
    if (mRunning)
    {
      mTriggerPercentage = 0.0F;
      mFinishTime = AnimationUtils.currentAnimationTimeMillis();
      mRunning = false;
      mParent.postInvalidate();
    }
  }
}
