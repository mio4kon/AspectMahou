package org.aspectj.lang;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Aspects14
{
  private static final String ASPECTOF = "aspectOf";
  private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
  private static final Object[] EMPTY_OBJECT_ARRAY;
  private static final String HASASPECT = "hasAspect";
  private static final Class[] PEROBJECT_CLASS_ARRAY;
  private static final Class[] PERTYPEWITHIN_CLASS_ARRAY;
  static Class class$java$lang$Class;
  static Class class$java$lang$Object;
  
  static
  {
    Class[] arrayOfClass1 = new Class[1];
    Class localClass1;
    Class[] arrayOfClass2;
    Class localClass2;
    if (class$java$lang$Object == null)
    {
      localClass1 = class$("java.lang.Object");
      class$java$lang$Object = localClass1;
      arrayOfClass1[0] = localClass1;
      PEROBJECT_CLASS_ARRAY = arrayOfClass1;
      arrayOfClass2 = new Class[1];
      if (class$java$lang$Class != null) {
        break label80;
      }
      localClass2 = class$("java.lang.Class");
      class$java$lang$Class = localClass2;
    }
    for (;;)
    {
      arrayOfClass2[0] = localClass2;
      PERTYPEWITHIN_CLASS_ARRAY = arrayOfClass2;
      EMPTY_OBJECT_ARRAY = new Object[0];
      return;
      localClass1 = class$java$lang$Object;
      break;
      label80:
      localClass2 = class$java$lang$Class;
    }
  }
  
  public Aspects14() {}
  
  public static Object aspectOf(Class paramClass)
    throws NoAspectBoundException
  {
    try
    {
      Object localObject = getSingletonOrThreadAspectOf(paramClass).invoke(null, EMPTY_OBJECT_ARRAY);
      return localObject;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new NoAspectBoundException(paramClass.getName(), localInvocationTargetException);
    }
    catch (Exception localException)
    {
      throw new NoAspectBoundException(paramClass.getName(), localException);
    }
  }
  
  public static Object aspectOf(Class paramClass1, Class paramClass2)
    throws NoAspectBoundException
  {
    try
    {
      Object localObject = getPerTypeWithinAspectOf(paramClass1).invoke(null, new Object[] { paramClass2 });
      return localObject;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new NoAspectBoundException(paramClass1.getName(), localInvocationTargetException);
    }
    catch (Exception localException)
    {
      throw new NoAspectBoundException(paramClass1.getName(), localException);
    }
  }
  
  public static Object aspectOf(Class paramClass, Object paramObject)
    throws NoAspectBoundException
  {
    try
    {
      Object localObject = getPerObjectAspectOf(paramClass).invoke(null, new Object[] { paramObject });
      return localObject;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new NoAspectBoundException(paramClass.getName(), localInvocationTargetException);
    }
    catch (Exception localException)
    {
      throw new NoAspectBoundException(paramClass.getName(), localException);
    }
  }
  
  private static Method checkAspectOf(Method paramMethod, Class paramClass)
    throws NoSuchMethodException
  {
    paramMethod.setAccessible(true);
    if ((!paramMethod.isAccessible()) || (!Modifier.isPublic(paramMethod.getModifiers())) || (!Modifier.isStatic(paramMethod.getModifiers()))) {
      throw new NoSuchMethodException(paramClass.getName() + ".aspectOf(..) is not accessible public static");
    }
    return paramMethod;
  }
  
  private static Method checkHasAspect(Method paramMethod, Class paramClass)
    throws NoSuchMethodException
  {
    paramMethod.setAccessible(true);
    if ((!paramMethod.isAccessible()) || (!Modifier.isPublic(paramMethod.getModifiers())) || (!Modifier.isStatic(paramMethod.getModifiers()))) {
      throw new NoSuchMethodException(paramClass.getName() + ".hasAspect(..) is not accessible public static");
    }
    return paramMethod;
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
  
  private static Method getPerObjectAspectOf(Class paramClass)
    throws NoSuchMethodException
  {
    return checkAspectOf(paramClass.getDeclaredMethod("aspectOf", PEROBJECT_CLASS_ARRAY), paramClass);
  }
  
  private static Method getPerObjectHasAspect(Class paramClass)
    throws NoSuchMethodException
  {
    return checkHasAspect(paramClass.getDeclaredMethod("hasAspect", PEROBJECT_CLASS_ARRAY), paramClass);
  }
  
  private static Method getPerTypeWithinAspectOf(Class paramClass)
    throws NoSuchMethodException
  {
    return checkAspectOf(paramClass.getDeclaredMethod("aspectOf", PERTYPEWITHIN_CLASS_ARRAY), paramClass);
  }
  
  private static Method getPerTypeWithinHasAspect(Class paramClass)
    throws NoSuchMethodException
  {
    return checkHasAspect(paramClass.getDeclaredMethod("hasAspect", PERTYPEWITHIN_CLASS_ARRAY), paramClass);
  }
  
  private static Method getSingletonOrThreadAspectOf(Class paramClass)
    throws NoSuchMethodException
  {
    return checkAspectOf(paramClass.getDeclaredMethod("aspectOf", EMPTY_CLASS_ARRAY), paramClass);
  }
  
  private static Method getSingletonOrThreadHasAspect(Class paramClass)
    throws NoSuchMethodException
  {
    return checkHasAspect(paramClass.getDeclaredMethod("hasAspect", EMPTY_CLASS_ARRAY), paramClass);
  }
  
  public static boolean hasAspect(Class paramClass)
    throws NoAspectBoundException
  {
    try
    {
      boolean bool = ((Boolean)getSingletonOrThreadHasAspect(paramClass).invoke(null, EMPTY_OBJECT_ARRAY)).booleanValue();
      return bool;
    }
    catch (Exception localException) {}
    return false;
  }
  
  public static boolean hasAspect(Class paramClass1, Class paramClass2)
    throws NoAspectBoundException
  {
    try
    {
      boolean bool = ((Boolean)getPerTypeWithinHasAspect(paramClass1).invoke(null, new Object[] { paramClass2 })).booleanValue();
      return bool;
    }
    catch (Exception localException) {}
    return false;
  }
  
  public static boolean hasAspect(Class paramClass, Object paramObject)
    throws NoAspectBoundException
  {
    try
    {
      boolean bool = ((Boolean)getPerObjectHasAspect(paramClass).invoke(null, new Object[] { paramObject })).booleanValue();
      return bool;
    }
    catch (Exception localException) {}
    return false;
  }
}
