package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.InterTypeFieldDeclaration;

public class InterTypeFieldDeclarationImpl
  extends InterTypeDeclarationImpl
  implements InterTypeFieldDeclaration
{
  private Type genericType;
  private String name;
  private AjType<?> type;
  
  public InterTypeFieldDeclarationImpl(AjType<?> paramAjType1, String paramString1, int paramInt, String paramString2, AjType<?> paramAjType2, Type paramType)
  {
    super(paramAjType1, paramString1, paramInt);
    name = paramString2;
    type = paramAjType2;
    genericType = paramType;
  }
  
  public InterTypeFieldDeclarationImpl(AjType<?> paramAjType1, AjType<?> paramAjType2, Field paramField)
  {
    super(paramAjType1, paramAjType2, paramField.getModifiers());
    name = paramField.getName();
    type = AjTypeSystem.getAjType(paramField.getType());
    Type localType = paramField.getGenericType();
    if ((localType instanceof Class))
    {
      genericType = AjTypeSystem.getAjType((Class)localType);
      return;
    }
    genericType = localType;
  }
  
  public Type getGenericType()
  {
    return genericType;
  }
  
  public String getName()
  {
    return name;
  }
  
  public AjType<?> getType()
  {
    return type;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(Modifier.toString(getModifiers()));
    localStringBuffer.append(" ");
    localStringBuffer.append(getType().toString());
    localStringBuffer.append(" ");
    localStringBuffer.append(targetTypeName);
    localStringBuffer.append(".");
    localStringBuffer.append(getName());
    return localStringBuffer.toString();
  }
}
