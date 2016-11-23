package android.support.v4.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

abstract class MapCollections<K, V>
{
  MapCollections<K, V>.EntrySet mEntrySet;
  MapCollections<K, V>.KeySet mKeySet;
  MapCollections<K, V>.ValuesCollection mValues;
  
  MapCollections() {}
  
  public static <K, V> boolean containsAllHelper(Map<K, V> paramMap, Collection<?> paramCollection)
  {
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext()) {
      if (!paramMap.containsKey(localIterator.next())) {
        return false;
      }
    }
    return true;
  }
  
  public static <T> boolean equalsSetHelper(Set<T> paramSet, Object paramObject)
  {
    boolean bool1 = true;
    boolean bool3;
    if (paramSet == paramObject) {
      bool3 = bool1;
    }
    boolean bool2;
    do
    {
      return bool3;
      bool2 = paramObject instanceof Set;
      bool3 = false;
    } while (!bool2);
    Set localSet = (Set)paramObject;
    try
    {
      if (paramSet.size() == localSet.size())
      {
        boolean bool4 = paramSet.containsAll(localSet);
        if (!bool4) {}
      }
      for (;;)
      {
        return bool1;
        bool1 = false;
      }
      return false;
    }
    catch (NullPointerException localNullPointerException)
    {
      return false;
    }
    catch (ClassCastException localClassCastException) {}
  }
  
  public static <K, V> boolean removeAllHelper(Map<K, V> paramMap, Collection<?> paramCollection)
  {
    int i = paramMap.size();
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext()) {
      paramMap.remove(localIterator.next());
    }
    return i != paramMap.size();
  }
  
  public static <K, V> boolean retainAllHelper(Map<K, V> paramMap, Collection<?> paramCollection)
  {
    int i = paramMap.size();
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext()) {
      if (!paramCollection.contains(localIterator.next())) {
        localIterator.remove();
      }
    }
    return i != paramMap.size();
  }
  
  protected abstract void colClear();
  
  protected abstract Object colGetEntry(int paramInt1, int paramInt2);
  
  protected abstract Map<K, V> colGetMap();
  
  protected abstract int colGetSize();
  
  protected abstract int colIndexOfKey(Object paramObject);
  
  protected abstract int colIndexOfValue(Object paramObject);
  
  protected abstract void colPut(K paramK, V paramV);
  
  protected abstract void colRemoveAt(int paramInt);
  
  protected abstract V colSetValue(int paramInt, V paramV);
  
  public Set<Map.Entry<K, V>> getEntrySet()
  {
    if (mEntrySet == null) {
      mEntrySet = new EntrySet();
    }
    return mEntrySet;
  }
  
  public Set<K> getKeySet()
  {
    if (mKeySet == null) {
      mKeySet = new KeySet();
    }
    return mKeySet;
  }
  
  public Collection<V> getValues()
  {
    if (mValues == null) {
      mValues = new ValuesCollection();
    }
    return mValues;
  }
  
  public Object[] toArrayHelper(int paramInt)
  {
    int i = colGetSize();
    Object[] arrayOfObject = new Object[i];
    for (int j = 0; j < i; j++) {
      arrayOfObject[j] = colGetEntry(j, paramInt);
    }
    return arrayOfObject;
  }
  
  public <T> T[] toArrayHelper(T[] paramArrayOfT, int paramInt)
  {
    int i = colGetSize();
    if (paramArrayOfT.length < i) {
      paramArrayOfT = (Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i);
    }
    for (int j = 0; j < i; j++) {
      paramArrayOfT[j] = colGetEntry(j, paramInt);
    }
    if (paramArrayOfT.length > i) {
      paramArrayOfT[i] = null;
    }
    return paramArrayOfT;
  }
  
  final class ArrayIterator<T>
    implements Iterator<T>
  {
    boolean mCanRemove = false;
    int mIndex;
    final int mOffset;
    int mSize;
    
    ArrayIterator(int paramInt)
    {
      mOffset = paramInt;
      mSize = colGetSize();
    }
    
    public boolean hasNext()
    {
      return mIndex < mSize;
    }
    
    public T next()
    {
      Object localObject = colGetEntry(mIndex, mOffset);
      mIndex = (1 + mIndex);
      mCanRemove = true;
      return localObject;
    }
    
    public void remove()
    {
      if (!mCanRemove) {
        throw new IllegalStateException();
      }
      mIndex = (-1 + mIndex);
      mSize = (-1 + mSize);
      mCanRemove = false;
      colRemoveAt(mIndex);
    }
  }
  
  final class EntrySet
    implements Set<Map.Entry<K, V>>
  {
    EntrySet() {}
    
    public boolean add(Map.Entry<K, V> paramEntry)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(Collection<? extends Map.Entry<K, V>> paramCollection)
    {
      int i = colGetSize();
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        colPut(localEntry.getKey(), localEntry.getValue());
      }
      return i != colGetSize();
    }
    
    public void clear()
    {
      colClear();
    }
    
    public boolean contains(Object paramObject)
    {
      if (!(paramObject instanceof Map.Entry)) {}
      Map.Entry localEntry;
      int i;
      do
      {
        return false;
        localEntry = (Map.Entry)paramObject;
        i = colIndexOfKey(localEntry.getKey());
      } while (i < 0);
      return ContainerHelpers.equal(colGetEntry(i, 1), localEntry.getValue());
    }
    
    public boolean containsAll(Collection<?> paramCollection)
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext()) {
        if (!contains(localIterator.next())) {
          return false;
        }
      }
      return true;
    }
    
    public boolean equals(Object paramObject)
    {
      return MapCollections.equalsSetHelper(this, paramObject);
    }
    
    public int hashCode()
    {
      int i = 0;
      int j = -1 + colGetSize();
      if (j >= 0)
      {
        Object localObject1 = colGetEntry(j, 0);
        Object localObject2 = colGetEntry(j, 1);
        int k;
        if (localObject1 == null)
        {
          k = 0;
          label44:
          if (localObject2 != null) {
            break label75;
          }
        }
        label75:
        for (int m = 0;; m = localObject2.hashCode())
        {
          i += (m ^ k);
          j--;
          break;
          k = localObject1.hashCode();
          break label44;
        }
      }
      return i;
    }
    
    public boolean isEmpty()
    {
      return colGetSize() == 0;
    }
    
    public Iterator<Map.Entry<K, V>> iterator()
    {
      return new MapCollections.MapIterator(MapCollections.this);
    }
    
    public boolean remove(Object paramObject)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean removeAll(Collection<?> paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public int size()
    {
      return colGetSize();
    }
    
    public Object[] toArray()
    {
      throw new UnsupportedOperationException();
    }
    
    public <T> T[] toArray(T[] paramArrayOfT)
    {
      throw new UnsupportedOperationException();
    }
  }
  
  final class KeySet
    implements Set<K>
  {
    KeySet() {}
    
    public boolean add(K paramK)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(Collection<? extends K> paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public void clear()
    {
      colClear();
    }
    
    public boolean contains(Object paramObject)
    {
      return colIndexOfKey(paramObject) >= 0;
    }
    
    public boolean containsAll(Collection<?> paramCollection)
    {
      return MapCollections.containsAllHelper(colGetMap(), paramCollection);
    }
    
    public boolean equals(Object paramObject)
    {
      return MapCollections.equalsSetHelper(this, paramObject);
    }
    
    public int hashCode()
    {
      int i = 0;
      int j = -1 + colGetSize();
      if (j >= 0)
      {
        Object localObject = colGetEntry(j, 0);
        if (localObject == null) {}
        for (int k = 0;; k = localObject.hashCode())
        {
          i += k;
          j--;
          break;
        }
      }
      return i;
    }
    
    public boolean isEmpty()
    {
      return colGetSize() == 0;
    }
    
    public Iterator<K> iterator()
    {
      return new MapCollections.ArrayIterator(MapCollections.this, 0);
    }
    
    public boolean remove(Object paramObject)
    {
      int i = colIndexOfKey(paramObject);
      if (i >= 0)
      {
        colRemoveAt(i);
        return true;
      }
      return false;
    }
    
    public boolean removeAll(Collection<?> paramCollection)
    {
      return MapCollections.removeAllHelper(colGetMap(), paramCollection);
    }
    
    public boolean retainAll(Collection<?> paramCollection)
    {
      return MapCollections.retainAllHelper(colGetMap(), paramCollection);
    }
    
    public int size()
    {
      return colGetSize();
    }
    
    public Object[] toArray()
    {
      return toArrayHelper(0);
    }
    
    public <T> T[] toArray(T[] paramArrayOfT)
    {
      return toArrayHelper(paramArrayOfT, 0);
    }
  }
  
  final class MapIterator
    implements Iterator<Map.Entry<K, V>>, Map.Entry<K, V>
  {
    int mEnd = -1 + colGetSize();
    boolean mEntryValid = false;
    int mIndex = -1;
    
    MapIterator() {}
    
    public final boolean equals(Object paramObject)
    {
      int i = 1;
      if (!mEntryValid) {
        throw new IllegalStateException("This container does not support retaining Map.Entry objects");
      }
      if (!(paramObject instanceof Map.Entry)) {
        return false;
      }
      Map.Entry localEntry = (Map.Entry)paramObject;
      if ((ContainerHelpers.equal(localEntry.getKey(), colGetEntry(mIndex, 0))) && (ContainerHelpers.equal(localEntry.getValue(), colGetEntry(mIndex, i)))) {}
      for (;;)
      {
        return i;
        int j = 0;
      }
    }
    
    public K getKey()
    {
      if (!mEntryValid) {
        throw new IllegalStateException("This container does not support retaining Map.Entry objects");
      }
      return colGetEntry(mIndex, 0);
    }
    
    public V getValue()
    {
      if (!mEntryValid) {
        throw new IllegalStateException("This container does not support retaining Map.Entry objects");
      }
      return colGetEntry(mIndex, 1);
    }
    
    public boolean hasNext()
    {
      return mIndex < mEnd;
    }
    
    public final int hashCode()
    {
      if (!mEntryValid) {
        throw new IllegalStateException("This container does not support retaining Map.Entry objects");
      }
      Object localObject1 = colGetEntry(mIndex, 0);
      Object localObject2 = colGetEntry(mIndex, 1);
      int i;
      int j;
      if (localObject1 == null)
      {
        i = 0;
        j = 0;
        if (localObject2 != null) {
          break label69;
        }
      }
      for (;;)
      {
        return j ^ i;
        i = localObject1.hashCode();
        break;
        label69:
        j = localObject2.hashCode();
      }
    }
    
    public Map.Entry<K, V> next()
    {
      mIndex = (1 + mIndex);
      mEntryValid = true;
      return this;
    }
    
    public void remove()
    {
      if (!mEntryValid) {
        throw new IllegalStateException();
      }
      colRemoveAt(mIndex);
      mIndex = (-1 + mIndex);
      mEnd = (-1 + mEnd);
      mEntryValid = false;
    }
    
    public V setValue(V paramV)
    {
      if (!mEntryValid) {
        throw new IllegalStateException("This container does not support retaining Map.Entry objects");
      }
      return colSetValue(mIndex, paramV);
    }
    
    public final String toString()
    {
      return getKey() + "=" + getValue();
    }
  }
  
  final class ValuesCollection
    implements Collection<V>
  {
    ValuesCollection() {}
    
    public boolean add(V paramV)
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(Collection<? extends V> paramCollection)
    {
      throw new UnsupportedOperationException();
    }
    
    public void clear()
    {
      colClear();
    }
    
    public boolean contains(Object paramObject)
    {
      return colIndexOfValue(paramObject) >= 0;
    }
    
    public boolean containsAll(Collection<?> paramCollection)
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext()) {
        if (!contains(localIterator.next())) {
          return false;
        }
      }
      return true;
    }
    
    public boolean isEmpty()
    {
      return colGetSize() == 0;
    }
    
    public Iterator<V> iterator()
    {
      return new MapCollections.ArrayIterator(MapCollections.this, 1);
    }
    
    public boolean remove(Object paramObject)
    {
      int i = colIndexOfValue(paramObject);
      if (i >= 0)
      {
        colRemoveAt(i);
        return true;
      }
      return false;
    }
    
    public boolean removeAll(Collection<?> paramCollection)
    {
      int i = colGetSize();
      boolean bool = false;
      for (int j = 0; j < i; j++) {
        if (paramCollection.contains(colGetEntry(j, 1)))
        {
          colRemoveAt(j);
          j--;
          i--;
          bool = true;
        }
      }
      return bool;
    }
    
    public boolean retainAll(Collection<?> paramCollection)
    {
      int i = colGetSize();
      boolean bool = false;
      for (int j = 0; j < i; j++) {
        if (!paramCollection.contains(colGetEntry(j, 1)))
        {
          colRemoveAt(j);
          j--;
          i--;
          bool = true;
        }
      }
      return bool;
    }
    
    public int size()
    {
      return colGetSize();
    }
    
    public Object[] toArray()
    {
      return toArrayHelper(1);
    }
    
    public <T> T[] toArray(T[] paramArrayOfT)
    {
      return toArrayHelper(paramArrayOfT, 1);
    }
  }
}
