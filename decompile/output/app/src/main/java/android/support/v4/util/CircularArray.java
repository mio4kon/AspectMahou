package android.support.v4.util;

public final class CircularArray<E>
{
  private int mCapacityBitmask;
  private E[] mElements;
  private int mHead;
  private int mTail;
  
  public CircularArray()
  {
    this(8);
  }
  
  public CircularArray(int paramInt)
  {
    if (paramInt < 1) {
      throw new IllegalArgumentException("capacity must be >= 1");
    }
    if (paramInt > 1073741824) {
      throw new IllegalArgumentException("capacity must be <= 2^30");
    }
    if (Integer.bitCount(paramInt) != 1) {}
    for (int i = Integer.highestOneBit(paramInt - 1) << 1;; i = paramInt)
    {
      mCapacityBitmask = (i - 1);
      mElements = ((Object[])new Object[i]);
      return;
    }
  }
  
  private void doubleCapacity()
  {
    int i = mElements.length;
    int j = i - mHead;
    int k = i << 1;
    if (k < 0) {
      throw new RuntimeException("Max array capacity exceeded");
    }
    Object[] arrayOfObject = new Object[k];
    System.arraycopy(mElements, mHead, arrayOfObject, 0, j);
    System.arraycopy(mElements, 0, arrayOfObject, j, mHead);
    mElements = ((Object[])arrayOfObject);
    mHead = 0;
    mTail = i;
    mCapacityBitmask = (k - 1);
  }
  
  public void addFirst(E paramE)
  {
    mHead = (-1 + mHead & mCapacityBitmask);
    mElements[mHead] = paramE;
    if (mHead == mTail) {
      doubleCapacity();
    }
  }
  
  public void addLast(E paramE)
  {
    mElements[mTail] = paramE;
    mTail = (1 + mTail & mCapacityBitmask);
    if (mTail == mHead) {
      doubleCapacity();
    }
  }
  
  public void clear()
  {
    removeFromStart(size());
  }
  
  public E get(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= size())) {
      throw new ArrayIndexOutOfBoundsException();
    }
    return mElements[(paramInt + mHead & mCapacityBitmask)];
  }
  
  public E getFirst()
  {
    if (mHead == mTail) {
      throw new ArrayIndexOutOfBoundsException();
    }
    return mElements[mHead];
  }
  
  public E getLast()
  {
    if (mHead == mTail) {
      throw new ArrayIndexOutOfBoundsException();
    }
    return mElements[(-1 + mTail & mCapacityBitmask)];
  }
  
  public boolean isEmpty()
  {
    return mHead == mTail;
  }
  
  public E popFirst()
  {
    if (mHead == mTail) {
      throw new ArrayIndexOutOfBoundsException();
    }
    Object localObject = mElements[mHead];
    mElements[mHead] = null;
    mHead = (1 + mHead & mCapacityBitmask);
    return localObject;
  }
  
  public E popLast()
  {
    if (mHead == mTail) {
      throw new ArrayIndexOutOfBoundsException();
    }
    int i = -1 + mTail & mCapacityBitmask;
    Object localObject = mElements[i];
    mElements[i] = null;
    mTail = i;
    return localObject;
  }
  
  public void removeFromEnd(int paramInt)
  {
    if (paramInt <= 0) {}
    int n;
    do
    {
      return;
      if (paramInt > size()) {
        throw new ArrayIndexOutOfBoundsException();
      }
      int i = mTail;
      int j = 0;
      if (paramInt < i) {
        j = mTail - paramInt;
      }
      for (int k = j; k < mTail; k++) {
        mElements[k] = null;
      }
      int m = mTail - j;
      n = paramInt - m;
      mTail -= m;
    } while (n <= 0);
    mTail = mElements.length;
    int i1 = mTail - n;
    for (int i2 = i1; i2 < mTail; i2++) {
      mElements[i2] = null;
    }
    mTail = i1;
  }
  
  public void removeFromStart(int paramInt)
  {
    if (paramInt <= 0) {}
    int m;
    do
    {
      return;
      if (paramInt > size()) {
        throw new ArrayIndexOutOfBoundsException();
      }
      int i = mElements.length;
      if (paramInt < i - mHead) {
        i = paramInt + mHead;
      }
      for (int j = mHead; j < i; j++) {
        mElements[j] = null;
      }
      int k = i - mHead;
      m = paramInt - k;
      mHead = (k + mHead & mCapacityBitmask);
    } while (m <= 0);
    for (int n = 0; n < m; n++) {
      mElements[n] = null;
    }
    mHead = m;
  }
  
  public int size()
  {
    return mTail - mHead & mCapacityBitmask;
  }
}
