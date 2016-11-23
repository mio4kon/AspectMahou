package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Type;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclareParents;
import org.aspectj.lang.reflect.TypePattern;

public class DeclareParentsImpl
  implements DeclareParents
{
  private AjType<?> declaringType;
  private String firstMissingTypeName;
  private boolean isExtends;
  private Type[] parents;
  private boolean parentsError = false;
  private String parentsString;
  private TypePattern targetTypesPattern;
  
  public DeclareParentsImpl(String paramString1, String paramString2, boolean paramBoolean, AjType<?> paramAjType)
  {
    targetTypesPattern = new TypePatternImpl(paramString1);
    isExtends = paramBoolean;
    declaringType = paramAjType;
    parentsString = paramString2;
    try
    {
      parents = StringToType.commaSeparatedListToTypeArray(paramString2, paramAjType.getJavaClass());
      return;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      parentsError = true;
      firstMissingTypeName = localClassNotFoundException.getMessage();
    }
  }
  
  public AjType getDeclaringType()
  {
    return declaringType;
  }
  
  public Type[] getParentTypes()
    throws ClassNotFoundException
  {
    if (parentsError) {
      throw new ClassNotFoundException(firstMissingTypeName);
    }
    return parents;
  }
  
  public TypePattern getTargetTypesPattern()
  {
    return targetTypesPattern;
  }
  
  public boolean isExtends()
  {
    return isExtends;
  }
  
  public boolean isImplements()
  {
    return !isExtends;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("declare parents : ");
    localStringBuffer.append(getTargetTypesPattern().asString());
    if (isExtends()) {}
    for (String str = " extends ";; str = " implements ")
    {
      localStringBuffer.append(str);
      localStringBuffer.append(parentsString);
      return localStringBuffer.toString();
    }
  }
}
