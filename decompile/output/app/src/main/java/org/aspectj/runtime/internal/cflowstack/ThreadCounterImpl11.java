package org.aspectj.runtime.internal.cflowstack;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class ThreadCounterImpl11
  implements ThreadCounter
{
  private static final int COLLECT_AT = 20000;
  private static final int MIN_COLLECT_AT = 100;
  private Counter cached_counter;
  private Thread cached_thread;
  private int change_count = 0;
  private Hashtable counters = new Hashtable();
  
  public ThreadCounterImpl11() {}
  
  private Counter getThreadCounter()
  {
    try
    {
      if (Thread.currentThread() == cached_thread) {
        break label225;
      }
      cached_thread = Thread.currentThread();
      cached_counter = ((Counter)counters.get(cached_thread));
      if (cached_counter == null)
      {
        cached_counter = new Counter();
        counters.put(cached_thread, cached_counter);
      }
      change_count = (1 + change_count);
      int i = Math.max(1, counters.size());
      if (change_count <= Math.max(100, 20000 / i)) {
        break label225;
      }
      ArrayList localArrayList = new ArrayList();
      Enumeration localEnumeration = counters.keys();
      while (localEnumeration.hasMoreElements())
      {
        Thread localThread2 = (Thread)localEnumeration.nextElement();
        if (!localThread2.isAlive()) {
          localArrayList.add(localThread2);
        }
      }
      localIterator = localArrayList.iterator();
    }
    finally {}
    Iterator localIterator;
    while (localIterator.hasNext())
    {
      Thread localThread1 = (Thread)localIterator.next();
      counters.remove(localThread1);
    }
    change_count = 0;
    label225:
    Counter localCounter = cached_counter;
    return localCounter;
  }
  
  public void dec()
  {
    Counter localCounter = getThreadCounter();
    value = (-1 + value);
  }
  
  public void inc()
  {
    Counter localCounter = getThreadCounter();
    value = (1 + value);
  }
  
  public boolean isNotZero()
  {
    return getThreadCountervalue != 0;
  }
  
  public void removeThreadCounter() {}
  
  static class Counter
  {
    protected int value = 0;
    
    Counter() {}
  }
}
