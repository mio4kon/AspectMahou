package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.LockSignature;

class LockSignatureImpl
  extends SignatureImpl
  implements LockSignature
{
  private Class parameterType;
  
  LockSignatureImpl(Class paramClass)
  {
    super(8, "lock", paramClass);
    parameterType = paramClass;
  }
  
  LockSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    if (parameterType == null) {
      parameterType = extractType(3);
    }
    return "lock(" + paramStringMaker.makeTypeName(parameterType) + ")";
  }
  
  public Class getParameterType()
  {
    if (parameterType == null) {
      parameterType = extractType(3);
    }
    return parameterType;
  }
}
