package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.PerClause;
import org.aspectj.lang.reflect.PerClauseKind;

public class PerClauseImpl
  implements PerClause
{
  private final PerClauseKind kind;
  
  protected PerClauseImpl(PerClauseKind paramPerClauseKind)
  {
    kind = paramPerClauseKind;
  }
  
  public PerClauseKind getKind()
  {
    return kind;
  }
  
  public String toString()
  {
    return "issingleton()";
  }
}
