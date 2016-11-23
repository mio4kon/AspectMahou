package org.aspectj.lang.reflect;

import java.lang.reflect.Field;

public abstract interface FieldSignature
  extends MemberSignature
{
  public abstract Field getField();
  
  public abstract Class getFieldType();
}
