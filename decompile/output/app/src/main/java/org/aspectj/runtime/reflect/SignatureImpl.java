package org.aspectj.runtime.reflect;

import java.lang.ref.SoftReference;
import java.util.StringTokenizer;
import org.aspectj.lang.Signature;

abstract class SignatureImpl
  implements Signature
{
  static Class[] EMPTY_CLASS_ARRAY = new Class[0];
  static String[] EMPTY_STRING_ARRAY;
  static final String INNER_SEP = ":";
  static final char SEP = '-';
  private static boolean useCache = true;
  Class declaringType;
  String declaringTypeName;
  ClassLoader lookupClassLoader = null;
  int modifiers = -1;
  String name;
  Cache stringCache;
  private String stringRep;
  
  static
  {
    EMPTY_STRING_ARRAY = new String[0];
  }
  
  SignatureImpl(int paramInt, String paramString, Class paramClass)
  {
    modifiers = paramInt;
    name = paramString;
    declaringType = paramClass;
  }
  
  public SignatureImpl(String paramString)
  {
    stringRep = paramString;
  }
  
  private ClassLoader getLookupClassLoader()
  {
    if (lookupClassLoader == null) {
      lookupClassLoader = getClass().getClassLoader();
    }
    return lookupClassLoader;
  }
  
  static boolean getUseCache()
  {
    return useCache;
  }
  
  static void setUseCache(boolean paramBoolean)
  {
    useCache = paramBoolean;
  }
  
  void addFullTypeNames(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    for (int i = 0; i < paramArrayOfClass.length; i++)
    {
      if (i > 0) {
        paramStringBuffer.append(", ");
      }
      paramStringBuffer.append(fullTypeName(paramArrayOfClass[i]));
    }
  }
  
  void addShortTypeNames(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    for (int i = 0; i < paramArrayOfClass.length; i++)
    {
      if (i > 0) {
        paramStringBuffer.append(", ");
      }
      paramStringBuffer.append(shortTypeName(paramArrayOfClass[i]));
    }
  }
  
  void addTypeArray(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    addFullTypeNames(paramStringBuffer, paramArrayOfClass);
  }
  
  protected abstract String createToString(StringMaker paramStringMaker);
  
  int extractInt(int paramInt)
  {
    return Integer.parseInt(extractString(paramInt), 16);
  }
  
  String extractString(int paramInt)
  {
    int i = 0;
    int j = stringRep.indexOf('-');
    int m;
    for (int k = paramInt;; k = m)
    {
      m = k - 1;
      if (k <= 0) {
        break;
      }
      i = j + 1;
      j = stringRep.indexOf('-', i);
    }
    if (j == -1) {
      j = stringRep.length();
    }
    return stringRep.substring(i, j);
  }
  
  String[] extractStrings(int paramInt)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(extractString(paramInt), ":");
    int i = localStringTokenizer.countTokens();
    String[] arrayOfString = new String[i];
    for (int j = 0; j < i; j++) {
      arrayOfString[j] = localStringTokenizer.nextToken();
    }
    return arrayOfString;
  }
  
  Class extractType(int paramInt)
  {
    return Factory.makeClass(extractString(paramInt), getLookupClassLoader());
  }
  
  Class[] extractTypes(int paramInt)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(extractString(paramInt), ":");
    int i = localStringTokenizer.countTokens();
    Class[] arrayOfClass = new Class[i];
    for (int j = 0; j < i; j++) {
      arrayOfClass[j] = Factory.makeClass(localStringTokenizer.nextToken(), getLookupClassLoader());
    }
    return arrayOfClass;
  }
  
  String fullTypeName(Class paramClass)
  {
    if (paramClass == null) {
      return "ANONYMOUS";
    }
    if (paramClass.isArray()) {
      return fullTypeName(paramClass.getComponentType()) + "[]";
    }
    return paramClass.getName().replace('$', '.');
  }
  
  public Class getDeclaringType()
  {
    if (declaringType == null) {
      declaringType = extractType(2);
    }
    return declaringType;
  }
  
  public String getDeclaringTypeName()
  {
    if (declaringTypeName == null) {
      declaringTypeName = getDeclaringType().getName();
    }
    return declaringTypeName;
  }
  
  public int getModifiers()
  {
    if (modifiers == -1) {
      modifiers = extractInt(0);
    }
    return modifiers;
  }
  
  public String getName()
  {
    if (name == null) {
      name = extractString(1);
    }
    return name;
  }
  
  public void setLookupClassLoader(ClassLoader paramClassLoader)
  {
    lookupClassLoader = paramClassLoader;
  }
  
  String shortTypeName(Class paramClass)
  {
    if (paramClass == null) {
      return "ANONYMOUS";
    }
    if (paramClass.isArray()) {
      return shortTypeName(paramClass.getComponentType()) + "[]";
    }
    return stripPackageName(paramClass.getName()).replace('$', '.');
  }
  
  String stripPackageName(String paramString)
  {
    int i = paramString.lastIndexOf('.');
    if (i == -1) {
      return paramString;
    }
    return paramString.substring(i + 1);
  }
  
  public final String toLongString()
  {
    return toString(StringMaker.longStringMaker);
  }
  
  public final String toShortString()
  {
    return toString(StringMaker.shortStringMaker);
  }
  
  public final String toString()
  {
    return toString(StringMaker.middleStringMaker);
  }
  
  String toString(StringMaker paramStringMaker)
  {
    boolean bool = useCache;
    String str = null;
    if ((!bool) || (stringCache == null)) {}
    for (;;)
    {
      try
      {
        stringCache = new CacheImpl();
        if (str == null) {
          str = createToString(paramStringMaker);
        }
        if (useCache) {
          stringCache.set(cacheOffset, str);
        }
        return str;
      }
      catch (Throwable localThrowable)
      {
        useCache = false;
        str = null;
        continue;
      }
      str = stringCache.get(cacheOffset);
    }
  }
  
  private static abstract interface Cache
  {
    public abstract String get(int paramInt);
    
    public abstract void set(int paramInt, String paramString);
  }
  
  private static final class CacheImpl
    implements SignatureImpl.Cache
  {
    private SoftReference toStringCacheRef;
    
    public CacheImpl()
    {
      makeCache();
    }
    
    private String[] array()
    {
      return (String[])toStringCacheRef.get();
    }
    
    private String[] makeCache()
    {
      String[] arrayOfString = new String[3];
      toStringCacheRef = new SoftReference(arrayOfString);
      return arrayOfString;
    }
    
    public String get(int paramInt)
    {
      String[] arrayOfString = array();
      if (arrayOfString == null) {
        return null;
      }
      return arrayOfString[paramInt];
    }
    
    public void set(int paramInt, String paramString)
    {
      String[] arrayOfString = array();
      if (arrayOfString == null) {
        arrayOfString = makeCache();
      }
      arrayOfString[paramInt] = paramString;
    }
  }
}
