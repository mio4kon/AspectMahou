package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.SignaturePattern;

public class SignaturePatternImpl
  implements SignaturePattern
{
  private String sigPattern;
  
  public SignaturePatternImpl(String paramString)
  {
    sigPattern = paramString;
  }
  
  public String asString()
  {
    return sigPattern;
  }
  
  public String toString()
  {
    return asString();
  }
}
