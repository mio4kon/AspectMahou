package org.aspectj.lang.reflect;

import java.lang.reflect.Type;

public abstract interface InterTypeFieldDeclaration
  extends InterTypeDeclaration
{
  public abstract Type getGenericType();
  
  public abstract String getName();
  
  public abstract AjType<?> getType();
}
