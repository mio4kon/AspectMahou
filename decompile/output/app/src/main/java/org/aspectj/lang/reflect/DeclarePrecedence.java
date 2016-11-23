package org.aspectj.lang.reflect;

public abstract interface DeclarePrecedence
{
  public abstract AjType getDeclaringType();
  
  public abstract TypePattern[] getPrecedenceOrder();
}
