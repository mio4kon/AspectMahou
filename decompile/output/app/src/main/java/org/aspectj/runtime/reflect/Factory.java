package org.aspectj.runtime.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.EnclosingStaticPart;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.AdviceSignature;
import org.aspectj.lang.reflect.CatchClauseSignature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.InitializerSignature;
import org.aspectj.lang.reflect.LockSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.lang.reflect.UnlockSignature;

public final class Factory
{
  private static Object[] NO_ARGS = new Object[0];
  static Class class$java$lang$ClassNotFoundException;
  static Hashtable prims = new Hashtable();
  int count;
  String filename;
  Class lexicalClass;
  ClassLoader lookupClassLoader;
  
  static
  {
    prims.put("void", Void.TYPE);
    prims.put("boolean", Boolean.TYPE);
    prims.put("byte", Byte.TYPE);
    prims.put("char", Character.TYPE);
    prims.put("short", Short.TYPE);
    prims.put("int", Integer.TYPE);
    prims.put("long", Long.TYPE);
    prims.put("float", Float.TYPE);
    prims.put("double", Double.TYPE);
  }
  
  public Factory(String paramString, Class paramClass)
  {
    filename = paramString;
    lexicalClass = paramClass;
    count = 0;
    lookupClassLoader = paramClass.getClassLoader();
  }
  
  static Class class$(String paramString)
  {
    try
    {
      Class localClass = Class.forName(paramString);
      return localClass;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new NoClassDefFoundError(localClassNotFoundException.getMessage());
    }
  }
  
