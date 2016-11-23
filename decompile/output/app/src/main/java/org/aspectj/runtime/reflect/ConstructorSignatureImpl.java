package org.aspectj.runtime.reflect;

import java.lang.reflect.Constructor;
import org.aspectj.lang.reflect.ConstructorSignature;

class ConstructorSignatureImpl
  extends CodeSignatureImpl
  implements ConstructorSignature
{
  private Constructor constructor;
  
  ConstructorSignatureImpl(int paramInt, Class paramClass, Class[] paramArrayOfClass1, String[] paramArrayOfString, Class[] paramArrayOfClass2)
  {
    super(paramInt, "<init>", paramClass, paramArrayOfClass1, paramArrayOfString, paramArrayOfClass2);
  }
  
  ConstructorSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramStringMaker.makeModifiersString(getModifiers()));
    localStringBuffer.append(paramStringMaker.makePrimaryTypeName(getDeclaringType(), getDeclaringTypeName()));
    paramStringMaker.addSignature(localStringBuffer, getParameterTypes());
    paramStringMaker.addThrows(localStringBuffer, getExceptionTypes());
    return localStringBuffer.toString();
  }
  
  public Constructor getConstructor()
  {
    if (constructor == null) {}
    try
    {
      constructor = getDeclaringType().getDeclaredConstructor(getParameterTypes());
      return constructor;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  public String getName()
  {
    return "<init>";
  }
}
