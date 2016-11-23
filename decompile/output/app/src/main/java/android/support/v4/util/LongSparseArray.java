package android.support.v4.util;

public class LongSparseArray<E>
  implements Cloneable
{
  private static final Object DELETED = new Object();
  private boolean mGarbage = false;
  private long[] mKeys;
  private int mSize;
  private Object[] mValues;
  
  public LongSparseArray()
  {
    this(10);
  }
  
  public LongSparseArray(int paramInt)
  {
    if (paramInt == 0) {
      mKeys = ContainerHelpers.EMPTY_LONGS;
    }
    int i;
    for (mValues = ContainerHelpers.EMPTY_OBJECTS;; mValues = new Object[i])
    {
      mSize = 0;
      return;
      i = ContainerHelpers.idealLongArraySize(paramInt);
      mKeys = new long[i];
    }
  }
  
  private void gc()
  {
    int i = mSize;
    int j = 0;
    long[] arrayOfLong = mKeys;
    Object[] arrayOfObject = mValues;
    for (int k = 0; k < i; k++)
    {
      Object localObject = arrayOfObject[k];
      if (localObject != DELETED)
      {
        if (k != j)
        {
          arrayOfLong[j] = arrayOfLong[k];
          arrayOfObject[j] = localObject;
          arrayOfObject[k] = null;
        }
        j++;
      }
    }
    mGarbage = false;
    mSize = j;
  }
  
  public void append(long paramLong, E paramE)
  {
    if ((mSize != 0) && (paramLong <= mKeys[(-1 + mSize)]))
    {
      put(paramLong, paramE);
      return;
    }
    if ((mGarbage) && (mSize >= mKeys.length)) {
      gc();
    }
    int i = mSize;
    if (i >= mKeys.length)
    {
      int j = ContainerHelpers.idealLongArraySize(i + 1);
      long[] arrayOfLong = new long[j];
      Object[] arrayOfObject = new Object[j];
      System.arraycopy(mKeys, 0, arrayOfLong, 0, mKeys.length);
      System.arraycopy(mValues, 0, arrayOfObject, 0, mValues.length);
      mKeys = arrayOfLong;
      mValues = arrayOfObject;
    }
    mKeys[i] = paramLong;
    mValues[i] = paramE;
    mSize = (i + 1);
  }
  
  public void clear()
  {
    int i = mSize;
    Object[] arrayOfObject = mValues;
    for (int j = 0; j < i; j++) {
      arrayOfObject[j] = null;
    }
    mSize = 0;
    mGarbage = false;
  }
  
  public LongSparseArray<E> clone()
  {
    LongSparseArray localLongSparseArray = null;
    try
    {
      localLongSparseArray = (LongSparseArray)super.clone();
      mKeys = ((long[])mKeys.clone());
      mValues = ((Object[])mValues.clone());
      return localLongSparseArray;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return localLongSparseArray;
  }
  
  public void delete(long paramLong)
  {
    int i = ContainerHelpers.binarySearch(mKeys, mSize, paramLong);
    if ((i >= 0) && (mValues[i] != DELETED))
    {
      mValues[i] = DELETED;
      mGarbage = true;
    }
  }
  
  public E get(long paramLong)
  {
    return get(paramLong, null);
  }
  
  public E get(long paramLong, E paramE)
  {
    int i = ContainerHelpers.binarySearch(mKeys, mSize, paramLong);
    if ((i < 0) || (mValues[i] == DELETED)) {
      return paramE;
    }
    return mValues[i];
  }
  
  public int indexOfKey(long paramLong)
  {
    if (mGarbage) {
      gc();
    }
    return ContainerHelpers.binarySearch(mKeys, mSize, paramLong);
  }
  
  public int indexOfValue(E paramE)
  {
    if (mGarbage) {
      gc();
    }
    for (int i = 0; i < mSize; i++) {
      if (mValues[i] == paramE) {
        return i;
      }
    }
    return -1;
  }
  
  public long keyAt(int paramInt)
  {
    if (mGarbage) {
      gc();
    }
    return mKeys[paramInt];
  }
  
  public void put(long paramLong, E paramE)
  {
    int i = ContainerHelpers.binarySearch(mKeys, mSize, paramLong);
    if (i >= 0)
    {
      mValues[i] = paramE;
      return;
    }
    int j = i ^ 0xFFFFFFFF;
    if ((j < mSize) && (mValues[j] == DELETED))
    {
      mKeys[j] = paramLong;
      mValues[j] = paramE;
      return;
    }
    if ((mGarbage) && (mSize >= mKeys.length))
    {
      gc();
      j = 0xFFFFFFFF ^ ContainerHelpers.binarySearch(mKeys, mSize, paramLong);
    }
    if (mSize >= mKeys.length)
    {
      int k = ContainerHelpers.idealLongArraySize(1 + mSize);
      long[] arrayOfLong = new long[k];
      Object[] arrayOfObject = new Object[k];
      System.arraycopy(mKeys, 0, arrayOfLong, 0, mKeys.length);
      System.arraycopy(mValues, 0, arrayOfObject, 0, mValues.length);
      mKeys = arrayOfLong;
      mValues = arrayOfObject;
    }
    if (mSize - j != 0)
    {
      System.arraycopy(mKeys, j, mKeys, j + 1, mSize - j);
      System.arraycopy(mValues, j, mValues, j + 1, mSize - j);
    }
    mKeys[j] = paramLong;
    mValues[j] = paramE;
    mSize = (1 + mSize);
  }
  
  public void remove(long paramLong)
  {
    delete(paramLong);
  }
  
  public void removeAt(int paramInt)
  {
    if (mValues[paramInt] != DELETED)
    {
      mValues[paramInt] = DELETED;
      mGarbage = true;
    }
  }
  
  public void setValueAt(int paramInt, E paramE)
  {
    if (mGarbage) {
      gc();
    }
    mValues[paramInt] = paramE;
  }
  
  public int size()
  {
    if (mGarbage) {
      gc();
    }
    return mSize;
  }
  
  public String toString()
  {
    if (size() <= 0) {
      return "{}";
    }
    StringBuilder localStringBuilder = new StringBuilder(28 * mSize);
    localStringBuilder.append('{');
    int i = 0;
    if (i < mSize)
    {
      if (i > 0) {
        localStringBuilder.append(", ");
      }
      localStringBuilder.append(keyAt(i));
      localStringBuilder.append('=');
      Object localObject = valueAt(i);
      if (localObject != this) {
        localStringBuilder.append(localObject);
      }
      for (;;)
      {
        i++;
        break;
        localStringBuilder.append("(this Map)");
      }
    }
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public E valueAt(int paramInt)
  {
    if (mGarbage) {
      gc();
    }
    return mValues[paramInt];
  }
}
