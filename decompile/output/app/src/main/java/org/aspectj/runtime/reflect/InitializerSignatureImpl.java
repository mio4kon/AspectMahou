package org.aspectj.runtime.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.aspectj.lang.reflect.InitializerSignature;

class InitializerSignatureImpl
  extends CodeSignatureImpl
  implements InitializerSignature
{
  private Constructor constructor;
  
  InitializerSignatureImpl(int paramInt, Class paramClass) {}
  
  InitializerSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramStringMaker.makeModifiersString(getModifiers()));
    localStringBuffer.append(paramStringMaker.makePrimaryTypeName(getDeclaringType(), getDeclaringTypeName()));
    localStringBuffer.append(".");
    localStringBuffer.append(getName());
    return localStringBuffer.toString();
  }
  
  public Constructor getInitializer()
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
    if (Modifier.isStatic(getModifiers())) {
      return "<clinit>";
    }
    return "<init>";
  }
}
