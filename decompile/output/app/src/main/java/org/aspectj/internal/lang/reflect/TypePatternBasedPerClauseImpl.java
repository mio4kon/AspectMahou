package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.PerClauseKind;
import org.aspectj.lang.reflect.TypePattern;
import org.aspectj.lang.reflect.TypePatternBasedPerClause;

public class TypePatternBasedPerClauseImpl
  extends PerClauseImpl
  implements TypePatternBasedPerClause
{
  private TypePattern typePattern;
  
  public TypePatternBasedPerClauseImpl(PerClauseKind paramPerClauseKind, String paramString)
  {
    super(paramPerClauseKind);
    typePattern = new TypePatternImpl(paramString);
  }
  
  public TypePattern getTypePattern()
  {
    return typePattern;
  }
  
  public String toString()
  {
    return "pertypewithin(" + typePattern.asString() + ")";
  }
}
