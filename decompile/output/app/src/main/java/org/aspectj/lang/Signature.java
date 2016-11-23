package org.aspectj.lang;

public abstract interface Signature
{
  public abstract Class getDeclaringType();
  
  public abstract String getDeclaringTypeName();
  
  public abstract int getModifiers();
  
  public abstract String getName();
  
  public abstract String toLongString();
  
  public abstract String toShortString();
  
  public abstract String toString();
}
