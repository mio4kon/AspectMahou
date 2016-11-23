package com.mio.aspect.aspect;

import android.util.Log;
import com.mio.aspect.internal.TimeWatcher;
import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class TimeAspect
{
  public static final String POINTCUT_CONSTRUCTOR = "execution(@com.mio.aspect.annotation.TimeTrace *.new(..))";
  public static final String POINTCUT_METHOD = "execution(@com.mio.aspect.annotation.TimeTrace * *(..))";
  private static Throwable ajc$initFailureCause;
  public static final TimeAspect ajc$perSingletonInstance;
  
  static
  {
    try
    {
      ajc$postClinit();
      return;
    }
    catch (Throwable localThrowable)
    {
      ajc$initFailureCause = localThrowable;
    }
  }
  
  public TimeAspect() {}
  
  private static void ajc$postClinit()
  {
    ajc$perSingletonInstance = new TimeAspect();
  }
  
  public static TimeAspect aspectOf()
  {
    if (ajc$perSingletonInstance == null) {
      throw new NoAspectBoundException("com.mio.aspect.aspect.TimeAspect", ajc$initFailureCause);
    }
    return ajc$perSingletonInstance;
  }
  
  public static boolean hasAspect()
  {
    return ajc$perSingletonInstance != null;
  }
  
  @Pointcut("execution(@com.mio.aspect.annotation.TimeTrace *.new(..))")
  public void constructorTimeTrace() {}
  
  @Pointcut("execution(@com.mio.aspect.annotation.TimeTrace * *(..))")
  public void methodTimeTrace() {}
  
  @Around("methodTimeTrace() || constructorTimeTrace()")
  public Object weaveJoinPoint(ProceedingJoinPoint paramProceedingJoinPoint)
    throws Throwable
  {
    Signature localSignature = paramProceedingJoinPoint.getSignature();
    String str1 = localSignature.getDeclaringType().getSimpleName();
    String str2 = localSignature.getName();
    TimeWatcher localTimeWatcher = new TimeWatcher();
    localTimeWatcher.start();
    Object localObject = paramProceedingJoinPoint.proceed();
    localTimeWatcher.stop();
    Log.d("mio4kon", "类:" + str1 + ", 方法:" + str2 + ", 耗费时间:" + localTimeWatcher.getTotalTimeMillis() + "ms");
    return localObject;
  }
}
