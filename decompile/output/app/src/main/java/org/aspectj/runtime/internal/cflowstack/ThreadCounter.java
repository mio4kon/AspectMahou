package org.aspectj.runtime.internal.cflowstack;

public abstract interface ThreadCounter
{
  public abstract void dec();
  
  public abstract void inc();
  
  public abstract boolean isNotZero();
  
  public abstract void removeThreadCounter();
}
