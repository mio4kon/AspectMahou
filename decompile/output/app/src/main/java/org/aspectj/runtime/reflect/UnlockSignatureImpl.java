package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.UnlockSignature;

class UnlockSignatureImpl
  extends SignatureImpl
  implements UnlockSignature
{
  private Class parameterType;
  
  UnlockSignatureImpl(Class paramClass)
  {
    super(8, "unlock", paramClass);
    parameterType = paramClass;
  }
  
  UnlockSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    if (parameterType == null) {
      parameterType = extractType(3);
    }
    return "unlock(" + paramStringMaker.makeTypeName(parameterType) + ")";
  }
  
  public Class getParameterType()
  {
    if (parameterType == null) {
      parameterType = extractType(3);
    }
    return parameterType;
  }
}
