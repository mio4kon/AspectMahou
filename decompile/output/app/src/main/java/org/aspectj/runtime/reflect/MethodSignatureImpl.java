package org.aspectj.runtime.reflect;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.aspectj.lang.reflect.MethodSignature;

class MethodSignatureImpl
  extends CodeSignatureImpl
  implements MethodSignature
{
  private Method method;
  Class returnType;
  
  MethodSignatureImpl(int paramInt, String paramString, Class paramClass1, Class[] paramArrayOfClass1, String[] paramArrayOfString, Class[] paramArrayOfClass2, Class paramClass2)
  {
    super(paramInt, paramString, paramClass1, paramArrayOfClass1, paramArrayOfString, paramArrayOfClass2);
    returnType = paramClass2;
  }
  
  MethodSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  private Method search(Class paramClass, String paramString, Class[] paramArrayOfClass, Set paramSet)
  {
    Method localMethod1;
    if (paramClass == null) {
      localMethod1 = null;
    }
    do
    {
      return localMethod1;
      if (!paramSet.contains(paramClass))
      {
        paramSet.add(paramClass);
        try
        {
          Method localMethod2 = paramClass.getDeclaredMethod(paramString, paramArrayOfClass);
          return localMethod2;
        }
        catch (NoSuchMethodException localNoSuchMethodException) {}
      }
      localMethod1 = search(paramClass.getSuperclass(), paramString, paramArrayOfClass, paramSet);
    } while (localMethod1 != null);
    Class[] arrayOfClass = paramClass.getInterfaces();
    if (arrayOfClass != null) {
      for (int i = 0;; i++)
      {
        if (i >= arrayOfClass.length) {
          break label110;
        }
        localMethod1 = search(arrayOfClass[i], paramString, paramArrayOfClass, paramSet);
        if (localMethod1 != null) {
          break;
        }
      }
    }
    label110:
    return null;
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramStringMaker.makeModifiersString(getModifiers()));
    if (includeArgs) {
      localStringBuffer.append(paramStringMaker.makeTypeName(getReturnType()));
    }
    if (includeArgs) {
      localStringBuffer.append(" ");
    }
    localStringBuffer.append(paramStringMaker.makePrimaryTypeName(getDeclaringType(), getDeclaringTypeName()));
    localStringBuffer.append(".");
    localStringBuffer.append(getName());
    paramStringMaker.addSignature(localStringBuffer, getParameterTypes());
    paramStringMaker.addThrows(localStringBuffer, getExceptionTypes());
    return localStringBuffer.toString();
  }
  
  public Method getMethod()
  {
    Class localClass;
    if (method == null) {
      localClass = getDeclaringType();
    }
    try
    {
      method = localClass.getDeclaredMethod(getName(), getParameterTypes());
      return method;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        HashSet localHashSet = new HashSet();
        localHashSet.add(localClass);
        method = search(localClass, getName(), getParameterTypes(), localHashSet);
      }
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
