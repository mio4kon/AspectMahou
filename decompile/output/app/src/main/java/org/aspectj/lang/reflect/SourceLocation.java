package org.aspectj.lang.reflect;

public abstract interface SourceLocation
{
  public abstract int getColumn();
  
  public abstract String getFileName();
  
  public abstract int getLine();
  
  public abstract Class getWithinType();
}
