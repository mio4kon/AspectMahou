package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.CodeSignature;

abstract class CodeSignatureImpl
  extends MemberSignatureImpl
  implements CodeSignature
{
  Class[] exceptionTypes;
  String[] parameterNames;
  Class[] parameterTypes;
  
  CodeSignatureImpl(int paramInt, String paramString, Class paramClass, Class[] paramArrayOfClass1, String[] paramArrayOfString, Class[] paramArrayOfClass2)
  {
    super(paramInt, paramString, paramClass);
    parameterTypes = paramArrayOfClass1;
    parameterNames = paramArrayOfString;
    exceptionTypes = paramArrayOfClass2;
  }
  
  CodeSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  public Class[] getExceptionTypes()
  {
    if (exceptionTypes == null) {
      exceptionTypes = extractTypes(5);
    }
    return exceptionTypes;
  }
  
  public String[] getParameterNames()
  {
    if (parameterNames == null) {
      parameterNames = extractStrings(4);
    }
    return parameterNames;
  }
  
  public Class[] getParameterTypes()
  {
    if (parameterTypes == null) {
      parameterTypes = extractTypes(3);
    }
    return parameterTypes;
  }
}
