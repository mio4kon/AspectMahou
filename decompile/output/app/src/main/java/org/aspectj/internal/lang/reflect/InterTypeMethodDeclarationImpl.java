package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.InterTypeMethodDeclaration;

public class InterTypeMethodDeclarationImpl
  extends InterTypeDeclarationImpl
  implements InterTypeMethodDeclaration
{
  private Method baseMethod;
  private AjType<?>[] exceptionTypes;
  private Type[] genericParameterTypes;
  private Type genericReturnType;
  private String name;
  private int parameterAdjustmentFactor = 1;
  private AjType<?>[] parameterTypes;
  private AjType<?> returnType;
  
  public InterTypeMethodDeclarationImpl(AjType<?> paramAjType, String paramString1, int paramInt, String paramString2, Method paramMethod)
  {
    super(paramAjType, paramString1, paramInt);
    name = paramString2;
    baseMethod = paramMethod;
  }
  
  public InterTypeMethodDeclarationImpl(AjType<?> paramAjType1, AjType<?> paramAjType2, Method paramMethod, int paramInt)
  {
    super(paramAjType1, paramAjType2, paramInt);
    parameterAdjustmentFactor = 0;
    name = paramMethod.getName();
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
    AjType[] arrayOfAjType = new AjType[arrayOfType.length - parameterAdjustmentFactor];
    int i = parameterAdjustmentFactor;
    if (i < arrayOfType.length)
    {
      if ((arrayOfType[i] instanceof Class)) {
        arrayOfAjType[(i - parameterAdjustmentFactor)] = AjTypeSystem.getAjType((Class)arrayOfType[i]);
      }
      for (;;)
      {
        i++;
        break;
        arrayOfAjType[(i - parameterAdjustmentFactor)] = arrayOfType[i];
      }
    }
    return arrayOfAjType;
  }
  
  public Type getGenericReturnType()
  {
    Object localObject = baseMethod.getGenericReturnType();
    if ((localObject instanceof Class)) {
      localObject = AjTypeSystem.getAjType((Class)localObject);
    }
    return localObject;
  }
  
  public String getName()
  {
    return name;
  }
  
  public AjType<?>[] getParameterTypes()
  {
    Class[] arrayOfClass = baseMethod.getParameterTypes();
    AjType[] arrayOfAjType = new AjType[arrayOfClass.length - parameterAdjustmentFactor];
    for (int i = parameterAdjustmentFactor; i < arrayOfClass.length; i++) {
      arrayOfAjType[(i - parameterAdjustmentFactor)] = AjTypeSystem.getAjType(arrayOfClass[i]);
    }
    return arrayOfAjType;
  }
  
  public AjType<?> getReturnType()
  {
    return AjTypeSystem.getAjType(baseMethod.getReturnType());
  }
  
  public TypeVariable<Method>[] getTypeParameters()
  {
    return baseMethod.getTypeParameters();
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(Modifier.toString(getModifiers()));
    localStringBuffer.append(" ");
    localStringBuffer.append(getReturnType().toString());
    localStringBuffer.append(" ");
    localStringBuffer.append(targetTypeName);
    localStringBuffer.append(".");
    localStringBuffer.append(getName());
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
