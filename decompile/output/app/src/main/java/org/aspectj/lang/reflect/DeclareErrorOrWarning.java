package org.aspectj.lang.reflect;

public abstract interface DeclareErrorOrWarning
{
  public abstract AjType getDeclaringType();
  
  public abstract String getMessage();
  
  public abstract PointcutExpression getPointcutExpression();
  
  public abstract boolean isError();
}
