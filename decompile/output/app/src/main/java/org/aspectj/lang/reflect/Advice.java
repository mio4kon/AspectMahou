package org.aspectj.lang.reflect;

import java.lang.reflect.Type;

public abstract interface Advice
{
  public abstract AjType getDeclaringType();
  
  public abstract AjType<?>[] getExceptionTypes();
  
  public abstract Type[] getGenericParameterTypes();
  
  public abstract AdviceKind getKind();
  
  public abstract String getName();
  
  public abstract AjType<?>[] getParameterTypes();
  
  public abstract PointcutExpression getPointcutExpression();
}
