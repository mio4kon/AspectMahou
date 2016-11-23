package org.aspectj.runtime.internal.cflowstack;

import java.util.Stack;

public class ThreadStackFactoryImpl
  implements ThreadStackFactory
{
  public ThreadStackFactoryImpl() {}
  
  public ThreadCounter getNewThreadCounter()
  {
    return new ThreadCounterImpl(null);
  }
  
  public ThreadStack getNewThreadStack()
  {
    return new ThreadStackImpl(null);
  }
  
  private static class ThreadCounterImpl
    extends ThreadLocal
    implements ThreadCounter
  {
    private ThreadCounterImpl() {}
    
    ThreadCounterImpl(ThreadStackFactoryImpl.1 param1)
    {
      this();
    }
    
    public void dec()
    {
      Counter localCounter = getThreadCounter();
      value = (-1 + value);
    }
    
    public Counter getThreadCounter()
    {
      return (Counter)get();
    }
    
    public void inc()
    {
      Counter localCounter = getThreadCounter();
      value = (1 + value);
    }
    
    public Object initialValue()
    {
      return new Counter();
    }
    
    public boolean isNotZero()
    {
      return getThreadCountervalue != 0;
    }
    
    public void removeThreadCounter()
    {
      remove();
    }
    
    static class Counter
    {
      protected int value = 0;
      
      Counter() {}
    }
  }
  
  private static class ThreadStackImpl
    extends ThreadLocal
    implements ThreadStack
  {
    private ThreadStackImpl() {}
    
    ThreadStackImpl(ThreadStackFactoryImpl.1 param1)
    {
      this();
    }
    
    public Stack getThreadStack()
    {
      return (Stack)get();
    }
    
    public Object initialValue()
    {
      return new Stack();
    }
    
    public void removeThreadStack()
    {
      remove();
    }
  }
}
