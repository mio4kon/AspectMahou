package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.TypePattern;

public class TypePatternImpl
  implements TypePattern
{
  private String typePattern;
  
  public TypePatternImpl(String paramString)
  {
    typePattern = paramString;
  }
  
  public String asString()
  {
    return typePattern;
  }
  
  public String toString()
  {
    return asString();
  }
}
