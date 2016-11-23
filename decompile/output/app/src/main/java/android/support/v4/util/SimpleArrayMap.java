package android.support.v4.util;

import java.util.Map;

public class SimpleArrayMap<K, V>
{
  private static final int BASE_SIZE = 4;
  private static final int CACHE_SIZE = 10;
  private static final boolean DEBUG = false;
  private static final String TAG = "ArrayMap";
  static Object[] mBaseCache;
  static int mBaseCacheSize;
  static Object[] mTwiceBaseCache;
  static int mTwiceBaseCacheSize;
  Object[] mArray;
  int[] mHashes;
  int mSize;
  
  public SimpleArrayMap()
  {
    mHashes = ContainerHelpers.EMPTY_INTS;
    mArray = ContainerHelpers.EMPTY_OBJECTS;
    mSize = 0;
  }
  
  public SimpleArrayMap(int paramInt)
  {
    if (paramInt == 0)
    {
      mHashes = ContainerHelpers.EMPTY_INTS;
      mArray = ContainerHelpers.EMPTY_OBJECTS;
    }
    for (;;)
    {
      mSize = 0;
      return;
      allocArrays(paramInt);
    }
  }
  
  public SimpleArrayMap(SimpleArrayMap paramSimpleArrayMap)
  {
    this();
    if (paramSimpleArrayMap != null) {
      putAll(paramSimpleArrayMap);
    }
  }
  
  private void allocArrays(int paramInt)
  {
    if (paramInt == 8) {}
    for (;;)
    {
      try
      {
        if (mTwiceBaseCache != null)
        {
          Object[] arrayOfObject2 = mTwiceBaseCache;
          mArray = arrayOfObject2;
          mTwiceBaseCache = (Object[])arrayOfObject2[0];
          mHashes = ((int[])arrayOfObject2[1]);
          arrayOfObject2[1] = null;
          arrayOfObject2[0] = null;
          mTwiceBaseCacheSize = -1 + mTwiceBaseCacheSize;
          return;
        }
        mHashes = new int[paramInt];
        mArray = new Object[paramInt << 1];
        return;
      }
      finally {}
      if (paramInt == 4) {
        try
        {
          if (mBaseCache != null)
          {
            Object[] arrayOfObject1 = mBaseCache;
            mArray = arrayOfObject1;
            mBaseCache = (Object[])arrayOfObject1[0];
            mHashes = ((int[])arrayOfObject1[1]);
            arrayOfObject1[1] = null;
            arrayOfObject1[0] = null;
            mBaseCacheSize = -1 + mBaseCacheSize;
            return;
          }
        }
        finally {}
      }
    }
  }
  
  private static void freeArrays(int[] paramArrayOfInt, Object[] paramArrayOfObject, int paramInt)
  {
    if (paramArrayOfInt.length == 8) {
      try
      {
        if (mTwiceBaseCacheSize < 10)
        {
          paramArrayOfObject[0] = mTwiceBaseCache;
          paramArrayOfObject[1] = paramArrayOfInt;
          for (int j = -1 + (paramInt << 1); j >= 2; j--) {
            paramArrayOfObject[j] = null;
          }
          mTwiceBaseCache = paramArrayOfObject;
          mTwiceBaseCacheSize = 1 + mTwiceBaseCacheSize;
        }
        return;
      }
      finally {}
    }
    if (paramArrayOfInt.length == 4) {
      try
      {
        if (mBaseCacheSize < 10)
        {
          paramArrayOfObject[0] = mBaseCache;
          paramArrayOfObject[1] = paramArrayOfInt;
          for (int i = -1 + (paramInt << 1); i >= 2; i--) {
            paramArrayOfObject[i] = null;
          }
          mBaseCache = paramArrayOfObject;
          mBaseCacheSize = 1 + mBaseCacheSize;
        }
        return;
      }
      finally {}
    }
  }
  
  public void clear()
  {
    if (mSize != 0)
    {
      freeArrays(mHashes, mArray, mSize);
      mHashes = ContainerHelpers.EMPTY_INTS;
      mArray = ContainerHelpers.EMPTY_OBJECTS;
      mSize = 0;
    }
  }
  
  public boolean containsKey(Object paramObject)
  {
    return indexOfKey(paramObject) >= 0;
  }
  
  public boolean containsValue(Object paramObject)
  {
    return indexOfValue(paramObject) >= 0;
  }
  
