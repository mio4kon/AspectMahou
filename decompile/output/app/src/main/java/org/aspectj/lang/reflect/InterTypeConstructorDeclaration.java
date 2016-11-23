package org.aspectj.lang.reflect;

import java.lang.reflect.Type;

public abstract interface InterTypeConstructorDeclaration
  extends InterTypeDeclaration
{
  public abstract AjType<?>[] getExceptionTypes();
  
  public abstract Type[] getGenericParameterTypes();
  
  public abstract AjType<?>[] getParameterTypes();
}
