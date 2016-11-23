package org.aspectj.runtime.reflect;

import java.lang.reflect.Modifier;

class StringMaker
{
  static StringMaker longStringMaker;
  static StringMaker middleStringMaker;
  static StringMaker shortStringMaker = new StringMaker();
  int cacheOffset;
  boolean includeArgs = true;
  boolean includeEnclosingPoint = true;
  boolean includeJoinPointTypeName = true;
  boolean includeModifiers = false;
  boolean includeThrows = false;
  boolean shortKindName = true;
  boolean shortPrimaryTypeNames = false;
  boolean shortTypeNames = true;
  
  static
  {
    shortStringMakershortTypeNames = true;
    shortStringMakerincludeArgs = false;
    shortStringMakerincludeThrows = false;
    shortStringMakerincludeModifiers = false;
    shortStringMakershortPrimaryTypeNames = true;
    shortStringMakerincludeJoinPointTypeName = false;
    shortStringMakerincludeEnclosingPoint = false;
    shortStringMakercacheOffset = 0;
    middleStringMaker = new StringMaker();
    middleStringMakershortTypeNames = true;
    middleStringMakerincludeArgs = true;
    middleStringMakerincludeThrows = false;
    middleStringMakerincludeModifiers = false;
    middleStringMakershortPrimaryTypeNames = false;
    shortStringMakercacheOffset = 1;
    longStringMaker = new StringMaker();
    longStringMakershortTypeNames = false;
    longStringMakerincludeArgs = true;
    longStringMakerincludeThrows = false;
    longStringMakerincludeModifiers = true;
    longStringMakershortPrimaryTypeNames = false;
    longStringMakershortKindName = false;
    longStringMakercacheOffset = 2;
  }
  
  StringMaker() {}
  
  public void addSignature(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    if (paramArrayOfClass == null) {
      return;
    }
    if (!includeArgs)
    {
      if (paramArrayOfClass.length == 0)
      {
        paramStringBuffer.append("()");
        return;
      }
      paramStringBuffer.append("(..)");
      return;
    }
    paramStringBuffer.append("(");
    addTypeNames(paramStringBuffer, paramArrayOfClass);
    paramStringBuffer.append(")");
  }
  
  public void addThrows(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    if ((!includeThrows) || (paramArrayOfClass == null) || (paramArrayOfClass.length == 0)) {
      return;
    }
    paramStringBuffer.append(" throws ");
    addTypeNames(paramStringBuffer, paramArrayOfClass);
  }
  
  public void addTypeNames(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    for (int i = 0; i < paramArrayOfClass.length; i++)
    {
      if (i > 0) {
        paramStringBuffer.append(", ");
      }
      paramStringBuffer.append(makeTypeName(paramArrayOfClass[i]));
    }
  }
  
  String makeKindName(String paramString)
  {
    int i = paramString.lastIndexOf('-');
    if (i == -1) {
      return paramString;
    }
    return paramString.substring(i + 1);
  }
  
  String makeModifiersString(int paramInt)
  {
    if (!includeModifiers) {
      return "";
    }
    String str = Modifier.toString(paramInt);
    if (str.length() == 0) {
      return "";
    }
    return str + " ";
  }
  
  public String makePrimaryTypeName(Class paramClass, String paramString)
  {
    return makeTypeName(paramClass, paramString, shortPrimaryTypeNames);
  }
  
  public String makeTypeName(Class paramClass)
  {
    return makeTypeName(paramClass, paramClass.getName(), shortTypeNames);
  }
  
  String makeTypeName(Class paramClass, String paramString, boolean paramBoolean)
  {
    if (paramClass == null) {
      return "ANONYMOUS";
    }
    if (paramClass.isArray())
    {
      Class localClass = paramClass.getComponentType();
      return makeTypeName(localClass, localClass.getName(), paramBoolean) + "[]";
    }
    if (paramBoolean) {
      return stripPackageName(paramString).replace('$', '.');
    }
    return paramString.replace('$', '.');
  }
  
  String stripPackageName(String paramString)
  {
    int i = paramString.lastIndexOf('.');
    if (i == -1) {
      return paramString;
    }
    return paramString.substring(i + 1);
  }
}