  static Class makeClass(String paramString, ClassLoader paramClassLoader)
  {
    Class localClass1;
    if (paramString.equals("*")) {
      localClass1 = null;
    }
    do
    {
      return localClass1;
      localClass1 = (Class)prims.get(paramString);
    } while (localClass1 != null);
    if (paramClassLoader == null) {}
    try
    {
      return Class.forName(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      Class localClass3;
      if (class$java$lang$ClassNotFoundException != null) {
        break label70;
      }
    }
    localClass3 = Class.forName(paramString, false, paramClassLoader);
    return localClass3;
    Class localClass2 = class$("java.lang.ClassNotFoundException");
    class$java$lang$ClassNotFoundException = localClass2;
    for (;;)
    {
      return localClass2;
      label70:
      localClass2 = class$java$lang$ClassNotFoundException;
    }
  }
  
  public static JoinPoint.StaticPart makeEncSJP(Member paramMember)
  {
    Object localObject;
    if ((paramMember instanceof Method))
    {
      Method localMethod = (Method)paramMember;
      localObject = new MethodSignatureImpl(localMethod.getModifiers(), localMethod.getName(), localMethod.getDeclaringClass(), localMethod.getParameterTypes(), new String[localMethod.getParameterTypes().length], localMethod.getExceptionTypes(), localMethod.getReturnType());
    }
    for (String str = "method-execution";; str = "constructor-execution")
    {
      return new JoinPointImpl.EnclosingStaticPartImpl(-1, str, (Signature)localObject, null);
      if (!(paramMember instanceof Constructor)) {
        break;
      }
      Constructor localConstructor = (Constructor)paramMember;
      localObject = new ConstructorSignatureImpl(localConstructor.getModifiers(), localConstructor.getDeclaringClass(), localConstructor.getParameterTypes(), new String[localConstructor.getParameterTypes().length], localConstructor.getExceptionTypes());
    }
    throw new IllegalArgumentException("member must be either a method or constructor");
  }
  
  public static JoinPoint makeJP(JoinPoint.StaticPart paramStaticPart, Object paramObject1, Object paramObject2)
  {
    return new JoinPointImpl(paramStaticPart, paramObject1, paramObject2, NO_ARGS);
  }
  
  public static JoinPoint makeJP(JoinPoint.StaticPart paramStaticPart, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    return new JoinPointImpl(paramStaticPart, paramObject1, paramObject2, new Object[] { paramObject3 });
  }
  
  public static JoinPoint makeJP(JoinPoint.StaticPart paramStaticPart, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
  {
    return new JoinPointImpl(paramStaticPart, paramObject1, paramObject2, new Object[] { paramObject3, paramObject4 });
  }
  
  public static JoinPoint makeJP(JoinPoint.StaticPart paramStaticPart, Object paramObject1, Object paramObject2, Object[] paramArrayOfObject)
  {
    return new JoinPointImpl(paramStaticPart, paramObject1, paramObject2, paramArrayOfObject);
  }
  
  public AdviceSignature makeAdviceSig(int paramInt, String paramString, Class paramClass1, Class[] paramArrayOfClass1, String[] paramArrayOfString, Class[] paramArrayOfClass2, Class paramClass2)
  {
    AdviceSignatureImpl localAdviceSignatureImpl = new AdviceSignatureImpl(paramInt, paramString, paramClass1, paramArrayOfClass1, paramArrayOfString, paramArrayOfClass2, paramClass2);
    localAdviceSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localAdviceSignatureImpl;
  }
  
  public AdviceSignature makeAdviceSig(String paramString)
  {
    AdviceSignatureImpl localAdviceSignatureImpl = new AdviceSignatureImpl(paramString);
    localAdviceSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localAdviceSignatureImpl;
  }
  
  public AdviceSignature makeAdviceSig(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
  {
    int i = Integer.parseInt(paramString1, 16);
    Class localClass = makeClass(paramString3, lookupClassLoader);
    StringTokenizer localStringTokenizer1 = new StringTokenizer(paramString4, ":");
    int j = localStringTokenizer1.countTokens();
    Class[] arrayOfClass1 = new Class[j];
    for (int k = 0; k < j; k++) {
      arrayOfClass1[k] = makeClass(localStringTokenizer1.nextToken(), lookupClassLoader);
    }
    StringTokenizer localStringTokenizer2 = new StringTokenizer(paramString5, ":");
    int m = localStringTokenizer2.countTokens();
    String[] arrayOfString = new String[m];
    for (int n = 0; n < m; n++) {
      arrayOfString[n] = localStringTokenizer2.nextToken();
    }
    StringTokenizer localStringTokenizer3 = new StringTokenizer(paramString6, ":");
    int i1 = localStringTokenizer3.countTokens();
    Class[] arrayOfClass2 = new Class[i1];
    for (int i2 = 0; i2 < i1; i2++) {
      arrayOfClass2[i2] = makeClass(localStringTokenizer3.nextToken(), lookupClassLoader);
    }
    AdviceSignatureImpl localAdviceSignatureImpl = new AdviceSignatureImpl(i, paramString2, localClass, arrayOfClass1, arrayOfString, arrayOfClass2, makeClass(paramString7, lookupClassLoader));
    localAdviceSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localAdviceSignatureImpl;
  }
  
  public CatchClauseSignature makeCatchClauseSig(Class paramClass1, Class paramClass2, String paramString)
  {
    CatchClauseSignatureImpl localCatchClauseSignatureImpl = new CatchClauseSignatureImpl(paramClass1, paramClass2, paramString);
    localCatchClauseSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localCatchClauseSignatureImpl;
  }
  
  public CatchClauseSignature makeCatchClauseSig(String paramString)
  {
    CatchClauseSignatureImpl localCatchClauseSignatureImpl = new CatchClauseSignatureImpl(paramString);
    localCatchClauseSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localCatchClauseSignatureImpl;
  }
  
  public CatchClauseSignature makeCatchClauseSig(String paramString1, String paramString2, String paramString3)
  {
    CatchClauseSignatureImpl localCatchClauseSignatureImpl = new CatchClauseSignatureImpl(makeClass(paramString1, lookupClassLoader), makeClass(new StringTokenizer(paramString2, ":").nextToken(), lookupClassLoader), new StringTokenizer(paramString3, ":").nextToken());
    localCatchClauseSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localCatchClauseSignatureImpl;
  }
  
  public ConstructorSignature makeConstructorSig(int paramInt, Class paramClass, Class[] paramArrayOfClass1, String[] paramArrayOfString, Class[] paramArrayOfClass2)
  {
    ConstructorSignatureImpl localConstructorSignatureImpl = new ConstructorSignatureImpl(paramInt, paramClass, paramArrayOfClass1, paramArrayOfString, paramArrayOfClass2);
    localConstructorSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localConstructorSignatureImpl;
  }
  
  public ConstructorSignature makeConstructorSig(String paramString)
  {
    ConstructorSignatureImpl localConstructorSignatureImpl = new ConstructorSignatureImpl(paramString);
    localConstructorSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localConstructorSignatureImpl;
  }
  
  public ConstructorSignature makeConstructorSig(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    int i = Integer.parseInt(paramString1, 16);
    Class localClass = makeClass(paramString2, lookupClassLoader);
    StringTokenizer localStringTokenizer1 = new StringTokenizer(paramString3, ":");
    int j = localStringTokenizer1.countTokens();
    Class[] arrayOfClass1 = new Class[j];
    for (int k = 0; k < j; k++) {
      arrayOfClass1[k] = makeClass(localStringTokenizer1.nextToken(), lookupClassLoader);
    }
    StringTokenizer localStringTokenizer2 = new StringTokenizer(paramString4, ":");
    int m = localStringTokenizer2.countTokens();
    String[] arrayOfString = new String[m];
    for (int n = 0; n < m; n++) {
      arrayOfString[n] = localStringTokenizer2.nextToken();
    }
    StringTokenizer localStringTokenizer3 = new StringTokenizer(paramString5, ":");
    int i1 = localStringTokenizer3.countTokens();
    Class[] arrayOfClass2 = new Class[i1];
    for (int i2 = 0; i2 < i1; i2++) {
      arrayOfClass2[i2] = makeClass(localStringTokenizer3.nextToken(), lookupClassLoader);
    }
    ConstructorSignatureImpl localConstructorSignatureImpl = new ConstructorSignatureImpl(i, localClass, arrayOfClass1, arrayOfString, arrayOfClass2);
    localConstructorSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localConstructorSignatureImpl;
  }
  
  public JoinPoint.EnclosingStaticPart makeESJP(String paramString, Signature paramSignature, int paramInt)
  {
    int i = count;
    count = (i + 1);
    return new JoinPointImpl.EnclosingStaticPartImpl(i, paramString, paramSignature, makeSourceLoc(paramInt, -1));
  }
  
  public JoinPoint.EnclosingStaticPart makeESJP(String paramString, Signature paramSignature, int paramInt1, int paramInt2)
  {
    int i = count;
    count = (i + 1);
    return new JoinPointImpl.EnclosingStaticPartImpl(i, paramString, paramSignature, makeSourceLoc(paramInt1, paramInt2));
  }
  
  public JoinPoint.EnclosingStaticPart makeESJP(String paramString, Signature paramSignature, SourceLocation paramSourceLocation)
  {
    int i = count;
    count = (i + 1);
    return new JoinPointImpl.EnclosingStaticPartImpl(i, paramString, paramSignature, paramSourceLocation);
  }
  
  public FieldSignature makeFieldSig(int paramInt, String paramString, Class paramClass1, Class paramClass2)
  {
    FieldSignatureImpl localFieldSignatureImpl = new FieldSignatureImpl(paramInt, paramString, paramClass1, paramClass2);
    localFieldSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localFieldSignatureImpl;
  }
  
  public FieldSignature makeFieldSig(String paramString)
  {
    FieldSignatureImpl localFieldSignatureImpl = new FieldSignatureImpl(paramString);
    localFieldSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localFieldSignatureImpl;
  }
  
  public FieldSignature makeFieldSig(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    FieldSignatureImpl localFieldSignatureImpl = new FieldSignatureImpl(Integer.parseInt(paramString1, 16), paramString2, makeClass(paramString3, lookupClassLoader), makeClass(paramString4, lookupClassLoader));
    localFieldSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localFieldSignatureImpl;
  }
  
  public InitializerSignature makeInitializerSig(int paramInt, Class paramClass)
  {
    InitializerSignatureImpl localInitializerSignatureImpl = new InitializerSignatureImpl(paramInt, paramClass);
    localInitializerSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localInitializerSignatureImpl;
  }
  
  public InitializerSignature makeInitializerSig(String paramString)
  {
    InitializerSignatureImpl localInitializerSignatureImpl = new InitializerSignatureImpl(paramString);
    localInitializerSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localInitializerSignatureImpl;
  }
  
  public InitializerSignature makeInitializerSig(String paramString1, String paramString2)
  {
    InitializerSignatureImpl localInitializerSignatureImpl = new InitializerSignatureImpl(Integer.parseInt(paramString1, 16), makeClass(paramString2, lookupClassLoader));
    localInitializerSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localInitializerSignatureImpl;
  }
  
  public LockSignature makeLockSig()
  {
    LockSignatureImpl localLockSignatureImpl = new LockSignatureImpl(makeClass("Ljava/lang/Object;", lookupClassLoader));
    localLockSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localLockSignatureImpl;
  }
  
  public LockSignature makeLockSig(Class paramClass)
  {
    LockSignatureImpl localLockSignatureImpl = new LockSignatureImpl(paramClass);
    localLockSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localLockSignatureImpl;
  }
  
  public LockSignature makeLockSig(String paramString)
  {
    LockSignatureImpl localLockSignatureImpl = new LockSignatureImpl(paramString);
    localLockSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localLockSignatureImpl;
  }
  
  public MethodSignature makeMethodSig(int paramInt, String paramString, Class paramClass1, Class[] paramArrayOfClass1, String[] paramArrayOfString, Class[] paramArrayOfClass2, Class paramClass2)
  {
    MethodSignatureImpl localMethodSignatureImpl = new MethodSignatureImpl(paramInt, paramString, paramClass1, paramArrayOfClass1, paramArrayOfString, paramArrayOfClass2, paramClass2);
    localMethodSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localMethodSignatureImpl;
  }
  
  public MethodSignature makeMethodSig(String paramString)
  {
    MethodSignatureImpl localMethodSignatureImpl = new MethodSignatureImpl(paramString);
    localMethodSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localMethodSignatureImpl;
  }
  
  public MethodSignature makeMethodSig(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
  {
    int i = Integer.parseInt(paramString1, 16);
    Class localClass = makeClass(paramString3, lookupClassLoader);
    StringTokenizer localStringTokenizer1 = new StringTokenizer(paramString4, ":");
    int j = localStringTokenizer1.countTokens();
    Class[] arrayOfClass1 = new Class[j];
    for (int k = 0; k < j; k++) {
      arrayOfClass1[k] = makeClass(localStringTokenizer1.nextToken(), lookupClassLoader);
    }
    StringTokenizer localStringTokenizer2 = new StringTokenizer(paramString5, ":");
    int m = localStringTokenizer2.countTokens();
    String[] arrayOfString = new String[m];
    for (int n = 0; n < m; n++) {
      arrayOfString[n] = localStringTokenizer2.nextToken();
    }
    StringTokenizer localStringTokenizer3 = new StringTokenizer(paramString6, ":");
    int i1 = localStringTokenizer3.countTokens();
    Class[] arrayOfClass2 = new Class[i1];
    for (int i2 = 0; i2 < i1; i2++) {
      arrayOfClass2[i2] = makeClass(localStringTokenizer3.nextToken(), lookupClassLoader);
    }
    return new MethodSignatureImpl(i, paramString2, localClass, arrayOfClass1, arrayOfString, arrayOfClass2, makeClass(paramString7, lookupClassLoader));
  }
  
  public JoinPoint.StaticPart makeSJP(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, int paramInt)
  {
    MethodSignature localMethodSignature = makeMethodSig(paramString2, paramString3, paramString4, paramString5, paramString6, "", paramString7);
    int i = count;
    count = (i + 1);
    return new JoinPointImpl.StaticPartImpl(i, paramString1, localMethodSignature, makeSourceLoc(paramInt, -1));
  }
  
  public JoinPoint.StaticPart makeSJP(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, int paramInt)
  {
    MethodSignature localMethodSignature = makeMethodSig(paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, paramString8);
    int i = count;
    count = (i + 1);
    return new JoinPointImpl.StaticPartImpl(i, paramString1, localMethodSignature, makeSourceLoc(paramInt, -1));
  }
  
  public JoinPoint.StaticPart makeSJP(String paramString, Signature paramSignature, int paramInt)
  {
    int i = count;
    count = (i + 1);
    return new JoinPointImpl.StaticPartImpl(i, paramString, paramSignature, makeSourceLoc(paramInt, -1));
  }
  
  public JoinPoint.StaticPart makeSJP(String paramString, Signature paramSignature, int paramInt1, int paramInt2)
  {
    int i = count;
    count = (i + 1);
    return new JoinPointImpl.StaticPartImpl(i, paramString, paramSignature, makeSourceLoc(paramInt1, paramInt2));
  }
  
  public JoinPoint.StaticPart makeSJP(String paramString, Signature paramSignature, SourceLocation paramSourceLocation)
  {
    int i = count;
    count = (i + 1);
    return new JoinPointImpl.StaticPartImpl(i, paramString, paramSignature, paramSourceLocation);
  }
  
  public SourceLocation makeSourceLoc(int paramInt1, int paramInt2)
  {
    return new SourceLocationImpl(lexicalClass, filename, paramInt1);
  }
  
  public UnlockSignature makeUnlockSig()
  {
    UnlockSignatureImpl localUnlockSignatureImpl = new UnlockSignatureImpl(makeClass("Ljava/lang/Object;", lookupClassLoader));
    localUnlockSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localUnlockSignatureImpl;
  }
  
  public UnlockSignature makeUnlockSig(Class paramClass)
  {
    UnlockSignatureImpl localUnlockSignatureImpl = new UnlockSignatureImpl(paramClass);
    localUnlockSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localUnlockSignatureImpl;
  }
  
  public UnlockSignature makeUnlockSig(String paramString)
  {
    UnlockSignatureImpl localUnlockSignatureImpl = new UnlockSignatureImpl(paramString);
    localUnlockSignatureImpl.setLookupClassLoader(lookupClassLoader);
    return localUnlockSignatureImpl;
  }
}
