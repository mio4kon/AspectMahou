package android.support.v4.util;

public class SparseArrayCompat<E>
  implements Cloneable
{
  private static final Object DELETED = new Object();
  private boolean mGarbage = false;
  private int[] mKeys;
  private int mSize;
  private Object[] mValues;
  
  public SparseArrayCompat()
  {
    this(10);
  }
  
  public SparseArrayCompat(int paramInt)
  {
    if (paramInt == 0) {
      mKeys = ContainerHelpers.EMPTY_INTS;
    }
    int i;
    for (mValues = ContainerHelpers.EMPTY_OBJECTS;; mValues = new Object[i])
    {
      mSize = 0;
      return;
      i = ContainerHelpers.idealIntArraySize(paramInt);
      mKeys = new int[i];
    }
  }
  
  private void gc()
  {
    int i = mSize;
    int j = 0;
    int[] arrayOfInt = mKeys;
    Object[] arrayOfObject = mValues;
    for (int k = 0; k < i; k++)
    {
      Object localObject = arrayOfObject[k];
      if (localObject != DELETED)
      {
        if (k != j)
        {
          arrayOfInt[j] = arrayOfInt[k];
          arrayOfObject[j] = localObject;
          arrayOfObject[k] = null;
        }
        j++;
      }
    }
    mGarbage = false;
    mSize = j;
  }
  
  public void append(int paramInt, E paramE)
  {
    if ((mSize != 0) && (paramInt <= mKeys[(-1 + mSize)]))
    {
      put(paramInt, paramE);
      return;
    }
    if ((mGarbage) && (mSize >= mKeys.length)) {
      gc();
    }
    int i = mSize;
    if (i >= mKeys.length)
    {
      int j = ContainerHelpers.idealIntArraySize(i + 1);
      int[] arrayOfInt = new int[j];
      Object[] arrayOfObject = new Object[j];
      System.arraycopy(mKeys, 0, arrayOfInt, 0, mKeys.length);
      System.arraycopy(mValues, 0, arrayOfObject, 0, mValues.length);
      mKeys = arrayOfInt;
      mValues = arrayOfObject;
    }
    mKeys[i] = paramInt;
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
  
  public SparseArrayCompat<E> clone()
  {
    SparseArrayCompat localSparseArrayCompat = null;
    try
    {
      localSparseArrayCompat = (SparseArrayCompat)super.clone();
      mKeys = ((int[])mKeys.clone());
      mValues = ((Object[])mValues.clone());
      return localSparseArrayCompat;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return localSparseArrayCompat;
  }
  
  public void delete(int paramInt)
  {
    int i = ContainerHelpers.binarySearch(mKeys, mSize, paramInt);
    if ((i >= 0) && (mValues[i] != DELETED))
    {
      mValues[i] = DELETED;
      mGarbage = true;
    }
  }
  
  public E get(int paramInt)
  {
    return get(paramInt, null);
  }
  
  public E get(int paramInt, E paramE)
  {
    int i = ContainerHelpers.binarySearch(mKeys, mSize, paramInt);
    if ((i < 0) || (mValues[i] == DELETED)) {
      return paramE;
    }
    return mValues[i];
  }
  
  public int indexOfKey(int paramInt)
  {
    if (mGarbage) {
      gc();
    }
    return ContainerHelpers.binarySearch(mKeys, mSize, paramInt);
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
  
  public int keyAt(int paramInt)
  {
    if (mGarbage) {
      gc();
    }
    return mKeys[paramInt];
  }
  
  public void put(int paramInt, E paramE)
  {
    int i = ContainerHelpers.binarySearch(mKeys, mSize, paramInt);
    if (i >= 0)
    {
      mValues[i] = paramE;
      return;
    }
    int j = i ^ 0xFFFFFFFF;
    if ((j < mSize) && (mValues[j] == DELETED))
    {
      mKeys[j] = paramInt;
      mValues[j] = paramE;
      return;
    }
    if ((mGarbage) && (mSize >= mKeys.length))
    {
      gc();
      j = 0xFFFFFFFF ^ ContainerHelpers.binarySearch(mKeys, mSize, paramInt);
    }
    if (mSize >= mKeys.length)
    {
      int k = ContainerHelpers.idealIntArraySize(1 + mSize);
      int[] arrayOfInt = new int[k];
      Object[] arrayOfObject = new Object[k];
      System.arraycopy(mKeys, 0, arrayOfInt, 0, mKeys.length);
      System.arraycopy(mValues, 0, arrayOfObject, 0, mValues.length);
      mKeys = arrayOfInt;
      mValues = arrayOfObject;
    }
    if (mSize - j != 0)
    {
      System.arraycopy(mKeys, j, mKeys, j + 1, mSize - j);
      System.arraycopy(mValues, j, mValues, j + 1, mSize - j);
    }
    mKeys[j] = paramInt;
    mValues[j] = paramE;
    mSize = (1 + mSize);
  }
  
  public void remove(int paramInt)
  {
    delete(paramInt);
  }
  
  public void removeAt(int paramInt)
  {
    if (mValues[paramInt] != DELETED)
    {
      mValues[paramInt] = DELETED;
      mGarbage = true;
    }
  }
  
  public void removeAtRange(int paramInt1, int paramInt2)
  {
    int i = Math.min(mSize, paramInt1 + paramInt2);
    for (int j = paramInt1; j < i; j++) {
      removeAt(j);
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
