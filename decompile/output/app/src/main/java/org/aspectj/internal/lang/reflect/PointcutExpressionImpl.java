package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.PointcutExpression;

public class PointcutExpressionImpl
  implements PointcutExpression
{
  private String expression;
  
  public PointcutExpressionImpl(String paramString)
  {
    expression = paramString;
  }
  
  public String asString()
  {
    return expression;
  }
  
  public String toString()
  {
    return asString();
  }
}
