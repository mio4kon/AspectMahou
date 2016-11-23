package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.DeclareSoft;
import org.aspectj.lang.reflect.PointcutExpression;

public class DeclareSoftImpl
  implements DeclareSoft
{
  private AjType<?> declaringType;
  private AjType<?> exceptionType;
  private String missingTypeName;
  private PointcutExpression pointcut;
  
  public DeclareSoftImpl(AjType<?> paramAjType, String paramString1, String paramString2)
  {
    declaringType = paramAjType;
    pointcut = new PointcutExpressionImpl(paramString1);
    try
    {
      exceptionType = AjTypeSystem.getAjType(Class.forName(paramString2, false, paramAjType.getJavaClass().getClassLoader()));
      return;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      missingTypeName = paramString2;
    }
  }
  
  public AjType getDeclaringType()
  {
    return declaringType;
  }
  
  public PointcutExpression getPointcutExpression()
  {
    return pointcut;
  }
  
  public AjType getSoftenedExceptionType()
    throws ClassNotFoundException
  {
    if (missingTypeName != null) {
      throw new ClassNotFoundException(missingTypeName);
    }
    return exceptionType;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("declare soft : ");
    if (missingTypeName != null) {
      localStringBuffer.append(exceptionType.getName());
    }
    for (;;)
    {
      localStringBuffer.append(" : ");
      localStringBuffer.append(getPointcutExpression().asString());
      return localStringBuffer.toString();
      localStringBuffer.append(missingTypeName);
    }
  }
}