  public void ensureCapacity(int paramInt)
  {
    if (mHashes.length < paramInt)
    {
      int[] arrayOfInt = mHashes;
      Object[] arrayOfObject = mArray;
      allocArrays(paramInt);
      if (mSize > 0)
      {
        System.arraycopy(arrayOfInt, 0, mHashes, 0, mSize);
        System.arraycopy(arrayOfObject, 0, mArray, 0, mSize << 1);
      }
      freeArrays(arrayOfInt, arrayOfObject, mSize);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    for (;;)
    {
      return true;
      if ((paramObject instanceof SimpleArrayMap))
      {
        SimpleArrayMap localSimpleArrayMap = (SimpleArrayMap)paramObject;
        if (size() != localSimpleArrayMap.size()) {
          return false;
        }
        int j = 0;
        try
        {
          while (j < mSize)
          {
            Object localObject4 = keyAt(j);
            Object localObject5 = valueAt(j);
            Object localObject6 = localSimpleArrayMap.get(localObject4);
            if (localObject5 == null)
            {
              if (localObject6 != null) {
                break label241;
              }
              if (!localSimpleArrayMap.containsKey(localObject4)) {
                break label241;
              }
            }
            else
            {
              boolean bool2 = localObject5.equals(localObject6);
              if (!bool2) {
                return false;
              }
            }
            j++;
          }
          if (!(paramObject instanceof Map)) {
            break;
          }
        }
        catch (NullPointerException localNullPointerException2)
        {
          return false;
        }
        catch (ClassCastException localClassCastException2)
        {
          return false;
        }
      }
      else
      {
        Map localMap = (Map)paramObject;
        if (size() != localMap.size()) {
          return false;
        }
        int i = 0;
        try
        {
          while (i < mSize)
          {
            Object localObject1 = keyAt(i);
            Object localObject2 = valueAt(i);
            Object localObject3 = localMap.get(localObject1);
            if (localObject2 == null)
            {
              if (localObject3 != null) {
                break label243;
              }
              if (!localMap.containsKey(localObject1)) {
                break label243;
              }
            }
            else
            {
              boolean bool1 = localObject2.equals(localObject3);
              if (!bool1) {
                return false;
              }
            }
            i++;
          }
          return false;
        }
        catch (NullPointerException localNullPointerException1)
        {
          return false;
        }
        catch (ClassCastException localClassCastException1)
        {
          return false;
        }
      }
    }
    label241:
    return false;
    label243:
    return false;
  }
  
  public V get(Object paramObject)
  {
    int i = indexOfKey(paramObject);
    if (i >= 0) {
      return mArray[(1 + (i << 1))];
    }
    return null;
  }
  
  public int hashCode()
  {
    int[] arrayOfInt = mHashes;
    Object[] arrayOfObject = mArray;
    int i = 0;
    int j = 0;
    int k = 1;
    int m = mSize;
    if (j < m)
    {
      Object localObject = arrayOfObject[k];
      int n = arrayOfInt[j];
      if (localObject == null) {}
      for (int i1 = 0;; i1 = localObject.hashCode())
      {
        i += (i1 ^ n);
        j++;
        k += 2;
        break;
      }
    }
    return i;
  }
  
  int indexOf(Object paramObject, int paramInt)
  {
    int i = mSize;
    int j;
    if (i == 0) {
      j = -1;
    }
    do
    {
      return j;
      j = ContainerHelpers.binarySearch(mHashes, i, paramInt);
    } while ((j < 0) || (paramObject.equals(mArray[(j << 1)])));
    for (int k = j + 1; (k < i) && (mHashes[k] == paramInt); k++) {
      if (paramObject.equals(mArray[(k << 1)])) {
        return k;
      }
    }
    for (int m = j - 1; (m >= 0) && (mHashes[m] == paramInt); m--) {
      if (paramObject.equals(mArray[(m << 1)])) {
        return m;
      }
    }
    return k ^ 0xFFFFFFFF;
  }
  
  public int indexOfKey(Object paramObject)
  {
    if (paramObject == null) {
      return indexOfNull();
    }
    return indexOf(paramObject, paramObject.hashCode());
  }
  
  int indexOfNull()
  {
    int i = mSize;
    int j;
    if (i == 0) {
      j = -1;
    }
    do
    {
      return j;
      j = ContainerHelpers.binarySearch(mHashes, i, 0);
    } while ((j < 0) || (mArray[(j << 1)] == null));
    for (int k = j + 1; (k < i) && (mHashes[k] == 0); k++) {
      if (mArray[(k << 1)] == null) {
        return k;
      }
    }
    for (int m = j - 1; (m >= 0) && (mHashes[m] == 0); m--) {
      if (mArray[(m << 1)] == null) {
        return m;
      }
    }
    return k ^ 0xFFFFFFFF;
  }
  
  int indexOfValue(Object paramObject)
  {
    int i = 2 * mSize;
    Object[] arrayOfObject = mArray;
    if (paramObject == null) {
      for (int k = 1; k < i; k += 2) {
        if (arrayOfObject[k] == null) {
          return k >> 1;
        }
      }
    }
    for (int j = 1; j < i; j += 2) {
      if (paramObject.equals(arrayOfObject[j])) {
        return j >> 1;
      }
    }
    return -1;
  }
  
  public boolean isEmpty()
  {
    return mSize <= 0;
  }
  
  public K keyAt(int paramInt)
  {
    return mArray[(paramInt << 1)];
  }
  
  public V put(K paramK, V paramV)
  {
    int i = 8;
    int j;
    if (paramK == null) {
      j = 0;
    }
    for (int k = indexOfNull(); k >= 0; k = indexOf(paramK, j))
    {
      int n = 1 + (k << 1);
      Object localObject = mArray[n];
      mArray[n] = paramV;
      return localObject;
      j = paramK.hashCode();
    }
    int m = k ^ 0xFFFFFFFF;
    if (mSize >= mHashes.length)
    {
      if (mSize < i) {
        break label275;
      }
      i = mSize + (mSize >> 1);
    }
    for (;;)
    {
      int[] arrayOfInt = mHashes;
      Object[] arrayOfObject = mArray;
      allocArrays(i);
      if (mHashes.length > 0)
      {
        System.arraycopy(arrayOfInt, 0, mHashes, 0, arrayOfInt.length);
        System.arraycopy(arrayOfObject, 0, mArray, 0, arrayOfObject.length);
      }
      freeArrays(arrayOfInt, arrayOfObject, mSize);
      if (m < mSize)
      {
        System.arraycopy(mHashes, m, mHashes, m + 1, mSize - m);
        System.arraycopy(mArray, m << 1, mArray, m + 1 << 1, mSize - m << 1);
      }
      mHashes[m] = j;
      mArray[(m << 1)] = paramK;
      mArray[(1 + (m << 1))] = paramV;
      mSize = (1 + mSize);
      return null;
      label275:
      if (mSize < 4) {
        i = 4;
      }
    }
  }
  
  public void putAll(SimpleArrayMap<? extends K, ? extends V> paramSimpleArrayMap)
  {
    int i = mSize;
    ensureCapacity(i + mSize);
    if (mSize == 0) {
      if (i > 0)
      {
        System.arraycopy(mHashes, 0, mHashes, 0, i);
        System.arraycopy(mArray, 0, mArray, 0, i << 1);
        mSize = i;
      }
    }
    for (;;)
    {
      return;
      for (int j = 0; j < i; j++) {
        put(paramSimpleArrayMap.keyAt(j), paramSimpleArrayMap.valueAt(j));
      }
    }
  }
  
  public V remove(Object paramObject)
  {
    int i = indexOfKey(paramObject);
    if (i >= 0) {
      return removeAt(i);
    }
    return null;
  }
  
  public V removeAt(int paramInt)
  {
    int i = 8;
    Object localObject = mArray[(1 + (paramInt << 1))];
    if (mSize <= 1)
    {
      freeArrays(mHashes, mArray, mSize);
      mHashes = ContainerHelpers.EMPTY_INTS;
      mArray = ContainerHelpers.EMPTY_OBJECTS;
      mSize = 0;
    }
    int[] arrayOfInt;
    Object[] arrayOfObject;
    do
    {
      return localObject;
      if ((mHashes.length <= i) || (mSize >= mHashes.length / 3)) {
        break;
      }
      if (mSize > i) {
        i = mSize + (mSize >> 1);
      }
      arrayOfInt = mHashes;
      arrayOfObject = mArray;
      allocArrays(i);
      mSize = (-1 + mSize);
      if (paramInt > 0)
      {
        System.arraycopy(arrayOfInt, 0, mHashes, 0, paramInt);
        System.arraycopy(arrayOfObject, 0, mArray, 0, paramInt << 1);
      }
    } while (paramInt >= mSize);
    System.arraycopy(arrayOfInt, paramInt + 1, mHashes, paramInt, mSize - paramInt);
    System.arraycopy(arrayOfObject, paramInt + 1 << 1, mArray, paramInt << 1, mSize - paramInt << 1);
    return localObject;
    mSize = (-1 + mSize);
    if (paramInt < mSize)
    {
      System.arraycopy(mHashes, paramInt + 1, mHashes, paramInt, mSize - paramInt);
      System.arraycopy(mArray, paramInt + 1 << 1, mArray, paramInt << 1, mSize - paramInt << 1);
    }
    mArray[(mSize << 1)] = null;
    mArray[(1 + (mSize << 1))] = null;
    return localObject;
  }
  
  public V setValueAt(int paramInt, V paramV)
  {
    int i = 1 + (paramInt << 1);
    Object localObject = mArray[i];
    mArray[i] = paramV;
    return localObject;
  }
  
  public int size()
  {
    return mSize;
  }
  
  public String toString()
  {
    if (isEmpty()) {
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
      Object localObject1 = keyAt(i);
      if (localObject1 != this)
      {
        localStringBuilder.append(localObject1);
        label73:
        localStringBuilder.append('=');
        Object localObject2 = valueAt(i);
        if (localObject2 == this) {
          break label116;
        }
        localStringBuilder.append(localObject2);
      }
      for (;;)
      {
        i++;
        break;
        localStringBuilder.append("(this Map)");
        break label73;
        label116:
        localStringBuilder.append("(this Map)");
      }
    }
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public V valueAt(int paramInt)
  {
    return mArray[(1 + (paramInt << 1))];
  }
}
