package android.support.v4.view.animation;

import android.view.animation.Interpolator;

abstract class LookupTableInterpolator
  implements Interpolator
{
  private final float mStepSize;
  private final float[] mValues;
  
  public LookupTableInterpolator(float[] paramArrayOfFloat)
  {
    mValues = paramArrayOfFloat;
    mStepSize = (1.0F / (-1 + mValues.length));
  }
  
  public float getInterpolation(float paramFloat)
  {
    if (paramFloat >= 1.0F) {
      return 1.0F;
    }
    if (paramFloat <= 0.0F) {
      return 0.0F;
    }
    int i = Math.min((int)(paramFloat * (-1 + mValues.length)), -2 + mValues.length);
    float f = (paramFloat - i * mStepSize) / mStepSize;
    return mValues[i] + f * (mValues[(i + 1)] - mValues[i]);
  }
}
