package org.aspectj.lang.reflect;

import java.lang.reflect.Method;

public abstract interface MethodSignature
  extends CodeSignature
{
  public abstract Method getMethod();
  
  public abstract Class getReturnType();
}
