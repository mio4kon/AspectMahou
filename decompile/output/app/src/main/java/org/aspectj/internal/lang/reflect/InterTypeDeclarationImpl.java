package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.InterTypeDeclaration;

public class InterTypeDeclarationImpl
  implements InterTypeDeclaration
{
  private AjType<?> declaringType;
  private int modifiers;
  private AjType<?> targetType;
  protected String targetTypeName;
  
  public InterTypeDeclarationImpl(AjType<?> paramAjType, String paramString, int paramInt)
  {
    declaringType = paramAjType;
    targetTypeName = paramString;
    modifiers = paramInt;
    try
    {
      targetType = ((AjType)StringToType.stringToType(paramString, paramAjType.getJavaClass()));
      return;
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
  }
  
  public InterTypeDeclarationImpl(AjType<?> paramAjType1, AjType<?> paramAjType2, int paramInt)
  {
    declaringType = paramAjType1;
    targetType = paramAjType2;
    targetTypeName = paramAjType2.getName();
    modifiers = paramInt;
  }
  
  public AjType<?> getDeclaringType()
  {
    return declaringType;
  }
  
  public int getModifiers()
  {
    return modifiers;
  }
  
  public AjType<?> getTargetType()
    throws ClassNotFoundException
  {
    if (targetType == null) {
      throw new ClassNotFoundException(targetTypeName);
    }
    return targetType;
  }
}
