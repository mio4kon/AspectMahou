package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.PerClauseKind;
import org.aspectj.lang.reflect.PointcutBasedPerClause;
import org.aspectj.lang.reflect.PointcutExpression;

public class PointcutBasedPerClauseImpl
  extends PerClauseImpl
  implements PointcutBasedPerClause
{
  private final PointcutExpression pointcutExpression;
  
  public PointcutBasedPerClauseImpl(PerClauseKind paramPerClauseKind, String paramString)
  {
    super(paramPerClauseKind);
    pointcutExpression = new PointcutExpressionImpl(paramString);
  }
  
  public PointcutExpression getPointcutExpression()
  {
    return pointcutExpression;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    switch (1.$SwitchMap$org$aspectj$lang$reflect$PerClauseKind[getKind().ordinal()])
    {
    }
    for (;;)
    {
      localStringBuffer.append(pointcutExpression.asString());
      localStringBuffer.append(")");
      return localStringBuffer.toString();
      localStringBuffer.append("percflow(");
      continue;
      localStringBuffer.append("percflowbelow(");
      continue;
      localStringBuffer.append("pertarget(");
      continue;
      localStringBuffer.append("perthis(");
    }
  }
}
