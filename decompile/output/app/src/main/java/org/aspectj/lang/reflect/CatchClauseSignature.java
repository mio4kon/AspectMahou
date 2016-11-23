package org.aspectj.lang.reflect;

import org.aspectj.lang.Signature;

public abstract interface CatchClauseSignature
  extends Signature
{
  public abstract String getParameterName();
  
  public abstract Class getParameterType();
}
