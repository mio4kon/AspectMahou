package org.aspectj.runtime.reflect;

import java.lang.reflect.Field;
import org.aspectj.lang.reflect.FieldSignature;

public class FieldSignatureImpl
  extends MemberSignatureImpl
  implements FieldSignature
{
  private Field field;
  Class fieldType;
  
  FieldSignatureImpl(int paramInt, String paramString, Class paramClass1, Class paramClass2)
  {
    super(paramInt, paramString, paramClass1);
    fieldType = paramClass2;
  }
  
  FieldSignatureImpl(String paramString)
  {
    super(paramString);
  }
  
  protected String createToString(StringMaker paramStringMaker)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramStringMaker.makeModifiersString(getModifiers()));
    if (includeArgs) {
      localStringBuffer.append(paramStringMaker.makeTypeName(getFieldType()));
    }
    if (includeArgs) {
      localStringBuffer.append(" ");
    }
    localStringBuffer.append(paramStringMaker.makePrimaryTypeName(getDeclaringType(), getDeclaringTypeName()));
    localStringBuffer.append(".");
    localStringBuffer.append(getName());
    return localStringBuffer.toString();
  }
  
  public Field getField()
  {
    if (field == null) {}
    try
    {
      field = getDeclaringType().getDeclaredField(getName());
      return field;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  public Class getFieldType()
  {
    if (fieldType == null) {
      fieldType = extractType(3);
    }
    return fieldType;
  }
}
