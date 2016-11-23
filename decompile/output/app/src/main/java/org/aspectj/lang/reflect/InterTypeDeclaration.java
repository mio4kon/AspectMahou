package org.aspectj.lang.reflect;

public abstract interface InterTypeDeclaration
{
  public abstract AjType<?> getDeclaringType();
  
  public abstract int getModifiers();
  
  public abstract AjType<?> getTargetType()
    throws ClassNotFoundException;
}
