package org.aspectj.internal.lang.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.StringTokenizer;
import org.aspectj.lang.reflect.AjTypeSystem;

public class StringToType
{
  public StringToType() {}
  
  public static Type[] commaSeparatedListToTypeArray(String paramString, Class paramClass)
    throws ClassNotFoundException
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
    Type[] arrayOfType = new Type[localStringTokenizer.countTokens()];
    int j;
    for (int i = 0; localStringTokenizer.hasMoreTokens(); i = j)
    {
      String str = localStringTokenizer.nextToken().trim();
      j = i + 1;
      arrayOfType[i] = stringToType(str, paramClass);
    }
    return arrayOfType;
  }
  
  private static Type makeParameterizedType(String paramString, Class paramClass)
    throws ClassNotFoundException
  {
    int i = paramString.indexOf('<');
    final Class localClass = Class.forName(paramString.substring(0, i), false, paramClass.getClassLoader());
    int j = paramString.lastIndexOf('>');
    new ParameterizedType()
    {
      public Type[] getActualTypeArguments()
      {
        return val$typeParams;
      }
      
      public Type getOwnerType()
      {
        return localClass.getEnclosingClass();
      }
      
      public Type getRawType()
      {
        return localClass;
      }
    };
  }
  
  public static Type stringToType(String paramString, Class paramClass)
    throws ClassNotFoundException
  {
    try
    {
      if (paramString.indexOf("<") == -1) {
        return AjTypeSystem.getAjType(Class.forName(paramString, false, paramClass.getClassLoader()));
      }
      Type localType = makeParameterizedType(paramString, paramClass);
      return localType;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      TypeVariable[] arrayOfTypeVariable = paramClass.getTypeParameters();
      for (int i = 0; i < arrayOfTypeVariable.length; i++) {
        if (arrayOfTypeVariable[i].getName().equals(paramString)) {
          return arrayOfTypeVariable[i];
        }
      }
      throw new ClassNotFoundException(paramString);
    }
  }
}
