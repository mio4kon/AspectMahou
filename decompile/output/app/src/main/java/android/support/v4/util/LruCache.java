package android.support.v4.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LruCache<K, V>
{
  private int createCount;
  private int evictionCount;
  private int hitCount;
  private final LinkedHashMap<K, V> map;
  private int maxSize;
  private int missCount;
  private int putCount;
  private int size;
  
  public LruCache(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("maxSize <= 0");
    }
    maxSize = paramInt;
    map = new LinkedHashMap(0, 0.75F, true);
  }
  
  private int safeSizeOf(K paramK, V paramV)
  {
    int i = sizeOf(paramK, paramV);
    if (i < 0) {
      throw new IllegalStateException("Negative size: " + paramK + "=" + paramV);
    }
    return i;
  }
  
  protected V create(K paramK)
  {
    return null;
  }
  
  public final int createCount()
  {
    try
    {
      int i = createCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  protected void entryRemoved(boolean paramBoolean, K paramK, V paramV1, V paramV2) {}
  
  public final void evictAll()
  {
    trimToSize(-1);
  }
  
  public final int evictionCount()
  {
    try
    {
      int i = evictionCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public final V get(K paramK)
  {
    if (paramK == null) {
      throw new NullPointerException("key == null");
    }
    Object localObject3;
    try
    {
      Object localObject2 = map.get(paramK);
      if (localObject2 != null)
      {
        hitCount = (1 + hitCount);
        return localObject2;
      }
      missCount = (1 + missCount);
      localObject3 = create(paramK);
      if (localObject3 == null) {
        return null;
      }
    }
    finally {}
    try
    {
      createCount = (1 + createCount);
      Object localObject5 = map.put(paramK, localObject3);
      if (localObject5 != null) {
        map.put(paramK, localObject5);
      }
      for (;;)
      {
        if (localObject5 == null) {
          break;
        }
        entryRemoved(false, paramK, localObject3, localObject5);
        return localObject5;
        size += safeSizeOf(paramK, localObject3);
      }
      trimToSize(maxSize);
    }
    finally {}
    return localObject3;
  }
  
  public final int hitCount()
  {
    try
    {
      int i = hitCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public final int maxSize()
  {
    try
    {
      int i = maxSize;
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public final int missCount()
  {
    try
    {
      int i = missCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public final V put(K paramK, V paramV)
  {
    if ((paramK == null) || (paramV == null)) {
      throw new NullPointerException("key == null || value == null");
    }
    try
    {
      putCount = (1 + putCount);
      size += safeSizeOf(paramK, paramV);
      Object localObject2 = map.put(paramK, paramV);
      if (localObject2 != null) {
        size -= safeSizeOf(paramK, localObject2);
      }
      if (localObject2 != null) {
        entryRemoved(false, paramK, localObject2, paramV);
      }
      trimToSize(maxSize);
      return localObject2;
    }
    finally {}
  }
  
  public final int putCount()
  {
    try
    {
      int i = putCount;
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public final V remove(K paramK)
  {
    if (paramK == null) {
      throw new NullPointerException("key == null");
    }
    try
    {
      Object localObject2 = map.remove(paramK);
      if (localObject2 != null) {
        size -= safeSizeOf(paramK, localObject2);
      }
      if (localObject2 != null) {
        entryRemoved(false, paramK, localObject2, null);
      }
      return localObject2;
    }
    finally {}
  }
  
  public void resize(int paramInt)
  {
    if (paramInt <= 0) {
      throw new IllegalArgumentException("maxSize <= 0");
    }
    try
    {
      maxSize = paramInt;
      trimToSize(paramInt);
      return;
    }
    finally {}
  }
  
  public final int size()
  {
    try
    {
      int i = size;
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  protected int sizeOf(K paramK, V paramV)
  {
    return 1;
  }
  
  public final Map<K, V> snapshot()
  {
    try
    {
      LinkedHashMap localLinkedHashMap = new LinkedHashMap(map);
      return localLinkedHashMap;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }
  
  public final String toString()
  {
    try
    {
      int i = hitCount + missCount;
      int j = 0;
      if (i != 0) {
        j = 100 * hitCount / i;
      }
      Object[] arrayOfObject = new Object[4];
      arrayOfObject[0] = Integer.valueOf(maxSize);
      arrayOfObject[1] = Integer.valueOf(hitCount);
      arrayOfObject[2] = Integer.valueOf(missCount);
      arrayOfObject[3] = Integer.valueOf(j);
      String str = String.format("LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]", arrayOfObject);
      return str;
    }
    finally {}
  }
  
  public void trimToSize(int paramInt)
  {
    Object localObject2;
    Object localObject3;
    try
    {
      if ((size < 0) || ((map.isEmpty()) && (size != 0))) {
        throw new IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");
      }
    }
    finally
    {
      throw localObject1;
      if ((size <= paramInt) || (map.isEmpty())) {
        return;
      }
      Map.Entry localEntry = (Map.Entry)map.entrySet().iterator().next();
      localObject2 = localEntry.getKey();
      localObject3 = localEntry.getValue();
      map.remove(localObject2);
      size -= safeSizeOf(localObject2, localObject3);
      evictionCount = (1 + evictionCount);
    }
  }
}
