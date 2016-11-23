package org.aspectj.lang.reflect;

public abstract interface DeclareSoft
{
  public abstract AjType getDeclaringType();
  
  public abstract PointcutExpression getPointcutExpression();
  
  public abstract AjType getSoftenedExceptionType()
    throws ClassNotFoundException;
}
