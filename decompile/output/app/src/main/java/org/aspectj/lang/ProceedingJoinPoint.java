package org.aspectj.lang;

import org.aspectj.runtime.internal.AroundClosure;

public abstract interface ProceedingJoinPoint
  extends JoinPoint
{
  public abstract Object proceed()
    throws Throwable;
  
  public abstract Object proceed(Object[] paramArrayOfObject)
    throws Throwable;
  
  public abstract void set$AroundClosure(AroundClosure paramAroundClosure);
}
