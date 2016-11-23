package org.aspectj.lang.reflect;

import java.lang.reflect.Method;

public abstract interface AdviceSignature
  extends CodeSignature
{
  public abstract Method getAdvice();
  
  public abstract Class getReturnType();
}
