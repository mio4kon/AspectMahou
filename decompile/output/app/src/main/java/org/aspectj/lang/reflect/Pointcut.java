package org.aspectj.lang.reflect;

public abstract interface Pointcut
{
  public abstract AjType getDeclaringType();
  
  public abstract int getModifiers();
  
  public abstract String getName();
  
  public abstract String[] getParameterNames();
  
  public abstract AjType<?>[] getParameterTypes();
  
  public abstract PointcutExpression getPointcutExpression();
}
