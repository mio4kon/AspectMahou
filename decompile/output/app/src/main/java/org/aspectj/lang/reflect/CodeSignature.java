package org.aspectj.lang.reflect;

public abstract interface CodeSignature
  extends MemberSignature
{
  public abstract Class[] getExceptionTypes();
  
  public abstract String[] getParameterNames();
  
  public abstract Class[] getParameterTypes();
}
