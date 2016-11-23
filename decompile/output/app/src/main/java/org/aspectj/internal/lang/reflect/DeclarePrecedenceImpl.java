package org.aspectj.internal.lang.reflect;

import java.util.StringTokenizer;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.DeclarePrecedence;
import org.aspectj.lang.reflect.TypePattern;

public class DeclarePrecedenceImpl
  implements DeclarePrecedence
{
  private AjType<?> declaringType;
  private TypePattern[] precedenceList;
  private String precedenceString;
  
  public DeclarePrecedenceImpl(String paramString, AjType paramAjType)
  {
    declaringType = paramAjType;
    precedenceString = paramString;
    String str = paramString;
    if (str.startsWith("(")) {
      str = str.substring(1, -1 + str.length());
    }
    StringTokenizer localStringTokenizer = new StringTokenizer(str, ",");
    precedenceList = new TypePattern[localStringTokenizer.countTokens()];
    for (int i = 0; i < precedenceList.length; i++) {
      precedenceList[i] = new TypePatternImpl(localStringTokenizer.nextToken().trim());
    }
  }
  
  public AjType getDeclaringType()
  {
    return declaringType;
  }
  
  public TypePattern[] getPrecedenceOrder()
  {
    return precedenceList;
  }
  
  public String toString()
  {
    return "declare precedence : " + precedenceString;
  }
}
