package org.aspectj.lang.reflect;

public abstract interface PointcutBasedPerClause
  extends PerClause
{
  public abstract PointcutExpression getPointcutExpression();
}
