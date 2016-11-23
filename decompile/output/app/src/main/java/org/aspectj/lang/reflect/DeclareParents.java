package org.aspectj.lang.reflect;

import java.lang.reflect.Type;

public abstract interface DeclareParents
{
  public abstract AjType getDeclaringType();
  
  public abstract Type[] getParentTypes()
    throws ClassNotFoundException;
  
  public abstract TypePattern getTargetTypesPattern();
  
  public abstract boolean isExtends();
  
  public abstract boolean isImplements();
}
