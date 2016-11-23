package org.aspectj.internal.lang.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.aspectj.lang.annotation.AdviceName;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.lang.reflect.AdviceKind;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.AjTypeSystem;
import org.aspectj.lang.reflect.PointcutExpression;

public class AdviceImpl
  implements Advice
{
  private static final String AJC_INTERNAL = "org.aspectj.runtime.internal";
  private final Method adviceMethod;
  private AjType[] exceptionTypes;
  private Type[] genericParameterTypes;
  private boolean hasExtraParam = false;
  private final AdviceKind kind;
  private AjType[] parameterTypes;
  private PointcutExpression pointcutExpression;
  
  protected AdviceImpl(Method paramMethod, String paramString, AdviceKind paramAdviceKind)
  {
    kind = paramAdviceKind;
    adviceMethod = paramMethod;
    pointcutExpression = new PointcutExpressionImpl(paramString);
  }
  
  protected AdviceImpl(Method paramMethod, String paramString1, AdviceKind paramAdviceKind, String paramString2)
  {
    this(paramMethod, paramString1, paramAdviceKind);
  }
  
  public AjType getDeclaringType()
  {
    return AjTypeSystem.getAjType(adviceMethod.getDeclaringClass());
  }
  
  public AjType<?>[] getExceptionTypes()
  {
    if (exceptionTypes == null)
    {
      Class[] arrayOfClass = adviceMethod.getExceptionTypes();
      exceptionTypes = new AjType[arrayOfClass.length];
      for (int i = 0; i < arrayOfClass.length; i++) {
        exceptionTypes[i] = AjTypeSystem.getAjType(arrayOfClass[i]);
      }
    }
    return exceptionTypes;
  }
  
  public Type[] getGenericParameterTypes()
  {
    if (genericParameterTypes == null)
    {
      Type[] arrayOfType = adviceMethod.getGenericParameterTypes();
      int i = 0;
      int j = arrayOfType.length;
      for (int k = 0; k < j; k++)
      {
        Type localType = arrayOfType[k];
        if (((localType instanceof Class)) && (((Class)localType).getPackage().getName().equals("org.aspectj.runtime.internal"))) {
          i++;
        }
      }
      genericParameterTypes = new Type[arrayOfType.length - i];
      int m = 0;
      if (m < genericParameterTypes.length)
      {
        if ((arrayOfType[m] instanceof Class)) {
          genericParameterTypes[m] = AjTypeSystem.getAjType((Class)arrayOfType[m]);
        }
        for (;;)
        {
          m++;
          break;
          genericParameterTypes[m] = arrayOfType[m];
        }
      }
    }
    return genericParameterTypes;
  }
  
  public AdviceKind getKind()
  {
    return kind;
  }
  
  public String getName()
  {
    String str = adviceMethod.getName();
    if (str.startsWith("ajc$"))
    {
      str = "";
      AdviceName localAdviceName = (AdviceName)adviceMethod.getAnnotation(AdviceName.class);
      if (localAdviceName != null) {
        str = localAdviceName.value();
      }
    }
    return str;
  }
  
  public AjType<?>[] getParameterTypes()
  {
    if (parameterTypes == null)
    {
      Class[] arrayOfClass = adviceMethod.getParameterTypes();
      int i = 0;
      int j = arrayOfClass.length;
      for (int k = 0; k < j; k++) {
        if (arrayOfClass[k].getPackage().getName().equals("org.aspectj.runtime.internal")) {
          i++;
        }
      }
      parameterTypes = new AjType[arrayOfClass.length - i];
      for (int m = 0; m < parameterTypes.length; m++) {
        parameterTypes[m] = AjTypeSystem.getAjType(arrayOfClass[m]);
      }
    }
    return parameterTypes;
  }
  
  public PointcutExpression getPointcutExpression()
  {
    return pointcutExpression;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (getName().length() > 0)
    {
      localStringBuffer.append("@AdviceName(\"");
      localStringBuffer.append(getName());
      localStringBuffer.append("\") ");
    }
    if (getKind() == AdviceKind.AROUND)
    {
      localStringBuffer.append(adviceMethod.getGenericReturnType().toString());
      localStringBuffer.append(" ");
    }
    switch (1.$SwitchMap$org$aspectj$lang$reflect$AdviceKind[getKind().ordinal()])
    {
    }
    AjType[] arrayOfAjType1;
    int i;
    for (;;)
    {
      arrayOfAjType1 = getParameterTypes();
      i = arrayOfAjType1.length;
      if (hasExtraParam) {
        i--;
      }
      for (int j = 0; j < i; j++)
      {
        localStringBuffer.append(arrayOfAjType1[j].getName());
        if (j + 1 < i) {
          localStringBuffer.append(",");
        }
      }
      localStringBuffer.append("after(");
      continue;
      localStringBuffer.append("after(");
      continue;
      localStringBuffer.append("after(");
      continue;
      localStringBuffer.append("around(");
      continue;
      localStringBuffer.append("before(");
    }
    localStringBuffer.append(") ");
    switch (1.$SwitchMap$org$aspectj$lang$reflect$AdviceKind[getKind().ordinal()])
    {
    }
    for (;;)
    {
      AjType[] arrayOfAjType2 = getExceptionTypes();
      if (arrayOfAjType2.length <= 0) {
        break label442;
      }
      localStringBuffer.append("throws ");
      for (int k = 0; k < arrayOfAjType2.length; k++)
      {
        localStringBuffer.append(arrayOfAjType2[k].getName());
        if (k + 1 < arrayOfAjType2.length) {
          localStringBuffer.append(",");
        }
      }
      localStringBuffer.append("returning");
      if (hasExtraParam)
      {
        localStringBuffer.append("(");
        localStringBuffer.append(arrayOfAjType1[(i - 1)].getName());
        localStringBuffer.append(") ");
      }
      localStringBuffer.append("throwing");
      if (hasExtraParam)
      {
        localStringBuffer.append("(");
        localStringBuffer.append(arrayOfAjType1[(i - 1)].getName());
        localStringBuffer.append(") ");
      }
    }
    localStringBuffer.append(" ");
    label442:
    localStringBuffer.append(": ");
    localStringBuffer.append(getPointcutExpression().asString());
    return localStringBuffer.toString();
  }
}
