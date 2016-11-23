package org.aspectj.runtime.reflect;

import java.lang.reflect.Method;
import java.util.StringTokenizer;
import org.aspectj.lang.reflect.AdviceSignature;

class AdviceSignatureImpl
  extends CodeSignatureImpl
  implements AdviceSignature
{
  private Method adviceMethod = null;
  Class returnType;
  
  AdviceSignatureImpl(int paramInt, String paramString, Class paramClass1, Class[] paramArrayOfClass1, String[] paramArrayOfString, Class[] paramArrayOfClass2, Class paramClass2)
  {
    super(paramInt, paramString, paramClass1, paramArrayOfClass1, paramArrayOfString, paramArrayOfClass2);
    returnType = paramClass2;
  }
  
  AdviceSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  private String toAdviceName(String paramString)
  {
    if (paramString.indexOf('$') == -1) {}
    String str;
    do
    {
      StringTokenizer localStringTokenizer;
      while (!localStringTokenizer.hasMoreTokens())
      {
        return paramString;
        localStringTokenizer = new StringTokenizer(paramString, "$");
      }
      str = localStringTokenizer.nextToken();
    } while ((!str.startsWith("before")) && (!str.startsWith("after")) && (!str.startsWith("around")));
    return str;
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (includeArgs) {
      localStringBuffer.append(paramStringMaker.makeTypeName(getReturnType()));
    }
    if (includeArgs) {
      localStringBuffer.append(" ");
    }
    localStringBuffer.append(paramStringMaker.makePrimaryTypeName(getDeclaringType(), getDeclaringTypeName()));
    localStringBuffer.append(".");
    localStringBuffer.append(toAdviceName(getName()));
    paramStringMaker.addSignature(localStringBuffer, getParameterTypes());
    paramStringMaker.addThrows(localStringBuffer, getExceptionTypes());
    return localStringBuffer.toString();
  }
  
  public Method getAdvice()
  {
    if (adviceMethod == null) {}
    try
    {
      adviceMethod = getDeclaringType().getDeclaredMethod(getName(), getParameterTypes());
      return adviceMethod;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  public Class getReturnType()
  {
    if (returnType == null) {
      returnType = extractType(6);
    }
    return returnType;
  }
}
