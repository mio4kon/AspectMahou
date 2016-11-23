package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.CatchClauseSignature;

class CatchClauseSignatureImpl
  extends SignatureImpl
  implements CatchClauseSignature
{
  String parameterName;
  Class parameterType;
  
  CatchClauseSignatureImpl(Class paramClass1, Class paramClass2, String paramString)
  {
    super(0, "catch", paramClass1);
    parameterType = paramClass2;
    parameterName = paramString;
  }
  
  CatchClauseSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    return "catch(" + paramStringMaker.makeTypeName(getParameterType()) + ")";
  }
  
  public String getParameterName()
  {
    if (parameterName == null) {
      parameterName = extractString(4);
    }
    return parameterName;
  }
  
  public Class getParameterType()
  {
    if (parameterType == null) {
      parameterType = extractType(3);
    }
    return parameterType;
  }
}
