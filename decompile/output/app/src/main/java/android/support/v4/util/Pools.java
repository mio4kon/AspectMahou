package android.support.v4.util;

public final class Pools
{
  private Pools() {}
  
  public static abstract interface Pool<T>
  {
    public abstract T acquire();
    
    public abstract boolean release(T paramT);
  }
  
  public static class SimplePool<T>
    implements Pools.Pool<T>
  {
    private final Object[] mPool;
    private int mPoolSize;
    
    public SimplePool(int paramInt)
    {
      if (paramInt <= 0) {
        throw new IllegalArgumentException("The max pool size must be > 0");
      }
      mPool = new Object[paramInt];
    }
    
    private boolean isInPool(T paramT)
    {
      for (int i = 0; i < mPoolSize; i++) {
        if (mPool[i] == paramT) {
          return true;
        }
      }
      return false;
    }
    
    public T acquire()
    {
      if (mPoolSize > 0)
      {
        int i = -1 + mPoolSize;
        Object localObject = mPool[i];
        mPool[i] = null;
        mPoolSize = (-1 + mPoolSize);
        return localObject;
      }
      return null;
    }
    
    public boolean release(T paramT)
    {
      if (isInPool(paramT)) {
        throw new IllegalStateException("Already in the pool!");
      }
      if (mPoolSize < mPool.length)
      {
        mPool[mPoolSize] = paramT;
        mPoolSize = (1 + mPoolSize);
        return true;
      }
      return false;
    }
  }
  
  public static class SynchronizedPool<T>
    extends Pools.SimplePool<T>
  {
    private final Object mLock = new Object();
    
    public SynchronizedPool(int paramInt)
    {
      super();
    }
    
    public T acquire()
    {
      synchronized (mLock)
      {
        Object localObject3 = super.acquire();
        return localObject3;
      }
    }
    
    public boolean release(T paramT)
    {
      synchronized (mLock)
      {
        boolean bool = super.release(paramT);
        return bool;
      }
    }
  }
}
