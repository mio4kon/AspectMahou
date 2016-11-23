package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclareErrorOrWarning;
import org.aspectj.lang.reflect.PointcutExpression;

public class DeclareErrorOrWarningImpl
  implements DeclareErrorOrWarning
{
  private AjType declaringType;
  private boolean isError;
  private String msg;
  private PointcutExpression pc;
  
  public DeclareErrorOrWarningImpl(String paramString1, String paramString2, boolean paramBoolean, AjType paramAjType)
  {
    pc = new PointcutExpressionImpl(paramString1);
    msg = paramString2;
    isError = paramBoolean;
    declaringType = paramAjType;
  }
  
  public AjType getDeclaringType()
  {
    return declaringType;
  }
  
  public String getMessage()
  {
    return msg;
  }
  
  public PointcutExpression getPointcutExpression()
  {
    return pc;
  }
  
  public boolean isError()
  {
    return isError;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("declare ");
    if (isError()) {}
    for (String str = "error : ";; str = "warning : ")
    {
      localStringBuffer.append(str);
      localStringBuffer.append(getPointcutExpression().asString());
      localStringBuffer.append(" : ");
      localStringBuffer.append("\"");
      localStringBuffer.append(getMessage());
      localStringBuffer.append("\"");
      return localStringBuffer.toString();
    }
  }
}
