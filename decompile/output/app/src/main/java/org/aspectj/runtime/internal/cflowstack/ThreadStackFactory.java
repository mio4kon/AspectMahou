package org.aspectj.runtime.internal.cflowstack;

public abstract interface ThreadStackFactory
{
  public abstract ThreadCounter getNewThreadCounter();
  
  public abstract ThreadStack getNewThreadStack();
}
