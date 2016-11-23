package android.support.v7.widget;

class RtlSpacingHelper
{
  public static final int UNDEFINED = Integer.MIN_VALUE;
  private int mEnd = Integer.MIN_VALUE;
  private int mExplicitLeft = 0;
  private int mExplicitRight = 0;
  private boolean mIsRelative = false;
  private boolean mIsRtl = false;
  private int mLeft = 0;
  private int mRight = 0;
  private int mStart = Integer.MIN_VALUE;
  
  RtlSpacingHelper() {}
  
  public int getEnd()
  {
    if (mIsRtl) {
      return mLeft;
    }
    return mRight;
  }
  
  public int getLeft()
  {
    return mLeft;
  }
  
  public int getRight()
  {
    return mRight;
  }
  
  public int getStart()
  {
    if (mIsRtl) {
      return mRight;
    }
    return mLeft;
  }
  
  public void setAbsolute(int paramInt1, int paramInt2)
  {
    mIsRelative = false;
    if (paramInt1 != Integer.MIN_VALUE)
    {
      mExplicitLeft = paramInt1;
      mLeft = paramInt1;
    }
    if (paramInt2 != Integer.MIN_VALUE)
    {
      mExplicitRight = paramInt2;
      mRight = paramInt2;
    }
  }
  
  public void setDirection(boolean paramBoolean)
  {
    if (paramBoolean == mIsRtl) {
      return;
    }
    mIsRtl = paramBoolean;
    if (mIsRelative)
    {
      if (paramBoolean)
      {
        int k;
        if (mEnd != Integer.MIN_VALUE)
        {
          k = mEnd;
          mLeft = k;
          if (mStart == Integer.MIN_VALUE) {
            break label77;
          }
        }
        label77:
        for (int m = mStart;; m = mExplicitRight)
        {
          mRight = m;
          return;
          k = mExplicitLeft;
          break;
        }
      }
      int i;
      if (mStart != Integer.MIN_VALUE)
      {
        i = mStart;
        mLeft = i;
        if (mEnd == Integer.MIN_VALUE) {
          break label133;
        }
      }
      label133:
      for (int j = mEnd;; j = mExplicitRight)
      {
        mRight = j;
        return;
        i = mExplicitLeft;
        break;
      }
    }
    mLeft = mExplicitLeft;
    mRight = mExplicitRight;
  }
  
  public void setRelative(int paramInt1, int paramInt2)
  {
    mStart = paramInt1;
    mEnd = paramInt2;
    mIsRelative = true;
    if (mIsRtl)
    {
      if (paramInt2 != Integer.MIN_VALUE) {
        mLeft = paramInt2;
      }
      if (paramInt1 != Integer.MIN_VALUE) {
        mRight = paramInt1;
      }
    }
    do
    {
      return;
      if (paramInt1 != Integer.MIN_VALUE) {
        mLeft = paramInt1;
      }
    } while (paramInt2 == Integer.MIN_VALUE);
    mRight = paramInt2;
  }
}
