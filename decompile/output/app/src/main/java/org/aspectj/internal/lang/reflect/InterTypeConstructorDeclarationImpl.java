package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.InterTypeConstructorDeclaration;

public class InterTypeConstructorDeclarationImpl
  extends InterTypeDeclarationImpl
  implements InterTypeConstructorDeclaration
{
  private Method baseMethod;
  
  public InterTypeConstructorDeclarationImpl(AjType<?> paramAjType, String paramString, int paramInt, Method paramMethod)
  {
    super(paramAjType, paramString, paramInt);
    baseMethod = paramMethod;
  }
  
  public AjType<?>[] getExceptionTypes()
  {
    Class[] arrayOfClass = baseMethod.getExceptionTypes();
    AjType[] arrayOfAjType = new AjType[arrayOfClass.length];
    for (int i = 0; i < arrayOfClass.length; i++) {
      arrayOfAjType[i] = AjTypeSystem.getAjType(arrayOfClass[i]);
    }
    return arrayOfAjType;
  }
  
  public Type[] getGenericParameterTypes()
  {
    Type[] arrayOfType = baseMethod.getGenericParameterTypes();
    AjType[] arrayOfAjType = new AjType[-1 + arrayOfType.length];
    int i = 1;
    if (i < arrayOfType.length)
    {
      if ((arrayOfType[i] instanceof Class)) {
        arrayOfAjType[(i - 1)] = AjTypeSystem.getAjType((Class)arrayOfType[i]);
      }
      for (;;)
      {
        i++;
        break;
        arrayOfAjType[(i - 1)] = arrayOfType[i];
      }
    }
    return arrayOfAjType;
  }
  
  public AjType<?>[] getParameterTypes()
  {
    Class[] arrayOfClass = baseMethod.getParameterTypes();
    AjType[] arrayOfAjType = new AjType[-1 + arrayOfClass.length];
    for (int i = 1; i < arrayOfClass.length; i++) {
      arrayOfAjType[(i - 1)] = AjTypeSystem.getAjType(arrayOfClass[i]);
    }
    return arrayOfAjType;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(Modifier.toString(getModifiers()));
    localStringBuffer.append(" ");
    localStringBuffer.append(targetTypeName);
    localStringBuffer.append(".new");
    localStringBuffer.append("(");
    AjType[] arrayOfAjType = getParameterTypes();
    for (int i = 0; i < -1 + arrayOfAjType.length; i++)
    {
      localStringBuffer.append(arrayOfAjType[i].toString());
      localStringBuffer.append(", ");
    }
    if (arrayOfAjType.length > 0) {
      localStringBuffer.append(arrayOfAjType[(-1 + arrayOfAjType.length)].toString());
    }
    localStringBuffer.append(")");
    return localStringBuffer.toString();
  }
}
