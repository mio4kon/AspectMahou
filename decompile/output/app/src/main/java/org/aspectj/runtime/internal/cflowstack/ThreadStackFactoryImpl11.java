package org.aspectj.runtime.internal.cflowstack;

public class ThreadStackFactoryImpl11
  implements ThreadStackFactory
{
  public ThreadStackFactoryImpl11() {}
  
  public ThreadCounter getNewThreadCounter()
  {
    return new ThreadCounterImpl11();
  }
  
  public ThreadStack getNewThreadStack()
  {
    return new ThreadStackImpl11();
  }
}
