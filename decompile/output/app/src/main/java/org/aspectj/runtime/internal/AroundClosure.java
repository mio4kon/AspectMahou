package org.aspectj.runtime.internal;

import org.aspectj.lang.ProceedingJoinPoint;

public abstract class AroundClosure
{
  protected int bitflags = 1048576;
  protected Object[] preInitializationState;
  protected Object[] state;
  
  public AroundClosure() {}
  
  public AroundClosure(Object[] paramArrayOfObject)
  {
    state = paramArrayOfObject;
  }
  
  public int getFlags()
  {
    return bitflags;
  }
  
  public Object[] getPreInitializationState()
  {
    return preInitializationState;
  }
  
  public Object[] getState()
  {
    return state;
  }
  
  public ProceedingJoinPoint linkClosureAndJoinPoint()
  {
    ProceedingJoinPoint localProceedingJoinPoint = (ProceedingJoinPoint)state[(-1 + state.length)];
    localProceedingJoinPoint.set$AroundClosure(this);
    return localProceedingJoinPoint;
  }
  
  public ProceedingJoinPoint linkClosureAndJoinPoint(int paramInt)
  {
    ProceedingJoinPoint localProceedingJoinPoint = (ProceedingJoinPoint)state[(-1 + state.length)];
    localProceedingJoinPoint.set$AroundClosure(this);
    bitflags = paramInt;
    return localProceedingJoinPoint;
  }
  
  public abstract Object run(Object[] paramArrayOfObject)
    throws Throwable;
}
