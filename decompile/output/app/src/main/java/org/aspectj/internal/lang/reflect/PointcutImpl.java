package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Method;
import java.util.StringTokenizer;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.Pointcut;
import org.aspectj.lang.reflect.PointcutExpression;

public class PointcutImpl
  implements Pointcut
{
  private final Method baseMethod;
  private final AjType declaringType;
  private final String name;
  private String[] parameterNames = new String[0];
  private final PointcutExpression pc;
  
  protected PointcutImpl(String paramString1, String paramString2, Method paramMethod, AjType paramAjType, String paramString3)
  {
    name = paramString1;
    pc = new PointcutExpressionImpl(paramString2);
    baseMethod = paramMethod;
    declaringType = paramAjType;
    parameterNames = splitOnComma(paramString3);
  }
  
  private String[] splitOnComma(String paramString)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
    String[] arrayOfString = new String[localStringTokenizer.countTokens()];
    for (int i = 0; i < arrayOfString.length; i++) {
      arrayOfString[i] = localStringTokenizer.nextToken().trim();
    }
    return arrayOfString;
  }
  
  public AjType getDeclaringType()
  {
    return declaringType;
  }
  
  public int getModifiers()
  {
    return baseMethod.getModifiers();
  }
  
  public String getName()
  {
    return name;
  }
  
  public String[] getParameterNames()
  {
    return parameterNames;
  }
  
  public AjType<?>[] getParameterTypes()
  {
    Class[] arrayOfClass = baseMethod.getParameterTypes();
    AjType[] arrayOfAjType = new AjType[arrayOfClass.length];
    for (int i = 0; i < arrayOfAjType.length; i++) {
      arrayOfAjType[i] = AjTypeSystem.getAjType(arrayOfClass[i]);
    }
    return arrayOfAjType;
  }
  
  public PointcutExpression getPointcutExpression()
  {
    return pc;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(getName());
    localStringBuffer.append("(");
    AjType[] arrayOfAjType = getParameterTypes();
    for (int i = 0; i < arrayOfAjType.length; i++)
    {
      localStringBuffer.append(arrayOfAjType[i].getName());
      if ((parameterNames != null) && (parameterNames[i] != null))
      {
        localStringBuffer.append(" ");
        localStringBuffer.append(parameterNames[i]);
      }
      if (i + 1 < arrayOfAjType.length) {
        localStringBuffer.append(",");
      }
    }
    localStringBuffer.append(") : ");
    localStringBuffer.append(getPointcutExpression().asString());
    return localStringBuffer.toString();
  }
}
