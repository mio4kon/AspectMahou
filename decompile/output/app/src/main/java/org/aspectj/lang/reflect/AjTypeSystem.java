package org.aspectj.lang.reflect;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import org.aspectj.internal.lang.reflect.AjTypeImpl;

public class AjTypeSystem
{
  private static Map<Class, WeakReference<AjType>> ajTypes = Collections.synchronizedMap(new WeakHashMap());
  
  public AjTypeSystem() {}
  
  public static <T> AjType<T> getAjType(Class<T> paramClass)
  {
    WeakReference localWeakReference = (WeakReference)ajTypes.get(paramClass);
    if (localWeakReference != null)
    {
      AjType localAjType = (AjType)localWeakReference.get();
      if (localAjType != null) {
        return localAjType;
      }
      AjTypeImpl localAjTypeImpl2 = new AjTypeImpl(paramClass);
      ajTypes.put(paramClass, new WeakReference(localAjTypeImpl2));
      return localAjTypeImpl2;
    }
    AjTypeImpl localAjTypeImpl1 = new AjTypeImpl(paramClass);
    ajTypes.put(paramClass, new WeakReference(localAjTypeImpl1));
    return localAjTypeImpl1;
  }
}
